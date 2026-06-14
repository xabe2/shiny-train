import lyrics.gui.VentanaPrincipal;

public class MainLoader {
    public static void main(String[] args) {
        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
        ventanaPrincipal.setTitle("Tarea 2 Fundamentos CS - MP3 con liricas");

        ventanaPrincipal.setResizable(false);
        ventanaPrincipal.setSize(500, 550);
        ventanaPrincipal.setLocationRelativeTo(null);

        ventanaPrincipal.setVisible(true);
    }

}