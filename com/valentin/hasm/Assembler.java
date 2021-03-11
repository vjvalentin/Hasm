package com.valentin.hasm;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Assembler {
	// Assembles the instructions and writes them to file
	private File inputFile;
	private Scanner reader;
	private FileWriter writer;
	private SymbolTable table;
	private int lc = 0;	// Line count, for error reporting
	// Store all instructions in a string, then write it out to a file at the end.
	private StringBuilder instructions = new StringBuilder();
	
	public Assembler(String inFile) throws Exception {
		// Use default output file path
		inputFile = new File(inFile);
		this.reader = new Scanner(inputFile);
		this.writer = new FileWriter("out.hack"); // Default output file
		table = new SymbolTable(inFile);
		table.buildTable();
	}
	
	public Assembler(String inFile, String outFile) throws Exception {
		inputFile = new File(inFile);
		this.reader = new Scanner(inputFile);
		this.writer = new FileWriter(outFile);
		table = new SymbolTable(inFile);
		table.buildTable();
	}
	
	private String buildAInstruction(int val) {
		// "A instructions" start with 0 and end with a 15-bit value
		String bin = Integer.toBinaryString(val);
		
		// If the string is less than 15 bits, 0-pad it
		if (bin.length() < 15) {
			int zeros = 15 - bin.length();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < zeros; i++)
				sb.append("0");
			sb.append(bin);
			return "0" + sb.toString();
		} 
		// If the string is more than 15 bits, return 15 least-significant
		else if (bin.length() > 15) {
			// Give warning
			System.out.println("WARNING: A-Instruction value " + val + " at line " + lc + " is too large to fit. Final value will be truncated.");
			int start = bin.length() - 15;
			return "0" + bin.substring(start, bin.length());
		}
		// If the string is exactly 15 bits, just return it
		else {
			return "0" + bin;
		}
	}
	
	private String buildCInstruction(String dest, String comp, String jump) throws Exception {
		// Returns a "C" instruction
		// All C instructions start out with three 1 bits, 
		// followed by an a bit that is set to 0 or 1 depending on the 
		// instruction, 6 comp bits that defines the computation, 
		// 3 destination bits to define the destination, and three 
		// jump bits to define a jump operation
		// [111][ac1c2c3c4c5c6][d1d2d3][j1j2j3]
		//   C        comp       dest    jump
		StringBuilder instruction = new StringBuilder();
		instruction.append("111"); // All C instructions start with this
		
		// Get comp instruction
		switch (comp) 
		{
			case "0": // a=0
				instruction.append("0101010");
				break;
			case "1": // a=0
				instruction.append("0111111");
				break;
			case "-1": // a=0
				instruction.append("0111010");
				break;
			case "D": // a=0
				instruction.append("0001100");
				break;
			case "A": // a=0
				instruction.append("0110000");
				break;
			case "M": // a=1
				instruction.append("1110000");
				break;
			case "!D": // a=0
				instruction.append("0001100");
				break;
			case "!A": // a=0
				instruction.append("0110001");
				break;
			case "!M": // a=1
				instruction.append("1110001");
				break;
			case "-D": // a=0
				instruction.append("0001111");
				break;
			case "-A": // a=0
				instruction.append("0110011");
				break;
			case "-M": // a=1
				instruction.append("1110011");
				break;
			case "D+1": // a=0
				instruction.append("0011111");
				break;
			case "A+1": // a=0
				instruction.append("0110111");
				break;
			case "M+1": // a=1
				instruction.append("1110111");
				break;
			case "D-1": // a=0
				instruction.append("0001110");
				break;
			case "A-1": // a=0
				instruction.append("0110010");
				break;
			case "M-1": // a=1
				instruction.append("1110010");
				break;
			case "D+A": // a=0
				instruction.append("0000010");
				break;
			case "D+M": // a=1
				instruction.append("1000010");
				break;
			case "D-A": // a=0
				instruction.append("0010011");
				break;
			case "D-M": // a=1
				instruction.append("1010011");
				break;
			case "A-D": // a=0
				instruction.append("0000111");
				break;
			case "M-D": // a=1
				instruction.append("1000111");
				break;
			case "D&A": // a=0
				instruction.append("0000000");
				break;
			case "D&M": // a=1
				instruction.append("1000000");
				break;
			case "D|A": // a=0
				instruction.append("0010101");
				break;
			case "D|M": // a=1
				instruction.append("1010101");
				break;
			default:
				System.err.println("Invalid comp instruction \"" + comp + "\" in line " + lc + ".");
				throw new Exception(); // TODO: Make a better exception for this
		}
		
		// Get dest instruction
		switch (dest) 
		{
			case "": // null
				instruction.append("000");
				break;
			case "M":
				instruction.append("001");
				break;
			case "D":
				instruction.append("010");
				break;
			case "MD":
				instruction.append("011");
				break;
			case "A":
				instruction.append("100");
				break;
			case "AM":
				instruction.append("101");
				break;
			case "AD":
				instruction.append("110");
				break;
			case "AMD":
				instruction.append("111");
				break;
			default:
				System.err.println("Invalid dest instruction \"" + dest + "\" at line " + lc + ".");
				throw new Exception();
		}
		
		// Get jump instruction
		switch(jump)
		{
			case "": // null
				instruction.append("000");
				break;
			case "JGT": // Jump greater than (0)
				instruction.append("001");
				break;
			case "JEQ": // Jump equal (0)
				instruction.append("010");
				break;
			case "JGE": // Jump greater or equal (0)
				instruction.append("011");
				break;
			case "JLT": // Jump less than (0)
				instruction.append("100");
				break;
			case "JNE": // Jump not equal (0)
				instruction.append("101");
				break;
			case "JLE": // Jump less than or equal (0)
				instruction.append("110");
				break;
			case "JMP": // Unconditional jump
				instruction.append("111");
				break;
			default:
				System.err.println("Invalid jump instruction \"" + jump + "\" at line " + lc + ".");
				throw new Exception();
		}
		
		// A final sanity check, the entire instruction should be exactly 16 bits wide
		String i = instruction.toString();
		if (i.length() != 16) {
			System.err.println("Invalid instruction width, expected 16-bit instruction, recieved " + i.length() + "-bit instruction instead.");
			System.err.println("Error in buildCInstruction() method of Assembler class.");
			throw new Exception();
		} else {
			return i;
		}
	}
	
	private void parseInstructions() throws Exception {
		String line = null;
		while (reader.hasNextLine()) {
			line = Utils.removeWhiteSpace(reader.nextLine());
			lc++;
			
			if (line.compareTo("") == 0 || line.charAt(0) == '/' || line.charAt(0) == '(') {
				// Ignore blank lines, comments, and labels.
				continue;
			}
			else if (line.charAt(0) == '@') {
				// First, find out if there are is a comment on this line
				int comment = line.indexOf("/");
				String value;
				// If there is, get the value up to, but not including, the comment
				if (comment > 0) {
					value = line.substring(1, comment);
					if (Utils.isInt(value)) {
						// Constant int, not a symbol
						instructions.append(buildAInstruction(Integer.parseInt(value)));
						instructions.append("\n");
					}
				}
				// otherwise, the entire rest of the string is a symbol
				else {
					String symbol = line.substring(1);
					int val;
					// If it's not an integer constant, get it's symbol from the table
					if (!Utils.isInt(symbol))
						val = SymbolTable.get(symbol); // Throws exception if symbol not found
					else
						val = Integer.parseInt(symbol);
					instructions.append(buildAInstruction(val));
					instructions.append("\n");
				}
			}
			else {
				// Anything else, we'll assume its a regular instruction
				// Regular instructions come in the form of M=M+1;JGT
				// The first part is the destination
				// The second part is the computation
				// The third part is the jump
				// Both the destination and the jump are optional
				int comment = line.indexOf("/"); // Has a comment
				int equal = line.indexOf("=");	// Has a dest
				int semi = line.indexOf(";");	// Has a jump
				String dest = null;
				String comp = null;
				String jump = null;
				
				// Strip comments
				if (comment > 0) {
					line = line.substring(0, comment);
				}
				
				// Is there a destination?
				if (equal > 0) {
					dest = line.substring(0, equal);
					// Is there a jump?
					if (semi > 0) {
						// Full instruction, get the comp between = and ;
						comp = line.substring(equal + 1, semi);
						// Get the jump after the ;
						jump = line.substring(semi + 1);
					}
					else {
						// No jump, just get everything after the =
						jump = "";
						comp = line.substring(equal + 1);
					}
				}
				else {
					// No dest
					dest = "";
					// Is there a jump?
					if (semi > 0) {
						// comp + jump, get comp up to ;
						comp = line.substring(0, semi);
						// get jump from ;
						jump = line.substring(semi + 1);
					}
					else {
						// No jump either, just a comp
						jump = "";
						comp = line;
					}
				}
				
				if (jump == null)
					System.out.println("JUMP IS NULL IN LINE " + lc);
				else if (dest == null)
					System.out.println("DEST IS NULL IN LINE " + lc);
				else if (comp == null)
					System.out.println("COMP IS NULL IN LINE " + lc);
				
				// Build the C instruction
				instructions.append(buildCInstruction(dest, comp, jump));
				instructions.append("\n");
			}
		}

	}

	public void assemble() throws Exception {
		// TODO: Write this out to a file, the print is for testing purposes.
		parseInstructions();
		writer.write(instructions.toString());
		writer.close();
	}
}
