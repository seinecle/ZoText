ZoText

Created 06 December 2011 by Clement Levallois

Contact: twitter: @seinecle

Licence: GPLv3



*** What is ZoText for? ***

Zotext is a Java program to convert a Zotero bibliography into raw text suitable for semantic analysis.



*** How to use ZoText? ***

1. from Zotero, export your Zotero bibliography 
=> from within Zotero, select the cog wheel icon.
=> select "Export"
=> select the Zotero rdf format, and including note and files

2. Launch ZoText with the "launch" button on the webpage and follow the instructions.
3. The output is a textfile created in the same repository of your Zotero bibliography. It contains all the raw text from the Zotero bibliography. Each original text is contained on single lines of this new file.


*** features ***
- process pdf and html docs
- filters stopwords with a customized list provided by the user
- detects duplicate documents by their names and / or by content analysis, and optionally removes them


*** note on the "detect duplicate documents by content analysis"
For each document, the longest line in the document is recorded and added to a list.
For each new document processed, its longest line is compared to the list of longest lines. If there is a match, the document is left out.



Questions and bug reports: contact me on Twitter @seinecle.