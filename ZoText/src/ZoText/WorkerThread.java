/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 */
public class WorkerThread implements Runnable {

    String extractedText;
    String currItemType;
    String currYearHere;
    String currMonthHere;
    private int countLines;
    private boolean newItemInCorpus;
    ArrayList<String> incipitLines = new ArrayList();
    private int lineSize;
    private int maxlineSize = 0;
    private int currmaxlineSize;
    private String longestString;
    private boolean newLongestSentence;

    WorkerThread(String extractedText, String tempYear, String tempMonth, String itemType) {

        this.extractedText = extractedText;
        this.currMonthHere = tempMonth;
        this.currYearHere = tempYear;

    }

    @Override
    public void run() {


        try {

            extractedText = extractedText.replace("&", "and");
            extractedText = extractedText.replace("<", "");
            extractedText = extractedText.replace(">", "");
            extractedText = extractedText.replaceAll("\n", "thisisanendofsentence");
            extractedText = extractedText.replaceAll("\\p{C}", " ");
            extractedText = extractedText.replaceAll("thisisanendofsentence", "\n");

            //System.out.println(currMonthHere);
            //System.out.println("in the WT2");





            String filteredText = "";

            //ConvertStreamToString cs = new ConvertStreamToString();
            //String s = cs.convertStreamToString(new FileInputStream(Mainthread.XMLOutput));

            ArrayList<String> lines = new ArrayList(Arrays.asList(extractedText.split("\r?\n")));
            //s = null;
            Iterator<String> it1 = lines.iterator();
            boolean badLineDetected;

            while (it1.hasNext()) {
                countLines++;
                String currBadLine = null;
                String currLine = it1.next();

                if (!"null".equals(Mainthread.stopWordsFile)) {
                    Iterator<String> it2 = Mainthread.badLines.iterator();
                    badLineDetected = false;

                    while (it2.hasNext()) {
                        currBadLine = it2.next();
                        //System.out.println(currBadLine);
                        //if ("Customer Centric Thoughts from the World’s Sharpest Minds in Marketing, Strategy, Innovation and Design".equals(currBadLine)){System.out.println("yes");}

                        //Regex part
                        Pattern r = Pattern.compile(currBadLine, Pattern.CASE_INSENSITIVE);
                        Matcher m = r.matcher(currLine);

                        if (m.find()) {
                            currLine = currLine.replaceAll(currBadLine, " ");
                            //System.out.println(currBadLine);

                        }

                    }
                }
                currLine = StringUtils.strip(currLine);
                lineSize = currLine.length();
                currmaxlineSize = maxlineSize;
                maxlineSize = Math.max(maxlineSize, lineSize);
                if (currmaxlineSize != maxlineSize) {
                    longestString = currLine;
                }

                filteredText = filteredText.concat(currLine);
                incipitLines.add(currLine);
                if (countLines == 3) {

                    newItemInCorpus = Mainthread.incipit.add(incipitLines);

                    if (!newItemInCorpus) {
                        return;
                    }




                }
                countLines = 0;


            }

            newLongestSentence = Mainthread.longestLines.add(longestString);
            if (!newLongestSentence) {
                return;
            }


            //            BufferedWriter fileFiltered = new BufferedWriter(new FileWriter(Mainthread.filteredOutput,true));
            //            try{fileFiltered.write(currLine);}catch (NullPointerException e){System.out.println("currLine was null, did not print to the Output");}
            //            fileFiltered.newLine();
            //            fileFiltered.close();







            BufferedWriter output = new BufferedWriter(new FileWriter(Mainthread.XMLOutput, true));
            //output.write("<item itemType=\"" + currItemType + "\" year=\"" + currYearHere + "\" month=\"" + currMonthHere + "\">");
            //output.newLine();
            //System.out.println(txt); 
            output.write(filteredText);
            output.newLine();
            //output.write("</item>");
            //output.newLine();


            output.close();




        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}