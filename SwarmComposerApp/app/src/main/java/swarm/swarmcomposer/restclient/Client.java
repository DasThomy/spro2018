package swarm.swarmcomposer.restclient;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import swarm.swarmcomposer.activity.AppInstance;
import swarm.swarmcomposer.activity.Settings;
import swarm.swarmcomposer.helper.CallBack;
import swarm.swarmcomposer.helper.CallBackwithObject;
import swarm.swarmcomposer.helper.CombSearchResult;
import swarm.swarmcomposer.helper.ListElement;
import swarm.swarmcomposer.model.Combination;
import swarm.swarmcomposer.model.Product;

/**
 * Rest Client:
 * implemented my Using RetroFit and BasicAuth
 */
public class Client {

    private RestClient client;

    private String password = "";
    private Context context;

    public Client() {

    }

    /**
     * Setup of the Normal Retro client without BasicAuth
     * @param context The context
     */
    public void setup(Context context) {
        this.context = context;
        try {
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(AppInstance.getInstance().getData().getServerAddresse())
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            client = retrofit.create(RestClient.class);
        } catch (IllegalArgumentException e) {
            Intent intent = new Intent(context, Settings.class);
            context.startActivity(intent);
        }
    }

    /**
     * Checking if BasicAuth is successfull by checking if we get a 401 unauth... or a 200 ok in the respondes
     * if we are succesfull we keep the okHttpClient attached
     * @param callBack
     */
    public void login(final CallBack callBack) {
        OkHttpClient ok = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder request = original.newBuilder().header("Authorization",
                        Credentials.basic(AppInstance.getInstance().getData().getEmail(), password));
                Request newRequest = request.build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(AppInstance.getInstance().getData().getServerAddresse())
                .addConverterFactory(GsonConverterFactory.create()).client(ok);
        Retrofit retrofit = builder.build();

        client = retrofit.create(RestClient.class);

        Call<ArrayList<ListElement>> loginCall = client.CombinationPublic();
        loginCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 200) {
                    AppInstance.getInstance().getData().setLoggedIn(true);
                    callBack.callBack(0);
                } else if (response.code() == 401) {
                    AppInstance.getInstance().getData().setLoggedIn(false);
                    callBack.callBack(1);
                    setup(context);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

/*
The following classes implement our different get Request to our Server
The are all very similare.
They setup the Interface and the wait ASync through the enqueue for the Answer.
In most cases the save the Data in the Model and return through the callback.
In some cases the return the object with them through the callback to avoid having to find them in the model
 */
    public void CombinationsOwn(final CallBack callBack, final int i) {

        //return kombinaiton name+Kombination ID
        Call<ArrayList<ListElement>> combinationCall = client.CombinationOwn();
        combinationCall.enqueue(new Callback<ArrayList<ListElement>>() {
            @Override
            public void onResponse(Call<ArrayList<ListElement>> call, Response<ArrayList<ListElement>> response) {
                if (response.isSuccessful()) {
                    AppInstance.getInstance().getData().setAllCombinationOwn(response.body());
                    callBack.callBack(i);
                    Log.i("CLIENTSS", "MESSAGES" + response.message());
                    Log.i("CLIENTSS", "Body" + response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ListElement>> call, Throwable t) {
                Log.e("ERRROR", "ups" + t);
                callBack.callBack(99);
            }
        });


    }

    public void CombinationsShare(final CallBack callBack, final int i) {

        Call<ArrayList<ListElement>> combinationCall = client.CombinationShared();
        combinationCall.enqueue(new Callback<ArrayList<ListElement>>() {
            @Override
            public void onResponse(Call<ArrayList<ListElement>> call, Response<ArrayList<ListElement>> response) {
                if (response.isSuccessful()) {
                    ArrayList<ListElement> asd = response.body();
                    AppInstance.getInstance().getData().setAllCombinationShared(response.body());
                    callBack.callBack(i);
                    Log.i("CLIENTSS", "MESSAGES" + response.message());
                    Log.i("CLIENTSS", "Body" + response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ListElement>> call, Throwable t) {
                Log.e("ERRROR", "ups" + t);
                callBack.callBack(99);
            }
        });
    }

    public void CombinationsPublic(final CallBack callBack, final int i) {

        //return kombinaiton name+Kombination ID
        Call<ArrayList<ListElement>> combinationCall = client.CombinationPublic();
        combinationCall.enqueue(new Callback<ArrayList<ListElement>>() {
            @Override
            public void onResponse(Call<ArrayList<ListElement>> call, Response<ArrayList<ListElement>> response) {
                if (response.isSuccessful()) {

                    AppInstance.getInstance().getData().setAllCombinationPublic(response.body());
                    callBack.callBack(i);
                    Log.i("CLIENTSS", "MESSAGES" + response.message());
                    Log.i("CLIENTSS", "Body" + response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ListElement>> call, Throwable t) {
                callBack.callBack(99);
                Log.e("ERRROR", "ups" + t);
            }
        });
    }

    public void Combination(final CallBackwithObject callBack, final int i, int combID) {

        Call<Combination> combinationCall = client.Combination(combID);
        combinationCall.enqueue(new Callback<Combination>() {
            @Override
            public void onResponse(Call<Combination> call, Response<Combination> response) {
                if (response.isSuccessful()) {
                    AppInstance.getInstance().getData().addCombination(response.body());
                    callBack.callBack(i,response.body());
                    Log.i("CLIENTSS", "MESSAGES" + response.message());
                    Log.i("CLIENTSS", "Body" + response.body());
                }
            }

            @Override
            public void onFailure(Call<Combination> call, Throwable t) {
                Log.e("ERRROR", "ups" + t);
                callBack.callBack(-1,null);
            }
        });
    }

    public void Product(final CallBackwithObject callBack, final int i, final int prodId) {

        Call<Product> combinationCall = client.Product(prodId);
        combinationCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {

                    Log.i("CLIENTSS", "MESSAGES" + response.headers());
                    Log.i("CLIENTSS", "Body" + response.body());
                    Product product = response.body();
                    product.setFull(true);
                    product.createLogo();
                    AppInstance.getInstance().getData().addProduct(product);
                    callBack.callBack(i, product);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("ERRROR", "ups" + t);
                callBack.callBack(99, null);
            }
        });
    }

    public void Products(final CallBack callBack, final int i) {
        Call<ArrayList<ListElement>> combinationCall = client.Products();
        combinationCall.enqueue(new Callback<ArrayList<ListElement>>() {
            @Override
            public void onResponse(Call<ArrayList<ListElement>> call, Response<ArrayList<ListElement>> response) {
                if (response.isSuccessful()) {
                    AppInstance.getInstance().getData().setAllProducts(response.body());
                    callBack.callBack(i);
                    Log.i("CLIENTSS", "MESSAGES" + response.message());
                    Log.i("CLIENTSS", "Body" + response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ListElement>> call, Throwable t) {
                Log.e("ERRROR", "ups" + t);
                callBack.callBack(99);
            }
        });
    }

    public void searchProducts(final CallBack callBack, final int i, String s, Boolean cert) {
        Call<ArrayList<ListElement>> combinationCall = client.SearchProducts(s, cert);
        combinationCall.enqueue(new Callback<ArrayList<ListElement>>() {
            @Override
            public void onResponse(Call<ArrayList<ListElement>> call, Response<ArrayList<ListElement>> response) {
                if (response.isSuccessful()) {
                    AppInstance.getInstance().getData().setSearchProducts(response.body());
                    callBack.callBack(i);
                    Log.i("CLIENTSS", "MESSAGES" + response.message());
                    Log.i("CLIENTSS", "Body" + response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ListElement>> call, Throwable t) {
                Log.e("ERRROR", "ups" + t);
                callBack.callBack(99);
            }
        });

    }


    public void searchCombination(final CallBack callBack, final int i, String s) {
        Call<CombSearchResult> combinationCall = client.SearchCombinations(s);
        combinationCall.enqueue(new Callback<CombSearchResult>() {
            @Override
            public void onResponse(Call<CombSearchResult> call, Response<CombSearchResult> response) {
                if (response.isSuccessful()) {
                    AppInstance.getInstance().getData().setCombSearchResult(response.body());
                    Log.i("CLIENTSS", "MESSAGES" + response.message());
                    Log.i("CLIENTSS", "Body" + response.body());
                    callBack.callBack(i);
                }
            }

            @Override
            public void onFailure(Call<CombSearchResult> call, Throwable t) {
                Log.e("ERRROR", "ups" + t);
                callBack.callBack(99);
            }
        });
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void logout() {
        AppInstance.getInstance().getData().setLoggedIn(false);
        password = "";

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(AppInstance.getInstance().getData().getServerAddresse())
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        client = retrofit.create(RestClient.class);
    }
}
