package hr.fer.zemris.umjint.labos_1;

import hr.fer.zemris.umjint.search_algorithms.AStarAlgoritam;
import hr.fer.zemris.umjint.search_algorithms.PretrazivanjeJednolikomCijenom;
import hr.fer.zemris.umjint.structures.Cvor;
import hr.fer.zemris.umjint.structures.Susjedstvo;
import hr.fer.zemris.umjint.utils.Util;

import java.util.Scanner;
//import hr.fer.zemris.umjint.search_algorithms.PretrazivanjeJednolikomCijenom;

public class Studenti {

	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		in.close();

		String[] studenti = input.split(" ");
		int[] vremena = new int[studenti.length];
		for (int i = 0; i < studenti.length; i++) {
			vremena[i] = Integer.parseInt(studenti[i]);
		}

		Susjedstvo susjedstvo = new Susjedstvo(vremena);
		Cvor pocetniCvor = new Cvor();
		
//		PretrazivanjeJednolikomCijenom pretrazivanje = new PretrazivanjeJednolikomCijenom(
//				susjedstvo, pocetniCvor);
		AStarAlgoritam pretrazivanje = new AStarAlgoritam(susjedstvo, pocetniCvor, Susjedstvo.HEURISTIC_TWO);
//		AStarAlgoritam pretrazivanje = new AStarAlgoritam(susjedstvo, pocetniCvor, Susjedstvo.HEURISTIC_ONE);

		pretrazivanje.pretrazi();
			
		System.out.println(pretrazivanje.getRjesenje() + " "
				+ pretrazivanje.getBrojStanja());
		Util.ispis(pretrazivanje.getCvorRjesenje());
	}

}
