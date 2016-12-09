package com.gongpingjia.gpjdetector.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.gongpingjia.gpjdetector.util.PinyinComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import getPinYinSZM.GB2Alpha;


public class CityData implements Parcelable {

    public List<String> mProvList;

    public ArrayList<List<String>> mCityList;

    public ArrayList<Province> cityData;

    public ArrayList<Province> cityNoProvinceData;


    public String mCurrentCity = "北京";

    public void LoadCityData(JSONArray cityJson) {
        mProvList = new ArrayList<String>();
        mCityList = new ArrayList<List<String>>();

        try {

            for (int i = 1; i < cityJson.length(); i++) {
                JSONObject provItem = cityJson.getJSONObject(i);
                String provName = provItem.getString("province");
                mProvList.add(provName);

                JSONArray cities = provItem.getJSONArray("cities");
                ArrayList<String> cityUnderProv = new ArrayList<String>();

                if (cities.length() == 1) {
                    JSONObject cityName = new JSONObject(cities.get(0).toString());
                    String city = cityName.getString("name");
                    cityUnderProv.add(city);
                } else {
                    for (int j = 1; j < cities.length(); j++) {

                        JSONObject cityName = new JSONObject(cities.get(j).toString());
                        String city = cityName.getString("name");
                        cityUnderProv.add(city);
                    }

                }

                mCityList.add(cityUnderProv);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //加载带全部字样的数据
    public void LoadCityData1(JSONArray cityJson) {
        cityData = new ArrayList<Province>();
        cityNoProvinceData = new ArrayList<Province>();
        try {

            for (int i = 1; i < cityJson.length(); i++) {
                JSONObject provItem = cityJson.getJSONObject(i);
                String provName = provItem.getString("province");
                Province province = new Province();
                province.setName(provName);
                if (provName.equals("重庆")) {
                    province.setLetter("C");
                } else {
                    GB2Alpha obj1 = new GB2Alpha();
                    province.setLetter(obj1.String2Alpha(provName.substring(0, 1)).toUpperCase());
                }

                JSONArray cities = provItem.getJSONArray("cities");
                City city = new City();
                city.setName("#全省车源");
                ArrayList<City> cityUnderProv = new ArrayList<City>();
                cityUnderProv.add(city);


                int index;
                if (cities.length() > 1) {
                    index = 1;
                } else {
                    index = 0;
                }

                for (int j = index; j < cities.length(); j++) {
                    JSONObject jsonObject = new JSONObject(cities.get(j).toString());
                    String cityName = jsonObject.getString("name");
                    City city1 = new City();
                    city1.setName(cityName);
                    GB2Alpha obj2 = new GB2Alpha();
                    city1.setLetter(obj2.String2Alpha(cityName.substring(0, 1)).toUpperCase());
                    cityUnderProv.add(city1);
                }
                province.setCitylist(cityUnderProv);
                cityData.add(province);


                ArrayList<City> newList = new ArrayList<City>();
                newList.addAll(cityUnderProv);
                newList.remove(0);
                Province newProvince = new Province();
                newProvince.setName(province.getName());
                newProvince.setLetter(province.getLetter());
                newProvince.setCitylist(newList);
                cityNoProvinceData.add(newProvince);
            }

            Collections.sort(cityData, new PinyinComparator());
            Collections.sort(cityNoProvinceData, new PinyinComparator());
            Log.d("msg", "cityData" + cityData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String ReadFileString(Context context, String name) {
        InputStreamReader brandFile;
        String line;
        String rslt = "";

        try {
            brandFile = new InputStreamReader(context.getAssets().open(name));
            BufferedReader bufReader = new BufferedReader(brandFile);

            while ((line = bufReader.readLine()) != null) {
                rslt += line;
            }
            brandFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return rslt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(mProvList);
        parcel.writeList(mCityList);
        parcel.writeString(mCurrentCity);
    }

    public class CitySearchResult implements Serializable {
        public String mProvName;

        public String mCityName;

        public CitySearchResult() {
            mProvName = null;
            mCityName = null;
        }
    }

    public List<CitySearchResult> search(String keywords) {
        List<CitySearchResult> results = new ArrayList<CitySearchResult>();

        for (int i = 0; i < mCityList.size(); i++) {
            List<String> cityList = mCityList.get(i);
            for (int j = 0; j < cityList.size(); j++) {
                String city = cityList.get(j);
                if (city.contains(keywords)) {
                    CitySearchResult rlt = new CitySearchResult();
                    rlt.mCityName = city;
                    rlt.mProvName = mProvList.get(i);
                    results.add(rlt);
                }
            }
        }

        return results;
    }

    public List<CitySearchResult> autocomplete(String keywords) {

        List<CitySearchResult> results = new ArrayList<CitySearchResult>();

        int count = 0;
        int max = 10;

        for (int i = 0; i < mCityList.size(); i++) {
            List<String> cityList = mCityList.get(i);
            for (int j = 0; j < cityList.size(); j++) {
                String city = cityList.get(j);
                if (city.contains(keywords)) {
                    CitySearchResult rlt = new CitySearchResult();
                    rlt.mCityName = city;
                    rlt.mProvName = mProvList.get(i);
                    results.add(rlt);
                    if ((++count) >= max)
                        break;
                }
            }
        }

        return results;
    }

    public List<String> getmProvList() {
        return mProvList;
    }

    public void setmProvList(List<String> mProvList) {
        this.mProvList = mProvList;
    }


    public ArrayList<Province> getCityData() {
        return cityData;
    }

    public ArrayList<Province> getNoProvinceData() {
        return cityNoProvinceData;
    }

    public void setCityData(ArrayList<Province> cityData) {
        this.cityData = cityData;
    }
}
