package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.data.CaptureItems;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.kZViews.TouchImageView;
import com.gongpingjia.gpjdetector.util.PhotoUtil;
import com.gongpingjia.gpjdetector.utility.FileUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.Utils;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@EFragment(R.layout.fragment_1_1)
public class Fragment1_1 extends Fragment {

    MainActivity_ mainActivity;
    ArrayList<CaptureItems> list1,list2,list3,list4,list5;
    ArrayList<CaptureItems> list = new ArrayList<CaptureItems>();
    String last_capture_path;
    GridViewAdapter adapter1,adapter2,adapter3,adapter4,adapter5;
    PopupWindow mPopupWindow;
    PopupWindow mGuidePopupWindow;

    @ViewById
    Button slidingmenu_toggler, extra;
    @ViewById
    TextView banner_title;
    @ViewById
    GridView gridView1,gridView2,gridView3,gridView4,gridView5;

    private HashMap<String, Integer> picMap = new HashMap<String, Integer>();
    private int mGuidePosition;
    private int mClickType;

    @Click
    void slidingmenu_toggler() {
        mainActivity.getSlidingMenu().toggle();
    }

    TextView image_title;
    TouchImageView touchImageView;
    Button close;
    ImageView recapture;
    ImageView delete;
    LinearLayout delete_group;

    kZDatabase db;

    private TextView mGuideBack;
    private TextView mGuidePhoto;
    private TouchImageView mGuideImg;

