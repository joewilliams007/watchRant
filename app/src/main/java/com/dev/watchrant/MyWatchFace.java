package com.dev.watchrant;

import static com.dev.watchrant.RantActivity.toast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.palette.graphics.Palette;

import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.MyApplication;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */

public class MyWatchFace extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create("sans-serif", Typeface.BOLD);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        Bitmap mBackgroundBitmap;
        Bitmap mGrayBackgroundBitmap;
        Bitmap mBackgroundScaledBitmap;
        Bitmap mGrayBackgroundScaledBitmap;

        Paint mBackgroundPaint;
        Paint mHandPaint;
        Paint mTextPaint;
        boolean mAmbient;
        Time mTime;

        /**
         * Handler to update the time once a second in interactive mode.
         */
        final Handler mUpdateTimeHandler = new EngineHandler(this);

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        boolean mRegisteredTimeZoneReceiver = false;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            Resources resources = MyWatchFace.this.getResources();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inDither = true;

            mBackgroundBitmap = Tools.loadImageFromStorage();
            if (mBackgroundBitmap == null) {
                toast("no avatar to set");
                mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_screen_full_black1);
            }

            mGrayBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_screen_full_black1);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);

            mHandPaint = new Paint();
            mHandPaint.setColor(Color.BLACK);

            mHandPaint.setAntiAlias(true);
            mHandPaint.setStrokeCap(Paint.Cap.ROUND);

            mTextPaint = new Paint();
            mTextPaint = createTextPaint(Color.WHITE);

            mTime = new Time();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mHandPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();

            if (mAmbient)
                canvas.drawColor(Color.BLACK);
            else
                canvas.drawColor(Color.BLACK);

            int width = bounds.width();
            int height = bounds.height();

            // Draw the background, scaled to fit.
            if (mBackgroundScaledBitmap == null
                    || mBackgroundScaledBitmap.getWidth() != width
                    || mBackgroundScaledBitmap.getHeight() != height) {
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                        width, height, true);
            }

            if (mGrayBackgroundScaledBitmap == null
                    || mGrayBackgroundScaledBitmap.getWidth() != width
                    || mGrayBackgroundScaledBitmap.getHeight() != height) {
                mGrayBackgroundScaledBitmap = Bitmap.createScaledBitmap(mGrayBackgroundBitmap,
                        width, height, true);
            }

            if (mAmbient) {
                canvas.drawBitmap(mGrayBackgroundScaledBitmap, 0, 0, mBackgroundPaint);
            } else {
                canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);
            }

            mHandPaint.setStrokeWidth(2);

            // Find the center. Ignore the window insets so that, on round watches with a
            // "chin", the watch face is centered on the entire screen, not just the usable
            // portion.
            float centerX = width / 2f;
            float centerY = height / 2f;

            float secRot = mTime.second / 30f * (float) Math.PI;
            int minutes = mTime.minute;
            float minRot = minutes / 30f * (float) Math.PI;
            float hrRot = ((mTime.hour + (minutes / 60f)) / 6f) * (float) Math.PI;

            float r1 = centerX - 85; // radius of the small conference
            float r2sec = centerX - 20;
            float r2min = centerX - 40;
            float r2hr = centerX - 60;

//          SECONDS
            if (!mAmbient) {
                float secXr1 = (float) Math.sin(secRot) * r1;
                float secYr1 = (float) -Math.cos(secRot) * r1;
                float secXr2 = (float) Math.sin(secRot) * r2sec;
                float secYr2 = (float) -Math.cos(secRot) * r2sec;
                mHandPaint.setColor(Color.WHITE);
                canvas.drawLine(centerX + secXr1, centerY + secYr1, centerX + secXr2, centerY + secYr2, mHandPaint);
            }

//          MINUTES
//            float minX = (float) Math.sin(minRot) * r2min;
//            float minY = (float) -Math.cos(minRot) * r2min;
            float minXr1 = (float) Math.sin(minRot) * r1;
            float minYr1 = (float) -Math.cos(minRot) * r1;
            float minXr2 = (float) Math.sin(minRot) * r2min;
            float minYr2 = (float) -Math.cos(minRot) * r2min;
//          IF DRAW A LINE
//            canvas.drawLine(centerX + minXr1, centerY + minYr1, centerX + minXr2, centerY + minYr2, mHandPaint);
//            mHandPaint.setStrokeWidth(getResources().getDimension(R.dimen.analog_hand_stroke_hours));
//          IF DRAW A CIRCLE
            mHandPaint.setStyle(Paint.Style.FILL);
            if (!mAmbient)
                mHandPaint.setColor(Color.BLACK);
            else
                mHandPaint.setColor(Color.WHITE);
            canvas.drawCircle(centerX + minXr2, centerY + minYr2, 5, mHandPaint);


//          HOURS
//            float hrX = (float) Math.sin(hrRot) * r2hr;
//            float hrY = (float) -Math.cos(hrRot) * r2hr;
            float hrXr1 = (float) Math.sin(hrRot) * r1;
            float hrYr1 = (float) -Math.cos(hrRot) * r1;
            float hrXr2 = (float) Math.sin(hrRot) * r2hr;
            float hrYr2 = (float) -Math.cos(hrRot) * r2hr;
//          IF DRAW A LINE
//            canvas.drawLine(centerX + hrXr1, centerY + hrYr1, centerX + hrXr2, centerY + hrYr2, mHandPaint);
//          IF DRAW A CIRCLE
            canvas.drawCircle(centerX + hrXr1, centerY + hrYr1, 8, mHandPaint);

            // display time in digital
            String text = mAmbient
                    ? String.format("%d:%02d:%02d", mTime.hour, mTime.minute, mTime.second)
                    : String.format("%d:%02d", mTime.hour, mTime.minute);
            mTextPaint.setTextSize(30);

//             shift up the digital time id there is a card
            float textYShift = centerY + 120;
            if (!getPeekCardPosition().isEmpty()) {
                textYShift = centerY - 85;
            }

            canvas.drawText(text,
                    computeXOffset(text, mTextPaint, bounds),
                    textYShift,//computeTimeYOffset(text, mTextPaint, bounds),
                    mTextPaint);

        }

        private float computeXOffset(String text, Paint paint, Rect watchBounds) {
            float centerX = watchBounds.exactCenterX();
            float timeLength = paint.measureText(text);
            return centerX - (timeLength / 2.0f);
        }

        private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
            float centerY = watchBounds.exactCenterY();
            Rect textBounds = new Rect();
            timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
            int textHeight = textBounds.height();
            return centerY + 200 + (textHeight / 2.0f);
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }
}