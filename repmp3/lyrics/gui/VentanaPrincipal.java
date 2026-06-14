package lyrics.gui;

import lyrics.manejadorLiricaMp3.ArchLirica;
import lyrics.manejadorLiricaMp3.DirectorGramatica;
import lyrics.manejadorLiricaMp3.Reproductor;
import lyrics.sablecc.lexer.Lexer;
import lyrics.sablecc.node.Start;
import lyrics.sablecc.parser.Parser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;

public class VentanaPrincipal extends JDialog {
    private JPanel contentPane;
    private JButton playButton;
    private JButton buttonCancel;
    private JButton cargarLrcButton;
    private JButton cargarMp3Button;
    private JTextField rutaLRC;
    private JTextField rutaMP3;
    private JLabel cuadroLetras;
    private JButton stopButton;
    private JPanel cargaArchivo;
    private JPanel Metadatos;
    private JPanel BotonesAcciones;
    private JTextArea textoMetadatos;
    private File archMP3 = null;
    private File archLRC = null;
    private Reproductor mp3 = new Reproductor();
    private Timer timerPrincipal;

    public VentanaPrincipal() {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(playButton);
        cuadroLetras.setBackground(new Color(255, 255, 255));
        cuadroLetras.setText("Aquí se verán las liricas");

        textoMetadatos.setText("Aquí se verán los metadatos");

        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reproducirPistas();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        cargarLrcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarLRC();
            }
        });
        cargarMp3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarMP3();
            }
        });
//        pauseButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                pausarReproduccion();
//            }
//        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerReproduccion();
            }
        });
    }

    private void cargarMP3() {
        JFileChooser ventanaEleccionMP3 =  new JFileChooser();

        ventanaEleccionMP3.setDialogTitle("Selecciona el archivo de audio (.mp3)");
        if (ventanaEleccionMP3.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        archMP3 = ventanaEleccionMP3.getSelectedFile();

        rutaMP3.setText(archMP3.getAbsolutePath());
    }

    private void cargarLRC(){
        JFileChooser ventanaEleccionLRC =  new JFileChooser();

        ventanaEleccionLRC.setDialogTitle("Selecciona el archivo de Letras (.lrc)");
        if (ventanaEleccionLRC.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        archLRC = ventanaEleccionLRC.getSelectedFile();

        rutaLRC.setText(archLRC.getAbsolutePath());
    }

    private void reproducirPistas() {

        if (archLRC == null || archMP3 == null){
            JOptionPane.showMessageDialog(this,"Archivos ingresados no existe!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!archLRC.getName().toLowerCase().contains(".lrc") || !archMP3.getName().toLowerCase().contains(".mp3")){
            JOptionPane.showMessageDialog(this,"Archivos ingresados no soportados!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try{
            detenerReproduccion();

            cuadroLetras.setText("   ");

            Parser p = new Parser(new Lexer(new PushbackReader(new BufferedReader(new FileReader(archLRC)))));
            Start tree = p.parse();

            DirectorGramatica director = new  DirectorGramatica();
            tree.apply(director);

            StringBuilder infoMetadatos = new StringBuilder();

            infoMetadatos.append("Título: ").append(director.getTitulo()).append("\n");
            infoMetadatos.append("Artista: ").append(director.getArtista()).append("\n");
            infoMetadatos.append("Álbum: ").append(director.getAlbum()).append("\n");

            if (!director.getOtrosMetadatos().isEmpty()) {
                infoMetadatos.append("\n--- Información Adicional ---\n");
                for (String metaExtra : director.getOtrosMetadatos()) {
                    infoMetadatos.append(metaExtra).append("\n");
                }
            }

            textoMetadatos.setText(infoMetadatos.toString());
            mp3.AbrirFichero(archMP3.getAbsolutePath());
            programarLetras(director.getBibliotecaLiricas());

            mp3.Play();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void programarLetras(ArrayList<ArchLirica> repositorio) {
        timerPrincipal = new Timer();

        for (ArchLirica item : repositorio) {
            timerPrincipal.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> cuadroLetras.setText(item.getTexto()));
                }
            }, item.getTiempoMilisegundo());
        }
    }

    private void detenerReproduccion() {
        try {
            if (mp3 != null) {
                mp3.Stop();
            }
            if (timerPrincipal != null) {
                timerPrincipal.cancel();
            }
            cuadroLetras.setText("Reproducción detenida");
        } catch (Exception ex) {
            System.out.println("Error al detener: " + ex.getMessage());
        }
    }

//    private void pausarReproduccion(){
//        try {
//            if (mp3 != null) {
//                mp3.Pausa();
//            }
//            if (timerPrincipal != null) {
//                timerPrincipal.; NO SE PUEDE, ES DEMASIADO COMPLEJO CALCULAR EL TIEMPO TRANSCURRIDO PARA RETOMAR LAS LIRICAS.
//            }
//            cuadroLetras.setText("Reproducción detenida");
//        } catch (Exception ex) {
//            System.out.println("Error al detener: " + ex.getMessage());
//        }
//    }

    private void onCancel() {
        detenerReproduccion();
        dispose();
    }

    public static void main(String[] args) {
        VentanaPrincipal dialog = new VentanaPrincipal();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
