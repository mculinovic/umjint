package hr.fer.zemris.umjint.logic;

import java.util.HashSet;
import java.util.Set;

public class ParserPropozicijskeLogike {

	private Set<Cvor> stablo;
	private Cvor korijen;

	private final static String LIJEVA_ZAGRADA = "(";
	private final static String DESNA_ZAGRADA = ")";
	public final static String OPERATOR_NE = "~";

	public static enum Operator {
		ILI("|"), I("&"), EKVIVALENCIJA("<->"), IMPLIKACIJA("->");

		private Operator(final String operator) {
			this.operator = operator;
		}

		private final String operator;

		@Override
		public String toString() {
			return operator;
		}
	}

	public ParserPropozicijskeLogike() {
		stablo = new HashSet<>();
	}

	public void parsirajFormulu(String formula) {
		korijen = izgradiStablo(formula);
	}

	private Cvor izgradiStablo(String formula) {

		if (formula == null || formula.isEmpty()) {
			throw new IllegalArgumentException(
					"Potrebno je unijeti dobro oblikovanu formulu propozicijske logike!");
		}

		formula = formula.replaceAll("\\s", "");
		Cvor cvor = null;

		if (jePropozicijskaVarijabla(formula)) {
			return new Cvor(formula, null, null);
		}

		int dubina = 0;
		int duljina = formula.length();

		for (int i = duljina - 1; i >= 0; i--) {
			if (formula.substring(i, i + 1).equals(DESNA_ZAGRADA)) {
				dubina++;
			} else if (formula.substring(i, i + 1).equals(LIJEVA_ZAGRADA)) {
				dubina--;
			} else if (dubina == 0) {
				for (Operator o : Operator.values()) {
					String operator = o.toString();
					int pocetak = i - operator.length() + 1;
					if (formula.startsWith(operator, pocetak)) {
						cvor = new Cvor();
						cvor.setFormula(operator);
						stablo.add(cvor);
						cvor.setLijevi(izgradiStablo(formula.substring(0,
								pocetak)));
						cvor.setDesni(izgradiStablo(formula.substring(i + 1)));
						break;
					}
				}
			}
		}

		if (cvor != null) {
			return cvor;
		}

		if (formula.substring(0, 1).equals(OPERATOR_NE)) {
			cvor = new Cvor();
			cvor.setFormula(OPERATOR_NE);
			stablo.add(cvor);
			cvor.setLijevi(izgradiStablo(formula.substring(1)));
			return cvor;
		} else if (formula.substring(0, 1).equals(LIJEVA_ZAGRADA)
				&& formula.substring(duljina - 1).equals(DESNA_ZAGRADA)) {
			return izgradiStablo(formula.substring(1, duljina - 1));
		} else {
			throw new IllegalArgumentException(
					"Potrebno je unijeti dobro oblikovanu formulu propozicijske logike!");
		}
	}

	private boolean jePropozicijskaVarijabla(String formula) {
		if (formula.length() == 1 && Character.isUpperCase(formula.charAt(0))) {
			return true;
		}
		return false;
	}

	public Cvor getKorijen() {
		return korijen;
	}

	public void ispisi(Cvor c) {
		if (c == null) {
			return;
		}
		String ispis = c.getFormula();
		Cvor l = c.getLijevi();
		Cvor d = c.getDesni();
		if (!c.jeList()) {
			ispis = "(" + ispis + ", ";
		}
		System.out.print(ispis);
		ispisi(l);

		if (l != null && d != null) {
			System.out.print(", ");
		}
		ispisi(d);

		if (!c.jeList()) {
			System.out.print(")");
		}

	}

	public Set<Cvor> getStablo() {
		return stablo;
	}

}
