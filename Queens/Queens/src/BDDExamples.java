
import net.sf.javabdd.*;

public class BDDExamples {

	/**
	 * Gives some examples on how the BDD package is used
	 */
	public static void main(String[] args){
		BDDFactory fact = JFactory.init(10,10);
		fact.setVarNum(2);
		
		BDD True = fact.one();
		BDD False = fact.zero();
		
		// the expression x0
		BDD x_0 = fact.ithVar(0);
		
		//the expression not x1
		BDD nx_1 = fact.nithVar(1);
		
		// the expression (not x1 or x0) and (True or false)
		BDD b = nx_1.or(x_0).and(True.or(False));
		
		// Checks whether or not expression is unsat
		System.out.println("b is unsat? : " + b.isZero());
		
		// checks whether expression is tautology
		System.out.println("b is tautology? : " + b.isOne());
		
		// In order to restrict or quantify the expression to a given assignment
		// we give the assignment as a conjunction where positive variables
		// indicate that the variable should be restricted to false, and vice versa.
		BDD restriction_x1_true_x0_false = fact.ithVar(1).and(fact.nithVar(0)); 
		
		BDD restricted = b.restrict(restriction_x1_true_x0_false);
		BDD existed = b.exist(x_0);
		
		// Exist. should be tautology
		System.out.println("Existiential quant. cause taut: " + existed.isOne());
		
		// Restriction shoule be unsat:
		System.out.println("Restriction caused unsat: " + restricted.isZero());
		
		// how to perform replacement
		BDDPairing replacement = fact.makePair();
		int[] from = {1};
		int[] to = {0};
		replacement.set(from,to);
		
		BDD b_replaced = existed.replace(replacement);
		
		
	}

}
