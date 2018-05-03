
        package sample;

        import com.jfoenix.controls.JFXButton;
        import com.jfoenix.controls.JFXComboBox;
        import com.jfoenix.controls.JFXDatePicker;
        import com.jfoenix.controls.JFXSlider;
        import javafx.beans.property.ReadOnlyStringWrapper;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.chart.*;
        import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.scene.control.ChoiceBox;
        import javafx.scene.text.Font;

        import java.io.*;
        import java.net.URL;
        import java.sql.Connection;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.time.LocalDate;
        import java.time.format.DateTimeFormatter;
        import java.util.*;

        import static sample.Database.*;

        public class Controller implements Initializable{

    //-------------------------------------------
    //
    //
    //        ADD NEW ENTRY
    //
    //

    @FXML
    private TextField descriptionTextField;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private TextField valueTextField;

    @FXML
    private JFXButton addEntry;

    // ------- CATEGORY STUFF

    @FXML
    private JFXComboBox categoryPicker;

    @FXML
    private JFXButton addCategory;

    @FXML
    private JFXButton removeCategory;

    @FXML
    private JFXButton categoryOK;

    @FXML
    private TextField categoryAdder;

    //-----------------------------------------
    //-----------------------------------------
    //-----------------------------------------


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
    private JFXButton deleteEntryButton;

    @FXML
    private ChoiceBox filterPicker;

    @FXML
    private Label monthLabel;

    @FXML
    private ChoiceBox monthPicker;



    //DATE FILTER STUFF


    @FXML
    private DatePicker fromDatePicker;

    //parameter that helps calculate fromRange of date period
    private long fromMillis = 0;

    @FXML
    private DatePicker toDatePicker;

    //parameter that helps calculate toRange of date period
    private long toMillis = 0;




    //BUDGET STUFF---------------------------------------

    @FXML
    private JFXButton addBudget;

    @FXML
    private JFXButton removeBudget;

    @FXML
    private ChoiceBox budgetCategoryChooser;

    @FXML
    private TableView budgetTable;

    @FXML
    private TableColumn<Budget, String> budgetCategory;

    @FXML
    private TableColumn<Budget, Double> budgetValue;

    @FXML
    private TableColumn<Budget, Integer> budgetPercentage;

    @FXML
    private JFXSlider alarmPercentageSlider;

    @FXML
    private TextField budgetValueTextField;

    //-----------------------------------------------------



    private ObservableList<Entry> rawEntryList;

    private ObservableList<Entry> displayedEntryList = FXCollections.observableArrayList();

    private ObservableList<String> categoryList = FXCollections.observableArrayList();

    private ObservableList<Budget> budgetList = FXCollections.observableArrayList();


    // CHART STUFF

    @FXML
    private JFXButton displayCategoryChartButton;

    @FXML
    private JFXButton monthChartButton;

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
    private JFXButton barChartButton;

    @FXML
    private JFXButton pieChartButton;

    @FXML
    private LineChart lineChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    private int userid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Database db = new Database();
        Connection conn = connect();
        //getting the user that is logged in
        userid = db.getCurrentUser(conn);
        System.out.println(userid);
        db.readAllEntries(conn, userid);
        populateTableColums();//populate entry table
        db.readAllBudgets(conn, userid);
        db.readCategories(conn, userid);

        rawEntryList = FXCollections.observableArrayList(db.getEntryObjects());
        displayedEntryList.addAll(rawEntryList);
        budgetList= FXCollections.observableArrayList(db.getBudgetObjects());
        categoryList = FXCollections.observableArrayList(db.getCategories());
        categoryPicker.setItems(FXCollections.observableArrayList(categoryList));

        //Adding logic to the Add expenses part
        addEntryActionSetter(conn, db);
        setTodaysDateOnDatePicker();

        //Adding logic to the remove entry button
        removeEntryActionSetter(conn);

        //Adding logic to the remove budget button
        removeBudgetActionSetter(conn);

        // Defining visiblity and stuff for the category adder
        categoryAdderSetVisibility();

        //Adding logic to the OK Button on the category manager
        categoryOKButtonActionSetter(conn);

        //Adding logic to the Remove Category  Button
        removeCategoryActionSetter(conn);

        //Adding logic to the add budget
        addBudgetActionSetter(conn);

        //Setting the items on the filter Date Pickers, and setting visibility
        setFilterPickersItemsAndVisibility();

        //Adding logic to the filter date picker
        filterPickerActionSetter();

        //Setting the logic to the fromDatePicker
        fromDatePickerActionSetter();

        // Setting the logic to the toDatePicker, also embeds the SEARCH functionality
        toDatePickerActionSetter();

        //Adding logic to the month Filter, embeds categoryUpdater() functionality
        monthFilterListenerAndActionSetter();

        //Sets the char initial visibility and adds function to chart buttons
        setInitialChartVisibilityAndActionSetter();

        populateBudgetTable();//populate budget table

        //Sets pieChart data
        chartUpdater();


        //DESIGN LOADING



    }

    public void addBudgetActionSetter(Connection conn) {
        addBudget.setOnAction((e -> {
            insertBudget(conn, budgetCategoryChooser, budgetValueTextField, alarmPercentageSlider, budgetList, userid);
        }));
    }

    public void setTodaysDateOnDatePicker() {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        datePicker.setValue(localDate);

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }




    public void setInitialChartVisibilityAndActionSetter() {
        barChart.setVisible(false);

        barChartButton.setOnAction((e -> {
            piechart.setVisible(false);
            barChart.setVisible(true);

        }));

        pieChartButton.setOnAction((e -> {
            barChart.setVisible(false);
            piechart.setVisible(true);

        }));
    }

    public void filterPickerActionSetter() {
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
    }

    public void monthFilterListenerAndActionSetter() {
        monthPicker.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

                    if (newValue == "All") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);

                    }
                    else if (newValue == "January") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 1) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }

                    }
                    else if (newValue == "February") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 2) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }

                    }
                    else if (newValue == "March") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 3) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "April") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 4) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "May") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 5) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "June") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 6) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "July") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 7) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "August") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 8) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "September") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 9) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "October") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 10) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "November") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 11) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    else if (newValue == "December") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        for (int i = 0; i < displayedEntryList.size(); i++) {
                            if (displayedEntryList.get(i).getMonth() != 12) {
                                displayedEntryList.remove(i);
                                i--;
                            }

                        }
                    }
                    categoryChartUpdater();
                }
        );
    }

    public void toDatePickerActionSetter() {
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
                    displayedEntryList.remove(0, displayedEntryList.size());
                    for (int i = 0; i < rawEntryList.size(); i++) {

                        long thisMillis = 0;
                        String objectValue = rawEntryList.get(i).getYear() + "-" + rawEntryList.get(i).getMonth() + "-" + rawEntryList.get(i).getDay();
                        SimpleDateFormat x = new SimpleDateFormat("yyyy-MM-dd");
                        Date thisDate = null;
                        try {
                            thisDate = x.parse(objectValue);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        thisMillis = thisDate.getTime();
                        if (thisMillis > fromMillis && thisMillis <= toMillis) {
                            displayedEntryList.add(rawEntryList.get(i));

                        }
                        else {
                            // ... user chose CANCEL or closed the dialog
                        }

                    }

                }
        );
    }

    public void fromDatePickerActionSetter() {
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
    }

    public void setFilterPickersItemsAndVisibility() {
        filterPicker.setItems(FXCollections.observableArrayList(
                "Period", "Month")
        );

        monthPicker.setItems(FXCollections.observableArrayList(
                "All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        );


        monthPicker.setVisible(false);
        monthLabel.setVisible(false);
        fromDatePicker.setVisible(false);
        toDatePicker.setVisible(false);
    }

    public void populateTableColums() {
        description.setCellValueFactory(new PropertyValueFactory<Entry, String>("description"));
        value.setCellValueFactory(new PropertyValueFactory<Entry, Double>("value"));
        category.setCellValueFactory(new PropertyValueFactory<Entry, String>("category"));
        date.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getMonth() + "/" +cellData.getValue().getDay())));
        table.setItems(displayedEntryList);
    }

    public void removeCategoryActionSetter(Connection conn) {
        removeCategory.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Category removal");
            alert.setContentText("Are you sure you want to delete this category?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                removeCategoryDB(conn, categoryPicker, categoryList, userid);
                categoryOK.setVisible(false);

            }

        });
    }

    public void categoryOKButtonActionSetter(Connection conn) {
        categoryOK.setOnAction(e -> {
            String categoryadderinput = categoryAdder.getCharacters().toString();
            categoryList.add(categoryadderinput);
            categoryAdder.setVisible(false);
            categoryPicker.setItems(FXCollections.observableArrayList(categoryList));

            insertCategory(conn, categoryAdder, categoryList, userid);
            categoryOK.setVisible(false);

        });
    }

    public void categoryAdderSetVisibility() {
        categoryOK.setVisible(false);
        categoryAdder.setVisible(false);

        addCategory.pressedProperty().addListener((o, old, newValue) ->
                categoryAdder.setVisible(true));
        addCategory.pressedProperty().addListener((o, old, newValue) ->
                categoryOK.setVisible(true));
    }

    public void removeEntryActionSetter(Connection conn) {
        deleteEntryButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Entry removal");
            alert.setContentText("Are you sure you want to delete this entry?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){

                //removes the items selected
                removeEntry(conn, table, rawEntryList, userid);
                chartUpdater();
            }


        });
    }
    public void removeBudgetActionSetter(Connection conn) {
        budgetTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        removeBudget.setOnAction((e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure you want to delete this item?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                removeBudget(conn, budgetTable, budgetList, userid);
                }
        }));
    }



    public void addEntryActionSetter(Connection conn, Database db) {
        addEntry.setOnAction (e -> {


            insertNewEntry(conn, db, descriptionTextField, valueTextField, datePicker, categoryPicker, rawEntryList, displayedEntryList, userid);

            //CLEARING PICKERS
            descriptionTextField.clear();
            datePicker.setValue(null);
            categoryPicker.setValue(null);
            valueTextField.clear();
            chartUpdater();
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
        for (int i = 0; i < rawEntryList.size(); i++) {
            if (rawEntryList.get(i).getMonth() == 1) {
                januaryTotal += rawEntryList.get(i).getValue();
            }
            else if (rawEntryList.get(i).getMonth() == 2) {
                februaryTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 3) {
                marchTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 4) {
                aprilTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 5) {
                mayTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 6) {
                juneTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 7) {
                julyTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 8) {
                augustTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 9) {
                septemberTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 10) {
                octoberTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 11) {
                novemberTotal += rawEntryList.get(i).getValue();

            }
            else if (rawEntryList.get(i).getMonth() == 12) {
                decemberTotal += rawEntryList.get(i).getValue();

            }


        }

        //PIE CHART

        displayCategoryChartButton.setOnAction(e -> {
            categoryChartUpdater();


        });

        monthChartButton.setOnAction(e -> {
            piechart.getData().clear();

            monthlyDataChart();

        });

        monthlyDataChart();


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


    private void populateBudgetTable() {
        budgetCategory.setCellValueFactory(new PropertyValueFactory<Budget, String>("budgetCategory"));
        budgetValue.setCellValueFactory(new PropertyValueFactory<Budget, Double>("budgetValue"));
        budgetPercentage.setCellValueFactory(new PropertyValueFactory<Budget, Integer>("budgetPercentage"));
        budgetTable.setItems(budgetList);

        budgetCategoryChooser.setItems(FXCollections.observableArrayList(categoryList));
    }


    private double getFilteredListSum() {
        double sum = 0;
        for (int i = 0; i < displayedEntryList.size(); i++) {
            sum += displayedEntryList.get(i).getValue();
        }
        return sum;
    }


    private void monthlyDataChart() {
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
    }

    private void categoryChartUpdater() {
        //clearing previous data
        piechart.getData().clear();

        //populating data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i <  categoryList.size();i++) {
            double categoryTotal = 0;
            for (int j = 0; j < displayedEntryList.size(); j++) {
                if (displayedEntryList.get(j).getCategory().equals(categoryList.get(i))) {
                    categoryTotal += displayedEntryList.get(j).getValue();
                }
            }
           pieChartData.add(new PieChart.Data(categoryList.get(i) + ": " + categoryTotal, categoryTotal));
        }

        piechart.setTitle("Details per category:");
        piechart.setData(pieChartData);

    }


}