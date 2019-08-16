package com.example.jonny.fftcgcompanion.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.activities.CardFilterActivity;

public class FilterLinearLayout extends LinearLayout
{
    private final int defaultBackground = getResources().getColor(R.color.colorBackgroundAltern1);
    private final int selectedBackground = getResources().getColor(R.color.selectedBackground);

    private int m_arrayEntriesID = -1;

    public FilterLinearLayout(Context context)
    {
        super(context);
    }

    public FilterLinearLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int totalWidth = MeasureSpec.getSize(widthMeasureSpec) + getPaddingLeft() + getPaddingRight();
        int totalHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int childCount = getChildCount();
        int lineHeight = 0;

        int xPos = 20;
        int yPos = 20;

        int childHeightMeasureSpec;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
        {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.AT_MOST);
        }
        else
        {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }


        for (int i = 0; i < childCount; ++i)
        {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE)
            {
                final LayoutParams params = (LayoutParams) child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                final int childWidth = child.getMeasuredWidth();
                lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + child.getPaddingTop());

                if (xPos + childWidth > totalWidth)
                {
                    xPos = 20;
                    yPos += lineHeight + 20;
                }

                xPos += childWidth + 20;
            }
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED)
        {
            totalHeight = yPos + lineHeight;
        }
        else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
        {
            if (yPos + lineHeight < totalHeight)
            {
                totalHeight = yPos + lineHeight;
            }
        }
        setMeasuredDimension(totalWidth, totalHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final int childCount = getChildCount();
        final int totalWidth = r - 1;
        int xPos = 20;
        int yPos = 20;

        for (int i = 0; i < childCount; ++i)
        {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE)
            {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                final LayoutParams params = (LayoutParams)child.getLayoutParams();
                if (xPos + childWidth > totalWidth)
                {
                    xPos = 20;
                    yPos += childHeight + 20;
                }
                child.layout(xPos, yPos, xPos + childWidth, yPos + childHeight);
                xPos += childWidth + 20;
            }
        }
    }

    private void initView(AttributeSet attrs)
    {
        TypedArray arr = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FilterLinearLayout, 0, 0);
        try
        {
            m_arrayEntriesID = arr.getResourceId(R.styleable.FilterLinearLayout_arrayentries, -1);
        }
        finally
        {
            arr.recycle();
        }
    }

    public void populateLayout(final OnFilterItemSelectedListener filterItemSelectedListener)
    {
        if (m_arrayEntriesID == -1)
            return;

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final String[] cardTypes = getResources().getStringArray(m_arrayEntriesID);
        int itemIndex = 0;
        for(String item : cardTypes)
        {
            View rowView = null;
            if (getOrientation() == VERTICAL)
            {
                rowView = layoutInflater.inflate(R.layout.simple_vertical_list_item, this, false);
            }
            else
            {
                rowView = layoutInflater.inflate(R.layout.simple_horizontal_list_item, this, false);
            }

            final Button itemButton = rowView.findViewById(R.id.item_button);
            final int itemButtonColour = defaultBackground;
            itemButton.setTag(itemIndex);
            itemButton.setText(item);

            boolean isSelected = filterItemSelectedListener.isFilterItemSelected(itemIndex);
            itemButton.setBackgroundColor(isSelected ? selectedBackground : itemButtonColour);
            itemButton.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = (int)itemButton.getTag();
                    boolean isSelected = filterItemSelectedListener.isFilterItemSelected(position);
                    if (!isSelected)
                    {
                        filterItemSelectedListener.onFilterItemSelected(position, true);
                        itemButton.setBackgroundColor(selectedBackground);
                    }
                    else
                    {
                        filterItemSelectedListener.onFilterItemSelected(position, false);
                        itemButton.setBackgroundColor(itemButtonColour);
                    }
                }
            });
            addView(rowView);
            itemIndex++;
        }
    }
}
