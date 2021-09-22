/* Advent of Code answers written by John Gaughan
 * Copyright (C) 2020  John Gaughan
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
package us.coffeecode.advent_of_code.y2018.opcode;

/**
 * <p>
 * OpCodes used in multiple days' puzzles. These model instructions in a fictional CPU.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public enum OpCode {

  addr() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] + registers[b];
    }
  },

  addi() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] + b;
    }

  },

  mulr() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] * registers[b];
    }
  },

  muli() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] * b;
    }
  },

  banr() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] & registers[b];
    }
  },

  bani() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] & b;
    }
  },

  borr() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] | registers[b];
    }
  },

  bori() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] | b;
    }
  },

  setr() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a];
    }
  },

  seti() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = a;
    }
  },

  gtir() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = a > registers[b] ? 1 : 0;
    }
  },

  gtri() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] > b ? 1 : 0;
    }
  },

  gtrr() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] > registers[b] ? 1 : 0;
    }
  },

  eqir() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = a == registers[b] ? 1 : 0;
    }
  },

  eqri() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] == b ? 1 : 0;
    }
  },

  eqrr() {

    @Override
    public void apply(final int[] registers, final int a, final int b, final int c) {
      registers[c] = registers[a] == registers[b] ? 1 : 0;
    }
  };

  /** Apply the action of this opcode against the provided registers using the provided parameters. */
  public abstract void apply(final int[] registers, final int a, final int b, final int c);

}
