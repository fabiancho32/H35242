import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTree;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

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
		
		JMenuItem mntmGuardarComo = new JMenuItem("Guardar Como");
		mnArchivo.add(mntmGuardarComo);
		
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
		scrollPane.setRowHeaderView(tln);
	}
}