    @AfterViews
    public void afterViews() {
        mainActivity = (MainActivity_) getActivity();
        db = mainActivity.getDatabase();
        banner_title.setText("车辆照片采集");

//        list = mainActivity.getDB1Items();
        list1 = mainActivity.getDB11Items("基本信息");
        list2 = mainActivity.getDB11Items("发动机舱");
        list3 = mainActivity.getDB11Items("细节");
        list4 = mainActivity.getDB11Items("内饰");
        list5 = mainActivity.getDB11Items("其他");
        adapter1 = new GridViewAdapter(list1,1);
        adapter2 = new GridViewAdapter(list2,2);
        adapter3 = new GridViewAdapter(list3,3);
        adapter4 = new GridViewAdapter(list4,4);
        adapter5 = new GridViewAdapter(list5,5);
        gridView1.setAdapter(adapter1);
        gridView2.setAdapter(adapter2);
        gridView3.setAdapter(adapter3);
        gridView4.setAdapter(adapter4);
        gridView5.setAdapter(adapter5);

        picMap.put("主驾驶座椅", R.drawable.pic_04);
        picMap.put("仪表盘", R.drawable.pic_05);
        picMap.put("换挡杆", R.drawable.pic_06);
        picMap.put("安全带底部", R.drawable.pic_07);
        picMap.put("中控台全图", R.drawable.pic_08);
        picMap.put("左A柱铰链螺丝", R.drawable.pic_09);
        picMap.put("左侧下边梁", R.drawable.pic_12);
        picMap.put("左B柱铰链螺丝", R.drawable.pic_13);
        picMap.put("后备箱底槽", R.drawable.pic_15);
        picMap.put("后挡玻璃生产日期", R.drawable.pic_16);
        picMap.put("右后玻璃生产日期", R.drawable.pic_18);
        picMap.put("右B柱铰链螺丝", R.drawable.pic_19);
        picMap.put("右前玻璃生产日期", R.drawable.pic_20);
        picMap.put("右A柱铰链螺丝", R.drawable.pic_21);
        picMap.put("铭牌", R.drawable.pic_22);
        picMap.put("右侧下边梁", R.drawable.pic_23);
        picMap.put("发动机机舱", R.drawable.pic_25);
        picMap.put("水箱框架", R.drawable.pic_26);
        picMap.put("发动机盖螺丝", R.drawable.pic_27);
        picMap.put("左前翼子板螺丝", R.drawable.pic_28);
        picMap.put("右前翼子板螺丝", R.drawable.pic_29);
        picMap.put("左前减震器底座", R.drawable.pic_30);
        picMap.put("右前减震器底座", R.drawable.pic_31);

        final View popupView = mainActivity.getLayoutInflater().inflate(R.layout.popup_image, null);

        image_title = (TextView) popupView.findViewById(R.id.image_title);
        touchImageView = (TouchImageView) popupView.findViewById(R.id.imageView);
        close = (Button) popupView.findViewById(R.id.extra);
        recapture = (ImageView) popupView.findViewById(R.id.recapture);
        delete = (ImageView) popupView.findViewById(R.id.delete);
        delete_group = (LinearLayout) popupView.findViewById(R.id.delete_group);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow = new PopupWindow(popupView);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));

        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);

        initGuidePop();
        mainActivity.getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                saveData2DB();
            }
        });
    }

    private void initGuidePop() {
        final View guidePopupView = mainActivity.getLayoutInflater().inflate(R.layout.guide_popup_image, null);
        mGuideBack = (TextView) guidePopupView.findViewById(R.id.back);
        mGuidePhoto = (TextView) guidePopupView.findViewById(R.id.photo);
        mGuideImg = (TouchImageView) guidePopupView.findViewById(R.id.imageView);
        mGuideBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuidePopupWindow.dismiss();
            }
        });
        mGuidePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(mGuidePosition);
                mGuidePopupWindow.dismiss();
            }
        });
        mGuidePopupWindow = new PopupWindow(guidePopupView);
        mGuidePopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mGuidePopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mGuidePopupWindow.setTouchable(true);
        mGuidePopupWindow.setOutsideTouchable(true);
        mGuidePopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
        mGuidePopupWindow.getContentView().setFocusable(true);
        mGuidePopupWindow.getContentView().setFocusableInTouchMode(true);
        mGuidePopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
    }

    class GridViewAdapter extends BaseAdapter {
        private  ArrayList<CaptureItems> list;
        private int type;

        public  GridViewAdapter(ArrayList<CaptureItems> list,int type){
            this.list = list;
            this.type = type;
        }

        @Override
        public int getCount() {
            return list == null? 0:list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            GridViewHolder viewHolder;
            if (null == view) {
                view = LayoutInflater.from(mainActivity).inflate(R.layout.gridview_item, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 230);
                view.setLayoutParams(param);
                viewHolder = new GridViewHolder((ImageView) view.findViewById(R.id.image), (TextView) view.findViewById(R.id.info));
                view.setTag(viewHolder);
            } else {
                viewHolder = (GridViewHolder) view.getTag();
            }

            if(list == null){
                return view;
            }

            viewHolder.textView.setText(list.get(position).desc);

            if (type == 5 && position == list.size() - 1) {
                viewHolder.imageView.setBackgroundResource(R.drawable.gridview_add_bg_selector);
                viewHolder.imageView.setImageBitmap(null);
            }
            final int count = 6;
            if (type == 5 && position >= count && position != list.size() - 1) {
                list.get(position).key = "extra_cap_" + (position - count);
            }
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickType = type;
                    if (list.get(position).file_path == null || list.get(position).file_path.equals("null")) {
                        if (Constant.PHOTO_USERTYPE.equals(SharedPreUtil.getInstance().getUser().getUser_type())) {
                            for (Map.Entry<String, Integer> entry : picMap.entrySet()) {
                                if (entry.getKey().equals(list.get(position).desc)) {
                                    int picId = entry.getValue();
                                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),picId);
                                    mGuideImg.setImageBitmap(bitmap);
                                    mGuidePopupWindow.showAtLocation(getActivity().findViewById(R.id.root_layout), Gravity.BOTTOM, 0, 0);
                                    mGuidePosition = position;
                                    return;
                                }
                            }
                        }
                        takePhoto(position);

                    } else {
                        if (type == 5 && position > count) {
                            delete_group.setVisibility(View.VISIBLE);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mPopupWindow.dismiss();
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                        } else {
                            delete_group.setVisibility(View.GONE);
                        }
                        image_title.setText(list.get(position).desc);
                        touchImageView.setImageBitmap(BitmapFactory.decodeFile(list.get(position).file_path));
                        recapture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mClickType = type;
                                takePhoto(position);
                                mPopupWindow.dismiss();
                            }
                        });
                        mPopupWindow.showAtLocation(getActivity().findViewById(R.id.root_layout),
                                Gravity.CENTER, 0, 0);
                    }


                }
            });
            if (null != list.get(position).file_path) {
                if (Utils.isFileExist(list.get(position).file_path)) {
                    Glide.with(Fragment1_1.this.getActivity()).load(list.get(position).file_path).into(viewHolder.imageView);
                }
            } else {
                viewHolder.imageView.setImageBitmap(null);
            }
            return view;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    private void takePhoto(int position) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED)) {
            last_capture_path = Constant.sdcard + "/GPJImages/"
                    + new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Uri uri = Uri.fromFile(new File(last_capture_path));
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, position);
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10086) {
                Log.d("msg", "10086");
                notifyDataSetChanged();
            } else {
                ArrayList<CaptureItems> list00 = new ArrayList<CaptureItems>();
                if(mClickType == 1){
                    list00 = list1;
                }else if(mClickType == 2){
                    list00 = list2;
                }else if(mClickType == 3){
                    list00 = list3;
                }else if(mClickType == 4){
                    list00 = list4;
                }else if(mClickType == 5){
                    list00 = list5;
                }
                final ArrayList<CaptureItems> list0 = list00;
                if (mClickType == 5 && requestCode == list5.size() - 1) {
                    LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.edittext, null);
                    final EditText editText = (EditText) view.findViewById(R.id.desc);
                    AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                            .setView(view)
                            .setPositiveButton("添加备注", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).create();
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            list5.get(requestCode).desc = editText.getText().toString();
                            notifyDataSetChanged();
                        }
                    });
                    list5.add(new CaptureItems("ADD", "点击添加", null));
                    adapter1.notifyDataSetChanged();
                }
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(mainActivity, "SD卡不可用，无法存储照片。", Toast.LENGTH_SHORT).show();
                } else {
                    list0.get(requestCode).file_path = last_capture_path.substring(0, last_capture_path.length() - 4) + "_compressed.jpg";
                    compressImage(last_capture_path, Constant.MAX_IMAGE_HEIGHT);

                }
            }
        }

    }

    private void notifyDataSetChanged() {
        if(mClickType == 1){
            adapter1.notifyDataSetChanged();
        }else if(mClickType == 2){
            adapter2.notifyDataSetChanged();
        }else if(mClickType == 3){
            adapter3.notifyDataSetChanged();
        }else if(mClickType == 4){
            adapter4.notifyDataSetChanged();
        }else if(mClickType == 5){
            adapter5.notifyDataSetChanged();
        }
    }

    void compressImage(String imagePath, int maxHeight) {
        Bitmap bitmap;
        bitmap = PhotoUtil.getLocalImage(new File(imagePath));
        int degree = PhotoUtil.getBitmapDegree(imagePath);
        if (degree != 0) {
            bitmap = PhotoUtil.rotateBitmapByDegree(bitmap, degree);
        }

        String fileName = last_capture_path.substring(0, last_capture_path.length() - 4) + "_compressed.jpg";

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
                FileUtils.deleteFile(last_capture_path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmap.recycle();
        notifyDataSetChanged() ;
   /*     PhotoUtil.photoZoom(getActivity(), Uri.fromFile(new File(fileName)),
                Uri.fromFile(new File(fileName)), 10086, 3, 2,
                1000, this);*/
        Log.d("msg", "11111111111111");
    }

    class GridViewHolder {
        public ImageView imageView;
        public TextView textView;

        public GridViewHolder(ImageView imageView, TextView textView) {
            this.imageView = imageView;
            this.textView = textView;
        }
    }

    @Background
    public void saveData2DB() {
//        int _id = 10129;
        int _id2 = 10167;
        int count = 7;
        list.clear();
        list.addAll(list1);
        list.addAll(list2);
        list.addAll(list3);
        list.addAll(list4);
        list.addAll(list5);
        for (CaptureItems item : list) {
            if(item.key.contains("extra_cap_")){
                db.insertItem(String.valueOf(_id2++), item.key, item.desc, item.file_path, "cap","其他","",String.valueOf(count++));
            }else{
                if(item.id == null)return;
                db.insertItem(item.id, item.key, item.desc, item.file_path, "cap","","","");
            }

        }
    }

}
