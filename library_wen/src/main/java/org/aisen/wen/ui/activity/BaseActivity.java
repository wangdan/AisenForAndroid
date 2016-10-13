package org.aisen.wen.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import org.aisen.wen.R;
import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.IWorkTask;
import org.aisen.wen.component.network.task.TaskManager;
import org.aisen.wen.support.utils.Logger;
import org.aisen.wen.support.utils.ViewUtils;
import org.aisen.wen.ui.fragment.ABaseFragment;
import org.aisen.wen.ui.widget.AsToolbar;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;

/**
 * Created by wangdan on 15-1-16.
 */
public class BaseActivity extends AppCompatActivity implements ITaskManager, AsToolbar.OnToolbarDoubleClick {

    static final String TAG = "Activity-Base";

    private static Class<? extends BaseActivityHelper> mHelperClass;
    private BaseActivityHelper mHelper;

    private int theme = 0;// 当前界面设置的主题

    private TaskManager taskManager;

    private boolean isDestory;

    // 当有Fragment Attach到这个Activity的时候，就会保存
    private Map<String, WeakReference<ABaseFragment>> fragmentRefs;

    private static BaseActivity runningActivity;

    private View rootView;

    private Toolbar mToolbar;

    public static BaseActivity getRunningActivity() {
        return runningActivity;
    }

    public static void setRunningActivity(BaseActivity activity) {
        runningActivity = activity;
    }

    public static void setHelper(Class<? extends BaseActivityHelper> clazz) {
        mHelperClass = clazz;
    }

    protected int configTheme() {
        if (mHelper != null) {
            int theme = mHelper.configTheme();
            if (theme > 0)
                return theme;
        }

        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mHelper == null) {
            try {
                if (mHelperClass != null) {
                    mHelper = mHelperClass.newInstance();
                    mHelper.bindActivity(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mHelper != null)
            mHelper.onCreate(savedInstanceState);

        fragmentRefs = new HashMap<>();

        if (savedInstanceState == null) {
            theme = configTheme();
        } else {
            theme = savedInstanceState.getInt("theme");
        }
        // 设置主题
        if (theme > 0)
            setTheme(theme);

        taskManager = new TaskManager();

        // 如果设备有实体MENU按键，overflow菜单不会再显示
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this);
        if (viewConfiguration.hasPermanentMenuKey()) {
            try {
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(viewConfiguration, false);
            } catch (Exception e) {
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mHelper != null)
            mHelper.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mHelper != null)
            mHelper.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mHelper != null)
            mHelper.onRestart();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    public View getRootView() {
        return rootView;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);

        rootView = view;

        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        rootView = view;

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null)
            setSupportActionBar(mToolbar);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mHelper != null)
            mHelper.onSaveInstanceState(outState);

        outState.putInt("theme", theme);
    }

    public void addFragment(String tag, ABaseFragment fragment) {
        fragmentRefs.put(tag, new WeakReference(fragment));
    }

    public void removeFragment(String tag) {
        fragmentRefs.remove(tag);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mHelper != null)
            mHelper.onResume();

        setRunningActivity(this);

        if (theme == configTheme()) {

        } else {
            Logger.i("theme changed, reload()");
            reload();

            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mHelper != null)
            mHelper.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mHelper != null)
            mHelper.onStop();
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {

        isDestory = true;

        removeAllTask(true);

        super.onDestroy();

        if (mHelper != null)
            mHelper.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mHelper != null) {
            boolean handle = mHelper.onOptionsItemSelected(item);
            if (handle)
                return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                if (onHomeClick())
                    return true;
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean onHomeClick() {
        if (mHelper != null) {
            boolean handle = mHelper.onHomeClick();
            if (handle)
                return true;
        }

        Set<String> keys = fragmentRefs.keySet();
        for (String key : keys) {
            WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
            ABaseFragment fragment = fragmentRef.get();
            if (fragment != null && fragment.onHomeClick())
                return true;
        }

        return onBackClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mHelper != null) {
            boolean handle = mHelper.onKeyDown(keyCode, event);
            if (handle)
                return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (onBackClick())
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onBackClick() {
        if (mHelper != null) {
            boolean handle = mHelper.onBackClick();
            if (handle)
                return true;
        }

        Set<String> keys = fragmentRefs.keySet();
        for (String key : keys) {
            WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
            ABaseFragment fragment = fragmentRef.get();
            if (fragment != null && fragment.onBackClick())
                return true;
        }

        finish();

        return true;
    }

    @Override
    final public void addTask(@SuppressWarnings("rawtypes") IWorkTask task) {
        taskManager.addTask(task);
    }

    @Override
    final public void removeTask(String taskId, boolean cancelIfRunning) {
        taskManager.removeTask(taskId, cancelIfRunning);
    }

    @Override
    final public void removeAllTask(boolean cancelIfRunning) {
        taskManager.removeAllTask(cancelIfRunning);
    }

    @Override
    final public void clearTaskCount(String taskId) {
        taskManager.clearTaskCount(taskId);
    }

    @Override
    final public int getTaskCount(String taskId) {
        return taskManager.getTaskCount(taskId);
    }

    /**
     * 以Toast形式显示一个消息
     *
     * @param msg
     */
    public void showMessage(CharSequence msg) {
        ViewUtils.showMessage(this, msg.toString());
    }

    /**
     * @param msgId
     */
    public void showMessage(int msgId) {
        showMessage(getText(msgId));
    }

    @Override
    public void finish() {
        // 2014-09-12 解决ATabTitlePagerFragment的destoryFragments方法报错的BUG
        setDestory(true);

        super.finish();

        if (mHelper != null) {
            mHelper.finish();
        }
    }

    public boolean isDestory() {
        return isDestory;
    }

    public void setDestory(boolean destory) {
        this.isDestory = destory;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mHelper != null) {
            mHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onToolbarDoubleClick() {
        Set<String> keys = fragmentRefs.keySet();
        for (String key : keys) {
            WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
            ABaseFragment fragment = fragmentRef.get();
            if (fragment != null && fragment instanceof AsToolbar.OnToolbarDoubleClick) {
                if (((AsToolbar.OnToolbarDoubleClick) fragment).onToolbarDoubleClick())
                    return true;
            }
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (mHelper != null) {
            mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public BaseActivityHelper getActivityHelper() {
        return mHelper;
    }

}
