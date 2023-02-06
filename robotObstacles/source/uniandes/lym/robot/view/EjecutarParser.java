package uniandes.lym.robot.view;

import uniandes.lym.robot.control.Parser;
import uniandes.lym.robot.control.Tokenizador;
import java.util.Scanner;
import java.io.File;

public class EjecutarParser {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Ingrese el nombre y la extensión del archivo de texto guardado en la carpeta 'doc': ");
		String input = scan.nextLine();
		
		String userDir = System.getProperty("user.dir");
		String filePath = userDir + File.separator + File.separator + "doc" + File.separator + input;
		Tokenizador tokenizador = new Tokenizador(filePath);
		System.out.println("\nEl programa leído es el siguiente:\n");
		tokenizador.leerArchivo();
		System.out.println("\nLos tokens identificados fueron:\n");
		
		tokenizador.tokenizeLineasDelPrograma();
		
		int i = 1;
		for (String token : tokenizador.tokens) {
		    System.out.println("Token " + i + "#	" +  token);
		    i++;
		}
		
		Parser parser = new Parser(tokenizador.tokens);
		parser.ejecutarParser();
		System.out.println("\nSyntax is correct :)");
		
		
		

	}

}
