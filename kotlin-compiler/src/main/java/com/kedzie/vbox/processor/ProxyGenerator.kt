package com.kedzie.vbox.processor

import com.google.auto.service.AutoService
import com.kedzie.vbox.processor.Util.*
import com.kedzie.vbox.soap.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.jvm.throws
import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.io.IOException
import java.lang.IllegalStateException
import java.util.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.ArrayType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.MemberName.Companion.member

/**
 * An annotation processor that reads Kotlin data classes and generates Moshi JsonAdapters for them.
 * This generates Kotlin code, and understands basic Kotlin language features like default values
 * and companion objects.
 *
 * The generated class will match the visibility of the given data class (i.e. if it's internal, the
 * adapter will also be internal).
 *
 * If you define a companion object, a jsonAdapter() extension function will be generated onto it.
 * If you don't want this though, you can use the runtime [JsonClass] factory implementation.
 */
@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class ProxyGenerator : KotlinAbstractProcessor(), KotlinMetadataUtils {

    private val remainingTypes = mutableListOf<TypeElement>()

    private val annotation = KsoapProxy::class.java

    private val roomEntities = mutableListOf<TypeSpec>()
    private val roomDaos = mutableListOf<TypeSpec>()

    override fun getSupportedAnnotationTypes() = setOf(annotation.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedOptions() = emptySet<String>()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        remainingTypes.addAll(findInjectedTypes(roundEnv))
        messager.printMessage(Diagnostic.Kind.NOTE, "types: $remainingTypes")
        val i = remainingTypes.iterator()
        while (i.hasNext()) {
            val injectedClass = createInjectedClass(i.next())
            // Verify that we have access to all types to be injected on this pass.
            val missingDependentClasses = !allTypesExist(injectedClass.methods)
            if (!missingDependentClasses) {
                try {
                    generateProxy(injectedClass)
                } catch (e: Throwable) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Code gen failed: $e", injectedClass.type)
                }

                i.remove()
            }
        }
        if (roundEnv.processingOver()) {
            if(!remainingTypes.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Could not find injection type required by $remainingTypes")
            }

            val entitiesStr = StringBuffer("entities = [")
            var isFirst = true
            for(entity in roomEntities) {
                if(!isFirst) {
                    entitiesStr.append(",")
                }
                isFirst = false
                entitiesStr.append("\n%N::class")
            }
            val dbSpec = TypeSpec.classBuilder("CacheDatabase")
                    .addModifiers(KModifier.ABSTRACT)
                    .superclass(ClassName("androidx.room", "RoomDatabase"))
                    .addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "Database"))
                            .addMember("version = 1")
                            .addMember(entitiesStr.append("]").toString(), *roomEntities.toTypedArray())
                            .build())

            for(dao in roomDaos) {
                dbSpec.addFunction(FunSpec.builder(dao.name!!)
                        .addModifiers(KModifier.ABSTRACT)
                        .returns(ClassName("com.kedzie.vbox.api", dao.name!!))
                        .build())
            }

            FileSpec.builder("com.kedzie.vbox.api", "CacheDatabase")
                    .addType(dbSpec.build())
                    .build()
                    .writeTo(filer)
        }
        return false
    }

    private fun findInjectedTypes(env: RoundEnvironment): Set<TypeElement> {
        // First gather the set of classes that have @Ksoap-annotated members.
        val injectedTypeNames = LinkedHashSet<TypeElement>()
        for (element in env.getElementsAnnotatedWith(KsoapProxy::class.java)) {
            if (element.kind == ElementKind.INTERFACE)
                injectedTypeNames.add(element as TypeElement)
        }
        return injectedTypeNames
    }

    /**
     * Return true if all element types are currently available in this code
     * generation pass. Unavailable types will be of kind [javax.lang.model.type.TypeKind.ERROR].
     */
    private fun allTypesExist(methods: Collection<ExecutableElement>): Boolean {
        for (method in methods) {
            for (parameter in method.parameters) {
                if (parameter.asType().kind == TypeKind.ERROR)
                    return false
            }
            if (method.returnType.kind == TypeKind.ERROR)
                return false
        }
        return true
    }

