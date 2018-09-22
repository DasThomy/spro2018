package swarm.swarmcomposer.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import swarm.swarmcomposer.helper.CallBack;
import swarm.swarmcomposer.helper.CallBackwithObject;
import swarm.swarmcomposer.model.Combination;
import swarm.swarmcomposer.model.Data;
import swarm.swarmcomposer.model.Product;
import swarm.swarmcomposer.restclient.Client;

/**
 * Singelton Instance that keeps track of the REST Client and the Data Model
 */
public class AppInstance {
    private static AppInstance ourInstance;
    private Data data;
    private Client client;

    public static AppInstance getInstance() {
        if (ourInstance == null) ourInstance = new AppInstance();
        return ourInstance;
    }

    public Data getData() {
        return data;
    }

    public Client getClient() {
        return client;
    }

    private AppInstance() {
        data = new Data();
        client = new Client();
    }

    /**
     *
     *               Async requesting a Combination over Rest Client
     *               Sync checking local Database if Combination is already downloaded
     * @param callback Callback of Class requesting the Data
     * @param i        CallBack id
     * @param combID   Id of Combination
     */

    public void getCombination(CallBackwithObject callback, int i, int combID) {
        //ASync
        for (Combination iter : data.getCombinationList()) {
            if (iter.getId() == combID) {
                callback.callBack(i, iter);
                return;
            }
        }
        AppInstance.getInstance().client.Combination(callback, i, combID);
    }

    /**
     *               Async requesting Products over the Rest Client
     *               Sync checking local Database if the List is already downloaded
     * @param listProducts Callback of Class requesting the Data
     * @param i            CallBack id
     */
    public void Products(CallBack listProducts, int i) {
        if (data.getAllProducts().isEmpty()) {
            client.Products(listProducts, i);
        }
        listProducts.callBack(i);
    }

    /**
     *
     *               Async requesting a Product over Rest Client
     *               Sync checking local Database if Combination is already downloaded
     * @param callback Callback of Class requesting the Data
     * @param i        CallBack id
     * @param prodID   Id of Product
     */
    public void getProduct(CallBackwithObject callback, int prodID, int i) {

        for (Product iter : data.getProductList()) {
            if (iter.getId() == prodID) {
                if (iter.isFull()) {
                    callback.callBack(i, iter);
                    return;
                } else break;
            }
        }
        //ASync
        AppInstance.getInstance().client.Product(callback, i, prodID);
    }

    /**
     *      Saving Email and ServerAdresse on Local device
     * @param context Context
     *
     */
    public void saveLokalData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", getData().getEmail());
        editor.putString("serverAdresse", getData().getServerAddresse());
        editor.apply();
    }

    /**
     * get locally saved Data over Shared Preferences
     * @param context Context
     *
     */
    public void getLokalData(Context context) {
        String defaultAdresse = "http://134.245.1.240:9001/swarm-composer-0.0.1-SNAPSHOT/";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String eMail = sharedPreferences.getString("email", "");
        String serverAdresse = sharedPreferences.getString("serverAdresse", defaultAdresse);
        getData().setEmail(eMail);
        getData().setServerAddresse(serverAdresse);
    }

}
