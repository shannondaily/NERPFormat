import java.io.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.value.*;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import javafx.geometry.Insets;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public final class test2 extends Application {

    final ComboBox[] vendorDropdown = new ComboBox[5];
    String[] originalFilePath = new String[5];
    String[] originalFileName = new String[5];
    String[] selectedVendor = new String[5];
    String[] newFileFolder = new String[6];
    String[] newFileName = new String[6];
    boolean[] wrongFormat = new boolean[5];
    int counter = 0;

    @Override
    public void start(final Stage stage) {

        stage.setTitle("NERP Format");
        stage.resizableProperty().setValue(Boolean.FALSE);

        Text[] fileDisplay = new Text[5];
        for (int i = 0; i < 5; i++) {
            fileDisplay[i] = new Text("Select File");
            fileDisplay[i].setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        }
        Text selectVendor = new Text("  Select Vendor");
        selectVendor.setFont(Font.font("Verdana", FontWeight.BOLD, 10));

        for (int i = 0; i < 5; i++) {
            vendorDropdown[i] = new ComboBox();
            vendorDropdown[i].getItems().addAll("Allied", "DigiKey", "Newark");
            final int index = i;
            vendorDropdown[i].getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                    selectVendor(ov, oldValue, newValue, index);
                }
            });
        }

        final FileChooser[] fileChooser = new FileChooser[5];
        final Button[] openButton = new Button[5];

        for (int i = 0; i < 5; i++) {
            fileChooser[i] = new FileChooser();
            openButton[i] = new Button("Select File");
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

        final Button convertFile = new Button("Convert Files");
        final DirectoryChooser directoryChooser = new DirectoryChooser();

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

        final Button combineFiles = new Button("Combine Files");
        final DirectoryChooser directoryChooserCombined = new DirectoryChooser();

        combineFiles.setOnAction((final ActionEvent e) -> {
            boolean flag = true;
            for (int i = 0; i <= counter && flag; i++) {
                if (originalFilePath[i] == null) {
                    flag = false;
                    openModal(stage, "Please select file for option " + (i + 1));
                } else if (selectedVendor[i] == null) {
                    flag = false;
                    openModal(stage, "Please select vendor for option " + (i + 1));
                }
            }
            if (flag) {
                directoryChooserCombined.setTitle("Select Destination");
                final File selectedDirectory = directoryChooserCombined.showDialog(stage);
                if (selectedDirectory != null) {
                    for (int i = 0; i <= counter; i++) {
                        getNewFileName(selectedDirectory, i);
                        directoryChooser.setInitialDirectory(selectedDirectory);
                        createFile(i);
                    }
                    getNewFileName(selectedDirectory, 5);
                    directoryChooserCombined.setInitialDirectory(selectedDirectory);
                    combineFiles();
                    openModal(stage, "File Has Been Created");
                }
            }
        });

        final Button addOption = new Button("Add new file");
        final Button removeOption = new Button("Remove file");

        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(selectVendor, 2, 2);
        GridPane.setConstraints(convertFile, 3, 1);
        GridPane.setConstraints(combineFiles, 3, 3);
        GridPane.setConstraints(addOption, 1, 1);
        GridPane.setConstraints(removeOption, 2, 1);
        for (int i = 0; i < 5; i++) {
            GridPane.setConstraints(fileDisplay[i], 0, i + 3);
            GridPane.setConstraints(openButton[i], 1, i + 3);
            GridPane.setConstraints(vendorDropdown[i], 2, i + 3);
        }

        inputGridPane.setHgap(8);
        inputGridPane.setVgap(8);
        inputGridPane.getChildren().addAll(selectVendor, convertFile, addOption, removeOption, fileDisplay[0], openButton[0], vendorDropdown[0]);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(60, 90, 60, 90));

        addOption.setOnAction((final ActionEvent e) -> {
            if (counter < 4) {
                counter++;
                inputGridPane.getChildren().addAll(fileDisplay[counter], openButton[counter], vendorDropdown[counter]);
                stage.sizeToScene();
            }
            if (counter == 1) {
                inputGridPane.getChildren().addAll(combineFiles);
                stage.sizeToScene();
            }
        });

        removeOption.setOnAction((final ActionEvent e) -> {
            if (counter > 0) {
                inputGridPane.getChildren().remove(fileDisplay[counter]);
                inputGridPane.getChildren().remove(openButton[counter]);
                inputGridPane.getChildren().remove(vendorDropdown[counter]);
                counter--;
                stage.sizeToScene();
            }
            if (counter == 0) {
                inputGridPane.getChildren().remove(combineFiles);
                stage.sizeToScene();
            }
        });

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }

    public void getOriginalFileName(File file, int index, Text text) {
        originalFilePath[index] = file.getPath();
        originalFileName[index] = file.getName();
        text.setText(originalFileName[index]);
    }

    public void selectVendor(ObservableValue<? extends String> observable, String oldValue, String newValue, int index) {
        selectedVendor[index] = newValue;
    }

    public void getNewFileName(File directory, int index) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        newFileFolder[index] = directory.getPath();
        newFileName[index] = newFileFolder[index] + "/NERP-FORMAT-" + sdf.format(timestamp) + index + ".csv";
    }

    public void createFile(int index) {
        if (selectedVendor[index].equals("DigiKey")) {
            DigiKey digiKey = new DigiKey();
            digiKey.extractData(originalFilePath[index]);
            digiKey.createFile(newFileName[index]);
        } else if (selectedVendor[index].equals("Allied")) {
            Allied allied = new Allied();
            allied.extractData(originalFilePath[index]);
            allied.createFile(newFileName[index]);
        } else if (selectedVendor[index].equals("Newark")) {

        }
    }

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

    public void findVendor(File file, int index, Stage stage) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String digiKeyHeader = "IndexQuantityPartNumberDescriptionCustomerReferenceAvailable"
                    + "QuantityBackorderQuantityUnitPriceUSDExtendedPriceUSD";
            String alliedHeader = "MaterialNumberMfrMaterialNumberCustomerMaterialNumberManufacturer"
                    + "MaterialDescriptionInStockQuantityPriceEachUSDYourPriceUSD";
            String newarkHeader = "LineNoNewarkPartNoRoHSCompliantNotesQtyManufacturerPartNumber"
                    + "ManufacturerNameManufacturerDescriptionAvailabilityUnitPriceLinePriceDiscountPrice";
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
                wrongFormat[index] = true;
                openModal(stage, "File " + (index + 1) + " is not formatted correctly");
            }
            br.close();
        } catch (IOException e) {
        }
    }

    public void combineFiles() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(newFileName[5]);
            for (int i = 0; i <= counter; i++) {
                try (BufferedReader br = new BufferedReader(new FileReader(newFileName[i]))) {
                    String line = br.readLine();
                    if (i == 0) {
                        writer.append(line);
                        writer.append('\n');
                    }
                    while ((line = br.readLine()) != null) {
                        writer.append(line);
                        writer.append('\n');
                    }
                }
                File file = new File(newFileName[i]);
                file.delete();
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

    public static void main(String[] args) {
        Application.launch(args);
    }
}
