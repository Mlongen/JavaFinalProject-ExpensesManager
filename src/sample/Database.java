package sample;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private ArrayList<Entry> entryObjects;

    public Database() {
        entryObjects = new ArrayList<>();
    }

    public  ArrayList<Entry> getEntryObjects() {
        return entryObjects;
    }


    public void setEntryObjects(ArrayList<Entry> entryObjects) {
        this.entryObjects = entryObjects;
    }

    //method to read text input
    public void readAndCreateArray (String read) {
        String[] splitted = read.split(";");
        for (int i = 0; i < splitted.length;i += 6) {
            Entry a = new Entry(splitted[i], Double.valueOf(splitted[i+1]), Integer.valueOf(splitted[i+2]),
                    Integer.valueOf(splitted[i+3]), Integer.valueOf(splitted[i+4]),splitted[i+5]);
            entryObjects.add(a);
        }

    }


    //method to remove entry

    public void removeEntry(String des, double v, int d, int m, int y, String c, int len) {
        for (int i = 0; i < getEntryObjects().size(); i++) {
            if (entryObjects.get(i).getDescription().equals(des) && entryObjects.get(i).getValue() == v && entryObjects.get(i).getDay() == d &&
                    entryObjects.get(i).getMonth() == m && entryObjects.get(i).getYear() == y && entryObjects.get(i).getCategory().equals(c)) {
                entryObjects.remove(i);
            }

        }
    }


    // method to get description



    //method to get entryObjects formatted to string for txt output

    public String getObjectsAsFormattedString() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < entryObjects.size(); i++) {
            result.add(entryObjects.get(i).getDescription() + ";" + entryObjects.get(i).getValue() + ";" + entryObjects.get(i).getDay() + ";" +
                    entryObjects.get(i).getMonth() + ";" + entryObjects.get(i).getYear() + ";" + entryObjects.get(i).getCategory());
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
