
package org.meimen.meimen.fragments;

import java.util.Calendar;

import org.meimen.meimen.R;
import org.meimen.meimen.activity.ExerciseActivity;
import org.meimen.meimen.receiver.AlarmReceiver;
import org.meimen.meimen.utils.ViewUtils;

import rx.Subscription;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

public class WelcomeFragment extends BaseFragment {

    private Subscription mStartBtnSubscription;

    int count = 1;

    AlarmReceiver alarm = new AlarmReceiver();
    boolean set = false;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        TextView dayTv = ViewUtils.findViewById(v, R.id.tv_day);
        TextView weekDayTv = ViewUtils.findViewById(v, R.id.tv_weekday);

        Calendar today = Calendar.getInstance();
        dayTv.setText(today.get(Calendar.DAY_OF_MONTH) + "");
        String[] weekDay = {
                "", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"
        };
        weekDayTv.setText(weekDay[today.get(Calendar.DAY_OF_WEEK)]);

        mStartBtnSubscription = RxView.clicks(ViewUtils.findViewById(v, R.id.v_btn))
                .subscribe(onClickEvent -> {
                    Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                    startActivity(intent);

                    /*
                     * if(!set){ alarm.setAlarm(MeiMenApplication.getContext()); }else{
                     * alarm.cancelAlarm(MeiMenApplication.getContext()); } set = !set;
                     */

                });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mStartBtnSubscription != null) {
            mStartBtnSubscription.unsubscribe();
        }
    }

}
