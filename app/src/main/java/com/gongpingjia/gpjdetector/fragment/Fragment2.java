package com.gongpingjia.gpjdetector.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.GalleryFileActivity_;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.activity.SplashActivity;
import com.gongpingjia.gpjdetector.data.kZDBItem;
import com.gongpingjia.gpjdetector.data.partItem;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.util.PhotoUtil;
import com.gongpingjia.gpjdetector.utility.FileUtils;
import com.gongpingjia.gpjdetector.utility.Utils;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


@EFragment(R.layout.fragment_2)
public class Fragment2 extends BaseFragment {

    MainActivity_ mainActivity;
    @ViewById
    TextView banner_title, part_title;
    @ViewById
    Button slidingmenu_toggler, known;
    @ViewById
    ImageView surface, tip_button;
    @ViewById
    View car_part_1, car_part_2, car_part_3, car_part_4, car_part_5,
            car_part_6, car_part_7, car_part_8, car_part_9, car_part_10, car_part_11, car_part_12,
            car_part_13;
    View[] car_parts;

    @ViewById
    LinearLayout car_parts_layout;
    @ViewById
    FrameLayout car_parts_frame;
    @ViewById
    RadioGroup p_level;
    @ViewById
    ListView mark_list;

    @ViewById
    RelativeLayout known_layout;

    ListAdapter adapter;

    ArrayList<kZDBItem> list;

    kZDatabase db;

    Resources res;

    int cur_part = 8;
    public PopupWindow popupWindow;

    HashMap<String, partItem> partMap;

    String last_capture_path;
    String last_pointer_xy;

    Bitmap baseBitmap;
    Canvas canvas;
    Paint paint;

    @Click
    void known() {
        known_layout.setVisibility(View.GONE);
    }

    @Click
    void known_layout() {

    }

