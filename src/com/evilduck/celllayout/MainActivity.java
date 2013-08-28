/*
 * Copyright 2013 Alexander Osmanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.evilduck.celllayout;

import java.util.ArrayList;
import java.util.Random;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.evilduck.celllayout.CellLayout.LayoutParams;

public class MainActivity extends Activity {

    private CellLayout cellLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	cellLayout = (CellLayout) findViewById(R.id.cell_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main_menu, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	doRandomRearrange();

	return true;
    }

    private static class ChildPair {
	View child1;
	View child2;
	Rect child1Bounds;
	Rect child2Bounds;
    }

    private void doRandomRearrange() {
	Random rnd = new Random();

	ArrayList<View> children = new ArrayList<View>();
	final ArrayList<ChildPair> pairs = new ArrayList<ChildPair>();
	for (int i = 0; i < cellLayout.getChildCount(); i++) {
	    children.add(cellLayout.getChildAt(i));
	}

	int pairsSize = cellLayout.getChildCount() / 2;
	for (int i = 0; i < pairsSize; i++) {
	    ChildPair pair = new ChildPair();
	    int randomIdx = rnd.nextInt(children.size());
	    pair.child1 = children.remove(randomIdx);
	    randomIdx = rnd.nextInt(children.size());
	    pair.child2 = children.remove(randomIdx);

	    pair.child1Bounds = getViewBounds(pair.child1);
	    pair.child2Bounds = getViewBounds(pair.child2);

	    pairs.add(pair);
	}

	for (ChildPair childPair : pairs) {
	    final View child1 = childPair.child1;
	    final View child2 = childPair.child2;

	    LayoutParams child1Params = (LayoutParams) child1.getLayoutParams();
	    LayoutParams child2Params = (LayoutParams) child2.getLayoutParams();
	    LayoutParams tmp = new LayoutParams(child1Params);

	    swap(child1Params, child2Params);
	    swap(child2Params, tmp);
	}
	cellLayout.requestLayout();

	cellLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
	    @Override
	    public boolean onPreDraw() {
		cellLayout.getViewTreeObserver().removeOnPreDrawListener(this);

		ArrayList<ValueAnimator> animators = new ArrayList<ValueAnimator>();
		for (ChildPair childPair : pairs) {
		    Rect child1New = getViewBounds(childPair.child1);
		    Rect child2New = getViewBounds(childPair.child2);

		    animators.add(createAnimator(childPair.child1, childPair.child1Bounds, child1New));
		    animators.add(createAnimator(childPair.child2, childPair.child2Bounds, child2New));
		}

		AnimatorSet as = new AnimatorSet();
		as.playTogether(animators.toArray(new ValueAnimator[animators.size()]));
		as.setDuration(300);
		as.setInterpolator(new AccelerateDecelerateInterpolator());
		as.start();
		return true;
	    }
	});

    }

    public ValueAnimator createAnimator(View view, Rect child1Old, Rect child1New) {
	PropertyValuesHolder leftHolder = PropertyValuesHolder.ofInt("left", child1Old.left, child1New.left);
	PropertyValuesHolder rightHolder = PropertyValuesHolder.ofInt("right", child1Old.right, child1New.right);
	PropertyValuesHolder topHolder = PropertyValuesHolder.ofInt("top", child1Old.top, child1New.top);
	PropertyValuesHolder bottomHolder = PropertyValuesHolder.ofInt("bottom", child1Old.bottom, child1New.bottom);

	return ObjectAnimator.ofPropertyValuesHolder(view, leftHolder, rightHolder, topHolder, bottomHolder);
    }

    private static Rect getViewBounds(View child) {
	return new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
    }

    private static void swap(LayoutParams dst, LayoutParams src) {
	dst.width = src.width;
	dst.height = src.height;
	dst.top = src.top;
	dst.left = src.left;
    }

}
