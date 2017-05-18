//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.operator.impl.mutation;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Note: this class does check that the polynomial mutation operator does not return invalid
 * values, but not that it works properly (@see PolynomialMutationWorkingTest)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PolynomialMutationTest {
  private static final double EPSILON = 0.00000000000001 ;
  
  @SuppressWarnings("deprecation")
  @Test
  public void shouldConstructorWithoutParameterAssignTheDefaultValues() {
    PolynomialMutation mutation = new PolynomialMutation() ;
    assertEquals(0.01, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(20.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldConstructorWithProblemAndDistributionIndexParametersAssignTheCorrectValues() {
    DoubleProblem problem = new MockDoubleProblem(4) ;
    PolynomialMutation mutation = new PolynomialMutation(problem, 10.0) ;
    assertEquals(1.0/problem.getNumberOfVariables(), (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(10.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double mutationProbability = 0.1 ;
    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, 2.0) ;
    assertEquals(mutationProbability, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double distributionIndex = 15.0 ;
    PolynomialMutation mutation = new PolynomialMutation(0.1, distributionIndex) ;
    assertEquals(distributionIndex, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @SuppressWarnings("deprecation")
  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double mutationProbability = -0.1 ;
    new PolynomialMutation(mutationProbability, 2.0) ;
  }

  @SuppressWarnings("deprecation")
  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    double distributionIndex = -0.1 ;
    new PolynomialMutation(0.1, distributionIndex) ;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.getMutationProbability(), EPSILON) ;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @SuppressWarnings("deprecation")
  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 20.0) ;

    mutation.execute(null) ;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfProbabilityIsZero() {
    double mutationProbability = 0.0;
    double distributionIndex = 20.0 ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    DoubleSolution solution = problem.createSolution() ;
    DoubleSolution oldSolution = (DoubleSolution)solution.copy() ;

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfItIsNotMutated() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(1.0) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex, randomGenerator) ;
    DoubleProblem problem = new MockDoubleProblem(1, BoundedRandomGenerator.bound(randomGenerator)) ;
    DoubleSolution solution = problem.createSolution() ;
    DoubleSolution oldSolution = (DoubleSolution)solution.copy() ;

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(1)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.6) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex, randomGenerator) ;
    DoubleProblem problem = new MockDoubleProblem(1, BoundedRandomGenerator.bound(randomGenerator)) ;
    DoubleSolution solution = problem.createSolution() ;

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers.greaterThanOrEqualTo(
        solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAnotherValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex, randomGenerator) ;
    DoubleProblem problem = new MockDoubleProblem(1, BoundedRandomGenerator.bound(randomGenerator)) ;
    DoubleSolution solution = problem.createSolution() ;

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers.greaterThanOrEqualTo(solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionWithSameLowerAndUpperBoundsReturnTheBoundValue() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex, randomGenerator) ;

    MockDoubleProblem problem = new MockDoubleProblem(1, BoundedRandomGenerator.bound(randomGenerator)) ;
    ReflectionTestUtils.setField(problem, "lowerLimit", Arrays.asList(new Double[]{1.0}));
    ReflectionTestUtils.setField(problem, "upperLimit", Arrays.asList(new Double[]{1.0}));

    DoubleSolution solution = problem.createSolution() ;

    mutation.execute(solution) ;

    assertEquals(1.0, solution.getVariableValue(0), EPSILON) ;
  }

  /**
   * Mock class representing a double problem
   */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

      private BoundedRandomGenerator<Double> variableRandomGenerator;
      
    /** Constructor */
    @SuppressWarnings("deprecation")
    @Deprecated
    public MockDoubleProblem(Integer numberOfVariables) {
      this(numberOfVariables, (min, max) -> JMetalRandom.getInstance().nextDouble(min, max));
    }

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables, BoundedRandomGenerator<Double> variableRandomGenerator) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);
      this.variableRandomGenerator = variableRandomGenerator;

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override
    public DoubleSolution createSolution() {
      return new DefaultDoubleSolution(this, variableRandomGenerator) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
  
	@SuppressWarnings("deprecation")
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		double crossoverProbability = 0.1;
		int alpha = 20;
		RepairDoubleSolutionAtBounds solutionRepair = new RepairDoubleSolutionAtBounds();
		@SuppressWarnings("serial")
		DoubleProblem problem = new AbstractDoubleProblem() {

			@Override
			public void evaluate(DoubleSolution solution) {
				// Do nothing
			}
			
			@Override
			public int getNumberOfVariables() {
				return 5;
			}
			
			@Override
			public Double getLowerBound(int index) {
				return 0.0;
			}
			
			@Override
			public Double getUpperBound(int index) {
				return 10.0;
			}

		};
		DoubleSolution solution = problem.createSolution();

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new PolynomialMutation(crossoverProbability, alpha, solutionRepair).execute(solution);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new PolynomialMutation(crossoverProbability, alpha, solutionRepair, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(solution);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}
