import static org.junit.Assert.*;
import org.junit.Test;

// Contains 10 unit tests for the Polynomial class.
// NOTE: When comparing Polynomial objects, assertTrue(p.equals(q)) seems
// to be necessary, as assertEquals(p, q) uses Object.equals and not Polynomial.equals
public class PolynomialUnitTest {
	public static final double EPSILON = 0.001;

	@Test // "Array constructor" here refers to Polynomial(int[] coeffs, int[] exps)
	public void arrayConstructorTest() {
		assertTrue("Using empty arrays, comparing to default constructor",
				new Polynomial(new int[0], new int[0])
					.equals(new Polynomial()));

		assertTrue("Using zero coefficients, comparing to default constructor",
				new Polynomial(new int[] {0, 0}, new int[] {randInt(), randInt()})
					.equals(new Polynomial()));
		
		// Check several polynomials
		for (int i = 0; i < 10; i++) {
			// Generate some random coefficients and exponents
			int c1 = randInt();
			int c2 = randInt();
			int c3 = randInt();
			int c4 = randInt();
			int e1 = randInt();
			int e2 = randInt();
			int e3 = randInt();
			int e4 = randInt();
			
			Polynomial base = new Polynomial(new int[] {c1, c2, c3}, new int[] {e1, e2, e3});
			
			assertTrue("Testing that order doesn't matter for the array constructor",
					new Polynomial(new int[] {c2, c3, c1}, new int[] {e2, e3, e1})
						.equals(base));
			
			assertTrue("Testing that zero coefficients are ignored",
					new Polynomial(new int[] {c1, 0, c2, 0, c3, 0}, new int[] {e1, e1, e2, e2, e3, e3})
						.equals(base));
			
			assertTrue("Testing that same-exponent terms are combined",
					new Polynomial(new int[] {c1-c4, c2, c3, c4}, new int[] {e1, e2, e3, e1})
						.equals(base));
					
			assertTrue("Testing that zero terms are removed",
					new Polynomial(new int[] {c1, c2, c3, c4, -c4}, new int[] {e1, e2, e3, e4, e4})
						.equals(base));
			
			assertTrue("Comparing a single constructor call to sequential additions",
					new Polynomial(new int[] {c1}, new int[] {e1})
						.add(new Polynomial(new int[] {c2}, new int[] {e2}))
						.add(new Polynomial(new int[] {c3}, new int[] {e3}))
						.equals(base));
		} // end for
		
	} // end arrayConstructorTest
	
	@Test
	public void evaluationTest() {
		for (int i = 0; i < 5; i++) {
			assertEquals("Ensuring the zero polynomial always evaluates to zero",
					new Polynomial().evaluate(randX()), 0, EPSILON);
		}
		
		try {
			new Polynomial(new int[] {randPosInt()}, new int[] {randNegInt()}).evaluate(0);
			fail("Raising zero to a negative exponent should throw an exception");
		} catch (Exception e) {};
		
		// Check several different Polynomials
		for (int i = 0; i < 10; i++) {
			// Generate some random coefficients and exponents
			int c1 = randInt();
			int c2 = randInt();
			int c3 = randInt();
			int e1 = randInt();
			int e2 = randInt();
			int e3 = randInt();
			
			Polynomial p = new Polynomial(new int[] {c1, c2, c3}, new int[] {e1, e2, e3});
			
			// Check several values of x for this Polynomial
			for (int j = 0; j < 10; j++) {
				double x = randX();
				
				assertEquals("Comparing evaluation values to mathematical standard",
						c1 * Math.pow(x, e1) + c2 * Math.pow(x, e2) + c3 * Math.pow(x, e3),
						p.evaluate(x), EPSILON);
			} // end inner for
		} // end outer for
		
	} // end evaluationTest
	
	@Test
	public void copyConstructorTest() {
		// Test several Polynomials
		for (int i = 0; i < 10; i++) {
			Polynomial p = randPoly();
			assertTrue("Making sure copy constructor makes a perfect copy",
					p.equals(new Polynomial(p)));
		} // end for
	} // end copyConstructorTest
	
	@Test
	public void toStringTest() {
		assertEquals("Ensuring proper representation of the zero polynomial",
				new Polynomial().toString(), "0x^1");
		
		// Test several Polynomials
		for (int i = 0; i < 10; i++) {
			// Generate some random (positive) coefficients and exponents
			int c1 = randPosInt();
			int c2 = randPosInt();
			int e1 = randPosInt();
			int e2 = randPosInt() + e1; // ensures this exponent is larger
			
			assertEquals("Ensuring a single term is represented correctly",
					new Polynomial(new int[] {c1}, new int[] {e1}).toString(),
					c1 + "x^" + e1);
			
			assertEquals("Ensuring multiple terms are represented and ordered correctly",
					new Polynomial(new int[] {c1, c2}, new int[] {e1, e2}).toString(),
					c2 + "x^" + e2 + "+" + c1 + "x^" + e1);
			
			assertEquals("Ensuring negatives display properly",
					new Polynomial(new int[] {-c1, -c2}, new int[] {-e1, -e2}).toString(),
					-c1 + "x^-" + e1 + "-" + c2 + "x^-" + e2);
			
		}
	}
	
