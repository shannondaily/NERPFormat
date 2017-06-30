import java.util.ArrayList;
public interface Vendor {
    public ArrayList[] getData();
    public void extractData(String fileName);
    public int[][] getLocation();
    public void createFile(String fileName);
}