package swarm.swarmcomposer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import swarm.swarmcomposer.helper.CallBack;
import swarm.swarmcomposer.helper.ListElement;
import swarm.swarmcomposer.helper.customListAdapter;
import swarm.swarmcomposer.R;

/**
 * This Class shows a List of all the Products.
 * It is very similiar with the ListCombination class
 * For documentation check the above mentioned class
 */
public class ListProducts extends ListStuff implements CallBack {
    ArrayAdapter prodAdapter;
    boolean certified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageButton settingsProductButton = findViewById(R.id.settingProductButton);
        settingsProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListProducts.this, Settings.class);
                startActivity(intent);
            }
        });

        ImageButton combinationButton = findViewById(R.id.combButton);

        ImageButton refresh = findViewById(R.id.imageButtonRefreshPro);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInstance.getInstance().getData().setAllProducts(new ArrayList<ListElement>());
                Spinner dropDown = findViewById(R.id.spinnerDropDownProd);
                int i = dropDown.getSelectedItemPosition();
                switch (i) {
                    case 0:
                        //all
                        AppInstance.getInstance().Products(ListProducts.this, 0);
                        break;
                    case 1:
                        //Suchergebnisse
                        AppInstance.getInstance().Products(ListProducts.this, 1);
                        break;
                    default:
                        AppInstance.getInstance().Products(ListProducts.this, 0);
                        break;
                }
                updateListView(prodAdapter);
            }
        });

        combinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListProducts.this, ListCombination.class);
                startActivity(intent);
            }
        });
        ImageButton certifedBut = findViewById(R.id.imageButtonCertified);
        if (certified) {
            certifedBut.setBackgroundColor(Color.BLUE);
        } else certifedBut.setBackgroundColor(Color.GRAY);
        certifedBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner dropDown = findViewById(R.id.spinnerDropDownProd);
                EditText editTextSearch = findViewById(R.id.editTextViewProd);
                certified = !certified;
                if (certified) {
                    Toast.makeText(ListProducts.this, R.string.certOnly, Toast.LENGTH_SHORT).show();
                    v.setBackgroundColor(Color.BLUE);
                } else {
                    v.setBackgroundColor(Color.GRAY);
                    Toast.makeText(ListProducts.this, R.string.allProd, Toast.LENGTH_SHORT).show();
                }
                AppInstance.getInstance().getClient().searchProducts(ListProducts.this, 1, editTextSearch.getText().toString(), certified);
                dropDown.setSelection(1);
            }
        });
        Spinner dropDown = findViewById(R.id.spinnerDropDownProd);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.dropDownProduct, android.R.layout.simple_dropdown_item_1line);
        dropDown.setAdapter(arrayAdapter);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (i) {
                    case 0:
                        //all
                        AppInstance.getInstance().Products(ListProducts.this, 0);
                        break;
                    case 1:
                        //Suchergebnisse
                        AppInstance.getInstance().Products(ListProducts.this, 1);
                        break;
                    default:
                        AppInstance.getInstance().Products(ListProducts.this, 0);
                        break;
                }
                updateListView(prodAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);

            }
        });

        EditText editTextSearch = findViewById(R.id.editTextViewProd);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                Spinner dropDown = findViewById(R.id.spinnerDropDownProd);
                if (editable.toString().isEmpty()) {
                    dropDown.setSelection(0);
                } else {
                    AppInstance.getInstance().getClient().searchProducts(ListProducts.this, 1, editable.toString(), certified);
                    dropDown.setSelection(1);
                }
            }

        });


    }

    private void updateListView(ArrayAdapter prodAdapter) {

        ListView listView = findViewById(R.id.listViewProd);
        listView.setAdapter(prodAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = (Integer) view.getTag();
                Intent intent = new Intent(ListProducts.this, ShowProduct.class);
                Log.i("IDD", "" + id);
                Bundle bundle = new Bundle();
                bundle.putInt("ProductID", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_list_products;
    }


    @Override
    public void callBack(int i) {
        switch (i) {
            case 0:
                if (AppInstance.getInstance().getData().getAllProducts() != null)
                    prodAdapter = new customListAdapter(ListProducts.this, AppInstance.getInstance().getData().getAllProducts());
                else
                    prodAdapter = new customListAdapter(ListProducts.this, new ArrayList<ListElement>());
                break;
            case 1:
                if (AppInstance.getInstance().getData().getAllProducts() != null)
                    prodAdapter = new customListAdapter(ListProducts.this, AppInstance.getInstance().getData().getSearchProducts());
                else
                    prodAdapter = new customListAdapter(ListProducts.this, new ArrayList<ListElement>());
                break;
            case 99:
                Toast.makeText(this, R.string.networkError, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        updateListView(prodAdapter);
    }
}
