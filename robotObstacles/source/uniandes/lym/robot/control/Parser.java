package uniandes.lym.robot.control;

import java.util.ArrayList;
import java.util.Arrays;


public class Parser {
	
	private ArrayList<String> arregloTokens;
	private String tokenActual;
	private int tokenId = 1;
	public ArrayList<String> variableNames = new ArrayList<String>();
	public ArrayList<String> procsNames = new ArrayList<String>();
	
	
	public Parser (ArrayList<String> arregloDeTokens) {
		arregloTokens = arregloDeTokens;
		tokenActual = arregloTokens.get(0);
		arregloTokens.remove(0);		
	}
	
	public void ejecutarParser() {
		program();
	}
	
	
	
	

	private void program() {
		if (tokenActual.equalsIgnoreCase("ROBOT_R")) {
			
			if (!arregloTokens.isEmpty()) {
				nextToken();
				if (tokenActual.equalsIgnoreCase("VARS")) {
					declarationOfVariables();
				}
				if (tokenActual.equalsIgnoreCase("PROCS")) {
					definitionOfProcedures();
				}
				if (tokenActual.equalsIgnoreCase("[")) {
					blockOfInstructions();	
				}
			}		
		} else {
			throw new RuntimeException("Error: 'ROBOT_R' key word is missing" + " --> token #: " + tokenId);
		}
	}
	
	private void declarationOfVariables() {
		if (tokenActual.equalsIgnoreCase("VARS")) {
			nextToken();
			listOfNames("varDefinition");
		}
		else {
			throw new RuntimeException("Error: 'VARS' key word is missing" + " --> token #: " + tokenId);
		}
		if (!(tokenActual.equals(";"))) {
			throw new RuntimeException("Error: '" + tokenActual + "' declaration of variables must end with ';'" + " --> token #: " + tokenId);
		}
		if (arregloTokens.size() > 1) {
			nextToken();
		}
		
	}
	
	private void listOfNames(String tipo) {
		name(tipo);
		if (tokenActual.equals(",")) {
			nextToken();
			listOfNames(tipo);
		}
	}
	
	private void name(String input) {
		if (!(Character.isLetter(tokenActual.charAt(0)) && tokenActual.chars().mapToObj(c -> (char) c).allMatch(c -> Character.isLetterOrDigit(c)))) {
			throw new RuntimeException("Error: '" + tokenActual + "' is not a valid name" + " --> token #: " + tokenId);
		}
		if (input.equalsIgnoreCase("varDefinition")) {
			variableNames.add(tokenActual);
		}
		if (input.equalsIgnoreCase("varVerify")) {
			if (!(variableNames.contains(tokenActual))) {
				throw new RuntimeException("Error: '" + tokenActual + "' variable has not been defined" + " --> token #: " + tokenId);
			}
		}
		if (input.equalsIgnoreCase("procDefinition")) {
			procsNames.add(tokenActual);
		}
		if (input.equalsIgnoreCase("procVerify")) {
			if (!(procsNames.contains(tokenActual))) {
				throw new RuntimeException("Error: '" + tokenActual + "' procedure has not been defined" + " --> token #: " + tokenId);
			}
		}
		nextToken();
	}
	
	private void definitionOfProcedures() {
		if (tokenActual.equalsIgnoreCase("PROCS")) {
			nextToken();
			procedureDefinitions();
		} else {
			throw new RuntimeException("Error: 'PROCS' key word is missing" + " --> token #: " + tokenId);
		}
	}
	
	private void procedureDefinitions() {
		procedureDefinition();
		if (isName(tokenActual)) {
			procedureDefinitions();
		}
	}
	
	private void procedureDefinition() {
		
		name("procDefinition");
		if (tokenActual.equals("[")) {
			nextToken();
			parameters();
			instructions();
		}
		else {
			throw new RuntimeException("Error: '" + tokenActual + "' a procedure definition must start with '['" + " --> token #: " + tokenId);
		}
		if (!(tokenActual.equals("]"))) {
			throw new RuntimeException("Error: '" + tokenActual + "' a procedure definition must be followed by ']'" + " --> token !#: " + tokenId);
		}
		if (arregloTokens.size() > 1) {
			nextToken();
		}
		
	}
	
	private void parameters() {
		if (tokenActual.equals("|")) {
			nextToken();
			if (!(tokenActual.equals("|"))){
				listOfNames("varDefinition");
			}
			
		}
		else {
			throw new RuntimeException("Error: '" + tokenActual + "' parameters must be  preceded and followed by the symbol '|'" + " --> token #: " + tokenId);
		}
		if (!(tokenActual.equals("|"))){
			throw new RuntimeException("Error: '" + tokenActual + "' parameters must be  preceded and followed by the symbol '|'" + " --> token #: " + tokenId);
		}
		nextToken();
	}
	
