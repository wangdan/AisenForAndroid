package com.m.common.utils;

/**
 * <p>Title: PunycodeUtil.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: TEC 3rd of CNNIC </p>
 * @author Orsen Leo
 * @version 1.0
 */

import java.io.*;
import java.util.*;

public class PunycodeUtil {

	private static final String input_string_null = "Translate Error : input string is a null";
	private static final String punystr_bad_input = "Exception in change punycode to chinese,Input_Str is not punycode";
	private static final String unicode_bad_input = "Exception in change chinese to punycode,Input_Str is not unicode chinese";
	private static final String uninum_bad_input = "Exception in change U+num to chinese,Input_Str is not unicode num";
	private static final String too_long_output = "Output Error, Output would exceed the space provided(256 as default)";
	// public static final String output_meaningless =
	// "Output Error, Output would contain some meaningless unicodes";
	private static final String translate_cancel = "Translate cancel nothing changed";
	private static final String Integer_overflow = "Input needs wider integers to process";

	private static PunycodeUtil punycoder = null;

	/**
	 * change chinese string to punycode string chstr is the chinese str to be
	 * translated can not be null
	 * */
	public static String chinese2punycode(String chstr) throws PunyException {
		if (chstr == null || chstr.equals("")) {
			throw new PunyException(input_string_null);
		}
		String punystr = "";
		try {
			punystr = cdntopuny(chstr);
			return punystr;
		} catch (PunyException e) {
			throw e;
		}
	}

	/**
	 * change punycode string to chinese string punystr is the punycode str to
	 * be translated can not be null
	 * */

	public static String punycode2chinese(String punystr) throws PunyException {
		if (punystr == null || punystr.equals("")) {
			throw new PunyException(punystr_bad_input);
		}
		String chstr = "";
		try {
			chstr = punys_to_ocdn(punystr);
			return chstr;
		} catch (PunyException e) {
			throw e;
		}
	}

	private static long decode_digit(long cp) {
		return cp - 48 < 10 ? cp - 22 : cp - 65 < 26 ? cp - 65 : cp - 97 < 26 ? cp - 97 : base;
	}

	private static char encode_digit(long d, int flag) {
		double x, y;
		/* 0..25 map to ASCII a..z or A..Z */
		/* 26..35 map to ASCII 0..9 */
		if (d < 26)
			x = 1;
		else
			x = 0;
		if (flag != 0)
			y = (1 << 5);
		else
			y = (0 << 5);
		return ((char) (d + 22 + 75 * x - y));
	}

	private static char encode_basic(long bcp, int flag) {
		boolean x, y;
		int z;
		// bcp -= (bcp - 97 < 26) << 5;
		if ((bcp - 97) < 26)
			bcp -= (1 << 5);
		else
			bcp -= (0 << 5);
		if (flag > 0)
			x = true;
		else
			x = false;
		if ((bcp - 65) < 26)
			y = true;
		else
			y = false;
		if (x && y)
			z = 1;
		else
			z = 0;
		// long yy=bcp + (z<< 5);
		// char yx=(char)(49);
		// Long.
		return (char) (bcp + (z << 5));

	}

	private static long adapt(long delta, long numpoints, int firsttime) {
		long k;

		delta = (firsttime > 0) ? ((long) delta / damp) : (delta >> 1);
		/* delta >> 1 is a faster way of doing delta / 2 */
		delta += delta / numpoints;

		for (k = 0; delta > ((base - tmin) * tmax) / 2; k += base) {
			delta /= base - tmin;
		}

		return k + (base - tmin + 1) * delta / (delta + skew);
	}

