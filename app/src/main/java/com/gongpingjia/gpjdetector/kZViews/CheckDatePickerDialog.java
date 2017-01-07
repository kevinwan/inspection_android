package com.gongpingjia.gpjdetector.kZViews;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.util.DhUtil;

import java.util.Calendar;

public class CheckDatePickerDialog extends AlertDialog {


    NumberPicker yearPicker, monthPicker, dayPicker;

    OnDialogCallBack dialogCallBack;

    int minYear, minMonth, minDay, maxYear, maxMonth, maxDay;

    Button okB;

    Button noB;

    int currentYear;

    public int currentMonth;

    public int currentDay;

    int monthPickerVisibility;
    int dayPickerVisibility = View.GONE;
    String title;
    int year_newVal, value_year, month;

    public boolean setCurrentTime = false;
    private TextView dialog_title;

    public CheckDatePickerDialog(Context context) {
        super(context);
    }

    public void setName(String title) {
        this.title = title;

    }

    public CheckDatePickerDialog(Context context, String title) {
        super(context);
        this.title = title;
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams params = getWindow()
                .getAttributes();
        params.width = DhUtil.dip2px(getContext(), 280);
        getWindow().setAttributes(params);
        getWindow().setWindowAnimations(R.style.umeng_fb_image_dialog_anim);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker_dialog);


        Calendar a = Calendar.getInstance();
        currentYear = a.get(Calendar.YEAR);// 得到年

        currentMonth = a.get(Calendar.MONTH) + 1;
        currentDay = a.get(Calendar.DAY_OF_MONTH);

        yearPicker = (NumberPicker) findViewById(R.id.year);
        yearPicker.setMinValue(minYear);
        yearPicker.setMaxValue(maxYear);

        yearPicker.setFocusableInTouchMode(true);
        yearPicker.setFocusable(true);
        if (dayPickerVisibility == View.VISIBLE) {
            yearPicker.setOnValueChangedListener(new OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (newVal == currentYear) {
                        monthPicker.setMinValue(currentMonth);
                        monthPicker.setMaxValue(12);

                    } else {
                        monthPicker.setMinValue(1);
                        monthPicker.setMaxValue(12);
                    }
                    year_newVal = newVal;
                    dayPicker.setMinValue(1);
                    dayPicker.setMaxValue(getDay(year_newVal, month));
                }
            });
        }
        value_year = yearPicker.getValue();
        monthPicker = (NumberPicker) findViewById(R.id.month);
        monthPicker.setMinValue(minMonth);
        monthPicker.setMaxValue(maxMonth);
        monthPicker.setFocusableInTouchMode(true);
        monthPicker.setFocusable(true);
        monthPicker.setVisibility(monthPickerVisibility);

        monthPicker.setOnValueChangedListener(new OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                month = i1;
                dayPicker.setMinValue(1);
                dayPicker.setMaxValue(getDay(year_newVal, month));
            }
        });

        dayPicker = (NumberPicker) findViewById(R.id.day);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);

        dayPicker.setFocusableInTouchMode(true);
        dayPicker.setFocusable(true);
        dayPicker.setVisibility(dayPickerVisibility);

        if (setCurrentTime) {
            yearPicker.setValue(currentYear);
            monthPicker.setValue(currentMonth);
            dayPicker.setValue(currentDay);
        }
        dialog_title = (TextView)findViewById(R.id.dialog_title);
        okB = (Button) findViewById(R.id.ok);
        okB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialogCallBack != null) {
                    if (monthPicker.getVisibility() == View.VISIBLE) {
                        dialogCallBack.onSelectResult(yearPicker.getValue() + "", monthPicker.getValue() + "", dayPicker.getValue() + "");
                    } else {
                        dialogCallBack.onSelectResult(yearPicker.getValue() + "", "", "");
                    }
                }
                dismiss();
            }
        });
        noB = (Button) findViewById(R.id.no);
        noB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dialogCallBack.onNoSelectResult();
            }
        });


    }


    private int getDay(int year, int mouth) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
//year年
        time.set(Calendar.MONTH, mouth - 1);
//Calendar对象默认一月为0,month月
        int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }


    public void setMonthPickerVisibility(int Visibility) {
        this.monthPickerVisibility = Visibility;
    }

    public void setDayPickerVisibility(int Visibility) {

        this.dayPickerVisibility = Visibility;
    }


    public int getMinDay() {
        return minDay;
    }

    public void setMinDay(int minDay) {
        this.minDay = minDay;
    }

    public int getMaxDay() {
        return maxDay;
    }

    public void setMaxDay(int maxDay) {
        this.maxDay = maxDay;
    }

    public int getMinYear() {
        return minYear;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    public int getMinMonth() {
        return minMonth;
    }

    public void setMinMonth(int minMonth) {
        this.minMonth = minMonth;
    }

    public int getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    public int getMaxMonth() {
        return maxMonth;
    }

    public void setMaxMonth(int maxMonth) {
        this.maxMonth = maxMonth;
    }

    public OnDialogCallBack getDialogCallBack() {
        return dialogCallBack;
    }

    public void setDialogCallBack(OnDialogCallBack dialogCallBack) {
        this.dialogCallBack = dialogCallBack;
    }

    public void setNoText(String notitle) {
        noB.setText(notitle);
    }

    public void setTitleText(String title) {
        dialog_title.setText(title);
    }

    public interface OnDialogCallBack {
        void onSelectResult(String year, String month, String day);
        void onNoSelectResult();
    }


}
