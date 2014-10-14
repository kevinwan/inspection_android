package com.gongpingjia.gpjdetector.data;

import java.util.ArrayList;

/**
 * Created by Kooze on 14-9-3.
 */
public class partItem {

    public partItem() {}

    int part_no;
    String part_name;

    public String getId() {
        return id;
    }

    String id;
    int p_level;
    ArrayList<String> marks;

    public void setMarks(ArrayList<String> marks) {
        this.marks = marks;
    }

    public partItem(int part_no, String id, String part_name) {
        this.part_no = part_no;
        this.id = id;
        this.part_name = part_name;
        this.marks = new ArrayList<String>();

        this.p_level = -1;
    }

    public String toString() {
        String markString = "";
        for (String mark:this.marks) {
            markString += ("|" + mark);
        }
        return id + "|"
            + part_no + "|"
            + part_name
            + markString;
    }

    public partItem(String value) {
        String[] values = value.split("|");
        if (value.length() < 3) {
            return;
        }
        this.id = values[0];
        this.part_no = Integer.parseInt(values[1]);
        this.part_name = values[2];
        if (value.length() > 3) {
            for (int i = 3; i < values.length; i++) {
                this.marks.add(values[i]);
            }

        }
    }


    public int getPart_no() {
        return part_no;
    }

    public void setPart_no(int part_no) {
        this.part_no = part_no;
    }

    public String getPart_name() {
        return part_name;
    }

    public void setPart_name(String part_name) {
        this.part_name = part_name;
    }

    public int getP_level() {
        return p_level;
    }

    public void setP_level(int p_level) {
        this.p_level = p_level;
    }

    public ArrayList<String> getMarks() {
        return marks;
    }

    public void addMarks(String marks) {
        this.marks.add(marks);
    }

}