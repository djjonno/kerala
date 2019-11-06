package org.kerala.core.server.converters

interface Converter<Source, Target> {
  fun convert(source: Source): Target
}
