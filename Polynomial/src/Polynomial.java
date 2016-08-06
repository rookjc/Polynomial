import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for representing polynomials, along with basic operations performable
 * on them and a method to parse a polynomial from a string.
 * 
 * @author Jayson Rook
 * @version 1.0
 */
public class Polynomial {
	
	// Helper class for representing an individual term in a larger polynomial
	private static class PolynomialTerm {
		private int coefficient;
		private int exponent;
		
		// Constructor from a coefficient and an exponent
		public PolynomialTerm(int coeff, int exp) {
			coefficient = coeff;
			exponent = exp;
		}
		
		// Copy constructor
		public PolynomialTerm(PolynomialTerm term) {
			coefficient = term.coefficient;
			exponent = term.exponent;
		}
		
		// Modifies this term's coefficient by adding another coefficient to it.
		public void addCoefficient(int otherCoeff) {
			coefficient += otherCoeff;
		}
		
		// Returns a new term representing the product of this and another
		// term, without modifying either original.
		public PolynomialTerm multiply(PolynomialTerm otherTerm) {
			return new PolynomialTerm(coefficient * otherTerm.coefficient,
					exponent + otherTerm.exponent);
		}
		
		// Returns a new term equal to the negation of this one.
		public PolynomialTerm negate() {
			return new PolynomialTerm(-coefficient, exponent);
		}
		
		// Computes the value of this term for a particular value of x
		public double evaluate(double x) {
			if (x == 0 && exponent < 1)
				throw new ArithmeticException("Cannot raise zero to exponent " + exponent);
			return coefficient * Math.pow(x, exponent);
		}
		
		// Returns true iff this term is equivalent to another term
		public boolean equals(PolynomialTerm otherTerm) {
			return coefficient == otherTerm.coefficient
					&& exponent == otherTerm.exponent;
		}
		
		// Gives standard String representation of this term
		public String toString() {
			String result = "";
			if (coefficient > 0)
				result = "+";
			result += coefficient + "x^" + exponent;
			return result;
		}

	} // end PolynomialTerm class
	
	// Comparator for PolynomialTerms, such that terms with lower exponents are "greater"
	private static class TermExpComparator implements Comparator<PolynomialTerm> {
		@Override
		public int compare(PolynomialTerm x, PolynomialTerm y) {
			return -Integer.compare(x.exponent, y.exponent);
		}
	}
	private static final TermExpComparator COMPARATOR = new TermExpComparator();
	
	// Regular expression for capturing the first term's coefficient,
	// the first term's exponent, and the rest of the expression.
	private static final Pattern PARSE_PATTERN =
			Pattern.compile("\\+?(\\-?[\\d]+)x\\^(\\-?[\\d]+)(.*)");
	
	private ArrayList<PolynomialTerm> terms;
	
	/**
	 * Creates a zero polynomial (0x^1).
	 */
	public Polynomial() {
		terms = new ArrayList<>();
	}
	
	/**
	 * Creates a polynomial from two arrays, representing coefficient / exponent
	 * pairs for the terms of the polynomial.
	 * @param coeffs the array of coefficients
	 * @param exps the array of corresponding exponents
	 */
	public Polynomial(int[] coeffs, int[] exps) {
		this();
		// Assuming coeffs and exps have the same length
		for (int i = 0; i < coeffs.length; i++)
			addTerm(new PolynomialTerm(coeffs[i], exps[i]));
		
		// Order the terms upon creation, so the terms can always be assumed
		// to be in order.
		orderTerms();
	}
	
	/**
	 * Copy constructor
	 * @param p the polynomial to copy
	 */
	public Polynomial(Polynomial p) {
		this();
		for (PolynomialTerm term : p.terms)
			terms.add(new PolynomialTerm(term));
		// No need to use addTerm() or orderTerms(), since the terms are copied from
		// Polynomial p and must already be properly arranged.
	}
	
	/**
	 * Returns the polynomial resulting from the addition of this and another
	 * polynomial, without modifying either of the originals.
	 * @param p the polynomial added to this one
	 * @return the polynomial representing the sum
	 */
	public Polynomial add(Polynomial p) {
		// Copy the other Polynomial, and modify by adding terms from this one.
		Polynomial result = new Polynomial(p);
		for (PolynomialTerm term : terms)
			result.addTerm(term);
		
		// Order the terms upon creation, so the terms can always be assumed
		// to be in order.
		result.orderTerms();
		return result;
	}
	
	/**
	 * Returns the polynomial resulting from the multiplication of this and
	 * another polynomial, without modifying either of the originals.
	 * @param p the polynomial multiplied to this one
	 * @return the polynomial representing the product
	 */
	public Polynomial multiply(Polynomial p) {
		Polynomial result = new Polynomial();
		
		// For every combination of a term in this Polynomial and a term
		// in p, add the terms' product to the result Polynomial.
		for (PolynomialTerm p1Term : terms) {
			for (PolynomialTerm p2Term : p.terms)
				result.addTerm(p1Term.multiply(p2Term));
		}
		
		// Order the terms upon creation, so the terms can always be assumed
		// to be in order.
		result.orderTerms();
		return result;
	}
	
