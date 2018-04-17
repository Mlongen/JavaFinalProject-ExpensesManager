package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ChoiceBox;
import java.io.*;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable{

    private Database database;
    @FXML
    private TableView<Entry> table;
    @FXML
    private TableColumn<Entry, String> description;
    @FXML
    private TableColumn<Entry, Double> value;
    @FXML
    private TableColumn<Entry, String> category;
    @FXML
    private Button deleteButton;
    @FXML
    private ChoiceBox monthPicker;

    public ObservableList<Entry> list = FXCollections.observableArrayList();

    public ObservableList<Entry> filteredList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Scanner input = null;
        try {
            input = new Scanner(new File("database.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //reading text file
        String read = input.nextLine();
        // creating new database object
        Database db = new Database();
        //populating database with scanner read
        db.readAndCreateArray(read);
        //creating observablelist to display tables
        list = FXCollections.observableArrayList(db.getObjects());
        filteredList.addAll(list);
        System.out.println(filteredList);



        //populating columns
        description.setCellValueFactory(new PropertyValueFactory<Entry, String>("description"));
        value.setCellValueFactory(new PropertyValueFactory<Entry, Double>("value"));
        category.setCellValueFactory(new PropertyValueFactory<Entry, String>("category"));
        table.setItems(filteredList);
        //listening for changes

        // Setting choiceBox Items
        monthPicker.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        );
        monthPicker.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                    if (newValue == "January") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 01) {
                                filteredList.remove(i);
                                i--;
                            }

                        }

                    }
                    else if (newValue == "February") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 02) {
                                filteredList.remove(i);
                                i--;
                            }

                        }

                    }
                    else if (newValue == "March") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 03) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                }
        );



























        //delete button action
        deleteButton.setOnAction(e -> {
            Entry selectedItem = table.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Entry removal");
            alert.setContentText("Are you sure you want to delete this entry?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){

                //removes the item selected
                table.getItems().remove(selectedItem);

                //creates a formatted string for the output

                List<String> formattingString = new ArrayList<>();
                for (int i = 0; i < list.size();i++) {
                    formattingString.add(list.get(i).getDescription() + ";" + list.get(i).getValue() + ";" + list.get(i).getDay() + ";" +
                            list.get(i).getMonth() + ";" + list.get(i).getYear() + ";" + list.get(i).getCategory());
                }
                String formattedString = String.join(";", formattingString);

                //saves output file

                try (PrintWriter out = new PrintWriter(new FileWriter("database.txt", false))) {
                    out.println(formattedString);
                } catch (IOException z) {
                    z.printStackTrace();
                }

            }
            else {
                // ... user chose CANCEL or closed the dialog
            }

        });

    }
}
