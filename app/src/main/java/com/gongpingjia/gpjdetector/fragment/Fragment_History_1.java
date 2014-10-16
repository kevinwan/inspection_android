package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.HistoryActivity;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.adapter.ItemListAdapter;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.BitmapCache;
import com.gongpingjia.gpjdetector.utility.QRHelper;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.kZDatabase;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


@EFragment(R.layout.fragment_history_1)
public class Fragment_History_1 extends Fragment {
    Activity parentActivity;


    PopupWindow mPopupWindow;
    RequestUtils requestUtils;

    String url;
    WebView webView;

    @ViewById
    ProgressBar wait;

    @ViewById
    ListView listView;

    historyAdapter adapter;

    ArrayList<HashMap<String, String>> list;

    String report_url;


    kZDatabase db;

    ImageLoader imageLoader;

    int page = 1;
    int has_next = 1;


    @AfterViews
    public void afterViews() {
        parentActivity = getActivity();
        requestUtils = new RequestUtils(parentActivity);
        db = ((HistoryActivity)getActivity()).getDatabase();

        RequestQueue mQueue = Volley.newRequestQueue(parentActivity);
        imageLoader = new ImageLoader(mQueue, new BitmapCache());

        final View popupView = parentActivity.getLayoutInflater().inflate(R.layout.popup_web, null);
        final ProgressBar progressBar = (ProgressBar) popupView.findViewById(R.id.progressbar);
        webView = (WebView) popupView.findViewById(R.id.webview);
        final Button close = (Button) popupView.findViewById(R.id.extra);
        final TextView web_title = (TextView) popupView.findViewById(R.id.web_title);
        final ImageView qr_img_button = (ImageView) popupView.findViewById(R.id.qr_img);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                webView.loadUrl("about:blank");
            }
        });

        mPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));

        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);


        progressBar.setMax(100);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (progressBar.getVisibility() == View.GONE)
                        progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);

            }

        });

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        list = new ArrayList<HashMap<String, String>>();


        requestUtils.getHistory(String.valueOf(page), new RequestUtils.OngetHistoryCallback() {
            @Override
            public void OnHistorySuccess(JSONObject jsonObject) {
                try {
                    report_url = jsonObject.getString("report_url");
                    has_next = jsonObject.getInt("has_next");
                    if (1 == has_next) ++page;
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); ++i) {
                        JSONObject item = data.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("accident", item.getString("accident"));
                        map.put("model", item.getString("model"));
                        map.put("d_model", item.getString("d_model"));
                        map.put("condition", item.getString("condition"));
                        map.put("time", item.getString("time"));
                        map.put("id", item.getString("id"));

                        list.add(map);
                    }
                    wait.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    wait.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void OnHistoryError(String errorMessage) {
                wait.setVisibility(View.GONE);
                Toast.makeText(parentActivity, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        adapter = new historyAdapter();
        listView.setAdapter(adapter);
        AlertDialog alertDialog;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> map = list.get(i);
                url = Constant.SERVER_DOMAIN + report_url + "?rid=" + map.get("id");
                webView.loadUrl(url);
                LinearLayout qr_view = (LinearLayout) LayoutInflater.from(parentActivity).inflate(R.layout.qr_view, null);
                ImageView qr_img = (ImageView) qr_view.findViewById(R.id.qr_img);
                qr_img.setImageBitmap(QRHelper.createQR(url, 800, 800));
                final AlertDialog alertDialog = new AlertDialog.Builder(parentActivity).setView(qr_view)
                        .setPositiveButton("完成", null).create();

                web_title.setText(map.get("model") + " " + map.get("d_model"));
                mPopupWindow.showAtLocation(parentActivity.findViewById(R.id.root_layout), Gravity.BOTTOM, 0, 0);

                qr_img_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.show();
                    }
                });
            }
        });

    }

    void loadMore(final View view) {
        view.findViewById(R.id.button_more).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.progressbar_more).setVisibility(View.VISIBLE);
        requestUtils.getHistory(String.valueOf(page), new RequestUtils.OngetHistoryCallback() {
            @Override
            public void OnHistorySuccess(JSONObject jsonObject) {
                try {
                    report_url = jsonObject.getString("report_url");
                    has_next = jsonObject.getInt("has_next");
                    if (0 != has_next) ++page;
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); ++i) {
                        JSONObject item = data.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("accident", item.getString("accident"));
                        map.put("model", item.getString("model"));
                        map.put("d_model", item.getString("d_model"));
                        map.put("condition", item.getString("condition"));
                        map.put("time", item.getString("time"));
                        map.put("id", item.getString("id"));

                        list.add(map);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view.findViewById(R.id.button_more).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressbar_more).setVisibility(View.INVISIBLE);
            }

            @Override
            public void OnHistoryError(String errorMessage) {
                wait.setVisibility(View.GONE);
                Toast.makeText(parentActivity, errorMessage, Toast.LENGTH_LONG).show();
                view.findViewById(R.id.button_more).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressbar_more).setVisibility(View.INVISIBLE);
            }
        });
    }

    class historyAdapter extends BaseAdapter {
        public historyAdapter() {

        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            ViewHoler viewHoler;
            if (null == view) {
                view = LayoutInflater.from(parentActivity).inflate(R.layout.history_list_item, null);
                viewHoler = new ViewHoler((NetworkImageView) view.findViewById(R.id.thumbnail),
                        (TextView) view.findViewById(R.id.title),
                        (TextView) view.findViewById(R.id.accident),
                        (TextView) view.findViewById(R.id.condition),
                        (TextView) view.findViewById(R.id.time),
                        (TextView) view.findViewById(R.id.score));
                view.setTag(viewHoler);
            } else {
                viewHoler = (ViewHoler) view.getTag();
            }

            HashMap<String, String> map = list.get(position);
            viewHoler.title.setText(map.get("model") + " " + map.get("d_model"));
            viewHoler.accident.setText("历史车况：" + map.get("accident"));
            viewHoler.condition.setText("车辆现况：" + map.get("condition"));
            viewHoler.score.setText(map.get("condition") + map.get("accident"));
            viewHoler.time.setText("检测时间：" + map.get("time"));

            viewHoler.thumbnail.setImageUrl(getThumbnailUrl(map.get("model")), imageLoader);

            if (position == list.size() - 1) {
                view.findViewById(R.id.more).setVisibility(View.VISIBLE);
                Button button_more = (Button) view.findViewById(R.id.button_more);
                if (has_next == 1) {
                    final View subview = view;
                    button_more.setText("点击加载更多");
                    button_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadMore(subview);
                        }
                    });
                } else {
                    button_more.setText("没有更多了");
                    button_more.setOnClickListener(null);
                }

            } else {
                view.findViewById(R.id.more).setVisibility(View.GONE);
            }

            return view;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
    class ViewHoler {

        NetworkImageView thumbnail;
        TextView title;
        TextView accident;
        TextView condition;
        TextView time;
        TextView score;

        public ViewHoler(NetworkImageView thumbnail,
                         TextView title,
                         TextView accident,
                         TextView condition,
                         TextView time,
                         TextView score) {
            this.thumbnail = thumbnail;
            this.title = title;
            this.accident = accident;
            this.condition = condition;
            this.time = time;
            this.score = score;

        }
    }

    String getThumbnailUrl(String name) {
        Cursor cursor = db.getModelThumbnail(name);
        if (null == cursor) return null;
        String thumb = cursor.getString(0);
        return "http://gongpingjia.qiniudn.com"
                + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                + thumb;
    }
}
