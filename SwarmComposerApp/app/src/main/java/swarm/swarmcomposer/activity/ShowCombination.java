package swarm.swarmcomposer.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import swarm.swarmcomposer.helper.ArcArrow;
import swarm.swarmcomposer.helper.CallBackwithObject;
import swarm.swarmcomposer.model.Combination;
import swarm.swarmcomposer.model.Connection;
import swarm.swarmcomposer.model.ProductInComb;
import swarm.swarmcomposer.R;

/**
 * This Page calculates which Objects are going to be drawn in the next Class the DrawCombination
 * The Objects are setup here, though they are not created here
 */
public class ShowCombination extends AppCompatActivity implements CallBackwithObject {


    DrawCombination drawCombination;
    ArrayList<ArcArrow> arcarrows;
    ArrayList<Bitmap> bitmaps;
    private int combID;
    Bitmaps bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arcarrows = new ArrayList<>();
        bitmaps = new ArrayList<>();
        bitmap = new Bitmaps();

        Bundle bundle = getIntent().getExtras();
        combID = bundle.getInt("KombinationID");
        Log.i("IDDD", "" + combID);
        AppInstance.getInstance().getCombination(ShowCombination.this, 0, combID);

    }

    // radius of the Product
    private int radius = 50;

    /**
     * @param productInCombList List of all Products
     *                          This Methods adds all Products to be drawn
     */
    private void drawProducts(ArrayList<ProductInComb> productInCombList) {
        for (ProductInComb product : productInCombList) {
            product.getProduct().createLogo();

            if (product.getProduct().getName() == null) product.getProduct().setName("");
            ImageView circleImage = new ImageView(ShowCombination.this);
            if (product.getProduct().getLogo() == null)
                circleImage.setImageResource(R.drawable.facility_management_4);
            else circleImage.setImageBitmap(product.getProduct().getLogo());

            Resources mResources = getResources();
            Bitmap mBitmap;
            if (product.getProduct().getLogo() == null) {
                final int bitmapResourceID = R.drawable.facility_management_4;

                circleImage.setImageBitmap(BitmapFactory.decodeResource(mResources, bitmapResourceID));

                mBitmap = BitmapFactory.decodeResource(mResources, bitmapResourceID);
            } else {
                mBitmap = product.getProduct().getLogo();
            }
            mBitmap = bitmap.getCircularBitmap(mBitmap);

            mBitmap = bitmap.addBorderToCircularBitmap(mBitmap, 10, Color.BLACK);

            Bitmap scaledBitmap = Bitmaps.scaleDown(mBitmap, 120, true);

            circleImage.setImageBitmap(scaledBitmap);
            Bitmap textBitmap = Bitmaps.drawStringOnBitmap(scaledBitmap, product.getProduct().getName(),
                    new Point(0, scaledBitmap.getHeight()));

            //bitmaps.add(textBitmap);
            drawCombination.addToBitmaps(textBitmap);

        }


    }

    /**
     * @param connection a single Connection
     *                   Draws a straight Line Connection
     */
    private void drawConnection(Connection connection) {
        arcarrows.add(new ArcArrow(ShowCombination.this, radius, connection.getProductStart().getxPosition(), connection.getProductStart().getyPosition(),
                connection.getProductTarget().getxPosition(), connection.getProductTarget().getyPosition(), 20, connection.getStatus(),false));

    }

    /**
     * @param connection one way direction
     *                   draws a curved Connection
     */
    private void drawDoubleConnection(Connection connection) {
        arcarrows.add(new ArcArrow(ShowCombination.this, radius, connection.getProductStart().getxPosition(), connection.getProductStart().getyPosition(),
                connection.getProductTarget().getxPosition(), connection.getProductTarget().getyPosition(), 20, connection.getStatus(),true));

    }


    Combination combination = null;

    /**
     *
     * @param i          method of caller
     * @param object     called upton object
     */
    @Override
    public void callBack(int i, Object object) {

        switch (i) {
            case 0:

                combination = (Combination) object;
                if (combination == null) return; //ERROR
                updateConnections(combination);

                /*
                 * The Next Part runs through all connections and checks if we have a "double Connection".
                 * If it is succsessfull it orders the double Connection to be a round one
                 * if the connection doesn´t have a counter Part it is a straight line
                 */
                ArrayList<Connection> connectionList = combination.getConnections();
                for (int singleRun = 0; singleRun < connectionList.size(); singleRun++) {
                    boolean drawn = false;
                    for (int checkDouble = 0; checkDouble < connectionList.size(); checkDouble++) {
                        if (connectionList.get(singleRun).getSourceProduct() == connectionList.get(checkDouble).getTargetProduct() &&
                                connectionList.get(checkDouble).getSourceProduct() == connectionList.get(singleRun).getTargetProduct()) {
                            /// This Connection is going to be a double Line
                            drawDoubleConnection(connectionList.get(singleRun));
                            drawn = true;
                            break;
                        }
                    }
                    // And this one a straight line
                    if (!drawn) drawConnection(connectionList.get(singleRun));
                }
                Point point = new Point();
                getWindowManager().getDefaultDisplay().getSize(point);
                drawCombination = new DrawCombination(this, arcarrows, radius,
                        combination.getProductsInComb(), bitmaps, point, combID);
                drawProducts(combination.getProductsInComb());
                drawCombination.setBackgroundColor(Color.TRANSPARENT);
                setContentView(drawCombination);


                break;
            case 1:
                break;

            case -1:
                Toast.makeText(this, R.string.networkError, Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /**
     * sets the Start and Target Product according to the ID
     * @param combination The combination
     */
    private void updateConnections(Combination combination) {

        for (Connection iter : combination.getConnections()) {
            for (ProductInComb product : combination.getProductsInComb()) {
                if (iter.getSourceProduct() == product.getId()) iter.setProductStart(product);
                else if (iter.getTargetProduct() == product.getId()) iter.setProductTarget(product);
            }
        }
    }

}
