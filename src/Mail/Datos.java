/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mail;

import java.io.Serializable;

/**
 *
 * @author PC
 */
public class Datos implements Serializable{
    private boolean Descarga;
    public Datos(){
    }

    public boolean isDescarga() {
        return Descarga;
    }

    public void setDescarga(boolean Descarga) {
        this.Descarga = Descarga;
    }
    
}
