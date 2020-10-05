package kolesnikov.ru.traps.servers;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import kolesnikov.ru.traps.Objects.Keys;
import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.Utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

import static com.google.firebase.crash.FirebaseCrash.log;

public class Server {


    public static final String GET_TRAPS = "getTraps/";
    public static final String FIND_TRAP_FOR_BARCODE = "find?";
    public static final String FIND_KEY = "key?";
    public static final String ADD_TRAP = "add?";
    public static final String EDIT_TRAP = "edit?";
    //    private static final String SERVER_ADDR = "http://localhost:8001/";
    private static final String SERVER_ADDR = "http://koles-doc96.myjino.ru/";
    private static final String SERVER_HOST = "koles-doc96.myjino";

    private Context ctx;
    private String line = "";
    private List<Trap> traps = new ArrayList<>();

    public Server() {
    }

    public Server(Context ctx) {

        this.ctx = ctx;
    }

    // Получить список всех ловушек
    public List<Trap> getTraps() {
        String line = "";
        traps = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Call<List<Trap>> query = null;
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDR) // Адрес сервера
                    // говорим ретрофиту что для сериализации необходимо использовать GSON
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            query = retrofit.create(IRetrofit.class).getTraps();
            traps = query.execute().body();
            return traps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTraps2() {
        String line = "";
        try {
            line = new getTrapsAsync().execute().get();
        } catch (Exception e) {}
        return line;
    }


    // Поиск ловушки по QR коду
    public Trap findTrap(String barcode) {
        String line = "";
        try {
//            line = new FindTrapForBarcodeAsync().execute(barcode).get();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDR) // Адрес сервера
                    // говорим ретрофиту что для сериализации необходимо использовать GSON
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Call<List<Trap>> query = retrofit.create(IRetrofit.class).findTrap(URLEncoder.encode(barcode, "UTF-8"));
            Response<List<Trap>> execute = query.execute();
            List<Trap> body = execute.body();
            if (!body.isEmpty()) {
                return body.get(0);
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String findkKey(String key) {
        try {
            return new getKeyAsync().execute(key).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Добавление ловушки
    public String addTrap(String barcode, String traceBittes, String adhesivePlateReplacement,
                          String numberPests, String isTrapDamage, String isTrapReplacement, String isTrapReplacementDo, String photo, String customNumber,
                          String comment, String commentPhoto, String nameTrap, String kind) {
        line = "";
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("file", "data:image/png;base64," + photo); //Base64 image
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDR) // Адрес сервера
                    // говорим ретрофиту что для сериализации необходимо использовать GSON
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Call<String> query = retrofit.create(IRetrofit.class).addTrap(URLEncoder.encode(barcode, "UTF-8"), traceBittes, adhesivePlateReplacement, numberPests,
                    isTrapDamage, isTrapReplacement, isTrapReplacementDo, customNumber, comment, commentPhoto, nameTrap, kind, photo);
            Response<String> execute = query.execute();
            System.out.println(execute.message());
            System.out.println(execute.body());
            return execute.body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0";
    }

    public String editTrap(String id, String traceBittes, String adhesivePlateReplacement,
                           String numberPests, String isTrapDamage, String isTrapReplacement, String isTrapReplacementDo, String photo,
                           String customNumber, String comment, String commentPhoto, String nameTrap, String kind) {
        line = "";
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDR) // Адрес сервера
                    // говорим ретрофиту что для сериализации необходимо использовать GSON
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Call<String> query = retrofit.create(IRetrofit.class).editTrap(id, traceBittes, adhesivePlateReplacement, numberPests,
                    isTrapDamage, isTrapReplacement, isTrapReplacementDo, customNumber, comment, commentPhoto, nameTrap, kind, photo);
            Response<String> execute = query.execute();
            return execute.body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0";
    }


    class getTrapsAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String line = "";
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                System.out.println(SERVER_ADDR + GET_TRAPS);
                HttpGet httpget = new HttpGet(SERVER_ADDR + GET_TRAPS);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity httpEntity = response.getEntity();
                line = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
            return line;
        }
    } // Получить список всех ловушек

    static class FindTrapForBarcodeAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String line = "";
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(SERVER_ADDR + FIND_TRAP_FOR_BARCODE + "barCode=" + params[0]);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity httpEntity = response.getEntity();
                line = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return line;
        }
    }

    public static class getKeyAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String line = "";
            try {
//            line = new FindTrapForBarcodeAsync().execute(barcode).get();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDR) // Адрес сервера
                        // говорим ретрофиту что для сериализации необходимо использовать GSON
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Call<List<Keys>> query = retrofit.create(IRetrofit.class).findKey(URLEncoder.encode(params[0], "UTF-8"));
                Response<List<Keys>> execute = query.execute();
                List<Keys> body = execute.body();
                if (!body.isEmpty()) {
                    return "1";
                }
            } catch (
                    Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
