package com.gongpingjia.gpjdetector.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.CategoryActivity;
import com.gongpingjia.gpjdetector.activity.HistoryActivity;
import com.gongpingjia.gpjdetector.utility.BitmapCache;
import com.gongpingjia.gpjdetector.utility.kZDatabase;

public class ModelFragment extends Fragment implements
        AbsListView.OnItemClickListener {

    public String mBrandName;
    public String mBrandSlug;
    public List<Map<String, String>> mModelUnderBrand;
    private OnFragmentModelSelectionListener mListener;
    private ListView mListView;
    private ModelListAdapter mAdapter;
    private TextView mTxtBrand;
    private NetworkImageView mImgBrand;
    public String brand_logo_url;
    kZDatabase db;
    

    public ModelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModelUnderBrand = new ArrayList<Map<String, String>>();
        if (null == mBrandSlug) return;
        db = ((CategoryActivity)getActivity()).getDatabase();
        Cursor cursor = db.getModelList(mBrandSlug);
        do {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("slug", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("parent", cursor.getString(2));
            map.put("keywords", cursor.getString(3));
            map.put("thumbnail", cursor.getString(4));
            map.put("logo_img", cursor.getString(5));

            mModelUnderBrand.add(map);
        } while (cursor.moveToNext());
        cursor.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_model_list, container, false);


        mTxtBrand = (TextView) view.findViewById(R.id.txt_logo);
        mImgBrand = (NetworkImageView) view.findViewById(R.id.img_logo);
        brand_logo_url += "?imageView2/0/w/100/h/100";
		mImgBrand.setImageUrl(brand_logo_url, new ImageLoader(Volley.newRequestQueue(getActivity()), new BitmapCache()));
        mTxtBrand.setText(mBrandName);
        mAdapter = new ModelListAdapter(inflater);
        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        if (0 == mAdapter.getCount()) {
            TextView empty = (TextView) view.findViewById(android.R.id.empty);
            empty.setText("没有相应车型");
        }

        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	view.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
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
    public void onPause() {
    	super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onFragmentModelSelection(
                    mModelUnderBrand.get(position).get("slug"),
                    mModelUnderBrand.get(position).get("name"),
                    mModelUnderBrand.get(position).get("logo_img"));
           
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


    public interface OnFragmentModelSelectionListener {

        public void onFragmentModelSelection(String modelSlug,
                                             String modelName, String modelThumbnail);
    }

    public class ModelListAdapter extends BaseAdapter {

        public LayoutInflater mInflater;
        
        ImageLoader mImageLoader;

        public ModelListAdapter(LayoutInflater inflater) {
            mInflater = inflater;
            
            RequestQueue mQueue = Volley.newRequestQueue(getActivity());    
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());

        }

        @Override
        public int getCount() {

            return mModelUnderBrand.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }
        
        @Override  
        public int getViewTypeCount() {  
            return 2;  
        }
        
        @Override  
        public int getItemViewType(int position) {  
            return 0;
        } 

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ModelViewHolder modelHolder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_model_item, null);
                modelHolder = new ModelViewHolder();
                modelHolder.modelText = (TextView) convertView.findViewById(R.id.modelText);

                convertView.setTag(modelHolder);
                
            } else {
                modelHolder = (ModelViewHolder) convertView.getTag();
            }

            modelHolder.modelText.setText(mModelUnderBrand.get(position).get("name"));

            return convertView;

        }
        
        

        public final class ModelViewHolder {
            public TextView modelText;
        }
        

    }
    

}
