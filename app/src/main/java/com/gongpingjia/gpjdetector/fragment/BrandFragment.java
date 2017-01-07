package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.CategoryActivity;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.kZViews.NetworkImageView;
import com.gongpingjia.gpjdetector.kZViews.PinnedHeaderListView;
import com.gongpingjia.gpjdetector.util.DhUtil;
import com.gongpingjia.gpjdetector.utility.BitmapCache;
import com.gongpingjia.gpjdetector.utility.kZDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商标查询(首字母查询)
 *
 * @author Administrator
 */
public class BrandFragment extends Fragment implements AbsListView.OnItemClickListener
//        , NetDownloadFile.OnNetDownloadFileListener
{
    public static final int RESULT_EMPTY = 3;

    public Handler mHandler;

//    public CategoryData mCategoryData;

    private RemoveWindow mRemoveWindow = new RemoveWindow();

    private WindowManager mWindowManager;

    private TextView mDialogText;

    private boolean mShowing;

    private OnFragmentBrandSelectionListener mListener;

    private HashMap<String, Integer> alphaIndexer;

    private FirstLetterListView mFirstLetterListView;

    private PinnedHeaderListView mListView;

    private BrandListAdapter mAdapter;

    public GridView hotBrandGridView;

    public HotBrandAdapter hotBrandAdapter;

    private String[] mHotBrandName = {"大众", "现代", "福特", "宝马", "别克", "本田", "奥迪", "雪佛兰", "丰田", "日产"};


    public List<Map<String, String>> mHotBrandList = new ArrayList<Map<String, String>>();

    public List<Map<String, String>> mBrandList = new ArrayList<Map<String, String>>();

    public View hotBrandView;

    private int mCurrentPosition = -1;

    public boolean isNotNeedDetail = false;

    private boolean isNeedUnlimitedBrand = false;

    OnDelClickListener onDelClickListener;

    public boolean isFromFilter = false;

    private List<String> letters;

    private kZDatabase db;

    ImageLoader mImageLoader;

    public BrandFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedUnlimitedBrand = isNotNeedDetail;
        setRetainInstance(true);
        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand, container, false);
        RelativeLayout layout = (RelativeLayout) view;
        view.setBackgroundColor(Color.WHITE);


        mListView = (PinnedHeaderListView) view.findViewById(R.id.listview);
        View headerView = inflater.inflate(R.layout.listview_item_header, mListView, false);
        if (!isFromFilter) {
            mListView.setPinnedHeader(headerView);
        }

        mAdapter = new BrandListAdapter(inflater);
        mHandler = new Handler();


        hotBrandView = LayoutInflater.from(getActivity()).inflate(R.layout.hot_brand_gridview, null);
        hotBrandGridView = (GridView) hotBrandView.findViewById(R.id.gridView);
