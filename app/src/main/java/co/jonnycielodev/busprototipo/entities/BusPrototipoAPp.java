package co.jonnycielodev.busprototipo.entities;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jonny on 12/10/2017.
 */

public class BusPrototipoAPp  extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
