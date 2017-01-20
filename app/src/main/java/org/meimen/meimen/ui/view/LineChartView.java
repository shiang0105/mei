
package org.meimen.meimen.ui.view;

import java.util.ArrayList;

import org.meimen.meimen.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by yst on 9/7/15.
 */
public class LineChartView extends FrameLayout {

    private int mMax = 100;
    private int mMin = 0;
    private ArrayList<Pair<Integer, String>> mDataList;
    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Path mPath = new Path();
    private LinearLayout mXIndexLayout;

    private int mXIndexHeight = 60;

    public LineChartView(Context context) {
        this(context, null, 0);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDataList = new ArrayList<>();
        mDataList.add(new Pair<>(40, "S"));
        mDataList.add(new Pair<>(99, "1"));
        mDataList.add(new Pair<>(20, "2"));
        mDataList.add(new Pair<>(40, "3"));
        mDataList.add(new Pair<>(0, "4"));
        mDataList.add(new Pair<>(100, "5"));
        mDataList.add(new Pair<>(66, "6"));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getContext().getResources().getColor(R.color.powder_blue));
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(getContext().getResources().getColor(R.color.white_alpha_50));
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(25);

        mPath = new Path();

        mXIndexLayout = new LinearLayout(getContext());
        mXIndexLayout.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mXIndexHeight);
        params.gravity = Gravity.BOTTOM;
        mXIndexLayout.setLayoutParams(params);
        setXIndexLayout(mDataList);

        addView(mXIndexLayout);
    }

    private void setDataList(ArrayList<Pair<Integer, String>> dataList) {
        mDataList = dataList;
        setXIndexLayout(mDataList);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() / 3 + mXIndexHeight);

        super.onMeasure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() / 3 + mXIndexHeight, MeasureSpec.EXACTLY));
    }

    private void setXIndexLayout(ArrayList<Pair<Integer, String>> mDataList) {
        mXIndexLayout.removeAllViews();
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = 0; i < mDataList.size(); i++) {
                TextView tv = new TextView(getContext());
                tv.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
                tv.setTextColor(getContext().getResources().getColor(R.color.white_alpha_50));
                tv.setText(mDataList.get(i).second);
                mXIndexLayout.addView(tv);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int size = (mDataList == null) ? 0 : mDataList.size();

        if (size > 1) {
            float spanSize = width / (size - 1);
            drawChart(canvas, 0 + spanSize / 2, 0, width - spanSize, height - mXIndexHeight);
        } else if (size > 0) {
            // TODO
        }

    }

    private void drawChart(Canvas canvas, float startX, float startY, float width, float height) {
        int size = (mDataList == null) ? 0 : mDataList.size();
        float spanSize = width / (size - 1);
        for (int i = 0; i < mDataList.size() - 1; i++) {
            canvas.drawLine(startX + i * spanSize, startY + height - (height * mDataList.get(i).first / mMax), startX + (i + 1)
                    * spanSize, startY + height
                    - (height * mDataList.get(i + 1).first / mMax),
                    mPaint);

        }
    }

    private void drawXIndex(Canvas canvas, float startX, float startY, float width, float height) {
        int size = (mDataList == null) ? 0 : mDataList.size();
        float spanSize = width / (size - 1);
        for (int i = 0; i < mDataList.size(); i++) {
            canvas.drawText(mDataList.get(i).second, startX + i * spanSize, startY + height, mTextPaint);
        }
    }

}
