
package org.meimen.meimen.ui.fragments;

import org.meimen.meimen.R;
import org.meimen.meimen.ui.activity.MainActivity;
import org.meimen.meimen.utils.ViewUtils;

import rx.Subscription;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

public class LoginFragment extends BaseFragment {

    private Subscription mStartBtnSubscription;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        mStartBtnSubscription = RxView.clicks(ViewUtils.findViewById(v, R.id.v_btn))
                .subscribe(onClickEvent -> {
                    ((MainActivity) getActivity()).pushFragment(ExerciseFragment.newInstance());
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
