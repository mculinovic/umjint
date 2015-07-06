package hr.fer.zemris.umjint.logic;

import static hr.fer.zemris.umjint.logic.ParserPropozicijskeLogike.Operator.EKVIVALENCIJA;
import static hr.fer.zemris.umjint.logic.ParserPropozicijskeLogike.Operator.I;
import static hr.fer.zemris.umjint.logic.ParserPropozicijskeLogike.Operator.ILI;
import static hr.fer.zemris.umjint.logic.ParserPropozicijskeLogike.Operator.IMPLIKACIJA;
import static hr.fer.zemris.umjint.logic.ParserPropozicijskeLogike.OPERATOR_NE;

//import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class CNFConverter {
	
	private Cvor korijen;
	private ParserPropozicijskeLogike parser;
	private Set<Klauzula> klauzule;
	
	public CNFConverter() {
		this.klauzule = new LinkedHashSet<>();
		this.parser = new ParserPropozicijskeLogike();
	}

	public Set<Klauzula> cnfConvert(String formula) {
		parser.parsirajFormulu(formula);
		this.korijen = parser.getKorijen();
		
		ukloniEkvivalencije(korijen);
		provjeriInvoluciju(korijen);
		
		ukloniImplikacije(korijen);
		provjeriInvoluciju(korijen);
		
		potisniNegacije(korijen);
		provjeriInvoluciju(korijen);
		
		primjeniDistributivnost(korijen);
		provjeriInvoluciju(korijen);
		
		izgradiKlauzule(korijen, null, false);
		return klauzule;
	}

