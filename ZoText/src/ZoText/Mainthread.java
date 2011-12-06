/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

/**
 *
 * @author C. Levallois
 */

import helloworldapp.ConvertStreamToString;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.xml.sax.InputSource;

public class Mainthread {

    /**
     * @param args the command line arguments
     */
    private static String chosenLanguage = "english";
    private static String detectedLanguage;
    public static int counter = 0;
    static String XMLOutput; 
    static String stopWordsFile;
    //static String stopWordsFile = "D:/Docs Pro Clement/Writing/Article Neuromarketing/analysis/stopwords.txt";
    static ArrayList<String> badLines = new ArrayList();
    static HashSet<ArrayList> incipit = new HashSet();
    static HashSet<String> longestLines = new HashSet();
    private static String wkDir;
    static String uniqueFileNames;
    static String includeHTML;
    static String includePDF;

    
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {

        

        wkDir = args[3];
        stopWordsFile = args[4];
        XMLOutput = wkDir+"\\ZoteroText.txt";
        uniqueFileNames = args[2];
        includeHTML = args[0];
        includePDF = args[1];
        
        
        if (!"null".equals(stopWordsFile)) badLines = Mainthread.buildStopWordList();
        readMasterList();
        System.exit(0);

        //CooccurrencesCounter.doCounting();

    }

    ;      

    
    
    
        static void readMasterList() throws IOException {
        // Detect the language for a HTML document.
        BufferedWriter output = new BufferedWriter(new FileWriter(Mainthread.XMLOutput,false));
        output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        output.newLine();
        output.write("<root>");
        output.newLine();
        output.close();
        InputSource is = new InputSource(new StringReader(getFileContents(wkDir+"/Exported Items.rdf")));
        ParserXML Pxml = new ParserXML(is, "masterList");
        output = new BufferedWriter(new FileWriter(Mainthread.XMLOutput,true));
        output.write("</root>");
        output.newLine();
        output.close();
        Pxml.pool.shutdown();

    }


        
        
    public static ArrayList buildStopWordList() throws IOException{
        
                //ConvertStreamToString cs = new ConvertStreamToString();
                InputStream is = new FileInputStream(stopWordsFile);
                String s = IOUtils.toString(is, "UTF-8");
                //String s = cs.convertStreamToString(new FileInputStream(Mainthread.stopWordsFile));
                return new ArrayList(Arrays.asList(s.split("\r\n")));
                
    }    
        
        
    // utility function
    public static String getFileContents(String filename)
            throws IOException, FileNotFoundException {
        File file = new File(filename);
        StringBuilder contents = new StringBuilder();

        BufferedReader input = new BufferedReader(new FileReader(file));

        try {
            String line = null;

            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            input.close();
        }

        return contents.toString();
    }

    // utility method
    public static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            return null;
        }

    }

    static String getDetectedLanguage() {
        return detectedLanguage;
    }

    static String getChosenLanguage() {
        return chosenLanguage;
    }
}
