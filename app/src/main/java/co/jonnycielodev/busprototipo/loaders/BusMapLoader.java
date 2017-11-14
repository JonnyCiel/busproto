package co.jonnycielodev.busprototipo.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jonny on 11/10/2017.
 */

public class BusMapLoader extends AsyncTaskLoader<JSONObject> {

    private String url;
    private JSONObject fullResponse;

    public BusMapLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public JSONObject loadInBackground() {
        try {
            URL getUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");

            String message = "";
            if(connection.getResponseCode() == 200){
                InputStream stream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(stream);

                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = reader.readLine()) != null){
                    message += line;
                }

                fullResponse = new JSONObject(message);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fullResponse;
    }

}
