
import java.util.ArrayList;
import java.io.*;

public class Allied implements Vendor {

    //The cells of the CSV file being read are stored here
    private ArrayList[] data;

    //This instance variable is where the column of information is located in the
    //Original file and where it should go
    //First Number what column it is located, second what column it should go to
    //Initialized at zero
    private final int[][] location = {{4, 4}, {6, 5}};

    //This method goes through the CSV file being read and stores the cells in to the
    //Instance varaiable data
    @Override
    public void extractData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            //Read the first line to get the number of columns
            String line = br.readLine();
            String[] row = line.split(",");

            //Create array for data and initialize each arraylist
            data = new ArrayList[row.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = new ArrayList();
            }

            //Read the rest of the file
            while ((line = br.readLine()) != null) {
                //Split the file by commas
                row = line.split(",");
                //Store the information
                //Use i to traverse through each line and j through each column of data
                //Ignore rows that have a blank first column
                int j = 0;
                for (int i = 0; i < row.length && row[0].length() > 0; i++) {
                    //If the cell is mepty add an empty string
                    if ((row[i]).length() == 0) {
                        data[j].add("");
                    } //If the cell has commas in it loop through multiple commas
                    //Until the final part is found
                    else if (row[i].charAt(0) == '"') {
                        String temp = row[i];
                        while (temp.charAt(temp.length() - 1) != '"') {
                            temp += "," + row[++i];
                        }
                        temp = temp.replaceAll("[^A-Za-z0-9 .,;:_/\"-]", "");
                        data[j].add(temp);
                    } //If not a special case add it to data
                    else {
                        String temp = row[i].replaceAll("[^A-Za-z0-9 .,;:_/\"-]", "");
                        data[j].add(temp);
                    }
                    j++;
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }

    //This method creates the output csv file
    public void createFile(String fileName) {
        FileWriter writer = null;
        try {
            //Add the headers to the file
            String line = "Doc Type,Account Assignment,Item Category,"
                    + "Material,Short Text,Quantity,UoM,Delivery Date,"
                    + "Material Group,Plant,Porg,Purchasing Group,Requisitioner,"
                    + "Req. Tracking Number,Desired Vendor,Fixed Vendor,Valuation "
                    + "Price,G/L Account,Cost Center,WBS Element,Network,Activity,"
                    + "Fiscal Year,Fund Code,Functional Area,Funds Center,Earmarked "
                    + "Funds,Earmarked Funds Line item,DODAAC,Standard Document Number,"
                    + "Credit Card Number,ACRN,Project Code,Instrument Type";
            String[] row = line.split(",");
            writer = new FileWriter(fileName);
            writer.append(line);
            writer.append('\n');

            //Create 2d array to represent each cell of new csv file
            Object[][] spreadSheet = new Object[row.length][data[0].size()];
            //Initialize each element to a blank string
            for (int i = 0; i < spreadSheet.length; i++) {
                for (int j = 0; j < spreadSheet[i].length; j++) {
                    spreadSheet[i][j] = "";
                }
            }

            //Write each colum where it should go
            //Loop through each pair of positions
            for (int[] pair : location) {
                //Extract each position and store it in int
                int currentPosition = pair[0];
                int destination = pair[1];
                //Loop through the spreadsheet 2d array
                for (int i = 0; i < spreadSheet.length; i++) {
                    //If the columns match write into the array
                    if (i == destination) {
                        for (int j = 0; j < spreadSheet[i].length; j++) {
                            spreadSheet[destination][j] = data[currentPosition].get(j);
                        }
                    }
                }
            }

            //Write into the csv file going through columns first
            for (int j = 0; j < spreadSheet[0].length; j++) {
                for (int i = 0; i < spreadSheet.length; i++) {
                    writer.append((String) spreadSheet[i][j]);
                    writer.append(",");
                }
                writer.append('\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
            }
        }
    }
}
