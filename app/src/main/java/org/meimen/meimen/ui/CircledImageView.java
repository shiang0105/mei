
package org.meimen.meimen.ui;

import org.meimen.meimen.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

/**
 * TODO Use Xfermode to do so.
 */
public class CircledImageView extends UriImageView {
    private static final int DEFAULT_BORDERWIDTH = 0;

    private static final int DEFAULT_BORDERCOLOR = Color.WHITE;

    private int borderWidth;

    private int viewWidth;

    private int viewHeight;

    private Bitmap image;

    private Paint paint;

    private Paint paintBorder;

    private BitmapShader shader;

    private ColorStateList borderColor;

    public CircledImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CircledImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CircledImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircledImageView, defStyle, 0);

        borderWidth = a.getDimensionPixelSize(R.styleable.CircledImageView_circle_border_width, DEFAULT_BORDERWIDTH);
        borderColor = a.getColorStateList(R.styleable.CircledImageView_circle_border_color);
        if (null == borderColor) {
            borderColor = ColorStateList.valueOf(DEFAULT_BORDERCOLOR);
        }

        paint = new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        paintBorder.setColor(borderColor.getDefaultColor());
        paintBorder.setAntiAlias(true);

        a.recycle();
    }

    /**
     * TODO Use Xfermode to do so.
     */
    private void loadBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            // check if image is changed. if so, shader should be set to null in order to recreate again
            if (null != image && bitmap != image) {
                shader = null;
            }

            image = bitmap;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        // load the bitmap
        loadBitmap();

        // init shader
        if (image != null) {
            if (null == shader)
                shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvas.getWidth(), canvas.getHeight(), false),
                        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            paint.setShader(shader);
            int circleCenter = viewWidth / 2;

            // circleCenter is the x or y of the view's center
            // radius is the radius in pixels of the cirle to be drawn
            // paint contains the shader that will texture the shape
            if (borderWidth > 0) {
                canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth, paintBorder);
            }
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec, widthMeasureSpec);

        viewWidth = width - (borderWidth * 2);
        viewHeight = height - (borderWidth * 2);

        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = viewWidth;

        }

        return result;
    }

    private int measureHeight(int measureSpecHeight, int measureSpecWidth) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = viewHeight;
        }
        return result;
    }

}
