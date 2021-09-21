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
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/17">Year 2018, day 17</a>. This puzzle asks us to simulate what is
 * essentially pouring water into a series of cups, then calculating how much water they hold. Part one wants the total
 * water, including water at rest inside cups as well as water flowing over and around them: part two wants only the
 * water at rest.
 * </p>
 * <p>
 * This was an interesting problem with some surprisingly tricky edge cases to get the water flow correct. Water fills
 * up, requiring moving up as the puzzle progresses. Water can spill over one or both sides of a cup, sometimes into the
 * same cup, sometimes into different cups. The approach I settled on that seems to work is to use a stack: this means
 * backtracking up a cup while it fills is a built-in feature that needs no special handling. Filling one square at a
 * time was far too error prone. This solution alternates between two modes. First, water flows down until it hits
 * something. This is a trivial case. Second, water fills a row of a cup. We need to know if this row will spill over
 * the edge: if so, fill with water in motion which needs to fill one past the edge: add that hanging bit of water to
 * the stack so it can flow down. Otherwise, fill with water at rest.
 * </p>
 * <p>
 * Boundaries were not too bad to get right. We need a buffer of two grid columns on either side to ensure there is a
 * dead space that the algorithm can use. I also shrank the input horizontally for more convenient visualization.
 * Finally, the program specification requires that we only count water between the bounds of the first and last Y
 * coordinates of program input, inclusive.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day17 {

  public long calculatePart1() {
    final Tile[][] tiles = getInput();
    simulate(tiles);
    return score(tiles, false);
  }

  public long calculatePart2() {
    final Tile[][] tiles = getInput();
    simulate(tiles);
    return score(tiles, true);
  }

  private long score(final Tile[][] tiles, final boolean partTwo) {
    long score = 0;
    // Find the row with the first tile of clay: this is where we start counting.
    int start_y = Integer.MAX_VALUE;
    out: for (int y = 0; y < tiles.length; ++y) {
      for (int x = 0; x < tiles[y].length; ++x) {
        if (tiles[y][x] == Tile.CLAY) {
          start_y = y;
          break out;
        }
      }
    }

    // Find the row with the last tile of clay: this is where we end counting.
    int end_y = Integer.MIN_VALUE;
    out: for (int y = tiles.length - 1; y > 0; --y) {
      for (int x = 0; x < tiles[y].length; ++x) {
        if (tiles[y][x] == Tile.CLAY) {
          // Add one for < comparison later.
          end_y = y + 1;
          break out;
        }
      }
    }

    // Count the water tiles.
    for (int y = start_y; y < end_y; ++y) {
      for (final Tile tile : tiles[y]) {
        if (tile == Tile.WATER_AT_REST) {
          ++score;
        }
        else if (!partTwo && tile == Tile.WATER_IN_MOTION) {
          ++score;
        }
      }
    }
    return score;
  }

  private void simulate(final Tile[][] tiles) {
    // Start by adding water below the spring.
    int spring_x = 0;
    while (tiles[0][spring_x] != Tile.SPRING) {
      ++spring_x;
    }
    tiles[1][spring_x] = Tile.WATER_IN_MOTION;

    // Create a queue of tiles to update and add the base case: the tile under the spring.
    final Deque<Point> queue = new LinkedList<>();
    queue.addFirst(new Point(spring_x, 1));

    // As long as there are tiles left to update, keep updating them.
    while (!queue.isEmpty()) {
      final Point current = queue.pollFirst();
      if (current.y >= tiles.length - 1) {
        // Ignore updates beyond the bottom of the grid.
        continue;
      }
      final Point below = new Point(current.x, current.y + 1);

      if (current.get(tiles) == Tile.WATER_IN_MOTION) {

        // If this water in motion can flow further down, do so.
        if (below.get(tiles) == Tile.SAND) {
          below.set(tiles, Tile.WATER_IN_MOTION);
          // Come back and process the current tile again later, in case something below fills up.
          queue.addFirst(current);
          queue.addFirst(below);
        }

        // There is something below that can cause this layer to spread out.
        else if (below.get(tiles) == Tile.CLAY || below.get(tiles) == Tile.WATER_AT_REST) {

          // Will this layer overflow? If so, get the bounds of what to fill.
          int fillLeft = current.x;
          int fillRight = current.x;
          boolean overflowLeft = false;
          boolean overflowRight = false;
          for (int x = current.x; x > 0; --x) {
            // Go left until we hit clay to the side, or sand below.
            if (new Point(x - 1, current.y).get(tiles) == Tile.CLAY) {
              fillLeft = x;
              break;
            }
            else if (new Point(x, current.y + 1).get(tiles) == Tile.SAND) {
              fillLeft = x;
              overflowLeft = true;
              break;
            }
          }
          for (int x = current.x; x < tiles[current.y].length; ++x) {
            // Go right until we hit clay to the side, or sand below.
            if (new Point(x + 1, current.y).get(tiles) == Tile.CLAY) {
              fillRight = x;
              break;
            }
            else if (new Point(x, current.y + 1).get(tiles) == Tile.SAND) {
              fillRight = x;
              overflowRight = true;
              break;
            }
          }

          // If the current layer overflows, add the layer as water in motion.
          if (overflowLeft || overflowRight) {
            for (int x = fillLeft; x <= fillRight; ++x) {
              new Point(x, current.y).set(tiles, Tile.WATER_IN_MOTION);
            }
            if (overflowLeft) {
              queue.addFirst(new Point(fillLeft, current.y));
            }
            if (overflowRight) {
              queue.addFirst(new Point(fillRight, current.y));
            }
          }

          // Otherwise, add the layer as water at rest.
          else {
            for (int x = fillLeft; x <= fillRight; ++x) {
              new Point(x, current.y).set(tiles, Tile.WATER_AT_REST);
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private String toString(final Tile[][] tiles, final int max_y) {
    final StringBuilder str = new StringBuilder(tiles.length * (tiles[0].length + 2));
    for (int y = 0; y < tiles.length && y < max_y; ++y) {
      for (final Tile tile : tiles[y]) {
        str.append(tile);
      }
      str.append('\n');
    }
    return str.toString();
  }

  /** Get the input data for this solution. */
  private Tile[][] getInput() {
    try {
      List<String> lines = Files.readAllLines(Utils.getInput(2018, 17));

      // Parse lines into something more usable. First array is 0=x, 1=y. Inner array is either [value] or [min, max]
      final int[][][] clayLocations = new int[lines.size()][2][];
      for (int i = 0; i < clayLocations.length; ++i) {
        final String line = lines.get(i);
        boolean xFirst = line.charAt(0) == 'x';
        int n1 = Integer.parseInt(line.substring(2, line.indexOf(',')));
        int rangeStart = 1 + line.indexOf('=', line.indexOf(','));
        int rangeSeparator = line.indexOf("..");
        int n2 = Integer.parseInt(line.substring(rangeStart, rangeSeparator));
        int n3 = Integer.parseInt(line.substring(rangeSeparator + 2));
        if (xFirst) {
          clayLocations[i][0] = new int[1];
          clayLocations[i][1] = new int[2];
          clayLocations[i][0][0] = n1;
          clayLocations[i][1][0] = n2;
          clayLocations[i][1][1] = n3;
        }
        else {
          clayLocations[i][0] = new int[2];
          clayLocations[i][1] = new int[1];
          clayLocations[i][1][0] = n1;
          clayLocations[i][0][0] = n2;
          clayLocations[i][0][1] = n3;
        }
      }

      // Find the maximum x and y values. X starts at 500, the location of the spring.
      int xMin = 500;
      int xMax = 500;
      int yMax = 0;
      for (final int[][] locations : clayLocations) {
        // Start with the first element of each array
        xMin = Math.min(xMin, locations[0][0]);
        xMax = Math.max(xMax, locations[0][0]);
        yMax = Math.max(yMax, locations[1][0]);

        // If there is a second element, factor that in, too.
        if (locations[0].length > 1) {
          xMin = Math.min(xMin, locations[0][1]);
          xMax = Math.max(xMax, locations[0][1]);
        }
        else {
          yMax = Math.max(yMax, locations[1][1]);
        }
      }

      // Increment both maximums to account for off-by-one, and an additional unit of X for spill-over off the edges
      xMin -= 2;
      xMax += 2;
      ++yMax;

      // Allocate the tile array and populate with default values
      final Tile[][] tiles = new Tile[yMax][xMax - xMin];
      for (int y = 0; y < tiles.length; ++y) {
        for (int x = 0; x < tiles[y].length; ++x) {
          tiles[y][x] = Tile.SAND;
        }
      }

      // Populate the tile array with locations of clay.
      for (final int[][] locations : clayLocations) {
        if (locations[0].length == 1) {
          for (int y = locations[1][0]; y <= locations[1][1]; ++y) {
            tiles[y][locations[0][0] - xMin] = Tile.CLAY;
          }
        }
        else {
          for (int x = locations[0][0]; x <= locations[0][1]; ++x) {
            tiles[locations[1][0]][x - xMin] = Tile.CLAY;
          }
        }
      }

      // Add the spring
      tiles[0][500 - xMin] = Tile.SPRING;

      return tiles;
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static final class Point {

    final int x;

    final int y;

    Point(final int _x, final int _y) {
      x = _x;
      y = _y;
    }

    Tile get(final Tile[][] tiles) {
      return tiles[y][x];
    }

    void set(final Tile[][] tiles, final Tile tile) {
      tiles[y][x] = tile;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof Point) {
        final Point p = (Point) obj;
        return p.x == x && p.y == y;
      }
      return false;
    }

    @Override
    public int hashCode() {
      return (y << 16) + x;
    }

    @Override
    public String toString() {
      return "(" + x + "," + y + ")";
    }
  }

  private static enum Tile {

    CLAY("#"),
    SAND("."),
    SPRING("+"),
    WATER_IN_MOTION("|"),
    WATER_AT_REST("~");

    final String str;

    Tile(final String _str) {
      str = _str;
    }

    @Override
    public String toString() {
      return str;
    }

  }

}
