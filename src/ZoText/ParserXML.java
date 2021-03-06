/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

/**
 *
 * @author C. Levallois
 */
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParserXML extends DefaultHandler {

    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
    public String itemType;
    public static int counter;
    public String extractedText;
    private HashSet<String> fileSet = new HashSet();
    private boolean newTitle;
    private boolean newAbstract;
    private boolean newAttachment = false;
    private boolean newIsPartOf;
    String currItemType;
    String currYearHere;
    String currMonthHere;
    private int countLines;
    private boolean newItemInCorpus;
    private int currLineSize;
    private int maxLineSize = 0;
    private boolean newLongestSentence;
    private boolean newSubject;
    private boolean newAuthor;
    private boolean newDocument;
    private StringBuilder subjectBuilder;
    private StringBuilder documentBuilder;
    private StringBuilder titleBuilder;
    private StringBuilder attachmentBuilder;
    private StringBuilder authorBuilder;
    private StringBuilder abstractBuilder;

    public ParserXML(InputSource toBeParsed, String type) throws IOException {


        parseDocument(toBeParsed);
        //printData();




    }

    private void parseDocument(InputSource toBeParsed) throws IOException {

//        InputStream is = toBeParsed.getByteStream();
//        String myString = IOUtils.toString(is, "UTF-8");
//        System.out.println(myString);


        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(toBeParsed, this);

        } catch (SAXException se) {
        } catch (ParserConfigurationException pce) {
        } catch (IOException ie) {
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {



        if (qName.matches("dc:title") & Mainthread.titlesIncluded && !newAttachment && !newIsPartOf) {
            newTitle = true;
            titleBuilder = new StringBuilder();

        }


        if (qName.matches("z:Attachment")) {
            newAttachment = true;
            attachmentBuilder = new StringBuilder();

        }

        if (qName.contains("bib:") & !newIsPartOf & !qName.contains("authors")) {
            documentBuilder = new StringBuilder();
            newDocument = true;

        }

        if (qName.matches("dc:subject")) {
            subjectBuilder = new StringBuilder();
            newSubject = true;
        }

        if (qName.matches("foaf:surname")) {
            authorBuilder = new StringBuilder();
            newAuthor = true;

        }


        if (qName.matches("dcterms:isPartOf") & Mainthread.subjectsIncluded) {
            newIsPartOf = true;

        }


        if (qName.matches("dcterms:abstract") & Mainthread.abstractsIncluded) {
            abstractBuilder = new StringBuilder();
            newAbstract = true;
        }



        if (qName.equalsIgnoreCase("rdf:resource") && attributes.getQName(0).equalsIgnoreCase("rdf:resource") && Mainthread.attachmentsIncluded) {

            try {

                //limits the text to 20 entries for debugging purposes
                counter++;


                String fileName = attributes.getValue(0);
                //System.out.println(attributes.getValue(0));
                String[] fileSplit = attributes.getValue(0).split("/");
                boolean fileIsUnique = fileSet.add(fileSplit[fileSplit.length - 1]);
                //System.out.println(fileSplit[fileSplit.length-1]);

                boolean isHTML = fileName.contains(".html");
                boolean isPDF = fileName.contains(".pdf");
                //if (isHTML) pool.execute(new BoilerPlateExtractor(attributes.getValue(0)));
                if (fileIsUnique | !Mainthread.uniqueFileNames) {

                    if (isHTML && Mainthread.includeHTML) {
                        extractedText = new BoilerPlateExtractor(attributes.getValue(0)).run();
//                        pool.execute(new WorkerThread(extractedText));
                        WorkerThread(extractedText);
                    }
                    if (isPDF && Mainthread.includePDF) {
                        extractedText = new PdfExtractor(attributes.getValue(0)).run();
//                        pool.execute(new WorkerThread(extractedText));
                        WorkerThread(extractedText);
                    }


                }
            } catch (BoilerpipeProcessingException ex) {
                Logger.getLogger(ParserXML.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ParserXML.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (newTitle & Mainthread.titlesIncluded) {
            titleBuilder.append(ch, start, length);

        }

        if (newAbstract & Mainthread.abstractsIncluded) {
            abstractBuilder.append(ch, start, length);
        }

        if (newSubject & Mainthread.subjectsIncluded) {
            subjectBuilder.append(ch, start, length);
        }

        if (newAuthor & Mainthread.authorsIncluded) {
            authorBuilder.append(ch, start, length);
        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {


        if (qName.equalsIgnoreCase("dcterms:abstract") & abstractBuilder != null & Mainthread.abstractsIncluded) {
            documentBuilder.append(abstractBuilder.toString()).append("|");
            //System.out.println("documentBuilder so far is: " + documentBuilder.toString());
            newAbstract = false;
        }

        if (qName.equalsIgnoreCase("dc:title") & titleBuilder != null & Mainthread.titlesIncluded) {
            documentBuilder.append(titleBuilder.toString()).append("|");
            //System.out.println("documentBuilder so far is: " + documentBuilder.toString());
            newTitle = false;
        }

        if (qName.matches("z:Attachment") & Mainthread.attachmentsIncluded) {
            //documentBuilder.append(attachmentBuilder.toString()).append("|");
            newAttachment = false;

        }

        if (qName.matches("dcterms:isPartOf")) {
            newIsPartOf = false;

        }


        if (qName.matches("dc:subject") & Mainthread.subjectsIncluded) {
            if (subjectBuilder.toString().contains(",")) {
                System.out.println("subject is: " + subjectBuilder.toString());
                documentBuilder.append(subjectBuilder.toString()).append("|");
                //System.out.println("documentBuilder so far is: " + documentBuilder.toString());

            }
            newSubject = false;
        }

        if (qName.matches("foaf:surname") & Mainthread.authorsIncluded) {
            System.out.println("author is: " + authorBuilder.toString());
            documentBuilder.append(authorBuilder.toString()).append("|");
            //System.out.println("documentBuilder so far is: " + documentBuilder.toString());

            newAuthor = false;
        }

        if (qName.contains("bib:authors") & !newIsPartOf & documentBuilder != null) {
            System.out.println("qName is: "+qName);
            System.out.println("localName is: "+localName);
            System.out.println("whole documentBuilder is: " + documentBuilder.toString());
            pool.execute(new WorkerThread(documentBuilder.toString()));
            newDocument = false;
        }



    }

    public void WorkerThread(String extractedText) {
        try {
            extractedText = extractedText.replace("&", " and ");
            extractedText = extractedText.replace("<", " ");
            extractedText = extractedText.replace(">", " ");
            extractedText = extractedText.replaceAll("\n", "thisisanendofsentence");
            extractedText = extractedText.replaceAll("\\p{C}", " ");
            extractedText = extractedText.replaceAll("thisisanendofsentence", "\n");

            StringBuilder filteredText = new StringBuilder();
            StringBuilder incipitLines = new StringBuilder();

            //System.out.println(currMonthHere);
            //System.out.println("in the WT2");







            //ConvertStreamToString cs = new ConvertStreamToString();
            //String s = cs.convertStreamToString(new FileInputStream(Mainthread.XMLOutput));

            ArrayList<String> lines = new ArrayList(Arrays.asList(extractedText.split("\r?\n")));
            //s = null;
            Iterator<String> it1 = lines.iterator();
            String longestString = "";
            maxLineSize = 0;
            //boolean badLineDetected;
            countLines = 0;
            while (it1.hasNext()) {
//                if (ParserXML.counter > 500) {
//                    break;
//                }

                String currLine = it1.next().trim();
                if (!currLine.matches(".*\\w.*")) {

                    continue;
                }

                int indexEndString = Math.min(currLine.length(), 50);

//                if(currLine.length()>50)
//                
//                {
//                    System.out.println("currLine is:\""+currLine+"\"");
//                    System.out.println(countLines+" / "+lines.size());
//            }

                if ((!"null".equals(Mainthread.stopWordsFile) & Mainthread.badLinesSet.contains(currLine))) {
//                        System.out.println("line ignored but continuing the loop!"); 
                    continue;
                }

//
//                    Iterator<String> it2 = Mainthread.badLines.iterator();
//                    //badLineDetected = false;
//
//                    while (it2.hasNext()) {
//                        currBadLine = it2.next();
//                        //System.out.println(currBadLine);
//                        //if ("Customer Centric Thoughts from the World’s Sharpest Minds in Marketing, Strategy, Innovation and Design".equals(currBadLine)){System.out.println("yes");}
//
//                        //Regex part
//                        Pattern r = Pattern.compile(currBadLine, Pattern.CASE_INSENSITIVE);
//                        Matcher m = r.matcher(currLine);
//
//                        if (m.find()) {
//                            currLine = currLine.replaceAll(currBadLine, " ");
//                            //System.out.println(currBadLine);
//
//                        }
//
//                    }
                countLines++;
                currLineSize = currLine.length();
                if (currLineSize > maxLineSize) {
                    longestString = currLine.substring(0, indexEndString);
                    maxLineSize = currLineSize;
                }

                filteredText = filteredText.append(currLine);
                incipitLines.append(currLine.substring(0, indexEndString));
                if (countLines == 5) {

                    newItemInCorpus = Mainthread.incipit.add(incipitLines.toString().substring(0, indexEndString));

                    if (!newItemInCorpus) {
                        System.out.println("breaking because duplicate found with incipit!");
                        System.out.println("incipit was: " + incipitLines.toString().substring(0, indexEndString));
                        return;
                    }




                }



            }

            newLongestSentence = Mainthread.longestLines.add(longestString);
            if (!newLongestSentence) {
                System.out.println("breaking because duplicate found with longest sentence!");
                System.out.println("longest line was: " + longestString);

                return;
            }


            //            BufferedWriter fileFiltered = new BufferedWriter(new FileWriter(Mainthread.filteredOutput,true));
            //            try{fileFiltered.write(currLine);}catch (NullPointerException e){System.out.println("currLine was null, did not print to the Output");}
            //            fileFiltered.newLine();
            //            fileFiltered.close();





            synchronized (Mainthread.queue) {
                Mainthread.queue.add(filteredText.toString());
                System.out.println("text added!");
            }
            Mainthread.emptyQueue();

//            synchronized(Mainthread.output){
//            Mainthread.output = new BufferedWriter(new FileWriter(Mainthread.XMLOutput, true));
//            Mainthread.output.write(filteredText);
//            Mainthread.output.newLine();
//            Mainthread.output.close();}
        } catch (IOException ex) {
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
