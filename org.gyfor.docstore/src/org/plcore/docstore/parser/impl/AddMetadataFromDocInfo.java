/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.plcore.docstore.parser.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.transform.TransformerException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.xml.XmpSerializer;


/**
 * This is an example on how to add metadata to a document.
 *
 * @author Ben Litchfield
 * 
 */
public final class AddMetadataFromDocInfo {

  private AddMetadataFromDocInfo() {
    // utility class
  }


  /**
   * This will print the documents data.
   *
   * @param args
   *          The command line arguments.
   *
   * @throws IOException
   *           If there is an error parsing the document.
   * @throws TransformerException
   */
  public static void main(String[] args) throws IOException, TransformerException {
    // if( args.length != 2 )
    // {
    // usage();
    // }
    // else
    // {
    
    args = new String[2];
    args[0] = "AFI_Payment_Advice_2017_02_24.pdf";
    args[1] = "AFI_Payment_Advice_2017_02_24_wmd.pdf";

    PDDocument document = null;

    try {
      document = PDDocument.load(new File(args[0]));
      if (document.isEncrypted()) {
        System.err.println("Error: Cannot add metadata to encrypted document.");
        System.exit(1);
      }
      PDDocumentCatalog catalog = document.getDocumentCatalog();
      PDDocumentInformation info = document.getDocumentInformation();
      System.out.println(info);
      System.out.println("Keywords " + info.getKeywords());
      System.out.println("Title " + info.getTitle());
      System.out.println("Subject " + info.getSubject());
      System.out.println("Transaction date " + info.getCustomMetadataValue("tranDate"));
      System.out.println(info.getCustomMetadataValue("tranNarrative"));
      System.out.println(info.getCustomMetadataValue("allocAccount"));
      System.out.println(info.getCustomMetadataValue("allocAmount"));

      XMPMetadata metadata = XMPMetadata.createXMPMetadata();

      AdobePDFSchema pdfSchema = metadata.createAndAddAdobePDFSchema();
      //pdfSchema.setKeywords(info.getKeywords());
      pdfSchema.setProducer(info.getProducer());

      //XMPSchema defaultSchema = metadata.createAndAddDefaultSchema(nsPrefix, nsURI);
      
      XMPBasicSchema basicSchema = metadata.createAndAddXMPBasicSchema();
      //basicSchema.setModifyDate(info.getModificationDate());
      basicSchema.setCreateDate(info.getCreationDate());
      basicSchema.setCreatorTool(info.getCreator());
      basicSchema.setMetadataDate(new GregorianCalendar());

      DublinCoreSchema dcSchema = metadata.createAndAddDublinCoreSchema();
      dcSchema.setTitle(info.getTitle());
      dcSchema.addCreator("PDFBox");
      dcSchema.setDescription(info.getSubject());

      PDMetadata metadataStream = new PDMetadata(document);
      catalog.setMetadata(metadataStream);

      XmpSerializer serializer = new XmpSerializer();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      serializer.serialize(metadata, baos, false);
      metadataStream.importXMPMetadata(baos.toByteArray());

      document.save(args[1]);
    } finally {
      if (document != null) {
        document.close();
      }
    }
    // }
  }


  /**
   * This will print the usage for this document.
   */
  public static void usage() {
    System.err.println("Usage: java " + AddMetadataFromDocInfo.class.getName() + " <input-pdf> <output-pdf>");
  }
}
