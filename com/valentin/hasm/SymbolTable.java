package com.valentin.hasm;
/**
 * Defines and builds a symbol table for Hack Assembly programs.
 * @version 1.0 2021-03-06
 * @author Vincent Valentin
 */

import java.util.*;
import java.io.*;

public class SymbolTable {
	private static Map<String, Integer> symbolTable = new HashMap<String, Integer>();
	private File inputFile;
	private Scanner inputFileScanner;
	private static int userMemory = 16;	// Start of user-defined memory allocation
	private static int ic = 0;			// Instruction counter, for label addressing
	private static int lc = 0;			// Line counter, for error reporting
	
	public SymbolTable(String filePath) throws FileNotFoundException {
		this.init();
		this.inputFile = new File(filePath);
		this.inputFileScanner = new Scanner(inputFile);
	}
	
	private void putSymbol(String s, int address) {
		if (SymbolTable.symbolTable.get(s) == null) { // IF the symbol doesn't already exist in the table
			SymbolTable.symbolTable.put(s, address); // Store it in table
			SymbolTable.userMemory++; // Increase user memory
		}
	}
	
	private void init() {
		// Insert built-in types into the symbolTable
		
		// R0-R15 are "virtual" registers, mapped to address 0-15
		SymbolTable.symbolTable.put("R0", 0);
		SymbolTable.symbolTable.put("R1", 1);
		SymbolTable.symbolTable.put("R2", 2);
		SymbolTable.symbolTable.put("R3", 3);
		SymbolTable.symbolTable.put("R4", 4);
		SymbolTable.symbolTable.put("R5", 5);
		SymbolTable.symbolTable.put("R6", 6);
		SymbolTable.symbolTable.put("R7", 7);
		SymbolTable.symbolTable.put("R8", 8);
		SymbolTable.symbolTable.put("R9", 9);
		SymbolTable.symbolTable.put("R10", 10);
		SymbolTable.symbolTable.put("R11", 11);
		SymbolTable.symbolTable.put("R12", 12);
		SymbolTable.symbolTable.put("R13", 13);
		SymbolTable.symbolTable.put("R14", 14);
		SymbolTable.symbolTable.put("R15", 15);
		SymbolTable.symbolTable.put("SCREEN", 16384);	// Beginning of screen memory
		SymbolTable.symbolTable.put("KBD", 24576);		// Keyboard register
		SymbolTable.symbolTable.put("SP", 0);			// Stack pointer
		SymbolTable.symbolTable.put("LCL", 1);			// Not sure what LCL stands for
		SymbolTable.symbolTable.put("ARG", 2);			// Argument register...?
		SymbolTable.symbolTable.put("THIS", 3);		// This register
		SymbolTable.symbolTable.put("THAT", 4);		// That register
	}
	
	public void buildTable() throws Exception {
		String line = null;
		String symbol = null;	// Holds any symbols after a instructions
		
		// First pass, process the labels
		while (inputFileScanner.hasNextLine()) {
			line = Utils.removeWhiteSpace(inputFileScanner.nextLine());
			lc++; // Line count, for error messages
			String labelSymbol = null;
			if (line.compareTo("") == 0 || line.charAt(0) == '/') {
				// Ignore blank lines and comments
				continue;
			}
			else if (line.charAt(0) == '(') {
				// Beginning of a label
				int endLabel = line.indexOf(')');
				if (endLabel < 0) { // Bad label syntax
					System.err.println("Bad label syntax on line " + lc + ". Expected ) at end of label definition."); 
					throw new Exception();
				} else {
					// Increase the line counter but not the instruction counter
					lc++;
					labelSymbol = line.substring(1, endLabel);
					// Add the symbol with the current instruction counter
					SymbolTable.symbolTable.put(labelSymbol, ic);
				}
			}
			else {
				// If it's not a blank line, a label or a loop label it's a regular instruction
				ic++;
			}
		}
		
		// Reset for second pass
		lc = 0;
		ic = 0;
		inputFileScanner = new Scanner(inputFile); // Get ready to read for second pass
		
		while (inputFileScanner.hasNextLine()) {
			line = Utils.removeWhiteSpace(inputFileScanner.nextLine());
			if (line.compareTo("") == 0 || line.charAt(0) == '/' || line.charAt(0) == '(') {
				// Ignore blank lines, comments, and labels. Increase line count but not instruction count
				lc++; // line counter
				continue;
			}
			else if (line.charAt(0) == '@') {
				// An "a instruction". Basically a variable declaration.
				// increase line and instruction counts
				lc++;
				ic++;
				// Find out if there are is a comment on this line
				int comment = line.indexOf("/");
				// If there is, get the value up to, but not including, the comment
				if (comment > 0) {
					symbol = line.substring(1, comment);
					if (Utils.isInt(symbol)) {
						continue;	// Not a symbol, it's an int constant
					}
				}
				// otherwise, the entire rest of the string is the symbol
				else {
					symbol = line.substring(1);
					if (Utils.isInt(symbol)) {
						continue;	// Not a symbol, it's an int constant
					}
				}
				this.putSymbol(symbol, SymbolTable.userMemory); // Add the symbol if it doesn't already exist
			}
			else {
				// Anything else, we'll assume its a regular instruction
				lc++;
				ic++;
			}
		}
		
	}

	public void printTable() {
		// For error checking
		for (String i : SymbolTable.symbolTable.keySet())
			System.out.println("Key: " + i + " value: " + SymbolTable.symbolTable.get(i));
	}

	public static int get(String key) throws Exception {
		// If the key exists, return it's value, otherwise throw an exception
		if (symbolTable.containsKey(key))
			return symbolTable.get(key);
		else {
			System.err.println("Attempted to access invalid key \"" + key + "\" in symbol table!");
			throw new Exception();
		}
	}
	
}

	

