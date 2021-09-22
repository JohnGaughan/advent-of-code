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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/18">Year 2018, day 18</a>. This puzzle is a variation of Conway's Game of
 * Life, where a grid mutates itself based on adjacency rules. Part one asks us to iterate ten times then calculate a
 * score based on the grid state. Part two asks the same, except with one billion iterations.
 * </p>
 * <p>
 * This is a common theme in Advent of Code: do a thing and prove it works, then do the same thing a ridiculously large
 * number of times that is not computationally feasible. The underlying computations are trivially easy, but part two
 * adds in a requirement to look for repetition. In this case there is a lead-in of just over four hundred calculations
 * before the sequence settles in to a loop. The calculation code needs to look for a duplicate grid state, then figure
 * out not only the period of repetition but also where in that period the current state is. I use a linked hash map for
 * this purpose. The key is the hash code of the grid state, with the value being its score. Once we calculate all
 * possible values, all that is left is figuring out the parameters of the repetition then advancing through the linked
 * map, which iterates in insertion order, to retrieve the previously-calculated value.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day18 {

  private static final int OPEN = '.';

  private static final int TREE = '|';

  private static final int LUMBERYARD = '#';

  public long calculatePart1() {
    return calculate(10);
  }

  public long calculatePart2() {
    return calculate(1_000_000_000);
  }

  private int calculate(final int iterations) {
    int[][] grid = getInput();
    final Map<Integer, Integer> cache = new LinkedHashMap<>(1024);
    cache.put(Integer.valueOf(Arrays.deepHashCode(grid)), Integer.valueOf(score(grid)));
    boolean skipped = false;
    for (int i = 0; i < iterations; ++i) {
      grid = iterate(grid);
      if (!skipped) {
        final Integer hash = Integer.valueOf(Arrays.deepHashCode(grid));
        if (cache.containsKey(hash)) {
          // Figure out where the collision is in insertion order.
          int start = 0;
          final var iter = cache.entrySet().iterator();
          while (iter.hasNext()) {
            if (iter.next().getKey().equals(hash)) {
              break;
            }
            ++start;
          }
          // Get the cycle length, then figure out how many extra iterations to get the target value.
          final int cycleLength = cache.size() - start;
          final int residue = (iterations - i) % cycleLength - 2;
          for (int j = 0; j < residue; ++j) {
            iter.next();
          }
          return iter.next().getValue().intValue();
        }
        else {
          cache.put(hash, Integer.valueOf(score(grid)));
        }
      }
    }
    return score(grid);
  }

  private int[][] iterate(final int[][] grid) {
    final int[][] newGrid = new int[grid.length][];
    // Copy the grid so we only need to set changed elements.
    for (int y = 0; y < grid.length; ++y) {
      newGrid[y] = Arrays.copyOf(grid[y], grid[y].length);
    }

    for (int y = 0; y < newGrid.length; ++y) {
      for (int x = 0; x < newGrid[y].length; ++x) {
        if (grid[y][x] == OPEN && countNeighbors(grid, x, y, TREE) >= 3) {
          newGrid[y][x] = TREE;
        }
        else if (grid[y][x] == TREE && countNeighbors(grid, x, y, LUMBERYARD) >= 3) {
          newGrid[y][x] = LUMBERYARD;
        }
        else if (grid[y][x] == LUMBERYARD
          && (countNeighbors(grid, x, y, LUMBERYARD) < 1 || countNeighbors(grid, x, y, TREE) < 1)) {
            newGrid[y][x] = OPEN;
          }
      }
    }
    return newGrid;
  }

  private int countNeighbors(final int[][] grid, final int x, final int y, final int value) {
    int count = 0;
    final boolean hasLeft = x > 0;
    final boolean hasRight = x < grid[y].length - 1;
    final boolean hasUp = y > 0;
    final boolean hasDown = y < grid.length - 1;

    if (hasUp && hasLeft && grid[y - 1][x - 1] == value) {
      ++count;
    }
    if (hasUp && grid[y - 1][x] == value) {
      ++count;
    }
    if (hasUp && hasRight && grid[y - 1][x + 1] == value) {
      ++count;
    }

    if (hasLeft && grid[y][x - 1] == value) {
      ++count;
    }
    if (hasRight && grid[y][x + 1] == value) {
      ++count;
    }

    if (hasDown && hasLeft && grid[y + 1][x - 1] == value) {
      ++count;
    }
    if (hasDown && grid[y + 1][x] == value) {
      ++count;
    }
    if (hasDown && hasRight && grid[y + 1][x + 1] == value) {
      ++count;
    }

    return count;
  }

  private int score(final int[][] grid) {
    int trees = 0;
    int lumberyards = 0;
    for (int[] row : grid) {
      for (int element : row) {
        if (element == TREE) {
          ++trees;
        }
        else if (element == LUMBERYARD) {
          ++lumberyards;
        }
      }
    }
    return trees * lumberyards;
  }

  /** Get the input data for this solution. */
  private int[][] getInput() {
    try {
      return Files.readAllLines(Utils.getInput(2018, 18)).stream().map(s -> s.chars().toArray()).toArray(int[][]::new);
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @SuppressWarnings("unused")
  private String toString(final int[][] grid) {
    final StringBuilder str = new StringBuilder(grid.length * (grid[0].length + 1));
    for (final int[] row : grid) {
      str.append(Arrays.stream(row).mapToObj(Character::toString).collect(Collectors.joining())).append('\n');
    }
    return str.toString();
  }

}
