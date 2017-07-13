import java.io.*;
import java.util.*;
import com.opencsv.*;

public class McMaster
{
	//Instance variables
	private static String [][] vendorSpreadSheet;
	private static String [][] NERPSpreadSheet;
	
	//extractData method
	//Takes the vendor CSV file and puts into a data structure
	//Stores data in an instance variable
	//Return: void, Parameters: String fileName (vendor CSV)
	public void extractData(String inputFileName)
	{
		//Variables
		String line = null;
		int numberOfColumns, numberOfRows, arrayListCount;
		ArrayList<String> rawTextList = new ArrayList<String>();
		ArrayList<String> separatedTextList = new ArrayList<String>();
		
		//McMaster tableview doesn't include headers. I need to add the headers to the CSV file prior
		//to anything else, because my program searches for the data by the header.
		rawTextList.add("Line,Quantity,Unit of Measure,Product,Description,Ships,Unit Price,Total");
		
		//Add each line of the CSV file to a new element of the ArrayList.
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(inputFileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Why is the reader reading the null line? Filled with commas? Doesn't matter. I will just need
		//to add some logic to keep from using those extra values to the NERP file. If line empty, return.
		try{
			while((line = reader.readLine()) != null)
			{
				rawTextList.add(line);
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//numberOfColumns will be the number of comma separated data points in the first element of rawTextList.
		String [] num = rawTextList.get(0).split(",");
		numberOfColumns = num.length;
		
		//numberOfRows will be the size of rawTextList
		numberOfRows = rawTextList.size();
		
		//Initialize the size of vendorSpreadSheet.
		vendorSpreadSheet = new String [numberOfRows][numberOfColumns];
		
		//Fill 2D array to avoid null pointer exception
		for(int n = 0; n < vendorSpreadSheet.length; n++)
		{
			for(int m = 0; m < vendorSpreadSheet[n].length; m++)
			{
				vendorSpreadSheet[n][m] = "";
			}
		}
		
		//rawTextList has each line as an element.
		//Add the CSV data to the vendorSpreadSheet
		for(int i = 0; i < numberOfRows; i++ )
		{			
			String [] record = rawTextList.get(i).split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			
			for(int j = 0; j < record.length; j++)
			{
				if(j < numberOfColumns)
				{
					vendorSpreadSheet[i][j] = record[j];
				}				
			}
		}
		
		//Create 1d array of the header strings.
		String [] headers = ("Doc Type,Account Assignment,Item Category,Material,Short Text,Quantity,UoM,Delivery Date,"
				+ "Material Group,Plant,Porg,Purchasing Group,Requisitioner,Req. Tracking Number,Desired Vendor,Fixed Vendor,"
				+ "Valuation Price,G/L Account,Cost Center,WBS Element,Network,Activity,Fiscal Year,Fund Code,Functional Area,"
				+ "Funds Center,Earmarked Funds,Earmarked Funds Line item,DODAAC,Standard Document Number,Credit Card Number,"
				+ "ACRN,Project Code,Instrument Type").split(",");
		
		//Initialize the size of the NERPSpreadSheet
		NERPSpreadSheet = new String [vendorSpreadSheet.length][headers.length];
		
		//Fill NERPSpreadSheet with empty Strings
		for(int a = 0; a < NERPSpreadSheet.length; a++)
		{
			for(int b = 0; b < NERPSpreadSheet[a].length; b++)
			{
				NERPSpreadSheet[a][b] = "";
			}
		}
		
		//Add headers to NERPSpreadSheet
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < NERPSpreadSheet[i].length; j++)
			{
				NERPSpreadSheet[i][j] = headers[j];
			}
		}
    }//End extractData method
	
	
	//createFile method
	//Takes data and finds columns from vendor that are needed
	//Puts new data in spreadsheet that has all of the headers
	//Return: void, Parameter: String fileName
	public void createFile(String outputFileName)
	{
		//Variables
		int cIndex;
		String quantityString = "Quantity";
		String partDescriptionString = "Description";
		
		String NERPQuantityString = "Quantity";
		String NERPPartDescriptionString = "Short Text";
		
		ArrayList<String> qtyData = new ArrayList<>();
		ArrayList<String> partDescription = new ArrayList<>();
		

//Putting data in the right columns, just not getting the correct data.
		
		//Grab the items needed from spreadSheet: ShortText and Qty
		//Get index of and store data for quantity
		cIndex = getColumnIndex(vendorSpreadSheet, quantityString);
		getColumnData(cIndex, qtyData);
		
		cIndex = getColumnIndex(NERPSpreadSheet, NERPQuantityString);
		addVendorData(cIndex, qtyData);
	
		//Get index of and store data for short text
		cIndex = getColumnIndex(vendorSpreadSheet, partDescriptionString);
		getColumnData(cIndex, partDescription);
		
		cIndex = getColumnIndex(NERPSpreadSheet, NERPPartDescriptionString);
		addVendorData(cIndex, partDescription);
		
		//Create CSV file
		writeCSVFile(outputFileName);	
	}//End createFile method
	
	
	//Methods--------------------------------------------------------------------------------
	//getColumnIndex method
	public static int getColumnIndex(String [][] spreadSheet, String anyString)
	{
		int columnIndex = 0;
		for(int i = 0; i < spreadSheet.length; i++)
		{
			for(int j = 0; j < spreadSheet[i].length; j++)
			{
				if(spreadSheet[i][j].equals(anyString))
				{
					columnIndex = j;
				}
			}
		}
		return columnIndex;
	}//End getColumnIndex method
	
	
	//getColumnData method
	public static void getColumnData(int columnIndex, ArrayList<String> data)
	{
		for(int row = 0; row < vendorSpreadSheet.length; row++)
		{
			for(int col = 0; col < vendorSpreadSheet[row].length; col++)
			{
				if(col == columnIndex && row > 0)
				{
					data.add(vendorSpreadSheet[row][col]);
				}
			}
		}
	}//End getColumnData method
	
	
	//addVendorData method
	public static void addVendorData(int cIndex, ArrayList<String> vendorData)
	{
		//Add vendor data to NERP column
		for(int i = cIndex; i < cIndex + 1; i++)
		{
			for(int j = 1; j < vendorData.size(); j++)
			{
				NERPSpreadSheet[j][i] = vendorData.get(j-1);
			}
		}
	}
		
	
	//writeCSVFile method
	public static void writeCSVFile(String outputFileName)
	{
		//Variables
		boolean onlyCommas;
		
		//Declare and initialize the CSVWriter using the outputFileName
	    CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(outputFileName));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Add all elements to the CSV file
		for(int i = 0; i < NERPSpreadSheet.length; i++)
		{	
			//Empty string to add row data to. Empty each time the loop starts over.
			String emptyString = "";
			
			//Add each element in a row to a string separated by commas
			for(int j = 0; j < NERPSpreadSheet[i].length; j++)
			{
				emptyString += NERPSpreadSheet[i][j] + ",";
			}
					
			//Dont add row if it only contains commas
			if(onlyCommas = emptyString.chars().allMatch(c -> c == ','))
			{
				//Skip to the end
				i = NERPSpreadSheet.length;
			}
			
			else
			{
				//Write new row of data
				String [] record = emptyString.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				String s;
				
				for(int p = 0; p < record.length; p++)
				{
					record[p] = record[p].replaceAll("\"", "");
					
				}			
				
				//Write the record to file
				writer.writeNext(record);
			}
		}
	        
	    //Close the writer
	    try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//End writeCSVFile	
}//End class