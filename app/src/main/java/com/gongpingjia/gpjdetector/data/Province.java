package com.gongpingjia.gpjdetector.data;

import java.util.List;

/**
 * Created by Administrator on 2016/5/25.
 */
public class Province {

    public String letter;

    public String name;

    public List<City> citylist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCitylist() {
        return citylist;
    }

    public void setCitylist(List<City> citylist) {
        this.citylist = citylist;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
}
