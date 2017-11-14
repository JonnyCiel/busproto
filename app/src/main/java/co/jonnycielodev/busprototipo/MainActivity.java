package co.jonnycielodev.busprototipo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import co.jonnycielodev.busprototipo.entities.Bus;
import co.jonnycielodev.busprototipo.entities.Users;

public class MainActivity extends AppCompatActivity implements MainActivityAdapterClick {


    private RecyclerView mRecyclerView;
    private MainActivityAdapter adapter;
    private ArrayList<Bus> mBusArrayList = new ArrayList<>();
    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Buses");
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Bus currentBus;
    private CoordinatorLayout container;


    private static final int RC_SIGN_IN = 123;
    private DatabaseReference mReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

    private ValueEventListener mEventListener;
    private TextView tvEmptyState;
    private ProgressBar mProgressBar;

    @Override
    protected void onStart() {
        super.onStart();
        if (mEventListener != null){
            mReference.addValueEventListener(mEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mEventListener != null){
            mReference.removeEventListener(mEventListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.mainRecycler);
        tvEmptyState = findViewById(R.id.mainTextView);
        mProgressBar = findViewById(R.id.mainProgressBar);
        container = findViewById(R.id.mainContainer);
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lista de rutas");

        setupAdapter();
        setupRecyclerView();



        Button btn = findViewById(R.id.enviar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> rutas = new ArrayList<String>();
                rutas.add("SALADO");
                rutas.add("AVENIDA JORDAN");
                rutas.add("LIBERTADOR");

                ArrayList<Double> lat = new ArrayList<Double>();
                lat.add(4.450516299987381);
                //lat.add(4.4390201623450105);
                // lat.add(4.4390201623450105);

                ArrayList<Double> lng = new ArrayList<Double>();
                lng.add(-75.15607933282467);
                //lng.add(-75.18815855264279);
                // lng.add(-75.18815855264279);

                Bus bus = new Bus(82, "wtl78m", rutas, lat, lng);
                mReference.push().setValue(bus);
            }
        });

        mEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBusArrayList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Bus bus = data.getValue(Bus.class);
                    mBusArrayList.add(bus);
                }
                adapter.notifyDataSetChanged();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.GONE);
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addValueEventListener(mEventListener);
        mReference.keepSynced(true);

    }

    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
    }

    private void setupAdapter() {
        adapter = new MainActivityAdapter(mBusArrayList, this, this);
    }

    @Override
    public void onRutasClick(Bus bus) {
        String msg = " ";
        int numRutas = 0;
        for (String ruta : bus.getRutas()) {
            numRutas++;
            if (numRutas == 3) {
                msg += ruta + ".";
            } else {
                msg += ruta + ", ";
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Rutas")
                .setMessage(msg)
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permiso para conocer tu ubicación")
                        .setMessage("Para calcular el tiempo de tu ruta necesitamos acceso a tu GPS")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("bus", currentBus);
                startActivity(intent);
            }
        }

    }

    @Override
    public void onContainerClick(Bus bus) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        currentBus = bus;
        if (info != null && info.isConnected()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkLocationPermission()) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this, MapsActivity.class);
                        intent.putExtra("bus", bus);
                        startActivity(intent);
                    }
                }
            } else {
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("bus", bus);
                startActivity(intent);
            }
        } else {
            Snackbar.make(container, "Conéctate a internet para ver el mapa", Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_registrar:
                FirebaseUser userRegis = FirebaseAuth.getInstance().getCurrentUser();
                if (userRegis != null) {
                    Snackbar.make(container, "Por favor cierra sesión antes de crear un registro", Snackbar.LENGTH_SHORT).show();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                                    )
                                    .build(),
                            RC_SIGN_IN);
                }
                break;

            case R.id.menu_verUsuarios:
                Intent intent = new Intent(this, UsersActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_cerrarSesión:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    Snackbar.make(container, "Has cerrado sesión", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Snackbar.make(container, "No hay sesiones activas", Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    mReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean exist = dataSnapshot.child(user.getUid()).exists();
                            Users users;
                            if (!exist) {
                                if (user.getPhotoUrl() != null) {
                                    users = new Users(System.currentTimeMillis(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                                } else {
                                    users = new Users(System.currentTimeMillis(), user.getDisplayName(), user.getEmail(), "NO");
                                }

                                mReferenceUsers.child(user.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Snackbar.make(container, "Error escribiendo datos", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                Snackbar.make(container, "Registrado con éxito", Snackbar.LENGTH_SHORT).show();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Snackbar.make(container, "Sin conexión a internet", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(container, "Sin conexión a internet", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Snackbar.make(container, "Error desconocido, intenta de nuevo.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
}
