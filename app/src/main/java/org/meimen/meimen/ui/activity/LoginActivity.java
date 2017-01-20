
package org.meimen.meimen.ui.activity;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.meimen.meimen.R;
import org.meimen.meimen.manager.AccountManager;
import org.meimen.meimen.model.UserInfo;
import org.meimen.meimen.utils.ViewUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

public class LoginActivity extends FragmentActivity {

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View v = ViewUtils.findViewById(this, R.id.btn_custom_login);

        mCallbackManager = CallbackManager.Factory.create();
        final FacebookCallback fbManager = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserInfo();
            }

            @Override
            public void onCancel() {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }

            @Override
            public void onError(FacebookException exception) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        };

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager.getInstance().login(LoginActivity.this, Arrays.asList("user_friends", "email"), mCallbackManager,
                        fbManager);
            }
        });
    }

    private void getUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        if (object != null) {
                            UserInfo user = new UserInfo();
                            try {
                                user.setName(object.getString("name"));
                                user.setEmail(object.getString("email"));
                                user.setUserId(object.getString("id"));
                                AccountManager.getInstance().setLoginUserInfo(user);
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_OK, resultIntent);

                                finish();
                            } catch (JSONException e) {
                                AccountManager.getInstance().logout();
                                // todo error handling
                            }
                        } else {
                            AccountManager.getInstance().logout();
                            // todo error handling
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email, id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
