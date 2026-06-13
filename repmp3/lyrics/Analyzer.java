package lyrics;
import lyrics.sablecc.node.*;
import lyrics.sablecc.lexer.*;
import lyrics.sablecc.parser.*;

import java.io.*;
import java.io.BufferedReader;

public class Analyzer {
    static void main(String[] args) {
        try{
            System.out.println("Se lee'archivo .lrc");
            Parser p = new Parser(new Lexer(new PushbackReader(new BufferedReader(new FileReader("/home/ariel/Documentos/repmp3/lyrics/bring me to life.lrc")))));

            Start tree = p.parse();

            //tree.apply(new ASTDisplay());
            //tree.apply(new ASTPrinter());

            Traductor tr_cambiarnombre = new Traductor();
            tree.apply(tr_cambiarnombre);

            System.out.println("NUMERO DE LIRICAS CARGADAS EXITOSAMENTE: " + tr_cambiarnombre.getBibliotecaLiricas().size());
            
            Reproductor mp3 = new Reproductor();
            mp3.AbrirFichero("/home/ariel/IdeaProjects/shiny-train/repmp3/lyrics/evanescense.mp3");

            mp3.programarLiricas(tr_cambiarnombre.getBibliotecaLiricas());
            mp3.Play();

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
