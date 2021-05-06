package projecto_es;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
//import java.awt.List;
import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.stream.Collectors;
import java.util.List;

public class StaticFunctions {

	public static Boolean saveRule(Rule rule, File file) throws IOException {
		int cont = 0;
		Scanner myReader = new Scanner(file);
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			cont++;
		}
		myReader.close();

		if (rule == null) {
			return false;
		}
		rule.changeID(cont + 1);
		try {
			FileWriter fileWriter = new FileWriter(file, true);
			PrintWriter out = new PrintWriter(fileWriter);
			out.println(rule.toString());
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static List<String> sortAlphabetically(List<String> list_string) {
		Sorter s = new Sorter(list_string);
		List<String> return_list = s.getListSorted();
		return return_list;
	}

//	public static void main(String[] args) {
//		/// EXEMPLO DE USO DA FUN��O saveRule
//		try {
//			String a = "0:Long_method:false:WMC_CLASS:LT:4";
//			String b = "LOC_METHOD:EQ:6";
//			String c = "CYCLO_METHOD:GT:7";
//			String d = a + ":" + "AND" + ":" + b + ":" + "OR" + ":" + c;
//			String f = a + ":" + "OR" + ":" + b + ":" + "OR" + ":" + c;
//			Rule rule = new Rule(d);
//			Rule rule2 = new Rule(f);
//			File myObj = new File("saveRule.txt");
//			Boolean go = saveRule(rule, myObj);
//			// System.out.print(go);
//			saveRule(rule2, myObj);
//			// File myObj = new File("C:\\Users\\catar\\Desktop\\saveRule.txt");
//			Scanner myReader = new Scanner(myObj);
//			// System.out.print(myObj.getPath());
//			while (myReader.hasNextLine()) {
//				String data = myReader.nextLine();
//				System.out.println(data);
//			}
//			myReader.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		String a1 = "manga.ac";
		String a2 = "Ronaldo";
		String a3 = "lol";
		String a4 = "benfica";
		String a5 = "Benfico";
		String a6 = "manga.aB";
		list.add(a1);
		list.add(a2);
		list.add(a3);
		list.add(a4);
		list.add(a5);
		list.add(a6);
		list = sortAlphabetically(list);
		for (String s : list)
			System.out.println(s);
	}

}
