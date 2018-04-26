package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Budget {

    private SimpleStringProperty budgetCategory;
    private SimpleDoubleProperty budgetValue;
    private SimpleIntegerProperty budgetPercentage;

    public Budget(String budgetCategory, Double budgetValue, Integer budgetPercentage) {
        this.budgetCategory = new SimpleStringProperty(budgetCategory);
        this.budgetValue = new SimpleDoubleProperty(budgetValue);
        this.budgetPercentage = new SimpleIntegerProperty(budgetPercentage);
    }


    //getters
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
}
