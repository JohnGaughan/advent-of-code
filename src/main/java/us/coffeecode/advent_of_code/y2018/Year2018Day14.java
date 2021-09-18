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

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/14">Year 2018, day 14</a>. This is another circular buffer question where
 * we need to generate numbers in the buffer, then process them somehow. Part one asks us to generate enough of the
 * sequence to get the values at the provided offset from the start, while part two asks us to find the location of a
 * number in the sequence. The same input value is interpreted differently in the two parts.
 * </p>
 * <p>
 * Day 9 was a good use of a circular linked list, but this time, the performance was too poor. Instead, I opted for an
 * integer array. Fill up the array which needs to be sized large enough to have the necessary data, then process it.
 * For part one we generate the sequence then add up numbers at the provided offset. For part two we search through the
 * array for the search needle, stopping when found. Using an array instead of a circular linked list reduced the
 * execution time by an order of magnitude.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day14 {

  public long calculatePart1() {
    final int input = getInput();
    final int[] array = generate(input * 5 / 3);
    long answer = 0;
    for (int i = 0; i < 10; ++i) {
      answer *= 10;
      answer += array[input + i];
    }
    return answer;
  }

  public long calculatePart2() {
    final int[] array = generate(20_225_750);
    final int[] match = digits(getInput());

    for (int i = 0; i < array.length - match.length; ++i) {
      boolean matches = true;
      for (int j = 0; j < match.length; ++j) {
        if (array[i + j] != match[j]) {
          matches = false;
          break;
        }
      }
      if (matches) {
        return i;
      }
    }
    return -1;
  }

  private int[] generate(final int size) {
    final int[] array = new int[size];
    array[0] = 3;
    array[1] = 7;
    int used = 2;
    int first = 0;
    int second = 1;
    while (used < array.length) {
      final int newScore = array[first] + array[second];
      if (newScore > 9) {
        array[used] = newScore / 10;
        ++used;
        if (used < array.length) {
          array[used] = newScore % 10;
          ++used;
        }
      }
      else {
        array[used] = newScore;
        ++used;
      }
      first = (first + array[first] + 1) % used;
      second = (second + array[second] + 1) % used;
    }
    return array;
  }

  private int[] digits(final int input) {
    final String str = Integer.toString(input);
    final int[] digits = new int[str.length()];
    for (int i = 0; i < digits.length; ++i) {
      digits[i] = str.codePointAt(i) - '0';
    }
    return digits;
  }

  /** Get the input data for this solution. */
  private int getInput() {
    try {
      return Integer.parseInt(Files.readString(Utils.getInput(2018, 14)).trim());
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
