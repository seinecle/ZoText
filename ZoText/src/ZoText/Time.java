/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZoText;

/**
 *
 * @author C. Levallois
 */
public class Time {

    String year;
    String month;
    String day;

    Time(String time) {

        String fullTime[];
        fullTime = time.split("-");
        if (fullTime.length == 2) {
            //System.out.println(time);
            //System.out.println(time);
            day = "15";
            year = fullTime[1];
            month = fullTime[0];


        } else {

            if (fullTime.length == 1) {
                //System.out.println("WEIRD TIME " + time);
                if (time.matches(".*\\d\\d\\d\\d.*")) {
                    year = time.replaceAll("(.*)(\\d\\d\\d\\d)(.*)", "$2");
                    month = "6";
                    day = "15";

                };
            } else {
                year = fullTime[2];
                month = fullTime[1];
                day = fullTime[0];
                if (fullTime[2].length() == 2 && fullTime[0].length() == 4) {
//                    System.out.println("WEIRD YEAR " + fullTime[2]);
//                    System.out.println("WEIRD YEAR: CORRESPONDING time " + time);
                    year = fullTime[0];
                    day = fullTime[2];


                }

            }
        }
        //System.out.println(year + " " + month + " " + day);

    }

public String getDay(){return day;}

}


