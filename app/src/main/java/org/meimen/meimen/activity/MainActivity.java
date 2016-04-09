
package org.meimen.meimen.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.meimen.meimen.R;
import org.meimen.meimen.database.DatabaseHelper;
import org.meimen.meimen.fragments.BaseFragment;
import org.meimen.meimen.fragments.RecordFragment;
import org.meimen.meimen.fragments.WelcomeFragment;
import org.meimen.meimen.manager.AccountManager;
import org.meimen.meimen.manager.SidebarManager;
import org.meimen.meimen.model.UserInfo;
import org.meimen.meimen.utils.ViewUtils;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_CODE = 1;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;

    private SidebarManager.SidebarConfig mSidebarConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mDrawerLayout = ViewUtils.findViewById(this, R.id.drawer_layout);

        initToolbar();
        initSidebar();

        // ActionbarManager.getInstance().init(mToolbar);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, WelcomeFragment.newInstance());
        transaction.commit();

        createFakeData();
        DatabaseHelper.getsInstance(this).getDataSource().getAllExerciseRecords("admin");
        DatabaseHelper.getsInstance(this).getDataSource().getDurations("admin");

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
                                updateSidebarProfile();
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

    private void createFakeData() {
        DatabaseHelper.getsInstance(this).getDataSource().removeExerciseRecords("admin");
        DatabaseHelper.getsInstance(this).getDataSource().insertExerciseRecords("admin", 1445961601000l, 10);
        DatabaseHelper.getsInstance(this).getDataSource().insertExerciseRecords("admin", 1445961621000l, 20);
        DatabaseHelper.getsInstance(this).getDataSource().insertExerciseRecords("admin", 1445961631000l, 30);
        DatabaseHelper.getsInstance(this).getDataSource().insertExerciseRecords("admin", 1445875201000l, 50);
        DatabaseHelper.getsInstance(this).getDataSource().insertExerciseRecords("admin", 1445702401000l, 70);
        DatabaseHelper.getsInstance(this).getDataSource().insertExerciseRecords("admin", 1445702402000l, 1);

        DatabaseHelper.getsInstance(this).getDataSource().insertExerciseRecords("admin", 1441123199000l, 10);

    }

    private void initSidebar() {
        ViewGroup sidebarV = ViewUtils.findViewById(this, R.id.layout_sidebar);
        mSidebarConfig = new SidebarManager.SidebarConfig(sidebarV)
                .append(SidebarManager.Function.PROFILE)
                .append(SidebarManager.Function.DIVIDER)
                .append(SidebarManager.Function.MAIN_PAGE)
                .append(SidebarManager.Function.RECORD)
                .append(SidebarManager.Function.STORY)
                .append(SidebarManager.Function.QNA)
                .append(SidebarManager.Function.RESTAURANT)
                .append(SidebarManager.Function.DIVIDER)
                .append(SidebarManager.Function.SETTING)
                .append(SidebarManager.Function.LOGOUT)
                .append(SidebarManager.Function.LOGO);
        SidebarManager.getInstance().init(mSidebarConfig);
        SidebarManager.getInstance().setListener(new SidebarManager.SidebarItemClickListener() {
            @Override
            public void onSidebarItemClick(SidebarManager.Function function, Bundle bundle) {
                switch (function) {
                    case PROFILE:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_CODE);
                        break;
                    case LOGOUT:
                        AccountManager.getInstance().logout();
                        SidebarManager.getInstance().toggleToLogoutStyle();
                        break;
                    default:
                        ViewUtils.showToast(function.toString());
                        break;
                }
                mDrawerLayout.closeDrawers();
            }
        });
        updateSidebarProfile();
    }

    private void updateSidebarProfile() {
        if (AccountManager.getInstance().isLogin()) {
            SidebarManager.getInstance().toggleToLoginStyle();
        } else {
            SidebarManager.getInstance().toggleToLogoutStyle();
        }
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); // 设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout mDrawerLayout = ViewUtils.findViewById(this, R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.abc_action_bar_up_description, R.string.abc_activity_chooser_view_see_all) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // mAnimationDrawable.stop();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // mAnimationDrawable.start();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void pushFragment(BaseFragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
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
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            // additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN_CODE:
                updateSidebarProfile();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.item_open_calendar) {
            pushFragment(RecordFragment.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
