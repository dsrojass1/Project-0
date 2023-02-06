package uniandes.lym.robot.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Tokenizador {
	
	/************************ ATRIBUTOS *******************************/
	
	private String direccionArchivo;
	private ArrayList<String> lineasDelPrograma;
	public ArrayList<String> tokens = new ArrayList<>();;
	
	
	
	/************************ CONSTRUCTORES *******************************/
	
	public Tokenizador (String nombreArchivo) {
	    this.direccionArchivo = nombreArchivo;
	    this.lineasDelPrograma = new ArrayList<String>();
	  }
	
	
	
	/************************ MÉTODOS *******************************/
	
	/* 
	 * Este método setter asigna al atributo de la clase 'nombreArchivo'
	 */
	public void setNombreArchivo (String direccion) {
		direccionArchivo = direccion;
	}
	
	/* 
	 * Este método lee del archivo las lineas del programa y las almacena en el 
 	 * arreglo de strings 'lineasDelPrograma'
	 */
	public void leerArchivo() {
	  try (BufferedReader br = new BufferedReader(new FileReader(direccionArchivo))) {
	    String line;
	    while ((line = br.readLine()) != null) {
	    	System.out.println(line);
	    	lineasDelPrograma.add(line);
	    }
	  } catch (IOException e) {
	    System.out.println("Error reading file: " + e.getMessage());
	  }
	}
	
	
	
	/* 
	 * Tokenizador  
	 */
	public void tokenizeLineasDelPrograma() {
		  for (String linea : lineasDelPrograma) {
		    StringBuilder tokenBuffer = new StringBuilder();
		    for (int i = 0; i < linea.length(); i++) {
		      char c = linea.charAt(i);
		      String characterString = String.valueOf(c);

		      if (!(characterString.equals(" ") || characterString.equals("\n"))) {
		        tokenBuffer.append(characterString);
		        if (characterString.equals("|") || characterString.equals("[") || characterString.equals("]") || characterString.equals(":") || characterString.equals(",")) {
		          
		        	if (tokenBuffer.length() > 1) {
		        		tokens.add(tokenBuffer.substring(0, tokenBuffer.length() - 1).toString());
		        		tokens.add(tokenBuffer.substring(tokenBuffer.length() - 1).toString());
		        		tokenBuffer = new StringBuilder();
		        	} else {
		        	
		        	tokens.add(tokenBuffer.toString());
		        	tokenBuffer = new StringBuilder();
		        	}
		        }
		      } else if (tokenBuffer.length() > 0) {
		        tokens.add(tokenBuffer.toString());
		        tokenBuffer = new StringBuilder();
		      }
		    }
		    if (tokenBuffer.length() > 0) {
		      tokens.add(tokenBuffer.toString());
		    }
		  }
		}
	
	
	
	
}
