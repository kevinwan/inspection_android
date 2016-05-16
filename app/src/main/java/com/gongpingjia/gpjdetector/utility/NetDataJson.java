package com.gongpingjia.gpjdetector.utility;


import android.os.AsyncTask;

import com.gongpingjia.gpjdetector.data.KCookie;
import com.gongpingjia.gpjdetector.data.UserInfo;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NetDataJson {

    public String SERVER_DOMAIN = Constant.SERVER_DOMAIN;

    public GetJsonTask mTask;

    public PostJsonTask mPostTask;

    public OnNetDataJsonListener mListener;

    private final static int REQUEST_TIMEOUT = 10000;

    private final static int SO_TIMEOUT = 10000;

    private UserInfo userInfo;

    private String getLocalCookie() {

        String cookie = userInfo.getSession();
        if (null == cookie) {
            return null;
        } else {
            return "sessionid=" + cookie;
        }
    }

    private class GetJsonTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... args) {
            DefaultHttpClient client = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            JSONObject jsonObject;

            try {

                HttpResponse response;
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
                if (args.length > 1 && args[1].equals("post")) {
                    String url = CRequest.UrlPage(args[0]);
                    Map<String, String> mapRequest = CRequest.URLRequest(args[0]);
                    HttpPost post = new HttpPost(url);

                    String session = getLocalCookie();
                    if (null != session) {
                        post.addHeader("cookie", session);
                    }

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    for (String strRequestKey : mapRequest.keySet()) {
                        String strRequestValue = mapRequest.get(strRequestKey);
                        params.add(new BasicNameValuePair(strRequestKey, strRequestValue));
                    }
                    post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    response = client.execute(post);
                } else {
                    HttpGet get = new HttpGet(args[0]);
                    String session = getLocalCookie();
                    if (null != session) {
                        get.addHeader("cookie", session);
                    }
                    response = client.execute(get);
                }
                HttpEntity entity = response.getEntity();


                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                    builder.append(s);
                }
                jsonObject = new JSONObject(builder.toString());

                List<Cookie> cookies = client.getCookieStore().getCookies();
                if (!cookies.isEmpty()) {
                    for (int i = 0; i < cookies.size(); i++) {
                        if (null != cookies.get(i).getName() && null != cookies.get(i).getValue()) {
                            if (cookies.get(i).getName().equals("sessionid")) {
                                userInfo.setSession(cookies.get(i).getValue());
                                userInfo.setExpiryDate(cookies.get(i).getExpiryDate());
                                SharedPreUtil.getInstance().putUser(userInfo);
                            }

                        }
                    }
                }


            } catch (ConnectTimeoutException e) {
                mListener.onDataJsonError("网络连接超时，请检查您的网络连接。");
                e.printStackTrace();
                return null;
            } catch (SocketTimeoutException e) {
                mListener.onDataJsonError("获取数据超时，请检查您的网络连接。");
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            String status = "failed";
            if (null == json) {
                mListener.onDataJsonError("数据异常，请检查网络连接或稍后重试。");
                return;
            }

            try {
                status = json.getString("status");
                if (!status.equals("success")) {
                    mListener.onDataJsonError(json.getString("msg"));
                } else {
                    mListener.onDataJsonUpdate(json);
                }

            } catch (JSONException e) {
                mListener.onDataJsonError("没有数据，请检查网络连接。");
                e.printStackTrace();
            }

        }
    }

    public interface OnNetDataJsonListener {
        public void onDataJsonError(String errorMessage);

        public void onDataJsonUpdate(JSONObject json);
    }

    public NetDataJson(OnNetDataJsonListener listener) {
        mListener = listener;
        SharedPreUtil.initSharedPreference(GPJApplication.getInstance().getApplicationContext());
        userInfo = SharedPreUtil.getInstance().getUser();
    }

    public void cancelTask() {
        if (null != mTask) {
            mTask.cancel(true);
        }
    }

    public void requestData(String... args) {
        mTask = new GetJsonTask();
        if (args.length > 1) {
            mTask.execute(SERVER_DOMAIN + args[0], args[1]);
        } else {
            mTask.execute(SERVER_DOMAIN + args[0]);
        }

    }

    public void post(String url, JSONObject jsonObject) {
        mPostTask = new PostJsonTask();
        mPostTask.execute(SERVER_DOMAIN + url, jsonObject.toString());
    }

    private class PostJsonTask extends AsyncTask<String, JSONObject, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... args) {
            DefaultHttpClient client = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            if (args.length < 2) return null;
            String url = args[0];
            String strJson = args[1];
            JSONObject jsonObject;
            try {
                HttpResponse response;
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);


                StringEntity stringEntity = new StringEntity(strJson, HTTP.UTF_8);
                HttpPost post = new HttpPost(url);

                String session = getLocalCookie();
                if (null != session) {
                    post.addHeader("cookie", session);
                }

                post.setEntity(stringEntity);
                response = client.execute(post);
                HttpEntity entity = response.getEntity();

                List<Cookie> cookies = client.getCookieStore().getCookies();
                if (!cookies.isEmpty()) {
                    KCookie cookie = new KCookie();
                    for (int i = 0; i < cookies.size(); i++) {
                        if (null != cookies.get(i).getName() && null != cookies.get(i).getValue()) {
                            if (cookies.get(i).getName().equals("sessionid")) {
                                userInfo.setSession(cookies.get(i).getValue());
                                userInfo.setExpiryDate(cookies.get(i).getExpiryDate());
                                SharedPreUtil.getInstance().putUser(userInfo);
                            }

                        }
                    }
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                    builder.append(s);
                }
                jsonObject = new JSONObject(builder.toString());


            } catch (ConnectTimeoutException e) {
                mListener.onDataJsonError("网络连接超时，请检查您的网络连接。");
                e.printStackTrace();
                return null;
            } catch (SocketTimeoutException e) {
                mListener.onDataJsonError("获取数据超时，请检查您的网络连接。");
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return jsonObject;
        }

        @Override
        protected void onProgressUpdate(JSONObject... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            String status = "failed";
            if (null == json) {
                mListener.onDataJsonError("数据异常，请检查网络连接或稍后重试。");
                return;
            }

            try {
                status = json.getString("status");
                if (!status.equals("success")) {
                    mListener.onDataJsonError(json.getString("msg"));
                } else {
                    mListener.onDataJsonUpdate(json);
                }

            } catch (JSONException e) {
                mListener.onDataJsonError("没有数据，请检查网络连接。");
                e.printStackTrace();
            }

        }
    }

}
