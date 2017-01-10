package com.gongpingjia.gpjdetector.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.adapter.SearchAdapter;
import com.gongpingjia.gpjdetector.data.SearchResult;
import com.gongpingjia.gpjdetector.fragment.BrandFragment;
import com.gongpingjia.gpjdetector.fragment.ModelFragment;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 车辆品牌 hqh
 *
 * @author Administrator
 */
public class CategoryActivity extends AppCompatActivity implements
        BrandFragment.OnFragmentBrandSelectionListener,
        ModelFragment.OnFragmentModelSelectionListener, ModelFragment.ModelDetailFragment.OnFragmentModelDetailSelectionListener {

    private String mBrandSlug;

    private String maxPrice;

    private String mBrandName;

    private String mModelSlug;

    private String mModelName;

    private String mModelThumbnail;

    public BrandFragment mBrandFragment;

    public ModelFragment mModelFragment;

    public ModelFragment.ModelDetailFragment mModelDetailFragment;

//    private CategoryData mCategoryData;

    private boolean isNotNeedDetail = false;

    private boolean isNotNeedModel = false;

    private boolean isFromFilter = false;

    private final int BRAND_RESULT = 10001;


    private List<SearchResult> mResultSet;
    // 车辆详情是否已展开,即ModelDetailFragment是否已显示
    public boolean modelDetailIsShow = false;

    // 是否需要展示ModelDetailFragment
    boolean needModelDetailFragment;

    private final int REQUEST_CITY_FRAGMENT = 1;

    FrameLayout linearLayout;

    ImageView searchImageView;

    AutoCompleteTextView mAutoCompleteTextView;

    SearchAdapter mSearchAdapter;

    kZDatabase database;
    private ArrayList<Map<String, String>> mBrandList = new ArrayList<>();

    String mPageName;

    public kZDatabase getDatabase() {
        return database;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mPageName = getClass().getSimpleName();
        database = new kZDatabase(CategoryActivity.this);
        Cursor cursor = database.getBrandList();
        do {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("slug", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("first_lett", cursor.getString(2));
            map.put("logo_img", cursor.getString(3));

            mBrandList.add(map);
        } while (cursor.moveToNext());
        cursor.close();
        // mSwipeBackLayout = getSwipeBackLayout();
        //设置可以滑动的区域，推荐用屏幕像素的一半来指定
        searchImageView = (ImageView) findViewById(R.id.right_accuracy);
        searchImageView.setImageResource(R.drawable.icon_car_search1);
        searchImageView.setVisibility(View.VISIBLE);
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoCompleteTextView.setVisibility(mAutoCompleteTextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        ImageView btn_back = (ImageView) findViewById(R.id.title_back);
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.searchTxt);
        linearLayout = (FrameLayout) this.findViewById(R.id.container_model);
        ImageView title_back = (ImageView)findViewById(R.id.title_back);
        title_back.setBackgroundResource(R.drawable.back);
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CategoryActivity.this.finish();
                CategoryActivity.this.onBackPressed();
            }
        });

        TextView title_txt = (TextView) findViewById(R.id.top_title);
        title_txt.setText("品牌");

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mBrandSlug = bundle.getString("brandSlug");
            mBrandName = bundle.getString("brandName");
            mModelSlug = bundle.getString("modelSlug");
            mModelName = bundle.getString("modelName");
            maxPrice =  bundle.getString("maxPrice");
            isNotNeedDetail = bundle.getBoolean("isNotNeedDetail");
            isNotNeedModel = bundle.getBoolean("isNotNeedModel");
            isNotNeedDetail = isNotNeedModel ? true : isNotNeedDetail;
            isFromFilter = bundle.getBoolean("isFromFilter");
        }

        needModelDetailFragment = getIntent().getBooleanExtra("needModelDetailFragment", true);

//        mCategoryData = ((GPJApplication) getApplication()).getCategoryData();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mBrandFragment = new BrandFragment();
        mBrandFragment.isFromFilter = isFromFilter;
        mBrandFragment.setOnDelClickListener(new BrandFragment.OnDelClickListener() {

            @Override
            public void onDelClick() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(mModelFragment);
                transaction.commit();
            }
        });
        mModelFragment = new ModelFragment();
        mModelFragment.isFromFilter = isFromFilter;
        mModelFragment.needModelDetailFragment = this.needModelDetailFragment;
        mModelDetailFragment = new ModelFragment.ModelDetailFragment();

