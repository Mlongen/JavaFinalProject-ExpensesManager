
        package sample;

        import com.jfoenix.controls.*;
        import com.jfoenix.validation.RequiredFieldValidator;
        import javafx.beans.binding.Bindings;
        import javafx.beans.property.ReadOnlyStringWrapper;
        import javafx.beans.property.SimpleStringProperty;
        import javafx.beans.value.ChangeListener;
        import javafx.beans.value.ObservableValue;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.fxml.Initializable;
        import javafx.scene.Node;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.chart.*;
        import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.scene.control.ChoiceBox;
        import javafx.scene.image.ImageView;
        import javafx.scene.layout.StackPane;

        import javafx.scene.paint.Color;
        import javafx.scene.text.Font;
        import javafx.stage.Stage;


        import java.io.*;
        import java.net.URL;
        import java.sql.Connection;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.time.LocalDate;
        import java.time.format.DateTimeFormatter;
        import java.util.*;

        import static javafx.scene.paint.Color.BLUE;
        import static javafx.scene.paint.Color.RED;
        import static javafx.scene.paint.Color.color;
        import static sample.Database.*;

        public class Controller implements Initializable {


            //-------------------------------------------
            //
            //
            //        ADD NEW ENTRY
            //
            //


            @FXML
            private MenuItem exitButton;
            @FXML
            private MenuItem logoutButton;


            @FXML
            private JFXTextField descriptionTextField;

            @FXML
            private JFXDatePicker datePicker;

            @FXML
            private JFXTextField valueTextField;

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
            private JFXButton categoryCancel;
            @FXML
            private JFXTextField categoryAdder;

            //-----------------------------------------
            //-----------------------------------------
            //-----------------------------------------


            @FXML
            private TableView<Entry> table;
            @FXML
            private TableColumn<Entry, String> description;

            @FXML
            private TableColumn<Entry, String> value;

            @FXML
            private TableColumn<Entry, String> date;

            @FXML
            private TableColumn<Entry, String> category;

            @FXML
            private Label sum;


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
            private JFXDatePicker fromDatePicker;

            //parameter that helps calculate fromRange of date period
            private long fromMillis = 0;

            @FXML
            private JFXDatePicker toDatePicker;

            //parameter that helps calculate toRange of date period
            private long toMillis = 0;


            //BUDGET STUFF---------------------------------------


            @FXML
            private JFXButton addBudget;

            @FXML
            private JFXButton removeBudget;

            @FXML
            private JFXComboBox budgetCategoryChooser;

            @FXML
            private TableView budgetTable;

            @FXML
            private TableColumn<Budget, String> budgetCategory;

            @FXML
            private TableColumn<Budget, Double> budgetValue;

            @FXML
            private TableColumn<Budget, Integer> budgetPercentage;

            @FXML
            private TableColumn<Budget, Integer> budgetCurrent;

            @FXML
            private JFXSlider alarmPercentageSlider;

            @FXML
            private JFXTextField budgetValueTextField;


            //-----------------------------------------------------


            private ObservableList<Entry> rawEntryList;

            private ObservableList<Entry> displayedEntryList = FXCollections.observableArrayList();

            private ObservableList<String> categoryList = FXCollections.observableArrayList();

            private ObservableList<Budget> budgetList = FXCollections.observableArrayList();



            // CHART STUFF


            @FXML
            private JFXButton displayCategoryChartButton;

            @FXML
            private ChoiceBox chartPicker;

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
            private StackedBarChart<String, Number> barChart;

            @FXML
            private JFXButton barChartButton;

            @FXML
            private JFXButton pieChartButton;

            @FXML
            private ImageView descriptionX;

            @FXML
            private ImageView dateX;

            @FXML
            private ImageView amountX;

            @FXML
            private ImageView entryCategoryX;

            @FXML
            private Label entryErrorLabel;

            @FXML
            private Label budgetErrorLabel;

            @FXML
            private ImageView alertCategoryX;

            @FXML
            private ImageView alertValueX;

            @FXML
            private ImageView tutorialImage;

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
                db.readAllEntries(conn, userid);
                populateTableColums();//populate entry table
                db.readAllBudgets(conn, userid);
                db.readCategories(conn, userid);
                db.readAllBudgets(conn, userid);

                rawEntryList = FXCollections.observableArrayList(db.getEntryObjects());
                displayedEntryList.addAll(rawEntryList);
                budgetList = FXCollections.observableArrayList(db.getBudgetObjects());
                categoryList = FXCollections.observableArrayList(db.getCategories());
                categoryPicker.setItems(FXCollections.observableArrayList(categoryList));

                //Adding logic to the Add expenses part
                addEntryActionSetter(conn, db);
                setTodaysDateOnDatePicker();

                sum.setText(String.valueOf(getFilteredListSum()));

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


                chartPicker.setItems(FXCollections.observableArrayList("Monthly", "Category"));
                chartPickerActionSetter();
                //DESIGN LOADING

                descriptionX.setVisible(false);
                dateX.setVisible(false);
                amountX.setVisible(false);
                entryCategoryX.setVisible(false);
                entryErrorLabel.setVisible(false);
                budgetErrorLabel.setVisible(false);
                alertCategoryX.setVisible(false);
                alertValueX.setVisible(false);


                exitButton.setOnAction((e -> {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Exit program.");
                    alert.setContentText("Are you sure you want to exit?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        System.exit(0);

                    }

                }));


                ArrayList<Entry> januaryExpenses = new ArrayList<>();
                ArrayList<Entry> februaryExpenses = new ArrayList<>();
                ArrayList<Entry> marchExpenses = new ArrayList<>();
                ArrayList<Entry> aprilExpenses = new ArrayList<>();
                ArrayList<Entry> mayExpenses = new ArrayList<>();
                ArrayList<Entry> juneExpenses = new ArrayList<>();
                ArrayList<Entry> julyExpenses = new ArrayList<>();
                ArrayList<Entry> augustExpenses = new ArrayList<>();
                ArrayList<Entry> septemberExpenses = new ArrayList<>();
                ArrayList<Entry> octoberExpenses = new ArrayList<>();
                ArrayList<Entry> novemberExpenses = new ArrayList<>();
                ArrayList<Entry> decemberExpenses = new ArrayList<>();

                divideExpensesPerMonth(januaryExpenses, februaryExpenses, marchExpenses, aprilExpenses, mayExpenses, juneExpenses, julyExpenses, augustExpenses, septemberExpenses, octoberExpenses, novemberExpenses, decemberExpenses);


                xAxis.setCategories(FXCollections.observableArrayList(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "December")));
                yAxis.setLabel("Value");

                barChart.setTitle("Monthly analysis");


                for (int i = 0; i < categoryList.size(); i++) {
                    XYChart.Series<String, Number> moneySpent = new XYChart.Series<>();
                    double categoryTotalJanuary = 0;
                    double categoryTotalFebruary = 0;
                    double categoryTotalMarch = 0;
                    double categoryTotalApril = 0;
                    double categoryTotalMay = 0;
                    double categoryTotalJune = 0;
                    double categoryTotalJuly = 0;
                    double categoryTotalAugust = 0;
                    double categoryTotalSeptember = 0;
                    double categoryTotalOctober = 0;
                    double categoryTotalNovember = 0;
                    double categoryTotalDecember = 0;

                    for (int j = 0; j < januaryExpenses.size(); j++) {
                        if (januaryExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalJanuary += januaryExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < februaryExpenses.size(); j++) {
                        if (februaryExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalFebruary += februaryExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < marchExpenses.size(); j++) {
                        if (marchExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalMarch += marchExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < aprilExpenses.size(); j++) {
                        if (aprilExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalApril += aprilExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < mayExpenses.size(); j++) {
                        if (mayExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalMay += mayExpenses.get(j).getValue();

                        }
                    }
                    for (int j = 0; j < juneExpenses.size(); j++) {
                        if (juneExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalJune += juneExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < julyExpenses.size(); j++) {
                        if (julyExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalJuly += julyExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < augustExpenses.size(); j++) {
                        if (augustExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalAugust += augustExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < septemberExpenses.size(); j++) {
                        if (septemberExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalSeptember += septemberExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < octoberExpenses.size(); j++) {
                        if (octoberExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalOctober += octoberExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < novemberExpenses.size(); j++) {
                        if (novemberExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalNovember += novemberExpenses.get(j).getValue();
                        }
                    }
                    for (int j = 0; j < decemberExpenses.size(); j++) {
                        if (decemberExpenses.get(j).getCategory().equals(categoryList.get(i))) {
                            categoryTotalDecember += decemberExpenses.get(j).getValue();
                        }
                    }

                    if (categoryTotalJanuary > 0) {

                        moneySpent.getData().add(new XYChart.Data<>("January", categoryTotalJanuary));
                    }
                    if (categoryTotalFebruary > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("February", categoryTotalFebruary));
                    }
                    if (categoryTotalMarch > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("March", categoryTotalMarch));
                    }
                    if (categoryTotalApril > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("April", categoryTotalApril));
                    }
                    if (categoryTotalMay > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("May", categoryTotalMay));
                    }
                    if (categoryTotalJune > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("June", categoryTotalJune));
                    }
                    if (categoryTotalJuly > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("July", categoryTotalJuly));
                    }
                    if (categoryTotalAugust > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("August", categoryTotalAugust));
                    }
                    if (categoryTotalSeptember > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("September", categoryTotalSeptember));
                    }
                    if (categoryTotalOctober > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("October", categoryTotalOctober));
                    }
                    if (categoryTotalNovember > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("November", categoryTotalNovember));
                    }
                    if (categoryTotalDecember > 0) {
                        moneySpent.getData().add(new XYChart.Data<>("December", categoryTotalDecember));
                    }
                    barChart.getData().add(moneySpent);

                    moneySpent.setName(categoryList.get(i));

                }











            }
                //START OF METHODS



            public void divideExpensesPerMonth(ArrayList<Entry> januaryExpenses, ArrayList<Entry> februaryExpenses, ArrayList<Entry> marchExpenses, ArrayList<Entry> aprilExpenses, ArrayList<Entry> mayExpenses, ArrayList<Entry> juneExpenses, ArrayList<Entry> julyExpenses, ArrayList<Entry> augustExpenses, ArrayList<Entry> septemberExpenses, ArrayList<Entry> octoberExpenses, ArrayList<Entry> novemberExpenses, ArrayList<Entry> decemberExpenses) {
                for (int i = 0; i < rawEntryList.size(); i++) {
                    if (rawEntryList.get(i).getMonth() == 1) {
                        januaryExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 2) {
                        februaryExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 3) {
                        marchExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 4) {
                        aprilExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 5) {
                        mayExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 6) {
                        juneExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 7) {
                        julyExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 8) {
                        augustExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 9) {
                        septemberExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 10) {
                        octoberExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 11) {
                        novemberExpenses.add(rawEntryList.get(i));
                    } else if (rawEntryList.get(i).getMonth() == 12) {
                        decemberExpenses.add(rawEntryList.get(i));
                    }
                }
            }


            public void addBudgetActionSetter(Connection conn) {
        addBudget.setOnAction((e -> {
            boolean categoryValidator = false;
            boolean valueValidator = false;
            if (!budgetCategoryChooser.getSelectionModel().isEmpty()) {
                categoryValidator = true;
            } else {
                alertCategoryX.setVisible(true);
                budgetCategoryChooser.setUnFocusColor(RED);
                budgetErrorLabel.setVisible(true);
            }
            if (budgetValueTextField.getText().matches("[0-9.]{1,10}")) {
                valueValidator = true;
            } else {
                alertValueX.setVisible(true);
                budgetValueTextField.setUnFocusColor(RED);
                budgetErrorLabel.setVisible(true);
            }

            if (categoryValidator && valueValidator) {
                insertBudget(conn, budgetCategoryChooser, budgetValueTextField, alarmPercentageSlider, budgetList, userid);
                budgetCategoryChooser.setUnFocusColor(Color.rgb(86, 164, 213));
                budgetValueTextField.setUnFocusColor(Color.rgb(86, 164, 213));
                alertCategoryX.setVisible(false);
                alertValueX.setVisible(false);
                budgetErrorLabel.setVisible(false);

            }


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
            if (newValue == "None") {
                displayedEntryList.remove(0, displayedEntryList.size());
                displayedEntryList.addAll(rawEntryList);
                chartPicker.setValue("Monthly");
                chartUpdater();
            }
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

    public void chartPickerActionSetter() {
        chartPicker.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            if (newValue == "Monthly") {
                piechart.getData().clear();

                monthlyDataChart();
                }
                if (newValue == "Category") {
                categoryChartUpdater();

            }}
            );

    }

    public void monthFilterListenerAndActionSetter() {
        monthPicker.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

                    if (newValue == "All") {
                        displayedEntryList.remove(0, displayedEntryList.size());
                        displayedEntryList.addAll(rawEntryList);
                        chartPicker.setValue("Monthly");
                        chartUpdater();

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
                    chartPicker.setValue("Category");
                    categoryChartUpdater();
                    sum.setText(String.valueOf(getFilteredListSum()));


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
                        chartPicker.setValue("Category");
                        categoryChartUpdater();
                        sum.setText(String.valueOf(getFilteredListSum()));

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
                "None", "Period", "Month")
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
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        value.setCellValueFactory(cellData -> Bindings.format("%.2f", cellData.getValue().getValue()));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
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
            categoryPicker.setVisible(true);
            categoryPicker.setItems(FXCollections.observableArrayList(categoryList));

            insertCategory(conn, categoryAdder, categoryList, userid);
            categoryOK.setVisible(false);
            categoryCancel.setVisible(false);
            addCategory.setVisible(true);
            removeCategory.setVisible(true);
            budgetCategoryChooser.setItems(FXCollections.observableArrayList(categoryList));

        });
    }

    public void categoryAdderSetVisibility() {
        categoryOK.setVisible(false);
        categoryAdder.setVisible(false);
        categoryCancel.setVisible(false);


        addCategory.setOnAction((e -> {
            categoryAdder.setVisible(true);
            categoryPicker.setVisible(false);
            addCategory.setVisible(false);
            removeCategory.setVisible(false);
            categoryOK.setVisible(true);
            categoryCancel.setVisible(true);
        }));

        categoryCancel.setOnAction((e -> {
            addCategory.setVisible(true);
            removeCategory.setVisible(true);
            categoryOK.setVisible(false);
            categoryAdder.setVisible(false);
            categoryPicker.setVisible(true);
            categoryCancel.setVisible(false);

        }));

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
            boolean descriptionValidator = false;
            boolean dateValidator = false;
            boolean amountValidator = false;
            boolean categoryValidator = false;
            System.out.println(descriptionTextField.getText());
            if (descriptionTextField.getText().matches("[A-Za-z0-9_.]{1,20}")) {
                descriptionValidator = true;
            } else {
                descriptionX.setVisible(true);
                entryErrorLabel.setVisible(true);
                descriptionTextField.setUnFocusColor(RED);
            }

            if (datePicker.getValue().toString().matches("[2]+[0]+[0-9]{2}+[-]+[0-9]{2}+[-]+[0-9]{2}")) {
                dateValidator = true;
            } else {
                dateX.setVisible(true);
                entryErrorLabel.setVisible(true);

            }

            if (valueTextField.getText().matches("[0-9.]{1,10}")) {
                amountValidator = true;
            } else {
                amountX.setVisible(true);
                entryErrorLabel.setVisible(true);
                valueTextField.setUnFocusColor(RED);
            }
            if (!categoryPicker.getSelectionModel().isEmpty()) {
                categoryValidator = true;
            } else {
                entryCategoryX.setVisible(true);
                entryErrorLabel.setVisible(true);
                categoryPicker.setUnFocusColor(RED);
            }

            if (descriptionValidator && dateValidator && amountValidator && categoryValidator) {
                insertNewEntry(conn, db, descriptionTextField, valueTextField, datePicker, categoryPicker, rawEntryList, displayedEntryList, userid);
                descriptionTextField.clear();
                datePicker.setValue(null);
                categoryPicker.setValue(null);
                valueTextField.clear();
                descriptionX.setVisible(false);
                dateX.setVisible(false);
                amountX.setVisible(false);
                entryCategoryX.setVisible(false);
                entryErrorLabel.setVisible(false);
                chartUpdater();
                setTodaysDateOnDatePicker();
                descriptionTextField.setUnFocusColor(Color.rgb(86, 164, 213));
                valueTextField.setUnFocusColor(Color.rgb(86, 164, 213));
                categoryPicker.setUnFocusColor(Color.rgb(86, 164, 213));
            }

            //CLEARING PICKERS

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
            sum.setText(String.valueOf(getFilteredListSum()));



        }


        monthlyDataChart();


        //BARCHART



