import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
public class DigiKey implements Vendor{
    private ArrayList[] data;
    //First Number what column it is located, second what column it should go to
    private final int[][] location={{3,4},{1,5},{7,6}};
    public ArrayList[] getData(){
        return data;
    }
    public int[][] getLocation(){
        return location;
    }
    public void extractData(String fileName){
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            line = br.readLine();
            String[] row = line.split(",");
            data=new ArrayList[row.length];
            for(int k=0;k<data.length;k++){
                data[k]=new ArrayList();
            }
            int j;
            while ((line = br.readLine()) != null) {
                row = line.split(",");
                j=0;
                for(int i=0;i<row.length&&((String)row[0]).length()>0;i++){
                    if(((String)row[i]).length()==0){
                        data[j].add("");
                    }
                    else if(((String)row[i]).charAt(0)=='"'){
                        String temp=row[i];
                        while(temp.charAt(temp.length()-1)!='"'){
                            temp+=","+row[i];
                            i++;
                        }
                        data[j].add(temp);
                    }
                    else{
                        data[j].add(row[i]);
                    }
                    j++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createFile(String fileName){
        FileWriter writer = null;
        try(BufferedReader br = new BufferedReader(new FileReader("headers.txt"))) {
            String line= br.readLine();
            String[] row = line.split(",");
            writer = new FileWriter(fileName);
            writer.append(line);
            writer.append('\n');
            
            Object[][] spreadSheet=new Object[row.length][data[0].size()];
            for(int i=0;i<spreadSheet.length;i++){
                for(int j=0;j<spreadSheet[i].length;j++){
                    spreadSheet[i][j]="";
                }
            }
            for (int[] pair : location){
                int currentPosition=pair[0];
                int destination=pair[1];
                for(int i=0;i<spreadSheet.length;i++){
                    if(i==destination){
                        for(int j=0;j<spreadSheet[i].length;j++){
                            spreadSheet[destination][j]=data[currentPosition].get(j);
                        }
                    }
                }
            }
            for(int j=0;j<spreadSheet[0].length;j++){
                for(int i=0;i<spreadSheet.length;i++){
                    writer.append((String)spreadSheet[i][j]);
                    writer.append(",");
                }
                writer.append('\n');
            }
         } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
