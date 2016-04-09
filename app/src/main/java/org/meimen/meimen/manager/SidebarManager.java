
package org.meimen.meimen.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.meimen.meimen.R;
import org.meimen.meimen.model.SidebarItem;
import org.meimen.meimen.model.UserInfo;
import org.meimen.meimen.ui.CircledImageView;
import org.meimen.meimen.utils.ViewUtils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SidebarManager implements View.OnClickListener {

    private static SidebarManager sSidebarManager;

    private View mProfileView;

    private View mLogoutView;

    private SidebarConfig config;

    private SidebarItemClickListener mListener;

    public static SidebarManager getInstance() {
        if (sSidebarManager == null) {
            synchronized (SidebarManager.class) {
                if (sSidebarManager == null) {
                    sSidebarManager = new SidebarManager();
                }
            }
        }
        return sSidebarManager;
    }

    public void init(SidebarConfig config) {
        this.config = config;
        setConfig(config);
    }

    public void setConfig(SidebarConfig config) {
        this.config = config;
        config.mSidebarViewGroup.get().removeAllViews();
        for (int i = 0; i < config.functions.size(); i++) {
            addSidebarElement(config.functions.get(i));
        }
    }

    private void addSidebarElement(Function func) {
        if (config != null && config.mSidebarViewGroup != null) {
            ViewGroup sidebarViewGroup = config.mSidebarViewGroup.get();
            if (sidebarViewGroup != null) {

                LayoutInflater inflater = LayoutInflater.from(sidebarViewGroup.getContext());
                switch (func) {
                    case PROFILE:
                        mProfileView = inflater.inflate(R.layout.sidebar_login_status, sidebarViewGroup, false);
                        sidebarViewGroup.addView(mProfileView);
                        mProfileView.setTag(func);
                        mProfileView.setOnClickListener(this);
                        if (AccountManager.getInstance().isLogin()) {
                            toggleToLoginStyle();
                        } else {
                            toggleToLogoutStyle();
                        }
                        break;
                    case MAIN_PAGE:
                        addSidebarElement(sidebarViewGroup, new SidebarItem(R.mipmap.icon_calendar,
                                "首頁"), func);
                        break;
                    case RECORD:
                        addSidebarElement(sidebarViewGroup, new SidebarItem(R.mipmap.icon_calendar,
                                "平甩記錄"), func);
                        break;
                    case STORY:
                        addSidebarElement(sidebarViewGroup, new SidebarItem(R.mipmap.icon_group,
                                "見證故事"), func);
                        break;
                    case QNA:
                        addSidebarElement(sidebarViewGroup, new SidebarItem(R.mipmap.icon_group,
                                "常見問答"), func);
                        break;
                    case RESTAURANT:
                        addSidebarElement(sidebarViewGroup, new SidebarItem(R.mipmap.icon_lists,
                                "捐助公益"), func);
                    case CHARITY:
                        addSidebarElement(sidebarViewGroup, new SidebarItem(R.mipmap.icon_lists,
                                "捐助公益"), func);
                        break;
                    case SETTING:
                        addSidebarElement(sidebarViewGroup, new SidebarItem(R.mipmap.icon_settings,
                                "設定"), func);
                        break;
                    case LOGOUT:
                        mLogoutView = addSidebarElement(sidebarViewGroup, new SidebarItem(
                                R.mipmap.icon_logout,
                                "登出"), func);
                        mLogoutView.setVisibility(AccountManager.getInstance().isLogin() ? View.GONE : View.VISIBLE);
                        break;
                    case DIVIDER:
                        View divider = inflater.inflate(R.layout.divider_grey_gradient, sidebarViewGroup, false);
                        sidebarViewGroup.addView(divider);
                        break;
                    case LOGO:
                        View logo = inflater.inflate(R.layout.sidebar_logo, sidebarViewGroup, false);
                        sidebarViewGroup.addView(logo);
                    default:
                        break;
                }
            }
        }

    }

    private View addSidebarElement(ViewGroup parent, SidebarItem item, Function func) {
        View itemV = SidebarItemFactory.createSidebarItem(parent, item);
        ViewGroup sidebarViewGroup = config.mSidebarViewGroup.get();
        if (sidebarViewGroup != null) {
            sidebarViewGroup.addView(itemV);
            itemV.setTag(func);
            itemV.setOnClickListener(this);
        } else {
            Log.e("john", "side bar add error");
        }
        return itemV;
    }

    public void toggleToLoginStyle() {
        if (AccountManager.getInstance().isLogin()) {
            Uri uri = AccountManager.getInstance().getProfileUrl();
            UserInfo userInfo = AccountManager.getInstance().getLoginUserInfo();
            if (userInfo != null) {
                ViewUtils.findViewById(mProfileView, R.id.layout_login).setVisibility(View.VISIBLE);
                ViewUtils.findViewById(mProfileView, R.id.layout_nonlogin).setVisibility(View.GONE);

                CircledImageView profileIv = ViewUtils.findViewById(mProfileView, R.id.civ_profile);
                TextView nameTv = ViewUtils.findViewById(mProfileView, R.id.tv_name);
                TextView mailTv = ViewUtils.findViewById(mProfileView, R.id.tv_mail);
                if (uri != null) {
                    profileIv.loadURL(uri.toString());
                }
                nameTv.setText(userInfo.getName() == null ? "" : userInfo.getName());
                mailTv.setText(userInfo.getEmail() == null ? "" : userInfo.getEmail());

                if (mLogoutView != null) {
                    mLogoutView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void toggleToLogoutStyle() {
        ViewUtils.findViewById(mProfileView, R.id.layout_login).setVisibility(View.GONE);
        ViewUtils.findViewById(mProfileView, R.id.layout_nonlogin).setVisibility(View.VISIBLE);

        final ImageView img = ViewUtils.findViewById(mProfileView, R.id.iv_icon);
        // img.setImageDrawable(img.getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));
        ((TextView) ViewUtils.findViewById(mProfileView, R.id.tv_title)).setText("登入");
        if (mLogoutView != null) {
            mLogoutView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Function function = (Function) v.getTag();

        if (mListener != null) {
            mListener.onSidebarItemClick(function, null);
        }
    }

    public void setListener(SidebarItemClickListener mListener) {
        this.mListener = mListener;
    }

    public enum Function {
        PROFILE, MAIN_PAGE, RECORD, STORY, QNA, RESTAURANT, CHARITY, SETTING, LOGOUT, DIVIDER, LOGO
    }

    public interface SidebarItemClickListener {
        void onSidebarItemClick(Function function, Bundle bundle);
    }

    public static class SidebarItemFactory {
        public static View createSidebarItem(ViewGroup parent, final SidebarItem item) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sidebar_item, parent, false);
            final ImageView iconIv = ViewUtils.findViewById(v, R.id.iv_icon);
            TextView titleTv = ViewUtils.findViewById(v, R.id.tv_title);
            final Resources res = parent.getResources();
            new AsyncTask<Object, Void, Object>() {

                Drawable drawable;

                @Override
                protected Object doInBackground(Object[] params) {
                    drawable = res.getDrawable(item.getIconId());
                    return null;
                }

                @Override
                public void onPostExecute(Object result) {
                    iconIv.setImageDrawable(drawable);
                }

            }.execute();
            //iconIv.setImageDrawable(parent.getResources().getDrawable(item.getIconId()));
            titleTv.setText(item.getTitle());
            return v;
        }
    }

    public static class SidebarConfig {
        private ArrayList<Function> functions = new ArrayList<>();
        private WeakReference<ViewGroup> mSidebarViewGroup;

        public SidebarConfig(ViewGroup sidebarViewGroup) {
            mSidebarViewGroup = new WeakReference<>(sidebarViewGroup);
        }

        public SidebarConfig append(Function f) {
            functions.add(f);
            return this;
        }

        public SidebarConfig remove(Function f) {
            functions.remove(f);
            return this;
        }
    }
}
