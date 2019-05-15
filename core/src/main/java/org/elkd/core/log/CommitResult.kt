package org.elkd.core.log

import org.elkd.shared.annotations.Mockable

@Mockable
data class CommitResult<E : LogEntry>(val committed: List<E>, val commitIndex: Long)
