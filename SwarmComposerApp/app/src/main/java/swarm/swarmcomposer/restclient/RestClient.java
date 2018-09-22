package swarm.swarmcomposer.restclient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import swarm.swarmcomposer.helper.CombSearchResult;
import swarm.swarmcomposer.helper.ListElement;
import swarm.swarmcomposer.model.Combination;
import swarm.swarmcomposer.model.Product;
/**
 * Interface of the RetroClient
 * This Interface describes how the Get Requests need to look
 */
public interface RestClient {

    @GET("app/combinations/own")
    Call<ArrayList<ListElement>> CombinationOwn();

    @GET("app/products")
    Call<ArrayList<ListElement>> Products();

    @GET("app/combinations/{combID}")
    Call<Combination> Combination(@Path("combID") int combID);

    @GET("detailcombination")
    Call<Combination> Combination();

    @GET("app/combinations/public")
    Call<ArrayList<ListElement>> CombinationPublic();

    @GET("app/combinations/shared")
    Call<ArrayList<ListElement>> CombinationShared();

    @GET("app/products/{prodId}")
    Call<Product> Product(@Path("prodId") int prodId);

    @GET("app/products")
    Call<ArrayList<ListElement>> SearchProducts(@Query("search") String searchTag, @Query("onlyCertified") Boolean cert);

    @GET("app/combinations")
    Call<CombSearchResult> SearchCombinations(@Query("search") String searchTag);

    @GET("app/login")
    Call<ArrayList> login();
}
