package com.m.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.m.common.params.Params;
import com.m.common.params.ParamsUtil;

public class KeyGenerator {

	private KeyGenerator() {

	}
	
	public static String generateMD5(String key) {
		return generateMD5(key, null);
	}

	public static String generateMD5(String action, Params params) {
		String key;
		if (params == null)
			key = action;
		else
			key = action + ParamsUtil.encodeToURLParams(params);
		
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			byte[] bytes = mDigest.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					sb.append('0');
				}
				sb.append(hex);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return String.valueOf(key.hashCode());
		}
	}
}
