package org.kmapper

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate

class KMapperDefinitionProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.kmapper.annotation.KMapperDefinition")

        val invalidSymbols = symbols.filter { !it.validate() }.toList()
        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(KMapperDefinitionVisitor(), Unit) }

        return invalidSymbols
    }

    inner class KMapperDefinitionVisitor : KSVisitorVoid() {
        @OptIn(KspExperimental::class)
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            logger.logging("KMapperDefinitionVisitor#visitClassDeclaration : {}", classDeclaration)
            val firstClass = (classDeclaration.annotations.toList().first().arguments.first().value as KSType).declaration.qualifiedName!!.asString()
            val firstClazz = Class.forName(firstClass).kotlin
            val secondClass = (classDeclaration.annotations.toList().first().arguments.get(1).value as KSType).declaration.qualifiedName!!.asString()
            val secondClazz = Class.forName(secondClass).kotlin
            val fileKotlinPoet = KMapperClassGenerator().map(firstClazz, secondClazz)
            fileKotlinPoet.writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
        }
    }
}