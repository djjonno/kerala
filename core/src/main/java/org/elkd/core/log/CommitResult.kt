package org.elkd.core.log

import org.elkd.shared.annotations.Mockable

@Mockable
data class CommitResult<T>(val committed: List<T>, val commitIndex: Long)
