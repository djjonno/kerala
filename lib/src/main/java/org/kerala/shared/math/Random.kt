@file:JvmName("random")
package org.kerala.shared.math

import kotlin.random.Random

fun IntRange.random() =
    Random.nextInt((endInclusive + 1) - start) + start

/**
 * Find a random number +-deviation from the given point.
 *
 * e.g point: 500, dev: 0.2  is saying, give me a random point
 * 20% -+ of 500, e.g 489
 */
fun randomizeNumberPoint(point: Int, dev: Double): Int {
  val offset = (point * dev).toInt()
  return (point - offset..point + offset).random()
}
