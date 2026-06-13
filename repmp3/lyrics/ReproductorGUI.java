package lyrics;

import lyrics.manejadorLiricaMp3.ArchLirica;
import lyrics.manejadorLiricaMp3.DirectorGramatica;
import lyrics.manejadorLiricaMp3.Reproductor;
import lyrics.sablecc.lexer.Lexer;
import lyrics.sablecc.node.Start;
import lyrics.sablecc.parser.Parser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ReproductorGUI extends JFrame {

    private JButton btnPlay;
    private JButton btnStop;
    private JLabel lblLetra;

    private Reproductor reproductor;
    private Timer timerPrincipal;

    public ReproductorGUI() {
        // 1. Configuración de la ventana minimalista
        setTitle("Reproductor MP3 LRC");
        setSize(500, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 2. Pantalla de las letras (Centro)
        lblLetra = new JLabel("Haz clic en 'Cargar y Reproducir'", SwingConstants.CENTER);
        lblLetra.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblLetra, BorderLayout.CENTER);

        // 3. Panel de botones (Abajo)
        JPanel panelBotones = new JPanel();
        btnPlay = new JButton("Cargar y Reproducir");
        btnStop = new JButton("Stop");

        panelBotones.add(btnPlay);
        panelBotones.add(btnStop);
        add(panelBotones, BorderLayout.SOUTH);

        reproductor = new Reproductor(); // Tu clase base usando BasicPlayer

        // 4. Eventos de los botones
        btnPlay.addActionListener(e -> cargarYReproducir());
        btnStop.addActionListener(e -> detenerReproduccion());
    }

    private void cargarYReproducir() {
        try {
            JFileChooser chooser = new JFileChooser();

            // Elegir el MP3
            chooser.setDialogTitle("Selecciona el archivo de Audio (.mp3)");
            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            File mp3File = chooser.getSelectedFile();

            // Elegir el LRC
            chooser.setDialogTitle("Selecciona el archivo de Letras (.lrc)");
            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            File lrcFile = chooser.getSelectedFile();

            // A. Detener cualquier cosa que esté sonando antes
            detenerReproduccion();

            // B. Parsear el archivo .lrc (Etapas 1 y 2)
            Lexer lexer = new Lexer(new PushbackReader(new FileReader(lrcFile), 1024));
            Parser parser = new Parser(lexer);
            Start ast = parser.parse();
            DirectorGramatica visitador = new DirectorGramatica();
            ast.apply(visitador);

            // C. Cargar MP3 en tu clase Reproductor
            reproductor.AbrirFichero(mp3File.getAbsolutePath());

            // D. Programar los temporizadores para la letra (Etapa 3)
            programarLetras(visitador.getBibliotecaLiricas());

            // E. ¡Reproducir!
            reproductor.Play();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void programarLetras(ArrayList<ArchLirica> repositorio) {
        timerPrincipal = new Timer();

        for (ArchLirica item : repositorio) {
            timerPrincipal.schedule(new TimerTask() {
                @Override
                public void run() {
                    // SwingUtilities asegura que actualizar la interfaz desde otro hilo sea seguro
                    SwingUtilities.invokeLater(() -> lblLetra.setText(item.getTexto()));
                }
            }, item.getTiempoMilisegundo());
        }
    }

    private void detenerReproduccion() {
        try {
            if (reproductor != null) {
                reproductor.Stop();
            }
            if (timerPrincipal != null) {
                timerPrincipal.cancel(); // Matamos el hilo del Timer para que las letras se detengan
            }
            lblLetra.setText("Reproducción detenida");
        } catch (Exception ex) {
            System.out.println("Error al detener: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Lanzar la interfaz
        SwingUtilities.invokeLater(() -> {
            ReproductorGUI ventana = new ReproductorGUI();
            // Centrar ventana en la pantalla
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}