/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author C. Levallois
 */
public class Writer extends Thread{

    static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue();
    
    @Override
public void run(){
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(Mainthread.XMLOutput, true));
            while (!queue.isEmpty()){
                String s = queue.poll();
                if (s!=null){
                    bf.write(s);
                    bf.newLine();
                }
            }
            bf.flush();
      


        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bf.close();
            } catch (IOException ex) {
                Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
}
