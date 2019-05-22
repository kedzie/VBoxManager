package com.kedzie.vbox.soap

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KsoapProxy(val methodIncludeStrategy: KsoapMethodStrategy = KsoapMethodStrategy.INCLUDE_ALL)

enum class KsoapMethodStrategy {
    INCLUDE_ALL, INCLUDE_ANNOTATED
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KsoapObject(val value: String, val namespace : String = "http://www.virtualbox.org")

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Cacheable(val value: String = "",
                           /** update the cached  */
                           val put: Boolean = true,
                           /** get from the cache  */
                           val get: Boolean = true)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Ksoap(
        val value: String = "",
        val namespace: String = "http://www.w3.org/2001/XMLSchema",
        val type: String = "",
        val prefix: String = "",
        val thisReference: String = "_this",
        val cacheable: Boolean = false)