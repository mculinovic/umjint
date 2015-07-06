package hr.fer.zemris.umjint.structures;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class Susjedstvo {

	private static int[] vremena;

	public static int brojStudenata;
	
	public final static int UNIFORM_COST = 0;
	public final static int HEURISTIC_ONE = 1;
	public final static int HEURISTIC_TWO = 2;

	public Susjedstvo(int[] vremena) {
		Susjedstvo.setVremena(vremena);
		brojStudenata = vremena.length;
	}

//	public List<Cvor> generirajSusjedstvo(Cvor cvor) {
//		List<Cvor> susjedi = new LinkedList<Cvor>();
//
//		if (cvor.getStanje().isPocetak()) {
//			BitSet bs = cvor.getStanje().getStudenti();
//			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
//				for (int j = bs.nextSetBit(i + 1); j >= 0; j = bs
//						.nextSetBit(j + 1)) {
//					BitSet studenti = (BitSet) bs.clone();
//					studenti.flip(i);
//					studenti.flip(j);
//					int dodatnoVrijeme = cvor.getVrijeme()
//							+ Math.max(getVremena()[i], getVremena()[j]);
//					Stanje stanje = new Stanje(Stanje.KRAJ, studenti);
//					susjedi.add(new Cvor(stanje, dodatnoVrijeme, cvor));
//				}
//			}
//		} else {
//			BitSet bs = cvor.getStanje().getStudenti();
//			for (int i = bs.nextClearBit(0); i >= 0
//					&& i < Susjedstvo.brojStudenata; i = bs.nextClearBit(i + 1)) {
//				BitSet studenti = (BitSet) bs.clone();
//				studenti.flip(i);
//				int dodatnoVrijeme = cvor.getVrijeme() + getVremena()[i];
//				Stanje stanje = new Stanje(Stanje.POCETAK, studenti);
//				susjedi.add(new Cvor(stanje, dodatnoVrijeme, cvor));
//			}
//		}
//
//		return susjedi;
//	}
	
	public List<Cvor> generirajSusjedstvo(Cvor cvor) {
		List<Cvor> susjedi = new LinkedList<Cvor>();

		if (cvor.getStanje().isPocetak()) {
			BitSet bs = cvor.getStanje().getStudenti();
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
				BitSet studenti1 = (BitSet) bs.clone();
				studenti1.flip(i);
				int dodatnoVrijeme1 = cvor.getVrijeme() + vremena[i];
				Stanje stanje1 = new Stanje(Stanje.KRAJ, studenti1);
				susjedi.add(new Cvor(stanje1, dodatnoVrijeme1, cvor));
				for (int j = bs.nextSetBit(i + 1); j >= 0; j = bs
						.nextSetBit(j + 1)) {
					BitSet studenti = (BitSet) bs.clone();
					studenti.flip(i);
					studenti.flip(j);
					int dodatnoVrijeme = cvor.getVrijeme()
							+ Math.max(getVremena()[i], getVremena()[j]);
					Stanje stanje = new Stanje(Stanje.KRAJ, studenti);
					susjedi.add(new Cvor(stanje, dodatnoVrijeme, cvor));
				}
			}
		} else {
			BitSet bs = cvor.getStanje().getStudenti();
			for (int i = bs.nextClearBit(0); i >= 0
					&& i < Susjedstvo.brojStudenata; i = bs.nextClearBit(i + 1)) {
				BitSet studenti = (BitSet) bs.clone();
				studenti.flip(i);
				int dodatnoVrijeme = cvor.getVrijeme() + getVremena()[i];
				Stanje stanje = new Stanje(Stanje.POCETAK, studenti);
				susjedi.add(new Cvor(stanje, dodatnoVrijeme, cvor));
				for (int j = bs.nextClearBit(i + 1); j >= 0 && j < Susjedstvo.brojStudenata; j = bs
						.nextClearBit(j + 1)) {
					BitSet studenti1 = (BitSet) bs.clone();
					studenti1.flip(i);
					studenti1.flip(j);
					int dodatnoVrijeme1 = cvor.getVrijeme()
							+ Math.max(getVremena()[i], getVremena()[j]);
					Stanje stanje1 = new Stanje(Stanje.KRAJ, studenti1);
					susjedi.add(new Cvor(stanje1, dodatnoVrijeme1, cvor));
				}
			}
		}

		return susjedi;
	}
	
	public static int[] getVremena() {
		return vremena;
	}

	public static void setVremena(int[] vremena) {
		Susjedstvo.vremena = vremena;
	}
}
