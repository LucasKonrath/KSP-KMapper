package me.tatocaster

import me.tatocaster.testClasses.EmptyConstructorClass
import org.kmapper.generated.KClassImplToDestinationRecordClassKMapper

fun main() {
    println("start")
    val from = EmptyConstructorClass()
    from.testString = "Test"
    from.testInt = 10
    val to = KClassImplToDestinationRecordClassKMapper(from).map()
    println("$to")
}