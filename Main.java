
import java.io.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.value.*;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public final class Main extends Application {
    
    //Variables that will be changed by the methods and need to be outside of them
    final ComboBox[] vendorDropdown = new ComboBox[5];
    String[] originalFilePath = new String[5];
    String[] originalFileName = new String[5];
    String[] selectedVendor = new String[5];
    String[] newFileFolder = new String[6];
    String[] newFileName = new String[6];
    String[] docType = new String[5];
    String[] account = new String[5];
    String[] uom = new String[5];
    String[] date = new String[5];
    String[] requisitioner = new String[5];
    String[] trackNum = new String[5];
    String[] desVendor = new String[5];
    String[] network = new String[5];
    String[] activity= new String[5];
    String[] creditCard = new String[5];
    String[] glAccount = new String[5];
    String[] wbs = new String[5];
    String[] material = new String[5];
    boolean[] wrongFormat = new boolean[5];
    int counter = 0;

    @Override
    //Method to launch GUI
    public void start(final Stage stage) {
        
        //Create the stage, set the title and eliminate the maximize button
        stage.setTitle("NERP Format");
        stage.resizableProperty().setValue(Boolean.FALSE);
        
        //Create the text that will display the file name, initialize to "Select File" and make it Bold
        Text[] fileDisplay = new Text[5];
        for (int i = 0; i < 5; i++) {
            fileDisplay[i] = new Text("Select File");
            fileDisplay[i].setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        }
        
        //Create the text above the options and make it bold
        Text selectVendor = new Text("  Select Vendor");
        selectVendor.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        
        Text selectDocTypeText = new Text("Select Doctype");
        selectDocTypeText.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        
        Text selectUOM = new Text("Select UoM");
        selectUOM.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        
        Text selectDate = new Text("Delivery Date");
        selectDate.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        
        Text selectAccount = new Text("Account Assignment");
        selectAccount.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        
        //Inititialize each of the vendor selectors, add the vendors and a listener
        for (int i = 0; i < 5; i++) {
            vendorDropdown[i] = new ComboBox();
            vendorDropdown[i].getItems().addAll("", "Allied", "DigiKey", "McMaster", "Newark");
            final int index = i;
            vendorDropdown[i].getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                    selectedVendor[index] = newValue;
                }
            });
        }
        
        //Create combobox array for doc type
        final ComboBox[] docTypeDropdown = new ComboBox[5];
        
        //Initialize each combobox
        for (int i = 0; i < 5; i++) {
            docTypeDropdown[i] = new ComboBox();
            docTypeDropdown[i].getItems().addAll("", "ZLCL", "ZMAN", "ZPCD", "ZSPS");
            final int index = i;
            docTypeDropdown[i].getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                    docType[index] = newValue;
                }
            });
        }
        
        //Create combobox array for uom dropdown menu
        final ComboBox[] uomDropdown = new ComboBox[5];
        
        //Initialize each combobox and add a listener
        for (int i = 0; i < 5; i++) {
            uomDropdown[i] = new ComboBox();
            uomDropdown[i].getItems().addAll("","AU", "EA");
            final int index = i;
            uomDropdown[i].getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                    uom[index] = newValue;
                }
            });
        }
        
        //Create the ccount dropdown
        final ComboBox[] accountDropdown = new ComboBox[5];
        for(int i = 0; i < 5; i++){
            accountDropdown[i] = new ComboBox();
            accountDropdown[i].getItems().addAll("", "N", "P");
        }
        
        
        //Create the fileChoosers and their respective butttons
        final FileChooser[] fileChooser = new FileChooser[5];
        final Button[] openButton = new Button[5];
        
        //Initialize each file chooser and button, call methods to change the displayed text to the file name,
        //save the file name and path, resize the window and automatically select the right vendor
        for (int i = 0; i < 5; i++) {
            fileChooser[i] = new FileChooser();
            openButton[i] = new Button("Add File");
            final FileChooser tempFileChooser = fileChooser[i];
            final Text tempText = fileDisplay[i];
            final Button tempButton = openButton[i];
            final int index = i;
            tempButton.setOnAction((final ActionEvent e) -> {
                tempFileChooser.setTitle("Select Files to convert");
                tempFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                File file = tempFileChooser.showOpenDialog(stage);
                if (file != null) {
                    getOriginalFileName(file, index, tempText);
                    tempFileChooser.setInitialDirectory(file.getParentFile());
                    stage.sizeToScene();
                    findVendor(file, index, stage);
                }
            });
        }
        
        //Create text inputs and initialize them
        TextField[] requisitionerInput = new TextField[5];
        initializeTextField(requisitionerInput, "Requisitioner", requisitioner, 10);
        TextField[] trackNumInput = new TextField[5];
        initializeTextField(trackNumInput, "Req. Tracking Number", trackNum, 6);
        TextField[] desVendorInput = new TextField[5];
        initializeTextField(desVendorInput, "Desired Vendor", desVendor, 6);
        TextField[] networkInput = new TextField[5];
        initializeTextField(networkInput,"Network", network, 10);
        TextField[] activityInput = new TextField[5];
        initializeTextField(activityInput, "Activity", activity, 10);
        TextField[] creditCardInput = new TextField[5];
        initializeTextField(creditCardInput, "Credit Card", creditCard, 6);
        TextField[] glAccountInput = new TextField[5];
        initializeTextField(glAccountInput, "G/L Account", glAccount, 10);
        TextField[] wbsInput = new TextField[5];
        initializeTextField(wbsInput, "WBS", wbs, 10);
        TextField[] materialInput = new TextField[5];
        initializeTextField(materialInput, "Material", material, 10);
        
        //Initialize each date selector and save the date
        DatePicker datePicker[] = new DatePicker[5];
        for (int i = 0; i < 5; i++) {
            datePicker[i] = new DatePicker();
            final DatePicker tempDatePicker = datePicker[i];
            final int index = i;
            tempDatePicker.setOnAction(event -> {
                final LocalDate selectedDate = tempDatePicker.getValue();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                date[index] = selectedDate.format(format);
            });
        }
        
        
        //Create the convert files button and a directory chooser to choose destination
        final Button convertFile = new Button("Convert Files");
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        
        //Go trough each option and check to see if each file and vendor are selected
        //and the correct format is selected, if true then select directory for file to go,
        //use it to create the new file name and create the new file
         //print success message
        convertFile.setOnAction((final ActionEvent e) -> {
            boolean flag = true;
            for (int i = 0; i <= counter && flag; i++) {
                if (originalFilePath[i] == null) {
                    flag = false;
                    openModal(stage, "Please select file for option " + (i + 1));
                } else if (selectedVendor[i] == null) {
                    flag = false;
                    openModal(stage, "Please select vendor for option " + (i + 1));
                } else if (wrongFormat[i]) {
                    flag = false;
                    openModal(stage, "File for option " + (i + 1) + " is not formatted correctly");
                }
            }
            if (flag) {
                directoryChooser.setTitle("Select Destination");
                final File selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory != null) {
                    for (int i = 0; i <= counter; i++) {
                        getNewFileName(selectedDirectory, i);
                        directoryChooser.setInitialDirectory(selectedDirectory);
                        createFile(i);
                    }
                    if (counter > 0) {
                        openModal(stage, "Files Have Been Created");
                    } else {
                        openModal(stage, "File Has Been Created");
                    }
                }
            }
        });
        
        //Create buttons to add more ooptions and remove options
        final Button addOption = new Button("Add More Files");
        final Button removeOption = new Button("Remove Files");
        
        //Create gridpane and add all the elements
        final GridPane inputGridPane = new GridPane();
        
        GridPane.setConstraints(selectVendor, 2, 2);
        GridPane.setConstraints(selectDocTypeText, 3, 2);
        GridPane.setConstraints(selectUOM, 4, 2);
        GridPane.setConstraints(selectDate, 5, 2);
        GridPane.setConstraints(convertFile, 5, 1);
        GridPane.setConstraints(addOption, 3, 1);
        GridPane.setConstraints(removeOption, 4, 1);
        GridPane.setConstraints(selectAccount, 6, 2);
        for (int i = 0; i < 5; i++) {
            GridPane.setConstraints(fileDisplay[i], 0, 3 * i + 3);
            GridPane.setConstraints(openButton[i], 1, 3 * i + 3);
            GridPane.setConstraints(vendorDropdown[i], 2, 3 * i + 3);
            GridPane.setConstraints(docTypeDropdown[i], 3, 3 * i + 3);
            GridPane.setConstraints(uomDropdown[i], 4, 3 * i + 3);
            GridPane.setConstraints(datePicker[i], 5, 3 * i + 3);
            GridPane.setConstraints(accountDropdown[i], 6, 3 * i + 3);
            GridPane.setConstraints(activityInput[i], 7, 3 * i + 3);
            GridPane.setConstraints(glAccountInput[i], 7, 3 * i + 3);
            GridPane.setConstraints(requisitionerInput[i], 3, 3 * i + 4);
            GridPane.setConstraints(trackNumInput[i], 4, 3 * i + 4);
            GridPane.setConstraints(desVendorInput[i], 5, 3 * i + 4);
            GridPane.setConstraints(networkInput[i], 7, 3 * i + 4);
            GridPane.setConstraints(wbsInput[i], 7, 3 * i + 4);
            GridPane.setConstraints(creditCardInput[i], 6, 3 * i + 4);
            GridPane.setConstraints(materialInput[i], 2, 3 * i + 4);
        }
        
        //Set the vertical and horizontal gaps and add the elements
        inputGridPane.setHgap(8);
        inputGridPane.setVgap(8);
        inputGridPane.getChildren().addAll(openButton[0], materialInput[0], accountDropdown[0], selectAccount, creditCardInput[0], desVendorInput[0], trackNumInput[0], requisitionerInput[0], datePicker[0], docTypeDropdown[0], uomDropdown[0], selectDate, selectDocTypeText, selectUOM, selectVendor, convertFile, addOption, removeOption, fileDisplay[0], vendorDropdown[0]);
        stage.sizeToScene();
        
        //Set padding
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(60, 90, 60, 90));
        
        //For add option add another option if there is still room
        addOption.setOnAction((final ActionEvent e) -> {
            if (counter < 4) {
                counter++;
                inputGridPane.getChildren().addAll(fileDisplay[counter], materialInput[counter], accountDropdown[counter], creditCardInput[counter], openButton[counter], vendorDropdown[counter], docTypeDropdown[counter], uomDropdown[counter], datePicker[counter], requisitionerInput[counter], trackNumInput[counter], desVendorInput[counter]);
                stage.sizeToScene();
            }
        });
        
        //Remove options if there are extra
        removeOption.setOnAction((final ActionEvent e) -> {
            if (counter > 0) {
                inputGridPane.getChildren().remove(fileDisplay[counter]);
                inputGridPane.getChildren().remove(openButton[counter]);
                inputGridPane.getChildren().remove(vendorDropdown[counter]);
                inputGridPane.getChildren().remove(docTypeDropdown[counter]);
                inputGridPane.getChildren().remove(uomDropdown[counter]);
                inputGridPane.getChildren().remove(datePicker[counter]);
                inputGridPane.getChildren().remove(requisitionerInput[counter]);
                inputGridPane.getChildren().remove(trackNumInput[counter]);
                inputGridPane.getChildren().remove(desVendorInput[counter]);
                inputGridPane.getChildren().remove(networkInput[counter]);
                inputGridPane.getChildren().remove(activityInput[counter]);
                inputGridPane.getChildren().remove(creditCardInput[counter]);
                inputGridPane.getChildren().remove(accountDropdown[counter]);
                inputGridPane.getChildren().remove(materialInput[counter]);
                counter--;
                stage.sizeToScene();
            }
        });
        
        //Initialize each account combobox and add listener
        for (int i = 0; i < 5; i++) {
            final int index = i;
            accountDropdown[i].getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                    account[index] = newValue;
                    if(newValue.equals("P")){
                        inputGridPane.getChildren().addAll(glAccountInput[index], wbsInput[index]);
                        
                        activityInput[index].clear();
                        activity[index] = null;
                        inputGridPane.getChildren().remove(activityInput[index]);
                        
                        networkInput[index].clear();
                        network[index] = null;
                        inputGridPane.getChildren().remove(networkInput[index]);
                        
                        stage.sizeToScene();
                    }
                    else if(newValue.equals("N")){
                        inputGridPane.getChildren().addAll(activityInput[index], networkInput[index]);
                        
                        glAccountInput[index].clear();
                        glAccount[index] = null;
                        inputGridPane.getChildren().remove(glAccountInput[index]);
                        
                        wbsInput[index].clear();
                        wbs[index] = null;
                        inputGridPane.getChildren().remove(wbsInput[index]);
                        
                        stage.sizeToScene();
                    }
                    else{
                         activityInput[index].clear();
                        activity[index] = null;
                        inputGridPane.getChildren().remove(activityInput[index]);
                        
                        networkInput[index].clear();
                        network[index] = null;
                        inputGridPane.getChildren().remove(networkInput[index]);
                        
                        glAccountInput[index].clear();
                        glAccount[index] = null;
                        inputGridPane.getChildren().remove(glAccountInput[index]);
                        
                        wbsInput[index].clear();
                        wbs[index] = null;
                        inputGridPane.getChildren().remove(wbsInput[index]);
                        
                        stage.sizeToScene();
                    }
                }
            });
        }

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }
    
    //This method gets the original file name and displays it and stores its path
    public void getOriginalFileName(File file, int index, Text text) {
        originalFilePath[index] = file.getPath();
        originalFileName[index] = file.getName();
        text.setText(originalFileName[index]);
    }
    
    //This method creates the new file name by using the path of the selected directory and a time stamp
    public void getNewFileName(File directory, int index) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        newFileFolder[index] = directory.getPath();
        newFileName[index] = newFileFolder[index] + "/NERP-FORMAT-" + sdf.format(timestamp) + index + ".xls";
    }
    
    //This method selects the proper vendor and creates the file
    public void createFile(int index) {
        if (selectedVendor[index].equals("DigiKey")) {
            DigiKey digiKey = new DigiKey();
            digiKey.extractData(originalFilePath[index]);
            digiKey.createFile(newFileName[index], docType[index], account[index], uom[index], date[index], requisitioner[index], trackNum[index], desVendor[index], network[index], activity[index], creditCard[index], wbs[index], glAccount[index], material[index]);
        } else if (selectedVendor[index].equals("Allied")) {
            Allied allied = new Allied();
            allied.extractData(originalFilePath[index]);
            allied.createFile(newFileName[index], docType[index], account[index], uom[index], date[index], requisitioner[index], trackNum[index], desVendor[index], network[index], activity[index], creditCard[index], wbs[index], glAccount[index], material[index]);
        } else if (selectedVendor[index].equals("Newark")) {
            Newark newark = new Newark();
            newark.extractData(originalFilePath[index]);
            newark.createFile(newFileName[index], docType[index], account[index], uom[index], date[index], requisitioner[index], trackNum[index], desVendor[index], network[index], activity[index], creditCard[index], wbs[index], glAccount[index], material[index]);
        } else if (selectedVendor[index].equals("McMaster")) {
            McMaster mcMaster = new McMaster();
            mcMaster.extractData(originalFilePath[index]);
            mcMaster.createFile(newFileName[index], docType[index], account[index], uom[index], date[index], requisitioner[index], trackNum[index], desVendor[index], network[index], activity[index], creditCard[index], wbs[index], glAccount[index], material[index]);
        }
    }
    
    //This method opens a modal and dysplays text
    public void openModal(Stage stage, String message) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.resizableProperty().setValue(Boolean.FALSE);
        Button btn = new Button("Close");
        btn.setOnAction((final ActionEvent e) -> {
            dialog.close();
        });
        final GridPane inputGridPane = new GridPane();
        GridPane.setConstraints(new Text(message), 1, 1);
        GridPane.setConstraints(btn, 1, 2);
        inputGridPane.setHgap(3);
        inputGridPane.setVgap(4);
        inputGridPane.getChildren().addAll(new Text(message), btn);
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(45, 45, 45, 45));
        dialog.setScene(new Scene(rootGroup));
        dialog.show();
    }
    
    //This method detects the correct vendor, if string not found on first line search for it,
    //at bottom of file for mcmaster files, if format not found dysplay an error message and 
    //set the value of wrong format ot true
    public void findVendor(File file, int index, Stage stage) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String digiKeyHeader = "IndexQuantityPartNumberDescriptionCustomerReferenceAvailable"
                    + "QuantityBackorderQuantityUnitPriceUSDExtendedPriceUSD";
            String alliedHeader = "MaterialNumberMfrMaterialNumberCustomerMaterialNumberManufacturer"
                    + "MaterialDescriptionInStockQuantityPriceEachUSDYourPriceUSD";
            String newarkHeader = "LineNoNewarkPartNoRoHSCompliantNotesQtyManufacturerPartNumber"
                    + "ManufacturerNameManufacturerDescriptionAvailabilityUnitPriceLinePriceDiscountPrice";
            String mcmaster = "MerchandiseTotal";
            String line = br.readLine().replaceAll("[^A-Za-z0-9]", "");
            if (line.equals(digiKeyHeader)) {
                wrongFormat[index] = false;
                selectedVendor[index] = "DigiKey";
                vendorDropdown[index].setValue(selectedVendor[index]);
            } else if (line.equals(alliedHeader)) {
                wrongFormat[index] = false;
                selectedVendor[index] = "Allied";
                vendorDropdown[index].setValue(selectedVendor[index]);
            } else if (line.equals(newarkHeader)) {
                wrongFormat[index] = false;
                selectedVendor[index] = "Newark";
                vendorDropdown[index].setValue(selectedVendor[index]);
            } else {
                String cleanLine = null;
                while ((line = br.readLine()) != null) {
                    cleanLine = line.replaceAll("[^A-Za-z]", "");
                }
                if (cleanLine.equals(mcmaster)) {
                    wrongFormat[index] = false;
                    selectedVendor[index] = "McMaster";
                    vendorDropdown[index].setValue(selectedVendor[index]);
                } else {
                    wrongFormat[index] = true;
                    openModal(stage, "File " + (index + 1) + " is not formatted correctly");
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }
    //This method initializes texts inputs and adds a pormpt and a listener
    public void initializeTextField(TextField[] textField, String prompt, String[] save, int col) {
        for (int i = 0; i < textField.length; i++) {
            textField[i] = new TextField();
            textField[i].setPromptText(prompt);
            textField[i].setPrefColumnCount(col);
            final int index =i;
            textField[i].textProperty().addListener((observable, oldValue, newValue) -> {
                save[index] = newValue;
            });
        }
    }

    //Main method call mehtod that launches the gui
    public static void main(String[] args) {
        Application.launch(args);
    }
}
