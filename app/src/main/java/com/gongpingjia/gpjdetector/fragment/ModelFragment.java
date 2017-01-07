package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.CategoryActivity;
import com.gongpingjia.gpjdetector.adapter.ModelAdapter;
import com.gongpingjia.gpjdetector.adapter.ModelDetailYearAadpter;
import com.gongpingjia.gpjdetector.data.ModelDetail;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.kZViews.LoadingDialog;
import com.gongpingjia.gpjdetector.kZViews.MyGridView;
import com.gongpingjia.gpjdetector.kZViews.PinnedHeaderListView;
import com.gongpingjia.gpjdetector.kZViews.SildingFinishLayout;
import com.gongpingjia.gpjdetector.utility.BitmapCache;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * 选择车型
 * hqh
 *
 * @author Administrator
 */
public class ModelFragment extends Fragment {

//    public CategoryData mCategoryData;

    public String mBrandName;

    public String mBrandSlug;

    private OnFragmentModelSelectionListener mListener;

    private TextView mTxtBrand;

    private NetworkImageView mImgBrand;

    public String brand_logo_url;

    public boolean isNotNeedDetail = false;

    public boolean isNeedUnlimitedModel = false;


    SildingFinishLayout mSildingFinishLayout;

    public boolean needModelDetailFragment = true;

    public boolean isFromFilter = false;

    private TextView tvAllSeries;


    private CategoryActivity categoryActivity;

    ModelAdapter adapter;

    ExpandableListView listV;

    TagFlowLayout mTagFlowLayout;

