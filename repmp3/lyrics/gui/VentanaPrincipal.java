package lyrics.gui;

import lyrics.manejadorLiricaMp3.ArchLirica;
import lyrics.manejadorLiricaMp3.DirectorGramatica;
import lyrics.manejadorLiricaMp3.Reproductor;
import lyrics.sablecc.lexer.Lexer;
import lyrics.sablecc.lexer.LexerException;
import lyrics.sablecc.node.Start;
import lyrics.sablecc.parser.Parser;
import lyrics.sablecc.parser.ParserException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VentanaPrincipal extends JFrame {
    private JPanel contentPane;
    private JButton playButton;
    private JButton buttonCancel;
    private JButton cargarLrcButton;
    private JButton cargarMp3Button;
    private JTextField rutaLRC;
    private JTextField rutaMP3;
    private JButton stopButton;
    private JPanel cargaArchivo;
    private JPanel Metadatos;
    private JPanel BotonesAcciones;
    private JTextArea textoMetadatos;
    private JTextPane LiricasTextPane;
    private JButton creditosButton;
    private File archMP3 = null;
    private File archLRC = null;
    private Reproductor mp3 = new Reproductor();
    private Timer timerPrincipal;

    public VentanaPrincipal() {

        setContentPane(contentPane);
        getRootPane().setDefaultButton(playButton);

        setTitle("Tarea 2 Fundamentos CS - MP3 con líricas");
        setSize(500, 550);
        setResizable(false);
        setLocationRelativeTo(null);

        LiricasTextPane.setText("Aquí se verán las liricas");
        textoMetadatos.setText("Aquí se verán los metadatos");
        LiricasTextPane.setEditable(false);
        LiricasTextPane.setOpaque(false);
        StyledDocument doc = LiricasTextPane.getStyledDocument();
        SimpleAttributeSet centro = new SimpleAttributeSet();
        StyleConstants.setAlignment(centro, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), centro, false);

        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reproducirPistas();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salirDePrograma();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                salirDePrograma();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salirDePrograma();
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
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerReproduccion();
            }
        });
        creditosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                creditos();
            }
        });
    }

    private void creditos() {
        JOptionPane.showMessageDialog(this, """
                Código creado por:
                Ariel Bobadilla
                Felipe Silva
                Benjamin Bravo
                Se utilizó IA para preguntas puntuales y apoyo en bugs.\
                 El presente trabajo no posee código generado directamente por IA
                """, "Creditos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarMP3() {
        if (archMP3 != null) {
            detenerReproduccion();
        }

        JFileChooser ventanaEleccionMP3 = new JFileChooser(System.getProperty("user.home"));
        ventanaEleccionMP3.setDialogTitle("Selecciona el archivo de audio (.mp3)");
        FileNameExtensionFilter filtroMP3 = new FileNameExtensionFilter("Archivos de audio", "mp3");
        ventanaEleccionMP3.setFileFilter(filtroMP3);
        ventanaEleccionMP3.setAcceptAllFileFilterUsed(false);

        if (ventanaEleccionMP3.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        archMP3 = ventanaEleccionMP3.getSelectedFile();
        rutaMP3.setText(archMP3.getAbsolutePath());
    }

    private void cargarLRC() {
        if (archLRC != null) {
            detenerReproduccion();
        }

        JFileChooser ventanaEleccionLRC = new JFileChooser(System.getProperty("user.home"));
        ventanaEleccionLRC.setDialogTitle("Selecciona el archivo de Letras (.lrc)");
        FileNameExtensionFilter filtroLRC = new FileNameExtensionFilter("Archivos .LRC", "lrc");
        ventanaEleccionLRC.setFileFilter(filtroLRC);
        ventanaEleccionLRC.setAcceptAllFileFilterUsed(false);

        if (ventanaEleccionLRC.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        archLRC = ventanaEleccionLRC.getSelectedFile();
        rutaLRC.setText(archLRC.getAbsolutePath());
    }

    private void reproducirPistas() {

        try {
            if (archLRC == null || archMP3 == null) {
                JOptionPane.showMessageDialog(this, "Archivos ingresados no existe!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!archLRC.getName().toLowerCase().contains(".lrc") || !archMP3.getName().toLowerCase().contains(".mp3")) {
                JOptionPane.showMessageDialog(this, "Archivos ingresados no soportados!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("DEBUG ***");

            detenerReproduccion();
            LiricasTextPane.setText("   ");

            Parser p = new Parser(new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(new FileInputStream(archLRC), StandardCharsets.UTF_8)))));
            Start tree = p.parse();

            System.out.println("DEBUG ***");

            DirectorGramatica director = new DirectorGramatica();
            tree.apply(director);

            String metadatosListos = armarMetadatos(director.getArtista(), director.getTitulo(), director.getAlbum(), director.getOtrosMetadatos());
            textoMetadatos.setText(metadatosListos);

            mp3.AbrirFichero(archMP3.getAbsolutePath());
            programarLetras(director.getBibliotecaLiricas());
            mp3.Play();

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "El archivo no se ha encontrado!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParserException e) {
            JOptionPane.showMessageDialog(this, "Error interno en Parser!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error interno en E/S!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (LexerException e) {
            JOptionPane.showMessageDialog(this, "Error interno en Lexer!\n" +
                    "Verifique que el archivo .irc este correctamente formateado!\n" +
                    "Cualquier error de formato arrojará este error\n\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error desconocido!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Este es el timer, programa una lista de tareas por hacer, no lo hace en tiempo real.
    private void programarLetras(ArrayList<ArchLirica> repositorio) {
        timerPrincipal = new Timer();

        for (ArchLirica item : repositorio) {
            timerPrincipal.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> LiricasTextPane.setText(item.getTexto()));
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
            LiricasTextPane.setText("Reproducción detenida");

        } catch (Exception ex) {
            System.out.println("Error al detener: " + ex.getMessage());
        }
    }

    private void salirDePrograma() {
        detenerReproduccion();
        dispose();
    }

    // Funciones auxiliares, para no tener un codigo juntito tan largo

    private String armarMetadatos(String artista, String titulo, String album, ArrayList<String> otros) {
        StringBuilder texto = new StringBuilder();

        texto.append("Título: ").append(titulo).append("\n");
        texto.append("Artista: ").append(artista).append("\n");
        texto.append("Álbum: ").append(album).append("\n");

        texto.append("\nInformación Adicional\n");

        if (otros.isEmpty()) {
            texto.append("No hay metadatos adicionales!");
        } else {
            for (String x : otros) {
                texto.append(x).append("\n");
            }
        }

        return texto.toString();
    }
}