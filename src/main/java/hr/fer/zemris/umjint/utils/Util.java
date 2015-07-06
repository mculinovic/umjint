package hr.fer.zemris.umjint.utils;

import hr.fer.zemris.umjint.structures.Cvor;
import hr.fer.zemris.umjint.structures.Stanje;
import hr.fer.zemris.umjint.structures.Susjedstvo;

import java.util.BitSet;
import java.util.Stack;

public class Util {
	
	private static Stack<Stanje> put;

	public static void ispis(Cvor cvor) {
		put = new Stack<Stanje>();
		izgradiPut(cvor);
		ispisiPut();
	}

	private static void izgradiPut(Cvor cvor) {
		Cvor roditelj = cvor.getRoditelj();
		if (roditelj == null) {
			put.push(cvor.getStanje());
		} else {
			put.push(cvor.getStanje());
			izgradiPut(roditelj);
		}
	}

	private static void ispisiPut() {
		Stack<Stanje> temp = new Stack<Stanje>();
		temp.addAll(put);
		BitSet prethodni = null;
		BitSet trenutni = null;
		int[] vremena = Susjedstvo.getVremena();
		int count = 0;
		while (!temp.isEmpty()) {
			Stanje s = temp.pop();
			prethodni = trenutni;
			trenutni = s.getStudenti();
			if (count > 0) {
				prethodni.xor(trenutni);
				String prijelaz = "";
				for (int i = prethodni.nextSetBit(0); i >= 0; i = prethodni.nextSetBit(i + 1)) {
					prijelaz += (vremena[i]) + " ";
				}
				prijelaz += count % 2 == 0 ? "<-" : "->";
				System.out.println(prijelaz);
			}
			count++;
		}
	}
}
