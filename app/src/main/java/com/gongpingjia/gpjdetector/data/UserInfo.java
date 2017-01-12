package com.gongpingjia.gpjdetector.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kooze on 14-9-4.
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -5683263669918171030L;

    private String phone;
    private String user;
    private String email;
    private String company;
    private String user_type;
    private String password;
    private String session;
    private Date expiryDate;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserInfo() {}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public UserInfo(JSONObject jsonObject) {
        try {
            this.phone = jsonObject.getString("phone");
            this.user = jsonObject.getString("user");
            this.email = jsonObject.getString("email");
            this.company = jsonObject.getString("company_name");
            this.user_type = jsonObject.getString("user_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
