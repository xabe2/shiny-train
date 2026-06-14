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
        String tagCrudo = node.getTagOtro().getText();
        String tagLimpio = tagCrudo.substring(1, tagCrudo.length() - 1).trim();
        otrosMetadatos.add(tagLimpio);
    }

    @Override
    public void inAConTextoLinea(AConTextoLinea node) {
        String tiempo_tag = node.getTiempoTag().getText();
        String letra = node.getTextoLirica().getText();

        int minutos = Integer.parseInt(tiempo_tag.substring(1, 3));
        int segundos = Integer.parseInt(tiempo_tag.substring(4, 6));
        int centesimas = Integer.parseInt(tiempo_tag.substring(7, 9));
        long totalMilisegundos = (minutos * 60000L) + (segundos * 1000L) + (centesimas * 10L);

        long LATENCIA_LIRICAS = 300L;

        System.out.println("DEBUG LATENCIA LIRICAS: " + totalMilisegundos);

        long tiempoCorregido = Math.max(0, totalMilisegundos + LATENCIA_LIRICAS);
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
