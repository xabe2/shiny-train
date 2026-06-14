package lyrics.manejadorLiricaMp3;
import lyrics.sablecc.analysis.*;
import lyrics.sablecc.node.*;

import java.util.ArrayList;

public class DirectorGramatica extends DepthFirstAdapter {

    private ArrayList<ArchLirica> bibliotecaLiricas = new ArrayList<>();
    private ArrayList<String> otrosMetadatos = new ArrayList<>();
    private String artista = "Desconocido";
    private String titulo =  "Desconocido";
    private String album = "Desconocido";

    private final long LATENCIA_LIRICAS = 600L; //LAS LIRICAS APARECEN CON UN DESFASE, HAY QUE IR PROBANDO MANUAL ESTE VALOR,
    // SOLO MODIFICAR SI SABES QUE PONERLE!

    @Override
    public void inAArMetaTag(AArMetaTag node) {
        String artista_crudo = node.getTagArtista().getText();
        artista = artista_crudo.substring(4, artista_crudo.length() - 1).trim();
    }

    @Override
    public void inATiMetaTag(ATiMetaTag node) {
        String titulo_crudo = node.getTagTitulo().getText();
        titulo = titulo_crudo.substring(4, titulo_crudo.length() - 1).trim();
    }

    // Extraer Álbum
    @Override
    public void inAAlMetaTag(AAlMetaTag node) {
        String album_crudo = node.getTagAlbum().getText();
        album = album_crudo.substring(4, album_crudo.length() - 1).trim();
    }

    @Override
    public void inAOtroMetaTag(AOtroMetaTag node) {
        //todo Estaba pensando... en atraparlos en un arraylist y solo leerlos usando un foreach en un Jtextpane plano!. PROBAR DESPUES!

        String tagCrudo = node.getTagOtro().getText();
        String tagLimpio = tagCrudo.substring(1, tagCrudo.length() - 1).trim();
        otrosMetadatos.add(tagLimpio);
    }

    @Override
    public void inAConTextoLinea(AConTextoLinea node) {

        String tag = node.getTiempoTag().getText();
        String letra = node.getTextoLirica().getText();

        int minutos = Integer.parseInt(tag.substring(1, 3));
        int segundos = Integer.parseInt(tag.substring(4, 6));
        int centesimas = Integer.parseInt(tag.substring(7, 9));

        long totalMilisegundos = (minutos * 60000L) + (segundos * 1000L) + (centesimas * 10L);

        //System.out.println("Milisegundos: " + totalMilisegundos);

        long tiempoCorregido = Math.max(0, totalMilisegundos + LATENCIA_LIRICAS); // POR SI AL RESTAR, EL TIEMPO QUEDA NEGATIVO!
        //System.out.println("Milisegundos originales: " + totalMilisegundos + "; Corregidos: " + tiempoCorregido);
        bibliotecaLiricas.add(new ArchLirica(tiempoCorregido,letra.trim()));
    }

    public ArrayList<ArchLirica> getBibliotecaLiricas() {
        return bibliotecaLiricas;
    }

    public ArrayList<String> getOtrosMetadatos() {
        return otrosMetadatos;
    }

    public String getArtista() {
        return artista;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAlbum() {
        return album;
    }
}
