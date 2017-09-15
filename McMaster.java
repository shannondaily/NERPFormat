import java.util.ArrayList;
import java.io.*;
import java.math.BigDecimal;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
public class McMaster implements Vendor{
    //The cells of the CSV file being read are stored here
    private ArrayList[] data;

    //This instance variable is where the column of information is located in the
    //Original file, initialized at zero
    private final int priceCol = 6;
    private final int descriptionCol = 4;
    private final int quantityCol = 1;

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
            while (line != null) {
                //Split the file by commas
                row = line.split(",");
                //Store the information
                //Use i to traverse through each line and j through each column of data
                int j = 0;
                for (int i = 0; i < row.length && row[0].length() > 0; i++) {
                    //If the cell is mepty add an empty string
                    if (((String) row[i]).length() == 0) {
                        data[j].add("");
                    } //If the cell has commas in it loop through multiple commas
                    //Until the final part is found
                    else if (row[i].charAt(0) == '"') {
                        String temp = row[i];
                        while (temp.charAt(temp.length() - 1) != '"') {
                            temp += "," + row[++i];
                        }
                        temp = temp.replaceAll("[^A-Za-z0-9 .]", "");
                        data[j].add(temp);
                    } //If not a special case add it to data
                    else {
                        String temp = row[i].replaceAll("[^A-Za-z0-9 .]", "");
                        data[j].add(temp);
                    }
                    j++;
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
        }
    }
    //This method creates the output xls file
    @Override
    public void createFile(String fileName, String docType, String account, String uom, String date, String requesitioner, String trackNum, String desVendor, String network, String activity, String creditCard, String wbs, String glAccount, String material){
        try {
            //Open template file to modify
            FileInputStream excelFile = new FileInputStream(new File("lib/template.xls"));
            Workbook workbook = new HSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 1;
            
            //Go through each row and add the values to each cell
            for(int i = 0; i < data[0].size(); i++){
                Row row = sheet.getRow(rowNum++);
                Cell[] cell = new Cell[34];
                for(int j = 0; j < cell.length; j++){
                    cell[j] = row.getCell(j);
                    //If there is a cell that has not been initialized copy the formatting of the avobe cell
                    if(cell[j] == null){
                        Row tempRow = sheet.getRow(rowNum - 2);
                        Cell tempCell = tempRow.getCell(j);
                        CellStyle tempCellStyle = tempCell.getCellStyle();
                        cell[j] = row.createCell(j);
                        cell[j].setCellStyle(tempCellStyle);
                    }
                }
                cell[0].setCellValue(docType);
                cell[1].setCellValue(account);
                cell[3].setCellValue(material);
                cell[4].setCellValue(((String) data[descriptionCol].get(i)).toUpperCase());
                cell[5].setCellValue(Double.parseDouble((String) data[quantityCol].get(i)));
                cell[6].setCellValue(uom);
                cell[7].setCellValue(date);
                cell[9].setCellValue("WC17");
                cell[10].setCellValue(1700);
                cell[11].setCellValue("NHC");
                cell[12].setCellValue(requesitioner);
                cell[13].setCellValue(trackNum);
                //If no desired vendor typed skip
                if (desVendor != null) {
                    cell[14].setCellValue(new BigDecimal(desVendor).doubleValue());
                }
                cell[16].setCellValue(Double.parseDouble((String) data[priceCol].get(i)));
                cell[17].setCellValue(glAccount);
                cell[19].setCellValue(wbs);
                //If not network typed skip
                if (network != null) {
                    cell[20].setCellValue(new BigDecimal(network).doubleValue());
                }
                cell[21].setCellValue(activity);
                cell[30].setCellValue(creditCard);
            }
            
            excelFile.close();
            
            //Create the output file
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            
        }
    }
}
