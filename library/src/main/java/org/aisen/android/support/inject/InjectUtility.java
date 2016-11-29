package org.aisen.android.support.inject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;

import org.aisen.android.common.utils.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InjectUtility {

	static final String TAG = "InjectUtility";

    public static void initInjectedView(Activity sourceActivity) {
		initInjectedView(sourceActivity, sourceActivity, sourceActivity.getWindow().getDecorView());
	}

    public static void initInjectedView(Context context, final Object injectedSource, View sourceView) {
		long start = System.currentTimeMillis();

		Class<?> clazz = injectedSource.getClass();
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			if (clazz.getName().startsWith("android")) {
				break;
			}

			Method[] methods = clazz.getDeclaredMethods();

			for (final Method method : methods) {
				Class<?>[] paramsType = method.getParameterTypes();
				if (paramsType != null && paramsType.length == 1) {
					if (paramsType[0].getName().equals(View.class.getName())) {
						OnClick onClick = method.getAnnotation(OnClick.class);

						if (onClick != null) {
							int[] ids = onClick.value();
							for (int id : ids) {
								if (id != View.NO_ID) {
									View view = sourceView.findViewById(id);
									if (view != null) {
										view.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												try {
													method.setAccessible(true);

													method.invoke(injectedSource, v);
												} catch (Throwable e) {
													e.printStackTrace();
												}
											}

										});
									}
								}
							}
						}
					}
				}
			}

			Field[] fields = clazz.getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field field : fields) {
					ViewInject viewInject = field.getAnnotation(ViewInject.class);
					if (viewInject != null) {
						// ViewId可以是id配置，也可以是IdStr配置
						int viewId = viewInject.id();
						if (viewId == 0) {
							String idStr = viewInject.idStr();
							if (!TextUtils.isEmpty(idStr)) {
								try {
									String packageName = context.getPackageName();
									Resources resources = context.getPackageManager().getResourcesForApplication(packageName);

									viewId = resources.getIdentifier(idStr, "id", packageName);

									if (viewId == 0)
										throw new RuntimeException(String.format("%s 的属性%s关联了id=%s，但是这个id是无效的", clazz.getSimpleName(),
												field.getName(), idStr));
								} catch (Exception e) {
//									e.printStackTrace();
								}
							}
						}
						if (viewId != 0) {
							try {
								field.setAccessible(true);
								/*
								 * 当已经被赋值时，不在重复赋值，用于include，inflate情景下的viewinject组合
								 */
								if (field.get(injectedSource) == null) {
									field.set(injectedSource, sourceView.findViewById(viewId));

//									if (Logger.DEBUG) {
//										Logger.v(TAG, "id = %d, view = %s", viewId, field.get(injectedSource) + "");
//									}
								} else {
									continue;
								}
							} catch (Exception e) {
								Logger.printExc(InjectUtility.class, e);
							}
						}

					}
				}
			}
		}

		if (Logger.DEBUG)
			Logger.v(TAG, "耗时 %s ms : " + injectedSource, String.valueOf(System.currentTimeMillis() - start));
	}


}
