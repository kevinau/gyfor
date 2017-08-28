package org.gyfor.docstore.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.gyfor.doc.Document;
import org.gyfor.doc.DocumentStoreListener;
import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.IDocumentStore;
import org.gyfor.doc.ISegment;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = ISearchEngine.class, immediate = true)
public class LuceneSearchEngine implements DocumentStoreListener, ISearchEngine {

  private final Logger logger = LoggerFactory.getLogger(LuceneSearchEngine.class);
  
  
  @Configurable
  private Path baseDir = Paths.get(System.getProperty("user.home"), "/docstore");

  private static final String LUCENE = "lucene";

  private Path luceneDir;
  
  private Analyzer analyzer;

  private Directory directory;
  
  @Reference
  private IDocumentStore docStore;

  
  @Activate 
  public void activate(ComponentContext context) {
    ComponentConfiguration.load(this, context);
    
    try {
      luceneDir = baseDir.resolve(LUCENE);
      Files.createDirectories(luceneDir);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    try {
      directory = FSDirectory.open(luceneDir);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    docStore.addDocumentStoreListener(this);

    analyzer = new StandardAnalyzer();
  }
  
  
  @Deactivate
  public void deactivate() {
    docStore.removeDocumentStoreListener(this);
    try {
      directory.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    analyzer = null;
  }


  @Override
  public void documentAdded(Document doc) {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    try (IndexWriter iwriter = new IndexWriter(directory, config)) {
      org.apache.lucene.document.Document indexedDoc = new org.apache.lucene.document.Document();
      
      IDocumentContents docContents = doc.getContents();
      // Add document id
      indexedDoc.add(new Field("id", doc.getHashCode(), StringField.TYPE_STORED));
      // ... and the date of the document
      long dateLong = doc.getOriginTime().getTime();
      indexedDoc.add(new LongPoint("date", dateLong));
      indexedDoc.add(new StoredField("date", dateLong)); 
      // ... and finally all the text of the document.
      List<? extends ISegment> segments = docContents.getSegments();
      StringBuilder body = new StringBuilder();
      for (ISegment segment : segments) {
        body.append(segment.getText());
        body.append(' ');
      }
      logger.info("Indexing {} ({})", doc.getHashCode(), doc.getOriginName());
      System.out.println("indexing " + body.toString());
      indexedDoc.add(new Field("body", body.toString(), TextField.TYPE_NOT_STORED));

      iwriter.addDocument(indexedDoc);
      iwriter.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public List<DocumentReference> searchIndex(String queryString) {
    /*
     * If your index has changed and you wish to see the changes reflected in
     * searching, you should use DirectoryReader.openIfChanged(DirectoryReader)
     * to obtain a new reader and then create a new IndexSearcher from that.
     * Also, for low-latency turnaround it's best to use a near-real-time reader
     * (DirectoryReader.open(IndexWriter)). Once you have a new IndexReader,
     * it's relatively cheap to create a new IndexSearcher from it.
     * 
     */
    List<DocumentReference> results = new ArrayList<>();
    
    try (DirectoryReader ireader = DirectoryReader.open(directory)) {
      IndexSearcher isearcher = new IndexSearcher(ireader);
      // Parse a simple query that searches for "text":
      QueryParser parser = new QueryParser("body", analyzer);
      Query query = parser.parse(queryString);
      ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
      // Iterate through the results:
      for (int i = 0; i < hits.length; i++) {
        org.apache.lucene.document.Document hitDoc = isearcher.doc(hits[i].doc);
        String documentId = hitDoc.get("id");
        String daten = hitDoc.get("date");
        LocalDate date = LocalDate.ofEpochDay(Long.parseLong(daten) / (24 * 60 * 60 * 1000));
        DocumentReference result = new DocumentReference(date, documentId);
        results.add(result);
      }
      ireader.close();
    } catch (IOException | ParseException ex) {
      throw new RuntimeException(ex);
    }
    return results;
  }

  
  @Override
  public void documentRemoved(Document doc) {
    // TODO Auto-generated method stub
    
  }
}
