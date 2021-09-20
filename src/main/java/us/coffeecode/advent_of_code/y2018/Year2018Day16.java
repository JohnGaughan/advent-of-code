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
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/16">Year 2018, day 16</a>. This is another virtual instruction set. Part
 * one is a test of parsing the input and handling it correctly. Part two builds on this by further processing the input
 * then running a program.
 * </p>
 * <p>
 * The bulk of the logic is in two places. First, the test method is responsible for taking a sample and testing each of
 * the opcodes against that data to see if it can produce the same result. There is a flag that alters the behavior for
 * parts one and two. In part one we simply count how many opcodes can produce that result. In part two, we assign the
 * opcode its numeric value if it is the only opcode that can produce that result. Only in part two, we also skip
 * opcodes that are already known so we can, on future passes through the sample data, further narrow down and assign
 * codes.
 * </p>
 * <p>
 * The opcode class stores the codes and holds an array where the index is the opcode's ID, 0-15. This is where we
 * assign opcodes to numeric codes. The static method can tell us if the opcodes are all assigned. In part two we loop
 * over the sample data until this condition is true, then execute the program and return the answer.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day16 {

  public long calculatePart1() {
    final Input input = getInput();
    long result = 0;
    for (final Sample sample : input.samples) {
      if (test(sample, false) >= 3) {
        ++result;
      }
    }
    return result;
  }

  public long calculatePart2() {
    final Input input = getInput();
    // Keep testing until all opcodes are assigned.
    while (!OpCode.allAssigned()) {
      for (final Sample sample : input.samples) {
        test(sample, true);
      }
    }

    // Now execute the program.
    final int[] registers = new int[] { 0, 0, 0, 0 };
    for (final int[] instruction : input.instructions) {
      OpCode.BY_CODE[instruction[0]].apply(registers, instruction[1], instruction[2], instruction[3]);
    }
    return registers[0];
  }

  /** Test a sample to see if three or more opcodes would have the same effect. */
  private int test(final Sample sample, final boolean partTwo) {
    int count = 0;
    OpCode toAssign = null;
    for (final OpCode opCode : OpCode.OPCODES) {
      if (partTwo && opCode.hasCode()) {
        continue;
      }
      final int[] registers = Arrays.copyOf(sample.registersBefore, sample.registersBefore.length);
      opCode.apply(registers, sample.instruction[1], sample.instruction[2], sample.instruction[3]);
      if (Arrays.equals(registers, sample.registersAfter)) {
        ++count;
        toAssign = opCode;
      }
    }
    if (partTwo && count == 1 && toAssign != null) {
      toAssign.setCode(sample.instruction[0]);
    }
    return count;
  }

  /** Get the input data for this solution. */
  private Input getInput() {
    try {
      return new Input(Files.readAllLines(Utils.getInput(2018, 16)));
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static abstract class OpCode {

    static final Collection<OpCode> OPCODES = new ArrayList<>(16);

    static final OpCode[] BY_CODE = new OpCode[16];

    static boolean allAssigned() {
      for (int i = 0; i < BY_CODE.length; ++i) {
        if (BY_CODE[i] == null) {
          return false;
        }
      }
      return true;
    }

    static final OpCode addr = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] + registers[b];
      }
    };

    static final OpCode addi = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] + b;
      }
    };

    static final OpCode mulr = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] * registers[b];
      }
    };

    static final OpCode muli = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] * b;
      }
    };

    static final OpCode banr = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] & registers[b];
      }
    };

    static final OpCode bani = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] & b;
      }
    };

    static final OpCode borr = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] | registers[b];
      }
    };

    static final OpCode bori = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] | b;
      }
    };

    static final OpCode setr = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a];
      }
    };

    static final OpCode seti = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = a;
      }
    };

    static final OpCode gtir = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = a > registers[b] ? 1 : 0;
      }
    };

    static final OpCode gtri = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] > b ? 1 : 0;
      }
    };

    static final OpCode gtrr = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] > registers[b] ? 1 : 0;
      }
    };

    static final OpCode eqir = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = a == registers[b] ? 1 : 0;
      }
    };

    static final OpCode eqri = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] == b ? 1 : 0;
      }
    };

    static final OpCode eqrr = new OpCode() {

      @Override
      void apply(final int[] registers, final int a, final int b, final int c) {
        registers[c] = registers[a] == registers[b] ? 1 : 0;
      }
    };

    OpCode() {
      OPCODES.add(this);
    }

    abstract void apply(final int[] registers, final int a, final int b, final int c);

    void setCode(final int code) {
      BY_CODE[code] = this;
    }

    boolean hasCode() {
      for (final OpCode opCode : BY_CODE) {
        if (this == opCode) {
          return true;
        }
      }
      return false;
    }
  }

  /** Sample data in the first part of the input file. */
  private static final class Sample {

    final int[] registersBefore;

    final int[] instruction;

    final int[] registersAfter;

    Sample(final int[] _registersBefore, final int[] _instruction, final int[] _registersAfter) {
      registersBefore = _registersBefore;
      instruction = _instruction;
      registersAfter = _registersAfter;
    }
  }

  /** Contains all of the data in the input file. */
  private static final class Input {

    private static final Pattern INSTRUCTION_SPLIT = Pattern.compile(" ");

    private static final Pattern SAMPLE_SPLIT = Pattern.compile(", ");

    private static int[] make(final String line, final Pattern split) {
      final String[] strs = split.split(line);
      return Arrays.stream(strs).mapToInt(Integer::parseInt).toArray();
    }

    final List<Sample> samples = new ArrayList<>(780);

    final List<int[]> instructions;

    Input(final List<String> input) {
      // Handle four lines at a time, reading in the input for part 1.
      int idx = 0;
      for (; input.get(idx).startsWith("Before"); idx += 4) {
        String s0 = input.get(idx).substring(9);
        s0 = s0.substring(0, s0.length() - 1);
        String s1 = input.get(idx + 1);
        String s2 = input.get(idx + 2).substring(9);
        s2 = s2.substring(0, s2.length() - 1);
        samples.add(new Sample(make(s0, SAMPLE_SPLIT), make(s1, INSTRUCTION_SPLIT), make(s2, SAMPLE_SPLIT)));
      }

      // Consume blank lines
      while (input.get(idx).isBlank()) {
        ++idx;
      }

      // Read in the instructions for part 2.
      instructions = new ArrayList<>(input.size() - idx + 1);
      for (; idx < input.size(); ++idx) {
        instructions.add(make(input.get(idx), INSTRUCTION_SPLIT));
      }
    }
  }

}
