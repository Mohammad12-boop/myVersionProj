package com.example.myversion_proj;

import java.util.ArrayList;

public class ScheduleModel {

    private String day;
    private ArrayList<ScheduleItems> items;

    public ScheduleModel(String day, ArrayList<ScheduleItems> items) {

        this.day = day;
        this.items = items;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<ScheduleItems> getItems() {
        return items;
    }

    public void setItems(ArrayList<ScheduleItems> items) {
        this.items = items;
    }
}
