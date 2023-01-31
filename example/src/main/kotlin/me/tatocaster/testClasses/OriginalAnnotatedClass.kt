package me.tatocaster.testClasses

import org.kmapper.KMappedField

data class OriginalAnnotatedClass(
        @property:KMappedField(destinationField = "testString")
        val string: String,
        @property:KMappedField(destinationField = "testInt")
        val int: Int
    )