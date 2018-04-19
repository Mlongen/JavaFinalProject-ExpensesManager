package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Budget {

    private SimpleStringProperty budgetCategory;
    private SimpleDoubleProperty budgetValue;
    private SimpleIntegerProperty percentage;


    public Budget(String budgetCategory, Double budgetValue, Integer percentage) {
        this.budgetCategory = new SimpleStringProperty(budgetCategory);
        this.budgetValue = new SimpleDoubleProperty(budgetValue);
        this.percentage = new SimpleIntegerProperty(percentage);
    }

    public String getCategory() {
        return budgetCategory.get();
    }

    public SimpleStringProperty budgetCategoryProperty() {
        return budgetCategory;
    }

    public void setCategory(String category) {
        this.budgetCategory.set(category);
    }

    public double getValue() {
        return budgetValue.get();
    }

    public SimpleDoubleProperty budgetValueProperty() {
        return budgetValue;
    }

    public void setValue(double value) {
        this.budgetValue.set(value);
    }

    public int getPercentage() {
        return percentage.get();
    }

    public SimpleIntegerProperty percentageProperty() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage.set(percentage);
    }
}
