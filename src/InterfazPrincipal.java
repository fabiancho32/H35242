import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;

public class InterfazPrincipal extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazPrincipal frame = new InterfazPrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InterfazPrincipal() {

		setTitle("COMPILADOR HUQ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JTree tree = new JTree();
		tree.setBounds(565, 50, 205, 490);
		contentPane.add(tree);

		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(32, 447, 521, 93);
		contentPane.add(textArea_1);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 770, 26);
		contentPane.add(menuBar);

		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);

		JMenuItem mntmAbrir = new JMenuItem("Abrir");		
		mnArchivo.add(mntmAbrir);
		

		JMenuItem mntmNuevo = new JMenuItem("Nuevo");		
		mnArchivo.add(mntmNuevo);

		JMenuItem mntmGuardar = new JMenuItem("Guardar");		
		mnArchivo.add(mntmGuardar);

		JMenu mnCompilar = new JMenu("Compilar");
		menuBar.add(mnCompilar);

		JMenuItem mntmIniciarCompilacion = new JMenuItem("Iniciar Compilacion");
		mnCompilar.add(mntmIniciarCompilacion);

		JLabel lblConsola = new JLabel("Consola");
		lblConsola.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblConsola.setForeground(Color.BLACK);
		lblConsola.setBounds(32, 415, 97, 19);
		contentPane.add(lblConsola);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(26, 39, 527, 363);
		contentPane.add(scrollPane);

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		TextLineNumber tln = new TextLineNumber(textArea);
		// scrollPane.setRowHeaderView(tln);

		
		
		/////////////////////////////////////////////////////////////////////////Action MENUS/////////////////////////////////////////////////////////////////
		mntmAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// Creamos el filtro
				FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.huq", "huq");
				// Le indicamos el filtro
				fileChooser.setFileFilter(filtro);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
					File archivo = fileChooser.getSelectedFile();
					if (archivo.getName().endsWith("huq")) {
						FileReader lector = null;
						try {
							lector = new FileReader(archivo);
							BufferedReader bfReader = new BufferedReader(lector);

							String lineaFichero;
							StringBuilder contenidoFichero = new StringBuilder();

							// Recupera el contenido del fichero
							while ((lineaFichero = bfReader.readLine()) != null) {
								contenidoFichero.append(lineaFichero);
								contenidoFichero.append("\n");
							}

							// Pone el contenido del fichero en el area de texto
							textArea.setText(contenidoFichero.toString());

						} catch (FileNotFoundException ex) {
							Logger.getLogger(InterfazPrincipal.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IOException ex) {
							Logger.getLogger(InterfazPrincipal.class.getName()).log(Level.SEVERE, null, ex);
						} finally {
							try {
								lector.close();
							} catch (IOException ex) {
								Logger.getLogger(InterfazPrincipal.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					} else {
						JOptionPane.showMessageDialog(null, "Recuerde, debe abrir un archivo con extension .huq",
								"Importante", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mntmNuevo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");   
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mntmGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		        //Creamos el filtro
		        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.huq", "huq");
		        //Le indicamos el filtro
		        fileChooser.setFileFilter(filtro);
		        if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(null)) {
		            File archivo = fileChooser.getSelectedFile();
		            if (!archivo.getName().endsWith("huq")) {
		            	archivo = new File(fileChooser.getSelectedFile()+".huq");
		            }
		                FileWriter escritor = null;
		                try {
		                    escritor = new FileWriter(archivo);
		                    escritor.write(textArea.getText());
		                } catch (FileNotFoundException ex) {
		                    Logger.getLogger(InterfazPrincipal.class.getName()).log(Level.SEVERE, null, ex);
		                } catch (IOException ex) {
		                    Logger.getLogger(InterfazPrincipal.class.getName()).log(Level.SEVERE, null, ex);
		                } finally {
		                    try {
		                        escritor.close();
		                    } catch (IOException ex) {
		                        Logger.getLogger(InterfazPrincipal.class.getName()).log(Level.SEVERE, null, ex);
		                    }
		                }
		            
		        }
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	}
}