	private void blockOfInstructions() {
		if (tokenActual.equals("[")){
			nextToken();
			instructions();
		} else {
			throw new RuntimeException("Error: '" + tokenActual + "' a block of instruction must be  preceded by the symbols '['" + " --> token #: " + tokenId);
		}
		if (!(tokenActual.equals("]"))){
			throw new RuntimeException("Error: '" + tokenActual + "' a block of instruction must be followed by the symbol ']'" + " --> token #: " + tokenId);
		}
		if (!(arregloTokens.isEmpty())) {
			nextToken();
		}
		
	}
	
	private void instructions() {
		instruction();
		
		if (tokenActual.equals(";")) {
			
			nextToken();
			instructions();
		}
	}
	
	private void instruction() {
		if (isCommand(tokenActual)) {
			command();
		}
		else if (isControlStructure(tokenActual)) {
			controlStructure();
		}
		else if (isProcedureCall(tokenActual)) {
			procedureCall();
		}
	}
	
	private void command() {
		if (tokenActual.equalsIgnoreCase("assignTo")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				number();
			
				if (tokenActual.equals(",")) {
					nextToken();
					name("varVerify");
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
					
				}	
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		
		}
		
		else if (tokenActual.equalsIgnoreCase("goto")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")) {
					nextToken();
					numberOrVariable();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		
		}
		
		else if (tokenActual.equalsIgnoreCase("move")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
			
		}
		
		else if (tokenActual.equalsIgnoreCase("turn")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				D();
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("face")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				O();
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}

		else if (tokenActual.equalsIgnoreCase("put")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")) {
					nextToken();
					X();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		
		}

		else if (tokenActual.equalsIgnoreCase("pick")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")) {
					nextToken();
					X();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		
		}

		else if (tokenActual.equalsIgnoreCase("moveToThe")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")) {
					nextToken();
					D();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		
		}

		else if (tokenActual.equalsIgnoreCase("moveInDir")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")) {
					nextToken();
					O();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		
		}
		
		else if (tokenActual.equalsIgnoreCase("jumpToThe")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")) {
					nextToken();
					D();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		 
		}

		else if (tokenActual.equalsIgnoreCase("jumpInDir")) {
			nextToken();
			if (tokenActual.equals(":")) {
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")) {
					nextToken();
					O();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}		
		}
		
		else if (tokenActual.equalsIgnoreCase("nop")) {
			nextToken();
			if (!(tokenActual.equals(":"))) {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
			nextToken();
		}


		
	}
	
	private void controlStructure() {
		if (tokenActual.equalsIgnoreCase("if")) {
			conditional();
		}
		else if (tokenActual.equalsIgnoreCase("while")) {
			loop();
		}
		else if (tokenActual.equalsIgnoreCase("repeat")) {
			repeatTimes();
		}
		
		
	}
	
	private void conditional() {
		nextToken();
		if (tokenActual.equals(":")) {
			nextToken();
			condition();
			
			if (tokenActual.equalsIgnoreCase("then")) {
				nextToken();
				
				if (tokenActual.equals(":")) {
					nextToken();
					blockOfInstructions();
					
					if (tokenActual.equalsIgnoreCase("else")) {
						nextToken();
						
						if (tokenActual.equals(":")) {
							nextToken();
							blockOfInstructions();
						} else {
							throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
						}
						
					} else {
						throw new RuntimeException("Error: '" + tokenActual + "' expected: 'else'" + " --> token #: " + tokenId);
					}
					
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
				}
				
				
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: 'then'" + " --> token #: " + tokenId);
			}
			
		} else {
			throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
		}
	}
	
	private void loop() {
		nextToken();
		if (tokenActual.equals(":")) {
			nextToken();
			condition();
			
			if (tokenActual.equalsIgnoreCase("do")){
				nextToken();
				if (tokenActual.equals(":")) {
					nextToken();
					blockOfInstructions();
					
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
				}
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: 'do'" + " --> token #: " + tokenId);
			}
			
		} else {
			throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
		}
		
		
	}
	
	private void repeatTimes() {
		nextToken();
		if (tokenActual.equals(":")){
			nextToken();
			numberOrVariable();
			blockOfInstructions();
			
		} else {
			throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
		}
	}
	
	private void condition() {
		if (tokenActual.equalsIgnoreCase("facing")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				O();
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("canPut")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")){
					nextToken();
					X();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("canPick")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")){
					nextToken();
					X();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("canMoveInDir")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")){
					nextToken();
					O();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("canJumpInDir")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")){
					nextToken();
					O();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("canMoveToThe")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")){
					nextToken();
					D();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("canJumpToThe")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				numberOrVariable();
				
				if (tokenActual.equals(",")){
					nextToken();
					D();
				} else {
					throw new RuntimeException("Error: '" + tokenActual + "' expected: ','" + " --> token #: " + tokenId);
				}
				
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		}
		
		else if (tokenActual.equalsIgnoreCase("not")) {
			nextToken();
			if (tokenActual.equals(":")){
				nextToken();
				condition();
			} else {
				throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
			}
		} else {
			throw new RuntimeException("Error: '" + tokenActual + "' does not correspond to any condition key words" + " --> token #: " + tokenId);
		}

	}
	
	private void procedureCall() {
		nextToken();
		if (tokenActual.equals(":")){
			nextToken();
			arguments();
		} else {
			throw new RuntimeException("Error: '" + tokenActual + "' expected: ':'" + " --> token #: " + tokenId);
		}
	}
	
	private void arguments() {
		argument();
		if (tokenActual.equals(",")) {
			nextToken();
			arguments();
		}
	}
	
	private void argument() {
		numberOrVariable();
		
	}
	
	private void numberOrVariable() {
		if (tokenActual.matches("\\d+")) {
			number();
		}
		else if (isName(tokenActual)) {
			name("varVerify");
		} else {
			throw new RuntimeException("Error: '" + tokenActual + "' does not correspond to variable nor number" + " --> token #: " + tokenId);
		}
			
	}
	
	private void number() {
		if (!(tokenActual.matches("\\d+"))) {
			throw new RuntimeException("Error: '" + tokenActual + "' does not correspond to number" + " --> token #: " + tokenId);
		}
		nextToken();
	}
	
	private void D() {
		String[] directions = {"left", "right", "around", "front", "back"};
		if (!(Arrays.stream(directions).anyMatch(tokenActual::equalsIgnoreCase))) {
			throw new RuntimeException("Error: '" + tokenActual + "' does not correspond to direction" + " --> token #: " + tokenId);
		}
		nextToken();
	}
	
	private void O() {
		String[] directions = {"north", "south", "east", "west"};
		if (!(Arrays.stream(directions).anyMatch(tokenActual::equalsIgnoreCase))) {
			throw new RuntimeException("Error: '" + tokenActual + "' does not correspond to coordinate" + " --> token #: " + tokenId);
		}
		nextToken();
	}
	
	private void X() {
		String[] directions = {"Balloons", "Chips"};
		if (!(Arrays.stream(directions).anyMatch(tokenActual::equalsIgnoreCase))) {
			throw new RuntimeException("Error: '" + tokenActual + "' does not correspond to ballon nor chips" + " --> token #: " + tokenId);
		}
		nextToken();
	}
	
	
	
	
	
	
	private void nextToken() {
		if (arregloTokens.size() > 0) {
			tokenActual = arregloTokens.get(0);
			arregloTokens.remove(0);
			tokenId ++;
		}
		else {
			throw new RuntimeException("Error: No hay más tokens disponibles");
		}
	}
	
	private boolean isName(String str) {
		boolean r = true;
		if (!(Character.isLetter(tokenActual.charAt(0)) && tokenActual.chars().mapToObj(c -> (char) c).allMatch(c -> Character.isLetterOrDigit(c)))) {
			r = false;
		}
		return r;
	}
	
	private boolean isCommand(String str) {
		boolean r = true;
		if (!(str.equalsIgnoreCase("assignTo") || str.equalsIgnoreCase("goto") || str.equalsIgnoreCase("move") || str.equalsIgnoreCase("turn") || str.equalsIgnoreCase("face") || str.equalsIgnoreCase("put") || str.equalsIgnoreCase("pick") || str.equalsIgnoreCase("moveToThe") || str.equalsIgnoreCase("moveInDir") || str.equalsIgnoreCase("jumpToThe") || str.equalsIgnoreCase("jumpInDir") || str.equalsIgnoreCase("nop"))) {
			r = false;
		}
		return r;
	}
	
	private boolean isControlStructure(String str) {
		boolean r = true;
		if (!(str.equalsIgnoreCase("if") || str.equalsIgnoreCase("while") || str.equalsIgnoreCase("repeat"))) {
			r = false;
		}
		return r;
	}
	
	private boolean isProcedureCall(String str) {
		if (!procsNames.stream().anyMatch(name -> name.equalsIgnoreCase(tokenActual))) {
			throw new RuntimeException("Error: '" + tokenActual + "' procedure has not been defined" + " --> token #: " + tokenId);
		}
		boolean r = isName(str);
		return r;
	}
	
	
	
}