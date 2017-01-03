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


@EFragment(R.layout.fragment_1)
public class Fragment1 extends Fragment {

    MainActivity_ mainActivity;
    ArrayList<CaptureItems> list;
    String last_capture_path;
    GridViewAdapter adapter;
    PopupWindow mPopupWindow;
    PopupWindow mGuidePopupWindow;

    @ViewById
    Button slidingmenu_toggler, extra;
    @ViewById
    TextView banner_title;
    @ViewById
    GridView gridView;

    private HashMap<String, Integer> picMap = new HashMap<String, Integer>();
    private int mGuidePosition;

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
//        list = mainActivity.getCaptureItems();
        adapter = new GridViewAdapter();
        list = mainActivity.getDB1Items();
        gridView.setAdapter(adapter);
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
        mPopupWindow = new PopupWindow(popupView, 1120, 1600, true);
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
        @Override
        public int getCount() {
            return list.size();
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

            viewHolder.textView.setText(list.get(position).desc);

            if (position == list.size() - 1) {
                viewHolder.imageView.setBackgroundResource(R.drawable.gridview_add_bg_selector);
                viewHolder.imageView.setImageBitmap(null);
            }
            final int count;
            if (Constant.CHECK_USERTYPE.equals(SharedPreUtil.getInstance().getUser().getUser_type())) {
                count = 14;
            } else {
                count = 31;
            }
            if (position >= count && position != list.size() - 1) {
                list.get(position).key = "extra_cap_" + (position - count);
            }
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                        final int count0 = count;
                        if (position > count0) {
                            delete_group.setVisibility(View.VISIBLE);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mPopupWindow.dismiss();
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();
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
                                takePhoto(position);
                                mPopupWindow.dismiss();
                            }
                        });
                        mPopupWindow.showAtLocation(getActivity().findViewById(R.id.root_layout),
                                Gravity.BOTTOM, 0, 120);
                    }


                }
            });
            if (null != list.get(position).file_path) {
                if (Utils.isFileExist(list.get(position).file_path)) {
                    viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(list.get(position).file_path));
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
                adapter.notifyDataSetChanged();
            } else {
                if (requestCode == list.size() - 1) {
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
                            list.get(requestCode).desc = editText.getText().toString();
                            adapter.notifyDataSetChanged();
                        }
                    });
                    list.add(new CaptureItems("ADD", "点击添加", null));
                    adapter.notifyDataSetChanged();
                }
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(mainActivity, "SD卡不可用，无法存储照片。", Toast.LENGTH_SHORT).show();
                } else {
                    list.get(requestCode).file_path = last_capture_path.substring(0, last_capture_path.length() - 4) + "_compressed.jpg";
                    compressImage(last_capture_path, Constant.MAX_IMAGE_HEIGHT);

                }
            }
        }

    }

    void compressImage(String imagePath, int maxHeight) {
        Bitmap bitmap;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        bitmap = BitmapFactory.decodeFile(imagePath, options);
//        int scale = (int)(options.outHeight / (float) maxHeight);
//        int ys = options.outHeight % maxHeight;
//        float fe = ys / (float) maxHeight;
//        if(fe >= 0.5) {
//            scale = scale + 1;
//        }
//        if (scale <= 0) {
//            scale = 1;
//        }
//        options.inSampleSize = scale;
//
//        options.inJustDecodeBounds = false;
//        bitmap = BitmapFactory.decodeFile(imagePath, options);


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

        PhotoUtil.photoZoom(getActivity(), Uri.fromFile(new File(fileName)),
                Uri.fromFile(new File(fileName)), 10086, 3, 2,
                1000, this);
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
        int count = 15;
        for (CaptureItems item : list) {
            if(item.key.contains("extra_cap_")){
                db.insertItem(String.valueOf(_id2++), item.key, item.desc, item.file_path, "cap","",String.valueOf(count++),"");
            }else{
                if(item.id == null)return;
                db.insertItem(item.id, item.key, item.desc, item.file_path, "cap","","","");
            }

        }
    }

}