    String datas[];
    LayoutInflater mInflater;
    View mainV, closeView;
    private kZDatabase db;
    private List<Map<String, String>> mModelUnderBrand = new ArrayList<Map<String, String>>();
    private List<String> munList = new ArrayList<>();
    private List<String> newlist = new ArrayList<>();
    private Map<String, List<Map<String, String>>> mModelBrandMap = new HashMap<String, List<Map<String, String>>>();
    ImageLoader mImageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedUnlimitedModel = isNotNeedDetail;
        setRetainInstance(true);
    }

    private void initData() {
        mModelUnderBrand.clear();
        munList.clear();
        newlist.clear();
        mModelBrandMap.clear();
        if (null == mBrandSlug) return;
        db = ((CategoryActivity) getActivity()).getDatabase();
        Cursor cursor = db.getModelList(mBrandSlug);
        do {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("slug", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("parent", cursor.getString(2));
            map.put("thumbnail", cursor.getString(3));
            map.put("logo_img", cursor.getString(4));
            map.put("mum", cursor.getString(5));
            mModelUnderBrand.add(map);
            if (!munList.contains(cursor.getString(5))) {
                munList.add(cursor.getString(5));
            }
        } while (cursor.moveToNext());
        cursor.close();
        int size = munList.size();
        for (Map<String, String> map : mModelUnderBrand) {
            for (int i = 0; i < size; i++) {
                String key = map.get("mum");
                if (!newlist.contains(key)) {
                    newlist.add(key);
                    mModelBrandMap.put(key, new ArrayList<Map<String, String>>());
                }
                if (munList.get(i).equals(key)) {
                    mModelBrandMap.get(key).add(map);
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_model, container, false);
        mainV = view;

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        mInflater = inflater;
        closeView = view.findViewById(R.id.close);
        mTxtBrand = (TextView) view.findViewById(R.id.txt_logo);
        mImgBrand = (NetworkImageView) view.findViewById(R.id.img_logo);
        brand_logo_url += "?imageView2/0/w/100/h/100";
        categoryActivity = (CategoryActivity) getActivity();
        mImgBrand.setImageUrl(brand_logo_url, mImageLoader);
//        ImageLoad.LoadImage(mImgBrand, brand_logo_url, R.drawable.brandnull, R.drawable.brandnull);
//        Glide.with(this).load(brand_logo_url).placeholder(R.drawable.brandnull).into(mImgBrand);

        mTxtBrand.setText(mBrandName);
        mTagFlowLayout = (TagFlowLayout) view.findViewById(R.id.id_flowlayout);

        tvAllSeries = (TextView) view.findViewById(R.id.tv_all);
        tvAllSeries.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("brandSlug", mBrandSlug);
                intent.putExtra("brandName", mBrandName);
                intent.putExtra("modelSlug", "");
                intent.putExtra("modelName", "");
                intent.putExtra("modelThumbnail", "");
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });

        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(ModelFragment.this).commitAllowingStateLoss();
                if (categoryActivity.mModelDetailFragment.isAdded()) {
                    getFragmentManager().beginTransaction().remove(categoryActivity.mModelDetailFragment).commitAllowingStateLoss();
                }
            }
        });
        if (isFromFilter) {
            tvAllSeries.setText("#" + mBrandName + "全系列");
            if (categoryActivity.getIntent().getBooleanExtra("isShowAllSeries", true)) {
                tvAllSeries.setVisibility(View.VISIBLE);
            } else {
                tvAllSeries.setVisibility(View.GONE);
            }
//            imgTopLine.setVisibility(View.VISIBLE);
        } else {
            tvAllSeries.setVisibility(View.GONE);
        }

        adapter = new ModelAdapter(getActivity());
        // Set the adapter
        listV = (ExpandableListView) view.findViewById(R.id.list);
        listV.setGroupIndicator(null);
        initData();
        if(mModelBrandMap != null){
            adapter.setData(mModelBrandMap, newlist);
            listV.setAdapter(adapter);
        }

        if (mModelUnderBrand == null || mModelUnderBrand.size() == 0) {
            Toast.makeText(getActivity(), "没有相应车型", Toast.LENGTH_SHORT).show();
            getFragmentManager().beginTransaction().remove(ModelFragment.this).commitAllowingStateLoss();
        } else {
            datas = getMums(newlist);

            mTagFlowLayout.setAdapter(new TagAdapter<String>(datas) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                            mTagFlowLayout, false);
                    tv.setText(s);
                    return tv;
                }
            });
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                listV.expandGroup(i);
            }

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                String modelsulg = intent.getStringExtra(Constant.MODEL_SLUG_KEY);
                if (!TextUtils.isEmpty(modelsulg)) {
                    initAadpterPosition(modelsulg);
                }
            }
        }


        // Set OnItemClickListener so we can be notified on item clicks
        listV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                adapter.setCurrentSelectPosition(groupPosition, childPosition);
                HashMap<String, String> childJo = (HashMap<String, String>) adapter.getChild(groupPosition, childPosition);
                if (null != mListener) {
                    if (needModelDetailFragment && !isFromFilter) {
                        mListener.onFragmentModelSelection(childJo.get("slug"),
                                childJo.get("name"),
                                childJo.get("logo_img"));
                    } else {
                        Intent intent = new Intent();

                        intent.putExtra("brandSlug", mBrandSlug);
                        intent.putExtra("brandName", mBrandName);
                        intent.putExtra("modelSlug", childJo.get("slug"));
                        intent.putExtra("modelName", childJo.get("name"));
                        intent.putExtra("modelThumbnail", childJo.get("logo_img"));
                        intent.putExtra("price", childJo.get("max_price_bn"));
                        Log.e("", "");
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }

                }

                return false;
            }
        });
        listV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mSildingFinishLayout = (SildingFinishLayout) view.findViewById(R.id.sildingFinishLayout);
        mSildingFinishLayout.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {

            @Override
            public void onSildingFinish() {
                CategoryActivity activity = (CategoryActivity) getActivity();
                activity.setModelDetailIsShow(false);
            }

            @Override
            public void onSildingStart() {
                getActivity().getSupportFragmentManager().beginTransaction().remove(ModelFragment.this).commit();

                if (categoryActivity.mModelDetailFragment != null && categoryActivity.mModelDetailFragment.isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(categoryActivity.mModelDetailFragment).commit();
                }
            }
        });

        // touchView要设置到ListView上面
        mSildingFinishLayout.setTouchView(listV);

        mTagFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                if (selectPosSet.size() == 1) {
                    listV.setSelectedGroup(selectPosSet.iterator().next());
                } else {
                    listV.setSelectedGroup(0);
                }

            }
        });

        return view;
    }

    public void clearCheck() {
        adapter.setCurrentSelectPosition(-1, -1);
    }

    public void refresh() {
//        getData();
        initData();
        clearCheck();
//        mModelUnderBrand = mCategoryData.getModelsByBrand(mBrandSlug);
//        ImageLoad.LoadImage(mImgBrand, brand_logo_url, R.drawable.brandnull, R.drawable.brandnull);
        Glide.with(this).load(brand_logo_url).placeholder(R.drawable.brandnull).into(mImgBrand);
        mTxtBrand.setText(mBrandName);
        tvAllSeries.setText("#" + mBrandName + "全系列");
        notifyData();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentModelSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        CategoryActivity activity = (CategoryActivity) getActivity();

        activity.clearbrandCheck();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private String[] getMums(List<String> list) {
        int size = list.size();
        String datas[] = new String[size];
        for (int i = 0; i < size; i++) {
            datas[i] = list.get(i);
        }
        return datas;
    }



    private void notifyData() {
        adapter.setData(mModelBrandMap, newlist);
        if (mModelBrandMap == null || mModelBrandMap.size() == 0) {
            Toast.makeText(getActivity(), "没有相应车型", Toast.LENGTH_SHORT).show();
            getFragmentManager().beginTransaction().remove(ModelFragment.this).commitAllowingStateLoss();
            return;
        }
        datas = getMums(newlist);

        mTagFlowLayout.setAdapter(new TagAdapter<String>(datas) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                        mTagFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listV.expandGroup(i);
        }

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            String modelsulg = intent.getStringExtra(Constant.MODEL_SLUG_KEY);
            if (!TextUtils.isEmpty(modelsulg)) {
                initAadpterPosition(modelsulg);
            }
        }
}

    public void initAadpterPosition(String modelSlug) {
        for (int i = 0; i < newlist.size(); i++) {
                List<Map<String, String>> listmap = mModelBrandMap.get(newlist.get(i));
                for (int j = 0; j < listmap.size(); j++) {
                    Map<String, String> map = listmap.get(j);
                    String slug = map.get("slug");
                    if (slug.equals(modelSlug)) {
                        adapter.setCurrentSelectPosition(i, j);
                        listV.setSelectedChild(i, j, true);
                    }
                }
        }
    }


    /**
     * The default content for this Fragment has a TextView that is shown when the list is empty. If you would like to
     * change the text, call this method to supply the text it should use.
     */

    public interface OnFragmentModelSelectionListener {

        public void onFragmentModelSelection(String modelSlug, String modelName, String modelThumbnail);
    }


    /***
     * 选择车辆品牌(手动或自动),排量
     * hqh
     *
     * @author Administrator
     */
    public static class ModelDetailFragment extends Fragment implements AbsListView.OnItemClickListener{

        public String mBrandName;

        public String mBrandSlug;

        public String mModelName;

        public String mModelSlug;

        public String mModelThumbnail;

        public List<ModelDetail> mModelDetailList = new ArrayList<>();

        private OnFragmentModelDetailSelectionListener mListener;

        private ModelDetailListAdapter mAdapter;

        private PinnedHeaderListView mListView;

        //判断是否为滑动结束
        public SildingFinishLayout mSildingFinishLayout;

        OnChangeModelParent onChangeModelParent;

        // 选择排量的数据
        List<ModelDetail> volumeList;

        // 选择手自动挡的数据
        List<ModelDetail> transmissionList;

        // 当前筛选的数据
        List<ModelDetail> currentSelectList;

        RadioButton manualB, automaticB;


        RadioGroup oneG, twoG, threeG;

        int minYear = 2000;

        ModelDetailYearAadpter yearAdapter;

        MyGridView gridV;


        /**
         * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
         * changes).
         */
        public boolean isSlidfinsh = false;
        List<String> volumneNameList;

        // 当前选中的手自动挡,排量
        String currentVolumn, currentTransmission;

        LoadingDialog progressDialog;

        View view, headView, closeView;

        private kZDatabase db;

//        public List<Map<String, String>> mModelUnderBrand;


        public ModelDetailFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            CategoryActivity activity = (CategoryActivity) getActivity();
            activity.setModelDetailIsShow(true);
        }

        private void initData() {
            mModelDetailList.clear();
            db = ((CategoryActivity) getActivity()).getDatabase();
            Cursor cursor = db.getModelDetailList(mBrandSlug, mModelSlug);
            do {
                ModelDetail modeldetail = new ModelDetail();
                modeldetail.setDetail_model(cursor.getString(0));
                modeldetail.setDetail_model_slug(cursor.getString(1));
                modeldetail.setPrice_bn(cursor.getString(2));
                String year = cursor.getString(3);
                modeldetail.setYear(year);
                modeldetail.setVolume(cursor.getString(4));
                modeldetail.setTransmission(cursor.getString(5));
                modeldetail.setYear(year);
                if (Integer.parseInt(year) >= minYear) {
                    mModelDetailList.add(modeldetail);
                }
            } while (cursor.moveToNext());
            cursor.close();
            notifyData();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.fragment_model_detail, container, false);
            headView = inflater.inflate(R.layout.model_detail_header, null);
            initView(view, inflater);// 控件初始化
            initData();
            return view;
        }


        public void refresh() {
            if (mBrandSlug != null && mModelSlug != null) {
                currentVolumn = "";
                currentTransmission = "";
                initData();
            }
        }


        public void initView(View view, LayoutInflater inflater) {
            progressDialog = new LoadingDialog(getActivity(), "加载中...");
            volumeList = new ArrayList<ModelDetail>();
            minYear = Calendar.getInstance().get(Calendar.YEAR) - 15;
            transmissionList = new ArrayList<ModelDetail>();

            mSildingFinishLayout = (SildingFinishLayout) view.findViewById(R.id.sildingFinishLayout);
            //
            mSildingFinishLayout.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {

                @Override
                public void onSildingFinish() {


                }

                @Override
                public void onSildingStart() {
                    isSlidfinsh = true;
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ModelDetailFragment.this).commit();
                }
            });

            // touchView要设置到ListView上面
            mSildingFinishLayout.setTouchView(view);

            if (mModelSlug == null && mBrandSlug == null)
                this.onDestroy();

            mModelDetailList = new ArrayList<ModelDetail>();

            // Set the adapter
            mListView = (PinnedHeaderListView) view.findViewById(R.id.listview);

            mSildingFinishLayout.setTouchView(mListView);


            closeView = view.findViewById(R.id.close);
            closeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().remove(ModelDetailFragment.this).commitAllowingStateLoss();
                }
            });
            mAdapter = new ModelDetailListAdapter(inflater);
            mListView.addHeaderView(headView);
            View headerView = inflater.inflate(R.layout.listview_item_header, mListView, false);
            mListView.setPinnedHeader(headerView);
            mListView.setAdapter(mAdapter);
            mListView.setOnScrollListener(mAdapter);
            mListView.setOnItemClickListener(this);

            View headV = view.findViewById(R.id.head);
            // 这个方法里面什么都没写,但是不可以去掉,不然会出现bug,后果自负
            headV.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                }
            });


            threeG = (RadioGroup) headView.findViewById(R.id.three_group);
            oneG = (RadioGroup) headView.findViewById(R.id.one_group);
            twoG = (RadioGroup) headView.findViewById(R.id.two_group);

            manualB = (RadioButton) headView.findViewById(R.id.manual);
            automaticB = (RadioButton) headView.findViewById(R.id.automatic);

            // 点击选择手自动挡
            OnClickListener transmissionListener = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Button b = (Button) v;
                    currentTransmission = b.getText().toString().replace("挡", "");
                    List<ModelDetail> list = getDataByTransmission(currentTransmission);
                    mAdapter.setData(list);
                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "暂无相应款型", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            manualB.setOnClickListener(transmissionListener);
            automaticB.setOnClickListener(transmissionListener);

            gridV = (MyGridView) view.findViewById(R.id.gridView);
            gridV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentSelectList = null;
                    yearAdapter.setCurrentPosition(position);
                    HashMap<String, Object> map = yearAdapter.getItem(position);
                    mAdapter.setData(mModelDetailList);
                    mListView.setSelection(Integer.parseInt(map.get("position").toString()) + 1);
                    oneG.check(-1);
                    twoG.check(-1);
                    threeG.check(-1);

                }
            });
        }

        public ViewGroup getParentView() {
            return (ViewGroup) mSildingFinishLayout.getParent();
        }

        private List<HashMap<String, Object>> getYearData() {
            List<HashMap<String, Object>> yearList = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < mModelDetailList.size(); i++) {
                ModelDetail modelDetail = (ModelDetail) mModelDetailList.get(i);
                if (yearList.size() == 0) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("year", modelDetail.getYear());
                    map.put("position", 0);
                    yearList.add(map);
                } else {
                    HashMap<String, Object> map = yearList.get(yearList.size() - 1);
                    if (!modelDetail.getYear().equals(map.get("year"))) {
                        HashMap<String, Object> map1 = new HashMap<String, Object>();
                        map1.put("year", modelDetail.getYear());
                        map1.put("position", i);
                        yearList.add(map1);
                    }
                }
            }

            return yearList;

        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (OnFragmentModelDetailSelectionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
            CategoryActivity activity = (CategoryActivity) getActivity();
            activity.setModelDetailIsShow(false);

            activity.clearmodelCheck();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) return;
            if (null != mListener) {
                ModelDetail modelDetail = null;
                if (currentSelectList != null) {
                    if (currentSelectList.size() >= position)
                        modelDetail = currentSelectList.get(position - 1);
                } else {
                    if (mModelDetailList.size() >= position)
                        modelDetail = mModelDetailList.get(position - 1);
                }

                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                if (modelDetail != null) {
                    mListener.onFragmentModelDetailSelection(modelDetail.getDetail_model_slug(),
                            modelDetail.getDetail_model(),
                            modelDetail.getYear(),
                            modelDetail.getMax_reg_year(),
                            modelDetail.getMin_reg_year(), modelDetail.getPrice_bn());
                }
            }
        }

        /**
         * The default content for this Fragment has a TextView that is shown when the list is empty. If you would like to
         * change the text, call this method to supply the text it should use.
         */
        public void setEmptyText(CharSequence emptyText) {
            View emptyView = mListView.getEmptyView();

            if (emptyText instanceof TextView) {
                ((TextView) emptyView).setText(emptyText);
            }
        }


        private void notifyData() {
            initVolumneButton();
            mAdapter.setData(mModelDetailList);
            if (progressDialog != null) {
                progressDialog.dismiss();
                CategoryActivity activity = (CategoryActivity) getActivity();
                if (activity != null) {
                    activity.setModelDetailIsShow(true);
                }
            }
            yearAdapter = new ModelDetailYearAadpter(getActivity(), getYearData());
            gridV.setAdapter(yearAdapter);
            updateListView();
        }


        public void updateListView() {
            if (mModelDetailList.size() != 0) {
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "暂无相应款型", Toast.LENGTH_SHORT).show();
                mListener.onFragmentModelDetailNull();
            }
        }

        public interface OnFragmentModelDetailSelectionListener {
            public void onFragmentModelDetailSelection(String modelDetailSlug, String modelDetailName,
                                                       String modelDetailYear, String maxyear, String minyear, String priceBn);

            public void onFragmentModelDetailNull();
        }

        public class ModelDetailListAdapter extends BaseAdapter implements PinnedHeaderListView.PinnedHeaderAdapter,
                ListView.OnScrollListener {

            public LayoutInflater mInflater;

            List<ModelDetail> list;

            public ModelDetailListAdapter(LayoutInflater inflater) {
                mInflater = inflater;
            }

            public void setData(List<ModelDetail> list) {
                this.list = list;
                notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                if (list == null) {
                    return 0;
                }
                return list.size();
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
                    convertView = mInflater.inflate(R.layout.list_model_detail_item, null);
                    holder = new ModelViewHolder();
                    holder.modelText = (TextView) convertView.findViewById(R.id.modelText);
                    holder.modelYear = (TextView) convertView.findViewById(R.id.title);
                    holder.modelPrice = (TextView) convertView.findViewById(R.id.modelprice);
                    convertView.setTag(holder);
                } else {
                    holder = (ModelViewHolder) convertView.getTag();
                }

                holder.modelText.setText(list.get(position).getDetail_model());
                holder.modelPrice.setText(list.get(position).getPrice_bn() + "万元");
                String year = list.get(position).getYear();
                if (needTitle(position)) {
                    holder.modelYear.setVisibility(View.VISIBLE);
                    holder.modelYear.setText(year);
                } else {
                    holder.modelYear.setVisibility(View.GONE);
                }

                return convertView;

            }

            @Override
            public int getPinnedHeaderState(int position) {
                if (getCount() == 0 || position <= 0) {
                    return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_GONE;
                }

                if (isMove(position - 1)) {
                    return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP;
                }

                return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_VISIBLE;
            }

            @Override
            public void configurePinnedHeader(View headerView, int position, int alpaha) {
                String currLetter = list.get(position - 1).getYear();
                if (!TextUtils.isEmpty(currLetter)) {
                    TextView headerTextView = (TextView) headerView.findViewById(R.id.header);
                    headerTextView.setText(currLetter);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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

                String currentTitle = list.get(position).getYear();
                String previousTitle = list.get(position - 1).getYear();
                if (null == currentTitle || null == previousTitle) {
                    return false;
                }

                return !currentTitle.equals(previousTitle);
            }

            private boolean isMove(int position) {
                String currentTitle = list.get(position).getYear();
                if ((position + 1) >= list.size()) {
                    return false;
                }
                String nextTitle = list.get(position + 1).getYear();
                if (null == currentTitle || null == nextTitle) {
                    return false;
                }

                return !currentTitle.equals(nextTitle);
            }

            public final class ModelViewHolder {
                public TextView modelText;

                public TextView modelYear;

                public TextView modelPrice;
            }

        }

        public OnChangeModelParent getOnChangeModelParent() {
            return onChangeModelParent;
        }

        public void setOnChangeModelParent(OnChangeModelParent onChangeModelParent) {
            this.onChangeModelParent = onChangeModelParent;
        }

        public interface OnChangeModelParent {
            void onchange(ViewGroup v);
        }

        public List<ModelDetail> getDataByVolume(String volume) {
            volumeList.clear();

            if (volume.equals("其他")) {
                for (Iterator iterator = mModelDetailList.iterator(); iterator.hasNext(); ) {
                    ModelDetail modelDetail = (ModelDetail) iterator.next();
                    if (volumneNameList != null) {
                        if (!volumneNameList.contains(modelDetail.getVolume())) {
                            if (TextUtils.isEmpty(currentTransmission)) {
                                volumeList.add(modelDetail);
                            } else if (modelDetail.getTransmission().equals(currentTransmission)) {
                                volumeList.add(modelDetail);
                            }
                        }
                    }
                }
            } else {
                for (Iterator iterator = mModelDetailList.iterator(); iterator.hasNext(); ) {
                    ModelDetail modelDetail = (ModelDetail) iterator.next();
                    if (modelDetail.getVolume().equals(volume)) {
                        if (TextUtils.isEmpty(currentTransmission)) {
                            volumeList.add(modelDetail);
                        } else if (modelDetail.getTransmission().equals(currentTransmission)) {
                            volumeList.add(modelDetail);
                        }
                    }
                }
            }
            currentSelectList = volumeList;
            return volumeList;
        }

        public List<ModelDetail> getDataByTransmission(String transmission) {
            transmissionList.clear();
            for (Iterator iterator = mModelDetailList.iterator(); iterator.hasNext(); ) {
                ModelDetail modelDetail = (ModelDetail) iterator.next();
                if (transmission.contains(modelDetail.getTransmission())) {
                    if (TextUtils.isEmpty(currentVolumn)) {
                        transmissionList.add(modelDetail);
                    } else if (modelDetail.getVolume().equals(currentVolumn)) {
                        transmissionList.add(modelDetail);
                    }
                }
            }
            currentSelectList = transmissionList;
            return transmissionList;
        }

        public void initVolumneButton() {
            reset();
            // 点击选择排量
            OnClickListener volumneListener = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    twoG.clearCheck();
                    RadioButton b = (RadioButton) v;
                    currentVolumn = b.getText().toString();
                    List<ModelDetail> list = getDataByVolume(currentVolumn);
                    mAdapter.setData(list);

                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "暂无相应款型", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            // 点击选择排量(下面三个)
            OnClickListener volumnetwoListener = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    oneG.clearCheck();
                    RadioButton b = (RadioButton) v;
                    currentVolumn = b.getText().toString();
                    List<ModelDetail> list = getDataByVolume(currentVolumn);
                    mAdapter.setData(list);

                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "暂无相应款型", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            volumneNameList = new ArrayList<String>();
            for (Iterator iterator = mModelDetailList.iterator(); iterator.hasNext(); ) {
                ModelDetail modelDetail = (ModelDetail) iterator.next();
                if (!volumneNameList.contains(modelDetail.getVolume())) {
                    if (volumneNameList.size() >= 5) {
                        volumneNameList.add("其他");
                    } else {
                        volumneNameList.add(modelDetail.getVolume());
                        Collections.sort(volumneNameList, new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                return new Double((String) o1).compareTo(new Double((String) o2));
                            }
                        });
                    }
                }
            }

            for (int i = 0; i < volumneNameList.size(); i++) {

                if (i < 3) {
                    oneG.setVisibility(View.VISIBLE);
                    RadioButton rb = (RadioButton) oneG.getChildAt(i);
                    if (rb != null) {
                        rb.setText(volumneNameList.get(i));
                        rb.setVisibility(View.VISIBLE);
                        rb.setOnClickListener(volumneListener);
                    }
                } else {
                    twoG.setVisibility(View.VISIBLE);
                    RadioButton rbt = (RadioButton) twoG.getChildAt(i - 3);
                    if (rbt != null) {
                        rbt.setText(volumneNameList.get(i));
                        rbt.setVisibility(View.VISIBLE);
                        rbt.setOnClickListener(volumnetwoListener);
                    }
                }
            }

        }

        private void reset() {
            oneG.setVisibility(View.GONE);
            twoG.setVisibility(View.GONE);
            oneG.clearCheck();
            twoG.clearCheck();
            threeG.clearCheck();
            int gsize = oneG.getChildCount();
            for (int i = 0; i < gsize; i++) {
                RadioButton rb = (RadioButton) oneG.getChildAt(i);
                if (rb != null) {
                    rb.setText("");
                    rb.setVisibility(View.INVISIBLE);
                    rb.setOnClickListener(null);
                }
            }

            int tsize = twoG.getChildCount();
            for (int i = 0; i < tsize; i++) {
                RadioButton rb = (RadioButton) twoG.getChildAt(i);
                if (rb != null) {
                    rb.setText("");
                    rb.setVisibility(View.INVISIBLE);
                    rb.setOnClickListener(null);
                }
            }
        }

    }
}
