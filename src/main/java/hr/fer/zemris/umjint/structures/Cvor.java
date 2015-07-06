package hr.fer.zemris.umjint.structures;

public class Cvor implements Comparable<Cvor> {

	private Stanje stanje;
	private int vrijeme;
	private Cvor roditelj;

	public Cvor() {
		this(new Stanje(), 0, null);
	}
	
	public Cvor(Stanje stanje, int vrijeme, Cvor roditelj) {
		this.stanje = stanje;
		this.vrijeme = vrijeme;
		this.setRoditelj(roditelj);
	}

	public Stanje getStanje() {
		return stanje;
	}

	public void setStanje(Stanje stanje) {
		this.stanje = stanje;
	}

	public int getVrijeme() {
		return vrijeme;
	}

	public void setVrijeme(int vrijeme) {
		this.vrijeme = vrijeme;
	}

	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (!(arg0 instanceof Cvor)) {
			return false;
		}
		Cvor drugi = (Cvor) arg0;
		return this.stanje.equals(drugi.stanje)
				&& this.vrijeme == drugi.vrijeme;
	}

	public int hashCode() {
		return this.stanje.hashCode() ^ Integer.valueOf(vrijeme).hashCode();
	}

	@Override
	public int compareTo(Cvor arg0) {
		if (arg0 == null) {
			return 1;
		}
		Cvor drugi = arg0;
		int rez = this.vrijeme - drugi.vrijeme;
		if (rez < 0) {
			return -1;
		} else if (rez > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public Cvor getRoditelj() {
		return roditelj;
	}

	public void setRoditelj(Cvor roditelj) {
		this.roditelj = roditelj;
	}
}
