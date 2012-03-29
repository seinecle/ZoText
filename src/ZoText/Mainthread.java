    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

/**
 *
 * @author C. Levallois
 */
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import java.awt.Component;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Mainthread {

    /**
     * @param args the command line arguments
     */
    public static int counter = 0;
    static String XMLOutput;
    static String stopWordsFile;
    //static String stopWordsFile = "D:/Docs Pro Clement/Writing/Article Neuromarketing/analysis/stopwords.txt";
    static ArrayList<String> badLines = new ArrayList();
    static HashSet<String> incipit = new HashSet();
    static HashSet<String> badLinesSet = new HashSet();
    static HashSet<String> longestLines = new HashSet();
    static String wkDir;
    static String uniqueFileNames;
    static String includeHTML;
    static String includePDF;
    static String abstractsIncluded;
    static String titlesIncluded;
    static String attachmentsIncluded;
    static ParserXML Pxml;
    static BufferedWriter output;
    private static BufferedWriter bf;
    static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue();
    private static Component frame;
    private static String currLine;
    private static Multiset<String> freqSet = HashMultiset.create();
    private static List<Entry<String>> freqList;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {

//        try {
            wkDir = args[3];
            stopWordsFile = args[4];
            System.out.println("stopWordsFile: "+stopWordsFile);
            XMLOutput = wkDir + "\\ZoteroText.txt";
            System.out.println("XML Output: "+XMLOutput);
            uniqueFileNames = args[2];
            System.out.println("uniqueFileNames: "+uniqueFileNames);
            includeHTML = args[0];
            System.out.println("include HTML: "+includeHTML);
            includePDF = args[1];
            System.out.println("include PDF: "+includePDF);
            abstractsIncluded = args[5];
            System.out.println("abstracts included: "+abstractsIncluded);
            titlesIncluded = args[6];
            System.out.println("titles included: "+titlesIncluded);
            attachmentsIncluded = args[7];
            System.out.println("attachmenents included: "+attachmentsIncluded);

            if (!"null".equals(stopWordsFile)) {
                //badLines = Mainthread.buildStopWordList();
                badLinesSet.addAll(Mainthread.buildStopWordSet());
            }

            readMasterList();
            System.out.println("done!");
            System.exit(0);
    }

    static void readMasterList() throws IOException {
        
        
        output = new BufferedWriter(new FileWriter(Mainthread.XMLOutput, false));
        output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        output.newLine();
        output.write("<root>");
        output.newLine();
        output.close();
        InputSource is = new InputSource(new StringReader(getFileContents(wkDir + "/My Library.rdf")));
        Pxml = new ParserXML(is, "masterList");
        
        

    }

    public static ArrayList buildStopWordList() throws IOException {

        //ConvertStreamToString cs = new ConvertStreamToString();
        InputStream is = new FileInputStream(stopWordsFile);
        String s = IOUtils.toString(is, "UTF-8");
        //String s = cs.convertStreamToString(new FileInputStream(Mainthread.stopWordsFile));
        return new ArrayList(Arrays.asList(s.split("\r\n")));

    }

    public static ArrayList buildStopWordSet() throws IOException {

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

    public static void emptyQueue() throws IOException {
        BufferedWriter bf = null;

        synchronized (bf = new BufferedWriter(new FileWriter(Mainthread.XMLOutput, true))) {
            while (!queue.isEmpty()) {
                String s = queue.poll();
                if (s != null) {
                    bf.write(s);
                    bf.newLine();
                }
            }
            bf.flush();
            bf.close();
        }


    }
}
