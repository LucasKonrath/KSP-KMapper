package me.tatocaster

import me.tatocaster.testClasses.DestinationRecordClass
import me.tatocaster.testClasses.EmptyConstructorClass
import org.kmapper.annotation.KMapperDefinition

@KMapperDefinition(from = EmptyConstructorClass::class, to = DestinationRecordClass::class)
interface TestClass {

}