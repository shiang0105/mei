
package org.meimen.meimen.fragments;

import android.app.Fragment;
import android.os.Bundle;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onBackPress() {
        return false;
    }

    public boolean isFragmentValid() {
        if (!isAdded() || getActivity() == null || isDetached() || isRemoving()) {
            return false;
        }
        return true;
    }

}
