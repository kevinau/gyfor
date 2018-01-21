A place were source documents are parsed and stored.

The document store itself is represented by the `IDocumentStore` interface.

Source documents are referenced by a CRC32 hash of the document contents.  This is unique 
to the source document, and means that the source document cannot be changed without 
invalidating the CRC32 hash.

The document store includes the following:
* A copy of the source document.
* If the source document is a PDF, an image of the first page.
* If the source document is a PDF or an image, a thumbnail image of the first page.
* Textual contents of the source document.

This project could be split into a document store and a collection of 
document parsers, but that is for a future time.

Why CRC32
---------

A CRC32 hash is used as the unique identifier of the source document.  An MD5, SHA1, 
SHA2 or SHA3 hash could also have been used.

The only reason why CRC32 was used is performance.  In the context of this document 
store, there is no need for a crypographically sound hash.  The only requirement of 
the hash is to uniquely identify a document.  

or 
