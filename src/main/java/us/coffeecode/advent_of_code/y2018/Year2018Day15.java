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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import us.coffeecode.advent_of_code.Utils;

/**
 * <p>
 * <a href="https://adventofcode.com/2018/day/15">Year 2018, day 15</a>. The problem asks us to simulate a game where
 * elves and goblins attack each other using very specific rules. Part one asks for the result of the game expressed as
 * the total hit points of the surviving force multiplied by the number of rounds. Part two asks us to find the lowest
 * bonus to give to the elves to guarantee they win.
 * </p>
 * <p>
 * Right now, this solution does not find the answer. It passes all of the sample inputs given by the problem. It passes
 * all of the supplementary sample inputs located in
 * <a href="https://github.com/ShaneMcC/aoc-2018/tree/master/15/tests">this GitHub repository</a>. This includes several
 * tests from a reddit thread and some others of unknown origin. All of these pass: however, none of the real inputs
 * pass. I tracked down three other official inputs other than my own. Of the four real problem inputs, I have answers
 * for two of them. Despite passing all of the test inputs, this solution does not find the answer for any of the real
 * inputs. There must be an edge case my program misses that is not covered by any of the test inputs, or is covered by
 * somehow my program pulls a Homer and still gets the right answer despite going about it the wrong way.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Year2018Day15 {

  private static final ThreadLocal<NumberFormat> FORMAT = new ThreadLocal<>() {

    protected NumberFormat initialValue() {
      return new DecimalFormat("###,###");
    }
  };

  public long calculatePart1() {
    return calculatePart1Impl(null);
  }

  public long calculatePart1(final String test) {
    return calculatePart1Impl(test);
  }

  private long calculatePart1Impl(final String test) {
    final var state = getInput(test);
    int rounds = 0;
    while (true) {
      final Set<Actor> turnTaken = new HashSet<>();
      AttackResult lastAttack = null;
      for (int y = 0; y < state.Y; ++y) {
        for (int x = 0; x < state.X; ++x) {
          final Point location = new Point(x, y);
          final Actor actor = state.actorAt(location);
          if (actor != null && !turnTaken.contains(actor)) {
            lastAttack = takeTurn(state, location);
            turnTaken.add(actor);
          }
        }
      }
      // Only count a round as complete if every actor took a turn, or the last action resulted in a kill that completed
      // the game: that is, wiped out one force.
      if (state.isComplete()) {
        if (lastAttack == AttackResult.KILLED) {
          ++rounds;
        }
        break;
      }
      ++rounds;
    }
    int hp = 0;
    for (final Actor[] row : state.actors) {
      for (final Actor e : row) {
        if (e != null) {
          hp += e.hp;
        }
      }
    }
    final int result = rounds * hp;
    System.out.println("Input: " + test);
    System.out.println("Rounds: " + FORMAT.get().format(rounds));
    System.out.println("HPs: " + FORMAT.get().format(hp));
    System.out.println("Result: " + FORMAT.get().format(result));
    System.out.println();
    return result;
  }

  public long calculatePart2() {
    return 0;
  }

  private AttackResult takeTurn(final State state, final Point location) {
    // If the current unit starts next to an enemy, attack.
    AttackResult result = attack(state, location);
    if (result != AttackResult.NONE) {
      return result;
    }

    // Otherwise move, then attack.
    final Point newLocation = move(state, location);
    return attack(state, newLocation);
  }

  private AttackResult attack(final State state, final Point location) {
    final Actor me = state.actorAt(location);
    final Point enemyLocation = state.getAdjacentEnemy(location, me.force);
    if (enemyLocation != null) {
      final Actor enemy = state.actorAt(enemyLocation);
      enemy.hp -= me.ap;
      if (enemy.hp <= 0) {
        state.removeActor(enemyLocation);
        return AttackResult.KILLED;
      }
      return AttackResult.ATTACKED;
    }
    return AttackResult.NONE;
  }

  private Point move(final State state, final Point location) {
    final Actor me = state.actorAt(location);
    // 1. Create paths branching out from the current location until we find an enemy, or
    // there are no more paths to try. This produces zero or more shortest paths.
    Set<Path> paths = enumerateAllShortestPaths(state, location);

    // 2. Remove paths that do not end up adjacent to an enemy.
    paths = getPathsEndingAtAnEnemy(state, paths, me.force);

    // 3. Remove paths whose target is not first in reading order.
    paths = getPathsEndingFirstInReadingOrder(state, paths);

    // 4. Of the remaining paths' first steps, pick the step first in the reading order.
    final Point step = getFirstStepFirstInReadingOrder(state, paths);

    // 5. Move!
    if (step != null) {
      state.moveActor(location, step);
      return step;
    }
    return location;
  }

  private Set<Path> enumerateAllShortestPaths(final State state, final Point location) {
    final Actor me = state.actorAt(location);

    final Set<Point> visited = new HashSet<>();

    Set<Path> paths = new HashSet<>();
    // Populate the base case, one step from current location.
    for (final Point neighbor : location.neighbors()) {
      if (state.isOpen(neighbor)) {
        paths.add(new Path(neighbor));
      }
    }

    // Iterate over each path and add on to them until we find an enemy or there are no enemies to find.
    // Always finish the final iteration to find ties.
    while (!paths.isEmpty()) {
      for (final Path path : paths) {
        final Point enemy = state.getAdjacentEnemy(path.end(), me.force);
        if (enemy != null) {
          return paths;
        }
      }

      final Set<Path> newPaths = new HashSet<>();
      for (final Path path : paths) {
        final Point end = path.end();
        for (final Point neighbor : end.neighbors()) {
          // Neighbor must not be occupied by a wall or actor: we cannot have visited it already.
          if (state.isOpen(neighbor) && !visited.contains(neighbor)) {
            newPaths.add(new Path(path, neighbor));
            visited.add(neighbor);
          }
        }
      }
      paths = reduce(newPaths);
    }
    return paths;
  }

  private Set<Path> getPathsEndingAtAnEnemy(final State state, final Set<Path> paths, final Force myForce) {
    final Set<Path> result = new HashSet<>();
    for (final Path path : paths) {
      final Point end = path.end();
      for (final Point neighbor : end.neighbors()) {
        final Actor actor = state.actorAt(neighbor);
        if (actor != null && actor.force != myForce) {
          result.add(path);
          break;
        }
      }
    }
    return result;
  }

  private Set<Path> getPathsEndingFirstInReadingOrder(final State state, final Set<Path> paths) {
    final NavigableMap<Point, Set<Path>> result = new TreeMap<>();
    for (final Path path : paths) {
      final Point end = path.end();
      if (!result.containsKey(end)) {
        result.put(end, new HashSet<>());
      }
      result.get(end).add(path);
    }
    final var firstEntry = result.firstEntry();
    return firstEntry == null ? Collections.emptySet() : firstEntry.getValue();
  }

  private Point getFirstStepFirstInReadingOrder(final State state, final Set<Path> paths) {
    Point first = null;
    for (final Path path : paths) {
      final Point start = path.start();
      if (first == null || start != null && start.compareTo(first) < 0) {
        first = start;
      }
    }
    return first;
  }

  /**
   * Reduce redundant paths. If two paths end at the same coordinate, keep only the one with the lower first step per
   * reading order.
   */
  private Set<Path> reduce(final Set<Path> paths) {
    // Group paths based on their end points.
    final Map<Point, Set<Path>> endPointsToPaths = new HashMap<>();
    for (final Path path : paths) {
      final Point p = path.end();
      if (!endPointsToPaths.containsKey(p)) {
        endPointsToPaths.put(p, new HashSet<>());
      }
      endPointsToPaths.get(p).add(path);
    }
    // For each point, get the path that is first in reading order.
    final Set<Path> result = new HashSet<>();
    for (final Point endPoint : endPointsToPaths.keySet()) {
      Path path = null;
      for (final Path candidate : endPointsToPaths.get(endPoint)) {
        if (path == null || candidate.start().compareTo(path.start()) < 0) {
          path = candidate;
        }
      }
      result.add(path);
    }
    return result;
  }

  /** Get the input data for this solution. */
  private State getInput(final String test) {
    try {
      if (test == null || test.isBlank()) {
        return new State(Files.readAllLines(Utils.getInput(2018, 15)));
      }
      return new State(Files.readAllLines(Utils.getInput(2018, 15, test)));
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static enum AttackResult {
    ATTACKED,
    KILLED,
    NONE;
  }

  private static final class Path
  implements Comparable<Path> {

    final Point[] elements;

    Path(final Point first) {
      elements = new Point[] { first };
    }

    Path(final Path first, final Point next) {
      elements = new Point[first.elements.length + 1];
      System.arraycopy(first.elements, 0, elements, 0, first.elements.length);
      elements[first.elements.length] = next;
    }

    Point start() {
      return elements[0];
    }

    Point end() {
      return elements[elements.length - 1];
    }

    @Override
    public int compareTo(final Path o) {
      // Elements array always has at least one element. First compare last element - we select in reading order.
      final int compare = elements[elements.length - 1].compareTo(o.elements[o.elements.length - 1]);
      if (compare != 0) {
        return compare;
      }
      return elements[0].compareTo(o.elements[0]);
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      else if (!(obj instanceof Path)) {
        return false;
      }
      return Arrays.equals(elements, ((Path) obj).elements);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(elements);
    }

    @Override
    public String toString() {
      return Arrays.toString(elements);
    }

  }

  private static final class Point
  implements Comparable<Point> {

    final int x;

    final int y;

    Point(final int _x, final int _y) {
      x = _x;
      y = _y;
    }

    Point[] neighbors() {
      return new Point[] { delta(0, -1), delta(-1, 0), delta(1, 0), delta(0, 1) };
    }

    Point delta(final int dx, final int dy) {
      return new Point(x + dx, y + dy);
    }

    @Override
    public int compareTo(final Point o) {
      // This sorts by reading order. If one is on a different horizontal line (y coordinate), use that comparison.
      final int result = Integer.compare(y, o.y);
      if (result != 0) {
        return result;
      }
      // Same line: compare the X coordinate.
      return Integer.compare(x, o.x);
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      else if (!(obj instanceof Point)) {
        return false;
      }
      Point p = (Point) obj;
      return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
      return x + (y << 16);
    }

    @Override
    public String toString() {
      return "(" + x + "," + y + ")";
    }

  }

  private static enum Force {
    ELVES,
    GOBLINS;
  }

  private static final class Actor {

    int ap = 3;

    int hp = 200;

    final Force force;

    Actor(final Force _force) {
      force = _force;
    }

    @Override
    public boolean equals(final Object obj) {
      return super.equals(obj);
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }

    @Override
    public String toString() {
      return force.name().substring(0, 1);
    }

  }

  private static final class State {

    final int X;

    final int Y;

    final boolean[][] walls;

    final Actor[][] actors;

    State(final List<String> input) {
      Y = input.size();
      X = input.get(0).length();
      walls = new boolean[Y][X];
      actors = new Actor[Y][X];
      for (int y = 0; y < Y; ++y) {
        final String line = input.get(y);
        for (int x = 0; x < X; ++x) {
          final int ch = line.codePointAt(x);
          if (ch == '#') {
            walls[y][x] = true;
          }
          else if (ch == 'E') {
            actors[y][x] = new Actor(Force.ELVES);
          }
          else if (ch == 'G') {
            actors[y][x] = new Actor(Force.GOBLINS);
          }
        }
      }
    }

    /** Get the adjacent enemy with the fewest HP: in a tie, select first in reading order. */
    Point getAdjacentEnemy(final Point location, final Force me) {
      Point currentBest = null;
      for (final Point candidateLocation : location.neighbors()) {
        final Actor candidate = actors[candidateLocation.y][candidateLocation.x];
        // Found an enemy
        if (candidate != null && candidate.force != me) {
          // First enemy
          if (currentBest == null) {
            currentBest = candidateLocation;
          }
          // Already found one: see if this is a "better" enemy
          else {
            int currentBestHp = actors[currentBest.y][currentBest.x].hp;
            int candidateHp = actors[candidateLocation.y][candidateLocation.x].hp;
            if (candidateHp < currentBestHp) {
              currentBest = candidateLocation;
            }
          }
        }
      }
      return currentBest;
    }

    boolean isComplete() {
      boolean hasGoblin = false;
      boolean hasElf = false;
      for (final Actor[] row : actors) {
        for (final Actor actor : row) {
          if (actor != null) {
            if (actor.force == Force.ELVES) {
              hasElf = true;
            }
            else if (actor.force == Force.GOBLINS) {
              hasGoblin = true;
            }
            if (hasGoblin && hasElf) {
              return false;
            }
          }
        }
      }
      return true;
    }

    void moveActor(final Point from, final Point to) {
      if (actors[to.y][to.x] != null) {
        throw new IllegalArgumentException("Location " + to + " is already occupied");
      }
      actors[to.y][to.x] = actors[from.y][from.x];
      actors[from.y][from.x] = null;
    }

    Actor actorAt(final Point location) {
      return actors[location.y][location.x];
    }

    void removeActor(final Point location) {
      actors[location.y][location.x] = null;
    }

    boolean isOpen(final Point location) {
      return !walls[location.y][location.x] && actors[location.y][location.x] == null;
    }

    @Override
    public String toString() {
      final StringBuilder str = new StringBuilder(walls.length * (walls[0].length + 2));
      for (int y = 0; y < Y; ++y) {
        for (int x = 0; x < X; ++x) {
          if (walls[y][x]) {
            str.append('#');
          }
          else if (actors[y][x] == null) {
            str.append('.');
          }
          else {
            str.append(actors[y][x]);
          }
        }
        str.append('\n');
      }
      return str.toString();
    }

  }

}
