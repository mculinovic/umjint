package hr.fer.zemris.umjint.search_algorithms;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import hr.fer.zemris.umjint.structures.Stanje;
import hr.fer.zemris.umjint.structures.Cvor;
import hr.fer.zemris.umjint.structures.Susjedstvo;

public class PretrazivanjeJednolikomCijenom {

	private PriorityQueue<Cvor> otvoreni;
	private Set<Stanje> posjeceno;
	private Cvor rjesenje;
	private Susjedstvo susjedstvo;


	public PretrazivanjeJednolikomCijenom(Susjedstvo susjedstvo,
			Cvor pocetniCvor) {
		otvoreni = new PriorityQueue<Cvor>();
		posjeceno = new HashSet<Stanje>();
		otvoreni.add(pocetniCvor);
		this.susjedstvo = susjedstvo;
	}

	public void pretrazi() {
		while (!otvoreni.isEmpty()) {
			Cvor cvor = otvoreni.remove();
			if (posjeceno.contains(cvor.getStanje())) {
				continue;
			}
			if (cvor.getStanje().getStudenti().isEmpty()) {
				rjesenje = cvor;
				break;
			}
			posjeceno.add(cvor.getStanje());
			List<Cvor> susjedi = susjedstvo.generirajSusjedstvo(cvor);
			for (Cvor susjed : susjedi) {
				if (!posjeceno.contains(susjed.getStanje())) {
						otvoreni.add(susjed);
				}
			}
		}
	}
	
	public Cvor getCvorRjesenje() {
		return rjesenje;
	}

	public int getRjesenje() {
		return rjesenje.getVrijeme();
	}

	public int getBrojStanja() {
		return posjeceno.size();
	}

	

}
