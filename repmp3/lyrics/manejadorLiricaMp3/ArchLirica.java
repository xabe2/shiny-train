package lyrics.manejadorLiricaMp3;

public class ArchLirica {
    private long tiempoMilisegundo;
    private String texto;

    public ArchLirica(long tiempoMilisegundo, String texto) {
        this.tiempoMilisegundo = tiempoMilisegundo;
        this.texto = texto;
    }

    public long getTiempoMilisegundo() {
        return tiempoMilisegundo;
    }

    public String getTexto() {
        return texto;
    }
}
