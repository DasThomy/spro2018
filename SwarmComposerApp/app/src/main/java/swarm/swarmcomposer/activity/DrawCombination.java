package swarm.swarmcomposer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import swarm.swarmcomposer.R;
import swarm.swarmcomposer.helper.ArcArrow;
import swarm.swarmcomposer.model.Combination;
import swarm.swarmcomposer.model.ProductInComb;

public class DrawCombination extends View {

    private final Context context;
    private Paint paint;
    private ArrayList<ArcArrow> arcarrows;
    private ArrayList<Bitmap> bitmaps;
    private float posX = 0;
    private float posY = 0;
    private double distanceOld = 0;
    private double distanceNew = 0;
    private float scale = 1;

    private ArrayList<ProductInComb> productInComb;
    private Bitmap btmpPDF;

    private boolean dontmove = false;
    private boolean moved = false;
    private float radius;

    private float newPosX = 0;
    private float newPosY = 0;
    private float oldPosX = 0;
    private float oldPosY = 0;
    private float finger1XOld = 0;
    private float finger1YOld = 0;



    private float oldScale = 1;
    Drawable drawable;
    Point point;
    int combId;

    public DrawCombination(Context context, ArrayList<ArcArrow> arcarrows,
                           int radius, ArrayList<ProductInComb> productInCombs, ArrayList<Bitmap> bitmaps, Point point, int combID) {
        super(context);
        this.radius = radius;
        this.context = context;
        this.arcarrows = arcarrows;
        this.productInComb = productInCombs;
        this.bitmaps = bitmaps;
        this.point= point;
        this.combId=combID;
        paint = new Paint(Color.BLACK);
        ImageView vectorDrawableCompat = new ImageView(context);
        vectorDrawableCompat.setImageResource(R.drawable.ic_share_alt_solid);
        drawable= vectorDrawableCompat.getDrawable();
        drawable.setTint(Color.rgb(199,199,199));
        if(point.x>point.y){
            int swap=point.x;
            //noinspection SuspiciousNameCombination
            point.x=point.y;
            point.y=swap;
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(context.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            drawable.setBounds(point.y-200,50,point.y-50,200);
        }else drawable.setBounds(point.x-200,50,point.x-50,200);
        canvas.translate(0, 0);
        canvas.scale(1, 1);
         drawable.draw(canvas);
        canvas.translate(posX, posY);
        canvas.scale(scale, scale);

        for (ArcArrow iter : arcarrows) {
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(iter.getPath(), paint);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawPath(iter.getArrow(),paint);
            iter.getDrawable().draw(canvas);
        }

        for (int i = 0; i < bitmaps.size(); i++) {
            canvas.drawBitmap(bitmaps.get(i), productInComb.get(i).getxPosition() - (bitmaps.get(i).getWidth() / 2), productInComb.get(i).getyPosition() - (bitmaps.get(i).getWidth() / 2), null);
        }




        invalidate();
    }


    private Bitmap takeScreenshot(View view) {

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();

        if(view.getDrawingCache() == null) return null;

        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount()==1){


            switch(event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    oldPosX = event.getX();
                    oldPosY = event.getY();
                    break;



                case MotionEvent.ACTION_UP:

                    if(!moved){
                        float firstX = (event.getX()-posX)/scale;
                        float firstY = (event.getY()-posY)/scale;
                        float absX= event.getRawX();
                        float absY= event.getRawY();
                        boolean opend=false;
                        for(ProductInComb iter: productInComb){
                            if(Math.sqrt(Math.pow(firstX-iter.getxPosition(),2)+Math.pow(firstY-iter.getyPosition(),2))<radius*scale){
                                Intent intent= new Intent(context, ShowProduct.class);
                                Bundle bundle= new Bundle();
                                bundle.putInt("ProductID",iter.getProduct().getId());
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                opend=true;
                                break;
                            }
                        }
                        if(!opend){
                            if(absX>point.x-200 && absY< 200){
                                PdfGenerator pdfGenerator= new PdfGenerator(context);
                                for(Combination combination: AppInstance.getInstance().getData().getCombinationList()){
                                    if(combination.getId()==combId){
                                        btmpPDF = takeScreenshot(DrawCombination.this);
                                        pdfGenerator.createPDF(combination, btmpPDF);
                                        pdfGenerator.openEmail(pdfGenerator.createText(combination),combination.getName());
                                        break;
                                    }
                                }

                            }
                        }

                    }
                    finger1XOld=0;
                    finger1YOld=0;
                    dontmove=false;
                    moved=false;
                    break;


                case MotionEvent.ACTION_MOVE:
                    if (!dontmove) {
                        newPosX = event.getX();
                        newPosY = event.getY();
                        posX += newPosX - oldPosX;
                        posY += newPosY - oldPosY;
                        oldPosX = newPosX;
                        oldPosY = newPosY;
                        moved = true;
                    }
                    break;

                default:
                    break;
            }

        } else if (event.getPointerCount() == 2) {
            //MOVE Stuff
            oldPosX = newPosX;
            oldPosY = newPosY;
             float finger2XOld;
             float finger2YOld;
             float finger1XNew;
             float finger1YNew;
             float finger2XNew;
             float finger2YNew;
             float minScale = 0.3F;
             float maxScale = 2F;
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    Log.i("PRESSEVENTS", "doublePressed");
                    break;
                case MotionEvent.ACTION_MOVE:

                    finger1XNew = event.getX(0);
                    finger1YNew = event.getY(0);
                    finger2XNew = event.getX(1);
                    finger2YNew = event.getY(1);
                    if (finger1XOld == 0 || finger1YOld == 0) {

                        dontmove = true;
                        oldScale = scale;
                        finger1XOld = finger1XNew;
                        finger1YOld = finger1YNew;
                        finger2XOld = finger2XNew;
                        finger2YOld = finger2YNew;
                        distanceOld = Math.sqrt(Math.pow(finger1XOld - finger2XOld, 2) + Math.pow(finger1YOld - finger2YOld, 2));
                    }
                    distanceNew = Math.sqrt(Math.pow(finger1XNew - finger2XNew, 2) + Math.pow(finger1YNew - finger2YNew, 2));

                    scale = (float) (distanceNew / distanceOld) - (1 - oldScale);
                    if (scale < minScale) scale = minScale;
                    if (scale > maxScale) scale = maxScale;
                    Log.i("scale", "Abstand Alt: " + distanceOld + "Abstand Neu: " + distanceNew);
                    Log.i("scale", "Scale: " + scale);


                    break;

                case MotionEvent.ACTION_UP:
                    Log.i("PRESSEVENTS", "done with TWO FINGERS");
                    break;
                default:
                    break;
            }


        }
        return true;
    }

    public void addToBitmaps(Bitmap textBitmap) {
        bitmaps.add(textBitmap);
    }
}