	@Test
	public void operationTest() {
		// Test multiple sets of Polynomials
		for (int i = 0; i < 10; i++) {
			Polynomial p = randPoly();
			Polynomial q = randPoly();
			
			// Test multiple values of x for each set of Polynomials
			for (int j = 0; j < 10; j++) {
				double x = randX();
				
				assertEquals("Comparing sum of polynomial evaluations"
						+ " to evaluation of added polynomials",
						p.add(q).evaluate(x), p.evaluate(x) + q.evaluate(x),
						EPSILON);
				
				assertEquals("Comparing product of polynomial evaluations"
						+ " to evaluation of multiplied polynomials",
						p.multiply(q).evaluate(x), p.evaluate(x) * q.evaluate(x),
						EPSILON);
				
				assertEquals("Comparing difference of polynomial evaluations"
						+ " to evaluation of subtracted polynomials",
						p.subtract(q).evaluate(x), p.evaluate(x) - q.evaluate(x),
						EPSILON);
				
				assertEquals("Comparing negation of polynomial evaluation"
						+ " to evaluation of negated polynomial",
						p.negate().evaluate(x), -p.evaluate(x),
						EPSILON);
				
			} // end inner for
		} // end outer for
	} // end operationTest
	
	@Test
	public void parseTest() {
		// Test several Polynomials
		for (int i = 0; i < 10; i++) {
			Polynomial p = randPoly();
			assertTrue("Ensuring parse of toString is equal to the original Polynomial",
					Polynomial.parse(p.toString()).equals(p));
		}
		
		for (int i = 0; i < 5; i++) {
			assertTrue("Testing if representations of the zero Polynomial can be parsed",
					Polynomial.parse("0x^" + randInt() + "+0x^" + randInt() + "-0x^" + randInt())
					.equals(new Polynomial()));
		}
		
		// Test several examples to compare parse to the array constructor
		for (int i = 0; i < 10; i++) {
			// Generate some random coefficients and exponents
			int c1 = randInt();
			int c2 = randInt();
			int c3 = randInt();
			int e1 = randInt();
			int e2 = randInt();
			int e3 = randInt();
			
			String str = c1 + "x^" + e1 + "+" + c2 + "x^" + e2 + "+" + c3 + "x^" + e3;
			str.replaceAll("\\+\\-", "-"); // get rid of "+-", since that isn't proper form
			
			assertTrue("Testing equality between array constructor and parse method",
					new Polynomial(new int[] {c1, c2, c3}, new int[] {e1, e2, e3})
					.equals(Polynomial.parse(str)));
		} // end for	
	} // end parseTest
	
	@Test
	public void commutativityTest() {
		// Test several pairs of Polynomials
		for (int i = 0; i < 10; i++) {
			Polynomial p = randPoly();
			Polynomial q = randPoly();
			
			assertTrue("Testing p+q = q+p",
					p.add(q).equals(q.add(p)));
			
			assertTrue("Testing p*q = q*p",
					p.multiply(q).equals(q.multiply(p)));
			
			assertTrue("Testing p-q = -(q-p)",
					p.subtract(q).equals(q.subtract(p).negate()));
		} // end for
	} // end commutativityTest
	
	@Test
	public void identityTest() {
		// Test several Polynomials
		for (int i = 0; i < 10; i++) {
			Polynomial p = randPoly();
			
			assertTrue("Testing p*0 = 0",
					p.multiply(new Polynomial()).equals(new Polynomial()));
			
			assertTrue("Testing p+0 = p",
					p.add(new Polynomial()).equals(p));
			
			assertTrue("Testing p*1 = p",
					p.multiply(new Polynomial(new int[] {1}, new int[] {0}))
					.equals(p));
			
			assertTrue("Testing p+(-p) = 0",
					p.add(p.negate()).equals(new Polynomial()));
			
			assertTrue("Testing p-p = 0",
					p.subtract(p).equals(new Polynomial()));
			
		} // end for
	} // end identityTest

	// Generates a random integer in -5 to +5
	// (limited range to prevent numbers getting too large)
	private static int randInt() {
		return (int) (-5 + 11 * Math.random());
	}
	
	// Generates a random integer in 1 to 5
	private static int randPosInt() {
		return (int) (1 + 5 * Math.random());
	}
	
	// Generates a random integer in -5 to -1
	private static int randNegInt() {
		return -randPosInt();
	}
	
	// Generate a random nonzero double in -5 to -1 or 1 to 5
	// (not including -1 < x < 1 to prevent numbers getting too large)
	private static double randX() {
		double x = 1 + 8 * Math.random();
		return x > 5 ? x - 10 : x;
	}
	
	// Generate a random Polynomial with small integer coefficients / exponents,
	// and a maximum of 5 terms.
	private static Polynomial randPoly() {
		int[] coeffs = {randInt(), randInt(), randInt(), randInt(), randInt()};
		int[] exps = {randInt(), randInt(), randInt(), randInt(), randInt()};
		return new Polynomial(coeffs, exps);
	}

}