	/**
	 * this method transfer unicode(U+****U+****...) into characters if bad
	 * input ,then return "" Modified by LiuYu 2004-11-19 if fail to transfer ,
	 * throw PunyExceptions
	 */
	private static String punycode_cn(String s_input) throws PunyException {
		if (s_input == null) {
			throw new PunyException(input_string_null);
		}

		char[] input = new char[unicode_max_length];
		char[] case_flags = new char[unicode_max_length];
		String bad_input = "";

		if (!((s_input.startsWith(u_big)) || (s_input.startsWith(u_small)))) {
			throw new PunyException(uninum_bad_input);
		}
		if (((s_input.length() % 6) != 0) || (s_input.length() < 2)) {
			throw new PunyException(uninum_bad_input);
		}

		String ls, lxs = s_input.substring(2);
		String ss = s_input.substring(0, 1);

		int input_length = 0;
		// ls=lxs.substring(0,lxs.indexOf('+')-2);
		while (lxs.indexOf('+') > 0) {
			if (ss.charAt(0) == 'U') {
				case_flags[input_length] = '1';
			} else if (ss.charAt(0) == 'u') {
				case_flags[input_length] = '0';
			} else {
				throw new PunyException(uninum_bad_input);
			}

			ls = lxs.substring(0, lxs.indexOf('+') - 1);
			input[input_length] = (char) Integer.parseInt(ls, 16);

			// System.out.println(ls);
			input_length++;

			lxs = lxs.substring(lxs.indexOf('+') - 1);
			ss = lxs.substring(0, 1);
			lxs = lxs.substring(2);

		}
		if (ss.charAt(0) == 'U')
			case_flags[input_length] = '1';
		else if (ss.charAt(0) == 'u')
			case_flags[input_length] = '0';
		else
			throw new PunyException(uninum_bad_input);

		input[input_length] = (char) Integer.parseInt(lxs, 16);
		return new String(input, 0, ++input_length);

	}

	/***
	 * this method transfer characters into unicode(U+****U+****...) if bad
	 * input ,then return ""**
	 * 
	 * s_input is a chinese or english string to be translated can not be null
	 */
	private static String get_cnuni_out(String s_input) {
		int k = s_input.length();
		String res = "", ith = "";
		int ii, m;
		for (int i = 0; i < k; ++i) {
			ith = Integer.toHexString((int) (s_input.charAt(i)));
			m = ith.length();
			for (ii = 0; ii < 4 - m; ii++) {
				ith = "0" + ith;
			}
			res = res + u_big + ith;
		}
		return res;
	}

	/**
	 * check weather the string is a domain name string input inputString return
	 * true false the input string cant be null
	 * */
	private static boolean isdomain(String inputStr) {
		boolean eflag = true;
		for (int j = 0; j < inputStr.length(); ++j) {
			if (espec_str.indexOf(inputStr.charAt(j)) < 0) {
				eflag = false;
				break;
			}
		}
		return eflag;
	}

