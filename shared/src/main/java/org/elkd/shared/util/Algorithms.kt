@file:JvmName("algorithm")
package org.elkd.shared.util

fun findMajority(arr: List<Number>): Number? {
  val n = arr.size
  var maxCount = 0
  var index = -1 // sentinels

  arr.forEachIndexed { i, iVal ->
    var count = 0
    arr.forEachIndexed { _, jVal ->
      if (iVal === jVal) {
        count++
      }
    }
    if (count > maxCount) {
      maxCount = count
      index = i
    }
  }

  // if maxCount is greater than n/2
  // return the corresponding element
  return if (maxCount > n / 2) {
    arr[index]
  } else {
    null
  }
}
