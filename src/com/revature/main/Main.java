package com.revature.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javafx.util.Pair;

public class Main {

	
	public static void main(String[] args) {
		
		System.out.println("Welcome to PEGA flash cards, a study tool");
		System.out.println("for PEGA glossary terms.\n");
		
		if(args.length == 2) {
			if(args[0].equals("init")) {
				generateGlossary(args[1]);
				return;
			}
			
		} else if(args.length == 1) {
			if(args[0].equals("show")) {
				printGlossary();
				return;
			} else if(args[0].equals("random")) {
				randomEntries();
				return;
			}
		}
		
		System.out.println("Incorrect usage! The following command-line "
				+ "arguments are available:");
		System.out.println("\tinit [filename] - initialize the flash cards\n"
				+ "\t   with a glossary html file.");
		System.out.println("\tshow - after initializing, this option will\n"
				+ "\t   print all terms and definitions to the console.");
		System.out.println("\trandom - displays a term, then displays the\n"
				+ "\t   definition when you press the enter key.");
		System.out.println("\t   Press enter for a new term.");
		System.out.println("Download the glossary html file from pdn.pega.com,\n"
				+ "a PDN account is required to access the file.");
	}

	private static void randomEntries() {
		System.out.println("Reading glossary...");
		ArrayList<Pair<String, String>> glossary = readSerializedGlossary();
		System.out.println("Press q to quit, enter to continue.");
		
		Scanner s = new Scanner(System.in);
		
		Random rand = new Random();
		
		int index = 0;
		
		int glossaryLength = glossary.size();
		
		while(true) {
			String input = s.nextLine();
			if(input.equals("q")) {
				System.out.println("Exiting!");
				break;
			}
			index = rand.nextInt(Integer.MAX_VALUE) % glossaryLength;
			Pair<String, String> entry = glossary.get(index);
			System.out.println("Term: " + entry.getKey());
			System.out.println();
			s.nextLine();
			System.out.println("Definition: " + entry.getValue());
			System.out.println();
		}
		s.close();
	}

	private static void printGlossary() {
		System.out.println("Reading glossary...");
		ArrayList<Pair<String, String>> glossary = readSerializedGlossary();
		System.out.println("Full glossary: ");
		for(Pair<String, String> p : glossary) {
			System.out.println(p);
			System.out.println();
		}
		
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Pair<String, String>> readSerializedGlossary() {
		
		ArrayList<Pair<String, String>> glossary = null;
		
		try(FileInputStream inFile = new FileInputStream(new File("glossaryData.obj"));
				ObjectInputStream inObj = new ObjectInputStream(inFile)) {
			
			glossary = (ArrayList<Pair<String, String>>)inObj.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error reading object file!!!");
		}
		
		return glossary;
	}
	
	/**
	 * Generate a glossary file which contains a serialized list
	 * of pairs of (term, definition)
	 * @param glossaryFile
	 */
	private static void generateGlossary(String glossaryFile) {
		
		Scanner s;
		
		try {
			s = new Scanner(new FileInputStream(new File(glossaryFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		ArrayList<Pair<String, String>> glossary = new ArrayList<>();
		
		//this was supposed to be a variable for debugging but is
		//is needed for normal execution now
		int deleteme = 0;
		
		boolean inTable = false;
		
		while(s.hasNext()) {
			
			String term = s.nextLine();
			
			if(!inTable) {				
				//skip to glossary terms
				try{
					if((term.trim()).substring(0, 6).equals("<table")) {
						inTable = true;
					}
				} catch(Exception e) {
					
				}
				continue;
			}
			
			if(term.contains("<span>")) {
				String key = term.substring(term.indexOf("<span>") + 6, term.indexOf("</span>"));
				String value = "";
				try {
					value = term.substring(term.lastIndexOf("<span>") + 6, term.length());
				} catch (Exception e) {
					//do nothing
				}
				term = term.substring(term.indexOf("</span>") + 6);
				//int i = 0;
				System.out.println(deleteme + " key: " + key);
				while(!term.contains("</span>") || !term.contains("</td>")) {
					System.out.print(".");
					
					try {
						term = s.nextLine();
						value += term;
					} catch (Exception e) {
						//do nothing
					}
					//i++;
				}
				System.out.println();
				System.out.println(deleteme + " key: " + key + " value: " + value);
				glossary.add(new Pair<String, String>(key, value.substring(0, value.length()-18)));
			}
			deleteme++;
		}
		
		System.out.println(glossary.get(1));
		
		s.close();
		
		//create file if non existent
		FileOutputStream fs = null;
		
		try {
			File f = new File("glossaryData.obj");
			
			if(!f.exists()) {
				f.createNewFile();
			}
			
			fs = new FileOutputStream(f);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't create new file!!!");
			return;
		}
		
		//write glossary data to file
		try {
			fs = new FileOutputStream(new File("glossaryData.obj"));
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(glossary);
			fs.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Exception writing file!!!");
		}
	}
}
