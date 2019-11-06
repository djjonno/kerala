package org.kerala.core.consensus.messages

/**
 * KV is the core, underlying data type that carries client data.
 * The log + consensus ensures that these KVs are replicated and persisted
 * to the log in the correct order.
 *
 * @param key Opaque field that contains a `key`
 * @param value Opaque field that contains a `value`
 *
 * A client, for example, could have a Topic of weather data that contains
 * sensor data emitted from IoT devices which could be located in various
 * locations. The contents of the stream could like something like:
 *
 * Topic(weather) [
 *     KV(seattle, Object{ temp: 13.3, precipitation: 15.0, humidity: 75.0, timestamp: 1570298469 },
 *     KV(brisbane, Object{ temp: 18.0, dewPoint: 0.0, humidity: 70.0, timestamp: 1570298529 },
 *     ...
 * ]
 *
 * The key is up to the client to determine what is suitable for the use case.
 * They may also choose to keep it null. Point is, the key/val fields are
 * entirely client driven and the platform does not inspect the fields at
 * all, it simply ensures they're delivered, in the correct order.
 */
data class KV(
    /* private to enforce opaqueness */
    val key: String,
    /* private to enforce opaqueness */
    val `val`: String
) {
  override fun toString(): String {
    return "KV($key, $`val`)"
  }
}
