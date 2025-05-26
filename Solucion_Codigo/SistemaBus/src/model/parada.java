
package model;

import java.io.Serializable;

public class parada implements Serializable{

    private String nombre;

    public parada(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
