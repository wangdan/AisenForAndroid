package com.m.support.task;

import com.m.R;
import com.m.common.context.GlobalContext;

import android.text.TextUtils;

public class TaskException extends Exception {

	private static final long serialVersionUID = -6262214243381380676L;

	public enum TaskError {
		// 无网络链接
		noneNetwork, 
		// 连接超时
		timeout, 
		// 返回数据不合法
		resultIllegal
	}
	
	private String errorCode;
	
	private String errorMsg;
	
	public TaskException(String errorCode, String errorMsg) {
		this(errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public TaskException(String errorCode) {
		this.errorCode = errorCode;
		
		try {
			TaskError error = TaskError.valueOf(errorCode);
			if (TaskError.noneNetwork == error)
				errorMsg = GlobalContext.getInstance().getResources().getString(R.string.noneNetwork);
			else if (TaskError.timeout == error)
				errorMsg = GlobalContext.getInstance().getResources().getString(R.string.timeout);
			else if (TaskError.resultIllegal == error)
				errorMsg = GlobalContext.getInstance().getResources().getString(R.string.resultIllegal);
		} catch (Exception e) {
		}
	}
	
	@Override
	public String getMessage() {
		if (!TextUtils.isEmpty(errorMsg))
			return errorMsg;

		return super.getMessage();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

}
