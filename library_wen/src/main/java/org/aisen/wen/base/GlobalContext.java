package org.aisen.wen.base;

import android.app.Application;

/**
 * Application的上下文
 *
 * Created by wangdan on 16/9/29.
 */
public class GlobalContext {

    private GlobalContext() {
    }

    private static Application _context;

    public static void onCreate(Application context) {
        _context = context;
    }

    public static Application getInstance() {
        return _context;
    }

}
