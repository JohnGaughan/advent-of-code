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
import java.util.List;
import java.util.stream.IntStream;

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/12">Year 2018, day 12</a>. Similar to Conway's game of life except in one
 * dimension, today's puzzle has us run a simulation that alters which nodes are living and dead. We then calculate a
 * value based on which positions are alive. The two parts differ only in the number of iterations.
 * </p>
 * <p>
 * This is easy enough to model, but fifty billion iterations is not feasible to simulate in any reasonable amount of
 * time. Clearly there has to be a short cut. As it turns out, there is. We only care about the score at any given
 * point, not the actual configuration. After a little over 100 iterations, the score stabilizes and never changes. This
 * algorithm detects that situation and stops, using a direct calculation to get the answer since we know how many
 * iterations remain and what their score is.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day12 {

  public long calculatePart1() {
    return calculate(20);
  }

  public long calculatePart2() {
    return calculate(50_000_000_000L);
  }

  public long calculate(final long iterations) {
    final Input input = getInput();
    final int padding = 150;
    boolean[] state = new boolean[padding * 2 + input.start.length];
    System.arraycopy(input.start, 0, state, padding, input.start.length);

    // Track how the count changes over time.
    long previousCount = count(state, padding);
    long previousDiff = Long.MIN_VALUE;
    int consecutive = 0;

    for (long i = 1; i <= iterations; ++i) {
      state = generation(state, input.rules);

      // Compare how the count and difference changed.
      long thisCount = count(state, padding);
      long thisDiff = thisCount - previousCount;
      if (thisDiff == previousDiff) {
        ++consecutive;
        // Two consecutive equal differences is not enough: seems the magic number is three before it stays the same
        // forever.
        if (consecutive == 3) {
          // Found the equilibrium point: stop here and take a short cut.
          return thisCount + (iterations - i) * previousDiff;
        }
      }
      else {
        consecutive = 0;
      }
      previousDiff = thisDiff;
      previousCount = thisCount;
    }
    return count(state, padding);
  }

  long count(final boolean[] state, final int padding) {
    long sum = 0;
    for (int i = 0; i < state.length; ++i) {
      if (state[i]) {
        sum += i - padding;
      }
    }
    return sum;
  }

  private boolean[] generation(final boolean[] inputState, final Rule[] rules) {
    final boolean[] output = new boolean[inputState.length];
    for (int i = 2; i < output.length - 2; ++i) {
      rule: for (final Rule rule : rules) {
        for (int j = 0; j < rule.match.length; ++j) {
          if (rule.match[j] != inputState[i + j - (rule.match.length >> 1)]) {
            continue rule;
          }
        }
        output[i] = rule.result;
        break;
      }
    }
    return output;
  }

  /** Get the input data for this solution. */
  private Input getInput() {
    try {
      return new Input(Utils.getLineGroups(Files.readAllLines(Utils.getInput(2018, 12))));
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static final class Rule {

    final boolean[] match = new boolean[5];

    final boolean result;

    Rule(final String input) {
      for (int i = 0; i < match.length; ++i) {
        match[i] = input.charAt(i) == '#';
      }
      result = input.codePointAt(input.length() - 1) == '#';
    }
  }

  private static final class Input {

    final boolean[] start;

    final Rule[] rules;

    Input(final List<List<String>> groups) {
      final String s = groups.get(0).get(0).substring(15);
      start = new boolean[s.length()];
      IntStream.range(0, s.length()).forEach(i -> start[i] = s.codePointAt(i) == '#');
      rules = groups.get(1).stream().map(Rule::new).toArray(Rule[]::new);
    }

  }

}
