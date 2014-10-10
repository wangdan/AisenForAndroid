package com.m.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectUtil {

	public static <T> T cloneObject(T t) {
		if (t == null)
			return null;

		if (!(t instanceof Serializable)) {
			return t;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = null;
			ObjectInputStream in = null;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(t);
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				in = new ObjectInputStream(bis);
				@SuppressWarnings("unchecked")
				T tmpT = (T) in.readObject();
				return tmpT;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
		} catch (Exception e) {
		}
		return t;
	}

}
