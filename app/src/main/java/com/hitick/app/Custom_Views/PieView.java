package com.hitick.app.Custom_Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.hitick.app.R;

/**
 * PieView displaying the PieChart
 */
public class PieView extends View {
    // Instance variables for storing the custom attributes

    // Color of the three slices in our Pie Chart
    private static int color_slice_A;
    private static int color_slice_B;
    private static int color_slice_C;

    // Color of the Timer ring outside the PieChart
    private static int color_timer_ring;

    // Variables for storing the Timer Text and Label Text Size
    private static int timerTextSize;
    private static int sliceLabelSize;

    // Variables for storing the details of the pieChart
    private static int pieRadius;
    private static int pieWidth;

    // Data for the PieChart
    private static int[] pieData;

    public PieView(Context context,int[] pieData) {
        super(context);
        this.pieData = pieData.clone();
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context, attrs);
    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttributes(context, attrs);
    }

    // Helper method to handle the custom attributes declared styleable in XML
    private static void handleAttributes(Context context, AttributeSet attrs) {
        // Obtain the styled attributes in a typed array
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.PieView, 0, 0);

        try {
            color_slice_A = array.getInteger(R.styleable.PieView_slice_A_Color, 0);
            color_slice_B = array.getInteger(R.styleable.PieView_slice_B_Color, 0);
            color_slice_C = array.getInteger(R.styleable.PieView_slice_C_Color, 0);

            color_timer_ring = array.getInteger(R.styleable.PieView_timer_ringColor, 0);

            sliceLabelSize = array.getInteger(R.styleable.PieView_slice_labelSize, 0);
            timerTextSize = array.getInteger(R.styleable.PieView_timer_textSize, 0);

            pieRadius = array.getInteger(R.styleable.PieView_pieRadius, 0);
            pieWidth = array.getInteger(R.styleable.PieView_pieWidth, 0);
        } finally {
            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Determine the radius that can be used for the pie

    }

    // Getter Setter Pairs for each styleable attribute
    public int getColor_slice_A() {
        return color_slice_A;
    }

    public int getColor_slice_B() {
        return color_slice_B;
    }

    public int getColor_slice_C() {
        return color_slice_C;
    }

    public int getColor_timer_ring() {
        return color_timer_ring;
    }

    public int getTimerTextSize() {
        return timerTextSize;
    }

    public int getSliceLabelSize() {
        return sliceLabelSize;
    }

    public int getPieRadius() {
        return pieRadius;
    }

    public int getPieWidth() {
        return pieWidth;
    }

    public int[] getPieData(){
        return pieData;
    }

    public void setColor_slice_A(int color_slice_a){
        this.color_slice_A = color_slice_a;
        invalidate();
    }

    public void setColor_slice_B(int color_slice_b){
        this.color_slice_B = color_slice_b;
        invalidate();
    }

    public void setColor_slice_C(int color_slice_c){
        this.color_slice_C = color_slice_c;
        invalidate();
    }

    public void setColor_timer_ring(int color_timer_ring){
        this.color_timer_ring = color_timer_ring;
        invalidate();
    }

    public void setTimerTextSize(int timerTextSize){
        this.timerTextSize = timerTextSize;
        invalidate();
    }

    public void setSliceLabelSize(int sliceLabelSize){
        this.sliceLabelSize = sliceLabelSize;
        invalidate();
    }

    public void setPieRadius(int pieRadius){
        this.pieRadius = pieRadius;
        invalidate();
    }

    public void setPieWidth(int pieWidth){
        this.pieWidth = pieWidth;
        invalidate();
    }

    public void setPieData(int[] pieData){
        this.pieData = pieData.clone();
        invalidate();
    }
}
