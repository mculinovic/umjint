package hr.fer.zemris.umjint.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class DokazivacTeorema {
	
	
	private static List<String> koraci = new ArrayList<>();
	private static int maxKlauzula;
	private static int pocetanBrojKlauzula;
	private static int MAX_KORAKA;
	private static int MAX_KLAUZULA;
	private static int brojKoraka;
	
	public static void main(String[] args) {
		
		if (args.length == 2) {
			MAX_KORAKA = Integer.parseInt(args[0]);
			MAX_KLAUZULA = Integer.parseInt(args[1]);
		}
		
		Scanner in = new Scanner(System.in);

		StringBuilder premisa = new StringBuilder();
		int brPremisa = 0;
		int strategija = 0;
		StringBuilder ciljnaFormula = new StringBuilder();
		String prethodniRedak = "";
		String redak = "";
		while(in.hasNextLine()) {
			prethodniRedak = redak;
			redak = in.nextLine();
			try {
				strategija = Integer.parseInt(redak);
			} catch(NumberFormatException e){
				if (prethodniRedak.isEmpty()) {
					continue;
				}
 				if (brPremisa > 0) {
					premisa.append("&(").append(prethodniRedak).append(")");
				} else {
					premisa.append("(").append(prethodniRedak).append(")");
				}
				brPremisa++;
				continue;
			}
			ciljnaFormula.append("~(").append(prethodniRedak).append(")");
			break;
		}
		in.close();
		
		if (rezolucijaOpovrgavanjem(premisa.toString(), ciljnaFormula.toString(), strategija)) {
			System.out.println();
			System.out.println("Dokazano");
			System.out.println("Broj koraka: " + brojKoraka);
			System.out.println("Najveci broj klauzula u memoriji: " + maxKlauzula);
			int i = 1;
			for (String korak : koraci) {
				if (korak.startsWith("-")) {
					System.out.println(korak);
				} else {
					System.out.println(i + ". " + korak);
					i++;
				}
			}	
		} else {
			System.out.println("Nije dokazano!");
		}
	}
	
	

	private static boolean rezolucijaOpovrgavanjem(String premisa, String ciljna, int strategija) {	
		CNFConverter konverterSkup = new CNFConverter();
		Set<Klauzula> skupPotpore = konverterSkup.cnfConvert(ciljna);
		CNFConverter konverterTeorem = new CNFConverter();
		Set<Klauzula> klauzule = konverterTeorem.cnfConvert("(" + premisa + ")&(" + ciljna + ")");
		Set<Klauzula> nove = new LinkedHashSet<>();
		Set<Set<Klauzula>> zatvoreniParovi = new HashSet<>();
		Set<Klauzula> izvedeneRezolvente = new HashSet<>();
		
		
		if (strategija == 1) {
			klauzule = izbaciTautologije(klauzule);
			skupPotpore = izbaciTautologije(skupPotpore);
		}
		
		pocetanBrojKlauzula = klauzule.size();
		int klauzulaPremisa = pocetanBrojKlauzula - skupPotpore.size();
		
		int brojac = 0;
		for (Klauzula k : klauzule) {
			koraci.add(k.toString());
			brojac++;
			if (brojac == klauzulaPremisa) {
				koraci.add("-----------");
			}	
		}
		koraci.add("-----------");
		System.out.println();
		
		if (strategija == 1) {
			klauzule = izbaciNadskupove(klauzule);
			skupPotpore = izbaciNadskupove(skupPotpore);
		}
		
		while (true) {
			for (Klauzula k1 : skupPotpore) {
				for (Klauzula k2: klauzule) {
					if (k1.equals(k2)) {
						continue;
					}
					Set<Klauzula> par = new HashSet<>();
					par.add(k1);
					par.add(k2);
					if (zatvoreniParovi.contains(par)) {
						continue;
					}
					Set<Klauzula> rezolvente = razrijesi(k1, k2);
					
					zatvoreniParovi.add(par);
					
					if (rezolvente != null) {
						if (strategija == 1) {
							rezolvente = izbaciTautologije(rezolvente);
							rezolvente = izbaciNadskupove(rezolvente);
						}
						for(Klauzula k : rezolvente) {
							if (!izvedeneRezolvente.contains(k)) {
								koraci.add(k.toString() + " (Klauzula -->" + k1.toString() + " Klauzula -->" + k2.toString() + ")");;
								if (MAX_KORAKA > 0 && brojKoraka > MAX_KORAKA) {
									System.out.println("Predugo vrijeme izvođenja");
									System.exit(1);
								}
								izvedeneRezolvente.add(k);
							}
						}
					}
					if (rezolvente == null) {
						koraci.add("NIL (Klauzula -->" + k1.toString() + " Klauzula -->" + k2.toString() + ")");
						return true;
					}
					nove.addAll(rezolvente);
				}
			}
			if (klauzule.containsAll(nove)) {
				return false;
			}
			klauzule.addAll(nove);
			skupPotpore.addAll(nove);

			if (strategija == 1) {
				skupPotpore = izbaciTautologije(skupPotpore);
				klauzule = izbaciTautologije(klauzule);
				skupPotpore = izbaciNadskupove(skupPotpore);
				klauzule = izbaciNadskupove(klauzule);
			}
			if (klauzule.size() > maxKlauzula) {
				maxKlauzula = klauzule.size();
				if (MAX_KLAUZULA > 0 && maxKlauzula > MAX_KLAUZULA) {
					System.out.println("Preveliko memorijsko opterećenje!");
					System.exit(1);
				}
			}
		}
	}

	private static Set<Klauzula> izbaciNadskupove(Set<Klauzula> klauzule) {
		Set<Klauzula> privremeni1 = new LinkedHashSet<>(klauzule);
		Set<Klauzula> privremeni2 = new LinkedHashSet<>(klauzule);
		
		for (Klauzula k1: privremeni1) {
			Set<String> literali1 = k1.getLiterali();
			for (Klauzula k2: privremeni2) {
				if (k1.equals(k2)) {
					continue;
				}
				Set<String> literali2 = k2.getLiterali();
				if (literali2.containsAll(literali1)) {
					klauzule.remove(k2);
				}
			}
		}
		return klauzule;
	}



	private static Set<Klauzula> izbaciTautologije(Set<Klauzula> klauzule) {
		Set<Klauzula> privremeni = new LinkedHashSet<>(klauzule);
		for (Klauzula k : klauzule) {
			if (k.jeTautologija()) {
				privremeni.remove(k);
			}
		}
		return privremeni;
	}



	private static Set<Klauzula> razrijesi(Klauzula k1, Klauzula k2) {
		Set<Klauzula> rezolvente = new LinkedHashSet<>();
		Set<String> literali1 = k1.getLiterali();
		Set<String> literali2 = k2.getLiterali();

		for (String l: literali1) {
			if ((l.startsWith("~") && literali2.contains(l.substring(1))) || literali2.contains("~" + l)) {
				brojKoraka++;
				Klauzula k = new Klauzula();
				String negacija;
				if (l.startsWith("~")) {
					negacija = l.substring(1);
				} else {
					negacija = "~" + l;
				}
				Set<String>noviSet = kopiraj(literali1, l);
				noviSet.addAll(kopiraj(literali2, negacija));
				k.setLiterali(noviSet);

				rezolvente.add(k);
				if (noviSet.isEmpty()) {
					return null;
				}
				
			}
		}
		return rezolvente;
	}



	private static Set<String> kopiraj(Set<String> set, String l) {
		Set<String> literali = new TreeSet<>();
		for (String s : set) {
			if (!s.equals(l)) {
				literali.add(s);
			}
		}
		return literali;
	}

}
