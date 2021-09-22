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
package us.coffeecode.advent_of_code.y2017;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.regex.Pattern;

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2017/day/23">Year 2017, day 23</a>. This is another assembly code simulator,
 * similar to day 18. The instructions are a bit simpler, however. Part one asks how many times the virtual machine
 * executes a multiply instruction, while part two flips an input bit and asks for the value of a register when the
 * program completes. The caveat here is the program is extremely inefficient and will not finish in any reasonable
 * amount of time.
 * </p>
 * <p>
 * The key to part two is figuring out the algorithm and coming up with a better version. Manipulating it by hand, I
 * translated it into pseudocode. From here I simplified variables. For example, register g is only ever used to do math
 * to compare expressions to zero in a JNZ instruction. This means g can be elided into expressions used in conditional
 * statements. Next, I found that registers b and c were used as lower and upper bounds for a loop. As I simplified and
 * restructured, these variables actually survived to the final iteration and initialize with values pulled directly
 * from the program input.
 * </p>
 * <p>
 * From here, I worked from inner to outer loops to determine that the algorithm is a very simple primality check. It
 * actually counts composite numbers between b and c but does so in the most naive way possible: dividing by every
 * single number between two and the number. Zero optimizations. This is why the program will not complete in a
 * reasonable amount of time. Instead, sieve enough prime numbers for the algorithm and perform a binary search on each
 * number to test its primality. Testing thousands of numbers in part two takes more time than testing a few tens of
 * numbers in part one using the less efficient algorithm, even with the (admittedly, low) overhead of the sieve.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2017Day23 {

  public long calculatePart1() {
    final State state = new State();
    final Instruction[] instructions = getInput();
    long multiplies = 0;
    while (state.pointer >= 0 && state.pointer < instructions.length) {
      if (instructions[state.pointer].op == OpCode.mul) {
        ++multiplies;
      }
      instructions[state.pointer].op.apply(instructions[state.pointer], state);
    }
    return multiplies;
  }

  public long calculatePart2() {
    final Instruction[] input = getInput();
    final int b = input[4].arg2 * input[0].arg2 - input[5].arg2;
    final int c = b - input[7].arg2;

    int composites = 0;
    final int[] primes = Utils.getPrimesUpTo(c);
    for (int i = b; i <= c; i += 17) {
      if (Arrays.binarySearch(primes, i) < 0) {
        ++composites;
      }
    }

    return composites;
  }

  /** Get the input data for this solution. */
  private Instruction[] getInput() {
    try {
      return Files.readAllLines(Utils.getInput(2017, 23)).stream().map(Instruction::new).toArray(Instruction[]::new);
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /** Operation codes representing actions a processor can take. */
  private static enum OpCode {

    set {

      @Override
      public void apply(final Instruction instruction, final State state) {
        final long n2 = instruction.arg2isReg ? state.registers[instruction.arg2] : instruction.arg2;
        state.registers[instruction.arg1] = n2;
        ++state.pointer;
      }
    },
    sub {

      @Override
      public void apply(final Instruction instruction, final State state) {
        final long n2 = instruction.arg2isReg ? state.registers[instruction.arg2] : instruction.arg2;
        state.registers[instruction.arg1] -= n2;
        ++state.pointer;
      }
    },
    mul {

      @Override
      public void apply(final Instruction instruction, final State state) {
        final long n2 = instruction.arg2isReg ? state.registers[instruction.arg2] : instruction.arg2;
        state.registers[instruction.arg1] *= n2;
        ++state.pointer;
      }
    },
    jnz {

      @Override
      public void apply(final Instruction instruction, final State state) {
        final long n1 = instruction.arg1isReg ? state.registers[instruction.arg1] : instruction.arg1;
        final long n2 = instruction.arg2isReg ? state.registers[instruction.arg2] : instruction.arg2;
        if (n1 != 0) {
          state.pointer += n2;
        }
        else {
          ++state.pointer;
        }
      }
    };

    public abstract void apply(final Instruction instruction, final State state);
  }

  /** Represents the current state of a single program. */
  private static final class State {

    final long[] registers = new long[8];

    int pointer = 0;
  }

  /** Instructions are opcodes with operands/arguments. */
  private static final class Instruction {

    private static final Pattern SEPARATOR = Pattern.compile(" ");

    final OpCode op;

    final int arg1;

    final int arg2;

    final boolean arg1isReg;

    final boolean arg2isReg;

    Instruction(final String input) {
      final String[] tokens = SEPARATOR.split(input);
      op = OpCode.valueOf(tokens[0]);
      // First argument is always a register identified by a character.
      final int a1 = tokens[1].codePointAt(0);
      if (Character.isDigit(a1)) {
        arg1 = Integer.parseInt(tokens[1]);
        arg1isReg = false;
      }
      else {
        arg1 = tokens[1].codePointAt(0) - 'a';
        arg1isReg = true;
      }
      // Second argument might not exist, or could be either a character or integer.
      if (tokens.length < 3) {
        arg2 = 0;
        arg2isReg = false;
      }
      else if (tokens[2].length() == 1) {
        // Could be a single-digit integer or a register.
        final int ch = tokens[2].codePointAt(0);
        if (Character.isLetter(ch)) {
          arg2 = ch - 'a';
          arg2isReg = true;
        }
        else {
          arg2 = Integer.parseInt(tokens[2]);
          arg2isReg = false;
        }
      }
      else {
        // Cannot possibly a register, assume integer.
        arg2 = Integer.parseInt(tokens[2]);
        arg2isReg = false;
      }
    }

  }

}