	/***
	 * Main_encode function s_input begins with "u+" Modified by LiuYu
	 * 2004-11-19 if fail to transfer , throw PunyExceptions
	 * */
	private static String punycode_encode(String s_input) throws PunyException {
		if (s_input == null) {
			throw new PunyException(input_string_null);
		}

		/** ����ַ�ΪȫӢ���ַ�,ֱ�ӷ����ַ��� */

		long n, delta, h, b, max_out, bias, m, q, k, t;
		int input_length;

		int j, out;

		// String inputtemp;

		long[] input = new long[unicode_max_length];

		char[] output = new char[ace_max_length + 1];
		char[] inout = new char[unicode_max_length];

		char[] case_flags = new char[unicode_max_length];
		/* Initialize the state: */
		n = initial_n;

		delta = 0;

		out = 0;

		input_length = 0;

		int output_length = ace_max_length;

		max_out = output_length;

		bias = initial_bias;

		String ls, lxs = s_input.substring(2);
		String ss = s_input.substring(0, 1);

		// ls=lxs.substring(0,lxs.indexOf('+')-2);
		while (lxs.indexOf('+') > 0) {

			if (ss.charAt(0) == 'U')
				case_flags[input_length] = '1';
			else if (ss.charAt(0) == 'u')
				case_flags[input_length] = '0';
			else
				throw new PunyException(uninum_bad_input);

			ls = lxs.substring(0, lxs.indexOf('+') - 1);
			input[input_length] = Long.parseLong(ls, 16);
			inout[input_length] = (char) Integer.parseInt(ls, 16);

			// System.out.println(ls);
			input_length++;

			lxs = lxs.substring(lxs.indexOf('+') - 1);
			ss = lxs.substring(0, 1);
			lxs = lxs.substring(2);

		}
		if (ss.charAt(0) == 'U')
			case_flags[input_length] = '1';
		else if (ss.charAt(0) == 'u')
			case_flags[input_length] = '0';
		else
			throw new PunyException(uninum_bad_input);
		;
		input[input_length] = Long.parseLong(lxs, 16);
		inout[input_length] = (char) Integer.parseInt(lxs, 16);
		// System.out.println(input[input_length]);

		input_length++;
		// //a..z,A..Z,0..9

		/** for all english string */
		boolean eflag = false;
		for (j = 0; j < input_length; ++j) {
			if (espec_str.indexOf(inout[j]) < 0) {
				eflag = true;
				break;
			}
		}
		if (eflag == false) {
			return (new String(inout, 0, input_length));
		}

		/* Handle the basic code points: */

		for (j = 0; j < input_length; ++j) {
			if (input[j] < (0x80L)) {
				if (max_out - out < 2)
					throw new PunyException(too_long_output);
				// output[out++] = case_flags ? encode_basic(input[j],
				// case_flags[j]) : input[j];
				output[out++] = encode_basic(input[j], (int) (case_flags[j]));
				// System.out.println(out+"uu"+output[out-1]);
			}
		}
		h = b = out;

		/* h is the number of code points that have been handled, b is the */
		/* number of basic code points, and out is the number of characters */
		/* that have been output. */

		if (b > 0)
			output[out++] = (char) (delimiter);

		/* Main encoding loop: */
		while (h < input_length) {
			/* All non-basic code points < n have been */
			/* handled already. Find the next larger one: */
			for (m = maxint, j = 0; j < input_length; ++j) {
				/* if (basic(input[j])) continue; */
				/* (not needed for Punycode) */
				if (((int) input[j] >= n) && (input[j] < m))
					m = input[j];
			}
			/* Increase delta enough to advance the decoder's */
			/* <n,i> state to <m,0>, but guard against overflow: */

			if (m - n > (maxint - delta) / (h + 1))
				throw new PunyException(Integer_overflow);
			delta += (m - n) * (h + 1);

			n = m;

			for (j = 0; j < input_length; ++j) { // System.out.println("tttl"+out);
				/* Punycode does not need to check whether input[j] is basic: */
				if (input[j] < n /* || basic(input[j]) */) {

					if (++delta == 0)
						throw new PunyException(Integer_overflow);
				}

				if (input[j] == n) {
					/* Represent delta as a generalized variable-length integer: */
					for (q = delta, k = base;; k += base) {
						if (out >= max_out)
							throw new PunyException(too_long_output);
						t = (k <= bias ? tmin : (k >= bias + tmax ? tmax : k - bias));
						if (q < t)
							break;
						output[out++] = encode_digit(t + (q - t) % (base - t), 0);
						// System.out.println("gg"+out);
						q = (q - t) / (base - t);
					}

					if (case_flags[j] != '0')
						output[out++] = encode_digit(q, 1);

					else
						output[out++] = encode_digit(q, 0);

					if (h == b)
						bias = adapt(delta, h + 1, 1);
					else
						bias = adapt(delta, h + 1, 0);
					// bias = adapt(delta, h + 1, h == b);
					delta = 0;
					++h;
				}
			} // end for

			++delta;
			++n;
		} // end while

		output_length = out;

		/**
		 * e_out = puny_prefix + new String(output, 0, output_length); return
		 * e_out;
		 */
		return puny_prefix + new String(output, 0, output_length);
	}

	/*
	 * domain name to punycode with "." ,for example iesg--kdjfkd.iesg--fjkdj
	 * cdns is the string to be translated , can not be null Modified by LiuYu
	 * 2004-11-19 if fail to transfer , throw PunyExceptions
	 */
	private static String cdntopuny(String cdns) throws PunyException {
		if (cdns == null) {
			throw new PunyException(input_string_null);
		}
		String rds = cdns;
		String rtds = "", tds = "", rtds_u = "", resu_out = "";
		int flag;
		while (rds.indexOf(dndot) != -1) {
			tds = rds.substring(0, rds.indexOf(dndot));
			rds = rds.substring(rds.indexOf(dndot) + 1);
			if (!(tds.equalsIgnoreCase(""))) {
				rtds_u = get_cnuni_out(tds);
				if (!(rtds_u.equalsIgnoreCase(""))) {
					try {
						resu_out = resu_out + punycode_encode(rtds_u) + dndot;
					} catch (PunyException e) {
						throw e;
					}
				}
			} else {
				resu_out += dndot;
			}
		}
		if (!(rds.equalsIgnoreCase(""))) {
			rtds_u = get_cnuni_out(rds);
			if (!(rtds_u.equalsIgnoreCase(""))) {
				try {
					resu_out = resu_out + punycode_encode(rtds_u);
				} catch (PunyException e) {
					throw e;
				}
			}
		}

		return resu_out;
	}

