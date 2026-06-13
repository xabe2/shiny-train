package lyrics;
import lyrics.debug.ASTDisplay;
import lyrics.debug.ASTPrinter;
import lyrics.manejadorLiricaMp3.DirectorGramatica;
import lyrics.manejadorLiricaMp3.Reproductor;
import lyrics.sablecc.node.*;
import lyrics.sablecc.lexer.*;
import lyrics.sablecc.parser.*;
import java.io.*;
import java.io.BufferedReader;
import java.util.Scanner;

public class Main {

    public static final boolean ES_NECESARIO_DEBUGEAR = true;

    static void main() {
        Scanner sc = new Scanner(System.in);
        String ruta_lrc = "";
        String ruta_mp3 = "";

        System.out.println("...:: REPRODUCTOR MP3 CON SOPORTE DE LIRICA - CONSOLA ::...");

        try{
            if(ES_NECESARIO_DEBUGEAR){
                ruta_lrc = "/home/ariel/IdeaProjects/shiny-train/repmp3/archivosMusica/bring me to life.lrc";
                ruta_mp3 = "/home/ariel/IdeaProjects/shiny-train/repmp3/archivosMusica/evanescense.mp3";
            }else {
                System.out.println("RUTA ARCHIVO LRC? ");
                ruta_lrc = sc.nextLine();

                System.out.println("RUTA ARCHIVO MP3? ");
                ruta_mp3 = sc.nextLine();
            }

            Parser p = new Parser(new Lexer(new PushbackReader(new BufferedReader(new FileReader(ruta_lrc)))));
            Start tree = p.parse();

            if(ES_NECESARIO_DEBUGEAR){
                tree.apply(new ASTDisplay());
                tree.apply(new ASTPrinter());
            }

            DirectorGramatica director = new DirectorGramatica();
            tree.apply(director);

            if(ES_NECESARIO_DEBUGEAR) System.out.println("NUMERO DE LIRICAS CARGADAS EXITOSAMENTE: " + director.getBibliotecaLiricas().size());
            
            Reproductor mp3 = new Reproductor();
            mp3.AbrirFichero(ruta_mp3);

            mp3.programarLiricas(director.getBibliotecaLiricas());
            mp3.Play();

        } catch (Exception e){ // todo Categorizar los errores?
            System.out.println(e.getMessage());
        }
    }
}
