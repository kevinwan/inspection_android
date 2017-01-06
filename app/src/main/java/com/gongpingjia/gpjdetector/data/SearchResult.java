package com.gongpingjia.gpjdetector.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/1/5.
 */
public class SearchResult {

    public String mBrandName;

    public String mBrandSlug;

    public String mModelName;

    public String mModelSlug;

    public int nums;

    public String mModelThumbnail;

    public boolean mIsBrand;

    public SearchResult() {
        mBrandName = null;
        mBrandSlug = null;
        mModelName = null;
        mModelSlug = null;
        mModelThumbnail = null;
        mIsBrand = false;
    }

    public boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
