package org.aisen.wen.component.network.http;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Params implements Serializable {

	private static final long serialVersionUID = 5990125562753037686L;

	private Map<String, String> mParameters = new HashMap<>();
	private List<String> mKeys = new ArrayList<>();

	// 某些时候，参数列表不需要进行编码，例如将参数作为URL QUERY时，防止进行了二次编码导致服务器不能解析
	private boolean encodeAble = true;

	public Params() {

	}

	public Params(String[] keys, String[] values) {
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			mKeys.add(key);
			mParameters.put(key, values[i]);
		}

	}

	public Params(String key, String value) {
		mKeys.add(key);
		mParameters.put(key, value);
	}

	public int size() {
		return mKeys.size();
	}

	public boolean containsKey(String key) {
		return mKeys.contains(key);
	}

	public List<String> getKeys() {
		return mKeys;
	}

	public void add(String key, String value) {
		add(key, value, false);
	}

	public void add(String key, String value, boolean encode) {
		if (!mKeys.contains(key)) {
			mKeys.add(key);
		}

		if (encode) {
			value = encode(value);
		}

		mParameters.put(key, value);
	}

	public String get(String key) {
		return mParameters.get(key);
	}

	public Map<String, String> getVaules() {
		return mParameters;
	}

	public void remove(String key) {
		if (mKeys.contains(key)) {
			mKeys.remove(key);
			mParameters.remove(key);
		}
	}

	public void addParams(Params params) {
		for (String key : params.getKeys()) {
			if (!mKeys.contains(key)) {
				mKeys.add(key);
			}
			mParameters.put(key, params.get(key));
		}
	}

	public boolean isEncodeAble() {
		return encodeAble;
	}

	public void setEncodeAble(boolean encodeAble) {
		this.encodeAble = encodeAble;
	}

	public void clearParams() {
		mParameters.clear();
		mKeys.clear();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (String key : mKeys) {
			sb.append(key).append("=").append(get(key)).append(",");
		}

		return sb.toString();
	}

	public String toURLQuery() {
		return encodeParams(this, "&");
	}

	private static String encodeParams(Params params, String splitStr) {
		StringBuffer paramsBuffer = new StringBuffer();
		for (String key : params.getKeys()) {
			if (params.get(key) == null)
				continue;

			if (paramsBuffer.length() != 0) {
				paramsBuffer.append(splitStr);
			}

			paramsBuffer.append(key + "=");
			paramsBuffer.append(params.get(key));
			paramsBuffer.append(params.isEncodeAble() ? encode(params.get(key)) : params.get(key));
		}
		return paramsBuffer.toString();
	}

	private static String encode(String value) {
		if (value == null)
			return "";

		String encoded = null;
		try {
			encoded = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		StringBuffer buf = new StringBuffer(encoded.length());
		char focus;
		for (int i = 0; i < encoded.length(); i++) {
			focus = encoded.charAt(i);
			if (focus == '*') {
				buf.append("%2A");
			} else if (focus == '+') {
				buf.append("%20");
			} else if (focus == '%' && (i + 1) < encoded.length() && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
				buf.append('~');
				i += 2;
			} else {
				buf.append(focus);
			}
		}
		return buf.toString();
	}

}
