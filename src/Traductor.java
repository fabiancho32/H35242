import java.util.ArrayList;
import java.util.StringTokenizer;

public class Traductor {
	ArrayList<String> listaLineas;
	ArrayList<String> listaPalabras;
	private StringTokenizer linea;
	private StringTokenizer palabra;
	private String textonevo ="import java.io.BufferedReader; \n" + "import java.io.IOException; \n"
			+ "import java.io.InputStreamReader; \n" + "public class Ejemplo {\n"
			+ "	public static void main(String args[] throws IOException){\n"
			+ "InputStreamReader isr = new InputStreamReader(System.in);\n"
			+ "BufferedReader br = new BufferedReader (isr);\n";
	private String codigo;
	private String comentarioinicio = "";
	private String comentariofin = "";
	private String numero = " int ";
	private String cadena = " String ";
	private String si = "if";
	private String entonces = " Entonces ";
	private String sino = " else ";
	private String escribir = "System.out.println(";
	private String leer = "br.readLine()";
	private String parse = "Integer.parseInt";

	public Traductor(String codigo) {
		this.codigo = codigo;
		listaLineas = new ArrayList<String>();
		listaPalabras = new ArrayList<String>();
		linea = new StringTokenizer(codigo, "\n");
	}

	public String eliminarEspaciosInicio(String linea) {

		for (int i = 0; i < linea.length(); i++) {
			if (linea.charAt(i) != ' ') {
				return linea.substring(i, linea.length());
			}
		}
		return "";
	}

	public String eliminarComentario(String linea) {
		if (linea.charAt(0) == '#') {
			linea = linea.substring(2, linea.length());
			int pos = linea.indexOf('#');
			comentarioinicio = linea.substring(0, pos + 2);
			comentarioinicio= "/*"+comentarioinicio.replaceAll("##", "*/");
			linea = linea.substring(pos + 2, linea.length());
			return linea;
		} else {
			int pos = linea.indexOf('#');
			comentariofin = linea.substring(pos, linea.length());
			comentariofin="//"+comentariofin.replaceAll("##", "");
			linea = linea.substring(0, pos);
			return linea;
		}
	}

	public void dividirLineaEnPalabra(String linea) {
		palabra = new StringTokenizer(linea);
		while (palabra.hasMoreTokens()) {
			listaPalabras.add(palabra.nextToken());
		}
	}

	public void dividirTextoenLinea() {
		while (linea.hasMoreTokens()) {
			listaLineas.add(linea.nextToken());
		}
		recorrerLineas();
	}
	/// no olvidar quitar espacios
	// limpiar arrays

	public void recorrerLineas() {
		for (int i = 0; i < listaLineas.size(); i++) {

			String linea = eliminarEspaciosInicio(listaLineas.get(i));

			while(linea.contains("#")) {
				linea = eliminarComentario(linea);
			}

			if (linea.contains("Numero") && linea.contains("Leer[]")) {
				validarAsignacionLeer(linea);
			}
			if (linea.contains("Numero") || linea.contains("Cadena")) {
				validarDeclaracion(linea);
			}

			if (linea.contains("Escribir")) {
				validarEscribir(linea);
			}
			if (linea.contains("Leer[]")) {
				validarLeer(linea);
			}

			if (linea.contains("Si") && linea.contains("Entonces")) {
				validarIf(linea);
			}

			if (linea.contains("Sino")) {
				validarSino();
			}

			if (linea.contains("Finsi")) {
				validarFinSi();
			}
			textonevo +=comentariofin+"\n";
			comentarioinicio="";
			comentariofin="";
			listaPalabras.clear();
		}

		System.out.println("Textojava:   \n" + textonevo);
	}

	public void validarDeclaracion(String linea) {
		String palabra;
		dividirLineaEnPalabra(linea);
		palabra = listaPalabras.get(0);

		if (palabra.equals("Numero")) {
			textonevo +=comentarioinicio+ numero;
		}

		if (palabra.equals("Cadena")) {
			textonevo +=comentarioinicio+ cadena;
		}

		palabra = listaPalabras.get(1);
		palabra = palabra.substring(0, palabra.length() - 1);
		textonevo += palabra + ";";

	}

	public void validarEscribir(String linea) {
		String mensaje = linea.substring(9, linea.length() - 2);
		mensaje = mensaje.replaceAll("@", "+");
		textonevo +=comentarioinicio+ escribir + mensaje + ");";
	}

	public void validarAsignacionLeer(String linea) {
		String palabra;
		dividirLineaEnPalabra(linea);
		palabra = listaPalabras.get(0);

		if (palabra.equals("Numero")) {
			textonevo +=comentarioinicio+ numero;
		}

		if (palabra.equals("Cadena")) {
			textonevo +=comentarioinicio+ cadena;
		}

		palabra = listaPalabras.get(1);
		palabra = palabra.substring(0, palabra.length() - 1);
		palabra = palabra.replaceAll("Leer[]", leer);
		textonevo += palabra + ";";
		//System.out.println("Este es el texto: " + textonevo);

	}

	public void validarLeer(String linea) {
		linea = linea.replace("Leer[].", leer + ";");
		textonevo +=comentarioinicio+ linea;
	}

	public void validarIf(String linea) {
		dividirLineaEnPalabra(linea);
		textonevo +=comentarioinicio+ si + "(" + listaPalabras.get(1) + ")" + "{";
	}

	public void validarSino() {
		textonevo += "} \n"+comentarioinicio + "else{";
	}

	public void validarFinSi() {
		textonevo +=comentarioinicio+"}";
	}
}