//        moneySpent.setName("Money spent");
//        moneySpent.getData().add(new XYChart.Data<>("January", januaryTotal));
//        moneySpent.getData().add(new XYChart.Data<>("February", februaryTotal));
//        moneySpent.getData().add(new XYChart.Data<>("March", marchTotal));
//        moneySpent.getData().add(new XYChart.Data<>("April", aprilTotal));
//        moneySpent.getData().add(new XYChart.Data<>("May", mayTotal));
//        moneySpent.getData().add(new XYChart.Data<>("June", juneTotal));
//        moneySpent.getData().add(new XYChart.Data<>("July", julyTotal));
//        moneySpent.getData().add(new XYChart.Data<>("August", augustTotal));
//        moneySpent.getData().add(new XYChart.Data<>("September", septemberTotal));
//        moneySpent.getData().add(new XYChart.Data<>("October", octoberTotal));
//        moneySpent.getData().add(new XYChart.Data<>("November", novemberTotal));
//        moneySpent.getData().add(new XYChart.Data<>("December", decemberTotal));
//
//        barChart.getData().add(moneySpent);






        //LINE CHART


    }


        private void populateBudgetTable() {
        budgetCategory.setCellValueFactory(new PropertyValueFactory<Budget, String>("budgetCategory"));
        budgetValue.setCellValueFactory(new PropertyValueFactory<Budget, Double>("budgetValue"));
        budgetPercentage.setCellValueFactory(new PropertyValueFactory<Budget, Integer>("budgetPercentage"));
        budgetCurrent.setCellValueFactory(new PropertyValueFactory<Budget, Integer>("budgetCurrent"));
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