package org.kerala.core.log

import org.kerala.shared.annotations.Mockable

@Mockable
data class CommitResult<E : LogEntry>(val committed: List<E>, val commitIndex: Long)