	/**
	 * Returns the negation of this polynomial, without modifying it.
	 * @return the negation of this polynomial
	 */
	public Polynomial negate() {
		Polynomial result = new Polynomial();
		
		// Simply add together the negation of each term in this Polynomial.
		// addTerm() and orderTerms() aren't needed, since correct arrangement
		// is ensured by the correctness of this (the source) Polynomial.
		for (PolynomialTerm term : terms)
			result.terms.add(term.negate());
		return result;
	}
	
	/**
	 * Returns the polynomial that results when another polynomial is subtracted
	 * from this one, without modifying either of the originals.
	 * @param p the polynomial subtracted from this one
	 * @return the polynomial representing the difference
	 */
	public Polynomial subtract(Polynomial p) {
		return add(p.negate());
	}
	
	/**
	 * Computes the value of this polynomial for a particular value of x.
	 * @param x the value of x in the computation
	 * @return the computed value
	 */
	public double evaluate(double x) {
		double termSum = 0;
		// Conduct the evaluation for each individual term, adding the results together.
		for (PolynomialTerm term : terms)
			termSum += term.evaluate(x);
		return termSum;
	}
	
	/**
	 * Returns true iff this and another polynomial are logically equivalent.
	 * @param p the polynomial to compare with this one
	 * @return true iff the two polynomials are equal
	 */
	public boolean equals(Polynomial p) {
		// terms should always be arranged properly (with no duplicate exponents
		// or zero coefficients), so unequal numbers of terms implies inequality
		if (terms.size() != p.terms.size())
			return false;
		
		// Since terms ArrayLists are always sorted, we can just check each term
		// index for equality between the two Polynomials
		for (int i = 0; i < terms.size(); i++) {
			if (!terms.get(i).equals(p.terms.get(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * Creates a string representation of the polynomial, with terms in descending
	 * order of exponent, no zero terms, and no duplicate exponents.
	 * @return the string representation
	 */
	public String toString() {
		// Special case for zero Polynomial
		if (terms.size() == 0)
			return "0x^1";
		
		// Otherwise, simply concatenate string representations of individual
		// terms, which are already in order.
		String result = "";
		for (PolynomialTerm term : terms)
			result += term;
		
		// Special case to remove an unneeded '+' at the beginning
		if (result.charAt(0) == '+')
			result = result.substring(1);
		
		return result;
	}
	
	/**
	 * Creates a polynomial from a valid string representation of it
	 * @param str a string representing a valid polynomial
	 * @return the created polynomial
	 */
	public static Polynomial parse(String str) {
		Polynomial result = new Polynomial();
		Matcher m = PARSE_PATTERN.matcher(str);
		
		// Match the coefficient and exponent for the first term, create
		// and add the appropriate PolynomialTerm to the result, and repeat
		// with the rest of the expression (as long as it matches the pattern).
		while (m.matches()) {
			int coeff = Integer.valueOf(m.group(1));
			int exp = Integer.valueOf(m.group(2));
			result.addTerm(new PolynomialTerm(coeff, exp));
			m = PARSE_PATTERN.matcher(m.group(3));
		}
		
		// Order the terms upon creation, so the terms can always be assumed
		// to be in order.
		result.orderTerms();
		return result;
	}
	
	// Sorts the terms ArrayList in descending order of exponent
	private void orderTerms() {
		terms.sort(COMPARATOR);
	}
	
	// Adds a single term to the polynomial, with some extra checks to prevent
	// duplicate exponents and zero coefficients
	private void addTerm(PolynomialTerm toAdd) {
		// Do nothing if the term is equal to zero
		if (toAdd.coefficient == 0)
			return;
		
		// Check for an existing term with the same exponent. If one exists,
		// add the coefficient of toAdd onto its coefficient.
		for (int i = 0; i < terms.size(); i++) {
			if (toAdd.exponent == terms.get(i).exponent) {
				terms.get(i).addCoefficient(toAdd.coefficient);
				ensureNonzeroTerm(i); // Ensure addition didn't result in a zero
				return; // End the method without actually adding toAdd to terms
			}
		}
		
		// The exponent doesn't appear elsewhere, so add this term to the ArrayList.
		terms.add(new PolynomialTerm(toAdd));
	}
	
	// Removes the term at the specified index, if its coefficient is zero
	private void ensureNonzeroTerm(int index) {
		if (terms.get(index).coefficient == 0)
			terms.remove(index);
	}
	
	// Generate a random Polynomial with small integer coefficients / exponents,
	// and a maximum of 5 terms.
	public static Polynomial random() {
		int[] coeffs = { randInt(), randInt(), randInt(), randInt(), randInt() };
		int[] exps = { randInt(), randInt(), randInt(), randInt(), randInt() };
		return new Polynomial(coeffs, exps);
	}

	// Generates a random integer in -5 to +5
	// (limited range to prevent numbers getting too large)
	private static int randInt() {
		return (int) (-5 + 11 * Math.random());
	}
	
	
} // end class
