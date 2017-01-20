
package org.meimen.meimen.ui.fragments;

import org.meimen.meimen.R;
import org.meimen.meimen.ui.activity.ExerciseActivity;
import org.meimen.meimen.utils.ViewUtils;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

public class ExerciseSelectFragment extends BaseFragment implements View.OnClickListener {

    private Subscription mStartBtnSubscription;
    private Subscription mUpdateLayoutSubscription;
    private boolean mIsStart = false;
    private MediaPlayer mediaPlayer;
    private TextView mTimerTv;

    public static ExerciseSelectFragment newInstance() {
        return new ExerciseSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_exercise_select, container, false);
        mTimerTv = ViewUtils.findViewById(v, R.id.tv_timer);
        mStartBtnSubscription = RxView.clicks(ViewUtils.findViewById(v, R.id.layout_btn))
                .mergeWith(RxView.clicks(ViewUtils.findViewById(v, R.id.tv_thirty)))
                .mergeWith(RxView.clicks(ViewUtils.findViewById(v, R.id.tv_twenty)))
                .mergeWith(RxView.clicks(ViewUtils.findViewById(v, R.id.tv_ten)))
                .mergeWith(RxView.clicks(ViewUtils.findViewById(v, R.id.btn_footer)))
                .subscribe(onClickEvent -> {
                    if (!mIsStart) {
                        ViewUtils.findViewById(v, R.id.btn_footer).setVisibility(View.VISIBLE);
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.test_audio);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mIsStart = false;
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }
                            });
                        }

                        mediaPlayer.start();
                        mIsStart = true;
                        mTimerTv.setVisibility(View.VISIBLE);
                        ViewUtils.findViewById(v, R.id.layout_click_to_invisible).setVisibility(View.GONE);
                    } else {
                        ViewUtils.findViewById(v, R.id.btn_footer).setVisibility(View.GONE);
                        mediaPlayer.pause();
                        mIsStart = false;

                        mTimerTv.setVisibility(View.GONE);
                        ViewUtils.findViewById(v, R.id.layout_click_to_invisible).setVisibility(View.VISIBLE);
                    }
                });
        mUpdateLayoutSubscription = Observable.just(1).repeat().observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            if (mediaPlayer != null) {

                long time = mediaPlayer.getCurrentPosition();
                long duration = mediaPlayer.getDuration();
                time = Math.round((double) (duration - time) / (double) 1000);

                String result = String.format("%02d:%02d", time / 60, time % 60);
                mTimerTv.setText(result);
            }
        });

        ViewUtils.findViewById(v, R.id.layout_close).setOnClickListener(this);
        ViewUtils.findViewById(v, R.id.tv_record).setOnClickListener(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mStartBtnSubscription != null) {
            mStartBtnSubscription.unsubscribe();
        }
        if (mUpdateLayoutSubscription != null) {
            mUpdateLayoutSubscription.unsubscribe();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
                getActivity().finish();
                break;
            case R.id.tv_record:
                ((ExerciseActivity) getActivity()).pushFragment(RecordFragment.newInstance());
                break;
            default:
                break;
        }
    }

}
