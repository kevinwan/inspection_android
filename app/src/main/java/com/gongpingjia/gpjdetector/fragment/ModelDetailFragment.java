package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.CategoryActivity;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.kZViews.PinnedHeaderListView;
import com.gongpingjia.gpjdetector.utility.NetDataJson;
import com.gongpingjia.gpjdetector.utility.kZDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModelDetailFragment extends Fragment implements
        AbsListView.OnItemClickListener, NetDataJson.OnNetDataJsonListener {

    public String mBrandName;
    public String mBrandSlug;
    public String mModelName;
    public String mModelSlug;
    public String mModelThumbnail;

    public List<Map<String, String>> mModelDetailList;
    private OnFragmentModelDetailSelectionListener mListener;
    private NetDataJson mNetModelDetail;
    private ModelDetailListAdapter mAdapter;
    private PinnedHeaderListView mListView;
    private TextView mTxtLoading;

    ArrayList modelDetailList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ModelDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_model_detail, container, false);

        if (mModelSlug == null && mBrandSlug == null) this.onDestroy();

        mModelDetailList = new ArrayList<Map<String, String>>();

        // Set the adapter
        mListView = (PinnedHeaderListView) view.findViewById(R.id.listview);

        View headerView = inflater.inflate(R.layout.listview_item_header, mListView, false);
        mListView.setPinnedHeader(headerView);

        mAdapter = new ModelDetailListAdapter(inflater);

        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setOnItemClickListener(this);

        mTxtLoading = (TextView) view.findViewById(android.R.id.empty);
        mTxtLoading.setText("正在加载...");
        mTxtLoading.setVisibility(View.VISIBLE);

        if (mBrandSlug != null && mModelSlug != null) {
            String url = GPJApplication.getInstance().getApiUrlFromMeta("detail_model");
            String params = "?brand=" + mBrandSlug + "&model=" + mModelSlug;

            getData(mModelSlug);

		/*	mNetModelDetail = new NetDataJson(this);
			mNetModelDetail.requestData(url + params);*/
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentModelDetailSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentModelDetailSelection(
                    mModelDetailList.get(position).get("slug"),
                    mModelDetailList.get(position).get("name"), mModelDetailList.get(position).get("year"));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onDataJsonUpdate(JSONObject json) {
        if (null == json) {
            return;
        }

        try {
            JSONArray detail = json.getJSONArray("detail_model");
            if (null == detail) {
                return;
            }

            for (int i = 0; i < detail.length(); i++) {
                JSONObject modelByYear = detail.getJSONObject(i);
                if (null != modelByYear) {
                    int year = modelByYear.getInt("year");
                    Iterator iter = modelByYear.keys();
                    while (iter.hasNext()) {
                        String slug = iter.next().toString();
                        if (!slug.equals("year")) {
                            String name = modelByYear.getString(slug);
                            Map<String, String> map = new HashMap<String, String>();

                            map.put("name", name);
                            map.put("slug", slug);
                            map.put("year", String.valueOf(year));

                            if (null != map.get("slug")
                                    && !map.get("slug").equals("null")) {
                                mModelDetailList.add(map);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        updateListView();

    }

    @Override
    public void onDataJsonError(String errorMessage) {
        //        Toast.makeText(getActivity(), "未找到相应数据", Toast.LENGTH_SHORT).show();
        //        getActivity().onBackPressed();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void updateListView() {
        if (mModelDetailList.size() != 0) {
            mTxtLoading.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        } else {
            mTxtLoading.setText("未找到对应车款信息");

            mListener.onFragmentModelDetailNull();
        }
    }

    private void getData(String model) {
        kZDatabase db = ((CategoryActivity) getActivity()).getDatabase();
        Cursor cursor = db.getModelDetail(new String[]{model});
        if (cursor != null & cursor.getCount() > 0) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("year", cursor.getString(0));
                map.put("slug", cursor.getString(1));
                map.put("url", cursor.getString(2));
                map.put("name", cursor.getString(3));
                map.put("price", cursor.getString(4));

                if (null != map.get("slug")
                        && !map.get("slug").equals("null")) {
                    mModelDetailList.add(map);
                }
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(getActivity(), "没有此车型!", Toast.LENGTH_SHORT).show();
        }
        cursor.close();

        updateListView();
    }

    public interface OnFragmentModelDetailSelectionListener {
        public void onFragmentModelDetailSelection(String modelDetailSlug,
                                                   String modelDetailName,
                                                   String modelDetailYear);

        public void onFragmentModelDetailNull();
    }

    public class ModelDetailListAdapter extends BaseAdapter implements PinnedHeaderListView.PinnedHeaderAdapter, ListView.OnScrollListener {

        public LayoutInflater mInflater;

        public ModelDetailListAdapter(LayoutInflater inflater) {
            mInflater = inflater;
        }

        @Override
        public int getCount() {
            return mModelDetailList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ModelViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.list_model_detail_item, null);
                holder = new ModelViewHolder();
                holder.modelText = (TextView) convertView
                        .findViewById(R.id.modelText);
                holder.modelYear = (TextView) convertView
                        .findViewById(R.id.title);
                holder.price = (TextView) convertView
                        .findViewById(R.id.price);
                convertView.setTag(holder);
            } else {
                holder = (ModelViewHolder) convertView.getTag();
            }

            holder.modelText
                    .setText(mModelDetailList.get(position).get("name"));

            String year = mModelDetailList.get(position).get("year");
            if (needTitle(position)) {
                holder.modelYear.setVisibility(View.VISIBLE);
                holder.modelYear.setText(year);
            } else {
                holder.modelYear.setVisibility(View.GONE);
            }
            holder.price.setText(mModelDetailList.get(position).get("price")+"万元");
            return convertView;

        }

        @Override
        public int getPinnedHeaderState(int position) {
            if (getCount() == 0 || position < 0) {
                return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_GONE;
            }

            if (isMove(position)) {
                return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP;
            }

            return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_VISIBLE;
        }

        @Override
        public void configurePinnedHeader(View headerView, int position, int alpaha) {
            String currLetter = mModelDetailList.get(position).get(
                    "year");
            if (!TextUtils.isEmpty(currLetter)) {
                TextView headerTextView = (TextView) headerView.findViewById(R.id.header);
                headerTextView.setText(currLetter);
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (view instanceof PinnedHeaderListView) {
                ((PinnedHeaderListView) view).controlPinnedHeader(firstVisibleItem);
            }

        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        // ===========================================================
        // Methods
        // ===========================================================

        private boolean needTitle(int position) {
            if (position == 0) {
                return true;
            }

            if (position < 0) {
                return false;
            }

            String currentTitle = mModelDetailList.get(position).get("year");
            String previousTitle = mModelDetailList.get(position - 1).get("year");
            if (null == currentTitle || null == previousTitle) {
                return false;
            }

            return !currentTitle.equals(previousTitle);
        }

        private boolean isMove(int position) {
            String currentTitle = mModelDetailList.get(position).get("year");
            if ((position + 1) >= mModelDetailList.size()) {
                return false;
            }
            String nextTitle = mModelDetailList.get(position + 1).get("year");
            if (null == currentTitle || null == nextTitle) {
                return false;
            }

            return !currentTitle.equals(nextTitle);
        }

        public final class ModelViewHolder {
            public TextView modelText;
            public TextView modelYear;
            public TextView price;
        }

    }

}
