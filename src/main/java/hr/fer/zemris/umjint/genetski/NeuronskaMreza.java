package hr.fer.zemris.umjint.genetski;

import java.util.Random;

/**
 * Neuronska mreža 1 x 4 x 1.
 * Prijenosna funkcija je sigmoidalna.
 * U ovoj laboratorijskoj vježbi svaka neuronska
 * mreža je jedinka genetskog algoritma.
 * @author mculinovic
 *
 */
public class NeuronskaMreza implements Comparable<NeuronskaMreza>{
	
	//Početne vrijednosti postavljaju se na slučajne vrijednosti
	//na primjer iz razdiobe N(0,2)
	//tezine sredisnjih(skrivenih) i izlaznog neurona
	private double[] weights = new double[13];
	
	private double out;
	
	public double greska;
	
	public double dobrota;
	
	public double vjerojatnost;
	
	public NeuronskaMreza(Random rand) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = rand.nextGaussian() * 2;
		}
	}
	
	
	public void out(double x) {
		out = 0.0;
		for (int i = 0; i < 4; i += 3) {
			out += sigmoidalFunction(x * weights[i] + weights[i + 1]) * weights[i+2];
		}
		out += weights[12];
	}
	
	private double sigmoidalFunction(double net) {
		return 1 / (1 + Math.exp(-net));
	}
	
	/**
	 * Izračun zbroja kvadrata razlika između
	 * evaluiranog izlaza za neki x i dane 
	 * vrijednost i za taj x. i dijeliti sa N
	 * @return
	 */
	public double greska(double t) {
		return (t - out) * (t - out);
	}


	@Override
	public int compareTo(NeuronskaMreza o) {
		return Double.compare(this.vjerojatnost, o.vjerojatnost);
	}

	
	public void mutiraj(double delta) {
		double p = 4.0 /weights.length;
		for (int i = 0; i < weights.length; i++) {
			if (Math.random() < p) {
				weights[i] += delta;
			}
		}
	}

	/**
	 * Izračun aritmetičke sredine između dviju jedinka
	 * @param mreza
	 */
	public NeuronskaMreza krizaj(NeuronskaMreza nm2, Random rand) {
		NeuronskaMreza nm = new NeuronskaMreza(rand);
		for (int i = 0; i < weights.length; i++) {
			nm.weights[i] = (this.weights[i] + nm2.weights[i]) / 2;
		}
		return nm;
	}

}
