package sample;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private ArrayList<Entry> objects;

    public Database() {
        objects = new ArrayList<>();
    }

    public  ArrayList<Entry> getObjects() {
        return objects;
    }


    public ArrayList<String> getObjectsDescription() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0;i < objects.size();i++) {
            result.add(objects.get(i).getDescription());
        }
        return result;
    }

    public void setObjects(ArrayList<Entry> objects) {
        this.objects = objects;
    }

    //method to read text input
    public void readAndCreateArray (String read) {
        String[] splitted = read.split(";");
        for (int i = 0; i < splitted.length;i += 6) {
            Entry a = new Entry(splitted[i], Double.valueOf(splitted[i+1]), Integer.valueOf(splitted[i+2]),
                    Integer.valueOf(splitted[i+3]), Integer.valueOf(splitted[i+4]),splitted[i+5]);
            objects.add(a);
        }

    }

    //method to create description array
    public void createDescriptionArray (List<Entry> x) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < x.size();i++) {
           result.add(x.get(i).toString());
            i+=5;
        }
    }

    //method to remove entry

    public void removeEntry(String des, double v, int d, int m, int y, String c, int len) {
        for (int i = 0; i < getObjects().size();i++) {
            if (objects.get(i).getDescription().equals(des) && objects.get(i).getValue() == v && objects.get(i).getDay() == d &&
                    objects.get(i).getMonth() == m && objects.get(i).getYear() == y && objects.get(i).getCategory().equals(c)) {
                objects.remove(i);
            }

        }
    }


    // method to get description



    //method to get objects formatted to string for txt output

    public String getObjectsAsFormattedString() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < objects.size();i++) {
            result.add(objects.get(i).getDescription() + ";" + objects.get(i).getValue() + ";" + objects.get(i).getDay() + ";" +
                    objects.get(i).getMonth() + ";" + objects.get(i).getYear() + ";" + objects.get(i).getCategory());
        }
        return String.join(";", result);
    }

    public void writeToTxt() {
        try (PrintWriter out = new PrintWriter(new FileWriter("database.txt", false))) {
            out.println(getObjectsAsFormattedString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //method to get absolute day




}
