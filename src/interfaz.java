import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class interfaz extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args)  {
		JFrame frame= new JFrame();
		String cwd = System.getProperty("user.dir");
        JFileChooser jfc = new JFileChooser(cwd);
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("código a analizar", "huq");
        jfc.addChoosableFileFilter(filter);
        frame.add(jfc);
        Dimension ss = Toolkit.getDefaultToolkit ().getScreenSize ();
        Dimension frameSize = new Dimension ( 500, 300 );
        frame.setBounds ( ss.width / 2 - frameSize.width / 2, 
                ss.height / 2 - frameSize.height / 2,
                frameSize.width, frameSize.height );
        frame.setVisible(true);
        	
        
    
		
		
	}

}
