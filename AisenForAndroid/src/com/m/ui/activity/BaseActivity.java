package com.m.ui.activity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.android.loader.BitmapLoader;
import org.android.loader.core.BitmapOwner;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.m.common.context.GlobalContext;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.CommSettings;
import com.m.common.utils.Logger;
import com.m.common.utils.SystemBarTintManager;
import com.m.common.utils.SystemUtility;
import com.m.support.Inject.InjectUtility;
import com.m.support.task.ITaskManager;
import com.m.support.task.TaskManager;
import com.m.support.task.WorkTask;
import com.m.ui.fragment.ABaseFragment;
import com.m.ui.utils.ViewUtils;

public class BaseActivity extends Activity implements ITaskManager, BitmapOwner {

	static final String TAG = "BaseActivity";
	
	private int theme = 0;
	
	private Locale language = null;

	private TaskManager taskManager;
	
	private boolean isDestory;

	// 当有Fragment Attach到这个Activity的时候，就会保存
	private Map<String, WeakReference<ABaseFragment>> fragmentRefs;

	private static BaseActivity runningActivity;

	private AActivityHelper activityHelper;
	
	private SystemBarTintManager systemBarTintManager;
	
	private View rootView;

	public static BaseActivity getRunningActivity() {
		return runningActivity;
	}

	public static void setRunningActivity(BaseActivity activity) {
		runningActivity = activity;
	}
	
