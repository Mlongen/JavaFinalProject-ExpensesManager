package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Entry  {
    private int id;
    private SimpleStringProperty description;
    private SimpleDoubleProperty value;
    private SimpleIntegerProperty day;
    private SimpleIntegerProperty month;
    private SimpleIntegerProperty year;
    private SimpleStringProperty category;




    public Entry(int id, String description, double value, int day, int month, int year, String category) {
        this.id = id;
        this.description = new SimpleStringProperty(description);
        this.value = new SimpleDoubleProperty(value);
        this.day = new SimpleIntegerProperty(day);
        this.month = new SimpleIntegerProperty(month);
        this.year = new SimpleIntegerProperty(year);
        this.category = new SimpleStringProperty(category);
    }


    public int getId() {
        return id;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        setDescription(description);
    }

    public Double getValue() {
        return value.get();
    }

    public void setValue(double value) {
        setValue(value);
    }


    public int getDay() {
        return day.get();
    }

    public void setDay(int day) {
        setDay(day);
    }


    public int getMonth() {
        return month.get();
    }

    public void setMonth(int month) {
        setMonth(month);
    }


    public int getYear() {
        return year.get();
    }

    public void setYear(int year) {
        setYear(year);
    }


    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        setCategory(category);
    }


    @Override
    public String toString() {
        return (this.getDescription());
    }



    //methods

}
