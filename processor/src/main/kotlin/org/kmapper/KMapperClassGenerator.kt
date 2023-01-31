package org.kmapper

import com.squareup.kotlinpoet.*
import org.kmapper.converters.Converters
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.*

class KMapperClassGenerator {

    val converters = Converters()

    /**
     *
     * Function called to create a class for mapping between the two specified classes.
     * The function introspects the classes via reflection, then generates a mapping class which instantiates an instance
     * of to based on an instance of from. This is done by generating the biggest constructor available of the destination
     * class, and then populating the remaining values via setters.
     *
     * For inserting each value on the destination class, a Converter is used. Converters are used to convert between
     * two data types, and they are registered on the Converters.kt file. If you wish to expand the current converters,
     * or create custom ones, you can add them on that file.
     */
    fun <T : Any, U: Any> map(from: KClass<T>, toCls: KClass<U>): FileSpec {

        val emptyConstructor: Boolean? = toCls.primaryConstructor?.parameters?.isEmpty()
        val nameMappings = mutableMapOf<String?, String?>()

        // Generate the base file spec, and insert the interface on it so that we can later use the .map() method.
        val fileSpec = FileSpec.builder("org.kmapper.generated", getClaszName(from, toCls))
        val clasz = TypeSpec.classBuilder(getClaszName(from, toCls))
        clasz.addSuperinterface(IKMapper::class)

        val constructor = FunSpec.constructorBuilder()
        constructor.addParameter("from", from)

        // Map the values with their field name or with the destination field name, if defined on the KMappedField annotation.
        from.declaredMemberProperties
            .forEach { m ->
                val ann: KMappedField? = m.findAnnotation<KMappedField>()
                val nameToPut = ann?.destinationField ?: m.name
                nameMappings[nameToPut] = m.name
            }


        val constructClass = FunSpec.builder("constructClass")
            .returns(toCls)

        // If it is an empty constructor, instantiate the class with it.
        if (emptyConstructor == true) {
            constructClass
                .addStatement("return %T()", toCls)
        } else {

            // If it isn't an empty constructor, instantiate the class with the primary constructor,
            // converting each value using the converters class.
            val accessors = toCls.primaryConstructor?.parameters?.map {
             param ->
             "convert(from." + nameMappings[param.name] + "!!, " + "from." + nameMappings[param.name] + "!!::class, " + param.type.toString().replace("?", "") +"::class) as " + param.type.toString().replace("?", "")   }

            constructClass.addStatement("return %L( %L )", toCls.simpleName!!, accessors!!.joinToString(", "))

        }

        // Write the constructClass function we defined above to the clasz definition.
        clasz.addFunction(constructClass.build())

        // Define the origin class as the constructor for the Mapper.
        clasz.primaryConstructor(constructor.build())
            .addProperty(PropertySpec.builder(
                "from", from)
                .initializer("from")
                .addModifiers(KModifier.PRIVATE)
                .build())


        // Override the IKMapper method to return an instance of toCls. Initialize the return with the constructor defined above.
        val mappingMethod = FunSpec.builder("map")
                .returns(toCls)
            .addModifiers(KModifier.OVERRIDE)
                .addStatement("val to = constructClass()")

        // For each remaining field which is mutable (and thus wasn't called on the above constructor) add a setter.
        toCls.declaredMemberProperties.forEach { m ->
            if (m is KMutableProperty<*>) {
                mappingMethod.addStatement("to.%L = convert(from.%L, from.%L!!::class, %L::class) as %L",  m.name, nameMappings[m.name]!!, nameMappings[m.name]!!,
                    m.returnType!!.toString().replace("?", "")
                    , m.returnType)
            }
        }

        // Return the populated object.
        mappingMethod.addStatement("return to")
        // Add the above defined mapping function to the class.
        clasz.addFunction(mappingMethod.build())

        // Add the conversion function to convert between data types.
        val convertFunction = FunSpec.builder("convert")
            .returns(Any::class)
            .addParameter("from", Any::class)
            .addParameter("typeClassifier", KClassifier::class)
            .addParameter("toClassifier", KClassifier::class)
            .addStatement("return %T().getConverter(typeClassifier, toClassifier)?.invoke(from)!!", Converters::class)

        clasz.addFunction(convertFunction.build())

        fileSpec.addType(clasz.build())
        return fileSpec.build()
    }

    fun getClaszName(to: Any, cls: KClass<*>): String {
        val toClass = to::class.simpleName
        val clsName = cls.simpleName
        return String.format("%sTo%sKMapper", toClass, clsName)
    }
}