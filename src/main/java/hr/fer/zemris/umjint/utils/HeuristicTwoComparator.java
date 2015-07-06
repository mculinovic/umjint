package hr.fer.zemris.umjint.utils;

import hr.fer.zemris.umjint.structures.Cvor;
import hr.fer.zemris.umjint.structures.Stanje;
import hr.fer.zemris.umjint.structures.Susjedstvo;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HeuristicTwoComparator implements Comparator<Cvor> {

	private int[] vremena = Susjedstvo.getVremena();
	private Map<BitSet, Integer> komparatorHash = new HashMap<BitSet, Integer>();

	@Override
	public int compare(Cvor cv1, Cvor cv2) {
		if (cv1.getVrijeme() + heuristika(cv1.getStanje()) < cv2.getVrijeme() + heuristika(cv2.getStanje())) {
			return -1;
		} else if (cv1.getVrijeme() + heuristika(cv1.getStanje()) > cv2.getVrijeme() + heuristika(cv2.getStanje())) {
			return 1;
		}
		return 0;
	}

	private int heuristika(Stanje stanje) {
		if (komparatorHash.containsKey(stanje.getStudenti())) {
			return komparatorHash.get(stanje.getStudenti());
		}
		BitSet bs = stanje.getStudenti();
		if (bs.isEmpty()) {
			return 0;
		}
		int[] sortVremena = new int[bs.cardinality()];
		for (int i = 0, j = 0; i < vremena.length; i++) {
			if (bs.get(i)) {
				sortVremena[j] = vremena[i];
					j++;
			}
		}
		Arrays.sort(sortVremena);
		int vrijednost = 0;
		for (int i = sortVremena.length - 1; i >= 0; i -= 2 ) {
			if (i > 1) {
				vrijednost += Math.max(sortVremena[i], sortVremena[i - 1]);
			} else {
				vrijednost += sortVremena[i];
			}
			//System.out.println(sortVremena[i]);
		}
//		for (int i = 0; i < sortVremena.length; i += 2 ) {
//			if (i < sortVremena.length - 1) {
//				vrijednost += Math.max(sortVremena[i], sortVremena[i + 1]);
//			} else {
//				vrijednost += sortVremena[i];
//			}
//			System.out.println(sortVremena[i]);
//		}
		komparatorHash.put(bs, vrijednost);
		return vrijednost;
	}
}
