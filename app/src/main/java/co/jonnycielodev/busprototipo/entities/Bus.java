package co.jonnycielodev.busprototipo.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jonny on 10/10/2017.
 */

public class Bus  implements Parcelable{
    private int rutaNumero;
    private String busPlaca;
    private ArrayList<String> rutas;
    private ArrayList<Double> lat;
    private ArrayList<Double> lng;

    public Bus(int rutaNumero, String busPlaca, ArrayList<String> rutas, ArrayList<Double> lat, ArrayList<Double> lng) {
        this.rutaNumero = rutaNumero;
        this.busPlaca = busPlaca;
        this.rutas = rutas;
        this.lat = lat;
        this.lng = lng;
    }

    protected Bus(Parcel in) {
        rutaNumero = in.readInt();
        busPlaca = in.readString();
        rutas = in.createStringArrayList();
        lat = (ArrayList<Double>) in.readSerializable();
        lng = (ArrayList<Double>) in.readSerializable();
    }

    public static final Creator<Bus> CREATOR = new Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };

    public ArrayList<String> getRutas() {
        return rutas;
    }

    public void setRutas(ArrayList<String> rutas) {
        this.rutas = rutas;
    }

    public Bus() {
    }

    public int getRutaNumero() {
        return rutaNumero;
    }

    public void setRutaNumero(int rutaNumero) {
        this.rutaNumero = rutaNumero;
    }

    public String getBusPlaca() {
        return busPlaca;
    }

    public void setBusPlaca(String busPlaca) {
        this.busPlaca = busPlaca;
    }

    public ArrayList<Double> getLat() {
        return lat;
    }

    public void setLat(ArrayList<Double> lat) {
        this.lat = lat;
    }

    public ArrayList<Double> getLng() {
        return lng;
    }

    public void setLng(ArrayList<Double> lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(rutaNumero);
        parcel.writeString(busPlaca);
        parcel.writeStringList(rutas);
        parcel.writeSerializable(lat);
        parcel.writeSerializable(lng);
    }
}