//    private fun createInjectedClass(injectedClassName: String): InjectedClass {
    private fun createInjectedClass(type: TypeElement): InjectedClass {
        val typeMetadata: KotlinMetadata? = type.kotlinMetadata
        if (typeMetadata !is KotlinClassMetadata) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR, "@KsoapProxy can't be applied to ${type}: must be a Kotlin class", type)
            throw IllegalStateException("@KsoapProxy can't be applied to ${type}: must be a Kotlin class")
        }
        val proto = typeMetadata.data.classProto
        val nameResolver = typeMetadata.data.nameResolver

        val methods = mutableListOf<ExecutableElement>()
        val cacheableFields = HashMap<String, ProtoBuf.Type>()

        val ksoapProxy = type.getAnnotation(KsoapProxy::class.java)

        for (member in type.enclosedElements) {
            if (member.kind == ElementKind.METHOD) {
                val method = member as ExecutableElement

                val ksoap = method.getAnnotation(Ksoap::class.java)

                typeMetadata.data.getFunctionOrNull(method)?. let {func ->
                    methods.add(method)
                    method.getAnnotation(Cacheable::class.java)?.let {
                        //cache field is based on annotation or method name
                        cacheableFields[if(it.value.isNotEmpty()) it.value else nameResolver.getString(func.name)] = func.returnType
                    }
                }
            }
        }
        return InjectedClass(type, typeMetadata.data, methods, cacheableFields)
    }

    internal data class InjectedClass(val type: TypeElement,
                                      val data: ClassData,
                                      val methods: List<ExecutableElement>,
                                      val cacheableFields: Map<String, ProtoBuf.Type>)

    private fun generateProxy(injected: InjectedClass) {
        val proto = injected.data.classProto
        val nameResolver = injected.data.nameResolver

        var typeName = nameResolver.getString(proto.fqName)
        val packageName = typeName.substring(0, typeName.lastIndexOf('/')).replace('/', '.')
        typeName = typeName.substring(typeName.lastIndexOf('/')+1)

        val proxyType = TypeSpec.classBuilder("${typeName}Proxy")
                .addModifiers(KModifier.OPEN)
                .addSuperinterface(ClassName(packageName, typeName))

        val primary = FunSpec.constructorBuilder()
                .addParameter("api", ClassName("com.kedzie.vbox.soap", "VBoxSvc"))
                .addParameter("database", ClassName("com.kedzie.vbox.api", "CacheDatabase"))
                .addParameter("idRef", String::class)

        if (injected.type.interfaces.isNotEmpty()) {
            var baseClass = "${proto.getSupertype(0).asTypeName(nameResolver, proto::getTypeParameter)}Proxy"
            baseClass = baseClass.substring(baseClass.lastIndexOf('.')+1)
            proxyType.superclass(ClassName("com.kedzie.vbox.api", baseClass))
            proxyType.addSuperclassConstructorParameter("api, database, idRef")
        } else {
            proxyType.addProperty(PropertySpec.builder("api", ClassName("com.kedzie.vbox.soap", "VBoxSvc"))
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("api")
                    .build())
            proxyType.addProperty(PropertySpec.builder("database", ClassName("com.kedzie.vbox.api", "CacheDatabase"))
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("database")
                    .build())
            proxyType.addProperty(PropertySpec.builder("idRef", String::class)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("idRef")
                    .build())
        }

        proxyType.primaryConstructor(primary.build())

        //ROOM Entity
        val daoType = TypeSpec.interfaceBuilder("${typeName}Dao")
                .addAnnotation(ClassName("androidx.room", "Dao"))

        val entityType = TypeSpec.classBuilder("${typeName}Entity")
                .addModifiers(KModifier.DATA)

        val entityPrimary = FunSpec.constructorBuilder()

        entityPrimary.addParameter("idRef", ClassName("kotlin", "String"))
        entityType.addProperty(PropertySpec.builder("idRef",
                ClassName("kotlin", "String"))
                .addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "PrimaryKey"))
                        .build())
                .initializer("idRef")
                .build())

        val fileSpec = FileSpec.builder(getPackage(injected.type).qualifiedName.toString(), "${typeName}Proxy")

        val foreignKeys = mutableListOf<AnnotationSpec>()
        val foreignKeysStr = StringBuffer("foreignKeys = [")

        var isFirst = true
        for((fieldName, fieldType) in injected.cacheableFields) {
            val fieldTypeName = fieldType.asTypeName(nameResolver, proto::getTypeParameter, false).copy(nullable = false)
            val type = elementUtils.getTypeElement(fieldTypeName.toString())

            val cacheType = if(type != null && typeUtils.isAssignable(type.asType(), elementUtils.getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
                ClassName("kotlin", "String")
            } else if (fieldTypeName is ParameterizedTypeName
                    && fieldTypeName.rawType == ClassName("androidx.lifecycle", "LiveData")) {
                fieldTypeName.typeArguments[0]
            } else {
                fieldType.asTypeName(nameResolver, proto::getTypeParameter, false)
            }

            entityPrimary.addParameter(fieldName, cacheType)
            val property = PropertySpec.builder(fieldName, cacheType)
                    .mutable()
                    .initializer(fieldName)

            if(type != null) {
                if (typeUtils.isAssignable(type.asType(), elementUtils.getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
                    if(!isFirst) {
                        foreignKeysStr.append(",")
                    }
                    isFirst = false
                    foreignKeysStr.append("\n%L")
                    foreignKeys.add(AnnotationSpec.builder(ClassName("androidx.room", "ForeignKey"))
                            .addMember("entity = %T::class", ClassName(packageName, "${typeName}Entity"))
                            .addMember("parentColumns = [%S]", "idRef")
                            .addMember("childColumns = [%S]", fieldName)
                            .addMember("onDelete = ForeignKey.CASCADE")
                            .build())

                    property.addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "ColumnInfo"))
                            .addMember("index = true")
                            .build())
                } else if(type.kind == ElementKind.ENUM) {
                    val adapterType = TypeSpec.classBuilder("${typeName}_${fieldName}_Adapter")

                    adapterType.addFunction(FunSpec.builder("toString")
                            .returns(ClassName("kotlin", "String").copy(nullable = true))
                            .addAnnotation(ClassName("androidx.room", "TypeConverter"))
                            .addParameter("value", cacheType)
                            .addStatement("return value?.name")
                            .build())

                    adapterType.addFunction(FunSpec.builder("toValue")
                            .returns(cacheType.copy(nullable = true))
                            .addAnnotation(ClassName("androidx.room", "TypeConverter"))
                            .addParameter("value", ClassName("kotlin", "String").copy(nullable = true))
                            .beginControlFlow("return value?.let")
                            .addStatement("%M(it)", MemberName(cacheType as ClassName, "valueOf"))
                            .endControlFlow()
                            .build())

                    fileSpec.addType(adapterType.build())

                    property.addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "TypeConverters"))
                            .addMember("%N::class", adapterType.build())
                            .build())
                }
            }

            entityType.addProperty(property.build())

            daoType.addFunction(FunSpec.builder("get${fieldName}")
                    .addModifiers(KModifier.ABSTRACT)
                    .returns((ClassName("androidx.lifecycle", "LiveData")).parameterizedBy(cacheType))
                    .addParameter("idRef", ClassName("kotlin", "String"))
                    .addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "Query"))
                            .addMember("%S", "select ${fieldName} from ${typeName} where idRef = :idRef")
                            .build())
                    .build())

            daoType.addFunction(FunSpec.builder("get${fieldName}Now")
                    .addModifiers(KModifier.ABSTRACT)
                    .addModifiers(KModifier.SUSPEND)
                    .returns(cacheType)
                    .addParameter("idRef", ClassName("kotlin", "String"))
                    .addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "Query"))
                            .addMember("%S", "select ${fieldName} from ${typeName} where idRef = :idRef")
                            .build())
                    .build())

            daoType.addFunction(FunSpec.builder("set${fieldName}")
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter("idRef", ClassName("kotlin", "String"))
                    .addParameter("value", cacheType)
                    .addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "Query"))
                            .addMember("%S", "update ${typeName} set ${fieldName} = :value where idRef = :idRef")
                            .build())
                    .build())
        }

        entityType.primaryConstructor(entityPrimary.build())

        entityType.addAnnotation(AnnotationSpec.builder(ClassName("androidx.room", "Entity"))
                .addMember("tableName = %S", typeName)
                .addMember(foreignKeysStr.append("]").toString(), *foreignKeys.toTypedArray())
                .build())


        fileSpec.addType(entityType.build().apply { roomEntities.add(this) })
        fileSpec.addType(daoType.build().apply { roomDaos.add(this) })

        for(method in injected.methods) {
            injected.data.getFunctionOrNull(method)?. let { func ->
                val returnTypeName = func.returnType.asTypeName(nameResolver, proto::getTypeParameter).copy(nullable = false)

                val spec = FunSpec.builder(method.simpleName.toString())
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(func.returnType.asTypeName(nameResolver, proto::getTypeParameter))


                if(func.isSuspend)
                    spec.addModifiers(KModifier.SUSPEND)

                for(param in func.valueParameterList) {
                    if(param.hasVarargElementType()) {
                        spec.addParameter(nameResolver.getString(param.name),
                                param.varargElementType.asTypeName(nameResolver, proto::getTypeParameter),
                                KModifier.VARARG)
                    } else {
                        spec.addParameter(nameResolver.getString(param.name),
                                param.type.asTypeName(nameResolver, proto::getTypeParameter))
                    }
                }

                val isLiveData = returnTypeName is ParameterizedTypeName
                        && returnTypeName.rawType == ClassName("androidx.lifecycle", "LiveData")

                if (isLiveData) {
                    spec.beginControlFlow("return %M", MemberName("androidx.lifecycle", "liveData"))
                }

                method.getAnnotation(Cacheable::class.java)?.takeIf { it.get }?.apply {
                    val returnType = elementUtils.getTypeElement(returnTypeName.toString())

                    if(isLiveData) {
                        if (returnType!=null && typeUtils.isAssignable(returnType.asType(), elementUtils.getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
                            spec.addStatement("emitSource(database.${typeName}Dao().get%L(idRef).%M( { ${returnTypeName}Proxy(api, database, it) } ))",
                                    MemberName("androidx.lifecycle.Transformations", "map"),
                                    if (value.isNotEmpty()) value else nameResolver.getString(func.name))
                        }
                        else {
                            spec.addStatement("emitSource(database.${typeName}Dao().get%L(idRef))",
                                    if (value.isNotEmpty()) value else nameResolver.getString(func.name))
                        }
                    }
                    else {
                        spec.beginControlFlow("database.${typeName}Dao().get%LNow(idRef)?.let",
                                if (value.isNotEmpty()) value else nameResolver.getString(func.name))

                        if (returnType!=null && typeUtils.isAssignable(returnType.asType(), elementUtils.getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
                            spec.addStatement("return ${returnTypeName}Proxy(api, database, it)")
                        } else {
                            spec.addStatement("return it")
                        }
                        spec.endControlFlow()
                    }
                }

                val ksoap = method.getAnnotation(Ksoap::class.java)
                        ?: injected.type.getAnnotation(Ksoap::class.java)

                //switch context
                spec.beginControlFlow("val (envelope, call) = %M(%M)",
                        MemberName("kotlinx.coroutines", "withContext"),
                        MemberName("kotlinx.coroutines.Dispatchers", "IO"))
                        .addStatement("val request = %T(%S, \"%L_%L\")",
                                ClassName("org.ksoap2.serialization", "SoapObject"),
                                "http://www.virtualbox.org/",
                                if (ksoap?.prefix != "") ksoap.prefix else typeName,
                                nameResolver.getString(func.name))

                if (ksoap?.thisReference != "")
                    spec.addStatement("request.addProperty(%S, idRef)", ksoap.thisReference)

                for (parameter in method.parameters) {
                    injected.data.getValueParameterOrNull(func, parameter)?.let { kparam ->

                        parameter.getAnnotation(Cacheable::class.java)?.takeIf { it.put }?.let {
                            spec.addStatement("database.${typeName}Dao().set%L(idRef, %L)",
                                    if (it.value.isNotEmpty()) it.value else nameResolver.getString(kparam.name),
                                    nameResolver.getString(kparam.name))
                        }

                        if (kparam.hasVarargElementType()) {
                            spec.beginControlFlow("for(element in %L)", nameResolver.getString(kparam.name))
                            marshalParameter(injected, spec, ksoap,
                                    kparam.varargElementType,
                                    elementUtils.getTypeElement(kparam.varargElementType.asTypeName(nameResolver, proto::getTypeParameter).toString()).asType(),
                                    nameResolver.getString(kparam.name),
                                    "element")
                            spec.endControlFlow()
                        } else {
                            marshalParameter(injected, spec, parameter.getAnnotation(Ksoap::class.java),
                                    kparam.type,
                                    parameter.asType(),
                                    nameResolver.getString(kparam.name),
                                    nameResolver.getString(kparam.name))
                        }
                    }
                }

                spec.addStatement("val envelope = %T(%T.VER11).setAddAdornments(false).setOutputSoapObject(request)",
                        ClassName("org.ksoap2.serialization", "SoapSerializationEnvelope"),
                        ClassName("org.ksoap2", "SoapEnvelope"))

                spec.addStatement("%T(envelope, api.soapCall(%M+request.getName(), envelope))",
                        ClassName("kotlin", "Pair"),
                        MemberName("com.kedzie.vbox.soap.VBoxSvc.Companion", "NAMESPACE"))
                        .endControlFlow()


                //suspend and enqueue
                spec.beginControlFlow(if(isLiveData) " %M<%T>" else "return %M<%T>",
                        MemberName("kotlinx.coroutines", "suspendCancellableCoroutine"),
                        if(isLiveData) { func.returnType.getArgument(0).type.asTypeName(nameResolver, proto::getTypeParameter) }
                        else { func.returnType.asTypeName(nameResolver, proto::getTypeParameter) })
                        .beginControlFlow("it.invokeOnCancellation")
                        .addStatement("call.cancel()")
                        .endControlFlow()

                val successFunc = FunSpec.builder("onResponse")
                        .addModifiers(KModifier.OVERRIDE)
                        .throws(ClassName("java.io", "IOException"))
                        .addParameter("call", ClassName("okhttp3", "Call"))
                        .addParameter("response", ClassName("okhttp3", "Response"))
                        .beginControlFlow("if(response.isSuccessful())")
                        .beginControlFlow("try")
                        .addStatement("val xp = %T()",
                                ClassName("org.kxml2.io", "KXmlParser"))
                        .addStatement("xp.setFeature(%M, true)",
                                MemberName("org.xmlpull.v1.XmlPullParser", "FEATURE_PROCESS_NAMESPACES"))
                        .addStatement("xp.setInput(response.body()!!.byteStream(), null);")
                        .addStatement("envelope.parse(xp);")
                        .beginControlFlow("if(envelope.bodyIn is org.ksoap2.SoapFault)")
                        .addStatement("it.%M(envelope.bodyIn as org.ksoap2.SoapFault)",
                                MemberName("kotlin.coroutines", "resumeWithException"))
                        .nextControlFlow("else")

                if (returnTypeName != ClassName("kotlin", "Unit")) {
                    successFunc.addStatement("val ks = envelope.bodyIn as %T",
                            ClassName("org.ksoap2.serialization", "KvmSerializable"))

                    if (func.returnType.nullable) {
                        successFunc.beginControlFlow("if(ks.getPropertyCount()==0)")
                                .addStatement("it.%M(null)", MemberName("kotlin.coroutines", "resume"))
                                .endControlFlow()
                    }

                    if(returnTypeName is ParameterizedTypeName) {
                        val componentType = func.returnType.getArgument(0)
                        messager.printMessage(Diagnostic.Kind.WARNING, "parameter")

                        val isList = returnTypeName.rawType == ClassName("kotlin.collections", "List")
                        val isMap = func.returnType.argumentCount == 2

                        if(isLiveData) {
                            successFunc.addStatement("val rawRet = ks.getProperty(0)")
                            if (func.returnType.nullable) {
                                successFunc.beginControlFlow("val ret: %T = if(rawRet!=null && !rawRet.toString().equals(\"anyType{}\"))",
                                        func.returnType.asTypeName(nameResolver, proto::getTypeParameter))
                                        .addStatement("%L", unmarshal(injected.data, successFunc, ksoap, componentType.type, "rawRet"))
                                        .nextControlFlow("else")
                                        .addStatement("null")
                                        .endControlFlow()
                            } else {
                                successFunc.addStatement("val ret = %L",
                                        unmarshal(injected.data, successFunc, ksoap, componentType.type, "rawRet"))
                            }
                        } else if (returnTypeName.rawType == ClassName("kotlin", "Array")) {
                            successFunc.addStatement("val list = %M<%T>()",
                                    MemberName("kotlin.collections", "mutableListOf"),
                                    componentType.type.asTypeName(nameResolver, proto::getTypeParameter))
                                    .beginControlFlow("for(i in 0..ks.getPropertyCount())")
                                    .beginControlFlow("ks.getProperty(i)?.let")
                                    .beginControlFlow("if(!it.toString().equals(\"anyType{}\"))")
                                    .addStatement("list.add(%L)", unmarshal(injected.data, successFunc, ksoap, componentType.type, "it"))
                                    .endControlFlow()
                                    .endControlFlow()
                                    .endControlFlow()
                                    .addStatement("val ret = list.toTypedArray()")
                        } else if (isMap) {
                            val valueType = func.returnType.getArgument(1)
                            successFunc.addStatement("val info = %T()",
                                    ClassName("org.ksoap2.serialization", "PropertyInfo"))
                            //Map<String, List<String>>
                            if (valueType.type.argumentCount == 1) {
                                successFunc.addStatement("val map = %M<String, %T<String>>()",
                                        MemberName("kotlin.collections", "mutableMapOf"),
                                        ClassName("kotlin.collections", "MutableList"))
                                        .beginControlFlow("for (i in 0..ks.getPropertyCount())")
                                        .addStatement("ks.getPropertyInfo(i, null, info)")
                                        .beginControlFlow("if (!map.containsKey(info.getName()))")
                                        .addStatement("map.put(info.getName(), %M<String>())",
                                                MemberName("kotlin.collections", "mutableListOf"))
                                        .endControlFlow()
                                        .addStatement("map.get(info.getName())!!.add(ks.getProperty(i).toString())")
                                        .endControlFlow()
                            } else { //Map<String, String>
                                successFunc.addStatement("val map = %M<String, String>()",
                                        MemberName("kotlin.collections", "mutableMapOf"))
                                        .beginControlFlow("for (i in 0..ks.getPropertyCount())")
                                        .addStatement("ks.getPropertyInfo(i, null, info)")
                                        .addStatement("map.put(info.getName(), ks.getProperty(i).toString())")
                                        .endControlFlow()
                            }
                            successFunc.addStatement("val ret = map")
                        } else if (isList) {
                            successFunc.addStatement("val list = %M<%T>()",
                                    MemberName("kotlin.collections", "mutableListOf"),
                                    componentType.type.asTypeName(nameResolver, proto::getTypeParameter))
                            successFunc.beginControlFlow("for(i in 0..ks.getPropertyCount())")
                            successFunc.beginControlFlow("if(ks.getProperty(i)!=null && !ks.getProperty(i).toString().equals(\"anyType{}\"))")
                            successFunc.addStatement("list.add(%L)", unmarshal(injected.data, successFunc, ksoap, componentType.type, "ks.getProperty(i)"))
                            successFunc.endControlFlow()
                            successFunc.endControlFlow()
                            successFunc.addStatement("val ret = list")
                        }
                    } else {
                        successFunc.addStatement("val rawRet = ks.getProperty(0)")
                        if (func.returnType.nullable) {
                            successFunc.beginControlFlow("val ret: %T = if(rawRet!=null && !rawRet.toString().equals(\"anyType{}\"))",
                                    func.returnType.asTypeName(nameResolver, proto::getTypeParameter))
                                    .addStatement("%L", unmarshal(injected.data, successFunc, ksoap, func.returnType, "rawRet"))
                                    .nextControlFlow("else")
                                    .addStatement("null")
                                    .endControlFlow()
                        } else {
                            successFunc.addStatement("val ret = %L",
                                    unmarshal(injected.data, successFunc, ksoap, func.returnType, "rawRet"))
                        }
                    }

                    method.getAnnotation(Cacheable::class.java)?.takeIf { it.put }?.let {
                        successFunc.addStatement("database.${typeName}Dao().set%L(idRef, ret)",
                                if (it.value.isNotEmpty()) it.value else nameResolver.getString(func.name))
                    }

                    successFunc.addStatement("it.%M(ret)", MemberName("kotlin.coroutines", "resume"))
                } else {
                    successFunc.addStatement("it.%M(Unit)", MemberName("kotlin.coroutines", "resume"))
                }

                successFunc.endControlFlow()
                        .nextControlFlow("catch(e: %T)",
                                ClassName("org.xmlpull.v1", "XmlPullParserException"))
                        .addStatement("it.%M(e)",
                                MemberName("kotlin.coroutines", "resumeWithException"))
                        .endControlFlow()
                        .nextControlFlow("else")
                        .addStatement("it.%M(IOException(%S))",
                                MemberName("kotlin.coroutines", "resumeWithException"),
                                "shitniz")
                        .endControlFlow()


                val failFunc = FunSpec.builder("onFailure")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("call", ClassName("okhttp3", "Call"))
                        .addParameter("exception", IOException::class)
                        .addStatement("it.%M(exception)",
                                MemberName("kotlin.coroutines", "resumeWithException"))

                val callback = TypeSpec.anonymousClassBuilder()
                        .addSuperinterface(ClassName("okhttp3", "Callback"))
                        .addFunction(successFunc.build())
                        .addFunction(failFunc.build())

                spec.addStatement("call.enqueue(%L)", callback.build())

                spec.endControlFlow()

                if (isLiveData) {
                    spec.endControlFlow()
                }

                proxyType.addFunction(spec.build())
            }
        }

        fileSpec.addType(proxyType.build())
                .build()
                .writeTo(filer)
    }

    /**
     * Generate code to marshall an element. Recursive list/array handling.
     * @param ksoap
     * @param type
     * @param name
     */
    private fun marshalParameter(injected: InjectedClass, spec: FunSpec.Builder, ksoap: Ksoap?, ktype: ProtoBuf.Type, type: TypeMirror, ksoapName: String, name: String) {
        val proto = injected.data.classProto
        val nameResolver = injected.data.nameResolver

        if (ktype.nullable)
            spec.beginControlFlow("%L?.let", name)

        val paramName: String = if (!ksoap?.value.isNullOrEmpty()) ksoap?.value!! else ksoapName

        //Arrays
        if (type.kind == TypeKind.ARRAY) {
            val componentType = (type as ArrayType).componentType
            spec.beginControlFlow("for(element in %L)", name)
            marshalParameter(injected, spec, ksoap, ktype.getArgument(0).type, componentType, ksoapName, "element")
            spec.endControlFlow()
        }
        //Collections
        else if (typeUtils.isAssignable(type, typeUtils.getDeclaredType(
                        elementUtils.getTypeElement("java.util.Collection"),
                        typeUtils.getWildcardType(null, null)))) {
            val componentType = Util.getGenericTypeArgument(type, 0)
            spec.beginControlFlow("for(element in %L)", name)
            marshalParameter(injected, spec, ksoap, ktype.getArgument(0).type, componentType, ksoapName, "element")
            spec.endControlFlow()
        } //Annotation-specified simple type
        else if (!ksoap?.type.isNullOrEmpty()) {
            spec.addStatement("request.addProperty(%S, %T(%S, %S, %L.toString()))",
                    paramName,
                    ClassName("org.ksoap2.serialization", "SoapPrimitive"),
                    ksoap!!.namespace,
                    ksoap!!.type,
                    name)
        } //Proxy
        else if (typeUtils.isAssignable(type, elementUtils.getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
            spec.addStatement("request.addProperty(%S, %L.idRef)",
                    paramName,
                    name)
        } //Enum
        else if (Util.isEnum(type)) {
            spec.addStatement("request.addProperty(%S, %T(%M, %L::class.java.simpleName, %L.value()))",
                    paramName,
                    ClassName("org.ksoap2.serialization", "SoapPrimitive"),
                    MemberName("com.kedzie.vbox.soap.VBoxSvc", "NAMESPACE"),
                    ktype.asTypeName(nameResolver, proto::getTypeParameter).copy(nullable = false),
                    name)
        } else {
            spec.addStatement("request.addProperty(%S, %L)",
                    paramName,
                    name)
        }

        if (ktype.nullable)
            spec.endControlFlow()
    }

    private fun unmarshal(classData: ClassData, spec: FunSpec.Builder, ksoap: Ksoap, returnType: ProtoBuf.Type, name: String): String {
        val proto = classData.classProto
        val nameResolver = classData.nameResolver

        val typeName = returnType.asTypeName(nameResolver, proto::getTypeParameter).copy(nullable = false)

        when(typeName) {
            ClassName("kotlin", "Int") -> "if($name is Int) $name as Int else $name.toString().toInt()"
            ClassName("kotlin", "Long") -> "if($name is Long) $name as Long else $name.toString().toLong()"
            ClassName("kotlin", "Short") -> "if($name is Short) $name as Short else $name.toString().toShort()"
            ClassName("kotlin", "Boolean") -> "if($name is Boolean) $name as Boolean else $name.toString().toBoolean()"
            ClassName("kotlin", "String") -> "if($name is String) $name as String else $name.toString()"
            ClassName("kotlin", "ByteArray") -> "android.util.Base64.decode($name.toString(), android.util.Base64.DEFAULT)"
            else -> null
        }?.let { return it }

        val returnTypeElement = elementUtils.getTypeElement(typeName.toString())

        if (typeUtils.isAssignable(returnTypeElement.asType(),
                        elementUtils.getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
            return "${typeName}Proxy(api, database, $name.toString())"
        }

        if (returnTypeElement.kind == ElementKind.ENUM) {
            return "${typeName}.fromValue($name.toString())"
        }
        //Complex object
        returnTypeElement.getAnnotation(KsoapObject::class.java)?.let {
            val typeMetadata: KotlinMetadata? = returnTypeElement.kotlinMetadata
            if (typeMetadata !is KotlinClassMetadata) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR, "Could not find type metadata for complex object", returnTypeElement)
                throw IllegalStateException("Could not find type metadata for complex object ${returnTypeElement}")
            }
            spec.addStatement("val soapObject = %L as %T",
                    name,
                    ClassName("org.ksoap2.serialization", "SoapObject"))

            for (p in typeMetadata.data.classProto.propertyList) {
                if(p.isGetterDefault) {
                    val field = typeMetadata.data.nameResolver.getString(p.name)
                    spec.addStatement("val raw_%L = soapObject.getProperty(%S)", field, field)
                    if(p.returnType.nullable) {
                        spec.addStatement("var %L: %T = null", field,
                                p.returnType.asTypeName(typeMetadata.data.nameResolver,
                                        typeMetadata.data.proto::getTypeParameter))
                        spec.beginControlFlow("if(raw_%L!=null && !raw_%L.toString().equals(\"anyType{}\"))", field, field)
                        spec.addStatement("%L = %L",
                                field,
                                unmarshal(typeMetadata.data, spec,
                                        ksoap,
                                        p.returnType,
                                        "raw_$field"))
                        spec.endControlFlow()
                    } else {
                        spec.addStatement("val %L = %L",
                                field,
                                unmarshal(typeMetadata.data, spec,
                                        ksoap,
                                        p.returnType,
                                        "raw_$field"))
                    }
                }
            }
            val buf = StringBuffer("val obj = ${typeName}(")
            val paramCount = typeMetadata.data.classProto.getConstructor(0).valueParameterCount
            for((i,p) in typeMetadata.data.classProto.getConstructor(0).valueParameterList.withIndex()) {
                val field = typeMetadata.data.nameResolver.getString(p.name)
                buf.append("$field")
                if(i<paramCount-1)
                    buf.append(",\n")
            }
            buf.append(")")
            spec.addStatement(buf.toString())
            return "obj"
        }

        return name
    }
}