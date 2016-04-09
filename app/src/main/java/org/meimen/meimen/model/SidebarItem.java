
package org.meimen.meimen.model;

/**
 * Created by yst on 9/12/15.
 */
public class SidebarItem {
    private int mIconId;
    private String mTitle;

    public SidebarItem(int iconId, String title) {
        mIconId = iconId;
        mTitle = title;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int mIconId) {
        this.mIconId = mIconId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
