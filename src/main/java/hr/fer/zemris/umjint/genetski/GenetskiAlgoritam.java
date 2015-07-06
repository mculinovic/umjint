package hr.fer.zemris.umjint.genetski;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

/**
 * Početna populacija je X neuronskih mreža, pri
 * čemu se X zadaje prilikom pokretanja.
 * Elitizam osigurava da se ne gubi trenutno najbolje rješenje,
 * najbolju jedinku prenosimo u sljedeću generaciju, a ostalih
 * x - 1 dobiva se križanjem između jedinki te generacije.
 * Nakon toga svaka neuronska mreža se mutira te tako nastaje
 * nova generacija.
 * 
 * Proporcionalna selekcija - ako jedinka ima veću dobrotu,
 * nju vjerojatnije izabiremo.
 * dobrota(X) = MAX(pogreska) - pogreska(x)
 * 
 * Potrebno je ispisivati broj generacije te iznos ukupne pogreške
 * Broj kromosoma te uvjete zaustavljanja postupka učenja(iznos ukupne pogreške
 * i broj iteracija) predaje korisnik kao argumente komandne linije.
 * 
 * Nakon što je postupak učenja završen, program treba omogućiti
 * korisniku da unosi nove uzorke te za svaki uzorak ispisati izlaz
 * prethodno naučene mreže.
 * Također, treba biti omogućeno učitavanje datoteke sa ispitnim primjerima
 * te zatim ispisati ukupnu pogrešku neuronske mreže na tim primjerima
 * (srednje kvadratno odstupanje)
 * @author mculinovic
 *
 */
public class GenetskiAlgoritam {
	
	private int brojKromosoma;
	private double ukupnaPogreska;
	private int brojIteracija;
	private Map<Double, Double> skupZaUcenje;
	private static final double K = 0.1;
	
	private NeuronskaMreza najboljaJedinka;
	
	private Random rand;
	
	public GenetskiAlgoritam(int brojKromosoma, double ukupnaPogreska, int brojIteracija) {
		super();
		this.brojKromosoma = brojKromosoma;
		this.ukupnaPogreska = ukupnaPogreska;
		this.brojIteracija = brojIteracija;
		rand = new Random(System.currentTimeMillis());
	}
	
	public void treniraj() {
		
		System.out.println("---------------------------");
		System.out.println("Učenje mreže započelo");
		System.out.println("---------------------------");
		
		skupZaUcenje = ucitajSkup("train.txt");
		NeuronskaMreza[] populacija = stvoriPocetnuPopulaciju();
		evaluiraj(populacija);
		int generacija = 1;
		System.out.println(generacija + " : " + najboljaJedinka.greska);
		
		while (najboljaJedinka.greska > ukupnaPogreska && generacija < brojIteracija - 1) {
			NeuronskaMreza[] novaPopulacija = new NeuronskaMreza[brojKromosoma];
			urediVjerojatnosti(populacija);
			
			int neurona = 0;
			novaPopulacija[neurona] = najboljaJedinka;
			neurona++;
			while (neurona < brojKromosoma) {
				
				int index = selektiraj(populacija);
				NeuronskaMreza nm1 = populacija[index];
				index = selektiraj(populacija);
				NeuronskaMreza nm2 = populacija[index];
				NeuronskaMreza nm = nm1.krizaj(nm2, rand);
				mutiraj(nm);
				novaPopulacija[neurona] = nm;
				neurona++;
			}
			populacija = novaPopulacija;
			//System.arraycopy(novaPopulacija, 0, populacija, 0, novaPopulacija.length);
			evaluiraj(populacija);
			//System.out.println(populacija.length);
			generacija++;
			System.out.println(generacija + " : " + najboljaJedinka.greska);
		}
		
		System.out.println("---------------------------");
		System.out.println("Učenje mreže završeno");
		
	}
	
	public void testiraj() {
		
		System.out.println("---------------------------");
		System.out.println("Unesite uzorak u formatu: {x f(x)}, ili unesite naziv datoteke: FILE naziv_datoteke");
		Scanner in = new Scanner(System.in);
		
		double greska = 0.0;
		int brojUzoraka = 0;
		int velicinaSkupa = 0;
		while (in.hasNextLine()) {
			String redak = in.nextLine().trim();
			if (redak.isEmpty()) {
				break;
			}
			if (redak.startsWith("FILE")) {
				Map<Double, Double> skupZaTestiranje = ucitajSkup((redak.split("\\s+"))[1]);
				for (Entry<Double, Double> entry : skupZaTestiranje.entrySet()) {
					najboljaJedinka.out(entry.getKey());
					greska += najboljaJedinka.greska(entry.getValue());
				}
				velicinaSkupa += skupZaTestiranje.size();
			} else {
				brojUzoraka++;
				String[] values = redak.split("\\s+");
				najboljaJedinka.out(Double.valueOf(values[0]));
				greska += najboljaJedinka.greska(Double.valueOf(values[1]));				
			}
		}
		in.close();
		int ukupnoUzoraka = brojUzoraka + velicinaSkupa;
		najboljaJedinka.greska = greska / ukupnoUzoraka;
		System.out.println("---------------------------");
		System.out.println("Ispitivanje završeno");
		System.out.println("Ukupna pogreška: " + najboljaJedinka.greska);
		
	}

