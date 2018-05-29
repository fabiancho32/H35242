import java.util.ArrayList;
import java.util.StringTokenizer;

public class convertirCodigo {
	ArrayList<String> listaLineas;
	ArrayList<String> listaPalabras;
	private StringTokenizer linea;
	private StringTokenizer palabra;
	private String textonevo = "";
	private String codigo;
	private String numero = " int ";
	private String cadena = " String ";
	private String si = "if";
	private String entonces = " Entonces ";
	private String sino = " else ";
	private String escribir = "System.out.println";
	private String leer = "br.readLine()";
	private String parse = "Integer.parseInt";

	public convertirCodigo(String codigo) {
		this.codigo = codigo;
		listaLineas = new ArrayList<String>();
		listaPalabras = new ArrayList<String>();
		linea = new StringTokenizer(codigo, "\n");

	}

	public void revisarCodigo() {

		while (linea.hasMoreTokens()) {
			listaLineas.add(linea.nextToken());
			// System.out.println("token: " + linea.nextToken());
		}

		// hacer ciclo
		String cad = listaLineas.get(0);

		if (cad.contains("##")) {

			cad = eliminarEspaciosInicio(cad);

			if (cad.charAt(0) == '#') {
				cad = cad.trim().substring(2, cad.length());
				StringTokenizer nuevo = new StringTokenizer(cad, "##");
				ArrayList<String> auxiliar = new ArrayList<>();
				while (nuevo.hasMoreTokens()) {
					auxiliar.add(nuevo.nextToken());
				}

				textonevo = textonevo + "/*" + auxiliar.get(0) + "*/";

				cad = auxiliar.get(1);
				dividirlinea(cad);
				cad = listaPalabras.get(0);
				if (validarPalabraReser(cad)) {
					cad = listaPalabras.get(1);
					cad = cad.substring(0, cad.length() - 1);
					textonevo += cad + ";";

					System.out.println("Este es el texto: " + textonevo);

				}
			}

		} else {
			cad = eliminarEspaciosInicio(cad);
			dividirlinea(cad);
			cad = listaPalabras.get(0);
			if (validarPalabraReser(cad)) {
				cad = listaPalabras.get(1);
				cad = cad.substring(0, cad.length() - 1);
				textonevo += cad + ";";
				System.out.println("Este es el texto: " + textonevo);

			}
		}

	}

	public String eliminarEspaciosInicio(String linea) {

		for (int i = 0; i < linea.length(); i++) {
			if (linea.charAt(i) != ' ') {
				return linea.substring(i, linea.length());
			}
		}
		return "";
	}

	public void dividirlinea(String linea) {
		palabra = new StringTokenizer(linea);
		while (palabra.hasMoreTokens()) {
			listaPalabras.add(palabra.nextToken());

		}
	}

	public boolean validarPalabraReser(String cadena) {
		if (cadena.equals("Numero")) {
			textonevo += numero;
			return true;
		}
		if (cadena.equals("Cadena")) {
			textonevo += this.cadena;
			return true;
		}

		if (cadena.equals("Sino")) {
			textonevo += sino;
			return true;
		}
		if (cadena.equals("FinSi")) {
			textonevo += cadena;
			return true;
		}
		return false;
	}

}
