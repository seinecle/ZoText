/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author C. Levallois
 */
public class BoilerPlateExtractor{
    
    
String detectedLanguage;
String language;
String currFile;
String currItemType;
String currYearHere;
String currMonthHere;
public String extractedText;

BoilerPlateExtractor(String file) {

    currFile = file;

    
}


public String run() {
        try {
            String folderNum = currFile.substring(currFile.indexOf("/")+1, currFile.lastIndexOf("/"));
            //System.out.println(folderNum);
            try{InputStream is = new FileInputStream("D:/Docs Pro Clement/E-humanities/TextMining/Exported Items/" + currFile);
            BufferedReader input = new BufferedReader(new FileReader("D:/Docs Pro Clement/E-humanities/TextMining/Exported Items/" + currFile));
            String txt = IOUtils.toString(is, "UTF-8");
            extractedText = DefaultExtractor.INSTANCE.getText(input);
            input.close();

            }catch(java.io.FileNotFoundException e){return extractedText="";}

            
        } 
        catch (BoilerpipeProcessingException ex) {
            Logger.getLogger(BoilerPlateExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }          catch (IOException ex) {
            Logger.getLogger(BoilerPlateExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NullPointerException ex) {
            //System.out.println(currTimeHere.year);
            //System.out.println(currTimeHere.month);
            //System.out.println(currTimeHere.day);
        }

       return extractedText;       
}
}
