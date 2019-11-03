@file:JvmName("algorithm")
package org.ravine.shared.util

fun <T : Number> Collection<T>.findMajority(): T? {
  val n = this.count()
  var maxCount = 0
  var index = -1 // sentinels

  this.forEachIndexed { i, _ ->
    var count = 0
    for (j in 0 until n) {
      if (this.elementAt(i) == this.elementAt(j)) count++
      if (count > maxCount) {
        maxCount = count
        index = i
      }
    }
  }

  // if maxCount is greater than n/2
  // return the corresponding element
  return if (maxCount > n / 2) {
    this.elementAt(index)
  } else {
    null
  }
}
