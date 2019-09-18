package org.elkd.core.runtime.client.consumer

import org.elkd.core.consensus.messages.Entry

interface Consumer {
  fun consume(index: Long, entry: Entry)
}
