package swarm.swarmcomposer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import swarm.swarmcomposer.helper.Compatibility;
import swarm.swarmcomposer.model.Combination;
import swarm.swarmcomposer.model.Connection;


public class PdfGenerator {
    private Context context;
    private File mypath;

    /**
     * The PDF Generator generates a PDF File  with the combination.
     * It also generates a text describing the combination
     * @param context
     */
    public PdfGenerator(Context context) {
        this.context = context;
    }

    /**
     * Create and Save PDF File
     * @param combination The Combination
     * @param btmp  of Creation
     */
    public void createPDF(Combination combination, Bitmap btmp) {

        PdfDocument curPdfDocument = new PdfDocument();
        PageInfo pageInfo = new PageInfo.Builder(btmp.getWidth(), btmp.getHeight(), 1).create();
        PdfDocument.Page page = curPdfDocument.startPage(pageInfo);
        Canvas can = page.getCanvas();

        can.drawBitmap(btmp, 0, 0, null);
        curPdfDocument.finishPage(page);

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "swarmcomposer");
        if (!path.exists()) {
            path.mkdirs();
        }
        this.mypath = new File(path, combination.getName() + ".pdf");

        try {
            curPdfDocument.writeTo(new FileOutputStream(mypath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        curPdfDocument.close();
    }

    /**
     *  Create the Text for the combination
     * @param combination The combination
     * @return String of combination
     */
    public String createText(Combination combination) {

        int numberCombination = combination.getProductsInComb().size();
        int numberConnections = combination.getConnections().size();
        int possibleConnections = 0;
        int notPossibleConnections = 0;
        int alternatives = 0;
        String singleConnections = "";
        for (Connection iter : combination.getConnections()) {
            if (iter.getStatus() == Compatibility.COMPATIBLE) possibleConnections++;
            else if (iter.getStatus() == Compatibility.COMPATIBLE_WITH_ALTERNATIVE) {
                alternatives++;
                notPossibleConnections++;
            } else if (iter.getStatus() == Compatibility.NOT_COMPATIBLE) notPossibleConnections++;
            singleConnections += ("Die Verbindung zwischen " + iter.getProductStart().getProduct().getName() + " und " + iter.getProductTarget().getProduct().getName());
            switch (iter.getStatus()) {
                case COMPATIBLE:
                    singleConnections += (" ist möglich. \n");
                    break;
                case COMPATIBLE_WITH_ALTERNATIVE:
                    singleConnections += (" ist nicht direkt möglich, es gibt jedoch mindestens eine Alternative.\n");
                    break;
                case NOT_COMPATIBLE:
                    singleConnections += (" ist leider nicht möglich.\n");
                    break;
                default:
                    break;
            }

        }
        String returnThis = "Die Kombination besteht aus " + numberCombination + " Diensten und hat " + numberConnections + " Verbindungen." +
                " Hierbei sind " + possibleConnections + " Verbindungen möglich und  " + notPossibleConnections + " nicht";
        if (alternatives > 0) {
            returnThis += (", wobei es bei " + alternatives + " eine Alternative gibt, mit der diese Verbindung möglich wäre.\n");
        } else returnThis += (". \n");
        returnThis += (singleConnections);
        return returnThis;
    }

    /**
     *  This class opens the Email Intent and adds the body subject and Pdf File
     * @param Body  String for the Body of the Email
     * @param name  Name of combination for the Header
     */
    public void openEmail(String Body, String name) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "SWARM Composer: "+name);
        intent.putExtra(Intent.EXTRA_TEXT, Body);


        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, Uri.fromFile(mypath), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mypath));
        Log.i("TAGGG", mypath.toString());
        context.startActivity(intent);
    }


}
