/* Advent of Code answers written by John Gaughan
 * Copyright (C) 2021  John Gaughan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package us.coffeecode.advent_of_code.y2018;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import us.coffeecode.advent_of_code.Utils;
import us.coffeecode.advent_of_code.y2018.opcode.OpCode;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/19">Year 2018, day 19</a>. Today's puzzle asks us to run a program and get
 * the results in one of the registers after it is complete. Parts one and two differ by one input, which is a flag that
 * controls an internal control constant. Part one returns quickly, part two runs for a very long time.
 * </p>
 * <p>
 * The key to part two is figuring out what the algorithm in the program does, and instead calculate it in a more
 * efficient manner. Personally, I do not like these types of problems where it is not feasible to automate it
 * completely: instead, we essentially need to rewrite the input in a different format. Anyway, the program sums up the
 * factors of a number it calculates. However, the factorization is performed in a very inefficient manner: nested
 * loops.
 * </p>
 * <p>
 * In order to make this program more general such that it actually uses the input rather than hard-coding an answer, it
 * runs the program for a short while to extract the number being factored. It then generates the necessary prime
 * factors using a sieve, the gets the prime factorization of that number. From here we can use combinations of the
 * prime factors to get the distinct factors. Then sum those factors.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day19 {

  public long calculatePart1() {
    State state = getInput();
    execute(state, Long.MAX_VALUE);
    return state.register[0];
  }

  public long calculatePart2() {
    State state = getInput();
    state.register[0] = 1;
    // Let the program execute for a few steps to calculate the number we need.
    execute(state, 30);
    final int number = state.register[3];
    // Get the prime factors, then combine them to get all of the factors.
    final int[] primeFactors = getPrimeFactors(number);
    long sum = 0;
    for (final int factor : getAllFactors(primeFactors)) {
      sum += factor;
    }
    return sum;
  }

  private void execute(final State state, final long maxIterations) {
    for (long i = 0; i < maxIterations && -1 < state.register[state.ip_reg]
      && state.register[state.ip_reg] < state.instructions.size(); ++i) {
      final Instruction inst = state.instructions.get(state.register[state.ip_reg]);
      inst.opcode.apply(state.register, inst.a, inst.b, inst.c);
      ++state.register[state.ip_reg];
    }
  }

  private int[] getPrimeFactors(final int number) {
    final int[] primes = Utils.getPrimesUpTo(number);
    final List<Integer> factors = new ArrayList<>();
    int value = number;
    int i = 0;
    // Divide by each prime factor, in turn, until it no longer divides whatever is left of the number.
    while (value > 1 && i < primes.length) {
      if (value % primes[i] == 0) {
        value /= primes[i];
        factors.add(Integer.valueOf(primes[i]));
      }
      else {
        ++i;
      }
    }
    return factors.stream().mapToInt(Integer::intValue).toArray();
  }

  private int[] getAllFactors(final int[] primeFactors) {
    final Set<Integer> factors = new HashSet<>();
    // Loop counter holds the bits for each array position to use.
    for (int bits = 0; bits < 1 << primeFactors.length; ++bits) {
      int factor = 1;
      // Inner counter is the index into the prime array for the factor to use.
      for (int j = 0; j < primeFactors.length; ++j) {
        if ((bits >> j & 0x1) > 0) {
          factor *= primeFactors[j];
        }
      }
      factors.add(Integer.valueOf(factor));
    }
    return factors.stream().mapToInt(Integer::intValue).toArray();
  }

  /** Get the input data for this solution. */
  private State getInput() {
    try {
      return new State(Files.readAllLines(Utils.getInput(2018, 19)));
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static final class Instruction {

    final OpCode opcode;

    final int a;

    final int b;

    final int c;

    Instruction(final OpCode _opcode, final int _a, final int _b, final int _c) {
      opcode = _opcode;
      a = _a;
      b = _b;
      c = _c;
    }

    @Override
    public String toString() {
      return opcode + "[" + a + "," + b + "," + c + "]";
    }
  }

  private static final class State {

    private static final Pattern SPLIT = Pattern.compile(" ");

    int ip_reg;

    final int[] register = new int[6];

    final List<Instruction> instructions;

    State(final List<String> lines) {
      instructions = new ArrayList<>(lines.size() - 1);
      for (final String line : lines) {
        final String[] tokens = SPLIT.split(line);
        if ("#ip".equals(tokens[0])) {
          ip_reg = Integer.parseInt(tokens[1]);
        }
        else {
          final OpCode opcode = OpCode.valueOf(tokens[0]);
          instructions.add(new Instruction(opcode, Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
            Integer.parseInt(tokens[3])));
        }
      }
    }

    @Override
    public String toString() {
      return Arrays.toString(register);
    }
  }

}
