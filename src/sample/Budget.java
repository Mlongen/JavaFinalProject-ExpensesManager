package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Budget {

    private final int id;
    private SimpleStringProperty budgetCategory;
    private SimpleDoubleProperty budgetValue;
    private SimpleIntegerProperty budgetPercentage;
    private SimpleDoubleProperty budgetCurrent;

    public Budget(int id, String budgetCategory, Double budgetValue, Integer budgetPercentage) {
        this.id = id;
        this.budgetCategory = new SimpleStringProperty(budgetCategory);
        this.budgetValue = new SimpleDoubleProperty(budgetValue);
        this.budgetPercentage = new SimpleIntegerProperty(budgetPercentage);
        this.budgetCurrent = new SimpleDoubleProperty(0.0);

    }


    //getters

    public int getBudgetId() {
        return id;
    }

    public String getBudgetCategory() {
        return budgetCategory.get();
    }

    public double getBudgetValue() {
        return budgetValue.get();
    }


    public int getBudgetPercentage() {
        return budgetPercentage.get();
    }

    public void setBudgetCategory(String budgetCategory) {
        this.budgetCategory.set(budgetCategory);
    }

    public void setBudgetValue(double budgetValue) {
        this.budgetValue.set(budgetValue);
    }

    public void setBudgetPercentage(int budgetPercentage) {
        this.budgetPercentage.set(budgetPercentage);
    }

    public double getBudgetCurrent() {
        return budgetCurrent.get();
    }


    public void setBudgetCurrent(double budgetCurrent) {
        this.budgetCurrent.set(budgetCurrent);
    }

    public SimpleDoubleProperty budgetCurrentProperty() {
        return budgetCurrent;
    }
}
