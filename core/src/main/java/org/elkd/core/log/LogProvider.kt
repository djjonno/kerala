package org.elkd.core.log

import org.elkd.shared.annotations.Mockable

@Mockable
class LogProvider<E> constructor(val log: Log<E>){

  fun logCommandExecutor(): LogCommandExecutor<E> = LogCommandExecutor(log)

}
