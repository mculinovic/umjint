package hr.fer.zemris.umjint.search_algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import hr.fer.zemris.umjint.structures.Stanje;
import hr.fer.zemris.umjint.structures.Cvor;
import hr.fer.zemris.umjint.structures.Susjedstvo;
import hr.fer.zemris.umjint.utils.HeuristicOneComparator;
import hr.fer.zemris.umjint.utils.HeuristicTwoComparator;

public class AStarAlgoritam {

	private PriorityQueue<Cvor> otvoreni;
	private Map<Stanje, Cvor> otvoreniMapa;
	private Map<Stanje, Cvor> zatvoreni;
	private Cvor rjesenje;
	private Susjedstvo susjedstvo;
	private int heuristika;

	public AStarAlgoritam(Susjedstvo susjedstvo, Cvor pocetniCvor,
			int heuristika) {
		this.heuristika = heuristika;
		if (this.heuristika == Susjedstvo.HEURISTIC_ONE) {
			HeuristicOneComparator comp = new HeuristicOneComparator();
			otvoreni = new PriorityQueue<Cvor>(10, comp);
		} else {
			HeuristicTwoComparator comp = new HeuristicTwoComparator();
			otvoreni = new PriorityQueue<Cvor>(10, comp);
		}
		otvoreniMapa = new HashMap<Stanje, Cvor>();
		zatvoreni = new HashMap<Stanje, Cvor>();
		otvoreni.add(pocetniCvor);
		this.susjedstvo = susjedstvo;
		
	}

	public void pretrazi() {
		while (!otvoreni.isEmpty()) {
			Cvor cvor = otvoreni.remove();
			if (zatvoreni.containsKey(cvor.getStanje())) {
				continue;
			}
			if (cvor.getStanje().getStudenti().isEmpty()) {
				rjesenje = cvor;
				break;
			}
			zatvoreni.put(cvor.getStanje(), cvor);
			List<Cvor> susjedi = susjedstvo.generirajSusjedstvo(cvor);
			for (Cvor susjed : susjedi) {
				if (zatvoreni.containsKey(susjed.getStanje())) {
					if (zatvoreni.get(susjed.getStanje()).getVrijeme() < susjed.getVrijeme()) {
						continue;
					} else {
						zatvoreni.remove(susjed.getStanje());
					}
				}
				if (otvoreniMapa.containsKey(susjed.getStanje())) {
					if (otvoreniMapa.get(susjed.getStanje()).getVrijeme() < susjed.getVrijeme()) {
						continue;
					} else {
						otvoreniMapa.remove(susjed.getStanje());
					}
				}
				otvoreni.add(susjed);
				otvoreniMapa.put(susjed.getStanje(), susjed);
			}
		}
	}


	public int getRjesenje() {
		return rjesenje.getVrijeme();
	}

	public int getBrojStanja() {
		return zatvoreni.size();
	}

	public Cvor getCvorRjesenje() {
		return rjesenje;
	}
}