	private void urediVjerojatnosti(NeuronskaMreza[] populacija) {

		Arrays.sort(populacija);
		double p = 0.0;
	
		for (NeuronskaMreza nm : populacija) {
			nm.vjerojatnost += p;
			p = nm.vjerojatnost;
		}
		//System.out.println(p);
	}

	/**
	 * Evaluacija populacije.
	 * Za svaku jedinku izračunava se greška, dobrota
	 * i vjerojatnost selekcije.
	 * @param populacija
	 */
	private double evaluiraj(NeuronskaMreza[] populacija) {

		int N = skupZaUcenje.size();
		double maxGreska = 0.0;
		//System.out.println("nova evaluacija");
		for (NeuronskaMreza mreza : populacija) {
			double greska = 0.0;
			for (Entry<Double, Double> entry : skupZaUcenje.entrySet()) {
				mreza.out(entry.getKey());
				greska += mreza.greska(entry.getValue());
			}
			mreza.greska = greska / N;
			if (mreza.greska > maxGreska) {
				maxGreska = mreza.greska;
			}
			if (najboljaJedinka == null || najboljaJedinka.greska > mreza.greska) {
				najboljaJedinka = mreza;
			}
			//System.out.println(mreza.greska);
		}
		
		double ukupnaDobrota = 0.0;
		for (NeuronskaMreza mreza : populacija) {
			mreza.dobrota = maxGreska - mreza.greska;
			ukupnaDobrota += mreza.dobrota;
			//System.out.println(mreza.dobrota);
		}
		 
		for (NeuronskaMreza mreza : populacija) {
			mreza.vjerojatnost = mreza.dobrota / ukupnaDobrota;
			//System.out.println(mreza.vjerojatnost);
		}
		
		return maxGreska;
	}

	private Map<Double, Double> ucitajSkup(String fileName) {
		Map<Double, Double> skup = new HashMap<>();
		
		try(BufferedReader br = new BufferedReader(
				new InputStreamReader(
				new BufferedInputStream(
				new FileInputStream(fileName)),"UTF-8"))) {
			
			while(true) {
				String redak = br.readLine();
				if (redak == null) {
					break;
				}
				if (redak.isEmpty()) {
					continue;
				}
				redak = redak.trim();
				String[] podaci = redak.split("\\s+");
				skup.put(Double.valueOf(podaci[0]), Double.valueOf(podaci[1]));
			}
			
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			System.out.println("Ne može se pronaći ulazna datoteka");
			System.exit(1);
		} catch (IOException ioex) {
			System.out.println("Error reading from file");
			System.exit(1);
		}
		
		
		
		return skup;
	}

	private NeuronskaMreza[] stvoriPocetnuPopulaciju() {
		
		NeuronskaMreza[] pocetna = new NeuronskaMreza[brojKromosoma];
		for (int i = 0; i < brojKromosoma; i++) {
			pocetna[i] = new NeuronskaMreza(rand);
		}
		return pocetna;
	}

	/**
	 * Dodavanjem broja nasumično odabranog iz normalne distribucije
	 * N(0,K), gdje je K neka odabrana vrijednost. (K = 0.1/1/10
	 * Pri tome mutira se samo p posto težina odjednom, p = 4/N
	 * @param mreza
	 */
	public void mutiraj(NeuronskaMreza mreza) {
		double delta = rand.nextGaussian() * K;
		mreza.mutiraj(delta);
	}
	
	public int selektiraj(NeuronskaMreza[] populacija) {
		
		//Arrays.sort(populacija);
		double vjerojatnost = Math.random();
		for (int i = 0; i < populacija.length; i++) {
			NeuronskaMreza nm = populacija[i];
			//System.out.println(nm.vjerojatnost + " : " + vjerojatnost);
			if (vjerojatnost <= nm.vjerojatnost) {
				return i;
			}
		}
		return -1;
	}


	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.out.println("Predan pogrešan broj parametara");
			System.out.println("Potrebno predati broj kromosoma, iznos ukupne pogreške i maksimalan broj iteracija");
			System.exit(1);
		}
		
		int brojKromosoma = Integer.valueOf(args[0]);
		double ukupnaPogreska = Double.valueOf(args[1]);
		int brojIteracija = Integer.valueOf(args[2]);
		
		GenetskiAlgoritam algoritam = new GenetskiAlgoritam(brojKromosoma, ukupnaPogreska, brojIteracija);	
		
		algoritam.treniraj();
		algoritam.testiraj();
	}

}
