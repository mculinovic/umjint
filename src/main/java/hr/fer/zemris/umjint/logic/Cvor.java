package hr.fer.zemris.umjint.logic;

public class Cvor {

	private String formula;
	private Cvor lijevi;
	private Cvor desni;
	
	public Cvor() {};

	public Cvor(String formula, Cvor lijevi, Cvor desni) {
		super();
		this.formula = formula;
		this.lijevi = lijevi;
		this.desni = desni;
	}

	public String getFormula() {
		return formula;
	}

	public Cvor getLijevi() {
		return lijevi;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public void setLijevi(Cvor lijevi) {
		this.lijevi = lijevi;
	}

	public void setDesni(Cvor desni) {
		this.desni = desni;
	}

	public Cvor getDesni() {
		return desni;
	}

	@Override
	public int hashCode() {
		return formula.hashCode();
	}

	public boolean equals(Cvor cvor) {
		if (cvor == null) {
			return false;
		}
		if (lijevi == null && desni == null) {
			return formula.equals(cvor.formula);
		} else if (lijevi == null) {
			return formula.equals(cvor.formula) && desni.equals(cvor.desni);
		} else if (desni == null) {
			return formula.equals(cvor.formula) && lijevi.equals(cvor.lijevi);
		}
		return formula.equals(cvor.formula) && lijevi.equals(cvor.lijevi)
				&& desni.equals(cvor.desni);
	}
	
	public boolean jeList() {
		if (lijevi == null && desni == null) {
			return true;
		}
		return false;
	}
	
	public Cvor copy() {
		Cvor c = new Cvor();
		c.setFormula(this.formula);
		c.setDesni(this.desni);
		c.setLijevi(this.lijevi);
		return c;
	}
}
