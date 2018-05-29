import java.awt.EventQueue;
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

public class Interfaz extends JFrame {

	private JPanel contentPane;
	private boolean guardar = false;
	private static final long serialVersionUID = 1L;
	private JTextArea textAreaAnalisis;
	private JTextArea textAreaConsola;
	private JTree tree;
	File Fichero;
	DefaultTreeModel modelo;
	DefaultMutableTreeNode root;
	static comp analizador = null;
	String recuperacion;

	/**
	 * Launch the application.
	 */
	static Interfaz frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Interfaz();
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
	public Interfaz() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 584, 493);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Archivo");
		menuBar.add(mnNewMenu);

		JMenuItem mntmGuardar = new JMenuItem("Guardar");
		mnNewMenu.add(mntmGuardar);
		mntmGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File archivo = Fichero;
				if (archivo != null) {
					if (!archivo.getName().endsWith("huq")) {
						archivo = new File(archivo.getAbsolutePath());
						Fichero = archivo;
						guardar = true;
					}
					FileWriter escritor = null;
					try {
						escritor = new FileWriter(archivo);
						escritor.write(textAreaAnalisis.getText());
						Fichero = archivo;
						guardar = true;
					} catch (FileNotFoundException ex) {
						Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
					} catch (IOException ex) {
						Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
					} finally {
						try {
							escritor.close();
						} catch (IOException ex) {
							Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}
		});

		JMenuItem mntmAbrir = new JMenuItem("Abrir");
		mnNewMenu.add(mntmAbrir);

		mntmAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				guardar = true;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// Creamos el filtro
				FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.huq", "huq");
				// Le indicamos el filtro
				fileChooser.setFileFilter(filtro);

				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
					File archivo = fileChooser.getSelectedFile();
					Fichero = archivo;
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
							textAreaAnalisis.setText(contenidoFichero.toString());
							Fichero = new File(archivo.getAbsolutePath());
							mntmGuardar.setEnabled(true);
							////////////////////////////////////////////////////

						} catch (FileNotFoundException ex) {
							Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IOException ex) {
							Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
						} finally {
							try {
								lector.close();
							} catch (IOException ex) {
								Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					} else {
						JOptionPane.showMessageDialog(null, "Recuerde, debe abrir un archivo con extension .huq",
								"Importante", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});

		JMenuItem mntmGuardarComo = new JMenuItem("GuardarComo");
		mnNewMenu.add(mntmGuardarComo);
		mntmGuardarComo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				guardarComo();
			}

		});

		JMenuItem mntmNuevo = new JMenuItem("Nuevo");
		mnNewMenu.add(mntmNuevo);

		mntmNuevo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaAnalisis.setText("");
				guardar = false;
				textAreaConsola.setText("");
				clearArbol();
				Fichero = null;
			}

		});

		JMenu mnNewMenu_1 = new JMenu("Compilar");
		menuBar.add(mnNewMenu_1);
		JMenuItem mntmAnalizar = new JMenuItem("Analizar");
		mnNewMenu_1.add(mntmAnalizar);
		mntmAnalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textAreaAnalisis.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "No hay código para compilar", "Oops...!",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					// SI HAY CODIGO PARA COMPILAR Y NO SE HA GUARDADO, ABRIMOS
					// MODAL PARA GUARDAR.
					if (Fichero == null && guardar == false) {
						if (guardarComo()) {
							compilar();

							JOptionPane.showMessageDialog(frame, "Listo!", "", JOptionPane.INFORMATION_MESSAGE);
						} else {
							textAreaConsola.setText("No pudimos cargar guardar su código.");
						}

					} else if (Fichero.exists() && guardar == false) {
						JOptionPane.showMessageDialog(frame, "De clic en Archivo>Guardar y vuelva a compilar",
								"Importante!", JOptionPane.INFORMATION_MESSAGE);
					} else {
						compilar();

						Traductor mitraductor = new Traductor(textAreaAnalisis.getText());
						mitraductor.dividirTextoenLinea();

						JOptionPane.showMessageDialog(frame, "Listo!", "", JOptionPane.INFORMATION_MESSAGE);
					}

				}

				// JOptionPane.showMessageDialog(frame, "Guarde los ultimos
				// cambios antes de
				// compilar", "Oops...!", JOptionPane.INFORMATION_MESSAGE);

			}

			private void compilar() {
				analizador.setRecuperacion("");
				InputStream stream = new ByteArrayInputStream(
						textAreaAnalisis.getText().getBytes(StandardCharsets.UTF_8));

				// Funcion reInit()
				if (analizador == null)
					analizador = new comp(stream);
				else {
					analizador.ReInit(stream);
					analizador.setRecuperacion("");
				}
				try {
					@SuppressWarnings("static-access")
					SimpleNode variable = analizador.Programa();
					//variable.dump("");
					root = new DefaultMutableTreeNode(variable);
					modelo = new DefaultTreeModel(root);
					llenarArbol(variable, root);
					modelo.reload();
					tree.setModel(modelo);
					recuperacion = analizador.getRecuperacion();
					if (recuperacion.equals(""))
						textAreaConsola.setText("Compilación finalizada.");
					else
						textAreaConsola.setText("Se han encontrado los siguientes errores:\n " + recuperacion
								+ "Compilación finalizada.");
					analizador.getContenedores().clear();
					analizador.getAcumular().clear();
				} catch (ParseException e1) {
					textAreaConsola.setText("Se han encontrado errores.\n\\n\\n" + e1);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			private ArrayList<Node> hijosArray(Node padre) {
				clearArbol();
				
				ArrayList<Node> hijos = new ArrayList<>();

				for (int i = 0; i < padre.jjtGetNumChildren(); i++) {
					hijos.add(padre.jjtGetChild(i));
				}
				return hijos;
			}

			private void llenarArbol(Node node, DefaultMutableTreeNode dnode) {
				if (node.jjtGetNumChildren() != 0) {
					for (Node nodoAux : hijosArray(node)) {
						DefaultMutableTreeNode dmnode = new DefaultMutableTreeNode(nodoAux.toString());
						dnode.add(dmnode);
						modelo.reload();
						llenarArbol(nodoAux, dmnode);
					}
				}
			}
		});

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 384, 265);
		contentPane.add(scrollPane);

		textAreaAnalisis = new JTextArea();
		textAreaAnalisis.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				guardar = false;
			}
		});

		scrollPane.setViewportView(textAreaAnalisis);
		TextLineNumber tln = new TextLineNumber(textAreaAnalisis);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 303, 548, 119);
		contentPane.add(scrollPane_1);

		textAreaConsola = new JTextArea();
		scrollPane_1.setViewportView(textAreaConsola);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(404, 11, 154, 265);
		contentPane.add(scrollPane_2);

		tree = new JTree();
		clearArbol();
		scrollPane_2.setViewportView(tree);

		JLabel lblConsola = new JLabel("Consola");
		lblConsola.setBounds(10, 287, 46, 14);
		contentPane.add(lblConsola);
		scrollPane.setRowHeaderView(tln); // descomentar

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
				Fichero = archivo;
				guardado = true;
			}
			FileWriter escritor = null;
			try {
				escritor = new FileWriter(archivo);
				escritor.write(textAreaAnalisis.getText());
				Fichero = archivo;
				guardado = true;
				guardar = true;
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					escritor.close();
				} catch (IOException ex) {
					Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return guardado;
	}

	public JTextArea getTextAreaAnalisis() {
		return textAreaAnalisis;
	}

	public JTextArea getTextAreaConsola() {
		return textAreaConsola;
	}

	public JTree getTree() {
		return tree;
	}

	public void clearArbol() {
		DefaultTreeModel model = new DefaultTreeModel(null);
		tree.setModel(model);
	}

}
