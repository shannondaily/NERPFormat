import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Allied implements Vendor{
    private ArrayList<String> shortText;
    private ArrayList<Integer> quantity;
    private ArrayList<Double> valuationPrice;
    public Allied(){
        shortText=new ArrayList();
        quantity=new ArrayList();
        valuationPrice=new ArrayList();
    }
    public ArrayList<String> getShortText(){
        return shortText;
    }
    public ArrayList<Integer> getQuantity(){
        return quantity;
    }
    public ArrayList<Double> getValuationPrice(){
        return valuationPrice;
    }
    public void extractData(String fileName){
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int x=1;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if(x!=1&&row[0].length()>0){
                    if(((String)row[4]).charAt(0)=='"'){
                        int i=5;
                        String temp=row[4];
                        while(temp.charAt(temp.length()-1)!='"'){
                            temp+=","+row[i];
                            i++;
                        }
                        shortText.add(temp);
                        quantity.add(Integer.parseInt(row[i+1]));
                        temp=row[i+2];
                        String temp2="";
                        for(int j=2;j<temp.length()-1;j++){
                            temp2+=temp.charAt(j);
                        }
                        valuationPrice.add(Double.parseDouble(temp2));
                    }
                    else{
                        shortText.add((String)row[4]);
                        String temp=row[6];
                        String temp2="";
                        for(int j=2;j<temp.length()-1;j++){
                            temp2+=temp.charAt(j);
                        }
                        valuationPrice.add(Double.parseDouble(temp2));
                        valuationPrice.add(Double.parseDouble(row[7]));
                    } 
                }
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
