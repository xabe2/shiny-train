package lyrics.manejadorLiricaMp3;

import javazoom.jlgui.basicplayer.BasicPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Reproductor {

    private BasicPlayer player;

    public Reproductor(){
        player = new BasicPlayer();
    }

    public void Play() throws Exception {
        player.play();
    }

    public void AbrirFichero(String ruta) throws Exception {
        player.open(new File(ruta));
    }

    public void Pausa() throws Exception {
        player.pause();
    }

    public void Continuar() throws Exception {
        player.resume();
    }

    public void Stop() throws Exception {
        player.stop();
    }

//    public static void main(String args[]){
//        try {
//            Reproductor mi_reproductor = new Reproductor();
//            mi_reproductor.AbrirFichero("/home/ariel/Documentos/repmp3/lyrics/evanescense.mp3");
//            mi_reproductor.Play();
//            String st = "Se cumplieron 5 segundos";
//            mi_reproductor.new Reminder(5000, st);
//
//
//        } catch (Exception ex) {
//            System.out.println("Error: " + ex.getMessage());
//        }
//    }
//
//    // UNA RECONVERSION DE LA CLASE REMINDER! todo Preguntar si se puede eliminar ese metodo?
//    public void programarLiricas(ArrayList<ArchLirica> biblioteca) {
//        Timer temporizador = new Timer();
//
//        for (ArchLirica x : biblioteca) {
//            temporizador.schedule(new TimerTask() {
//                @Override
//                public void run(){
//                    System.out.println(x.getTexto());
//                }
//            }, x.getTiempoMilisegundo());
//        }
//    }
}
