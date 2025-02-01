package wenchao.kiosk;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class Client {
    public static final String BASEURL = "http://51.83.97.190/";


    public static Retrofit getClient(){
        return new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
