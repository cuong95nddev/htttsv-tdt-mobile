package edu.tdt.appstudent2.views.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import edu.tdt.appstudent2.R;


/**
 * Created by Bichan on 6/22/2016.
 */
public class ImageViewFit extends ImageView {
    private float b;
    private float c;
    public ImageViewFit(Context context) {
        super(context);
    }

    public ImageViewFit(Context context, AttributeSet attrs) {
        super(context, attrs);
        a(attrs);
    }

    public ImageViewFit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        a(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageViewFit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        a(attrs);
    }
    private void a(AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.ImageViewFit);
        this.b = obtainStyledAttributes.getFloat(R.styleable.ImageViewFit_ratioHeight, -1.0f);
        this.c = obtainStyledAttributes.getFloat(R.styleable.ImageViewFit_ratioWidth, -1.0f);
    }
    @Override
    protected void onMeasure(int i, int i2) {
        if (this.b > 0.0f && this.c > 0.0f) {
            int size;
            int i3;
            size = MeasureSpec.getSize(i);
            i3 = (int) ((((float) size) / this.b) * this.c);
            i = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            i2 = MeasureSpec.makeMeasureSpec(i3, MeasureSpec.EXACTLY);
        }

        super.onMeasure(i, i2);
    }
}
