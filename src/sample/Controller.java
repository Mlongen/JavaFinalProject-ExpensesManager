package sample;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ChoiceBox;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private TableColumn<Entry, String> date;

    @FXML
    private TableColumn<Entry, String> category;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField valueTextField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button addEntry;



    @FXML
    private Button deleteButton;

    @FXML
    private Label monthLabel;

    @FXML
    private ChoiceBox monthPicker;




    //CATEGORY STUFF

    @FXML
    private ChoiceBox categoryPicker;

    @FXML
    private Button addCategory;

    @FXML
    private Button removeCategory;

    @FXML
    private Button categoryOK;

    @FXML
    private TextField categoryAdder;


    //DATE FILTER STUFF


    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private ChoiceBox filterPicker;


    //BUDGET STUFF

    @FXML
    private Button addBudget;

    @FXML
    private Button removeBudget;

    @FXML
    private ChoiceBox budgetCategoryChooser;





    //parameter that helps calculate fromRange of date period
    private long fromMillis;

    //parameter that helps calculate toRange of date period
    private long toMillis;


    public ObservableList<Entry> list = FXCollections.observableArrayList();

    public ObservableList<Entry> filteredList = FXCollections.observableArrayList();

    ObservableList<String> categoryList = FXCollections.observableArrayList();


    @FXML
    private PieChart piechart;

    private int januaryTotal = 0;
    private int februaryTotal = 0;
    private int marchTotal = 0;
    private int aprilTotal = 0;
    private int mayTotal = 0;
    private int juneTotal = 0;
    private int julyTotal = 0;
    private int augustTotal = 0;
    private int septemberTotal = 0;
    private int octoberTotal = 0;
    private int novemberTotal = 0;
    private int decemberTotal = 0;


    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private Button barChartButton;

    @FXML
    private Button pieChartButton;

    @FXML
    private LineChart lineChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // READING FILES
        Scanner inputdb = null;
        try {
            inputdb = new Scanner(new File("database.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




        //reading database file
        String readdb = inputdb.nextLine();
        //reading category file





        // creating new database object
        Database db = new Database();
        //populating database with scanner read
        db.readAndCreateArray(readdb);
        //creating observablelist to display tables
        list = FXCollections.observableArrayList(db.getObjects());
        filteredList.addAll(list);



        //adding new entry

        addEntry.setOnAction (e -> {

            String description = descriptionTextField.getText().toString();
            double value = Double.valueOf(valueTextField.getText());
            int year = Integer.valueOf(datePicker.getValue().toString().substring(0, 4));
            int month = Integer.valueOf(datePicker.getValue().toString().substring(5, 7));
            int day = Integer.valueOf(datePicker.getValue().toString().substring(9, 10));
            String category = categoryPicker.getSelectionModel().getSelectedItem().toString();

            Entry newEntry = new Entry(description, value, day, month, year, category);
            list.add(newEntry);
            filteredList.add(newEntry);
            chartUpdater();

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

        });



        //populating columns
        description.setCellValueFactory(new PropertyValueFactory<Entry, String>("description"));
        value.setCellValueFactory(new PropertyValueFactory<Entry, Double>("value"));
        category.setCellValueFactory(new PropertyValueFactory<Entry, String>("category"));
        date.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getDay() + "/" +cellData.getValue().getMonth())));
        table.setItems(filteredList);
        //listening for changes

        // Setting choiceBox Items
        monthPicker.setItems(FXCollections.observableArrayList(
                  "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        );


        //category manager
        categoryOK.setVisible(false);
        categoryAdder.setVisible(false);
        categoryList = FXCollections.observableArrayList();
        Scanner inputcategory = null;
        try {
            inputcategory = new Scanner(new File("category.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String readcategory = inputcategory.nextLine();
        String[] splittedcategory = readcategory.split(";");
        categoryList.addAll(splittedcategory);
        categoryPicker.setItems(FXCollections.observableArrayList(categoryList));

        addCategory.pressedProperty().addListener((o, old, newValue) ->
                categoryAdder.setVisible(true));
        addCategory.pressedProperty().addListener((o, old, newValue) ->
                categoryOK.setVisible(true));


        categoryOK.setOnAction(e -> {
            String categoryadderinput = categoryAdder.getCharacters().toString();
            categoryList.add(categoryadderinput);
            categoryAdder.setVisible(false);
            categoryPicker.setItems(FXCollections.observableArrayList(categoryList));

           String[] categories = new String[categoryList.size()];
           for (int i = 0; i < categoryList.size();i++) {
               categories[i] = categoryList.get(i).toString();
           }
           String categoriesoutput = String.join(";", categories);
            try (PrintWriter out = new PrintWriter(new FileWriter("category.txt", false))) {
                out.println(categoriesoutput);
            } catch (IOException z) {
                z.printStackTrace();
            }
            categoryOK.setVisible(false);

        });

        //REMOVE BUTTON FOR CATEGORIES

        barChart.setVisible(false);

        barChartButton.setOnAction((e -> {
            piechart.setVisible(false);
            barChart.setVisible(true);

        }));

        pieChartButton.setOnAction((e -> {
            barChart.setVisible(false);
            piechart.setVisible(true);

        }));

        removeCategory.setOnAction(e -> {
            Object selectedString = categoryPicker.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Category removal");
            alert.setContentText("Are you sure you want to delete this category?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){

                //removes the item selected
                categoryPicker.getItems().remove(selectedString);
                categoryList.remove(selectedString);

                //creates a formatted string for the output

                String[] categories = new String[categoryList.size()];
                for (int i = 0; i < categoryList.size();i++) {
                    categories[i] = categoryList.get(i).toString();
                }
                String categoriesRemovedOutput = String.join(";", categories);
                try (PrintWriter out = new PrintWriter(new FileWriter("category.txt", false))) {
                    out.println(categoriesRemovedOutput);
                } catch (IOException z) {
                    z.printStackTrace();
                }
                categoryOK.setVisible(false);

            }
            else {
                // ... user chose CANCEL or closed the dialog
            }

        });


        //SETTING UP DATE PICKERS AND FILTERS

        fromMillis = 0;
        toMillis = 0;

        fromDatePicker.setOnAction(e -> {
            String fromValue = fromDatePicker.getValue().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDate = null;
            try {
                fromDate = sdf.parse(fromValue);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            fromMillis = fromDate.getTime();
            System.out.println(fromMillis);

                }
        );

        toDatePicker.setOnAction(e -> {

            //GETTING THE DATE
            String fromValue = toDatePicker.getValue().toString();
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDate = null;
            try {
                fromDate = sdf2.parse(fromValue);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            toMillis = fromDate.getTime();

            // POPULATING THE FILTEREDLIST
            filteredList.remove(0, filteredList.size());
            for (int i = 0; i < list.size(); i++) {

                long thisMillis = 0;
                String objectValue = list.get(i).getYear() + "-" + list.get(i).getMonth() + "-" + list.get(i).getDay();
                SimpleDateFormat x = new SimpleDateFormat("yyyy-MM-dd");
                Date thisDate = null;
                try {
                    thisDate = x.parse(objectValue);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                thisMillis = thisDate.getTime();
                if (thisMillis > fromMillis && thisMillis <= toMillis) {
                    filteredList.add(list.get(i));

                }
                else {
                    // ... user chose CANCEL or closed the dialog
                }

            }

                }
        );

        filterPicker.setItems(FXCollections.observableArrayList(
                "Period", "Month")
        );

        monthPicker.setVisible(false);
        monthLabel.setVisible(false);
        fromDatePicker.setVisible(false);
        toDatePicker.setVisible(false);

        filterPicker.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                if (newValue == "Period") {
                    monthPicker.setVisible(false);
                    monthLabel.setVisible(false);
                    fromDatePicker.setVisible(true);
                    toDatePicker.setVisible(true);


                }
                if (newValue == "Month") {
                    monthPicker.setVisible(true);
                    monthLabel.setVisible(true);
                    fromDatePicker.setVisible(false);
                    toDatePicker.setVisible(false);

                }}
            );



        monthPicker.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

                    if (newValue == "All") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);

                    }
                    else if (newValue == "January") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 1) {
                                filteredList.remove(i);
                                i--;
                            }

                        }

                    }
                    else if (newValue == "February") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 2) {
                                filteredList.remove(i);
                                i--;
                            }

                        }

                    }
                    else if (newValue == "March") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 3) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "April") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 4) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "May") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 5) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "June") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 6) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "July") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 7) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "August") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 8) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "September") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 9) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "October") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 10) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "November") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 11) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "December") {
                        filteredList.remove(0, filteredList.size());
                        filteredList.addAll(list);
                        for (int i = 0; i < filteredList.size();i++) {
                            if (filteredList.get(i).getMonth() != 12) {
                                filteredList.remove(i);
                                i--;
                            }

                        }
                    }
                }
        );

        //SETTING PIECHART DATA

        chartUpdater();


        //set default date in date picker as current day

        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date , formatter);
        datePicker.setValue(localDate);


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
                list.remove(selectedItem);
                chartUpdater();

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

    private void chartUpdater() {
        januaryTotal = 0;
        februaryTotal = 0;
        marchTotal = 0;
        aprilTotal = 0;
        mayTotal = 0;
        juneTotal = 0;
        julyTotal = 0;
        augustTotal = 0;
        septemberTotal = 0;
        octoberTotal = 0;
        novemberTotal = 0;
        decemberTotal = 0;
        for (int i = 0; i < list.size();i++) {
            if (list.get(i).getMonth() == 1) {
                januaryTotal += list.get(i).getValue();
            }
            else if (list.get(i).getMonth() == 2) {
                februaryTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 3) {
                marchTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 4) {
                aprilTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 5) {
                mayTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 6) {
                juneTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 7) {
                julyTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 8) {
                augustTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 9) {
                septemberTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 10) {
                octoberTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 11) {
                novemberTotal += list.get(i).getValue();

            }
            else if (list.get(i).getMonth() == 12) {
                decemberTotal += list.get(i).getValue();

            }

        }



        //PIE CHART

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("January: $" + januaryTotal, januaryTotal),
                        new PieChart.Data("February: $" + februaryTotal, februaryTotal),
                        new PieChart.Data("March: $" + marchTotal, marchTotal),
                        new PieChart.Data("April: $" + aprilTotal, aprilTotal),
                        new PieChart.Data("May: $" + mayTotal, mayTotal),
                        new PieChart.Data("June: $" + juneTotal, juneTotal),
                        new PieChart.Data("July: $" + julyTotal, julyTotal),
                        new PieChart.Data("August: $" + augustTotal, augustTotal),
                        new PieChart.Data("September: $" + septemberTotal, septemberTotal),
                        new PieChart.Data("October: $" + octoberTotal, octoberTotal),
                        new PieChart.Data("November: $" + novemberTotal, novemberTotal),
                        new PieChart.Data("December: $" + decemberTotal, decemberTotal));


        piechart.setTitle("Monthly analysis");
        piechart.setData(pieChartData);


        //BARCHART


        xAxis.setCategories(FXCollections.observableArrayList(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "December")));
        yAxis.setLabel("Value");

        barChart.setTitle("Monthly analysis");

        XYChart.Series<String, Number> moneySpent = new XYChart.Series<>();
        moneySpent.setName("Money spent");
        moneySpent.getData().add(new XYChart.Data<>("January", januaryTotal));
        moneySpent.getData().add(new XYChart.Data<>("February", februaryTotal));
        moneySpent.getData().add(new XYChart.Data<>("March", marchTotal));
        moneySpent.getData().add(new XYChart.Data<>("April", aprilTotal));
        moneySpent.getData().add(new XYChart.Data<>("May", mayTotal));
        moneySpent.getData().add(new XYChart.Data<>("June", juneTotal));
        moneySpent.getData().add(new XYChart.Data<>("July", julyTotal));
        moneySpent.getData().add(new XYChart.Data<>("August", augustTotal));
        moneySpent.getData().add(new XYChart.Data<>("September", septemberTotal));
        moneySpent.getData().add(new XYChart.Data<>("October", octoberTotal));
        moneySpent.getData().add(new XYChart.Data<>("November", novemberTotal));
        moneySpent.getData().add(new XYChart.Data<>("December", decemberTotal));

        barChart.getData().clear();
        barChart.getData().add(moneySpent);




        lineChart.setVisible(false);
        //LINE CHART




    }




}
