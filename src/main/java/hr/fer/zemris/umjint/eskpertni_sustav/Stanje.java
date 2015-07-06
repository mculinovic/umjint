package hr.fer.zemris.umjint.eskpertni_sustav;

import java.util.Set;
import java.util.TreeSet;

public class Stanje {
	
	private String cilj;
	
	private TreeSet<Pravilo> pravila;

	public Stanje(String cilj, Set<Pravilo> pravila) {
		super();
		this.cilj = cilj;
		this.pravila = new TreeSet<>(pravila);
	}
	
	public String getCilj() {
		return cilj;
	}

	public TreeSet<Pravilo> getPravila() {
		return pravila;
	}
}
