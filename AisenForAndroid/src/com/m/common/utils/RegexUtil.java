package com.m.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexUtil {

	public static boolean stringFilter(String str, String regEx) throws PatternSyntaxException {
		// 只允许字母和数字
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.matches();
	}

}
