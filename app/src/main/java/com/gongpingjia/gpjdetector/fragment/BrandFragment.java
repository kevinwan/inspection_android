package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.CategoryActivity;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.kZViews.PinnedHeaderListView;
import com.gongpingjia.gpjdetector.global.GPJApplication;
import com.gongpingjia.gpjdetector.utility.BitmapCache;
import com.gongpingjia.gpjdetector.utility.kZDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class BrandFragment extends Fragment implements
        AbsListView.OnItemClickListener {

    public Handler mHandler;
    private RemoveWindow mRemoveWindow = new RemoveWindow();
    private WindowManager mWindowManager;
    private TextView mDialogText;
    private boolean mShowing;
    private OnFragmentBrandSelectionListener mListener;
    private HashMap<String, Integer> alphaIndexer;
    private FirstLetterListView mFirstLetterListView;
    private PinnedHeaderListView mListView;
    private BrandListAdapter mAdapter;
    kZDatabase db;
    private int mCurrentPosition = -1;


    private ArrayList<HashMap<String, String>> brandList;
 
    

    public BrandFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        brandList = new ArrayList<HashMap<String, String>>();
        db = ((CategoryActivity) getActivity()).getDatabase();
        Cursor cursor = db.getBrandList();
        do {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("slug", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("first_letter", cursor.getString(2));
            map.put("logo_img", cursor.getString(3));

            brandList.add(map);
        } while (cursor.moveToNext());
        cursor.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand_list, container, false);
        RelativeLayout layout = (RelativeLayout) view;

        mListView = (PinnedHeaderListView) view.findViewById(R.id.listview);
        View headerView = inflater.inflate(R.layout.listview_item_header, mListView, false);
        mListView.setPinnedHeader(headerView);
        mAdapter = new BrandListAdapter(inflater);
        mHandler = new Handler();

        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setOnItemClickListener(this);

        mFirstLetterListView = new FirstLetterListView(getActivity());
        mFirstLetterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        LayoutParams params = new LayoutParams(50, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.addView(mFirstLetterListView, params);

        alphaIndexer = new HashMap<String, Integer>();

        String prev = "*";
        String curr;
        for (int i = 0; i < brandList.size(); i++) {
            curr = brandList.get(i).get("first_letter");
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
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                mWindowManager.addView(mDialogText, lp);
            }
        });

        return view;
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
    	if (position == mCurrentPosition) {
    		return;
    	}
    	
    	mCurrentPosition = position;
    	
        if (null != mListener) {
            mListener.onFragmentBrandSelection(brandList.get(position).get("slug"),
                    brandList.get(position).get("name"),
                    "http://gongpingjia.qiniudn.com"
                            + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img")
                            + brandList.get(position).get("logo_img"));
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
        super.onDestroy();
        if (null != mWindowManager) {
            mWindowManager.removeView(mDialogText);
        }
    }

    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
        }
    }


    public interface OnFragmentBrandSelectionListener {

        public void onFragmentBrandSelection(String brandSlug, String brandName, String brand_logo_url);

    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public class BrandListAdapter extends BaseAdapter implements PinnedHeaderListView.PinnedHeaderAdapter, ListView.OnScrollListener {

        LayoutInflater mInflater;
        
        ImageLoader mImageLoader;

        public BrandListAdapter(LayoutInflater inflater) {
            mInflater = inflater;
            
            RequestQueue mQueue = Volley.newRequestQueue(getActivity());    
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {

            return brandList.size();
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
        };
        
        @Override
        public int getItemViewType(int position) {
        	return 0;
        };

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BrandViewHolder brandHolder = null;
            String currLetter = brandList.get(position).get("first_letter");
            int letterPos = alphaIndexer.get(currLetter);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_brand_item, null);
                brandHolder = new BrandViewHolder();
                brandHolder.brandLogo = (NetworkImageView) convertView.findViewById(R.id.brandLogo);
                brandHolder.brandText = (TextView) convertView.findViewById(R.id.brandText);
                brandHolder.brandFirstLetter = (TextView) convertView.findViewById(R.id.title);

                convertView.setTag(brandHolder);
                
            } else {
                brandHolder = (BrandViewHolder) convertView.getTag();
                
            }

            String filename = brandList.get(position).get("logo_img");
            String logo_url = "http://gongpingjia.qiniudn.com" + GPJApplication.getInstance().getApiUrlFromMeta("brand_model_logo_img") + filename + "?imageView2/0/w/150/h/150";
            brandHolder.brandLogo.setImageUrl(logo_url, mImageLoader);

            brandHolder.brandText.setText(brandList.get(position).get("name"));

            if (letterPos == position) {
                brandHolder.brandFirstLetter.setText(currLetter);
                brandHolder.brandFirstLetter.setVisibility(View.VISIBLE);
            } else {
                brandHolder.brandFirstLetter.setVisibility(View.GONE);
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
            String currLetter = brandList.get(position).get("first_letter");
            if (!TextUtils.isEmpty(currLetter)) {
                TextView headerTextView = (TextView) headerView.findViewById(R.id.header);
                headerTextView.setText(currLetter);
            }

        }

        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

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

            String currentTitle = brandList.get(position).get("first_letter");
            String previousTitle = brandList.get(position - 1).get("first_letter");
            if (null == currentTitle || null == previousTitle) {
                return false;
            }

            return !currentTitle.equals(previousTitle);
        }

        private boolean isMove(int position) {
            String currentTitle = brandList.get(position).get("first_letter");
            String nextTitle = brandList.get(position + 1).get("first_letter");
            if (null == currentTitle || null == nextTitle) {
                return false;
            }

            return !currentTitle.equals(nextTitle);
        }

        public final class BrandViewHolder {
            public NetworkImageView brandLogo;
            public TextView brandText;
            public TextView brandFirstLetter;
        }
        



    }

    public class FirstLetterListView extends View {

        OnTouchingLetterChangedListener onTouchingLetterChangedListener;
        String[] b = {"#", "A", "B", "C", "D", "F", "G", "H", "J", "K", "L",
                "M", "N", "O", "Q", "R", "S", "T", "W", "X", "Y", "Z"};
        int choose = -1;
        Paint paint = new Paint();
        boolean showBkg = false;

        public FirstLetterListView(Context context, AttributeSet attrs,
                                   int defStyle) {
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
            int singleHeight = height / b.length;
            for (int i = 0; i < b.length; i++) {
                paint.setColor(Color.parseColor("#ff336699"));
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setAntiAlias(true);
                paint.setTextSize((float) 18.0);
                if (i == choose) {
                    paint.setColor(Color.WHITE);
                    paint.setFakeBoldText(true);
                }
                float xPos = width / 2 - paint.measureText(b[i]) / 2;
                float yPos = singleHeight * i + singleHeight;
                canvas.drawText(b[i], xPos, yPos, paint);
                paint.reset();
            }

        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            final int action = event.getAction();
            final float y = event.getY();
            final int oldChoose = choose;
            final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
            final int c = (int) (y / getHeight() * b.length);

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    showBkg = true;
                    if (oldChoose != c && listener != null) {
                        if (c > 0 && c < b.length) {
                            listener.onTouchingLetterChanged(b[c]);
                            choose = c;
                            invalidate();
                        }
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (oldChoose != c && listener != null) {
                        if (c > 0 && c < b.length) {
                            listener.onTouchingLetterChanged(b[c]);
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

        public void setOnTouchingLetterChangedListener(
                OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
            this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
        }

    }

    private class LetterListViewListener implements
            OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);

                mListView.setSelection(position);

                if (!mShowing) {
                    mShowing = true;
                    mDialogText.setVisibility(View.VISIBLE);
                }
                mDialogText.setText(brandList.get(position).get(
                        "first_letter"));
                mHandler.removeCallbacks(mRemoveWindow);
                mHandler.postDelayed(mRemoveWindow, 1500);
            }
        }

    }

    private class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }

}
