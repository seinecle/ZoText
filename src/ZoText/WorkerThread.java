/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    StringBuilder incipitLines = new StringBuilder();
    StringBuilder filteredText = new StringBuilder();
    private int lineSize;
    private int maxlineSize = 0;
    private int currmaxlineSize;
    private String longestString;
    private boolean newLongestSentence;

    WorkerThread(String extractedText) {

        this.extractedText = extractedText;


    }

    @Override
    public void run() {
        try {
           extractedText = extractedText.replace("&", " and ");
            extractedText = extractedText.replace("<", " ");
            extractedText = extractedText.replace(">", " ");
            extractedText = extractedText.replaceAll("\n", "thisisanendofsentence");
            extractedText = extractedText.replaceAll("\\p{C}", " ");
            extractedText = extractedText.replaceAll("thisisanendofsentence", "\n");

            //System.out.println(currMonthHere);
            //System.out.println("in the WT2");







            //ConvertStreamToString cs = new ConvertStreamToString();
            //String s = cs.convertStreamToString(new FileInputStream(Mainthread.XMLOutput));

            ArrayList<String> lines = new ArrayList(Arrays.asList(extractedText.split("\r?\n")));
            //s = null;
            Iterator<String> it1 = lines.iterator();
            //boolean badLineDetected;
            countLines = 0;
            while (it1.hasNext()) {
                if (ParserXML.counter > 200) {
                    break;
                }
                countLines++;
                String currLine = it1.next().trim();

                int indexEndString = Math.min(currLine.length(), 50);

                if(currLine.length()>20)
//                
                {
                    System.out.println(currLine);
            }
                
                if (!"null".equals(Mainthread.stopWordsFile) & Mainthread.badLinesSet.contains(currLine)) {

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

                lineSize = currLine.length();
                currmaxlineSize = maxlineSize;
                maxlineSize = Math.max(maxlineSize, lineSize);
                if (currmaxlineSize != maxlineSize) {
                    longestString = currLine.substring(0, indexEndString);
                }

                filteredText = filteredText.append(currLine);
                incipitLines.append(currLine.substring(0, indexEndString));
                if (countLines == 2) {

                    newItemInCorpus = Mainthread.incipit.add(incipitLines.toString().substring(0, indexEndString));

                    if (!newItemInCorpus) {
                        return;
                    }




                }



            }

            newLongestSentence = Mainthread.longestLines.add(longestString);
            if (!newLongestSentence) {
                return;
            }


            //            BufferedWriter fileFiltered = new BufferedWriter(new FileWriter(Mainthread.filteredOutput,true));
            //            try{fileFiltered.write(currLine);}catch (NullPointerException e){System.out.println("currLine was null, did not print to the Output");}
            //            fileFiltered.newLine();
            //            fileFiltered.close();





            synchronized (Mainthread.queue) {
                Mainthread.queue.add(filteredText.toString());
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