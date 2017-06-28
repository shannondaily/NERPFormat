import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class DigiKey implements Vendor{
    public ArrayList<String> shortText;
    public ArrayList<Integer> quantity;
    public ArrayList<Double> valuationPrice;
    public DigiKey(){
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
                if(x!=1&&row.length>=5){
                    shortText.add((String)row[3]);
                    quantity.add(Integer.parseInt(row[1]));
                    valuationPrice.add(Double.parseDouble(row[7]));
                }
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