	/***
	 * this method transfer punycode(s) to original form -----characters if fail
	 * to transfer ,return "" else return right result
	 * 
	 * Modified by LiuYu 2004-11-19 if fail to transfer , throw PunyExceptions
	 * ***/

	private static String punys_to_ocdn(String punys) throws PunyException {
		if (punys == null) {
			throw new PunyException(input_string_null);
		}

		String rds = punys;
		String rtds = "", tds = "", rtds_u = "", resu_out = "", strpuny = "";
		int flag;
		int dec_ru, jtout_ru;
		while (rds.indexOf(dndot) != -1) {
			tds = rds.substring(0, rds.indexOf(dndot));
			rds = rds.substring(rds.indexOf(dndot) + 1);
			if (!(tds.equalsIgnoreCase(""))) {
				/** update for xn--��Сд�޹� */
				if (isdomain(tds)
						&& ((tds.length() < puny_prefix.length()) || (!tds.substring(0, puny_prefix.length()).equalsIgnoreCase(puny_prefix)))) {
					rtds_u = tds;
				} else {
					try {
						strpuny = punycode_decode(tds);
					} catch (PunyException e) {
						throw e;
					}
					rtds_u = punycode_cn(strpuny);
				}

				if (rtds_u.equalsIgnoreCase("")) {
					return rtds_u;
				} else {
					resu_out += rtds_u + dndot;
				}
			} else {
				resu_out += dndot;
			}
		}
		if (!(rds.equalsIgnoreCase(""))) {
			if (isdomain(tds) && ((rds.length() < puny_prefix.length()) || (!rds.substring(0, puny_prefix.length()).equalsIgnoreCase(puny_prefix)))) {
				rtds_u = rds;
			}
			/**
			 * if ((rds.indexOf(puny_prefix) == -1)&&isdomain(rds)) { rtds_u =
			 * rds; }
			 */
			else {

				try {
					strpuny = punycode_decode(rds);
				} catch (PunyException e) {
					throw e;
				}

				rtds_u = punycode_cn(strpuny);
			}
			if (rtds_u.equalsIgnoreCase(""))
				return rtds_u;
			else
				resu_out = resu_out + rtds_u;
		}
		return resu_out;
	}

