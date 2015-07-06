package hr.fer.zemris.umjint.structures;

import java.util.BitSet;

public class Stanje {

	public static final boolean POCETAK = true;
	public static final boolean KRAJ = false;

	private boolean pocetak;
	private BitSet studenti;

	public Stanje() {
		studenti = new BitSet(Susjedstvo.brojStudenata);
		studenti.set(0, Susjedstvo.brojStudenata);
		pocetak = POCETAK;

	}

	public Stanje(boolean pozicija, BitSet studenti) {
		this.pocetak = pozicija;
		this.studenti = studenti;
	}

	public boolean isPocetak() {
		return pocetak;
	}

	public void setPocetak(boolean pocetak) {
		this.pocetak = pocetak;
	}
	
	public BitSet getStudenti() {
		return studenti;
	}

	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (!(arg0 instanceof Stanje)) {
			return false;
		}
		Stanje drugi = (Stanje) arg0;
		return this.pocetak == drugi.pocetak
				&& this.studenti.equals(drugi.studenti);
	}

	public int hashCode() {
		return Boolean.valueOf(pocetak).hashCode() ^ studenti.hashCode();
	}
}
