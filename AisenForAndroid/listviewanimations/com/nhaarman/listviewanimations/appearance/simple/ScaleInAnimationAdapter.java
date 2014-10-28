/*
 * Copyright 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nhaarman.listviewanimations.appearance.simple;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;

public class ScaleInAnimationAdapter extends AnimationAdapter {

    private static final float DEFAULT_SCALE_FROM = 0.8f;

    private static final String SCALE_X = "scaleX";
    private static final String SCALE_Y = "scaleY";

    private final float mScaleFrom;

    public ScaleInAnimationAdapter( final BaseAdapter baseAdapter) {
        this(baseAdapter, DEFAULT_SCALE_FROM);
    }

    public ScaleInAnimationAdapter( final BaseAdapter baseAdapter, final float scaleFrom) {
        super(baseAdapter);
        mScaleFrom = scaleFrom;
    }

    
    @Override
    public Animator[] getAnimators( final ViewGroup parent,  final View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, SCALE_X, mScaleFrom, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, SCALE_Y, mScaleFrom, 1f);
        return new ObjectAnimator[]{scaleX, scaleY};
    }
}