	/**
	 * Important Function s_input is a punycode string can not be null s_input
	 * will not contain a "."
	 * */
	private static String punycode_decode(String s_input) throws PunyException {
		if (s_input == null) {
			throw new PunyException(input_string_null);
		}

		long n, max_out, bias, oldi, w, k, digit, t;

		int j, out, in, b, i, input_length, jj;
		/* Initialize the state: */

		char[] case_flags = new char[unicode_max_length];

		long[] output = new long[unicode_max_length];

		if (s_input.length() <= puny_prefix.length()) {
			// d_out = s_input;
			System.out.println("s_input.length() <= puny_prefix.length()");
			throw new PunyException(punystr_bad_input);
		}
		String inout = s_input.substring(0, puny_prefix.length());
		if (!(inout.equalsIgnoreCase(puny_prefix))) {
			// d_out = s_input;
			// System.out.println("inout.equalsIgnoreCase(puny_prefix)");
			throw new PunyException(punystr_bad_input);
		}
		String sprefix = s_input.substring(puny_prefix.length());
		for (jj = 0; jj < sprefix.length(); ++jj) {
			if (print_ascii.indexOf(sprefix.charAt(jj)) < 0)
				throw new PunyException(punystr_bad_input);
		}
		n = initial_n;
		out = 0;

		input_length = sprefix.length();

		char[] input = new char[ace_max_length + 2];
//	    if (s_input.length()<(puny_prefix.length()))
//	      throw new PunyException(punystr_bad_input);;

		for (i = 0; i < input_length; ++i) {
			input[i] = sprefix.charAt(i);
		}

		i = 0;
		input[input_length] = '0';
		int output_length = unicode_max_length;

		max_out = output_length;

		bias = initial_bias;

		/* Handle the basic code points: Let b be the number of input code */
		/* points before the last delimiter, or 0 if there is none, then */
		/* copy the first b code points to the output. */

		for (b = j = 0; j < input_length; ++j)
			if (input[j] == delimiter)
				b = j;
		if (b > max_out)
			throw new PunyException(too_long_output);

		for (j = 0; j < b; ++j) {
			// if (case_flags) case_flags[out] = flagged(input[j]);
			if ((((int) input[j]) - 65) < 26)
				case_flags[out] = '1';
			else
				case_flags[out] = '0';

			if (!(input[j] < 0x80))
				throw new PunyException(punystr_bad_input);
			;
			// #define basic(cp) ((punycode_uint)(cp) < 0x80)

			output[out++] = input[j];
		}

		/* Main decoding loop: Start just after the last delimiter if any */
		/* basic code points were copied; start at the beginning otherwise. */

		for (in = b > 0 ? b + 1 : 0; in < input_length; ++out) {
			/* in is the index of the next character to be consumed, and */
			/* out is the number of code points in the output array. */
			/* Decode a generalized variable-length integer into delta, */
			/* which gets added to i. The overflow checking is easier */
			/* if we increase i as we go, then subtract off its starting */
			/* value at the end to obtain delta. */
			for (oldi = i, w = 1, k = base;; k += base) {
				if (in >= input_length)
					throw new PunyException(punystr_bad_input);
				;

				digit = decode_digit(input[in++]);

				if (digit >= base)
					throw new PunyException(punystr_bad_input);
				;

				if (digit > (maxint - i) / w)
					throw new PunyException(Integer_overflow);
				i += digit * w;
				t = k <= bias /* + tmin */? tmin : /* +tmin not needed */
				k >= bias + tmax ? tmax : k - bias;
				if (digit < t)
					break;
				if (w > maxint / (base - t))
					throw new PunyException(Integer_overflow);
				w *= (base - t);
			}

			if (oldi == 0)
				bias = adapt(i - oldi, out + 1, 1);
			else
				bias = adapt(i - oldi, out + 1, 0);

			/* i was supposed to wrap around from out+1 to 0, */
			/* incrementing n each time, so we'll fix that now: */

			if (i / (out + 1) > maxint - n)
				throw new PunyException(Integer_overflow);
			n += i / (out + 1);
			i = i % (out + 1);

			/* Insert n at position i of the output: */

			/* not needed for Punycode: */
			/* if (decode_digit(n) <= base) return punycode_invalid_input; */
			if (out >= max_out)
				throw new PunyException(too_long_output);
			// if (case_flags)
			// {

			// memmove(case_flags + i + 1, case_flags + i, out - i);
			for (int q = 0; q < out - i; ++q) {
				case_flags[i + 1 + q] = case_flags[i + q];
			}
			/* Case of last character determines uppercase flag: */
			if (input[in - 1] - 65 < 26)
				case_flags[i] = '1';
			else
				case_flags[i] = '0';
			for (int qq = 0; qq < out - i; ++qq) {
				output[i + out - i - qq] = output[i + out - i - qq - 1];
			}
			// memmove(output + i + 1, output + i, (out - i) * sizeof *output);
			output[i++] = n;
		}
		output_length = out;

		long xx;
		String yy = "";
		String sy = "";
		int ys, yss;
		for (i = 0; i < output_length; ++i) {
			xx = output[i];
			sy = Long.toString(xx, 16);
			yss = sy.length();
			for (ys = 0; ys < 4 - yss; ++ys) {
				sy = "0" + sy;
			}

			if (case_flags[i] == '1') {
				yy = yy + u_big + sy;
			} else if (case_flags[i] == '0')
				yy = yy + u_small + sy;

		}

		// if (yy.length()<6)
		// yy=yy.substring(0,2)+"0"+yy.substring(2);
		// d_out = yy;
		// return d_out;
		return yy;
	} // ////////

	private static final int base = 36;
	private static final int tmin = 1;
	private static final int tmax = 26;
	private static final int skew = 38;
	private static final int damp = 700;
	private static final int initial_bias = 72;
	private static final int initial_n = 0x80;
	private static final int delimiter = 0x2D;

	private static final int unicode_max_length = 256;
	private static final int ace_max_length = 256;

	private static final String print_ascii = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_` "
			+ "abcdefghijklmnopqrstuvwxyz{|}~";
	private static final String espec_str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-";

	private static final long maxint = 9223372036854775807L;

	private static String puny_prefix = "xn--";
	private static String u_big = "U+";
	private static String u_small = "u+";
	private static char dndot = '.';

	public static class PunyException extends java.lang.Exception implements java.io.Serializable {

		public PunyException() {
			super();
		}

		public PunyException(String msg) {
			super(msg);
		}
	}

}