
package org.meimen.meimen.fragments;

import org.meimen.meimen.R;
import org.meimen.meimen.activity.ExerciseActivity;
import org.meimen.meimen.ui.LineChartView;
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
import android.widget.Button;
import android.widget.TextView;

public class ExerciseFragment extends BaseFragment implements View.OnClickListener {

    public enum ExercisePageStatus {
        SHOW_RECORD, SELECT_TIME, START_EXERCISE
    }

    private ExercisePageStatus mStatus = ExercisePageStatus.SHOW_RECORD;

    private Subscription mUpdateLayoutSubscription;
    private boolean mIsStart = false;
    private MediaPlayer mediaPlayer;
    private TextView mTimerTv;
    private TextView mTenTv;
    private TextView mTwentyTv;
    private TextView mThirtyTv;
    private Button mPauseFooter;
    private ViewGroup mInvisibleViewGroup;
    private LineChartView mSevendatRecordLCV;
    private View mRedPlusView;
    private View mGoRecordView;

    public static ExerciseFragment newInstance() {
        return new ExerciseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_excersie, container, false);

        mTimerTv = ViewUtils.findViewById(v, R.id.tv_timer);
        mPauseFooter = ViewUtils.findViewById(v, R.id.btn_footer);
        mInvisibleViewGroup = ViewUtils.findViewById(v, R.id.layout_click_to_invisible);
        mTenTv = ViewUtils.findViewById(v, R.id.tv_ten);
        mTwentyTv = ViewUtils.findViewById(v, R.id.tv_twenty);
        mThirtyTv = ViewUtils.findViewById(v, R.id.tv_thirty);
        mSevendatRecordLCV = ViewUtils.findViewById(v, R.id.lcv_record);
        mRedPlusView = ViewUtils.findViewById(v, R.id.tv_red_btn);
        mGoRecordView = ViewUtils.findViewById(v, R.id.tv_record);

        mUpdateLayoutSubscription = Observable.just(1).repeat().observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            if (mediaPlayer != null) {

                long time = mediaPlayer.getCurrentPosition();
                long duration = mediaPlayer.getDuration();
                time = Math.round((double) (duration - time) / (double) 1000);

                String result = String.format("%02d:%02d", time / 60, time % 60);
                mTimerTv.setText(result);
            }
        });

        mTenTv.setOnClickListener(this);
        mTwentyTv.setOnClickListener(this);
        mThirtyTv.setOnClickListener(this);
        ViewUtils.findViewById(v, R.id.btn_footer).setOnClickListener(this);
        ViewUtils.findViewById(v, R.id.layout_btn).setOnClickListener(this);
        ViewUtils.findViewById(v, R.id.layout_close).setOnClickListener(this);
        ViewUtils.findViewById(v, R.id.tv_record).setOnClickListener(this);

        switchStatus(mStatus);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUpdateLayoutSubscription != null) {
            mUpdateLayoutSubscription.unsubscribe();
        }
        cancelPlayer();
    }

    private void switchStatus(ExercisePageStatus status) {
        switch (status) {
            case SHOW_RECORD:
                mGoRecordView.setVisibility(View.VISIBLE);
                mInvisibleViewGroup.setVisibility(View.VISIBLE);
                mTimerTv.setVisibility(View.GONE);
                mTenTv.setVisibility(View.GONE);
                mTwentyTv.setVisibility(View.GONE);
                mThirtyTv.setVisibility(View.GONE);
                mSevendatRecordLCV.setVisibility(View.VISIBLE);
                mRedPlusView.setVisibility(View.VISIBLE);
                mPauseFooter.setVisibility(View.GONE);
                cancelPlayer();
                break;
            case SELECT_TIME:
                mGoRecordView.setVisibility(View.VISIBLE);
                mPauseFooter.setVisibility(View.GONE);
                mInvisibleViewGroup.setVisibility(View.VISIBLE);
                mTimerTv.setVisibility(View.GONE);
                mTenTv.setVisibility(View.VISIBLE);
                mTwentyTv.setVisibility(View.VISIBLE);
                mThirtyTv.setVisibility(View.VISIBLE);
                mSevendatRecordLCV.setVisibility(View.GONE);
                mRedPlusView.setVisibility(View.GONE);
                cancelPlayer();
                break;
            case START_EXERCISE:
                mGoRecordView.setVisibility(View.GONE);
                mPauseFooter.setVisibility(View.VISIBLE);
                mInvisibleViewGroup.setVisibility(View.GONE);
                mTimerTv.setVisibility(View.VISIBLE);
                mTenTv.setVisibility(View.GONE);
                mTwentyTv.setVisibility(View.GONE);
                mThirtyTv.setVisibility(View.GONE);
                mSevendatRecordLCV.setVisibility(View.GONE);
                mRedPlusView.setVisibility(View.GONE);
                break;
        }
        mStatus = status;
    }

    @Override
    public boolean onBackPress() {
        boolean handled = false;

        switch (mStatus) {
            case SHOW_RECORD:
                break;
            case SELECT_TIME:
                switchStatus(ExercisePageStatus.SHOW_RECORD);
                handled = true;
                break;
            case START_EXERCISE:
                switchStatus(ExercisePageStatus.SELECT_TIME);
                handled = true;
                break;
        }
        return handled;
    }

    private void cancelPlayer() {
        mIsStart = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (!isFragmentValid()) {
            return;
        }

        switch (v.getId()) {
            case R.id.layout_btn:
                switchStatus(ExercisePageStatus.SELECT_TIME);
                break;
            case R.id.layout_close:
                getActivity().finish();
                break;
            case R.id.tv_record:
                ((ExerciseActivity) getActivity()).pushFragment(
                        RecordFragment.newInstance(),
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out);
                break;
            case R.id.tv_ten:
            case R.id.tv_twenty:
            case R.id.tv_thirty:
                switchStatus(ExercisePageStatus.START_EXERCISE);
            case R.id.btn_footer:
                if (!mIsStart) {
                    mPauseFooter.setText("暫停");
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.test_audio);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                cancelPlayer();
                            }
                        });
                    }

                    mediaPlayer.start();
                    mIsStart = true;
                    mInvisibleViewGroup.setVisibility(View.GONE);
                } else {
                    mPauseFooter.setText("繼續");
                    mediaPlayer.pause();
                    mIsStart = false;
                    mInvisibleViewGroup.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }
}
