package org.elkd.core.server.converters

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConverterRegistry(val converters: Set<Converter<*, *>>) {
  inline fun <reified T : Converter<*, *>> getConverter(): T {
    return converters.filterIsInstance<T>().first()
  }

  companion object {
    val delegate = object : ReadOnlyProperty<Any, ConverterRegistry> {
      override fun getValue(thisRef: Any, property: KProperty<*>) = instance
    }

    val instance: ConverterRegistry by lazy {
      ConverterRegistry(setOf<Converter<*, *>>(
          AppendEntriesConverters.ToRpcRequest(),
          AppendEntriesConverters.ToRpcResponse(),
          AppendEntriesConverters.FromRpcRequest(),
          AppendEntriesConverters.FromRpcResponse(),

          RequestVoteConverters.ToRpcRequest(),
          RequestVoteConverters.ToRpcResponse(),
          RequestVoteConverters.FromRpcRequest(),
          RequestVoteConverters.FromRpcResponse(),

          KVConverters.ToRpc(),
          KVConverters.FromRpc(),

          EntryConverters.ToRpc(),
          EntryConverters.FromRpc()
      ))
    }
  }
}
