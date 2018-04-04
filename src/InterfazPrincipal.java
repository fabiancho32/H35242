import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class InterfazPrincipal extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static InterfazPrincipal frame;
	private JTextArea textAreaCodigo;
	private JTextArea txtAreaConsola;
	private JTree tree;
	DefaultTreeModel modelo;
	DefaultMutableTreeNode root;
	static AnalizadorSintactico analizador = null;
	String recuperacion;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new InterfazPrincipal();
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
		setBounds(100, 100, 802, 599);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

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
		mntmIniciarCompilacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textAreaCodigo.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "No hay código para compilar", "Oops...!",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					analizador.setRecuperacion("");
					InputStream stream = new ByteArrayInputStream(
							textAreaCodigo.getText().getBytes(StandardCharsets.UTF_8));
					// Funcion reInit()
					if (analizador == null)
						analizador = new AnalizadorSintactico(stream);					    
					else {
						analizador.ReInit(stream);
						analizador.setRecuperacion("");
					}
					try {
						@SuppressWarnings("static-access")
						SimpleNode variable = analizador.Programa();
						variable.dump("");
						root = new DefaultMutableTreeNode(variable.toString());
						modelo = new DefaultTreeModel(root);
						tree.setModel(modelo);
						modelo.reload();
						llenarArbol(variable);
						recuperacion = analizador.getRecuperacion();
						if (recuperacion.equals(""))
							txtAreaConsola.setText("Se ha compilado con éxito");
						else
							txtAreaConsola.setText("Se han encontrado los siguientes errores:\n "+recuperacion
									+ "se ha compilado con éxito");
					} catch (ParseException e1) {
						txtAreaConsola.setText("Se han encontrado errores.\n\\n\\n" + e1);

					}
				}
			}

			private void llenarArbol(SimpleNode actual) {
				for (int j = 0; j < actual.jjtGetNumChildren(); j++) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(actual.jjtGetChild(j));
					DefaultMutableTreeNode actualMutable = new DefaultMutableTreeNode(actual.toString());
					modelo.insertNodeInto(child, root, j);
					modelo.reload();
					llenarArbol((SimpleNode) actual.jjtGetChild(j));
				}
			}
		});
		mnCompilar.add(mntmIniciarCompilacion);

		JLabel lblConsola = new JLabel("Consola");
		lblConsola.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblConsola.setForeground(Color.BLACK);
		lblConsola.setBounds(32, 415, 97, 19);
		contentPane.add(lblConsola);

		JScrollPane scrollPaneCodigo = new JScrollPane();
		scrollPaneCodigo.setBounds(26, 39, 527, 363);
		contentPane.add(scrollPaneCodigo);

		textAreaCodigo = new JTextArea();
		scrollPaneCodigo.setViewportView(textAreaCodigo);
		TextLineNumber tln = new TextLineNumber(textAreaCodigo);

		JScrollPane scrollPaneTree = new JScrollPane();
		scrollPaneTree.setBounds(565, 37, 205, 503);
		contentPane.add(scrollPaneTree);

		tree = new JTree();
		scrollPaneTree.setViewportView(tree);

		JScrollPane scrollPaneConsola = new JScrollPane();
		scrollPaneConsola.setBounds(32, 447, 520, 93);
		contentPane.add(scrollPaneConsola);

		txtAreaConsola = new JTextArea();
		scrollPaneConsola.setViewportView(txtAreaConsola);

		/* Linea que permite contar numero de lineas en el texArea */
		// scrollPaneCodigo.setRowHeaderView(tln);

		//////////////////////////////////////////////////////////////////// Action
		///////////////////////////////////////////////////////////////////////// MENUS/////////////////////////////////////////////////////////////
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
							   textAreaCodigo.setText(contenidoFichero.toString());
							////////////////////////////////////////////////////

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
				textAreaCodigo.setText("");
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mntmGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// Creamos el filtro
				FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.huq", "huq");
				// Le indicamos el filtro
				fileChooser.setFileFilter(filtro);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(null)) {
					File archivo = fileChooser.getSelectedFile();
					if (!archivo.getName().endsWith("huq")) {
						archivo = new File(fileChooser.getSelectedFile() + ".huq");
					}
					FileWriter escritor = null;
					try {
						escritor = new FileWriter(archivo);
						escritor.write(textAreaCodigo.getText());
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

	public JTextArea getTextArea() {
		return textAreaCodigo;
	}

	public JTextArea getTextArea_1() {
		return txtAreaConsola;
	}

	public JTree getTree() {
		return tree;
	}
}
