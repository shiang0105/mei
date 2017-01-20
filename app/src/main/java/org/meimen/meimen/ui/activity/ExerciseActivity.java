
package org.meimen.meimen.ui.activity;

import org.meimen.meimen.R;
import org.meimen.meimen.ui.fragments.BaseFragment;
import org.meimen.meimen.ui.fragments.ExerciseFragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ExerciseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ExerciseFragment fragment = ExerciseFragment.newInstance();
        transaction.replace(R.id.fragment_container, fragment, String.valueOf(fragment.hashCode()));
        transaction.addToBackStack(String.valueOf(fragment.hashCode()));
        transaction.commit();
    }

    public void pushFragment(BaseFragment newFragment, int enter, int exit,
            int popEnter, int popExit) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                enter, exit, popEnter, popExit);
        transaction.replace(R.id.fragment_container, newFragment, String.valueOf(newFragment.hashCode()));
        transaction.addToBackStack(String.valueOf(newFragment.hashCode()));
        transaction.commit();
    }

    public void pushFragment(BaseFragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment, String.valueOf(newFragment.hashCode()));
        transaction.addToBackStack(String.valueOf(newFragment.hashCode()));
        transaction.commit();
    }

    public void popFragment() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        if (getTopFragment() != null && getTopFragment().onBackPress()) {
            return;
        }

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            // additional code
        } else if (count == 1) {
            finish();
        } else {
            getFragmentManager().popBackStack();

        }

    }

    public BaseFragment getTopFragment() {
        BaseFragment resultFragment = null;
        if (getSupportFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = getFragmentManager().getBackStackEntryAt(
                    getFragmentManager().getBackStackEntryCount() - 1);
            if (entry != null) {
                String name = entry.getName();
                resultFragment = (BaseFragment) getFragmentManager().findFragmentByTag(name);
            }
        }
        return resultFragment;
    }
}
