package com.gongpingjia.gpjdetector.util;


import com.gongpingjia.gpjdetector.data.Province;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Province> {

    public int compare(Province o1, Province o2) {
        if (o1.getLetter().equals("@")
                || o2.getLetter().equals("#")) {
            return -1;
        } else if (o1.getLetter().equals("#")
                || o2.getLetter().equals("@")) {
            return 1;
        } else {
            return o1.getLetter().compareTo(o2.getLetter());
        }
    }

}
