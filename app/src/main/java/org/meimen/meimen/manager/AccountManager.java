
package org.meimen.meimen.manager;

import java.util.Collection;

import org.meimen.meimen.model.UserInfo;

import android.app.Activity;
import android.net.Uri;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

public class AccountManager {

    private static AccountManager sInstance;

    private UserInfo mLoginUserInfo = new UserInfo();

    public static AccountManager getInstance() {
        if (sInstance == null) {
            synchronized (AccountManager.class) {
                if (sInstance == null) {
                    sInstance = new AccountManager();
                }
            }
        }
        return sInstance;
    }

    public UserInfo getLoginUserInfo() {
        if (mLoginUserInfo == null) {
            mLoginUserInfo = new UserInfo();
        }
        return mLoginUserInfo;
    }

    public Uri getProfileUrl() {
        return Profile.getCurrentProfile() == null ? null : Profile.getCurrentProfile().getProfilePictureUri(100, 100);
    }

    public void setLoginUserInfo(UserInfo mLoginUserInfo) {
        this.mLoginUserInfo = mLoginUserInfo;
    }

    public boolean isLogin() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    public void login(Activity activity, Collection permission, CallbackManager cbManager, FacebookCallback fcb) {
        LoginManager.getInstance().registerCallback(cbManager, fcb);
        LoginManager.getInstance().logInWithReadPermissions(activity, permission);
    }

    public void logout() {
        mLoginUserInfo = null;
        LoginManager.getInstance().logOut();
    }
}
