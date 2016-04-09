
package org.meimen.meimen.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UriImageView extends ImageView {
    private final static String TAG = UriImageView.class.getSimpleName();
    private final static int GRADIENT_TOP_COLOR = 0x66DBDBDB;
    private final static int GRADIENT_BOTTOM_COLOR = 0x4C000000;

    private String mURL = null;
    private ScaleType mUserScaleType;

    private boolean mEnableTopCrop = false;

    public UriImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public UriImageView(Context context, AttributeSet pAttributes) {
        super(context, pAttributes);
        init(context, pAttributes, 0);
    }

    public UriImageView(Context context, AttributeSet pAttributes, int pDefaultStyle) {
        super(context, pAttributes, pDefaultStyle);
        init(context, pAttributes, pDefaultStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mUserScaleType = getScaleType();
        loadURL(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEnableTopCrop && getScaleType() != ScaleType.MATRIX) {
            recomputeTopCropImgMatrix();
        }
        try {
            super.onDraw(canvas);

        } catch (RuntimeException e) {
            Log.e(TAG, "Exception in onDraw, URL= = " + mURL, e);
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        // backup the ScaleType
        mUserScaleType = scaleType;
        super.setScaleType(scaleType);
    }

    public void loadURL(String url) {
        loadURL(url, null);
    }

    public void loadURL(String url, final ImageLoadingListener listener) {
        if (TextUtils.isEmpty(url)) {
            mURL = url;
            super.setScaleType(ScaleType.CENTER_INSIDE);
            ImageLoader.getInstance().displayImage(null, this);
            return;
        }

        // already set the image. ignore the request
        if (url != null && url.equals(mURL)) {
            return;
        }

        mURL = url;

        super.setScaleType(ScaleType.CENTER_INSIDE);

        ImageAware aware = new ImageViewAware(UriImageView.this) {
            @Override
            protected void setImageBitmapInto(Bitmap bitmap, View view) {
                if (mEnableTopCrop) {
                    setScaleType(ScaleType.MATRIX);
                    recomputeTopCropImgMatrix();
                }

                // restore the ScaleType
                if (mUserScaleType != null) {
                    UriImageView.this.setScaleType(mUserScaleType);
                }

                super.setImageBitmapInto(bitmap, view);
            }
        };

        ImageLoader.getInstance().displayImage(url, aware);
    }

    private void addGradientMask(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Shader mShader = new LinearGradient(0, 0, 0, bitmap.getHeight(), GRADIENT_TOP_COLOR, GRADIENT_BOTTOM_COLOR,
                Shader.TileMode.REPEAT);
        paint.setShader(mShader);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
    }

    /**
     * Pre-load image, save it to disk cache.
     *
     * @param sync synchronized
     */
    public static void preloadImage(String imageUrl, boolean sync) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }

        // TODO: Make it possible to put this image in memory-cache.
        if (sync) {
            ImageLoader.getInstance().loadImageSync(imageUrl);
        } else {
            ImageLoader.getInstance().loadImage(imageUrl, null);
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        if (mEnableTopCrop) {
            recomputeTopCropImgMatrix();
        }
        return super.setFrame(l, t, r, b);
    }

    private void recomputeTopCropImgMatrix() {
        final Matrix matrix = getImageMatrix();

        float scale;
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int drawableWidth = getDrawable().getIntrinsicWidth();
        final int drawableHeight = getDrawable().getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
    }

    public void setEnableTopCrop(boolean enableTopCrop) {
        mEnableTopCrop = enableTopCrop;
    }
}
