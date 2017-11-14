package co.jonnycielodev.busprototipo.entities;

/**
 * Created by Jonny on 12/10/2017.
 */

public class Users {
    private long id;
    private String nombre;
    private String email;
    private String picPath;

    public Users() {
    }

    public Users(long id, String nombre, String email, String picPath) {

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.picPath = picPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
