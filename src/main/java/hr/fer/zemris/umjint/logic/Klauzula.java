package hr.fer.zemris.umjint.logic;

import java.util.Set;
import java.util.TreeSet;

public class Klauzula {
	
	

	Set<String> literali;
	
	public Klauzula() {
		literali = new TreeSet<>();
	}
	
	public void dodajLiteral(String literal) {
		literali.add(literal);
	}

	public Set<String> getLiterali() {
		return literali;
	}
	
	public void setLiterali(Set<String> literali) {
		this.literali = literali;
	}
	
	@Override
	public int hashCode() {
		return literali.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Klauzula)) {
			return false;
		}
		Klauzula drugi = (Klauzula) obj;
//		System.out.println(literali);
//		System.out.println(drugi.literali);
//		System.out.println(literali.equals(drugi.literali));
		return literali.equals(drugi.literali);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String l: literali) {
			if (i == literali.size() - 1) {
				sb.append(l);
			} else {
				sb.append(l + " V ");
			}
			i++;
		}
		return sb.toString();
	}

	public boolean jeTautologija() {
		for (String l: literali) {
			if (l.startsWith("~") && literali.contains(l.substring(1))) {
				return true;
			} else if (literali.contains("~" + l)) {
				return true;
			}
		}
		return false;
	}

}
