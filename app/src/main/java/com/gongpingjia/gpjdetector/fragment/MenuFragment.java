package com.gongpingjia.gpjdetector.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity_;
import com.gongpingjia.gpjdetector.global.Constant;
import com.gongpingjia.gpjdetector.utility.RequestUtils;
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
                if (view.getId() == R.id.btn_fragment0 && mainActivity.fragment0.vPager.getCurrentItem() != 2) {
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
                return;
            }

            MenuFragment.this.getView().findViewById(lastMenuID).setBackgroundResource(R.drawable.menu_button_bg_selector);
            view.setBackgroundResource(R.drawable.menu_button_bg_rectangle);

            switch (view.getId()) {
                case R.id.btn_fragment0:
                    if (mainActivity.fragment0.vPager.getCurrentItem() != 2) {
                        mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    }

                    mainActivity.showFragment(mainActivity.fragment0 = new Fragment0_());
                    break;
                case R.id.btn_fragment1:
                    mainActivity.showFragment(mainActivity.fragment1 = new Fragment1_());
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
                    mainActivity.showFragment(mainActivity.fragment7 = new Fragment7_());
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

    @Click
    public void submitButton() {

        final int index[] = new int[1];
        for (index[0] = 0; index[0] < mainActivity.menu_status.length; ++index[0]) {
            if (mainActivity.menu_status[index[0]]) continue;
            AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                    .setMessage("仍有未完成的检测项，请继续完成全部检测项后再尝试提交。")
                    .setPositiveButton("现在去完善", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (0 == index[0]) {
                                btn_fragment0.performClick();
                            } else if (1 == index[0]) {
                                btn_fragment1.performClick();
                            } else if (2 == index[0]) {
                                btn_fragment2.performClick();
                            } else if (3 == index[0]) {
                                btn_fragment3.performClick();
                            } else if (4 == index[0]) {
                                btn_fragment4.performClick();
                            } else if (5 == index[0]) {
                                btn_fragment5.performClick();
                            } else if (6 == index[0]) {
                                btn_fragment6.performClick();
                            } else if (7 == index[0]) {
                                btn_fragment7.performClick();
                            }
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
        mainActivity.finish();
        Toast.makeText(mainActivity, "检测数据已保存，可以再检测记录-未完成中继续检测。", Toast.LENGTH_LONG).show();
    }

    @AfterViews
    public void afterViews() {
        mainActivity = (MainActivity_) getActivity();
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
                Toast.makeText(mainActivity, "数据异常，无法提交。", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            mainActivity.requestUtils.PostItems(jsonObject, new RequestUtils.OnPostItemsCallback() {
                @Override
                public void OnPostSuccess(JSONObject jsonObject) {
                    progressDialog.dismiss();
                    Toast.makeText(mainActivity, "提交成功。", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }

                @Override
                public void OnPostError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}