	public BaseActivity() {
		try {
			if (SettingUtility.getSetting("activity_lifecycle") != null) {
				Class<?> clazz = Class.forName(SettingUtility.getStringSetting("activity_lifecycle"));
				activityHelper = (AActivityHelper) clazz.newInstance();
				activityHelper.setBaseActivity(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected int configTheme() {
		return CommSettings.getAppTheme();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Logger.w(Locale.getDefault().getLanguage() + "()" + Locale.getDefault().getCountry());
		
		fragmentRefs = new HashMap<String, WeakReference<ABaseFragment>>();

		if (savedInstanceState == null) {
			theme = configTheme();
			
			language = new Locale(SettingUtility.getPermanentSettingAsStr("language", Locale.getDefault().getLanguage()),
											SettingUtility.getPermanentSettingAsStr("language-country", Locale.getDefault().getCountry()));
		} else {
			theme = savedInstanceState.getInt("theme");
			
			language = new Locale(savedInstanceState.getString("language"), savedInstanceState.getString("language-country"));
		}
		// 设置主题
		if (theme > 0)
			setTheme(theme);
		// 设置语言
		setLanguage(language);

		taskManager = new TaskManager();

		// 取消重新启动
		if (ActivityHelper.getInstance().getBooleanShareData("crash_flag", false)) {
			ActivityHelper.getInstance().putBooleanShareData("crash_flag", false);
			Intent intent = new Intent();
			intent.setAction("com.m.common.crash_restart");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent restartIntent = PendingIntent.getActivity(GlobalContext.getInstance(), 11111, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			AlarmManager mgr = (AlarmManager) GlobalContext.getInstance().getSystemService(Context.ALARM_SERVICE);
			mgr.cancel(restartIntent);
		}

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
		
		if (activityHelper != null)
			activityHelper.onCreate(savedInstanceState);
		
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void setContentView(int layoutResID) {
		ViewGroup contentView = null;
		
		boolean set = false;
		if (activityHelper != null) {
			contentView = activityHelper.setContentView(layoutResID);
			if (contentView != null) {
				rootView = contentView;
				
				set = true;
				super.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			}
		}

		if (!set)
			super.setContentView(layoutResID);

		InjectUtility.initInjectedView(this);
	}
	
	public View getRootView() {
		return rootView;
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		
		rootView = view;

		InjectUtility.initInjectedView(this);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		
		rootView = view;
		
		InjectUtility.initInjectedView(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (activityHelper != null)
			activityHelper.onSaveInstanceState(outState);

		outState.putInt("theme", theme);
		outState.putString("language", language.getLanguage());
		outState.putString("language-country", language.getCountry());
	}
	
	public void addFragment(String tag, ABaseFragment fragment) {
		fragmentRefs.put(tag, new WeakReference<ABaseFragment>(fragment));
	}

	public void removeFragment(String tag) {
		fragmentRefs.remove(tag);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (activityHelper != null)
			activityHelper.onResume();

		runningActivity = this;

		if (theme == configTheme()) {

		} else {
			Logger.i("theme changed, reload()");
			reload();
			
			return;
		}
		
		String languageStr = SettingUtility.getPermanentSettingAsStr("language", Locale.getDefault().getLanguage());
		String country = SettingUtility.getPermanentSettingAsStr("language-country", Locale.getDefault().getCountry());
		if (language != null && language.getLanguage().equals(languageStr) && country.equals(language.getCountry())) {
			
		}
		else {
			Logger.i("language changed, reload()");
			reload();
			
			return;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (activityHelper != null)
			activityHelper.onPause();
		
//		doAnimation();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (activityHelper != null)
			activityHelper.onStop();
	}
	
	public void setLanguage(Locale locale) {
		Resources resources =getResources();//获得res资源对象  
		Configuration config = resources.getConfiguration();//获得设置对象
		config.locale = locale;
		DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
		resources.updateConfiguration(config, dm);
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
		
		if (activityHelper != null)
			activityHelper.onDestroy();

		removeAllTask(true);

		BitmapLoader.getInstance().cancelPotentialTask(this);

		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (activityHelper != null)
			activityHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (activityHelper != null)
			activityHelper.onCreateOptionsMenu(menu, getMenuInflater());
		return super.onCreateOptionsMenu(menu);
	}

	protected void resetAcUnused() {
		lastAcUnusedClickTime = 0L;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		lastAcUnusedClickTime = 0L;
		Logger.v(TAG, "onOptionsItemSelected");
		
		switch (item.getItemId()) {
		case android.R.id.home:
			if (activityHelper != null)
				if (activityHelper.onHomeClick())
					return true;

			if (onHomeClick())
				return true;
			break;
		default:
			if (activityHelper != null)
				if (activityHelper.onOptionsItemSelected(item))
					return true;
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onHomeClick() {
		Set<String> keys = fragmentRefs.keySet();
		for (String key : keys) {
			WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
			ABaseFragment fragment = fragmentRef.get();
			if (fragment != null && fragment.onHomeClick())
				return true;
		}

		finish();

		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (activityHelper != null)
				if (activityHelper.onBackClick())
					return true;

			if (onBackClick())
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onBackClick() {
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
	final public void addTask(@SuppressWarnings("rawtypes") WorkTask task) {
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
	final public int getTaskCount(String taskId) {
		return taskManager.getTaskCount(taskId);
	}

	/**
	 * 以Toast形式显示一个消息
	 * 
	 * @param msg
	 */
	public void showMessage(CharSequence msg) {
		ViewUtils.showMessage(msg.toString());
	}

	/**
	 * @param msgId
	 */
	public void showMessage(int msgId) {
		showMessage(getText(msgId));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (activityHelper != null) {
			if (activityHelper.onTouchEvent(event))
				return true;
		}
		return super.onTouchEvent(event);
	}

	private long lastAcUnusedClickTime = 0L;
	private Rect mRect;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			if (mRect == null) {
				mRect = new Rect(0, 0, SystemUtility.getScreenWidth(), 0);
				mRect.top = SystemUtility.getStatusBarHeight(this);
				mRect.bottom = mRect.top + SystemUtility.getActionBarHeight(this);
			}
			if (mRect.contains(Math.round(ev.getX()), Math.round(ev.getY()))) {
				if (System.currentTimeMillis() - lastAcUnusedClickTime < 1 * 1000) {
					if (onAcUnusedDoubleClicked())
						return super.dispatchTouchEvent(ev);
				}
				
				lastAcUnusedClickTime = System.currentTimeMillis();
			}
		}
		
		if (activityHelper != null) {
			if (activityHelper.dispatchTouchEvent(ev))
				return true;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	public boolean onAcUnusedDoubleClicked() {
		Set<String> keys = fragmentRefs.keySet();
		for (String key : keys) {
			WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
			ABaseFragment fragment = fragmentRef.get();
			if (fragment != null && fragment instanceof IAcUnusedDoubleClickedHandler) {
				if (((IAcUnusedDoubleClickedHandler) fragment).onAcUnusedDoubleClicked())
					return true;
			}
		}

		return false;
	}
	
	@Override
	public void finish() {
		// 2014-09-12 解决ATabTitlePagerFragment的destoryFragments方法报错的BUG
		setMDestory(true);
		
		super.finish();
		
//		setCancelAnim();
	}
	
//	protected boolean myIsDestoryed() {
//		return isDestory;
//	}
//	
	public boolean mIsDestoryed() {
		return isDestory;
	}
	
	public void setMDestory(boolean destory) {
		this.isDestory = destory;
	}
	
	@Override
	public boolean canDisplay() {
		return true;
	}
	
	public AActivityHelper getActivityHelper() {
		return activityHelper;
	}
	
	public void setSystemBarTintManager(SystemBarTintManager systemBarTintManager) {
		this.systemBarTintManager = systemBarTintManager;
	}
	
	public SystemBarTintManager getSystemBarTintManager() {
		return systemBarTintManager;
	}
	
	/********************
	 * 界面切换动画
	 */

//	private int startInAnimId = R.anim.zoom_in_center;
//	private int startOutAnimId = R.anim.slide_out_right;
//	private int exitInAnimId = R.anim.slide_in_right;
//	private int exitOutAnimId = R.anim.zoom_out_center;
//
//	protected void clearActivityAnim() {
//		overridePendingTransition(0, 0);
//	}
//
//	protected void setCancelAnim() {
//		setStartAnimation(true);
//	}
//
//	private boolean isStartAnimation = false;
//
//	public boolean isStartAnimation() {
//		return isStartAnimation;
//	}
//
//	public void setStartAnimation(boolean isStartAnimation) {
//		this.isStartAnimation = isStartAnimation;
//	}
//
//	private void doAnimation() {
//		if (isStartAnimation) {
//			startTransAnimation();
//		} else {
//			exitTransAnimation();
//		}
//	}
//
//	private void startTransAnimation() {
//		overridePendingTransition(startInAnimId, startOutAnimId);
//	}
//
//	private void exitTransAnimation() {
//		overridePendingTransition(exitInAnimId, exitOutAnimId);
//	}
//
//	public int getStartInAnimId() {
//		return startInAnimId;
//	}
//
//	public void setStartInAnimId(int startInAnimId) {
//		this.startInAnimId = startInAnimId;
//	}
//
//	public int getStartOutAnimId() {
//		return startOutAnimId;
//	}
//
//	public void setStartOutAnimId(int startOutAnimId) {
//		this.startOutAnimId = startOutAnimId;
//	}
//
//	public int getExitInAnimId() {
//		return exitInAnimId;
//	}
//
//	public void setExitInAnimId(int exitInAnimId) {
//		this.exitInAnimId = exitInAnimId;
//	}
//
//	public int getExitOutAnimId() {
//		return exitOutAnimId;
//	}
//
//	public void setExitOutAnimId(int exitOutAnimId) {
//		this.exitOutAnimId = exitOutAnimId;
//	}
	
	public interface IAcUnusedDoubleClickedHandler {
		
		public boolean onAcUnusedDoubleClicked();
		
	}
	
}
