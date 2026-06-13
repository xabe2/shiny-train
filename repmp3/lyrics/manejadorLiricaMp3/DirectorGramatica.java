package lyrics.manejadorLiricaMp3;
import lyrics.sablecc.analysis.*;
import lyrics.sablecc.node.*;

import java.util.ArrayList;

public class DirectorGramatica extends DepthFirstAdapter {

    private ArrayList<ArchLirica> bibliotecaLiricas = new ArrayList<>();

    public void outAOtroMetaTag(AOtroMetaTag node){
        System.out.println(node.toString());
    }

    public void outAVaciaLinea(AVaciaLinea node){
        System.out.println(node.toString());
    }

//   public void outAConTextoLinea(AConTextoLinea node){
//        System.out.println(node.toString());
//    }

    @Override
    public void inAConTextoLinea(AConTextoLinea node) {

        String tag = node.getTiempoTag().getText();
        String letra = node.getTextoLirica().getText();

        int minutos = Integer.parseInt(tag.substring(1, 3));
        int segundos = Integer.parseInt(tag.substring(4, 6));
        int centesimas = Integer.parseInt(tag.substring(7, 9));

        long totalMilisegundos = (minutos * 60000L) + (segundos * 1000L) + (centesimas * 10L);

        System.out.println("Milisegundos: " + totalMilisegundos);

        bibliotecaLiricas.add(new ArchLirica(totalMilisegundos,letra.trim()));
    }

    public ArrayList<ArchLirica> getBibliotecaLiricas() {
        return bibliotecaLiricas;
    }
}
