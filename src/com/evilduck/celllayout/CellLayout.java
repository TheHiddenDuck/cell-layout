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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public class CellLayout extends ViewGroup {

    private static final int DEFAULT_CELL_SIZE = 48;

    private int columns = 4;

    private int spacing = 0;

    private float cellSize;

    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);

	initAttrs(context, attrs);
    }

    public CellLayout(Context context, AttributeSet attrs) {
	super(context, attrs);

	initAttrs(context, attrs);
    }

    public CellLayout(Context context) {
	super(context);
    }

    public void initAttrs(Context context, AttributeSet attrs) {
	TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CellLayout, 0, 0);

	try {
	    columns = a.getInt(R.styleable.CellLayout_columns, 4);
	    spacing = a.getDimensionPixelSize(R.styleable.CellLayout_spacing, 0);
	} finally {
	    a.recycle();
	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	int heightMode = MeasureSpec.getMode(heightMeasureSpec);

	int width = 0;
	int height = 0;

	if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
	    width = MeasureSpec.getSize(widthMeasureSpec);
	    cellSize = (float) (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / (float) columns;
	} else {
	    cellSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CELL_SIZE, getResources()
		    .getDisplayMetrics());
	    width = (int) (columns * cellSize);
	}

	int childCount = getChildCount();
	View child;

	int maxRow = 0;

	for (int i = 0; i < childCount; i++) {
	    child = getChildAt(i);

	    LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

	    int top = layoutParams.top;
	    int w = layoutParams.width;
	    int h = layoutParams.height;

	    int bottom = top + h;

	    int childWidthSpec = MeasureSpec.makeMeasureSpec((int) (w * cellSize) - spacing * 2,
		    LayoutParams.MATCH_PARENT);
	    int childHeightSpec = MeasureSpec.makeMeasureSpec((int) (h * cellSize) - spacing * 2,
		    LayoutParams.MATCH_PARENT);
	    child.measure(childWidthSpec, childHeightSpec);

	    if (bottom > maxRow) {
		maxRow = bottom;
	    }
	}

	int measuredHeight = Math.round(maxRow * cellSize) + getPaddingTop() + getPaddingBottom();
	if (heightMode == MeasureSpec.EXACTLY) {
	    height = MeasureSpec.getSize(heightMeasureSpec);
	} else if (heightMode == MeasureSpec.EXACTLY) {
	    int atMostHeight = MeasureSpec.getSize(heightMeasureSpec);
	    height = Math.min(atMostHeight, measuredHeight);
	} else {
	    height = measuredHeight;
	}

	setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
	int childCount = getChildCount();

	View child;
	for (int i = 0; i < childCount; i++) {
	    child = getChildAt(i);

	    LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

	    int top = (int) (layoutParams.top * cellSize) + getPaddingTop() + spacing;
	    int left = (int) (layoutParams.left * cellSize) + getPaddingLeft() + spacing;
	    int right = (int) ((layoutParams.left + layoutParams.width) * cellSize) + getPaddingLeft() - spacing;
	    int bottom = (int) ((layoutParams.top + layoutParams.height) * cellSize) + getPaddingTop() - spacing;

	    child.layout(left, top, right, bottom);
	}
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
	return new CellLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
	return p instanceof CellLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
	return new CellLayout.LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
	return new LayoutParams();
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

	int top = 0;

	int left = 0;

	int width = 1;

	int height = 1;

	public LayoutParams() {
	    super(MATCH_PARENT, MATCH_PARENT);
	    top = 0;
	    left = 1;
	}

	public LayoutParams(int width, int height) {
	    super(width, height);
	    top = 0;
	    left = 1;
	}

	public LayoutParams(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CellLayout);
	    left = a.getInt(R.styleable.CellLayout_layout_left, 0);
	    top = a.getInt(R.styleable.CellLayout_layout_top, 0);
	    height = a.getInt(R.styleable.CellLayout_layout_cellsHeight, -1);
	    width = a.getInt(R.styleable.CellLayout_layout_cellsWidth, -1);

	    a.recycle();
	}

	public LayoutParams(ViewGroup.LayoutParams params) {
	    super(params);
	}

    }

}
