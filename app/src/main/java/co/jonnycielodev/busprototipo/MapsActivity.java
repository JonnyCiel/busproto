package co.jonnycielodev.busprototipo;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.jonnycielodev.busprototipo.entities.Bus;
import co.jonnycielodev.busprototipo.loaders.BusMapLoader;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, LoaderManager.LoaderCallbacks<JSONObject>, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private Bus currentBus;
    private PolylineOptions options;
    private ArrayList<LatLng> selectedCoor = new ArrayList<>();
    private String url;
    private LatLng origenLatLng;
    private boolean isFirstLaunch = true;
    private TextView tvInfo, tvParadero;
    private StringBuilder builder;
    private ArrayList<MarkerOptions> mArrayList = new ArrayList<>();
    private ArrayList<LatLng> mLatLngsMarkerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvInfo = findViewById(R.id.mapTextInfo);
        tvParadero = findViewById(R.id.mapTextParadero);

        builder = new StringBuilder("Aquí verás la estimación de tu ruta");
        tvInfo.setText(builder.toString());

        Intent intent = getIntent();
        currentBus = intent.getParcelableExtra("bus");
        getSupportActionBar().setTitle("Ruta " + currentBus.getRutaNumero());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

//        LatLng busRutaLatLgn = new LatLng(currentBus.getLat().get(0), currentBus.getLng().get(0));
//        selectedCoor.add(busRutaLatLgn);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(busRutaLatLgn);
//        markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_bus_stop));
//        markerOptions.title("Paradero");
//        mMap.addMarker(markerOptions);

        //tvParadero.setText(currentBus.getRutas().get(1));

        options = new PolylineOptions()
                .color(Color.BLUE)
                .width(10)
                .visible(true);

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            return true;
        }

        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Posición actual");
        markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_bus));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        if (isFirstLaunch) {
            isFirstLaunch = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        if (checkInternet() && !mLatLngsMarkerList.isEmpty()) {
            LatLng markerLatlng = mLatLngsMarkerList.get(0);
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            PolylineOptions polyLineOptions = new PolylineOptions();
            points.add(new LatLng(location.getLatitude(), location.getLongitude()));
            points.add(new LatLng(markerLatlng.latitude, markerLatlng.longitude));
            polyLineOptions.width(7 * 1);
            polyLineOptions.geodesic(true);
            polyLineOptions.color(getResources().getColor(R.color.colorPrimaryDark));
            polyLineOptions.addAll(points);
            Polyline polyline = mMap.addPolyline(polyLineOptions);
            polyline.setGeodesic(true);

            origenLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" +
                    location.getLatitude() + "," + location.getLongitude() + "&destinations=" +
                    markerLatlng.latitude + "," + markerLatlng.longitude +
                    "&key=AIzaSyA9mRI-l2TrxE_k92uil7O5iEYVTNrAUbI";

            if (!isFirstLaunch) {
                builder.delete(0, builder.length());
            }
            getLoaderManager().initLoader((int) System.currentTimeMillis(), null, this);


//            //Place current location marker
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title("Posición actual");
//            markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_bus));
//            mCurrLocationMarker = mMap.addMarker(markerOptions);

        /*url = "https://roads.googleapis.com/v1/snapToRoads?path=" + location.getLatitude() + "," + location.getLongitude()
                + "|" + currentBus.getLat().get(0) + ", " + currentBus.getLng().get(0) +
                "&interpolate=" + true + "&key=AIzaSyA9mRI-l2TrxE_k92uil7O5iEYVTNrAUbI";*/


            //move map camera
        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));*/
//            if (isFirstLaunch) {
//                isFirstLaunch = false;
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//            }
            //stop location updates
       /* if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
        } else {
            if (builder != null){
                builder.delete(0, builder.length());
                builder.append("Sin conexión a internet o paradero no seleccionado");
                tvInfo.setText(builder.toString());
            }else {
                builder = new StringBuilder("Sin conexión a internet o paradero no seleccionado");
                tvInfo.setText(builder.toString());
            }
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(6000);
        mLocationRequest.setFastestInterval(6000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


//        if (origenLatLng != null) {
//            url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" +
//                    origenLatLng.latitude + "," + origenLatLng.longitude + "&destinations=" +
//                    currentBus.getLat().get(0) + "," + currentBus.getLng().get(0) +
//                    "&key=AIzaSyA9mRI-l2TrxE_k92uil7O5iEYVTNrAUbI";
//
//            getLoaderManager().initLoader((int) System.currentTimeMillis(), null, this);
//        }


//        new AsyncTask<String, Void, JSONObject>(){
//
//
//            @Override
//            protected void onPostExecute(JSONObject jsonObject) {
//                super.onPostExecute(jsonObject);
//
//                if (jsonObject != null){
//                    Toast.makeText(MapsActivity.this, "Json bien", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            protected JSONObject doInBackground(String... strings) {
//                try {
//                    URL getUrl = new URL(strings[0]);
//                    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
//                    connection.setRequestMethod("GET");
//
//                    String message = "";
//                    if(connection.getResponseCode() == 200){
//                        InputStream stream = connection.getInputStream();
//                        InputStreamReader inputStreamReader = new InputStreamReader(stream);
//
//                        BufferedReader reader = new BufferedReader(inputStreamReader);
//                        String line = "";
//                        while ((line = reader.readLine()) != null){
//                            message += line;
//                        }
//
//                        JSONObject fullResponse = new JSONObject(message);
//                        return fullResponse;
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//        }.execute(url);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public android.content.Loader<JSONObject> onCreateLoader(int i, Bundle bundle) {
        return new BusMapLoader(this, url);
    }

    @Override
    public void onLoadFinished(android.content.Loader<JSONObject> loader, JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                JSONArray fullArray = jsonObject.getJSONArray("rows");
                JSONObject jsonRows = fullArray.getJSONObject(0);

                JSONArray elements = jsonRows.getJSONArray("elements");
                JSONObject elementObject = elements.getJSONObject(0);

                JSONObject distance = elementObject.getJSONObject("distance");
                String distanceText = distance.getString("text");

                JSONObject duration = elementObject.getJSONObject("duration");
                String arriveTime = duration.getString("text");


                //builder = new StringBuilder("Tu buseta está a " + distanceText + " del paradero.\n" + "Llegará en " + arriveTime);
                builder.delete(0, builder.length());
                builder.append("Tu buseta está a " + distanceText + " del paradero.\n" + "Llegará en " + arriveTime);
                tvInfo.setText(builder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, "Error en la soclititud", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<JSONObject> loader) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (checkInternet()){
            mArrayList.clear();
            mMap.clear();
            mLatLngsMarkerList.clear();
            tvParadero.setText("Paradero seleccionado");
            tvInfo.setText("Calculando distancia...Por favor espera...");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(latLng.latitude, latLng.longitude));
            markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_bus_stop));
            markerOptions.title("Paradero");
            mMap.addMarker(markerOptions);
            mArrayList.add(markerOptions);
            mLatLngsMarkerList.add(new LatLng(latLng.latitude, latLng.longitude));
        }else {
            tvInfo.setText("Sin conexión a internet, intenta de nuevo");
        }

    }
}
