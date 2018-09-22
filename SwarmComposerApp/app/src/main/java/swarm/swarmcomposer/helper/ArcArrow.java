package swarm.swarmcomposer.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;



import swarm.swarmcomposer.R;


public class ArcArrow {

    int radius;
    int midx,midy;
    Context context;
    Point start;
    PointF mid;
    Point end;
    PointF control;


    private double vectorX;
    private double vectorY;


    int length;
    private int buffer;

    Path path;
    double arcScale = 0;

    PointF bufferedEnd;
    PointF bufferedStart;

    Path arrow;
    private Drawable drawable;

    Boolean bool;

    public ArcArrow(Context context, int radius, int product1X, int product1Y, int product2X, int product2Y, int buffer, Compatibility status,Boolean bool) {
        this.bool=bool;
        this.context=context;
        this.radius=radius;
        this.start=new Point(product1X,product1Y);
        this.end=new Point(product2X,product2Y);
        this.midx=start.x+((product2X-product1X)/2);
        this.midy=start.y+((product2Y-product1Y)/2);
        this.mid=new PointF(midx,midy);

        if(bool){this.arcScale = -100;}

        this.buffer=buffer + radius;
        
        this.length=(int)Math.sqrt((start.x - end.x)*(start.x- end.x) + (start.y - end.y)*(start.y - end.y));


        calcControl();
        calcBuffer();
        calcHead();
        calcPath();
        calcStatusLogo(status);


    }


    private void calcControl(){

        float xDiff         = start.x - mid.x;
        float yDiff         = start.y - mid.y;

       Float contr = (float)(Math.sqrt( Math.pow(xDiff,2) + Math.pow(yDiff,2)) );

       if(bool) {
           this.control = new PointF((float) (mid.x + (yDiff / contr * arcScale)), (float) (mid.y - (xDiff / contr * arcScale)));
       }
       else{
          this.control = mid;
       }


    }

    public void calcPath() {

        this.path = new Path();
        path.moveTo(bufferedStart.x, bufferedStart.y);
        path.cubicTo(bufferedStart.x, bufferedStart.y, control.x, control.y, bufferedEnd.x, bufferedEnd.y);


    }

    private void calcBuffer() {
        vectorX =((end.x-start.x)/Math.sqrt(Math.pow(end.x-start.x,2)+Math.pow(end.y-start.y,2)));
        vectorY = ((end.y-start.y)/Math.sqrt(Math.pow(end.x-start.x,2)+Math.pow(end.y-start.y,2)));

       Point  bufferdStart=new Point((int)(vectorX*(buffer)+start.x),
                                      (int)(vectorY*(buffer)+start.y));
       Point bufferdEnd= new Point(((int)(-vectorX*(buffer)+end.x)),
                                     (int)(-vectorY*(buffer)+end.y));


       if(bool) {

           this.bufferedStart = rotation(start, bufferdStart, -30f);
           this.bufferedEnd = rotation(end, bufferdEnd, 30f);
       }else{
           this.bufferedStart = new PointF(bufferdStart.x,bufferdStart.y);
           this.bufferedEnd = new PointF(bufferdEnd.x,bufferdEnd.y);
       }
    }


    private void calcHead() {


        int sizeTri = 15;

        Point triHead=new Point((int)(-vectorX*(buffer)+end.x),
                                 (int)(-vectorY*(buffer)+end.y));

        Point triMiddle=new Point((int)(-vectorX*(buffer+10)+end.x),
                                   (int)(-vectorY*(buffer+10)+end.y));

        Point oneCorner=new Point((int) (vectorY*sizeTri+triMiddle.x),
                                   (int) (-vectorX*sizeTri+triMiddle.y));

        Point secondCorner=new Point((int) ((-vectorY*sizeTri)+triMiddle.x),
                                     (int) ((vectorX*sizeTri)+triMiddle.y));

        arrow = new Path();


        PointF triHeadH = rotation(end,triHead,-30f);
        PointF oneCornerH = rotation(end,oneCorner,-30f);
        PointF secondCornerH = rotation(end,secondCorner,-30f);

        if(bool) {
            arrow.moveTo(triHeadH.x, triHeadH.y);
            arrow.lineTo(oneCornerH.x, oneCornerH.y);
            arrow.lineTo(secondCornerH.x, secondCornerH.y);
        }else{
            arrow.moveTo(triHead.x, triHead.y);
            arrow.lineTo(oneCorner.x, oneCorner.y);
            arrow.lineTo(secondCorner.x, secondCorner.y);
        }

        arrow.close();
    }

    public Drawable getDrawable() {
        return drawable;
    }

    private void calcStatusLogo(Compatibility status) {
        ImageView vectorDrawableCompat = new ImageView(context);
        switch (status) {
            case COMPATIBLE:
                vectorDrawableCompat.setImageResource(R.drawable.ic_check_solid);
                drawable = vectorDrawableCompat.getDrawable();
                drawable.setTint(Color.rgb(11, 195, 0));
                break;
            case COMPATIBLE_WITH_ALTERNATIVE:
                vectorDrawableCompat.setImageResource(R.drawable.ic_code_branch_solid);
                drawable = vectorDrawableCompat.getDrawable();
                drawable.setTint(Color.rgb(229, 213, 0));
                break;
            case NOT_COMPATIBLE:
                vectorDrawableCompat.setImageResource(R.drawable.ic_times_solid);
                drawable = vectorDrawableCompat.getDrawable();
                drawable.setTint(Color.rgb(229, 32, 32));
                break;
            default:
                break;
        }

        int size=30;
        int statusX= (int)(control.x-(size/2));
        int statusY= (int)(control.y-(size/2));

        drawable.setBounds(statusX,statusY,size+statusX, size+statusY);
    }

    public Path getPath(){
        return this.path;
    }

    public Path getArrow(){

        return this.arrow;
    }



    private PointF rotation(Point ps,Point pr, Float f){

        Matrix matrix = new Matrix();
        matrix.setRotate(f,ps.x,ps.y);
        float[] rotArray = new float[2];
        rotArray[0]= pr.x;
        rotArray[1]= pr.y;
        matrix.mapPoints(rotArray);

        return new PointF(rotArray[0],rotArray[1]);

    }
}
