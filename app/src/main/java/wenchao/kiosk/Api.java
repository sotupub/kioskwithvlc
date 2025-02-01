package wenchao.kiosk;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
public interface Api {
    @GET("Api.php")
    Call<List<UpdateiNFO>> getUpdateInfo();
}
