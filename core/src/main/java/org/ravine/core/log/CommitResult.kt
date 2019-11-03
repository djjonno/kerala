package org.ravine.core.log

import org.ravine.shared.annotations.Mockable

@Mockable
data class CommitResult<E : LogEntry>(val committed: List<E>, val commitIndex: Long)
