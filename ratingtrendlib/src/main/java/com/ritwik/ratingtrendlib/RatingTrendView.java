package com.ritwik.ratingtrendlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;



public class RatingTrendView extends View {

    private static final String TAG = "Rating";

    /*Default values*/
    private static final float DEFAULT_STROKE_WIDTH = 3f;
    private static final float DEFAULT_SPACING = 12f;
    private static final float DEFAULT_CORNER_RADIUS = 8f;

    /***
     * User defined values
     */

    private int[] mRatingSequence;

    private int mOneStarStrokeColor;
    private int mOneStarFillColor;
    private int mTwoStarStrokeColor;
    private int mTwoStarFillColor;
    private int mThreeStarStrokeColor;
    private int mThreeStarFillColor;
    private int mFourStarStrokeColor;
    private int mFourStarFillColor;
    private int mFiveStarStrokeColor;
    private int mFiveStarFillColor;

    private float mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private float mCornerRadius = DEFAULT_CORNER_RADIUS;
    private float mSpacing = DEFAULT_SPACING;
    private int mStarIcon;


    private int mTotalWidth;
    private Rating mRating;


    public RatingTrendView(Context context) {
        this(context, null);
    }


    public RatingTrendView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //Focus on this constructor
    public RatingTrendView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }


    /***
     * Initializing user defined values
     *
     * @param context :
     * @param attrs :
     */
    private void initAttrs(Context context, AttributeSet attrs){

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingTrendView);
        mCornerRadius = typedArray.getDimension(R.styleable.RatingTrendView_rtv_cornerRadius,
                DEFAULT_CORNER_RADIUS);
        mStrokeWidth = typedArray.getDimension(R.styleable.RatingTrendView_rtv_strokeWidth,
                DEFAULT_STROKE_WIDTH);
        mStarIcon = typedArray.getResourceId(R.styleable.RatingTrendView_rtv_starIcon, R.drawable.ic_star);


        /***
         * Initializing colours
         */
        mOneStarStrokeColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_oneStarStrokeColor,
                getResources().getColor(R.color.oneStarStroke));
        mOneStarFillColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_oneStarFillColor,
                getResources().getColor(R.color.oneStarFill));

        mTwoStarStrokeColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_twoStarStrokeColor,
                getResources().getColor(R.color.twoStarStroke));
        mTwoStarFillColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_twoStarFillColor,
                getResources().getColor(R.color.twoStarFill));

        mThreeStarStrokeColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_threeStarStrokeColor,
                getResources().getColor(R.color.threeStarStroke));
        mThreeStarFillColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_threeStarFillColor,
                getResources().getColor(R.color.threeStarFill));

        mFourStarStrokeColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_fourStarStrokeColor,
                getResources().getColor(R.color.fourStarStroke));
        mFourStarFillColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_fourStarFillColor,
                getResources().getColor(R.color.fourStarFill));

        mFiveStarStrokeColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_fiveStarStrokeColor,
                getResources().getColor(R.color.fiveStarStroke));
        mFiveStarFillColor = typedArray.getColor(R.styleable.RatingTrendView_rtv_fiveStarFillColor,
                getResources().getColor(R.color.fiveStarFill));

        typedArray.recycle();

    }

    private int getDefaultWidth(){

        //return whatever :-)
        return 20;
    }
    private int getDefaultHeight(int measureSpec){
        int width = MeasureSpec.getSize(measureSpec);
        float actualWidth = (width - (8 * mStrokeWidth) - (7* mSpacing))/8;
        return (int) ((int) (actualWidth / Rating.WIDTH_BY_HEIGHT_RATIO) + getPaddingBottom() + getPaddingTop() + (0.5 * mStrokeWidth));
    }

    private int getExpectedSize(int size, int measureSpec){

        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
            default:
                break;
        }
        return result;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defWidth = getDefaultWidth();
        int defHeight = getDefaultHeight(widthMeasureSpec);

        mTotalWidth = getExpectedSize(defWidth, widthMeasureSpec);

        setMeasuredDimension(getExpectedSize(defWidth, widthMeasureSpec),
                getExpectedSize(defHeight, heightMeasureSpec));
    }



    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if (mRatingSequence == null){
            return;
        }
        int bw = getWidthOfEachRatng();

        //compensate the padding
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.save();
        mRating.setmWidth(bw);


        for (int i=0; i<mRatingSequence.length; i++){
            prepareRatingItem(mRatingSequence[i]);
            mRating.drawSelf(canvas);
            canvas.translate(bw + mSpacing, 0);

        }

        canvas.restore();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


    }

    /***
     * Setting last 8 rating Sequence
     * @param ratingSeq : array [5,4,2,1,2,1,1,4]
     *
     */
    public void setRatingSequence(int[] ratingSeq){

        if (ratingSeq.length > 8){
            throw new IllegalArgumentException("More than 8 sequence is not supported supported");
        }

        for (int x :ratingSeq){
            if (x<1 || x>5){
                throw new IllegalArgumentException("Max Rating value must be 5");
            }
        }
        this.mRatingSequence = ratingSeq;
        mRating = new Rating(mStarIcon,mCornerRadius, mStrokeWidth, getContext());
        invalidate();
    }


    private int getWidthOfEachRatng(){
        int availableWidth = (int) (mTotalWidth - (7*mSpacing) - getPaddingRight() - getPaddingLeft());
        return availableWidth/8;
    }



    private void prepareRatingItem(int value){

        mRating.setmValue(value);
        switch (value){
            case 1:
                mRating.setmFillColor(mOneStarFillColor);
                mRating.setmStrokeColor(mOneStarStrokeColor);
                break;

            case 2:
                mRating.setmStrokeColor(mTwoStarStrokeColor);
                mRating.setmFillColor(mTwoStarFillColor);
                break;

            case 3:
                mRating.setmFillColor(mThreeStarFillColor);
                mRating.setmStrokeColor(mThreeStarStrokeColor);
                break;

            case 4:
                mRating.setmStrokeColor(mFourStarStrokeColor);
                mRating.setmFillColor(mFourStarFillColor);
                break;

            case 5:
                mRating.setmFillColor(mFiveStarFillColor);
                mRating.setmStrokeColor(mFiveStarStrokeColor);
                break;
        }

    }
}
