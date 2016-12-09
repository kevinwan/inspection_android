package com.gongpingjia.gpjdetector.data;

/**
 * Created by Administrator on 2016/5/12.
 */
public class City {

    public String name;

    public boolean ischeck = false;

    public String letter;

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
}
