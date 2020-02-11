package viroyal.com.base.widget.CustomCalendarPopwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import viroyal.com.base.R;

public class CalendarPopwindow implements CalendarView.OnCalendarSelectListener,
    CalendarView.OnYearChangeListener{

    private Context context;
    private PopupWindow popupWindow;
    private String TAG ="CalendarPopwindow";
    private TextView mTextMonthDay;
    private ImageView mLastMonth, mNextMonth;

    private CalendarView mCalendarView;

    private int mYear;
    private CalendarLayout mCalendarLayout;

    public CalendarPopwindow(Context context ) {
        this.context = context;

    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    public void initCalendar() {
        View inflate = LayoutInflater.from(context).inflate(R.layout.popup_calendar,null,false);
        popupWindow = new PopupWindow(inflate, WindowManager.LayoutParams.MATCH_PARENT, 750, false);
        initView(inflate);
        initData();
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);


    }

    @SuppressLint("SetTextI18n")
    private void initView(View inflate) {
        mTextMonthDay = (TextView) inflate.findViewById(R.id.tv_month_day);
        mNextMonth = (ImageView) inflate.findViewById(R.id.iv_next);
        mLastMonth = (ImageView) inflate.findViewById(R.id.iv_previous);
        mTextMonthDay = (TextView) inflate.findViewById(R.id.tv_month_day);
        mCalendarView = (CalendarView) inflate.findViewById(R.id.calendarView);
        mLastMonth.setOnClickListener(view -> mCalendarView.scrollToPre());
        mNextMonth.setOnClickListener(view -> mCalendarView.scrollToNext());
//        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCalendarView.scrollToCurrent();
        //mCalendarView.addSchemeDate(getSchemeCalendar(2019, 6, 1, 0xFF40db25, "假"));
//                int year = 2019;
//                int month = 6;
//                Map<String, Calendar> map = new HashMap<>();
//                map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
//                        getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
//                map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
//                        getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
//                map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
//                        getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
//                map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
//                        getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
//                mCalendarView.addSchemeDate(map);
//            }
//        });
        mCalendarLayout = (CalendarLayout) inflate.findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurYear() + context.getResources().getString(R.string.pop_year)+mCalendarView.getCurMonth() + context.getResources().getString(R.string.pop_month));
    }

    private void initData() {
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(TempHolidayUtil.map());

    }

    //展示日历
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    public void showCalendar(View anchor) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            mCalendarView.scrollToCurrent();
            popupWindow.showAsDropDown(anchor, 0, 0);
        }

    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextMonthDay.setText(calendar.getYear() + context.getResources().getString(R.string.pop_year) + calendar.getMonth() + context.getResources().getString(R.string.pop_month));
    }
}
