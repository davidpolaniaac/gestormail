/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.FlagTerm;

/**
 *
 * @author PC
 */
public class Email {
    
    private static File folder;
    private static String email="";
    private static String password="";
    private static String ruta="";
    private static String MimeType[]={};
    private static int Time=1;
    
    public static void AwaitMessage(){
         final Runnable tarea = new Runnable() {
            public void run() {
              NewMensajes();
            }
          };
          ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
          timer.scheduleAtFixedRate(tarea, Time, Time, TimeUnit.MINUTES);  
    }
    public static void NewMensajes()
    {
        System.out.println("New Mensajes");
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", email, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            // Se obtienen los mensajes.
            mostrarCorreosNoLeidos(inbox);
            
        } catch (Exception mex) {
            mex.printStackTrace();
        }
    }
    public static boolean Conexion() throws GeneralSecurityException, MessagingException, IOException 
    {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", email, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            // Se obtienen los mensajes.
            Message[] mensajes = inbox.getMessages();
            System.out.println("Mensaje Total #"+ inbox.getMessageCount());
            
            
            for (int i = 0; i < mensajes.length; i++)
            {
                analizaParteDeMensaje(mensajes[i]);
                mensajes[i].setFlag(Flag.SEEN, true);
            }
            System.out.println("mensaje 1");
            return true;
        } catch (Exception mex) {
            System.out.println("mensaje 2");
            mex.printStackTrace();
            return false;
        }
    }
    private static void analizaParteDeMensaje(Part unaParte)
    {
        try
        {
          // Si es multipart, se analiza cada una de sus partes recursivamente.
            if (unaParte.isMimeType("multipart/*"))
            {
                Multipart multi;
                multi = (Multipart) unaParte.getContent();

                for (int j = 0; j < multi.getCount(); j++)
                {
                    analizaParteDeMensaje(multi.getBodyPart(j));
                }
            }
            else
            {
                for(String item:MimeType){
                  // Si es arcuhivo, se guarda en fichero 
                    if (unaParte.isMimeType(item))
                    {
                     salvaFichero(unaParte);
                     break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private static String nombreArchivo(String nombreReal,int num){
        
        File fichero;
        if(num==0){
          fichero = new File(ruta+"\\"+nombreReal);
        }else{
          fichero = new File(ruta+"\\"+num+"-"+nombreReal);
        }
        
        if (fichero.exists()){
            num=num+1;
            return nombreArchivo(nombreReal,num);
        }else{
            if(num==0){
                return ruta+"\\"+nombreReal;
            }else{
                return ruta+"\\"+num+"-"+nombreReal;
            }          
        }      
    }
     private static void salvaFichero(Part unaParte) throws FileNotFoundException, MessagingException, IOException
    {
            try{
            if(!folder.isDirectory()){
                folder.mkdir();
                System.out.println("Crea el un directorio");
            }
            FileOutputStream fichero = new FileOutputStream(nombreArchivo(unaParte.getFileName(),0));
            InputStream imagen = unaParte.getInputStream();
            byte[] bytes = new byte[1000];
            int leidos = 0;
              System.out.println("Archivo nuevo");
            while ((leidos = imagen.read(bytes)) > 0)
            {
                fichero.write(bytes, 0, leidos);
            }
            fichero.close();
            }catch(Exception e){
                System.out.println(unaParte.getFileName()+"  hay un error");
            }
    }
    public static void Datos()
    {
        String fichero = "datos.txt";
        try {
          FileInputStream fis = new FileInputStream(fichero);
          InputStreamReader isr = new InputStreamReader(fis,"utf8");
          BufferedReader br = new BufferedReader(isr);

          String linea;
          int i =0;
          while((linea = br.readLine()) != null)
          {
            if(i==0){
                email=linea;
            }else if(i==1){
                password=linea;
            }else if(i==2){
                ruta=linea;
                folder=new File(ruta);
            }else if(i==3){
                MimeType=linea.split(",");
            }else if(i==4){
                Time=Integer.parseInt(linea);
            }
            i++;
            System.out.println(linea);
          }
          fis.close();
        }
        catch(Exception e) {
          System.out.println("Excepcion leyendo fichero "+ fichero + ": " + e);
        }
    }
    private static void mostrarCorreosNoLeidos(Folder inbox){        
        try {
            inbox.close(false);
            inbox.open(Folder.READ_WRITE);
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message mensajes[] = inbox.search(ft);
            System.out.println("Correos sin leer: "+mensajes.length);
            for(int i = 0; i < mensajes.length; i++) {
                try {
                    
                    analizaParteDeMensaje(mensajes[i]);
                    mensajes[i].setFlag(Flag.SEEN, true);
                    
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println("Sin Información");
                }
            }
        } catch (MessagingException e) {
            System.out.println(e.toString());
        }
    }
}
