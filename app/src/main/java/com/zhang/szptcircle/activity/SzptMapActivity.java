package com.zhang.szptcircle.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.zhang.szptcircle.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SzptMapActivity extends AppCompatActivity {

    private ImageView imageViewECG = null;

    private Matrix matrix = null;
    private Matrix saveMatrix = null;

    private boolean isFirstTouch = true;
    private float maxScale = 2f;
    private float minScale = 0.1f;

    private float imageWidth = 1.0f;
    private float imageHeight = 1.0f;

    private float imageWidthInit = 1.0f;
    private float imageHeightInit = 1.0f;
    private float paddingScreenMin = 10.0f;
    private float paddingScreenMax = 10.0f;

    private PointF startPoint = null;
    private PointF middlePoint = null;

    private float oldDistance = 1f;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private float[] matrixValues = null;

    private TextView tvBackMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szpt_map);
        tvBackMap = findViewById(R.id.tv_backmap);
        tvBackMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        this.matrix = new Matrix();
        this.saveMatrix = new Matrix();
        this.matrixValues = new float[9];

        this.startPoint = new PointF();

        this.imageViewECG = this.findViewById(R.id.iv_szptmap);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.szptmap);
        this.imageWidth = bitmap.getWidth();
        this.imageHeight = bitmap.getHeight();

        this.imageViewECG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Initial the image view
                ImageView imageView = (ImageView)v;
                imageView.setScaleType(ImageView.ScaleType.MATRIX);

                // Initial the matrix
                matrix.set(imageView.getImageMatrix());

                // Initial the
                if(isFirstTouch){
                    isFirstTouch = false;
                    // Get the values of matrix
                    matrix.getValues(matrixValues);
                    // Initial the width of screen, scaleX = matrixValues[0], scaleY = matrixValues[4]
                    minScale = matrixValues[0];

                    // Initial the width and height
                    imageWidthInit = imageWidth*minScale;
                    imageHeightInit = imageHeight*minScale;
                    paddingScreenMax = matrixValues[2];
                }

                // Set the gestures
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    // Single finger
                    case MotionEvent.ACTION_DOWN:
                        matrix.set(imageView.getImageMatrix());
                        saveMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;

                    // Double fingers
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDistance = distanceDoubleFinger(event);
                        if(distanceDoubleFinger(event)>10f){
                            middlePoint = middlePoint(event);
                            saveMatrix.set(matrix);
                            mode = ZOOM;
                        }
                        break;

                    // Finger slide
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            // One finger slide
                            slideImage(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            // The double finger slide
                            float newDistance = distanceDoubleFinger(event);

                            // Get the values of matrix
                            matrix.getValues(matrixValues);
                            float newScale = newDistance/oldDistance;
                            float realNewScale = matrixValues[0] * newScale;

                            // Limit zooming
                            if (newDistance > 10f && (realNewScale>=minScale && realNewScale<=maxScale) ) {
                                matrix.set(saveMatrix);
                                matrix.postScale(newScale, newScale, middlePoint.x, middlePoint.y);
                            }
                        }
                        break;

                    // Reset the double finger
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                }
                // Reset the image view
                imageView.setImageMatrix(matrix);

                return true;
            }
        });

    }


    /**
     * @param event The touch of event
     * @return The distance of two point
     */
    private float distanceDoubleFinger(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Float.valueOf(String.valueOf(Math.sqrt(x * x + y * y))) ;
    }

    /*
     * @param event The touch of event
     * @return The PointF of middle
     */
    private PointF middlePoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    /**
     *  Slide the image
     * @param distanceX Horizontal sliding distance
     * @param distanceY Vertical sliding distance
     */
    private void slideImage(float distanceX, float distanceY){
        // Get the values
        float[] slideMatrixValue = new float[9];
        this.matrix.getValues(slideMatrixValue);
        float slideMatrixDX = slideMatrixValue[2];
        float slideMatrixDY = slideMatrixValue[5];

        float realWidth = this.imageWidth * slideMatrixValue[0];
        float realHeight = this.imageHeight * slideMatrixValue[0];

        // There are four cases for image sliding
        if(distanceY >= 0){// Slide bottom
            if(slideMatrixDY+distanceY < this.paddingScreenMin){
                slideLeftRight(distanceX, distanceY, realWidth, slideMatrixDX);
            }
        }else { // distanceY < 0, Slide top
            if(-(realHeight-this.imageHeightInit+slideMatrixDY+distanceY) < this.paddingScreenMin){
                slideLeftRight(distanceX, distanceY, realWidth, slideMatrixDX);
            }
        }
    }

    /**
     * Slide the image left and right
     * @param distanceX Horizontal slide distance
     * @param distanceY Vertical slide distance
     * @param realWidth The width of zoomed image
     * @param slideMatrixDX The horizontal slide distance recorded in matrix
     */
    private void slideLeftRight(float distanceX, float distanceY, float realWidth, float slideMatrixDX){
        // Get the width of screen
        float screenWidth = this.imageWidthInit+2*paddingScreenMax;

        if (distanceX >= 0) {// distanceX >= 0
            // Swipe to the right
            if(realWidth > screenWidth){
                // The size of the enlarged picture is larger than that of the screen.
                if((slideMatrixDX+distanceX) < this.paddingScreenMin) {
                    this.matrix.set(this.saveMatrix);
                    this.matrix.postTranslate(distanceX, distanceY);
                }
            }else {
                if((slideMatrixDX+distanceX) < this.paddingScreenMax) {
                    this.matrix.set(this.saveMatrix);
                    this.matrix.postTranslate(distanceX, distanceY);
                }
            }
        } else {// distanceX < 0
            // Swipe to the left
            if(realWidth > screenWidth){
                // The size of the enlarged picture is larger than that of the screen.
                if((screenWidth-realWidth-slideMatrixDX-distanceX) < this.paddingScreenMin) {
                    this.matrix.set(this.saveMatrix);
                    this.matrix.postTranslate(distanceX, distanceY);
                }
            }else {
                if((screenWidth-realWidth-slideMatrixDX-distanceX) < this.paddingScreenMax) {
                    this.matrix.set(this.saveMatrix);
                    this.matrix.postTranslate(distanceX, distanceY);
                }
            }
        }
    }


}