package com.gongpingjia.gpjdetector.data;

/**
 * Created by Kooze on 14-8-27.
 */
public class kZDBItem {
    private String key;
    private String name;
    private String value;
    private String priority;
    private String option;

    public void setItem(String key, String name, String value, String priority, String option) {
        this.key = key;
        this.name = name;
        this.value = value;
        this.priority = priority;
        this.option = option;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public kZDBItem() {}

    public kZDBItem(String key, String name, String value, String priority, String option) {
        this.key = key;
        this.name = name;
        this.value = value;
        this.priority = priority;
        this.option = option;
    }


}
