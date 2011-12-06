/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

/**
 *
 * @author C. Levallois
 */
import java.io.IOException;
import java.util.HashSet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;

public class ParserXML extends DefaultHandler {

    public String tempVal;
    public boolean btext = false;
    private String typeDoc;
    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
    private boolean bDate;
    private Time tempTime;
    private boolean bItemType;
    public String itemType;
    private String tempYear;
    private String tempMonth;
    private int counter;
    public String extractedText;
    private HashSet<String> fileSet = new HashSet();

    public ParserXML(InputSource toBeParsed, String type) throws IOException {

        typeDoc = type;

        parseDocument(toBeParsed);
        //printData();




    }

    public String getTypeDoc() {

        return typeDoc;
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

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = null;



        if (qName.equalsIgnoreCase("dc:Date")) {
            {
                bDate = true;
            }

        }

        if (qName.equalsIgnoreCase("rdf:resource")) {
            try {
                //create a new instance of text
                btext = true;
                //tempText = new Text();
                counter++;
                //if (counter < 20) {

                    String fileName = attributes.getValue(0);
                    //System.out.println(attributes.getValue(0));
                    String[] fileSplit = attributes.getValue(0).split("/");
                    boolean fileIsUnique = fileSet.add(fileSplit[fileSplit.length - 1]);
                    //System.out.println(fileSplit[fileSplit.length-1]);

                    boolean isHTML = fileName.contains(".html");
                    boolean isPDF = fileName.contains(".pdf");
                    //if (isHTML) pool.execute(new BoilerPlateExtractor(attributes.getValue(0)));
                    if (fileIsUnique || "false".equals(Mainthread.uniqueFileNames)) {

                        if (isHTML && "true".equals(Mainthread.includeHTML)) {
                            extractedText = new BoilerPlateExtractor(attributes.getValue(0)).run();
                            pool.execute(new WorkerThread(extractedText, tempYear, tempMonth, itemType));
                        }
                        if (isPDF && "true".equals(Mainthread.includePDF)) {
                            extractedText = new PdfExtractor(attributes.getValue(0)).run();
                            pool.execute(new WorkerThread(extractedText, tempYear, tempMonth, itemType));    
                        }
                        
                        //extractedText = new TextCleaner(extractedText).run();
//                    extractedText = BadWordsFilter.run(extractedText);
//                    new XMLWriter(extractedText,tempYear,tempMonth,itemType).run();
                        


                        //tempVal = tempVal + attributes.getValue(0);
                    }
               // }
            } catch (IOException ex) {
                Logger.getLogger(ParserXML.class.getName()).log(Level.SEVERE, null, ex);
            }

        }



    }

    public void characters(char[] ch, int start, int length) throws SAXException {


        if (bDate) {
            tempVal = tempVal + new String(ch, start, length);
        }



    }

    public void endElement(String uri, String localName, String qName) throws SAXException {


        if (qName.equalsIgnoreCase("rdf:resource") && tempVal != null) {
            btext = false;
        }

        if (qName.equalsIgnoreCase("dc:Date")) {
            bDate = false;
            //System.out.println(tempVal);
            tempVal = tempVal.substring(4);


            tempTime = new Time(tempVal);
            tempYear = tempTime.year;
            tempMonth = tempTime.month;

        }


    }
}
