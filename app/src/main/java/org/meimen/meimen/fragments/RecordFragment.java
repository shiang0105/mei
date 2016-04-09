
package org.meimen.meimen.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.meimen.meimen.R;
import org.meimen.meimen.activity.ExerciseActivity;
import org.meimen.meimen.database.DatabaseHelper;
import org.meimen.meimen.ui.MeiMenCalendarView;
import org.meimen.meimen.utils.CalendarUtils;
import org.meimen.meimen.utils.ViewUtils;

import rx.Subscription;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecordFragment extends BaseFragment {

    private Subscription mStartBtnSubscription;

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record, container, false);
        ViewUtils.findViewById(v, R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExerciseActivity) getActivity()).popFragment();
            }
        });

        ViewPager viewPager = ViewUtils.findViewById(v, R.id.vp_calendar);
        Calendar today = Calendar.getInstance();
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MeiMenCalendarView cv = new MeiMenCalendarView(getActivity());
            cv.setCalendar(today);
            LongSparseArray records = DatabaseHelper.getsInstance(getActivity()).getDataSource().getDurations("admin");
            for (int j = 0; j < records.size(); j++) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(records.keyAt(j));
                cv.mark(c);
            }
            today = CalendarUtils.getTheFirstDayOfPreviousMonth(today);
            views.add(0, cv);
        }

        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(views.size() - 1);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mStartBtnSubscription != null) {
            mStartBtnSubscription.unsubscribe();
        }
    }

    public static class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mListViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

}
