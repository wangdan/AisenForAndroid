package org.android.loader.display;

import org.android.loader.view.MyDrawable;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.m.R;
import com.m.common.context.GlobalContext;
import com.m.common.utils.Logger;

public class FadeInDisplayer implements Displayer {

	@Override
	public void loadCompletedisplay(ImageView imageView, BitmapDrawable drawable) {
		if (imageView.getClass().getSimpleName().indexOf("PhotoView") != -1)
			return;

		if (drawable instanceof MyDrawable) {
			MyDrawable myDrawable = (MyDrawable) drawable;
			Logger.d(FadeInDisplayer.class.getSimpleName(), "url = " + myDrawable.getMyBitmap().getUrl());
		}
		
		final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] { GlobalContext.getInstance().getResources().getDrawable(R.drawable.bg_timeline_loading), drawable });
		imageView.setImageDrawable(td);
		td.startTransition(300);
	}

	@Override
	public void loadFailDisplay(ImageView imageView, BitmapDrawable drawable) {
		imageView.setImageDrawable(drawable);
	}

}
