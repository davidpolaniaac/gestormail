/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mail;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

/**
 *
 * @author PC
 */
public class Gestor {
    
    Gestor()
    {
        JOptionPane.showMessageDialog(null,"Working in the background ....");
        ReadingEmail.ventana.setVisible(false);
        ReadingEmail.ventana.dispose();
    }
    
    public void Leer()throws Exception{
        Datos cab = new Datos();
        File f=new File("datos.bin");
        Email.Datos();
        if(f.exists())
        {
            FileInputStream fis=new FileInputStream(f);
            ObjectInputStream ois=new ObjectInputStream(fis);
            cab=(Datos)ois.readObject();
            fis.close();
            ois.close();
            if(cab.isDescarga()){
                Email.AwaitMessage();
            }else{
                JOptionPane.showMessageDialog(null,"Error en los datos rectificar");
            }
            
        }else{
            cab.setDescarga(Email.Conexion());
            if(cab.isDescarga()){
                f.createNewFile();
                FileOutputStream fos=new FileOutputStream(f);
                ObjectOutputStream oos=new ObjectOutputStream(fos);
                oos.writeObject(cab); //guardamos el objeto en el archivo
                fos.close();
                oos.close();
                Email.AwaitMessage();
            }else{
                JOptionPane.showMessageDialog(null,"Error en los datos rectificar");
            }
            
        }
    }
    
}
