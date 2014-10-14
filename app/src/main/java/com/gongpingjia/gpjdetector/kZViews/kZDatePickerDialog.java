package com.gongpingjia.gpjdetector.kZViews;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

public class kZDatePickerDialog extends DatePickerDialog {

	private final int sdk_int = android.os.Build.VERSION.SDK_INT;

	private int minYEAR;
	private int minMONTH;
	private int minDAY_OF_MONTH;

	private int maxYEAR;
	private int maxMONTH;
	private int maxDAY_OF_MONTH;

	private int curYEAR;
	private int curMONTH;
	private int curDAY_OF_MONTH;

	private DatePicker datePicker;
	private String titleMsg;
	private boolean isLowerSDK = false;

	private boolean isHideYear = false;
	private boolean isHideMonth = false;
	private boolean isHideDayOfMonth = false;
	private boolean hasMinDate = true;
	private boolean hasMaxDate = true;

	@SuppressLint("NewApi")
	public kZDatePickerDialog(Context context, OnDateSetListener callBack, Calendar curDate, Calendar minDate, Calendar maxDate, String titleMsg) {
		super(context, callBack, curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), curDate.get(Calendar.DAY_OF_MONTH));

		//init
		if (null != titleMsg) {
			this.titleMsg = titleMsg;
		}

		if (null == minDate) {
			hasMinDate = false;
		}

		if (null == maxDate) {
			hasMaxDate = false;
		}

		if (hasMinDate) {
			minYEAR = minDate.get(Calendar.YEAR);
			minMONTH = minDate.get(Calendar.MONTH);
			minDAY_OF_MONTH = minDate.get(Calendar.DAY_OF_MONTH);
		}

		if (hasMaxDate) {
			maxYEAR = maxDate.get(Calendar.YEAR);
			maxMONTH = maxDate.get(Calendar.MONTH);
			maxDAY_OF_MONTH = maxDate.get(Calendar.DAY_OF_MONTH);
		}

		if (null != curDate) {
			curYEAR = curDate.get(Calendar.YEAR);
			curMONTH = curDate.get(Calendar.MONTH);
			curDAY_OF_MONTH = curDate.get(Calendar.DAY_OF_MONTH);
		}

		//set min and max date
		if (sdk_int >= 11) {
			datePicker = this.getDatePicker();
			if (hasMinDate) {
				datePicker.setMinDate(minDate.getTime().getTime());
			}
			if (hasMaxDate) {
				datePicker.setMaxDate(maxDate.getTime().getTime());
			}
		} else {
			isLowerSDK = true;
		}
	}

	@Override
	public void onDateChanged(DatePicker view, int year,int month, int day) {

		if (isLowerSDK) {
			boolean beforeMinDate = false;
			boolean afterMaxDate = false;

			if (year < minYEAR) {
				beforeMinDate = true;
			} else if (year == minYEAR) {
				if(month < minMONTH) {
					beforeMinDate=true;
				}
				else if(month == minMONTH) {
					if(day < minDAY_OF_MONTH) {
						beforeMinDate = true;
					}
				}
			}

			if(!beforeMinDate) {
				if(year > maxYEAR) {
					afterMaxDate=true;
				} else if (year == maxYEAR) {
					if (month > maxMONTH) {
						afterMaxDate = true;
					} else if (month == maxMONTH){
						if (day > maxDAY_OF_MONTH) {
							afterMaxDate=true;
						}
					}
				}
			}

			if (!hasMinDate) {
				beforeMinDate = false;
			}

			if (!hasMaxDate) {
				afterMaxDate = false;
			}

			//need set invalid date to mindate or maxdate
			if (beforeMinDate || afterMaxDate) {
				if (beforeMinDate) {
					year = minYEAR;
					month = minMONTH;
					day = minDAY_OF_MONTH;
				} else {
					year = maxYEAR;
					month = maxMONTH;
					day = maxDAY_OF_MONTH;
				}
				view.updateDate(year,  month,  day);
			}
		}

		//display in view title
		String strDate = "";
		strDate += (!isHideYear ? (year + "年") : "");
		strDate += (!isHideMonth ? ((month + 1) + "月") : "");
		strDate += (!isHideDayOfMonth ? (day + "日") : "");
		setTitle(strDate);
	}

	@Override
	public void show() {
		super.show();
		DatePicker dp = findDatePicker((ViewGroup) this.getWindow().getDecorView());
		if (dp != null) {
			Class c = dp.getClass();
			Field f;
			try {
				if (isHideYear) {
					if (sdk_int < 11) {
						f = c.getDeclaredField("mYearPicker" );
						f.setAccessible(true);
						LinearLayout l= (LinearLayout)f.get(dp);
						l.setVisibility(View.GONE);
					} else {
						f = c.getDeclaredField("mYearSpinner");
						f.setAccessible(true );
						LinearLayout l= (LinearLayout)f.get(dp);
						l.setVisibility(View.GONE);
					}
				}
				if (isHideMonth) {
					if (sdk_int < 11) {
						f = c.getDeclaredField("mMonthPicker" );
						f.setAccessible(true);
						LinearLayout l= (LinearLayout)f.get(dp);
						l.setVisibility(View.GONE);
					} else {
						f = c.getDeclaredField("mMonthSpinner");
						f.setAccessible(true );
						LinearLayout l= (LinearLayout)f.get(dp);
						l.setVisibility(View.GONE);
					}

				}
				if (isHideDayOfMonth) {
					if (sdk_int < 11) {
						f = c.getDeclaredField("mDayPicker" );
						f.setAccessible(true);
						LinearLayout l= (LinearLayout)f.get(dp);
						l.setVisibility(View.GONE);
					} else {
						f = c.getDeclaredField("mDaySpinner");
						f.setAccessible(true );
						LinearLayout l= (LinearLayout)f.get(dp);
						l.setVisibility(View.GONE);
					}

				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}

		//display in view title
		String strDate = "";
		strDate += (!isHideYear ? (curYEAR + "年") : "");
		strDate += (!isHideMonth ? ((curMONTH + 1) + "月") : "");
		strDate += (!isHideDayOfMonth ? (curDAY_OF_MONTH + "日") : "");
		setTitle(strDate);
	}

	private DatePicker findDatePicker(ViewGroup group) {
		if (group != null) {
			for (int i = 0, j = group.getChildCount(); i < j; i++) {
				View child = group.getChildAt(i);
				if (child instanceof DatePicker) {
					return (DatePicker) child;
				} else if (child instanceof ViewGroup) {
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}

	@Override
	public void setTitle(CharSequence title) {
        if (null == titleMsg) return;
        String showMsg = titleMsg + title.toString();
		super.setTitle(showMsg);

	}

	public void hideYear() {
		isHideYear = true;
		if (isHideDayOfMonth && isHideMonth && isHideYear) {
			isHideDayOfMonth = false;
			isHideMonth = false;
			isHideYear = false;
		}
	}

	public void hideMonth() {
		isHideMonth = true;
		if (isHideDayOfMonth && isHideMonth && isHideYear) {
			isHideDayOfMonth = false;
			isHideMonth = false;
			isHideYear = false;
		}
	}

	public void hideDayOfMonth() {
		isHideDayOfMonth = true;
		if (isHideDayOfMonth && isHideMonth && isHideYear) {
			isHideDayOfMonth = false;
			isHideMonth = false;
			isHideYear = false;
		}
	}

}
