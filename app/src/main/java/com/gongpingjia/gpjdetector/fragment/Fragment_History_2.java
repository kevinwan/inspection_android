package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import com.gongpingjia.gpjdetector.activity.ModifyPasswordActivity_;
import com.gongpingjia.gpjdetector.adapter.ItemListAdapter;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.BitmapCache;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.kZDatabase;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


@EFragment(R.layout.fragment_history_2)
public class Fragment_History_2 extends Fragment {
    Activity parentActivity;
    @ViewById
    ListView listView;
    @ViewById
    ProgressBar wait;

    localAdapter adapter;

    kZDatabase db;

    ImageLoader imageLoader;

    ArrayList<HashMap<String, String>> historyList;

    @AfterViews
    public void afterViews() {
        parentActivity = getActivity();
        db = ((HistoryActivity)getActivity()).getDatabase();
        historyList = new ArrayList<HashMap<String, String>>();
        RequestQueue mQueue = Volley.newRequestQueue(parentActivity);
        imageLoader = new ImageLoader(mQueue, new BitmapCache());



        adapter = new localAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final String[] selection = new String[]{"继续检测", "删除记录"};
                final ArrayAdapter arrayAdapter = new ArrayAdapter(parentActivity, R.layout.simple_list_item, selection);
                AlertDialog dialog = new AlertDialog.Builder(parentActivity).
                        setNegativeButton("取消", null).
                        setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (0 == i) {
                                    //继续检测
                                    Constant.setTableName(historyList.get(position).get("tableName"));
                                    parentActivity.startActivity(new Intent().setClass(parentActivity, MainActivity_.class));
                                } else if (1 == i) {
                                    //删除记录
                                    db.deleteHistory(historyList.get(position).get("tableName"));
                                    historyList.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(parentActivity, "删除成功。", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).create();
                dialog.show();
            }
        });

        loadData();
    }

    @UiThread
    void loadData() {
        Cursor c = db.getHistoryList();
        if (c.getCount() > 0) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                if (c.getString(2).equals("0")) {
                    map.put("tableName", c.getString(0));
                    map.put("date", c.getString(1));
                    map.put("isFinish", c.getString(2));

                    historyList.add(map);
                }
            } while (c.moveToNext());

            for (int i = 0; i < historyList.size(); ++i) {
                if (null == db.getValue(historyList.get(i).get("tableName"), "brandSlug")) {
                    db.deleteHistory(historyList.get(i).get("tableName"));
                    historyList.remove(i);
                    --i;
                } else {
                    historyList.get(i).put("modelName", db.getValue(historyList.get(i).get("tableName"), "modelName"));
                    historyList.get(i).put("modelDetailName", db.getValue(historyList.get(i).get("tableName"), "modelDetailName"));
                    historyList.get(i).put("modelThumbnail", db.getValue(historyList.get(i).get("tableName"), "modelThumbnail"));
                }

            }
        }
        wait.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    class localAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return historyList.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            localViewHolder viewHolder;
            if (null == view) {
                view = LayoutInflater.from(parentActivity).inflate(R.layout.history_list_item2, null);
                viewHolder = new localViewHolder((TextView) view.findViewById(R.id.title),
                        (TextView) view.findViewById(R.id.date),
                        (NetworkImageView) view.findViewById(R.id.thumbnail));
                view.setTag(viewHolder);
            } else {
                viewHolder = (localViewHolder) view.getTag();
            }

            viewHolder.title.setText(historyList.get(position).get("modelName")
                    + " " + historyList.get(position).get("modelDetailName"));
            viewHolder.date.setText("检测时间：" + historyList.get(position).get("date"));
            viewHolder.thumbnail.setImageUrl(getThumbnailUrl(historyList.get(position).get("modelThumbnail")), imageLoader);
            return view;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }
    }

    class localViewHolder {
        public TextView title;
        public TextView date;
        public NetworkImageView thumbnail;
        public localViewHolder (TextView title, TextView date, NetworkImageView thumbnail) {
            this.title = title;
            this.date = date;
            this.thumbnail = thumbnail;
        }
    }

    String getThumbnailUrl(String thumbnail) {
        if (null == thumbnail) return null;
        return "http://gongpingjia.qiniudn.com"
                + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                + thumbnail;
    }
}
