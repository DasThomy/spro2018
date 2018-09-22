package swarm.swarmcomposer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import swarm.swarmcomposer.helper.CallBackwithObject;
import swarm.swarmcomposer.helper.customListAdapterProdDetails;
import swarm.swarmcomposer.model.Product;
import swarm.swarmcomposer.R;

/**
 * In this class we calculate the Product Page and Visualise it
 */
public class ShowProduct extends AppCompatActivity implements CallBackwithObject {
    int prodID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;//ERROR
        prodID = bundle.getInt("ProductID");
        AppInstance.getInstance().getProduct(ShowProduct.this, prodID, 0);

        ImageView productLogo = findViewById(R.id.productLogo);
        final ViewGroup.LayoutParams oldParams = productLogo.getLayoutParams();
        final RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(500, 500);
        productLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getLayoutParams() == oldParams) {
                    view.setLayoutParams(newParams);
                } else {
                    view.setLayoutParams(oldParams);
                }
            }
        });
    }

    /**
     *  In this class we add all of the information and the "Titles" of the information into an ArrayList
     *  so we can call our ArrayAdapter to create a nice List
     * @param i
     * @param object The Product we want to display
     */
    @Override
    public void callBack(int i, Object object) {
        Product findProduct  = (Product) object;
        if (findProduct == null) return; //ERROR
        ImageView productLogo = findViewById(R.id.productLogo);
        productLogo.setImageBitmap(findProduct.getLogo());
        final Product product = findProduct;
        TextView textView = findViewById(R.id.productName);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45f);
        textView.setText(product.getName());
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        arrayLists.add(new ArrayList<String>() {{
            add(product.getName());
        }});
        arrayLists.add(new ArrayList<String>() {{
            add(product.getOrganisation());
        }});
        arrayLists.add(new ArrayList<String>() {{
            add(product.getVersion());
        }});
        Date date = new Date(product.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy");
        final String dateString = simpleDateFormat.format(date);
        arrayLists.add(new ArrayList<String>() {{
            add(dateString);
        }});
        arrayLists.add(product.getTags());
        arrayLists.add(product.getFormatIn());
        arrayLists.add(product.getFormatOut());

        ArrayList<String> arrayListTitles = new ArrayList<>();
        arrayListTitles.add(getResources().getString(R.string.name) + ": ");
        arrayListTitles.add(getResources().getString(R.string.organisation) + ": ");
        arrayListTitles.add(getResources().getString(R.string.version) + ": ");
        arrayListTitles.add(getResources().getString(R.string.date) + ": ");
        arrayListTitles.add(getResources().getString(R.string.tags) + ": ");
        arrayListTitles.add(getResources().getString(R.string.formatIn) + ": ");
        arrayListTitles.add(getResources().getString(R.string.formatOut) + ": ");

        ListView listView = findViewById(R.id.listViewOneProd);
        ArrayAdapter prodAdapter = new customListAdapterProdDetails(this, arrayLists, arrayListTitles);
        listView.setAdapter(prodAdapter);
    }
}