//        mBrandFragment.mCategoryData = mCategoryData;
//        mModelFragment.mCategoryData = mCategoryData;

        mBrandFragment.isNotNeedDetail = this.isNotNeedDetail;

        transaction.add(R.id.container_brand, mBrandFragment);
        transaction.commit();



        mResultSet = addAllModel();
        mSearchAdapter = new SearchAdapter(CategoryActivity.this, mResultSet);
        mAutoCompleteTextView.setAdapter(mSearchAdapter);
        mAutoCompleteTextView.setThreshold(0);
        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAutoCompleteTextView.setText("");
                mAutoCompleteTextView.setVisibility(View.GONE);
                SearchResult cs = mSearchAdapter.mObjects.get(position);
                Intent intent = new Intent();
                if (!isFromFilter) {
                    intent.putExtra(Constant.BRAND_SLUG_KEY, cs.mBrandSlug);
                    intent.putExtra(Constant.MODEL_SLUG_KEY, cs.mModelSlug);
                    intent.putExtra(Constant.BRAND_NAME_KEY, cs.mBrandName);
                    intent.putExtra(Constant.MODEL_NAME_KEY, cs.mModelName);
                    intent.putExtra("isFromFilter", isFromFilter);
                    setIntent(intent);
                    if (mBrandFragment.isAdded()) {
                        mBrandFragment.setSelect();
                    }
                    initHistory();
                } else {
                    intent.putExtra("brandSlug", cs.mBrandSlug);
                    intent.putExtra("brandName", cs.mBrandName);
                    intent.putExtra("modelSlug", cs.mModelSlug);
                    intent.putExtra("modelName", cs.mModelName);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });


        initHistory();

    }




    private void initHistory() {

        String brandslug = getIntent().getStringExtra(Constant.BRAND_SLUG_KEY);
        if (!TextUtils.isEmpty(brandslug)) {
            String filename = getLogoByBrand(brandslug);
            String logo_url =
                    Constant.IMG_DOMAIN
                            + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                            + filename;
            onFragmentBrandSelection(brandslug, getIntent().getStringExtra(Constant.BRAND_NAME_KEY), logo_url);
        }

        Intent i = getIntent();
        if (!i.getBooleanExtra("isFromFilter", false)) {
            String modelslug = getIntent().getStringExtra(Constant.MODEL_SLUG_KEY);
            if (!TextUtils.isEmpty(modelslug)) {
                onFragmentModelSelection(modelslug, getIntent().getStringExtra(Constant.MODEL_NAME_KEY), "");
            }
        }


    }

    public List<SearchResult> addAllModel() {

        List<Map<String, String>> mModelList = new ArrayList<Map<String, String>>();
        Cursor cursor = database.getAllModelList();
        do {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("slug", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("parent", cursor.getString(2));
            map.put("thumbnail", cursor.getString(3));
            map.put("logo_img", cursor.getString(4));

            mModelList.add(map);
        } while (cursor.moveToNext());
        cursor.close();
        List<SearchResult> results = new ArrayList<SearchResult>();
        Iterator<Map<String, String>> iter = mModelList.iterator();

        while (iter.hasNext()) {
            Map<String, String> key = iter.next();
            SearchResult rlt = new SearchResult();
            if (null != key.get("parent") && !key.get("parent").equals("null")) {
                rlt.mIsBrand = false;
                rlt.mBrandSlug = key.get("parent");
                rlt.mModelSlug = key.get("slug");
                rlt.mModelName = key.get("name");
                rlt.mModelThumbnail = key.get("thumbnail");

                Iterator<Map<String, String>> brandIter = mBrandList.iterator();
                while (brandIter.hasNext()) {
                    Map<String, String> brand = brandIter.next();
                    if (brand.get("slug").equals(rlt.mBrandSlug)) {
                        rlt.mBrandName = brand.get("name");
                        break;
                    }
                }
                results.add(rlt);
            }
        }

        return results;
    }

    public String getLogoByBrand(String brand) {
        String url = "";
        for (int i = 0; i < mBrandList.size(); i++) {
            if (mBrandList.get(i).get("slug").equals(brand)) {
                url = mBrandList.get(i).get("logo_img");
            }
        }
        return url;
    }

    public void setModelDetailIsShow(boolean isshow) {
        this.modelDetailIsShow = isshow;
    }

    public void clearbrandCheck() {
        mBrandFragment.clearCheck();
    }

    public void clearmodelCheck() {
        mModelFragment.clearCheck();
    }


    /**
     * 品牌选择
     */
    @Override
    public void onFragmentBrandSelection(String brandSlug, String brandName, String brand_logo_url) {
        if (isNotNeedModel) {
            Intent intent = new Intent();
            intent.putExtra("brand", brandSlug);
            intent.putExtra("brandName", brandName);

            setResult(BRAND_RESULT, intent);
            this.finish();
            return;
        }

        mBrandSlug = brandSlug;
        mBrandName = brandName;
        mModelFragment.needModelDetailFragment = this.needModelDetailFragment;
        mModelFragment.isFromFilter = isFromFilter;
        mModelFragment.isNotNeedDetail = this.isNotNeedDetail;
        mModelFragment.mBrandSlug = mBrandSlug;
        mModelFragment.mBrandName = mBrandName;
//        mModelFragment.mCategoryData = mCategoryData;
        mModelFragment.brand_logo_url = brand_logo_url;


        if (mModelFragment.isAdded()) {
            mModelFragment.initData1();
            mModelFragment.refresh();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_open_in_anim, R.anim.activity_close_out_anim);
            transaction.replace(R.id.container_model, mModelFragment);
            transaction.commit();
        }
        modelDetailIsShow = false;
    }



    /**
     * 型号选择
     */
    @Override
    public void onFragmentModelSelection(String modelSlug, String modelName, String modelThumbnail) {
        if (mModelDetailFragment.mSildingFinishLayout != null)
            mModelDetailFragment.isSlidfinsh = false;

        mModelSlug = modelSlug;
        mModelName = modelName;
        mModelThumbnail = modelThumbnail;

        mModelDetailFragment.mBrandName = mBrandName;
        mModelDetailFragment.mBrandSlug = mBrandSlug;
        mModelDetailFragment.mModelName = mModelName;
        mModelDetailFragment.mModelSlug = mModelSlug;
        mModelDetailFragment.mModelThumbnail = mModelThumbnail;

        if (mModelDetailFragment.isAdded()) {
            mModelDetailFragment.refresh();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_open_in_anim, R.anim.activity_close_out_anim);
            transaction.replace(R.id.container_modeldetail, mModelDetailFragment);
            transaction.commitAllowingStateLoss();
        }


    }

    /**
     * 详细型号选择
     */
    @Override
    public void onFragmentModelDetailSelection(String modelDetailSlug, String modelDetailName, String modelDetailYear, String maxyear, String minyear, String pricebn) {
        Intent intent = new Intent();

        intent.putExtra("brandSlug", mBrandSlug);
        intent.putExtra("brandName", mBrandName);
        intent.putExtra("modelSlug", mModelSlug);
        intent.putExtra("modelName", mModelName);
        intent.putExtra("modelThumbnail", mModelThumbnail);
        intent.putExtra("modelDetailSlug", modelDetailSlug);
        intent.putExtra("modelDetailName", modelDetailName);
        intent.putExtra("modelDetailYear", modelDetailYear);

        intent.putExtra("maxyear", maxyear);
        intent.putExtra("minyear", minyear);
        intent.putExtra("price", pricebn);

        setResult(RESULT_OK, intent);
        CategoryActivity.this.finish();
    }

    @Override
    public void onFragmentModelDetailNull() {
        Toast.makeText(this, "未找到对应车款信息", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

        if (mModelFragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_open_in_anim, R.anim.activity_close_out_anim);
            if (mModelDetailFragment.isAdded()) {
                transaction.remove(mModelDetailFragment).commitAllowingStateLoss();
                if (mModelDetailFragment.isSlidfinsh) {
                    transaction.remove(mModelFragment).commitAllowingStateLoss();
                    mModelDetailFragment.isSlidfinsh = false;
                }
            } else {
                transaction.remove(mModelFragment).commitAllowingStateLoss();
            }
        } else {
            finish();
        }
    }
}
