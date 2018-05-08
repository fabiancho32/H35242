import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
	boolean guardar;
	String cadena;
	File fFichero;

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
		setBounds(100, 100, 863, 666);
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

		JMenuItem mntmGuardarComo = new JMenuItem("Guardar Como");
		mnArchivo.add(mntmGuardarComo);

		JMenuItem mntmGuardar = new JMenuItem("Guardar");
		mntmGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File archivo = fFichero;
					if (!archivo.getName().endsWith("huq")) {
						archivo = new File(archivo.getAbsolutePath());
						fFichero = archivo;
						guardar = true;
					}
					FileWriter escritor = null;
					try {
						escritor = new FileWriter(archivo);
						escritor.write(textAreaCodigo.getText());
						fFichero = archivo;
						guardar = true;
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
				} catch (Exception e2) {
					guardarComo();
				}
				

			}

		});
		mnArchivo.add(mntmGuardar);

		JMenu mnCompilar = new JMenu("Compilar");
		menuBar.add(mnCompilar);

		JMenuItem mntmIniciarCompilacion = new JMenuItem("Iniciar Compilacion");

		mntmIniciarCompilacion.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// SI NO HAY NADA ENTONCES IMPRIME MENSAJE "NO HAY CODIGO PARA COMPILAR"
				if (textAreaCodigo.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "No hay c�digo para compilar", "Oops...!",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					// SI HAY CODIGO PARA COMPILAR Y NO SE HA GUARDADO, ABRIMOS MODAL PARA GUARDAR.
					if (fFichero == null && guardar == false) {
						if (guardarComo()) {
							compilar();
							JOptionPane.showMessageDialog(frame, "Listo!", "", JOptionPane.INFORMATION_MESSAGE);
						} else {
							txtAreaConsola.setText("No pudimos cargar guardar su c�digo.");
						}

					} else if (fFichero.exists() && guardar == false) {
						JOptionPane.showMessageDialog(frame, "De clic en Archivo>Guardar y vuelva a compilar",
								"Importante!", JOptionPane.INFORMATION_MESSAGE);
					} else {
						compilar();
						JOptionPane.showMessageDialog(frame, "Listo!", "", JOptionPane.INFORMATION_MESSAGE);
					}

				}

			}

			private void compilar() {
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
					llenarArbol(variable, null);
					recuperacion = analizador.getRecuperacion();
					if (recuperacion.equals(""))
						txtAreaConsola.setText("Compilación finalizada.");
					else
						txtAreaConsola.setText("Se han encontrado los siguientes errores:\n " + recuperacion
								+ "Compilación finalizada.");
				} catch (ParseException e1) {
					txtAreaConsola.setText("Se han encontrado errores.\n\\n\\n" + e1);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			private ArrayList<Node> hijosArray(Node padre) {
				ArrayList<Node> hijos = new ArrayList<>();

				for (int i = 0; i < padre.jjtGetNumChildren(); i++) {
					hijos.add(padre.jjtGetChild(i));
				}
				return hijos;
			}

			private void llenarArbol(Node node, DefaultMutableTreeNode dnode) {

				if (node == modelo.getRoot()) {

					if (node.jjtGetNumChildren() != 0) {
						for (Node nodoAux : hijosArray(node)) {
							DefaultMutableTreeNode dmnode = new DefaultMutableTreeNode(nodoAux);
							root.add(new DefaultMutableTreeNode(dmnode));
							modelo.reload();
							llenarArbol(nodoAux, dmnode);
						}
					}

				} else {

					if (node.jjtGetNumChildren() != 0) {
						for (Node nodoAux : hijosArray(node)) {
							DefaultMutableTreeNode dmnode = new DefaultMutableTreeNode(nodoAux);
							root.add(new DefaultMutableTreeNode(dmnode));
							modelo.reload();
							llenarArbol(nodoAux, dmnode);
						}
					}
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
		textAreaCodigo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				guardar = false;
			}
		});
		scrollPaneCodigo.setViewportView(textAreaCodigo);
		TextLineNumber tln = new TextLineNumber(textAreaCodigo);

		JScrollPane scrollPaneTree = new JScrollPane();
		scrollPaneTree.setBounds(565, 37, 205, 569);
		contentPane.add(scrollPaneTree);

		tree = new JTree();
		clearArbol();
		scrollPaneTree.setViewportView(tree);

		JScrollPane scrollPaneConsola = new JScrollPane();
		scrollPaneConsola.setBounds(32, 447, 521, 159);
		contentPane.add(scrollPaneConsola);

		txtAreaConsola = new JTextArea();
		scrollPaneConsola.setViewportView(txtAreaConsola);

		/* Linea que permite contar numero de lineas en el texArea */
		scrollPaneCodigo.setRowHeaderView(tln);

		//////////////////////////////////////////////////////////////////// Action
		///////////////////////////////////////////////////////////////////////// MENUS/////////////////////////////////////////////////////////////
		mntmAbrir.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				guardar = true;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// Creamos el filtro
				FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.huq", "huq");
				// Le indicamos el filtro
				fileChooser.setFileFilter(filtro);

				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
					File archivo = fileChooser.getSelectedFile();
					fFichero = archivo;
					System.out.println(archivo.getAbsolutePath());

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
							fFichero = new File(archivo.getAbsolutePath());
							mntmGuardar.setEnabled(true);
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
				// poner opcion
				textAreaCodigo.setText("");
				guardar = false;
				txtAreaConsola.setText("");
				clearArbol();
				fFichero = null;
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mntmGuardarComo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				guardarComo();
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	}

	public boolean guardarComo() {
		boolean guardado = false;
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
				fFichero = archivo;
				guardado = true;
				guardar=true;
			}
			FileWriter escritor = null;
			try {
				escritor = new FileWriter(archivo);
				escritor.write(textAreaCodigo.getText());
				fFichero = archivo;
				guardado = true;
				guardar=true;
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
		return guardado;
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

	public void clearArbol() {
		DefaultTreeModel model = new DefaultTreeModel(null);
		tree.setModel(model);
	}

}