
package org.meimen.meimen.manager;

import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by yst on 9/15/15.
 */
public class ActionbarManager {

    private static ActionbarManager sActionbarManager;

    private Toolbar mToolbar;

    public static ActionbarManager getInstance() {
        if (sActionbarManager == null) {
            synchronized (ActionbarManager.class) {
                if (sActionbarManager == null) {
                    sActionbarManager = new ActionbarManager();
                }
            }
        }
        return sActionbarManager;
    }

    public void init(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    public void hideActionbar(){
        mToolbar.setVisibility(View.GONE);
    }

    public void showActionbar(){
        mToolbar.setVisibility(View.VISIBLE);
    }

    public int getVisibility(){
        return mToolbar.getVisibility();
    }
}
