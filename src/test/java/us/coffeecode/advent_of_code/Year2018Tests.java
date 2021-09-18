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
package us.coffeecode.advent_of_code;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.coffeecode.advent_of_code.y2018.*;

/**
 * <p>
 * Test harness that runs all of the individual day and part functions and validates their answers.
 * </p>
 * <p>
 * Copyright (c) 2021 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public class Year2018Tests {

  @Test
  public void year2018day01part1() {
    final long answer = new Year2018Day01().calculatePart1();
    assertEquals(540, answer);
  }

  @Test
  public void year2018day01part2() {
    final long answer = new Year2018Day01().calculatePart2();
    assertEquals(73_056, answer);
  }

  @Test
  public void year2018day02part1() {
    final long answer = new Year2018Day02().calculatePart1();
    assertEquals(6_888, answer);
  }

  @Test
  public void year2018day02part2() {
    final String answer = new Year2018Day02().calculatePart2();
    assertEquals("icxjvbrobtunlelzpdmfkahgs", answer);
  }

  @Test
  public void year2018day03part1() {
    final long answer = new Year2018Day03().calculatePart1();
    assertEquals(111_326, answer);
  }

  @Test
  public void year2018day03part2() {
    final long answer = new Year2018Day03().calculatePart2();
    assertEquals(1_019, answer);
  }

  @Test
  public void year2018day04part1() {
    final long answer = new Year2018Day04().calculatePart1();
    assertEquals(39_698, answer);
  }

  @Test
  public void year2018day04part2() {
    final long answer = new Year2018Day04().calculatePart2();
    assertEquals(14_920, answer);
  }

  @Test
  public void year2018day05part1() {
    final long answer = new Year2018Day05().calculatePart1();
    assertEquals(9_386, answer);
  }

  @Test
  public void year2018day05part2() {
    final long answer = new Year2018Day05().calculatePart2();
    assertEquals(4_876, answer);
  }

  @Test
  public void year2018day06part1() {
    final long answer = new Year2018Day06().calculatePart1();
    assertEquals(3_907, answer);
  }

  @Test
  public void year2018day06part2() {
    final long answer = new Year2018Day06().calculatePart2();
    assertEquals(42_036, answer);
  }

  @Test
  public void year2018day07part1() {
    final String answer = new Year2018Day07().calculatePart1();
    assertEquals("OKBNLPHCSVWAIRDGUZEFMXYTJQ", answer);
  }

  @Test
  public void year2018day07part2() {
    final long answer = new Year2018Day07().calculatePart2();
    assertEquals(982, answer);
  }

  @Test
  public void year2018day08part1() {
    final long answer = new Year2018Day08().calculatePart1();
    assertEquals(40_848, answer);
  }

  @Test
  public void year2018day08part2() {
    final long answer = new Year2018Day08().calculatePart2();
    assertEquals(34_466, answer);
  }

  @Test
  public void year2018day09part1() {
    final long answer = new Year2018Day09().calculatePart1();
    assertEquals(388_024, answer);
  }

  @Test
  public void year2018day09part2() {
    final long answer = new Year2018Day09().calculatePart2();
    assertEquals(3_180_929_875L, answer);
  }

  @Test
  public void year2018day10part1() {
    final String answer = new Year2018Day10().calculatePart1();
    assertEquals("RLEZNRAN", answer);
  }

  @Test
  public void year2018day10part2() {
    final long answer = new Year2018Day10().calculatePart2();
    assertEquals(10_240, answer);
  }

  @Test
  public void year2018day11part1() {
    final String answer = new Year2018Day11().calculatePart1();
    assertEquals("21,53", answer);
  }

  @Test
  public void year2018day11part2() {
    final String answer = new Year2018Day11().calculatePart2();
    assertEquals("233,250,12", answer);
  }

  @Test
  public void year2018day12part1() {
    final long answer = new Year2018Day12().calculatePart1();
    assertEquals(3_738, answer);
  }

  @Test
  public void year2018day12part2() {
    final long answer = new Year2018Day12().calculatePart2();
    assertEquals(3_900_000_002_467L, answer);
  }

  @Test
  public void year2018day13part1() {
    final String answer = new Year2018Day13().calculatePart1();
    assertEquals("26,99", answer);
  }

  @Test
  public void year2018day13part2() {
    final String answer = new Year2018Day13().calculatePart2();
    assertEquals("62,48", answer);
  }

  @Test
  public void year2018day14part1() {
    final long answer = new Year2018Day14().calculatePart1();
    assertEquals(5_715_102_879L, answer);
  }

  @Test
  public void year2018day14part2() {
    final long answer = new Year2018Day14().calculatePart2();
    assertEquals(20_225_706, answer);
  }

  @Test
  public void year2018day15part1() {
    final long answer = new Year2018Day15().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day15part1_2() {
    final long answer = new Year2018Day15().calculatePart1("secondRealInput");
    assertEquals(224_370, answer);
  }

  @Test
  public void year2018day15part1_3() {
    final long answer = new Year2018Day15().calculatePart1("thirdRealInput");
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day15part1_4() {
    final long answer = new Year2018Day15().calculatePart1("fourthRealInput");
    assertEquals(319_410, answer);
  }

  @Test
  public void year2018day15part1battle1() {
    final long answer = new Year2018Day15().calculatePart1("battle1");
    assertEquals(27_730, answer);
  }

  @Test
  public void year2018day15part1battle2() {
    final long answer = new Year2018Day15().calculatePart1("battle2");
    assertEquals(36_334, answer);
  }

  @Test
  public void year2018day15part1battle3() {
    final long answer = new Year2018Day15().calculatePart1("battle3");
    assertEquals(39_514, answer);
  }

  @Test
  public void year2018day15part1battle4() {
    final long answer = new Year2018Day15().calculatePart1("battle4");
    assertEquals(27_755, answer);
  }

  @Test
  public void year2018day15part1battle5() {
    final long answer = new Year2018Day15().calculatePart1("battle5");
    assertEquals(28_944, answer);
  }

  @Test
  public void year2018day15part1battle6() {
    final long answer = new Year2018Day15().calculatePart1("battle6");
    assertEquals(18_740, answer);
  }

  @Test
  public void year2018day15part1movement() {
    final long answer = new Year2018Day15().calculatePart1("movement");
    assertEquals(27_828, answer);
  }

  @Test
  public void year2018day15part1moveLeft() {
    final long answer = new Year2018Day15().calculatePart1("moveLeft");
    assertEquals(10_030, answer);
  }

  @Test
  public void year2018day15part1moveRight() {
    final long answer = new Year2018Day15().calculatePart1("moveRight");
    assertEquals(10_234, answer);
  }

  @Test
  public void year2018day15part1reddit1() {
    final long answer = new Year2018Day15().calculatePart1("reddit1");
    assertEquals(13_400, answer);
  }

  @Test
  public void year2018day15part1reddit2() {
    final long answer = new Year2018Day15().calculatePart1("reddit2");
    assertEquals(13_987, answer);
  }

  @Test
  public void year2018day15part1reddit3() {
    final long answer = new Year2018Day15().calculatePart1("reddit3");
    assertEquals(10_325, answer);
  }

  @Test
  public void year2018day15part1reddit4() {
    final long answer = new Year2018Day15().calculatePart1("reddit4");
    assertEquals(10_804, answer);
  }

  @Test
  public void year2018day15part1reddit5() {
    final long answer = new Year2018Day15().calculatePart1("reddit5");
    assertEquals(10_620, answer);
  }

  @Test
  public void year2018day15part1reddit6() {
    final long answer = new Year2018Day15().calculatePart1("reddit6");
    assertEquals(16_932, answer);
  }

  @Test
  public void year2018day15part1reddit7() {
    final long answer = new Year2018Day15().calculatePart1("reddit7");
    assertEquals(10_234, answer);
  }

  @Test
  public void year2018day15part1reddit8() {
    final long answer = new Year2018Day15().calculatePart1("reddit8");
    assertEquals(10_430, answer);
  }

  @Test
  public void year2018day15part1reddit9() {
    final long answer = new Year2018Day15().calculatePart1("reddit9");
    assertEquals(12_744, answer);
  }

  @Test
  public void year2018day15part1reddit10() {
    final long answer = new Year2018Day15().calculatePart1("reddit10");
    assertEquals(14_740, answer);
  }

  @Test
  public void year2018day15part1wall() {
    final long answer = new Year2018Day15().calculatePart1("wall");
    assertEquals(18_468, answer);
  }

  @Test
  public void year2018day15part2() {
    final long answer = new Year2018Day15().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day16part1() {
    final long answer = new Year2018Day16().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day16part2() {
    final long answer = new Year2018Day16().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day17part1() {
    final long answer = new Year2018Day17().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day17part2() {
    final long answer = new Year2018Day17().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day18part1() {
    final long answer = new Year2018Day18().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day18part2() {
    final long answer = new Year2018Day18().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day19part1() {
    final long answer = new Year2018Day19().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day19part2() {
    final long answer = new Year2018Day19().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day20part1() {
    final long answer = new Year2018Day20().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day20part2() {
    final long answer = new Year2018Day20().calculatePart2();
    assertEquals(499, answer);
  }

  @Test
  public void year2018day21part1() {
    final long answer = new Year2018Day21().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day21part2() {
    final long answer = new Year2018Day21().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day22part1() {
    final long answer = new Year2018Day22().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day22part2() {
    final long answer = new Year2018Day22().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day23part1() {
    final long answer = new Year2018Day23().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day23part2() {
    final long answer = new Year2018Day23().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day24part1() {
    final long answer = new Year2018Day24().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day24part2() {
    final long answer = new Year2018Day24().calculatePart2();
    assertEquals(Long.MIN_VALUE, answer);
  }

  @Test
  public void year2018day25part1() {
    final long answer = new Year2018Day25().calculatePart1();
    assertEquals(Long.MIN_VALUE, answer);
  }

}
