package org.kerala.core.consensus

import org.kerala.core.KeralaRuntimeException

sealed class RaftException(override val message: String) : KeralaRuntimeException(message)
class UnknownTopicException : RaftException("Unknown topic")
class ObsoleteTermException : RaftException("Request term is less than current term")
class EntryTermMismatch : RaftException("Log terms mismatch")
class NoPreviousEntryException : RaftException("No entry at prevLogIndex")

