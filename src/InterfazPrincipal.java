import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import prueba.*;
import prueba.ParseException;
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

		JMenuItem mntmGuardarComo = new JMenuItem("Guardar Como");
		mnArchivo.add(mntmGuardarComo);
		
		JMenuItem mntmGuardar = new JMenuItem("Guardar");
		mntmGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardar=true;
				if(fFichero!=null){
					modificar(fFichero,cadena,textAreaCodigo.getText());
				}else{
					mntmGuardar.getAction();
				}
				//falta el codigo que sobreescriba el archivo que ya esta cargador 
			}
		});
		mnArchivo.add(mntmGuardar);

		JMenu mnCompilar = new JMenu("Compilar");
		menuBar.add(mnCompilar);

		JMenuItem mntmIniciarCompilacion = new JMenuItem("Iniciar Compilacion");
		
		mntmIniciarCompilacion.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(guardar) {
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

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}else
				{
					JOptionPane.showMessageDialog(frame, "Guarde los ultimos cambios antes de compilar", "Oops...!",
							JOptionPane.INFORMATION_MESSAGE);
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
		textAreaCodigo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				guardar=false;
			}
		});
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
				guardar=true;
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
							   
							   fFichero= archivo;
							   cadena=textAreaCodigo.getText();
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
				//poner opcion
				textAreaCodigo.setText("");
				guardar=false;
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mntmGuardarComo.addActionListener(new ActionListener() {
			
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
						fFichero= archivo;
						cadena= textAreaCodigo.getText();
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
	  void Escribir(File fFichero,String cadena)
	    {
	        // Declaramos un buffer de escritura
	        BufferedWriter bw;

	        try
	        {
	            // Comprobamos si el archivo no existe y si es asi creamos uno nuevo.
	         if(!fFichero.exists())
	         {
	             fFichero.createNewFile();
	         }

	           // Luego de haber creado el archivo procedemos a escribir dentro de el.
	            bw = new BufferedWriter(new FileWriter(fFichero,true));
	            bw.write(cadena);
	            bw.close();

	        }catch(Exception e)
	        {
	            System.out.println(e);
	        }

	    }
	  void modificar(File fAntiguo,String aCadena,String nCadena)
	    {
	       /*
	            Las dos lienas de codigo siguientes, basicamente lo que hacen es generar un numero aleatorio y
	            asignarselos a una nueva variable "nFnuevo" (Nombre Fichero Nuevo) la cual es igual a la ruta
	            del directorio padre "fAntiguo" mas  la palabra auxiliar seguido del numero aletorio y la extension
	            del archivo nuevo
	       * */
	        Random numaleatorio = new Random(3816L);
	        String nFnuevo = fAntiguo.getParent()+"/auxiliar"+String.valueOf(Math.abs(numaleatorio.nextInt()))+".txt";

	     // Creo un nuevo archivo
	        File fNuevo= new File(nFnuevo);
	        // Declaro un nuevo buffer de lectura
	        BufferedReader br;
	        try
	        {
	            /*Valido si el fichero antiguo que pasamos como parametro existe, si es asi procedo a leer el
	            contenido que hay dentro de el
	             */

	            if(fAntiguo.exists())
	            {
	                br = new BufferedReader(new FileReader(fAntiguo));

	                String linea;

	                /* Mientras el contenido del archivo sea diferente de null procedo a comprar  la linea a modificar con
	                lo que hay dentro del archivo, si linea es igual a aCadena escribo el contenido de aCadena en mi nuevo
	                fichero(Auxiliar) de lo contrario escribo el mismo contenido que ya tenia el antiguo fichero en mi fichero auxiliar

	                 */
	                while((linea=br.readLine()) != null)
	                {
	                    if(linea.equals(aCadena))
	                    {
	                        Escribir(fNuevo,nCadena);

	                    }
	                    else
	                    {
	                        Escribir(fNuevo,linea);
	                    }
	                }

	              // Cierro el buffer de lectura
	                br.close();

	                // Capturo el nombre del fichero antiguo
	                String nAntiguo = fAntiguo.getName();
	                // Borro el fichero antiguo
	                borrar(fAntiguo);
	                //Renombro el fichero auxiliar con el nombre del fichero antiguo
	                fNuevo.renameTo(fAntiguo);




	            }
	            else
	            {
	                System.out.println("Fichero no Existe");
	            }

	        }catch(Exception e)
	        {
	            System.out.println(e);
	        }
	    }


void borrar (File Ffichero)
{
    try
    {
       // Comprovamos si el fichero existe  de ser así procedemos a borrar el archivo
        if(Ffichero.exists())
        {
            Ffichero.delete();
            System.out.println("Ficherro Borrado");
        }

    }catch(Exception e)
    {
        System.out.println(e);
    }
}
}

