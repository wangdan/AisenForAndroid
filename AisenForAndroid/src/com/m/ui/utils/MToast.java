package com.m.ui.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.m.R;
import com.m.common.context.GlobalContext;

public class MToast {

	public static int type = 0;
	public static int yOffset = 0;

	private static Toast toast;

	private MToast() {

	}

	public static void showMessage(CharSequence msg) {
		if (type == 0) {
			if (toast == null) {
				if (type == 0) {
					toast = Toast.makeText(GlobalContext.getInstance(), msg, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 50);
					// 定制的view
					View view = View.inflate(GlobalContext.getInstance(), R.layout.lay_toast, null);
					toast.setView(view);
				}
			}
			TextView txt = (TextView) toast.getView().findViewById(R.id.txt_msg);
			txt.setText(msg);
			toast.show();
		}
		else if (type == 1) {
			Toast.makeText(GlobalContext.getInstance(), msg, Toast.LENGTH_SHORT).show();
		}
		else if (type == 2) {
			if (toast == null) {
				toast = Toast.makeText(GlobalContext.getInstance(), msg, Toast.LENGTH_SHORT);
				// 定制View
				View view = View.inflate(GlobalContext.getInstance(), R.layout.lay_toast_v2, null);
				toast.setView(view);
			}
			
			TextView txt = (TextView) toast.getView().findViewById(R.id.txt_msg);
			txt.setText(msg);
//			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, yOffset);
			toast.show();
		}
	}

	public static void showMessage(int msgId) {
		showMessage(GlobalContext.getInstance().getResources().getString(msgId));
	}

}
