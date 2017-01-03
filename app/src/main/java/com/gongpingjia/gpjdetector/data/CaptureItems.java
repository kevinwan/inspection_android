package com.gongpingjia.gpjdetector.data;

/**
 * Created by Kooze on 14-9-30.
 */
public class CaptureItems {
    public String id;
    public String key;
    public String desc;
    public String file_path;
    public String check_order;

    public CaptureItems(String key, String desc, String file_path) {
        this.key = key;
        this.desc = desc;
        this.file_path = file_path;
    }
    public CaptureItems(String key, String desc, String file_path,String id) {
        this.id =id;
        this.key = key;
        this.desc = desc;
        this.file_path = file_path;
    }

    public CaptureItems(String key, String desc, String file_path,String id,String check_order) {
        this.id =id;
        this.key = key;
        this.desc = desc;
        this.file_path = file_path;
        this.check_order = check_order;
    }
}
