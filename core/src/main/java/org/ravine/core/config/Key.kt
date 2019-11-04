package org.ravine.core.config

@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Key(val defaultValue: String = "")
