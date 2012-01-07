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
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfExtractor {
    public final String currFile;
    public String extractedText;
    
    PdfExtractor(String file){
    currFile = file;
    
    }
    
    public String run() throws IOException{
        //PDDocument pdfDocument = new PDDocument();
                        try{
		        PDDocument pdfDocument = PDDocument.load( "D:/Docs Pro Clement/E-humanities/TextMining/Exported Items/" + currFile );
                        extractedText = new PDFTextStripper("UTF-8").getText(pdfDocument);
                        pdfDocument.close();

                        return extractedText;
                                    }catch(java.io.FileNotFoundException e){return extractedText="";}
                        


   
        
    }
    
    
}