    @Click
    void tip_button() {
        known_layout.setVisibility(View.VISIBLE);
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (isTransparentPixel(view, (int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            } else {

                switch (view.getId()) {
                    case R.id.car_part_1:
                        cur_part = 1;
                        break;
                    case R.id.car_part_2:
                        cur_part = 2;
                        break;
                    case R.id.car_part_3:
                        cur_part = 3;
                        break;
                    case R.id.car_part_4:
                        cur_part = 4;
                        break;
                    case R.id.car_part_5:
                        cur_part = 5;
                        break;
                    case R.id.car_part_6:
                        cur_part = 6;
                        break;
                    case R.id.car_part_7:
                        cur_part = 7;
                        break;
                    case R.id.car_part_8:
                        cur_part = 8;
                        break;
                    case R.id.car_part_9:
                        cur_part = 9;
                        break;
                    case R.id.car_part_10:
                        cur_part = 10;
                        break;
                    case R.id.car_part_11:
                        cur_part = 11;
                        break;
                    case R.id.car_part_12:
                        cur_part = 12;
                        break;
                    case R.id.car_part_13:
                        cur_part = 13;
                        break;
                    default:
                        cur_part = 0;
                        break;

                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (baseBitmap == null) {
                            baseBitmap = Bitmap.createBitmap(Constant.CANVAS_WIDTH,
                                    Constant.CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
                            canvas = new Canvas(baseBitmap);
                            canvas.drawColor(Color.TRANSPARENT);
                        }
                        break;
                    case MotionEvent.ACTION_UP:


                        canvas.drawCircle(motionEvent.getX(), motionEvent.getY(), 12, paint);
                        surface.setImageBitmap(baseBitmap);
                        switchPart(cur_part);
                        last_pointer_xy = motionEvent.getX() + "," + motionEvent.getY();
                        showPopupWindow(view,
                                (int) motionEvent.getX() + (int) car_parts_frame.getX() + (int) car_parts_layout.getX(),
                                (int) motionEvent.getY() + (int) car_parts_frame.getY() + (int) car_parts_layout.getY());
                        break;
                    default:
                        break;
                }
                MainActivity_ activity = (MainActivity_) getActivity();
                activity.lastpart = cur_part;
                return true;
            }
        }
    };


    private void switchPart(int cur_part) {
        part_title.setText(partMap.get(String.valueOf(cur_part)).getPart_name());
        switch (partMap.get(String.valueOf(cur_part)).getP_level()) {
            case -1:
                p_level.check(R.id.p0);
                break;
            case -2:
                p_level.check(R.id.p1);
                break;
            case -3:
                p_level.check(R.id.p2);
                break;
            case -4:
                p_level.check(R.id.p3);
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @AfterViews
    public void afterViews() {

        if (SplashActivity.isFirstIn) {
            known_layout.setVisibility(View.VISIBLE);
        }

        banner_title.setText("钣金修复漆面检查");
        mainActivity = (MainActivity_) getActivity();
        db = mainActivity.getDatabase();
        res = getResources();
        paint = new Paint();
        paint.setColor(Color.argb(0xff, 0xf9, 0x3f, 0x25));
        paint.setAntiAlias(true);

        car_parts = new View[]{car_part_1, car_part_2, car_part_3, car_part_4, car_part_5,
                car_part_6, car_part_7, car_part_8, car_part_9, car_part_10, car_part_11, car_part_12,
                car_part_13};

        for (View thisImageView : car_parts) {
            thisImageView.setOnTouchListener(touchListener);
        }

        partMap = mainActivity.getPartMap();

        list = mainActivity.getDB2Items();

        p_level.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.p0:
                        partMap.get(String.valueOf(cur_part)).setP_level(-1);
                        setImageVisibility(cur_part, false);
                        break;
                    case R.id.p1:
                        partMap.get(String.valueOf(cur_part)).setP_level(-2);
                        setImageVisibility(cur_part, true);
                        break;
                    case R.id.p2:
                        partMap.get(String.valueOf(cur_part)).setP_level(-3);
                        setImageVisibility(cur_part, true);
                        break;
                    case R.id.p3:
                        partMap.get(String.valueOf(cur_part)).setP_level(-4);
                        setImageVisibility(cur_part, true);
                        break;
                    default:
                        break;
                }
            }
        });

        mark_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] str = partMap.get(String.valueOf(cur_part)).getMarks().get(i).split(",");
                if (str.length < 3 || null == str[2]) {
                    Toast.makeText(mainActivity, "访问的图片不存在。", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Utils.isFileExist(str[2])) {
                    Toast.makeText(mainActivity, "访问的图片不存在。", Toast.LENGTH_SHORT).show();
                    return;
                }
                ;
                Intent intent = new Intent();
                intent.setClass(mainActivity, GalleryFileActivity_.class);
                intent.putExtra("url", str[2]);

                startActivity(intent);
            }
        });

        initView();

        mainActivity.getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                saveDateformView();
            }
        });

    }

    void initView() {

        adapter = new ListAdapter(mainActivity, partMap);
        mark_list.setAdapter(adapter);
        MainActivity_ activity = (MainActivity_) getActivity();

        cur_part = activity.lastpart;
        switchPart(activity.lastpart);

        if (baseBitmap == null) {
            baseBitmap = Bitmap.createBitmap(Constant.CANVAS_WIDTH,
                    Constant.CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.TRANSPARENT);
        }

        for (int i = 1; i <= 13; ++i) {
            setImageVisibility(i, partMap.get(String.valueOf(i)).getP_level() != -1);
        }
        reDrawPointer();
        adapter.notifyDataSetChanged();
    }

    @Background
    void saveDateformView() {
        try {
            for (int i = 1; i <= 13; ++i) {
                JSONObject rootJson = new JSONObject();
                JSONArray markArray = new JSONArray();
                partItem cur_part = partMap.get(String.valueOf(i));
                ArrayList<String> mark_list = cur_part.getMarks();
                for (String mark : mark_list) {
                    markArray.put(mark);
                }
                rootJson.accumulate("part_no", cur_part.getPart_no());
                rootJson.accumulate("marks", markArray);
                rootJson.accumulate("p_level", cur_part.getP_level());

                int index = mainActivity.searchIndex(cur_part.getId(), list);
                if (-1 == index) return;
                list.get(index).setValue(rootJson.toString());

                for (kZDBItem item : list) {
                    db.updateItem(item.getKey(), item.getValue());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    @Click
    public void slidingmenu_toggler() {
        mainActivity.getSlidingMenu().toggle();
    }


    private boolean isTransparentPixel(View view, int x, int y) {
        Drawable drawable = ((View) view).getBackground();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (null == bitmap || x < 0 || y < 0 || x >= bitmap.getWidth() || y >= bitmap.getHeight()) {
            return false;
        }
        int pixel = bitmap.getPixel(x, y);
        if (pixel == Color.TRANSPARENT) {
            return true;
        }
        return false;
    }

    private void showPopupWindow(View parent, int x, int y) {
        LayoutInflater layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (x > 600) {
            view = layoutInflater.inflate(R.layout.popupwindow_right, null);
        } else {
            view = layoutInflater.inflate(R.layout.popupwindow_left, null);
        }
        if (popupWindow == null) {

            popupWindow = new PopupWindow(view, 500, WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            popupWindow.setContentView(view);
        }
        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                        || Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED)) {
                    last_capture_path = Constant.sdcard + "/GPJImages/"
                            + new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                    Uri uri = Uri.fromFile(new File(last_capture_path));
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
                startActivityForResult(intent, Constant.REQUEST_CODE_CAMERA);
            }
        });
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                reDrawPointer();
            }
        });
        WindowManager windowManager = (WindowManager) mainActivity.getSystemService(Context.WINDOW_SERVICE);
        if (x > 600) {
            popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x - 436, y + 8);
        } else {
            popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x, y + 8);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 10086) {
                Log.d("msg", "10086");
                adapter.notifyDataSetChanged();
            } else {
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(mainActivity, "SD卡不可用，无法存储照片。", Toast.LENGTH_SHORT).show();
                } else {
                    partMap.get(String.valueOf(cur_part)).addMarks(last_pointer_xy + "," + (last_capture_path.substring(0, last_capture_path.length() - 4) + "_compressed.jpg"));
                    compressImage(last_capture_path, Constant.MAX_IMAGE_HEIGHT);

//                    adapter.notifyDataSetChanged();
                }
                popupWindow.dismiss();
            }


        }
    }

    void reDrawPointer() {
        baseBitmap.eraseColor(Color.TRANSPARENT);

        for (int i = 1; i <= 13; ++i) {
            ArrayList<String> list = partMap.get(String.valueOf(i)).getMarks();
            for (int j = 0; j < list.size(); ++j) {
                String[] xy = list.get(j).split(",");
                canvas.drawCircle(Float.parseFloat(xy[0]), Float.parseFloat(xy[1]), 12, paint);
            }
        }
        surface.setImageBitmap(baseBitmap);
        mainActivity.setBaseBitmap(baseBitmap);
    }


    @Background
    void compressImage(String imagePath, int maxHeight) {
        Bitmap bitmap;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        bitmap = BitmapFactory.decodeFile(imagePath, options); //此时返回bm为空
//        int scale = (int)(options.outHeight / (float) maxHeight);
//        int ys = options.outHeight % maxHeight;//求余数
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        }).start();

      /*  PhotoUtil.photoZoom(getActivity(), Uri.fromFile(new File(fileName)),
                Uri.fromFile(new File(fileName)), 10086, 3, 2,
                1000, this);*/
    }

    void setImageVisibility(int part, boolean isVisible) {
        View parentView = getView();
        View view;
        switch (part) {
            case 1:
                view = (View) parentView.findViewById(R.id.b_1);
                break;
            case 2:
                view = (View) parentView.findViewById(R.id.b_2);
                break;
            case 3:
                view = (View) parentView.findViewById(R.id.b_3);
                break;
            case 4:
                view = (View) parentView.findViewById(R.id.b_4);
                break;
            case 5:
                view = (View) parentView.findViewById(R.id.b_5);
                break;
            case 6:
                view = (View) parentView.findViewById(R.id.b_6);
                break;
            case 7:
                view = (View) parentView.findViewById(R.id.b_7);
                break;
            case 8:
                view = (View) parentView.findViewById(R.id.b_8);
                break;
            case 9:
                view = (View) parentView.findViewById(R.id.b_9);
                break;
            case 10:
                view = (View) parentView.findViewById(R.id.b_10);
                break;
            case 11:
                view = (View) parentView.findViewById(R.id.b_11);
                break;
            case 12:
                view = (View) parentView.findViewById(R.id.b_12);
                break;
            case 13:
                view = (View) parentView.findViewById(R.id.b_13);
                break;
            default:
                view = null;
                break;
        }
        if (null == view) return;
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public class ListAdapter extends BaseAdapter {

        HashMap<String, partItem> map;
        Context context;

        public ListAdapter(Context context, HashMap<String, partItem> map) {
            this.context = context;
            this.map = map;
        }

        @Override
        public int getCount() {
            return map.get(String.valueOf(cur_part)).getMarks().size();
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == view) {
                view = LayoutInflater.from(context).inflate(R.layout.mark_list_item, null);
                viewHolder = new ViewHolder((TextView) view.findViewById(R.id.text),
                        (ImageButton) view.findViewById(R.id.delete));
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.textView.setText(part_title.getText().toString() + "划痕");

            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    map.get(String.valueOf(cur_part)).getMarks().remove(position);
                    adapter.notifyDataSetChanged();
                    reDrawPointer();
                }
            });

            return view;
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

    class ViewHolder {

        public TextView textView;
        public ImageButton button;

        public ViewHolder(TextView textView, ImageButton button) {
            this.textView = textView;
            this.button = button;
        }
    }

}
