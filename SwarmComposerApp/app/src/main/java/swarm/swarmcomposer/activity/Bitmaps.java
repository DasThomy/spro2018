package swarm.swarmcomposer.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class Bitmaps {

    /**
     * @param srcBitmap bitmap to cut
     * @return bitmap in circular form
     */
    Bitmap getCircularBitmap(Bitmap srcBitmap) {
        int squareBitmapWidth = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());

        Bitmap dstBitmap = Bitmap.createBitmap(
                squareBitmapWidth,
                squareBitmapWidth,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(dstBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Rect rect = new Rect(0, 0, squareBitmapWidth, squareBitmapWidth);
        RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float left = (squareBitmapWidth - srcBitmap.getWidth()) / 2;
        float top = (squareBitmapWidth - srcBitmap.getHeight()) / 2;

        canvas.drawBitmap(srcBitmap, left, top, paint);
        //srcBitmap.recycle();

        return dstBitmap;
    }

    /**
     * @param srcBitmap   given bitmap to create the border
     * @param borderWidth preferred border with
     * @param borderColor preferred border color
     * @return bitmap with border
     */
    Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor) {
        int dstBitmapWidth = srcBitmap.getWidth() + borderWidth * 2;

        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);


        canvas.drawCircle(
                canvas.getWidth() / 2,
                canvas.getWidth() / 2,
                canvas.getWidth() / 2 - borderWidth / 2,
                paint
        );
        srcBitmap.recycle();

        return dstBitmap;
    }

    /**
     * @param srcBitmap    bitmap who need to be scaled down
     * @param maxImageSize maximum size of the bitmap (pixel)
     * @param filter       decides if the source should be filtered
     * @return scaled down bitmap
     */
    static Bitmap scaleDown(Bitmap srcBitmap, float maxImageSize,
                            boolean filter) {
        float ratio = Math.min(
                maxImageSize / srcBitmap.getWidth(),
                maxImageSize / srcBitmap.getHeight());
        int width = Math.round(ratio * srcBitmap.getWidth());
        int height = Math.round(ratio * srcBitmap.getHeight());

        return Bitmap.createScaledBitmap(srcBitmap, width,
                height, filter);
    }

    /**
     * @param src      the bitmap where to create the text
     * @param string   the name of the product
     * @param location starting point of the text
     * @return bitmap with text written on it
     */
    static Bitmap drawStringOnBitmap(Bitmap src, String string, Point location) {

        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.WHITE);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStyle(Paint.Style.FILL);

        char[] productName = string.toCharArray();
        int stringLength = productName.length;


        if (string.length() > 15) {
            for (int i = 0; i < stringLength; i++) {
                if (productName[i] == ' ') {
                    float widthShort = paint.measureText(string, 0, string.substring(0, i).length());
                    float widthLong = paint.measureText(string, string.substring(0, i + 1).length(), stringLength);
                    //Top textbox
                    canvas.drawRect(location.x, src.getHeight() - (paint.getTextSize() * 2),
                            widthShort, src.getHeight() - paint.getTextSize(), rectPaint);
                    canvas.drawText(string.substring(0, i), location.x, location.y - 2 - paint.getTextSize(), paint);
                    //Bottom textbox
                    canvas.drawRect(location.x, src.getHeight() - paint.getTextSize(),
                            widthLong, src.getHeight() - 2, rectPaint);
                    canvas.drawText(string.substring(i + 1, stringLength), location.x, location.y - 2, paint);

                    return result;
                }
            }
        } else {
            float widthString = paint.measureText(string, 0, stringLength);
            canvas.drawRect(location.x, src.getHeight() - paint.getTextSize(),
                    widthString, src.getHeight() - 2, rectPaint);
            canvas.drawText(string, location.x, location.y - 2, paint);
        }

        return result;
    }


}
