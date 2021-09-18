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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/13">Year 2018, day 13</a>. Today's puzzle presents a maze with carts
 * moving along tracks. Carts will eventually collide. Part one asks for the location of the first collision, while part
 * two asks for the location of the last cart remaining the instant after the final collision.
 * </p>
 * <p>
 * There are a few ways to approach this, and I took the simple one. Parse the input, and separate carts from the raw
 * data and track them separately. Use a priority queue to track carts, so they are sorted and processed in the correct
 * order. Each tick, drain the queue and process each cart. Each cart goes into a "processed" collection, which we add
 * back into the queue at the end of the tick. If there is a collision then the current cart is not added to the
 * processed collection, and all other carts in either collection are removed.
 * </p>
 * <p>
 * For part one, this is where it ends. Return the coordinates for the collision. For part two, keep going until there
 * is only one cart left. If the queue ever has a single element at the end of a tick, this is the success condition:
 * get the remaining cart and return its coordinates. Interestingly, my input has seventeen carts and there is never a
 * three-way or four-way collision. I expected this to be an edge condition the puzzle authors would throw in there to
 * break assumptions. The only collisions in my data were two carts.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day13 {

  public String calculatePart1() {
    return calculate(false);
  }

  public String calculatePart2() {
    return calculate(true);
  }

  private String calculate(final boolean partTwo) {
    final State state = getInput();
    while (!state.carts.isEmpty()) {
      final Collection<Cart> processedCarts = new ArrayList<>(state.carts.size());
      while (!state.carts.isEmpty()) {
        final Cart cart = state.carts.remove();

        // Move the cart forward one location.
        cart.x += cart.d.dx;
        cart.y += cart.d.dy;

        // Check for collision
        boolean collision = false;
        Iterator<Cart> iter = processedCarts.iterator();
        while (iter.hasNext()) {
          if (iter.next().collidesWith(cart)) {
            if (partTwo) {
              collision = true;
              iter.remove();
            }
            else {
              return cart.toString();
            }
          }
        }
        iter = state.carts.iterator();
        while (iter.hasNext()) {
          if (iter.next().collidesWith(cart)) {
            if (partTwo) {
              collision = true;
              iter.remove();
            }
            else {
              return cart.toString();
            }
          }
        }

        // No collision: update the current cart.
        if (!collision) {
          if (state.map[cart.y][cart.x] == '/' || state.map[cart.y][cart.x] == '\\') {
            cart.d = cart.d.corner(state.map[cart.y][cart.x]);
          }
          else if (state.map[cart.y][cart.x] == '+') {
            cart.d = cart.d.turn(cart.nextTurn);
            cart.nextTurn = cart.nextTurn.next();
          }
          processedCarts.add(cart);
        }
      }

      // Reset the carts back into the state and check for the exit condition of part two.
      state.carts.addAll(processedCarts);
      if (state.carts.size() == 1) {
        return state.carts.remove().toString();
      }
    }
    return "NO SOLUTION";
  }

  /** Get the input data for this solution. */
  private State getInput() {
    try {
      return new State(
        Files.readAllLines(Utils.getInput(2018, 13)).stream().map(s -> s.codePoints().toArray()).toArray(int[][]::new));
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static final class State {

    final int[][] map;

    final Queue<Cart> carts = new PriorityQueue<>(17);

    State(final int[][] _map) {
      map = _map;
      for (int y = 0; y < map.length; ++y) {
        for (int x = 0; x < map[y].length; ++x) {
          final Direction d = Direction.valueOf(map[y][x]);
          if (d != null) {
            map[y][x] = d == Direction.NORTH ? '|' : '-';
            carts.add(new Cart(x, y, d));
          }
        }
      }
    }

  }

  private static enum Direction {

    NORTH(0, -1, '^'),
    SOUTH(0, 1, 'v'),
    EAST(1, 0, '>'),
    WEST(-1, 0, '<');

    private static final Map<Direction, Map<Integer, Direction>> CORNERS = Map.of( //
      NORTH, Map.of(Integer.valueOf('\\'), WEST, Integer.valueOf('/'), EAST), //
      SOUTH, Map.of(Integer.valueOf('\\'), EAST, Integer.valueOf('/'), WEST), //
      EAST, Map.of(Integer.valueOf('\\'), SOUTH, Integer.valueOf('/'), NORTH), //
      WEST, Map.of(Integer.valueOf('\\'), NORTH, Integer.valueOf('/'), SOUTH));

    private static final Map<Direction, Map<Turn, Direction>> TURNS = Map.of( //
      NORTH, Map.of(Turn.LEFT, WEST, Turn.FORWARD, NORTH, Turn.RIGHT, EAST), //
      SOUTH, Map.of(Turn.LEFT, EAST, Turn.FORWARD, SOUTH, Turn.RIGHT, WEST), //
      EAST, Map.of(Turn.LEFT, NORTH, Turn.FORWARD, EAST, Turn.RIGHT, SOUTH), //
      WEST, Map.of(Turn.LEFT, SOUTH, Turn.FORWARD, WEST, Turn.RIGHT, NORTH));

    static Direction valueOf(final int codePoint) {
      for (final Direction d : values()) {
        if (d.ch == codePoint) {
          return d;
        }
      }
      return null;
    }

    final int dx;

    final int dy;

    final int ch;

    Direction(final int _dx, final int _dy, final int _ch) {
      dx = _dx;
      dy = _dy;
      ch = _ch;
    }

    Direction corner(final int c) {
      return CORNERS.get(this).get(Integer.valueOf(c));
    }

    Direction turn(final Turn turn) {
      return TURNS.get(this).get(turn);
    }
  }

  private static enum Turn {

    LEFT,
    FORWARD,
    RIGHT;

    private static final Map<Turn, Turn> TRANSITIONS = Map.of(LEFT, FORWARD, FORWARD, RIGHT, RIGHT, LEFT);

    Turn next() {
      return TRANSITIONS.get(this);
    }
  }

  private static final class Cart
  implements Comparable<Cart> {

    int x;

    int y;

    Direction d;

    Turn nextTurn = Turn.LEFT;

    Cart(final int _x, final int _y, final Direction _d) {
      x = _x;
      y = _y;
      d = _d;
    }

    boolean collidesWith(final Cart other) {
      return x == other.x && y == other.y;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final Cart o) {
      final int y_result = Integer.compare(y, o.y);
      if (y_result != 0) {
        return y_result;
      }
      return Integer.compare(x, o.x);
    }

    @Override
    public String toString() {
      return x + "," + y;
    }
  }

}
