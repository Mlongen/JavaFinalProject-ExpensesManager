package sample;

import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private ArrayList<Entry> entryObjects;
    private ArrayList<Budget> budgetObjects;
    private ArrayList categories;

    public Database() {
        entryObjects = new ArrayList<>();
        budgetObjects = new ArrayList<>();
        categories = new ArrayList();

    }



    public ArrayList<Entry> getEntryObjects() {
        return entryObjects;
    }

    public ArrayList<Budget> getBudgetObjects() {
        return budgetObjects;
    }

    public ArrayList getCategories() {
        return categories;
    }




    public String getObjectsAsFormattedString() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < entryObjects.size(); i++) {
            result.add(entryObjects.get(i).getDescription() + ";" + entryObjects.get(i).getValue() + ";" + entryObjects.get(i).getDay() + ";" +
                    entryObjects.get(i).getMonth() + ";" + entryObjects.get(i).getYear() + ";" + entryObjects.get(i).getCategory());
        }
        return String.join(";", result);
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:expensesmanager.db";
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite database successful");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public void readAllEntries(Connection conn) {
        String query = "SELECT id, description, value, day, month, year, category FROM entries";
        try {
            // a. Create a statement
            Statement stmt = conn.createStatement();
            //b. Execute the query -> returns a ResultSet
            ResultSet rs = stmt.executeQuery(query); //Iterator


            while (rs.next()) {
                Entry entr = new Entry(rs.getInt("id"), rs.getString("description"), rs.getDouble("value"), rs.getInt("day"), rs.getInt("month"), rs.getInt("year"), rs.getString("category"));

                entryObjects.add(entr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void insertNewEntry(Connection conn, Database db, TextField descriptionTextField, TextField valueTextField, DatePicker datePicker, ChoiceBox categoryPicker, ObservableList<Entry> rawEntryList, ObservableList<Entry> displayedEntryList) {
        int id = (int) System.currentTimeMillis();
        String description = descriptionTextField.getText().toString();
        double value = Double.valueOf(valueTextField.getText());
        int year = Integer.valueOf(datePicker.getValue().toString().substring(0, 4));
        int month = Integer.valueOf(datePicker.getValue().toString().substring(5, 7));
        int day = Integer.valueOf(datePicker.getValue().toString().substring(8, 10));
        String category = categoryPicker.getSelectionModel().getSelectedItem().toString();

        Entry newEntry = new Entry(id, description, value, day, month, year, category);
        rawEntryList.add(newEntry);
        displayedEntryList.add(newEntry);


        //-----------------------
        String query = "INSERT INTO entries(id, description, value, day, month, year, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);  //Creates a prepared statement  based on your Query str
            pstmt.setInt(1, id);
            pstmt.setString(2, description);
            pstmt.setDouble(3, value);
            pstmt.setInt(4, day);
            pstmt.setInt(5, month);
            pstmt.setInt(6, year);
            pstmt.setString(7, category);

            pstmt.executeUpdate();

            System.out.println("Successfully inserted a new entry");

        } catch (SQLException z) {
            z.printStackTrace();
        }
    }


    public static void removeEntry(Connection conn, TableView<Entry> table, ObservableList<Entry> rawEntryList) {
        ArrayList<Entry> deletionList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
        for (int i = 0; i < deletionList.size(); i++) {
            String query = "DELETE FROM entries WHERE id = ?";
            try {
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, deletionList.get(i).getId());
                pstmt.executeUpdate();

            } catch (SQLException f) {
                f.printStackTrace();

            }

        }
        rawEntryList.removeAll(table.getSelectionModel().getSelectedItems());
        table.getItems().removeAll(table.getSelectionModel().getSelectedItems());
    }


    public static void removeBudget(Connection conn, TableView<Budget> budgetTable, ObservableList<Budget> budgetList) {
        ArrayList<Budget> deletionList = new ArrayList<>(budgetTable.getSelectionModel().getSelectedItems());
        for (int i = 0; i < deletionList.size(); i++) {
            String query = "DELETE FROM budgets WHERE id = ?";
            try {
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, deletionList.get(i).getBudgetId());
                pstmt.executeUpdate();

            } catch (SQLException f) {
                f.printStackTrace();

            }

        }
        budgetList.removeAll(budgetTable.getSelectionModel().getSelectedItems());
    }


    public int readLastID(Connection conn) {
        String query = "SELECT * FROM entries ORDER BY id DESC LIMIT 1;";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs.getInt("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void readAllBudgets(Connection conn) {
        String query = "SELECT id, category, value, percentage FROM budgets";
        try {
            // a. Create a statement
            Statement stmt = conn.createStatement();
            //b. Execute the query -> returns a ResultSet
            ResultSet rs = stmt.executeQuery(query); //Iterator


            while (rs.next()) {
                Budget bdgt = new Budget(rs.getInt("id"), rs.getString("category"), rs.getDouble("value"), rs.getInt("percentage"));

                budgetObjects.add(bdgt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void insertBudget(Connection conn, ChoiceBox budgetCategoryChooser, TextField budgetValueTextField, Slider alarmPercentageSlider, ObservableList<Budget> budgetList) {
        int id = (int) System.currentTimeMillis();
        String chosenBudgetCategory = budgetCategoryChooser.getSelectionModel().getSelectedItem().toString();
        Double chosenBudgetValue = Double.valueOf(budgetValueTextField.getText());
        Integer chosenAlarmPercentage = (int)alarmPercentageSlider.getValue();

        Budget newBudget = new Budget(id, chosenBudgetCategory, chosenBudgetValue, chosenAlarmPercentage);

        budgetList.add(newBudget);

        //ADDING TO DATABASE
        String query = "INSERT INTO budgets(id, category, value, percentage) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);  //Creates a prepared statement  based on your Query str
            pstmt.setInt(1, id);
            pstmt.setString(2, chosenBudgetCategory);
            pstmt.setDouble(3, chosenBudgetValue);
            pstmt.setInt(4, chosenAlarmPercentage);

            pstmt.executeUpdate();

            System.out.println("Successfully inserted a new budget");

        } catch (SQLException z) {
            z.printStackTrace();
        }
    }

    public static void insertCategory(Connection conn, TextField categoryAdder, ObservableList<String> categoryList) {
        int id = (int) System.currentTimeMillis();
        String category = categoryAdder.getCharacters().toString();
        categoryList.add(category);

        //ADDING TO DATABASE
        String query = "INSERT INTO categories(id, category) VALUES (?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);  //Creates a prepared statement  based on your Query str
            pstmt.setInt(1, id);
            pstmt.setString(2, category);
            pstmt.executeUpdate();

            System.out.println("Successfully inserted a new category");

        } catch (SQLException z) {
            z.printStackTrace();
        }
    }

    public static void removeCategoryDB(Connection conn, ChoiceBox categoryPicker, ObservableList<String> categoryList) {
        String selectedString = categoryPicker.getSelectionModel().getSelectedItem().toString();
        String query = "DELETE FROM categories WHERE category = ?";
            try {
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, selectedString);
                pstmt.executeUpdate();

            } catch (SQLException f) {
                f.printStackTrace();

            }


        categoryPicker.getItems().remove(selectedString);
        categoryList.remove(selectedString);
    }







    public void readCategories(Connection conn) {
        String query = "SELECT category FROM categories";
        try {
            // a. Create a statement
            Statement stmt = conn.createStatement();
            //b. Execute the query -> returns a ResultSet
            ResultSet rs = stmt.executeQuery(query); //Iterator


            while (rs.next()) {
                String str = rs.getString("category");

                categories.add(str);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }







}
