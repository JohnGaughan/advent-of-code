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
package us.coffeecode.advent_of_code;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * This utility class contains general-purpose code used by multiple problems.
 * </p>
 * <p>
 * Copyright (c) 2020 John Gaughan
 * </p>
 *
 * @author John Gaughan &lt;john@coffeecode.us&gt;
 */
public final class Utils {

  /** Gets the path containing input data for the given year and day. Used by unit tests. */
  public static Path getInput(final int year, final int day) {
    return Path.of("src", "main", "resources", Integer.toString(year),
      "day" + (day < 10 ? "0" + day : Integer.toString(day)) + ".txt");
  }

  /** Gets the path containing input data for the given year and day. Used by unit tests. */
  public static Path getInput(final int year, final int day, final String test) {
    return Path.of("src", "main", "resources", Integer.toString(year),
      "day" + (day < 10 ? "0" + day : Integer.toString(day)) + "-" + test + ".txt");
  }

  /**
   * Given an array of code points, convert to a string. Java's String class can construct a string from bytes and
   * characters, but not code points.
   */
  public static String codePointsToString(final List<Integer> codePoints) {
    final StringBuilder str = new StringBuilder(codePoints.size());
    for (final Integer codePoint : codePoints) {
      str.appendCodePoint(codePoint.intValue());
    }
    return str.toString();
  }

  /**
   * Given an array of code points, convert to a string. Java's String class can construct a string from bytes and
   * characters, but not code points.
   */
  public static String codePointsToString(final Integer[] codePoints) {
    final StringBuilder str = new StringBuilder(codePoints.length);
    for (final Integer codePoint : codePoints) {
      str.appendCodePoint(codePoint.intValue());
    }
    return str.toString();
  }

  /**
   * Given an array of code points, convert to a string. Java's String class can construct a string from bytes and
   * characters, but not code points.
   */
  public static String codePointsToString(final int[] codePoints) {
    final StringBuilder str = new StringBuilder(codePoints.length);
    for (final int codePoint : codePoints) {
      str.appendCodePoint(codePoint);
    }
    return str.toString();
  }

  /**
   * <p>
   * Given a list of strings, group them up based on spacing. A blank string marks the boundary between string groups.
   * </p>
   * <p>
   * The blank strings are not returned.
   * </p>
   *
   * @param lines the lines to group.
   * @return the lines grouped together.
   */
  public static List<List<String>> getLineGroups(final List<String> lines) {
    // 1. Split lines list into segments based on blank lines separating passports.
    final List<List<String>> lineGroups = new ArrayList<>();
    int start = -1;
    for (int i = 0; i < lines.size(); ++i) {
      final boolean currentLineHasData = !lines.get(i).isBlank();
      // Not currently inside data
      if (start < 0) {
        // Don't combine these if statements!
        if (currentLineHasData) {
          start = i;
        }
      }
      // Current inside passport data
      else if (!currentLineHasData) {
        lineGroups.add(lines.subList(start, i));
        start = -1;
      }
    }
    // See if there is dangling data: if so, add it
    if (start > 0) {
      lineGroups.add(lines.subList(start, lines.size()));
    }

    return lineGroups;
  }

  /**
   * Get all prime numbers equal to or less than the provided upper bound.
   *
   * @param max the value of the maximum allowed number
   * @return an array of all unique prime number equal to or less than the maximum value.
   */
  public static int[] getPrimesUpTo(final int max) {
    // Sieve omits 1 and all even numbers. Mark composites as true, since the default array element is false.
    final boolean[] sieve = new boolean[max >> 1];
    int numPrimes = 1;
    for (int i = 0; i < sieve.length; ++i) {
      int value = (i << 1) + 3;
      if (!sieve[i]) {
        ++numPrimes;
        for (int j = i + value; j < sieve.length; j += value) {
          sieve[j] = true;
        }
      }
    }
    final int[] primes = new int[numPrimes];
    primes[0] = 2;
    int j = 0;
    for (int i = 1; i < primes.length; ++i) {
      while (sieve[j]) {
        ++j;
      }
      primes[i] = (j << 1) + 3;
      ++j;
    }
    return primes;
  }

  /**
   * Calculate all of the permutations of the provided items. Each permutation will be stored in a list that conveys the
   * ordering of the items, with all permutations in a set that enforces uniqueness among orderings.
   *
   * @param <T> the type of item being processed.
   * @param items the items for which to find all permutations.
   * @return a set containing all unique permutations.
   */
  public static <T> Set<List<T>> permutations(final Collection<T> items) {
    if (items instanceof List) {
      return permutations((List<T>) items, new ArrayList<>());
    }
    return permutations(new ArrayList<>(items), new ArrayList<>());
  }

  private static <T> Set<List<T>> permutations(final List<T> items, final List<T> permutations) {
    final Set<List<T>> results = new HashSet<>();
    // Tail case
    if (items.isEmpty()) {
      results.add(new ArrayList<>(permutations));
    }
    // Recursive case
    else {
      for (int i = 0; i < items.size(); ++i) {
        final List<T> newLocations = new ArrayList<>();
        newLocations.addAll(items.subList(0, i));
        newLocations.addAll(items.subList(i + 1, items.size()));
        final List<T> newPermutations = new ArrayList<>(permutations);
        newPermutations.add(items.get(i));
        results.addAll(permutations(newLocations, newPermutations));
      }
    }
    return results;
  }

  public static byte[] md5(final String input) {
    return MD5.get().digest(input.getBytes(ASCII));
  }

  public static String md5ToHex(final String input) {
    final byte[] bytes = MD5.get().digest(input.getBytes(ASCII));
    return toHexString(bytes);
  }

  public static String toHexString(final byte[] bytes) {
    final char[] hex = new char[bytes.length << 1];
    for (int i = 0; i < bytes.length; ++i) {
      final int v = bytes[i] & 0xFF;
      hex[i << 1] = CHARS[v >>> 4];
      hex[(i << 1) + 1] = CHARS[v & 0x0F];
    }
    return new String(hex);
  }

  public static String toHexString(final int[] bytes) {
    final char[] hex = new char[bytes.length << 1];
    for (int i = 0; i < bytes.length; ++i) {
      final int v = bytes[i] & 0xFF;
      hex[i << 1] = CHARS[v >>> 4];
      hex[(i << 1) + 1] = CHARS[v & 0x0F];
    }
    return new String(hex);
  }

  private static final Charset ASCII = Charset.forName("US-ASCII");

  /** Hexadecimal character set. */
  private static final char[] CHARS = "0123456789abcdef".toCharArray();

  /** MD5 message digest. */
  private static final ThreadLocal<MessageDigest> MD5 = new ThreadLocal<>() {

    protected MessageDigest initialValue() {
      try {
        return MessageDigest.getInstance("MD5");
      }
      catch (final NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
      }
    }
  };

}
