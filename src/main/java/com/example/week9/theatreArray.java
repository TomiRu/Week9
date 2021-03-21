package com.example.week9;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class theatreArray {
    ArrayList<Theatre> theatre_list;
    int length;


    public theatreArray() {
        theatre_list = new ArrayList<Theatre>();
    }

    public void addList(String name, String Id) {
        theatre_list.add(new Theatre(name, Id));
        length++;
    }

    public void print() {
        for (int i = 0; i < length; i++) {
            System.out.println(theatre_list.get(i).getName());
        }
    }

    public ArrayList getListNames() {
        ArrayList theatres_name = new ArrayList();
        for (int i = 0; i < length; i++) {
            String a = theatre_list.get(i).getName();
            theatres_name.add(a);

        }
        return theatres_name;
    }

    public String getId(String choice) {
        String Id = null;
        for (int i = 0; i < length; i++) {
            if (choice.contains(theatre_list.get(i).getName())) {
                Id = theatre_list.get(i).Id;
            }

        }
        return Id;
    }

}
