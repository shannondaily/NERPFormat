import java.util.ArrayList;
public interface Vendor {
    public ArrayList<String> getShortText();
    public ArrayList<Integer> getQuantity();
    public ArrayList<Double> getValuationPrice();
    public void extractData(String fileName);
}