package swarm.swarmcomposer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import swarm.swarmcomposer.helper.CallBack;
import swarm.swarmcomposer.helper.ListElement;
import swarm.swarmcomposer.helper.customListAdapter;
import swarm.swarmcomposer.R;
import swarm.swarmcomposer.restclient.RestClient;


/**
 * Displays a List of all available Combinations, categorised into different Groups
 */
public class ListCombination extends ListStuff implements CallBack {
    ArrayAdapter prodAdapter;
    int curSection = 0;
    RestClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageButton settingsCombButton = findViewById(R.id.settingCombButton);
        settingsCombButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCombination.this, Settings.class);
                startActivity(intent);
            }
        });

        ImageButton productsButton = findViewById(R.id.productButton);
        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCombination.this, ListProducts.class);
                startActivity(intent);
            }
        });

        ImageButton refresh = findViewById(R.id.imageButtonRefreshComb);
/*
The Refresh Button gets new Data from server even if there is local data
 */
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInstance.getInstance().getData().setAllCombinationPublic(new ArrayList<ListElement>());
                AppInstance.getInstance().getData().setAllCombinationOwn(new ArrayList<ListElement>());
                AppInstance.getInstance().getData().setAllCombinationShared(new ArrayList<ListElement>());
                Spinner dropDown = findViewById(R.id.spinnerDropDownKomb);
                int i = dropDown.getSelectedItemPosition();
                switch (i) {
                    case 0:
                        // Meine
                        AppInstance.getInstance().getClient().CombinationsOwn(ListCombination.this, 0);
                        break;
                    case 1:
                        //shared
                        AppInstance.getInstance().getClient().CombinationsShare(ListCombination.this, 1);
                        break;
                    case 2:
                        //Öffentliche
                        AppInstance.getInstance().getClient().CombinationsPublic(ListCombination.this, 2);
                        break;
                    case 3:
                        //Suchergebnisse
                        prodAdapter = new customListAdapter(ListCombination.this, AppInstance.getInstance().getData().getAllCombinationSearched());
                        break;
                    default:
                        break;
                }
                updateListView(prodAdapter);
            }
        });

        final Spinner dropDown = findViewById(R.id.spinnerDropDownKomb);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.dropDownCombination, android.R.layout.simple_dropdown_item_1line);
        dropDown.setAdapter(arrayAdapter);

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(AppInstance.getInstance()
                .getData().getServerAddresse()).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        client = retrofit.create(RestClient.class);
/*
The spinner lets you choose between the categories
 */
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        // Meine
                        if (!AppInstance.getInstance().getData().getLoggedIn())
                            adapterView.setSelection(2);
                        else
                            AppInstance.getInstance().getClient().CombinationsOwn(ListCombination.this, 0);

                        break;
                    case 1:
                        //shared
                        if (!AppInstance.getInstance().getData().getLoggedIn())
                            adapterView.setSelection(2);
                        else
                            AppInstance.getInstance().getClient().CombinationsShare(ListCombination.this, 1);
                        break;
                    case 2:
                        //Öffentliche
                        AppInstance.getInstance().getClient().CombinationsPublic(ListCombination.this, 2);
                        break;
                    case 3:
                        //Suchergebnisse
                        prodAdapter = new customListAdapter(ListCombination.this, AppInstance.getInstance().getData().getAllCombinationSearched());
                        updateListView(prodAdapter);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
                if (!AppInstance.getInstance().getData().getLoggedIn())
                    adapterView.setSelection(2);

            }
        });

        EditText editTextSearch = findViewById(R.id.editTextViewComb);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    dropDown.setSelection(curSection);
                } else {
                    AppInstance.getInstance().getClient().searchCombination(ListCombination.this, 3, editable.toString());
                    //saving from which section we are jumping from
                    if (dropDown.getSelectedItemPosition() != 3)
                        curSection = dropDown.getSelectedItemPosition();
                    dropDown.setSelection(3);

                }
            }
        });


    }
/*
 updates the List with the new ArrayAdapter
 */
    private void updateListView(ArrayAdapter prodAdapter) {

        ListView listView = findViewById(R.id.listViewComb);
        listView.setAdapter(prodAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = (Integer) view.getTag();
                Intent intent = new Intent(ListCombination.this, ShowCombination.class);
                Bundle bundle = new Bundle();
                bundle.putInt("KombinationID", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_list_combination;
    }

    /**
     * The CAllback manages all of the Data coming back from the Server
     * it seperates which case to use with the param
     *
     * @param i which case is relevant
     */
    @Override
    public void callBack(int i) {
        switch (i) {
            case 0:
                if (AppInstance.getInstance().getData().getAllCombinationOwn() != null)
                    prodAdapter = new customListAdapter(ListCombination.this, AppInstance.getInstance().getData().getAllCombinationOwn());
                else
                    prodAdapter = new customListAdapter(ListCombination.this, new ArrayList<ListElement>());
                break;
            case 1:
                if (AppInstance.getInstance().getData().getAllCombinationShared() != null)
                    prodAdapter = new customListAdapter(ListCombination.this, AppInstance.getInstance().getData().getAllCombinationShared());
                else
                    prodAdapter = new customListAdapter(ListCombination.this, new ArrayList<ListElement>());
                break;
            case 2:
                if (AppInstance.getInstance().getData().getAllCombinationPublic() != null)
                    prodAdapter = new customListAdapter(ListCombination.this, AppInstance.getInstance().getData().getAllCombinationPublic());
                else
                    prodAdapter = new customListAdapter(ListCombination.this, new ArrayList<ListElement>());
                break;
            case 3:
                if (AppInstance.getInstance().getData().getAllCombinationSearched() != null) {
                    prodAdapter = new customListAdapter(ListCombination.this, AppInstance.getInstance().getData().getCombSearchResult().getOwnCombinationsList());
                    prodAdapter.addAll(AppInstance.getInstance().getData().getCombSearchResult().getOwnCombinationsList());
                    prodAdapter.addAll(AppInstance.getInstance().getData().getCombSearchResult().getSharedCombinationsList());
                    prodAdapter.addAll(AppInstance.getInstance().getData().getCombSearchResult().getPublicCombinationList());
                } else
                    prodAdapter = new customListAdapter(ListCombination.this, new ArrayList<ListElement>());
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
