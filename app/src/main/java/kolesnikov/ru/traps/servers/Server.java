package kolesnikov.ru.traps.servers;

import android.content.Context;
import android.os.AsyncTask;

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

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.Utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.firebase.crash.FirebaseCrash.log;

public class Server {


    public static final String GET_TRAPS = "getTraps/";
    public static final String FIND_TRAP_FOR_BARCODE = "find?";
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
        Call<List<Trap>> query = null;
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDR) // Адрес сервера
                    // говорим ретрофиту что для сериализации необходимо использовать GSON
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            query = retrofit.create(IRetrofit.class).getTraps();
            traps = query.execute().body();
            return traps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    // Добавление ловушки
    public String addTrap(String barcode, String traceBittes, String adhesivePlateReplacement,
                          String numberPests, String isTrapDamage, String isTrapReplacement, String isTrapReplacementDo, String photo) {
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
                    isTrapDamage, isTrapReplacement, isTrapReplacementDo, photo);
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
                           String numberPests, String isTrapDamage, String isTrapReplacement, String isTrapReplacementDo, String photo) {
        line = "";
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("file", "data:image/png;base64," + photo); //Base64 image
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDR) // Адрес сервера
                    // говорим ретрофиту что для сериализации необходимо использовать GSON
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Call<String> query = retrofit.create(IRetrofit.class).editTrap(id, traceBittes, adhesivePlateReplacement, numberPests,
                    isTrapDamage, isTrapReplacement, isTrapReplacementDo, photo);
            Response<String> execute = query.execute();
            System.out.println(execute.message());
            System.out.println(execute.body());
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

    static class AddTrapAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String line = "";
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();

                String http = SERVER_ADDR + ADD_TRAP + "barCode=" + URLEncoder.encode(params[0], "UTF-8") +
                        "&traceBittes=" + params[1] + "&adhesivePlateReplacement=" + params[2] +
                        "&numberPests=" + params[3] + "&isTrapDamage=" + params[4] +
                        "&isTrapReplacement=" + params[5] + "&isTrapReplacementDo=" + params[6] +
                        "&photo=" + "2";

                HttpURLConnection urlConnection = null;
                try {

                    System.setProperty("http.keepAlive", "false");
                    URL url = new URL(http);
                    urlConnection = (HttpURLConnection) url.openConnection();
//                    urlConnection.disconnect();
//                    urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
//                    urlConnection.setConnectTimeout(10000);
//                    urlConnection.setRequestMethod("POST");
//                    urlConnection.setReadTimeout(10000);
//
//                    urlConnection.setDoOutput(true);
//                    urlConnection.setUseCaches(false);
//                    urlConnection.setRequestProperty("Host", SERVER_HOST);
//                    urlConnection.connect();

                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
                    out.write("skdjflsdflsjkldjflsjlkdjflsldj");
                    out.close();

                    int HttpResult = urlConnection.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        String response = "";
                        line = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        while ((response = br.readLine()) != null) {
                            line += response;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    assert urlConnection != null;
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return line;
        }
    }


}
