package lyrics.manejadorLiricaMp3;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import java.io.File;

public class Reproductor {

    private BasicPlayer player;

    public Reproductor(){
        player = new BasicPlayer();
    }

    public void Play() throws Exception {
        try{
            player.play();
        } catch (BasicPlayerException e) {
            throw new Exception("""
                    ¿Eliminó o movió el archivo?
                    codigo de error es:\s""" + e.getMessage());
        }

    }

    public void AbrirFichero(String ruta) throws Exception {
        try{
            player.open(new File(ruta));
        } catch (BasicPlayerException e) {
            throw new Exception("""
                    ¿Eliminó o movió el archivo?
                    codigo de error es:\s""" + e.getMessage());
        }
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
}
