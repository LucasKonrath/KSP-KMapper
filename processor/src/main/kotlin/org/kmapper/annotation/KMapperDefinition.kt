package org.kmapper.annotation

import kotlin.reflect.KClass

/**
 * Annotation used to mark the presence of a mapping method.
 * You can put this on top of any interface that It'll generate the mapper class for you.
 */
annotation class KMapperDefinition(val from: KClass<*>, val to: KClass<*>)