//        hotBrandGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        if (!isFromFilter)
            mListView.addHeaderView(hotBrandView);

        db = ((CategoryActivity) getActivity()).getDatabase();
        Cursor cursor = db.getBrandList();
        HashMap<String, String> ulmtdBrandMap = new HashMap<String, String>();
        ulmtdBrandMap.put("name", "不限品牌");
        ulmtdBrandMap.put("slug", "ulimtd_slug");
        ulmtdBrandMap.put("first_letter", "热门品牌");
        ulmtdBrandMap.put("logo_img", "localimage");

        mBrandList.add(ulmtdBrandMap);
        do {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("slug", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("first_letter", cursor.getString(2));
            map.put("logo_img", cursor.getString(3));

            mBrandList.add(map);
        } while (cursor.moveToNext());
        cursor.close();

        int length = mHotBrandName.length;
        for (int i = 0; i < length; i++) {

            for (Map<String, String> item : mBrandList) {
                if (mHotBrandName[i].equals(item.get("name"))) {
                    mHotBrandList.add(item);
                }
            }
        }
        int size = mBrandList.size();
        letters = new ArrayList<String>();
        Set<String> sets = new HashSet<String>();
        for (int i = 1; i < size; i++) {
            sets.add(mBrandList.get(i).get("first_letter"));
        }
        letters.addAll(sets);
        Collections.sort(letters);
        letters.add(0, "#");

        hotBrandAdapter = new HotBrandAdapter(getActivity(), mHotBrandList);
        hotBrandGridView.setAdapter(hotBrandAdapter);
        hotBrandGridView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mCurrentPosition) {
                    return;
                }

                mCurrentPosition = position;

                if (null != mListener) {
                    mCurrentPosition = -1;
                    if (mHotBrandList.get(position).get("name").equals("不限品牌")) {
                    } else {
                        clearCheck();
                        mListener.onFragmentBrandSelection(mHotBrandList.get(position).get("slug"),
                                mHotBrandList.get(position).get("name"),
                                Constant.IMG_DOMAIN + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                                        + mHotBrandList.get(position).get("logo_img"));
                    }
                }
            }

        });
        hotBrandAdapter.notifyDataSetChanged();

        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setOnItemClickListener(this);

        mFirstLetterListView = new FirstLetterListView(getActivity());
        mFirstLetterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        LayoutParams params = new LayoutParams(DhUtil.dip2px(getActivity(), 18), LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.addView(mFirstLetterListView, params);

        alphaIndexer = new HashMap<String, Integer>();

        String prev = "*";
        String curr;
        for (int i = 0; i < mBrandList.size(); i++) {
            curr = mBrandList.get(i).get("first_letter");
            if (!curr.equals(prev)) {
                alphaIndexer.put(curr, i);
                prev = curr;
            }
        }

        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        mDialogText = (TextView) inflater.inflate(R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);

        mHandler.post(new Runnable() {

            public void run() {
                WindowManager.LayoutParams lp =
                        new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                mWindowManager.addView(mDialogText, lp);
            }
        });

        if (isFromFilter) {
            hotBrandGridView.setVisibility(View.GONE);
            hotBrandView.setVisibility(View.GONE);
            headerView.setVisibility(View.GONE);
            mListView.removeHeaderView(hotBrandView);
        }
        setSelect();
        return view;
    }

    public void setSelect() {
        String brandslug = getActivity().getIntent().getStringExtra(Constant.BRAND_SLUG_KEY);
        if (!TextUtils.isEmpty(brandslug)) {
            final int position = getBrandPosition(brandslug);
            mAdapter.setCurrentSelectPosition(position);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.setSelection(position - mListView.getHeaderViewsCount());
                }
            }, 500);

        }
    }

    public int getBrandPosition(String brandslug) {
        int position = -1;
        for (int i = 0; i < mBrandList.size(); i++) {
            if (mBrandList.get(i).get("slug").equals(brandslug)) {
                position = i;
            }
        }

        return position;
    }


    public void clearCheck() {
        mAdapter.setCurrentSelectPosition(-1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentBrandSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CategoryActivity categoryActivity = (CategoryActivity) getActivity();
        mAdapter.setCurrentSelectPosition(position - mListView.getHeaderViewsCount());
        if (null != mListener) {
            if (!isFromFilter) {
                if (mBrandList.get(position - 1).get("name").equals("不限品牌")) {
                    getActivity().setResult(RESULT_EMPTY, new Intent());
                    getActivity().finish();
                } else {

                    if (categoryActivity.mModelDetailFragment.isAdded()) {
                        getFragmentManager().beginTransaction().remove(categoryActivity.mModelDetailFragment).commitAllowingStateLoss();
                    }
                    mListener.onFragmentBrandSelection(mBrandList.get(position - 1).get("slug"),
                            mBrandList.get(position - 1).get("name"),
                            Constant.IMG_DOMAIN + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                                    + mBrandList.get(position - 1).get("logo_img"));
                }
            } else {
                if (categoryActivity.mModelDetailFragment.isAdded()) {
                    getFragmentManager().beginTransaction().remove(categoryActivity.mModelDetailFragment).commitAllowingStateLoss();
                }
                mListener.onFragmentBrandSelection(mBrandList.get(position).get("slug"),
                        mBrandList.get(position).get("name"),
                        Constant.IMG_DOMAIN + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                                + mBrandList.get(position).get("logo_img"));
            }

        }
    }

    public BrandListAdapter getAdapter() {
        return mAdapter;
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mWindowManager) {
            removeWindow();
        }
    }

    @Override
    public void onDestroy() {

        if (null != mWindowManager) {
            mWindowManager.removeViewImmediate(mDialogText);
        }
        super.onDestroy();
    }

    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
        }
    }

  /*  @Override
    public void onDownloadUpdate(String path, String filename, String type, String id) {
        int position = Integer.valueOf(id);
        if (position < 0 || position > mCategoryData.mBrandList.size()) {
            return;
        }

        String slug = mCategoryData.mBrandList.get(position).get("slug");
        FileUtils fileUtils = new FileUtils();
        Bitmap logoImg = fileUtils.readFile2Bitmap(path, filename);
        if (null != logoImg) {
            mCategoryData.mBrandLogoImg.put(slug, logoImg);
            mAdapter.notifyDataSetChanged();
        }
    }*/

    public interface OnFragmentBrandSelectionListener {
        public void onFragmentBrandSelection(String brandSlug, String brandName, String brand_logo_url);
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public class BrandListAdapter extends BaseAdapter implements PinnedHeaderListView.PinnedHeaderAdapter,
            ListView.OnScrollListener {

        LayoutInflater mInflater;

        int selectPosition = -1;



        // AlphabetIndexer mAlphaIndexer;

        public BrandListAdapter(LayoutInflater inflater) {
            mInflater = inflater;

        }

        @Override
        public int getCount() {

            if (mBrandList == null) {

                return 0;
            }

            return mBrandList.size();
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
        public int getViewTypeCount() {
            return 2;
        }

        ;

        public void setCurrentSelectPosition(int position) {
            this.selectPosition = position;
            mCurrentPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if (mBrandList.get(position).get("name").equals("不限品牌")) {
                return 0;
            } else {
                return 1;
            }
        }

        ;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BrandViewHolder brandHolder = null;
            BrandStaticViewHolder brandStaticHolder = null;
            String currLetter = mBrandList.get(position).get("first_letter");
            int letterPos = alphaIndexer.get(currLetter);

            if (convertView == null) {
                if (0 == getItemViewType(position)) {
                    convertView = mInflater.inflate(R.layout.list_brand_static_item, null);
                    brandStaticHolder = new BrandStaticViewHolder();
                    brandStaticHolder.brandStaticLogo = (ImageView) convertView.findViewById(R.id.brandLogo);
                    brandStaticHolder.brandStaticText = (TextView) convertView.findViewById(R.id.brandText);
                    brandStaticHolder.brandFirstLetter = (TextView) convertView.findViewById(R.id.title);
                    convertView.setTag(brandStaticHolder);
                } else if (1 == getItemViewType(position)) {
                    convertView = mInflater.inflate(R.layout.list_brand_item, null);
                    brandHolder = new BrandViewHolder();
                    brandHolder.brandLogo = (NetworkImageView) convertView.findViewById(R.id.brandLogo);
                    brandHolder.brandText = (TextView) convertView.findViewById(R.id.brandText);
                    brandHolder.brandFirstLetter = (TextView) convertView.findViewById(R.id.title);
                    brandHolder.currentI = (ImageView) convertView.findViewById(R.id.current);
                    brandHolder.BgV = convertView.findViewById(R.id.bg);
                    convertView.setTag(brandHolder);
                }

            } else {
                if (0 == getItemViewType(position)) {
                    brandStaticHolder = (BrandStaticViewHolder) convertView.getTag();
                } else if (1 == getItemViewType(position)) {
                    brandHolder = (BrandViewHolder) convertView.getTag();
                }

            }

            if (0 == getItemViewType(position)) {
                if (isNeedUnlimitedBrand) {
                    brandStaticHolder.brandStaticLogo.setImageResource(R.drawable.ulmtd_brand);

                    brandStaticHolder.brandStaticText.setText(mBrandList.get(position).get("name"));

                    if (letterPos == position) {
                        brandStaticHolder.brandFirstLetter.setText(currLetter);
                        brandStaticHolder.brandFirstLetter.setVisibility(View.VISIBLE);
                    } else {
                        brandStaticHolder.brandFirstLetter.setVisibility(View.GONE);
                    }
                } else {
                    brandStaticHolder.brandFirstLetter.setVisibility(View.GONE);
                    brandStaticHolder.brandStaticLogo.setVisibility(View.GONE);
                    brandStaticHolder.brandStaticText.setVisibility(View.GONE);
                }

            } else if (1 == getItemViewType(position)) {


                String filename = mBrandList.get(position).get("logo_img");

                String logo_url =
                        Constant.IMG_DOMAIN
                                + ((GPJApplication) getActivity().getApplication()).getApiUrlFromMeta("brand_model_logo_img")
                                + filename + "?imageView2/0/w/100/h/100";

                brandHolder.brandLogo.setImageUrl(logo_url, mImageLoader);
//              ImageLoad.LoadImage(brandHolder.brandLogo, logo_url, R.drawable.brandnull, R.drawable.brandnull);
                Glide.with(BrandFragment.this).load(logo_url).placeholder(R.drawable.brandnull).error(R.drawable.brandnull).into(brandHolder.brandLogo);
                brandHolder.brandText.setText(mBrandList.get(position).get("name"));

                if (letterPos == position) {
                    brandHolder.brandFirstLetter.setText(currLetter);
                    brandHolder.brandFirstLetter.setVisibility(View.VISIBLE);
                } else {
                    brandHolder.brandFirstLetter.setVisibility(View.GONE);
                }
                if (selectPosition == position) {
                    brandHolder.currentI.setVisibility(View.VISIBLE);
                    brandHolder.BgV.setBackgroundColor(getActivity().getResources().getColor(R.color.select_list_bg));
                } else {
                    brandHolder.currentI.setVisibility(View.INVISIBLE);
                    brandHolder.BgV.setBackgroundColor(getActivity().getResources().getColor(R.color.nothing));
                }
            }

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
            String currLetter = mBrandList.get(position).get("first_letter");
            if (!TextUtils.isEmpty(currLetter)) {
                TextView headerTextView = (TextView) headerView.findViewById(R.id.header);
                headerTextView.setText(currLetter);
            }

        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (view instanceof PinnedHeaderListView) {
                ((PinnedHeaderListView) view).controlPinnedHeader(firstVisibleItem);
            }

        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        private boolean needTitle(int position) {
            if (position == 0) {
                return true;
            }

            if (position < 0) {
                return false;
            }

            String currentTitle = mBrandList.get(position).get("first_letter");
            String previousTitle = mBrandList.get(position - 1).get("first_letter");
            if (null == currentTitle || null == previousTitle) {
                return false;
            }

            return !currentTitle.equals(previousTitle);
        }

        private boolean isMove(int position) {
            String currentTitle = mBrandList.get(position).get("first_letter");
            String nextTitle = mBrandList.get(position + 1).get("first_letter");
            if (null == currentTitle || null == nextTitle) {
                return false;
            }

            return !currentTitle.equals(nextTitle);
        }

        public final class BrandViewHolder {
            public NetworkImageView brandLogo;

            public TextView brandText;

            public TextView brandFirstLetter;


            public ImageView currentI;

            public View BgV;
        }

        public final class BrandStaticViewHolder {
            public ImageView brandStaticLogo;

            public TextView brandStaticText;

            public TextView brandFirstLetter;
        }

    }

    public interface OnDelClickListener {
        void onDelClick();
    }

    public class FirstLetterListView extends View {

        OnTouchingLetterChangedListener onTouchingLetterChangedListener;

        // String[] b = { "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        // "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        // "W", "X", "Y", "Z" };
//        String[] b = {"#", "A", "B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "O", "Q", "R", "S", "T", "W",
//                "X", "Y", "Z"};

        int choose = -1;

        Paint paint = new Paint();

        boolean showBkg = false;

        public FirstLetterListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public FirstLetterListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FirstLetterListView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (showBkg) {
                canvas.drawColor(Color.parseColor("#20000000"));
            }

            int height = getHeight();
            int width = getWidth();
            int singleHeight = height / letters.size();
            for (int i = 0; i < letters.size(); i++) {
                paint.setColor(Color.parseColor("#ff336699"));
                paint.setTypeface(Typeface.DEFAULT);
                paint.setAntiAlias(true);
                paint.setTextSize((getActivity().getResources().getDimension(R.dimen.text_smaller)));
                if (i == choose) {
                    // paint.setColor(Color.WHITE);
                    paint.setColor(Color.parseColor("#3d3d3d"));
                    paint.setFakeBoldText(true);
                }
                float xPos = width / 2 - paint.measureText(letters.get(i)) / 2;
                float yPos = singleHeight * i + singleHeight;
                canvas.drawText(letters.get(i), xPos, yPos, paint);
                paint.reset();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            final int action = event.getAction();
            final float y = event.getY();
            final int oldChoose = choose;
            final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
            final int c = (int) (y / getHeight() * letters.size());

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    showBkg = true;
                    if (oldChoose != c && listener != null) {
                        if (c > 0 && c < letters.size()) {
                            listener.onTouchingLetterChanged(letters.get(c));
                            choose = c;
                            invalidate();
                        }
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (oldChoose != c && listener != null) {
                        if (c > 0 && c < letters.size()) {
                            listener.onTouchingLetterChanged(letters.get(c));
                            choose = c;
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    showBkg = false;
                    choose = -1;
                    invalidate();
                    break;
            }
            return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
            this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
        }

    }

    private class LetterListViewListener implements OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);

                mListView.setSelection(position);

                if (!mShowing) {
                    mShowing = true;
                    mDialogText.setVisibility(View.VISIBLE);
                }
                mDialogText.setText(mBrandList.get(position).get("first_letter"));
                mHandler.removeCallbacks(mRemoveWindow);
                mHandler.postDelayed(mRemoveWindow, 1500);
            }
        }

    }

    public OnDelClickListener getOnDelClickListener() {
        return onDelClickListener;
    }

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    private class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }

    public class HotBrandAdapter extends BaseAdapter {

        private Context mContext;

        private LayoutInflater mInflater;

        private List<Map<String, String>> data;

        public HotBrandAdapter(Context c, List<Map<String, String>> data) {
            this.mContext = c;
            this.data = data;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {

            return data.size();
        }

        @Override
        public Object getItem(int position) {

            try {
                return data.get(position);
            } catch (Exception e) {
//                Utils.LogE("异常" + e);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String, String> map = data.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_hot_brand_item, null);
                holder = new ViewHolder();
                holder.info_txt = (TextView) convertView.findViewById(R.id.info_txt);
                holder.brandLogo = (NetworkImageView) convertView.findViewById(R.id.brandLogo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {

                String filename = map.get("logo_img");
                String logo_url =
                        Constant.IMG_DOMAIN
                                + ((GPJApplication) getActivity().getApplication()).getApiUrlFromMeta("brand_model_logo_img")
                                + filename + "?imageView2/0/w/100/h/100";
                holder.brandLogo.setImageUrl(logo_url, mImageLoader);
                holder.info_txt.setText(map.get("name"));
            } catch (Exception e) {
//                Utils.LogE("热门品牌ITEM异常：" + e.getMessage());
            }
            return convertView;
        }


    }

    private static class ViewHolder {
        public TextView info_txt;

        public NetworkImageView brandLogo;
    }

}
