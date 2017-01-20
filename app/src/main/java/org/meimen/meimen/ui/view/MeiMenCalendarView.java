
package org.meimen.meimen.ui.view;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.meimen.meimen.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by yst on 9/8/15.
 */
public class MeiMenCalendarView extends LinearLayout {

    private static String TAG = MeiMenCalendarView.class.getSimpleName();

    // private int mHeight = 140;
    private Calendar mToday;
    private Calendar mFirstDayOfMonth;
    private String[] weekDay = {
            "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"
    };
    private static SparseArray<String> weekDayName = new SparseArray<String>() {
        {
            append(Calendar.SUNDAY, "SUN");
            append(Calendar.MONDAY, "MON");
            append(Calendar.TUESDAY, "TUE");
            append(Calendar.WEDNESDAY, "WED");
            append(Calendar.THURSDAY, "THU");
            append(Calendar.FRIDAY, "FRI");
            append(Calendar.SATURDAY, "SAT");
        }
    };

    public MeiMenCalendarView(Context context) {
        this(context, null, 0);
    }

    public MeiMenCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeiMenCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCalendar(Calendar calendar) {
        removeAllViews();
        mToday = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        mFirstDayOfMonth = new GregorianCalendar(mToday.get(Calendar.YEAR), mToday.get(Calendar.MONTH), 1);
        setView(mFirstDayOfMonth);
    }

    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mToday = Calendar.getInstance();
        mFirstDayOfMonth = new GregorianCalendar(mToday.get(Calendar.YEAR), mToday.get(Calendar.MONTH), 1);
        setView(mFirstDayOfMonth);
    }

    private void setView(Calendar firstDay) {
        setOrientation(VERTICAL);
        LinearLayout titleLayout = new LinearLayout(getContext());
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1));
        titleLayout.setOrientation(HORIZONTAL);
        addView(titleLayout);

        for (int i = 0; i < 7; i++) {
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
            tv.setText(weekDay[i]);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            titleLayout.addView(tv);
        }

        int daysInMonth = firstDay.getActualMaximum(Calendar.DAY_OF_MONTH);
        int mWeeksInMonth = firstDay.getActualMaximum(Calendar.WEEK_OF_MONTH);

        for (int i = 0; i < mWeeksInMonth; i++) {
            LinearLayout monthDayLayout = new LinearLayout(getContext());
            monthDayLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0, 1));
            monthDayLayout.setOrientation(HORIZONTAL);
            addView(monthDayLayout);
        }

        // 前一個月的
        int emptyNum = (firstDay.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.SUNDAY) % 7;
        for (int i = 0; i < emptyNum; i++) {
            LinearLayout parent = (LinearLayout) getChildAt(1);
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.MATCH_PARENT, 1));
            parent.addView(tv);
        }

        // 當月
        for (int i = 0; i < daysInMonth; i++) {
            int parentId = (i + emptyNum) / 7 + 1;
            LinearLayout parent = (LinearLayout) getChildAt(parentId);
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.MATCH_PARENT, 1));
            tv.setText((i + 1) + "");
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            parent.addView(tv);
        }

        // 下一個月
        int leftEmpty = (7 - ((emptyNum + daysInMonth) % 7)) % 7;
        for (int i = 0; i < leftEmpty; i++) {
            LinearLayout parent = (LinearLayout) getChildAt(mWeeksInMonth);
            TextView view = new TextView(getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.MATCH_PARENT, 1));
            parent.addView(view);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mToday != null) {
            int weekDayInMonth = mToday.getActualMaximum(Calendar.WEEK_OF_MONTH);
            // header + weekDay
            int totalRow = weekDayInMonth + 1;
            float totalWidth = getMeasuredWidth();

            super.onMeasure(MeasureSpec.makeMeasureSpec((int) totalWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec((int) (totalWidth / weekDay.length * totalRow), MeasureSpec.EXACTLY));

            // setMeasuredDimension((int) totalWidth, (int) (totalWidth / weekDay.length * totalRow));
        }
    }

    public void mark(Calendar calendar) {
        if (calendar == null || mToday == null || calendar.get(Calendar.YEAR) != mToday.get(Calendar.YEAR)
                || calendar.get(Calendar.MONTH) != mToday.get(Calendar.MONTH)) {
            return;
        }
        Log.i(TAG,
                "mark : " + calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + 1 + "/"
                        + calendar.get(Calendar.DATE));

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int emptyNum = (mFirstDayOfMonth.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.SUNDAY) % 7;

        int parentId = (dayOfMonth + emptyNum - 1) / 7 + 1;

        LinearLayout parent = (LinearLayout) getChildAt(parentId);
        parent.getChildAt((dayOfMonth + emptyNum - 1) % 7)
                .setBackground(getResources().getDrawable(R.drawable.circle_grey));

    }

}
