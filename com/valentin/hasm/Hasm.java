package com.valentin.hasm;

public class Hasm {
	public static void main(String [] args) throws Exception {
		// Check command-line arguments
		if (args.length < 1) {
			System.out.println("Usage: java Hasm <inputFile> {outputFile}.");
			System.out.println("If no output file is specified, default output file is ./out.hack");
			System.exit(0);
		} else if (args.length == 1) {
			// Just input file given
			Assembler ass = new Assembler(args[0]);
			ass.assemble();
		} else {
			// input and output file given, ignore any other commands
			Assembler ass = new Assembler(args[0], args[1]);
			ass.assemble();
		}
	}
}
