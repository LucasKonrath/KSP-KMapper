package me.tatocaster

import org.kmapper.annotation.KMapperDefinition
import org.kmapper.testClasses.DestinationRecordClass
import org.kmapper.testClasses.OriginalClass

@KMapperDefinition(from = OriginalClass::class, to = DestinationRecordClass::class)
class TestKMapper {

}