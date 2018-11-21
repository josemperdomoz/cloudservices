
package aws;

import java.io.*;


public class CSV {

    public CSV(){

    }

    public void addToCSV(int measurementNumber, long responseTime) throws FileNotFoundException {

        try
        {
            FileWriter fileWriter = new FileWriter("downloadTest.txt",true);
            StringBuilder sb = new StringBuilder();

            sb.append(measurementNumber);
            sb.append(",");
            sb.append(responseTime);
            sb.append("\r\n");

            fileWriter.append(sb.toString());

            System.out.println("Stored 1 measurement!");
            fileWriter.close();
        }
        catch(IOException e1)
        {
            System.out.println(e1.toString());
        }

    }
}