//	private void ispisiKlauzule() {
//		for (Klauzula k : klauzule) {
//			Set<String> l = k.getLiterali();
//			System.out.print("{");
//			for (String s: l) {
//				System.out.print(s + ", ");
//			}
//			System.out.println("}");
//		}		
//	}

	private void izgradiKlauzule(Cvor c, Klauzula klauzula, boolean negacija) {
		if (c == null) return;
		
		if (!c.jeList()) {
			if (c.getFormula().equals(I.toString())) {
				Klauzula k1 = new Klauzula();
				izgradiKlauzule(c.getLijevi(), k1, negacija);
				if (!k1.getLiterali().isEmpty()) {
					klauzule.add(k1);
				}
				Klauzula k2 = new Klauzula();
				izgradiKlauzule(c.getDesni(), k2, negacija);
				if (!k2.getLiterali().isEmpty()) {
					klauzule.add(k2);
				}
			} else if (c.getFormula().equals(OPERATOR_NE)) {
				izgradiKlauzule(c.getLijevi(), klauzula, true);
			} else if (c.getFormula().equals(ILI.toString())) {
				boolean flag = false;
				if (klauzula == null) {
					klauzula = new Klauzula();
					flag = true;
				}
				izgradiKlauzule(c.getLijevi(), klauzula, negacija);
				izgradiKlauzule(c.getDesni(), klauzula, negacija);
				if (flag) {
					klauzule.add(klauzula);
				}
			}
		} else {
			boolean flag = false;
			if (klauzula == null) {
				klauzula = new Klauzula();
				flag = true;
			}
			if (negacija) {
				klauzula.dodajLiteral("~"+ c.getFormula());
			} else {
				klauzula.dodajLiteral(c.getFormula());
			}
			if (flag) {
				klauzule.add(klauzula);
			}
		}
	}

	private void ukloniEkvivalencije(Cvor c) {
		if (c == null) return;
		if (!c.jeList() && c.getFormula().equals(EKVIVALENCIJA.toString())) {
			
			Cvor l = c.getLijevi();
			Cvor d = c.getDesni();
			
			//promijeni vrijednosti trenutnog cvora
			c.setFormula(I.toString());
			
			//stvori lijevu stranu izraza
			Cvor lijevi = new Cvor();
			lijevi.setFormula(ILI.toString());
			Cvor negacija = new Cvor();
			negacija.setFormula(OPERATOR_NE);
			negacija.setLijevi(l);
			lijevi.setLijevi(negacija);
			lijevi.setDesni(d);
			c.setLijevi(lijevi);
			
			//stvori desnu stranu izraza
			Cvor desni = new Cvor();
			desni.setFormula(ILI.toString());
			negacija = new Cvor();
			negacija.setFormula(OPERATOR_NE);
			negacija.setLijevi(d.copy());
			desni.setLijevi(negacija);
			desni.setDesni(l.copy());
			c.setDesni(desni);
			
			//preusmjeri referencu na novostvoreni cvor
		}
		ukloniEkvivalencije(c.getLijevi());
		ukloniEkvivalencije(c.getDesni());
	}

	private void ukloniImplikacije(Cvor c) {
		if (c == null) return;
		
		if (c.getFormula().equals(IMPLIKACIJA.toString())) {
			
			c.setFormula(ILI.toString());
			Cvor l = c.getLijevi();
			Cvor negacija = new Cvor();
			negacija.setFormula(OPERATOR_NE);
			negacija.setLijevi(l);
			c.setLijevi(negacija);
		}
		
		ukloniImplikacije(c.getLijevi());
		ukloniImplikacije(c.getDesni());
		
	}

	private void potisniNegacije(Cvor c) {
		if (c == null) return;
		
		//slucaj za korijen stabla
		if (c.getFormula().equals(OPERATOR_NE)) {
			Cvor izraz = c.getLijevi();
			String operator = izraz.getFormula();
			if (operator.equals(I.toString()) || operator.equals(ILI.toString())) {
				if (operator.equals(I.toString())) {
					izraz.setFormula(ILI.toString());
				} else {
					izraz.setFormula(I.toString());
				}
				Cvor ne1 = new Cvor();
				ne1.setFormula(OPERATOR_NE);
				ne1.setLijevi(izraz.getLijevi());
				izraz.setLijevi(ne1);
				
				Cvor ne2 = new Cvor();
				ne2.setFormula(OPERATOR_NE);
				ne2.setLijevi(izraz.getDesni());
				izraz.setDesni(ne2);
				korijen = izraz;
				potisniNegacije(korijen);
				return;
			}
		}
		
		Cvor l = c.getLijevi();
		if (l != null && l.getFormula().equals(OPERATOR_NE)) {
			Cvor izraz = l.getLijevi();
			String operator = izraz.getFormula();
			if (operator.equals(I.toString()) || operator.equals(ILI.toString())) {
				if (operator.equals(I.toString())) {
					izraz.setFormula(ILI.toString());
				} else {
					izraz.setFormula(I.toString());
				}
				Cvor ne1 = new Cvor();
				ne1.setFormula(OPERATOR_NE);
				ne1.setLijevi(izraz.getLijevi());
				izraz.setLijevi(ne1);
				
				Cvor ne2 = new Cvor();
				ne2.setFormula(OPERATOR_NE);
				ne2.setLijevi(izraz.getDesni());
				izraz.setDesni(ne2);
				
				c.setLijevi(izraz);
				potisniNegacije(korijen);
				return;
			}
		}
		
		Cvor d = c.getDesni();
		if (d != null && d.getFormula().equals(OPERATOR_NE)) {
			Cvor izraz = d.getLijevi();
			String operator = izraz.getFormula();
			if (operator.equals(I.toString()) || operator.equals(ILI.toString())) {
				if (operator.equals(I.toString())) {
					izraz.setFormula(ILI.toString());
				} else {
					izraz.setFormula(I.toString());
				}
				Cvor ne1 = new Cvor();
				ne1.setFormula(OPERATOR_NE);
				ne1.setLijevi(izraz.getLijevi());
				izraz.setLijevi(ne1);
				
				Cvor ne2 = new Cvor();
				ne2.setFormula(OPERATOR_NE);
				ne2.setLijevi(izraz.getDesni());
				izraz.setDesni(ne2);
				
				c.setDesni(izraz);
				potisniNegacije(korijen);
				return;
			}
		}
		
		potisniNegacije(c.getLijevi());
		potisniNegacije(c.getDesni());
	}

	private void primjeniDistributivnost(Cvor c) {
		if (c == null) return;
		
		if (c.getFormula().equals(ILI.toString())) {
			Cvor d = c.getDesni();
			if (d.getFormula().equals(I.toString())) {
				c.setFormula(I.toString());
				
				Cvor novi1 = new Cvor();
				novi1.setFormula(ILI.toString());
				novi1.setLijevi(c.getLijevi());
				novi1.setDesni(d.getLijevi());
				
				Cvor novi2 = new Cvor();
				novi2.setFormula(ILI.toString());
				novi2.setLijevi(c.getLijevi().copy());
				novi2.setDesni(d.getDesni());
				
				c.setLijevi(novi1);
				c.setDesni(novi2);
				primjeniDistributivnost(korijen);
				return;
			}
			
			Cvor l = c.getLijevi();
			if (l.getFormula().equals(I.toString())) {
				c.setFormula(I.toString());
				
				Cvor novi1 = new Cvor();
				novi1.setFormula(ILI.toString());
				novi1.setDesni(c.getDesni());
				novi1.setLijevi(l.getLijevi());
				
				Cvor novi2 = new Cvor();
				novi2.setFormula(ILI.toString());
				novi2.setDesni(c.getDesni().copy());
				novi2.setLijevi(l.getDesni());
				
				c.setLijevi(novi1);
				c.setDesni(novi2);
				primjeniDistributivnost(korijen);
				return;
			}
		}
		
		primjeniDistributivnost(c.getLijevi());
		primjeniDistributivnost(c.getDesni());
	}
	
	private void provjeriInvoluciju(Cvor c) {
		if (c == null) {
			return;
		}
		
		if (c.equals(korijen) && c.getFormula().equals(OPERATOR_NE)) {
			Cvor lijevi = c.getLijevi();
			if (lijevi != null && !lijevi.jeList() && lijevi.getFormula().equals(OPERATOR_NE)) {
				korijen = lijevi.getLijevi();
				c = null;
				lijevi = null;
				provjeriInvoluciju(korijen);
				return;
			}
		}
		
		Cvor l = c.getLijevi();
		if (l != null && !l.jeList() && l.getFormula().equals(OPERATOR_NE)) {
			Cvor lijevi = l.getLijevi();
			if (lijevi != null && !lijevi.jeList() && lijevi.getFormula().equals(OPERATOR_NE)) {
				c.setLijevi(lijevi.getLijevi());
				l = null;
				lijevi = null;
				provjeriInvoluciju(korijen);
				return;
			}
		}
		
		Cvor d = c.getDesni();
		if (d != null && !d.jeList() && d.getFormula().equals(OPERATOR_NE)) {
			Cvor lijevi = d.getLijevi();
			if (lijevi != null && !lijevi.jeList() && lijevi.getFormula().equals(OPERATOR_NE)) {
				c.setDesni(lijevi.getLijevi());
				l = null;
				lijevi = null;
				provjeriInvoluciju(korijen);
				return;
			}
		};
		
		provjeriInvoluciju(c.getLijevi());
		provjeriInvoluciju(c.getDesni());
		
	}
	
	public Cvor getKorijen() {
		return korijen;
	}

}
