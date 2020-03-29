package kolesnikov.ru.traps;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Server {


    private static final String GET_TRAPS = "getTraps/";
    private static final String FIND_TRAP_FOR_BARCODE = "find?";
//    private static final String SERVER_ADDR = "http://localhost:8001/";
    private static final String SERVER_ADDR = "http://koles-doc96.myjino.ru/";

    private Context ctx;

    public Server() {}

    public Server(Context ctx) {
        this.ctx = ctx;
    }


    // Получить список всех ловушек
    public String getTraps() {
        String line = "";
        try {
            line = new getTrapsAsync().execute().get();
        } catch (Exception e) {}
        return line;
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
                HttpEntity httpEntity =response.getEntity();
                line = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return line;
    }
} // Получить список всех ловушек

    public String findTrap(String barcode) {
        String line = "";
        try {
            line = new getTrapsAsync().execute(barcode).get();
        } catch (Exception e) {}
        return line;
    }

    class findTrapForBarcodeAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String line = "";
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(SERVER_ADDR + FIND_TRAP_FOR_BARCODE + "barCode=" + params[0]);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity httpEntity =response.getEntity();
                line = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return line;
        }
    }


}
