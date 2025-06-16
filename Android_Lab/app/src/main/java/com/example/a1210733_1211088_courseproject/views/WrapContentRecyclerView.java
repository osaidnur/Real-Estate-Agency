package com.example.a1210733_1211088_courseproject.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Custom RecyclerView that wraps its content height dynamically
 */
public class WrapContentRecyclerView extends RecyclerView {
    
    public WrapContentRecyclerView(@NonNull Context context) {
        super(context);
    }
    
    public WrapContentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    public WrapContentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (getLayoutManager() == null || getAdapter() == null) {
            super.onMeasure(widthSpec, heightSpec);
            return;
        }
        
        int itemCount = getAdapter().getItemCount();
        if (itemCount == 0) {
            super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
            return;
        }
        
        // Measure the height of all items
        int totalHeight = 0;
        int parentWidth = MeasureSpec.getSize(widthSpec);
        
        for (int i = 0; i < itemCount; i++) {
            ViewHolder viewHolder = getAdapter().createViewHolder(this, getAdapter().getItemViewType(i));
            getAdapter().onBindViewHolder(viewHolder, i);
            
            viewHolder.itemView.measure(
                MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            );
            
            totalHeight += viewHolder.itemView.getMeasuredHeight();
        }
        
        // Add padding
        totalHeight += getPaddingTop() + getPaddingBottom();
        
        // Set the measured height
        setMeasuredDimension(
            MeasureSpec.getSize(widthSpec),
            totalHeight
        );
    }
}
