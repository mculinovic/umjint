package hr.fer.zemris.umjint.eskpertni_sustav;


import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Pravilo implements Comparable<Pravilo> {

	private String ime;

	private Pair prioritet;

	private Map<String, Set<String>> uvjet;

	private Map<String, Set<String>> akcija;

	public Pravilo() {
		uvjet = new LinkedHashMap<>();
		akcija = new LinkedHashMap<>();
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public void setPrioritet(Pair prioritet) {
		this.prioritet = prioritet;
	}

	public void dodajUvjet(String parametar, String vrijednost) {
		if (!uvjet.containsKey(parametar)) {
			Set<String> vrijednosti = new LinkedHashSet<>();
			uvjet.put(parametar, vrijednosti);
		}
		Set<String> vrijednosti = uvjet.get(parametar);
		vrijednosti.add(vrijednost);
	}

	public Map<String, Set<String>> getUvjet() {
		return uvjet;
	}

	public Map<String, Set<String>> getAkcija() {
		return akcija;
	}

	public String getIme() {
		return ime;
	}

	public Pair getPrioritet() {
		return prioritet;
	}

	public void dodajAkciju(String parametar, String vrijednost) {
		if (!akcija.containsKey(parametar)) {
			Set<String> vrijednosti = new LinkedHashSet<>();
			akcija.put(parametar, vrijednosti);
		}
		Set<String> vrijednosti = akcija.get(parametar);
		vrijednosti.add(vrijednost);

	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Pravilo)) {
			return false;
		}

		Pravilo drugi = (Pravilo) obj;
		return ime == drugi.ime
				&& prioritet.prioritet == drugi.prioritet.prioritet
				&& prioritet.brojac == drugi.prioritet.brojac
				&& uvjet.equals(drugi.uvjet) && akcija.equals(drugi.akcija);
	}

	@Override
	public int hashCode() {
		return ime.hashCode() ^ uvjet.hashCode() ^ akcija.hashCode()
				^ prioritet.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IF ");
		int size = getUvjet().size();
		int i = 0;
		for (Entry<String, Set<String>> e : getUvjet().entrySet()) {
			Set<String> values = e.getValue();
			sb.append(e.getKey() + " = ");
			int sizeV = values.size();
			int j = 0;
			for (String s : values) {
				if (j != sizeV - 1) {
					sb.append(s + "|");
				} else {
					sb.append(s);
				}
				j++;
			}
			if (i != size - 1) {
				sb.append(" & ");
			} else {
				sb.append(" ");
			}
			i++;
		}
		
		size = getAkcija().size();
		i = 0;
		sb.append("THEN ");
		for (Entry<String, Set<String>> e : getAkcija().entrySet()) {
			Set<String> values = e.getValue();
			sb.append(e.getKey() + " = ");
			int sizeV = values.size();
			int j = 0;
			for (String s : values) {
				if (j != sizeV - 1) {
					sb.append(s + "|");
				} else {
					sb.append(s);
				}
				j++;
			}
			if (i != size - 1) {
				sb.append(" & ");
			} else {
				sb.append(" ");
			}
			i++;
		}
		return sb.toString();
	}
	
	@Override
	public int compareTo(Pravilo arg0) {
		if (arg0 == null) {
			return 1;
		}
		Pravilo drugi = arg0;
		if (this.prioritet.prioritet == arg0.prioritet.prioritet) {
			return compareBrojac(this.prioritet.brojac, drugi.prioritet.brojac);
		}
		else if (this.prioritet.prioritet < arg0.prioritet.prioritet) {
			return 1;
		}
		else {
			return -1;
		}
	}
	
	private int compareBrojac(int brojac1, int brojac2) {
		if (brojac1 < brojac2) {
			return -1;
		} else if (brojac1 == brojac2) {
			return 0;
		} else {
			return 1;
		}
	}

	static class Pair {

		int prioritet;
		int brojac;

		public Pair(int prioritet, int brojac) {
			this.prioritet = prioritet;
			this.brojac = brojac;
		}

		@Override
		public int hashCode() {
			return Integer.valueOf(prioritet).hashCode()
					^ Integer.valueOf(brojac).hashCode();
		}
	}


}
