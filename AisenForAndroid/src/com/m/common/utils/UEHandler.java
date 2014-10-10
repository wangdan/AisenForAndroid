package com.m.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.m.common.context.GlobalContext;
import com.m.common.settings.SettingUtility;

public class UEHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String info = null;
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;
		try {
			baos = new ByteArrayOutputStream();
			printStream = new PrintStream(baos);
			ex.printStackTrace();
			ex.printStackTrace(printStream);
			byte[] data = baos.toByteArray();
			info = new String(data);
			data = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (printStream != null)
					printStream.close();
				if (baos != null)
					baos.close();
			} catch (Exception e) {
			}
		}

		if (TextUtils.isEmpty(info))
			return;

		write2ErrorLog(info);

		if (SettingUtility.getBooleanSetting("crashToRestart")) {
			// 存在BUG，重复重启的问题
			if (!ActivityHelper.getInstance().getBooleanShareData("app_exit", false)) {
				// 当程序自动运行后，取消自动重启延迟
				ActivityHelper.getInstance().putBooleanShareData("crash_flag", true);
				Intent intent = new Intent();
				intent.setAction("com.m.common.crash_restart");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent restartIntent = PendingIntent.getActivity(GlobalContext.getInstance(), 11111, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
				AlarmManager mgr = (AlarmManager) GlobalContext.getInstance().getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, restartIntent);
			}
		}
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	private void write2ErrorLog(String content) {
		String filePath = SystemUtility.getSdcardPath() + File.separator + SettingUtility.getStringSetting("root_path") + File.separator
				+ SettingUtility.getStringSetting("crashlog") + File.separator + DateUtils.formatDate(System.currentTimeMillis(), "yyyy年MM月dd日HH时")
				+ ".txt";
		
		Logger.d(UEHandler.class.getSimpleName(), String.format("save crashlog file path = %s", filePath));

		// 错误日志具体时间
		StringBuffer sb = new StringBuffer();
		sb.append("\n").append("crash_log:").append(DateUtils.formatDate(System.currentTimeMillis(), DateUtils.TYPE_01)).append("\n").append(content)
				.append("\n");
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream OutStream = null;
		try {
			OutStream = new FileOutputStream(file, true);
			OutStream.write(sb.toString().getBytes());
			OutStream.flush();
			OutStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != OutStream)
				try {
					OutStream.close();
				} catch (Exception e2) {
				}
		}

	}

}
