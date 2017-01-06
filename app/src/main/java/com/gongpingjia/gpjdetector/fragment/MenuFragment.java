package com.gongpingjia.gpjdetector.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.data.UserInfo;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
import com.gongpingjia.gpjdetector.utility.SharedPreUtil;
import com.gongpingjia.gpjdetector.utility.kZDatabase;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;



@EFragment(R.layout.fragment_menu)
public class MenuFragment extends Fragment {

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

            if (lastMenuID == view.getId()) {
                mainActivity.getSlidingMenu().showContent();
                if (view.getId() == R.id.btn_fragment0 ) {
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
                return;
            }

            MenuFragment.this.getView().findViewById(lastMenuID).setBackgroundResource(R.drawable.menu_button_bg_selector);
            view.setBackgroundResource(R.drawable.menu_button_bg_rectangle);

            switch (view.getId()) {
                case R.id.btn_fragment0:
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

                    mainActivity.showFragment(mainActivity.fragment0 = new Fragment0_());
                    break;
                case R.id.btn_fragment1:
                   if(Constant.CHECK_USERTYPE.equals(SharedPreUtil.getInstance().getUser().getUser_type())) {
                       mainActivity.showFragment(mainActivity.fragment1 = new Fragment1_());
                   }else{
                       mainActivity.showFragment(mainActivity.fragment1_1 = new Fragment1_1_());
                   }
                    break;
                case R.id.btn_fragment2:
                    mainActivity.showFragment(mainActivity.fragment2 = new Fragment2_());
                    break;
                case R.id.btn_fragment3:
                    mainActivity.showFragment(mainActivity.fragment3 = new Fragment3_());
                    break;
                case R.id.btn_fragment4:
                    mainActivity.showFragment(mainActivity.fragment4 = new Fragment4_());
                    break;
                case R.id.btn_fragment5:
                    mainActivity.showFragment(mainActivity.fragment5 = new Fragment5_());
                    break;
                case R.id.btn_fragment6:
                    mainActivity.showFragment(mainActivity.fragment6 = new Fragment6_());
                    break;
                case R.id.btn_fragment7:
                    mainActivity.showFragment(mainActivity.feedbackFragment = new FragmentFeedBack());
                    break;
                default:
                    break;
            }

            lastMenuID = view.getId();
        }
    };
    @ViewById
    public Button btn_fragment0, btn_fragment1, btn_fragment2, btn_fragment3, btn_fragment4,
            btn_fragment5, btn_fragment6, btn_fragment7, submitButton, exitButton, saveButton;
    MainActivity_ mainActivity;

    int lastMenuID = R.id.btn_fragment0;

    kZDatabase db;

    @Click
    public void submitButton() {

        int index0 = 0;
        int size;
        if(Constant.CHECK_USERTYPE.equals(SharedPreUtil.getInstance().getUser().getUser_type())){
            size = mainActivity.menu_status.length-1;
        }else{
            size = 1;
        }
        for (index0 = 0; index0 < size; ++index0) {
            final int index = index0;
            if (mainActivity.menu_status[index0]) continue;
            AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                    .setMessage("仍有未完成的检测项，请继续完成全部检测项后再尝试提交。")
                    .setPositiveButton("现在去完善", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (0 == index) {
                                btn_fragment0.performClick();
                            } else if (1 == index) {
                                btn_fragment1.performClick();
                            } else if (2 == index) {
                                btn_fragment2.performClick();
                            } else if (3 == index) {
                                btn_fragment3.performClick();
                            } else if (4 == index) {
                                btn_fragment4.performClick();
                            } else if (5 == index) {
                                btn_fragment5.performClick();
                            } else if (6 == index) {
                                btn_fragment6.performClick();
                            }
//                            else if (7 == index[0]) {
//                                btn_fragment7.performClick();
//                            }
                        }
                    })
                    .create();
            dialog.show();
            return;
        }

        ProgressDialog progressDialog = Constant.showProgress(mainActivity, null, "正在提交数据...");
        buildDataTask task = new buildDataTask(progressDialog);
        task.execute();
    }

    @Click
    public void exitButton() {
        mainActivity.showExitDialog();
    }

    @Click
    void saveButton() {
        int index = mainActivity.searchIndex("CX", mainActivity.getDB01Items());
        String modelName = null;
//        if (index != 0) {
            modelName = mainActivity.getDB01Items().get(index).getValue();
            if (null == modelName || modelName.equals("") || modelName.equals("null") || modelName.equals("NULL")) {
                Toast.makeText(mainActivity, "请先选定车型后再保存。", Toast.LENGTH_LONG).show();
                return;
            }
      /*  } else {
            Toast.makeText(mainActivity, "保存失败，请重试。", Toast.LENGTH_LONG).show();
            return;
        }*/

        final ProgressDialog progressDialog = Constant.showProgress(mainActivity, null, "正在保存...");

        mainActivity.getDatabase().setStatus(Constant.getTableName(), mainActivity.checkStatus());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                mainActivity.finish();
                String msg;
                UserInfo user = SharedPreUtil.getInstance().getUser();
                if(user != null && Constant.CHECK_USERTYPE.equals(user.getUser_type())){
                    msg = "采集的信息已保存，可以在检测定价记录-未提交中，继续完成信息采集";
                } else {
                    msg = "检测记录已保存，可以在检测定价记录-未提交中，继续检测";
                }
                Toast.makeText(mainActivity, msg, Toast.LENGTH_LONG).show();
            }
        }, 3000);
    }

    @AfterViews
    public void afterViews() {
        mainActivity = (MainActivity_) getActivity();
        db = mainActivity.getDatabase();
        UserInfo user = SharedPreUtil.getInstance().getUser();
        if(user != null && Constant.CHECK_USERTYPE.equals(user.getUser_type())){

            btn_fragment2.setVisibility(View.VISIBLE);
            btn_fragment3.setVisibility(View.VISIBLE);
            btn_fragment4.setVisibility(View.VISIBLE);
            btn_fragment5.setVisibility(View.VISIBLE);
            btn_fragment6.setVisibility(View.VISIBLE);
            btn_fragment7.setVisibility(View.VISIBLE);
        }else{
            btn_fragment2.setVisibility(View.GONE);
            btn_fragment3.setVisibility(View.GONE);
            btn_fragment4.setVisibility(View.GONE);
            btn_fragment5.setVisibility(View.GONE);
            btn_fragment6.setVisibility(View.GONE);
            btn_fragment7.setVisibility(View.GONE);
        }

        btn_fragment0.setOnClickListener(buttonListener);
        btn_fragment1.setOnClickListener(buttonListener);
        btn_fragment2.setOnClickListener(buttonListener);
        btn_fragment3.setOnClickListener(buttonListener);
        btn_fragment4.setOnClickListener(buttonListener);
        btn_fragment5.setOnClickListener(buttonListener);
        btn_fragment6.setOnClickListener(buttonListener);
        btn_fragment7.setOnClickListener(buttonListener);

        MenuFragment.this.getView().findViewById(R.id.btn_fragment0).setBackgroundResource(R.drawable.menu_button_bg_rectangle);

    }

    class buildDataTask extends AsyncTask<Void, Void, JSONObject> {

        ProgressDialog progressDialog;

        public buildDataTask (ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected JSONObject doInBackground(Void...params) {
            return mainActivity.buildData();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (null == jsonObject) {
                progressDialog.dismiss();
                return;
            }
            mainActivity.requestUtils.PostItems(jsonObject, new RequestUtils.OnPostItemsCallback() {
                @Override
                public void OnPostSuccess(JSONObject jsonObject) {
                    progressDialog.dismiss();
                    Toast.makeText(mainActivity, "提交成功。", Toast.LENGTH_SHORT).show();
                    db.setIsFinish(Constant.getTableName());


//                    getActivity().finish();
                    exitThisActivity();
                }

                @Override
                public void OnPostError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void exitThisActivity() {
        getActivity().finish();
    }

}