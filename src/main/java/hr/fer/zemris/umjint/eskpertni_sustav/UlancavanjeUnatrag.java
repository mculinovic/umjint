package hr.fer.zemris.umjint.eskpertni_sustav;

import hr.fer.zemris.umjint.eskpertni_sustav.Pravilo.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class UlancavanjeUnatrag {

	private static Map<String, Set<String>> cinjenice = new HashMap<>();

	private static Set<Pravilo> bazaPravila = new LinkedHashSet<>();
	private static Map<String, String> radnaMemorija = new HashMap<>();

	private static Stack<Stanje> stog = new Stack<>();

	private static String cilj;

	private final static int DEFAULT_PRIORITET = Integer.MIN_VALUE;

	private static Scanner in = new Scanner(System.in);

	public static void main(String[] args) {

		parseFromFile("zivotinje.txt");

		ispisiCinjenice();
		System.out.println();
		ispisiPravila(bazaPravila);
		System.out.println();

		System.out.println("Unesite željeni cilj");
		System.out.print("> ");
		cilj = in.nextLine();

		Stanje pocetno = new Stanje(cilj, pronadiPravila(cilj));
		stog.push(pocetno);
		procesZakljucivanja();
		in.close();
	}

	private static void procesZakljucivanja() {

		int brojacKoraka = 0;
		boolean nepoznat = true;
		while (!stog.isEmpty()) {

			TreeSet<Pravilo> pravila = stog.peek().getPravila();
			
			brojacKoraka++;
			System.out.println();
			System.out.println("KORAK " + brojacKoraka);
			System.out.println("---------------------");
			ispisiPravila(pravila);
			System.out.println("---------------------");
			ispisiMemoriju();

			if (pravila.isEmpty()) {
				stog.pop();
				if (stog.isEmpty()) {
					break;
				}
				pravila = stog.peek().getPravila();
				pravila.remove(pravila.first());
				continue;
			}
			
			
			Pravilo pravilo = pravila.first();
			
			boolean zadovoljivost = true;
			
			for (Map.Entry<String, Set<String>> premisa : pravilo.getUvjet()
					.entrySet()) {
				String parametar = premisa.getKey();
				String value = radnaMemorija.get(parametar);

				// parametarska vrijednost nije u radnoj memoriji
				if (value == null) {
					Set<Pravilo> izvodeVrijednost = pronadiPravila(parametar);
					if (!izvodeVrijednost.isEmpty()) {
						Stanje novo = new Stanje(parametar, izvodeVrijednost);
						stog.push(novo);
						zadovoljivost = false;
						break;
					} else {
						System.out.println("Unesite vrijednost parametra : "
								+ parametar);
						System.out.print("Moguce vrijednosti su:");
						for (String s : cinjenice.get(parametar)) {
							System.out.print(" " + s);
						}
						System.out.println();
						String vrijednost = null;
						do {
							System.out.print("> ");
							vrijednost = in.nextLine();
						} while (!cinjenice.get(parametar).contains(vrijednost));
						radnaMemorija.put(parametar, vrijednost);
						value = vrijednost;
					}
				}
				
				if (!premisa.getValue().contains(value)) {
					// vrijednost nađena u memoriji ne podudara se sa
					// vrijednošću
					zadovoljivost = false;
					pravila.remove(pravilo);
					break;
				}
								
			}

			if (zadovoljivost) {
				if (stog.peek().getCilj().equals(cilj)) {
					nepoznat = false;
				}
				System.out.println("---------------------");
				System.out.println("PALI :" + pravilo);
				for (Map.Entry<String, Set<String>> zakljucak : pravilo
						.getAkcija().entrySet()) {
					String parametar = zakljucak.getKey();
					for (String value : zakljucak.getValue()) {
						radnaMemorija.put(parametar, value);
					}
				}
				stog.pop();
			}

		}

		System.out.println();
		System.out.println("KRAJ");
		System.out.println("---------------------");
		ispisiMemoriju();
		if (nepoznat) {
			System.out.println(cilj + " = nepoznat");
		}
	}

	private static void ispisiMemoriju() {
		for (Map.Entry<String, String> var : radnaMemorija.entrySet()) {
			System.out.println(var.getKey() + " = " + var.getValue());
		}
	}

	private static Set<Pravilo> pronadiPravila(String cilj) {
		Set<Pravilo> ciljnaPravila = new HashSet<>();
		for (Pravilo pravilo : bazaPravila) {
			if (pravilo.getAkcija().containsKey(cilj)) {
				ciljnaPravila.add(pravilo);
			}
		}
		return ciljnaPravila;
	}

	private static void ispisiPravila(Set<Pravilo> pravila) {

		int brojac = 1;
		for (Pravilo pravilo : pravila) {
			System.out.println("[" + (brojac++) + "]" + pravilo);
		}

	}

	private static void ispisiCinjenice() {
		for (Entry<String, Set<String>> e : cinjenice.entrySet()) {
			Set<String> values = e.getValue();
			System.out.print(e.getKey() + ": ");
			int size = values.size();
			int i = 0;
			for (String s : values) {
				if (i != size - 1) {
					System.out.print(s + "|");
				} else {
					System.out.print(s);
				}
				i++;
			}
			System.out.println();
		}
	}

	private static void parseFromFile(String datoteka) {

		int brojacPravila = 0;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new BufferedInputStream(new FileInputStream(datoteka)),
					"UTF-8"));

			Pravilo pravilo = null;
			boolean stvoreno = false;
			int prioritet = Integer.MIN_VALUE;
			while (true) {
				String redak = br.readLine();
				if (redak == null) {
					break;
				}
				if (redak.isEmpty()) {
					continue;
				}

				redak = redak.trim();
				if (redak.startsWith("RULE")) {
					brojacPravila++;
					String[] podaci = redak.split("\\s+");
					pravilo = new Pravilo();
					pravilo.setIme(podaci[1]);
					stvoreno = true;
				} else if (redak.startsWith("SALIENCE")) {
					if (!stvoreno) {
						brojacPravila++;
						pravilo = new Pravilo();
						pravilo.setIme("Pravilo " + brojacPravila);
					}

					String[] podaci = redak.split("\\s+");
					prioritet = Integer.valueOf(podaci[1]);

				} else if (redak.startsWith("IF")) {
					if (!stvoreno) {
						brojacPravila++;
						pravilo = new Pravilo();
						prioritet = DEFAULT_PRIORITET;
						pravilo.setIme("Pravilo " + brojacPravila);
					}
					pravilo.setPrioritet(new Pair(prioritet, brojacPravila));

					String[] podaci = redak.split("\\s+", 2);
					obradiParametre(podaci, pravilo, false);

					stvoreno = true;

				} else if (redak.startsWith("THEN")) {
					if (stvoreno) {
						String[] podaci = redak.split("\\s+", 2);
						obradiParametre(podaci, pravilo, true);
						bazaPravila.add(pravilo);
					}
					stvoreno = false;
				} else if (redak.contains("=")) {

					String[] cinjenica = redak.split("=", 2);
					String varijabla = cinjenica[0].trim();
					if (varijabla.endsWith("*")) {
						varijabla = varijabla.substring(0,
								varijabla.length() - 1);
					}
					String[] vrijednosti = cinjenica[1].trim().split("\\|");
					if (!cinjenice.containsKey(varijabla)) {
						Set<String> set = new HashSet<>();
						cinjenice.put(varijabla, set);
					}
					Set<String> set = cinjenice.get(varijabla);
					for (String vrijednost : vrijednosti) {
						set.add(vrijednost.trim());
					}

				}
			}

			br.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading file");
			e.printStackTrace();
		}
	}

	private static void obradiParametre(String[] podaci, Pravilo pravilo,
			boolean akcija) {
		String lista = podaci[1].trim();
		podaci = lista.split("&");
		for (String podatak : podaci) {
			podatak = podatak.trim();
			String[] pridruzivanje = podatak.split("=", 2);
			String parametar = pridruzivanje[0].trim();
			String vrijednost = pridruzivanje[1].trim();
			if (vrijednost.contains("|")) {
				String[] vrijednosti = vrijednost.split("\\|");
				for (String value : vrijednosti) {
					if (akcija) {
						pravilo.dodajAkciju(parametar, value.trim());
					} else {
						pravilo.dodajUvjet(parametar, value.trim());
					}
				}
			} else {
				if (akcija) {
					pravilo.dodajAkciju(parametar, vrijednost);
				} else {
					pravilo.dodajUvjet(parametar, vrijednost);
				}
			}
		}
	}

}
