package com.kedzie.vbox.processor;


import com.google.auto.service.AutoService;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.KSOAPMethodStrategy;
import com.kedzie.vbox.soap.KSoapObject;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static com.kedzie.vbox.processor.Util.adapterName;
import static com.kedzie.vbox.processor.Util.elementToString;
import static com.kedzie.vbox.processor.Util.getAnnotation;
import static com.kedzie.vbox.processor.Util.getPackage;
import static com.kedzie.vbox.processor.Util.rawTypeToString;
import static com.kedzie.vbox.processor.Util.typeToString;
import static com.squareup.javawriter.JavaWriter.stringLiteral;
import static com.squareup.javawriter.JavaWriter.type;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;


/**
 * Creates KSOAP Proxy classes
 *
 * @author kedzie
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.kedzie.vbox.soap.KSOAP")
public final class ProxyProcessor extends AbstractProcessor {
    private final Set<String> remainingTypeNames = new LinkedHashSet<String>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(KSOAP.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        remainingTypeNames.addAll(findInjectedClassNames(roundEnv));
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "types: " + remainingTypeNames);
        for (Iterator<String> i = remainingTypeNames.iterator(); i.hasNext();) {
            InjectedClass injectedClass = createInjectedClass(i.next());
            // Verify that we have access to all types to be injected on this pass.
            boolean missingDependentClasses = !allTypesExist(injectedClass.methods);
            if (!missingDependentClasses) {
                try {
                    generateProxy(injectedClass.type, injectedClass.methods);
                } catch (Throwable e) {
                    error("Code gen failed: " + e, injectedClass.type);
                }
                i.remove();
            }
        }
        if (roundEnv.processingOver() && !remainingTypeNames.isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Could not find injection type required by " + remainingTypeNames);
        }
        return false;
    }

    private Set<String> findInjectedClassNames(RoundEnvironment env) {
        // First gather the set of classes that have @KSOAP-annotated members.
        Set<String> injectedTypeNames = new LinkedHashSet<String>();
        for (Element element : env.getElementsAnnotatedWith(KSOAP.class)) {
            if (!validateInjectable(element)) {
                continue;
            }

            Element enclosing = element.getEnclosingElement();
            if(element.getKind()==ElementKind.INTERFACE)
                injectedTypeNames.add(rawTypeToString(element.asType(), '.'));
            else if(enclosing.getKind()==ElementKind.INTERFACE)
                injectedTypeNames.add(rawTypeToString(enclosing.asType(), '.'));
        }
        return injectedTypeNames;
    }

    /**
     * Return true if all element types are currently available in this code
     * generation pass. Unavailable types will be of kind {@link javax.lang.model.type.TypeKind#ERROR}.
     */
    private boolean allTypesExist(Collection<ExecutableElement> methods) {
        for (ExecutableElement method: methods) {
            for(Element parameter : method.getParameters()) {
                if (parameter.asType().getKind() == TypeKind.ERROR)
                    return false;
            }
            if(method.getReturnType().getKind() == TypeKind.ERROR)
                return false;
        }
        return true;
    }

    private boolean validateInjectable(Element injectable) {
        if (injectable.getKind()!= ElementKind.INTERFACE &&
                injectable.getKind()!= ElementKind.METHOD &&
                injectable.getKind()!= ElementKind.PARAMETER) {
            error("@KSOAP is only valid on INTERFACE, METHOD, or PARAMETER"
                    + elementToString(injectable), injectable);
            return false;
        }
        return true;
    }

    /**
     * @param injectedClassName the name of a class with an @Inject-annotated member.
     */
    private InjectedClass createInjectedClass(String injectedClassName) {
        TypeElement type = processingEnv.getElementUtils().getTypeElement(injectedClassName);
        List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
        for (Element member : type.getEnclosedElements()) {
            if(member.getKind().equals(ElementKind.METHOD)) {
                methods.add((ExecutableElement) member);
            }
        }
        return new InjectedClass(type, methods);
    }


    /**
     * Write a proxy class for {@code type}
     */
    private void generateProxy(TypeElement type, List<ExecutableElement> methods) throws IOException {
        String packageName = getPackage(type).getQualifiedName().toString();
        String strippedTypeName =
                strippedTypeName(type.getQualifiedName().toString(), packageName);
        String adapterName = adapterName(type, "$$Proxy");
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(adapterName, type);
        JavaWriter writer = new JavaWriter(sourceFile.openWriter());

        writer.emitSingleLineComment("Code generated by ksoap-compiler.  Do not edit.");
        writer.emitPackage(packageName);
        writer.emitImports("com.kedzie.vbox.soap.VBoxSvc",
                "com.google.common.base.Objects",
                "com.kedzie.vbox.app.Utils",
                "org.ksoap2.serialization.SoapObject",
                "org.ksoap2.serialization.SoapPrimitive",
                "org.ksoap2.serialization.SoapSerializationEnvelope",
                "org.ksoap2.serialization.KvmSerializable",
                "org.ksoap2.serialization.PropertyInfo",
                "org.ksoap2.SoapEnvelope",
                "android.os.Parcelable",
                "android.os.Parcel",
                "java.util.HashMap",
                "java.util.Map",
                "java.util.List",
                "java.util.ArrayList",
                "java.io.IOException");
        writer.emitEmptyLine();
        writer.emitJavadoc("Proxy for %s", strippedTypeName);

        String baseClass = type.getInterfaces().isEmpty() ? "com.kedzie.vbox.api.BaseProxy" :
                typeToString(type.getInterfaces().get(0))+"$$Proxy";
        writer.beginType(adapterName, "class", EnumSet.of(PUBLIC),
                baseClass,
                strippedTypeName);

        writer.emitEmptyLine();

        //Parcelable.Creator definition
        String creatorType = String.format("Parcelable.Creator<%s>",adapterName);
        writer.beginType("ProxyCreator", "class", EnumSet.of(PUBLIC, STATIC), null, creatorType);

        writer.beginMethod(adapterName, "createFromParcel", EnumSet.of(PUBLIC), "Parcel", "source");
        writer.emitStatement("final ClassLoader loader = %s.class.getClassLoader()", adapterName);
        writer.emitStatement("VBoxSvc vmgr =  source.readParcelable(loader)");
        writer.emitStatement("String id = source.readString()");
        writer.emitStatement("Map<String, Object> cache = new HashMap<String, Object>()");
        writer.emitStatement("source.readMap(cache, loader)");
        writer.emitStatement("return (%s)vmgr.getProxy(%s.class, id, cache)", adapterName, strippedTypeName);
        writer.endMethod();

        writer.beginMethod(adapterName+"[]", "newArray", EnumSet.of(PUBLIC), "int", "size");
        writer.emitStatement("return new %s[size]", adapterName);
        writer.endMethod();
        writer.endType();

        writer.emitField(creatorType, "CREATOR", EnumSet.of(PUBLIC, STATIC, FINAL), "new ProxyCreator()");

        writer.emitEmptyLine();

        //constructor
        writer.beginConstructor(EnumSet.of(PUBLIC),
                "VBoxSvc", "vmgr",
                "String", "id",
                type(Class.class, "?"), "type",
                type(Map.class, "String", "Object"), "cache");
        writer.emitStatement("super(vmgr, id, type, cache)");
        writer.endConstructor();

        Map<String, Object> typeKSOAP = getAnnotation(KSOAP.class, type);
        KSOAPMethodStrategy methodIncludeStrategy = (KSOAPMethodStrategy)typeKSOAP.get("methodIncludeStrategy");
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Method include strategy: " + methodIncludeStrategy);
        //methods
        for(ExecutableElement method : methods) {
            Map<String, Object> ksoap = getAnnotation(KSOAP.class, method);
            //Skip excluded methods using method include strategy
            if(ksoap==null && methodIncludeStrategy.equals(KSOAPMethodStrategy.INCLUDE_ANNOTATED))
                continue;
            //Inherit type annotation if method annotation is missing
            if(ksoap==null)
                ksoap = typeKSOAP;

            String methodName = method.getSimpleName().toString();
            String returnType = typeToString(method.getReturnType());

            writer.emitAnnotation(Override.class);
            List<String> args = new ArrayList<String>(method.getParameters()==null ? 0 : method.getParameters().size()*2);
            for (VariableElement parameter : method.getParameters()) {
                args.add(typeToString(parameter.asType()));
                args.add(parameter.getSimpleName().toString());
            }
            List<String> thrown = new ArrayList<String>(method.getThrownTypes().size());
            boolean throwsIOException = thrown.contains("java.io.IOException");
            for (TypeMirror ex : method.getThrownTypes()) {
                thrown.add(typeToString(ex));
            }
            writer.beginMethod(method.getReturnType().getKind() == TypeKind.VOID ? "void" : returnType,
                    methodName, EnumSet.of(PUBLIC), args, thrown);

            final Boolean cacheable = (Boolean)ksoap.get("cacheable");

            if(cacheable) {
                StringBuffer cmd = new StringBuffer("String cacheKey = ").append(stringLiteral(methodName));
                for (VariableElement parameter : method.getParameters()) {
                    cmd.append(" + String.valueOf(").append(parameter.getSimpleName()).append(")");
                }
                writer.emitStatement(cmd.toString());

                writer.emitStatement("if(_cache.containsKey(cacheKey)) return (%s)_cache.get(cacheKey)",
                        method.getReturnType().getKind().isPrimitive() ?
                                getTypeUtils().boxedClass(getTypeUtils().getPrimitiveType(method.getReturnType().getKind())) :
                                returnType);
            }
            String prefix = (String)ksoap.get("prefix");
            writer.emitStatement("SoapObject request = new SoapObject(%s, \"%s_%s\")",
                    stringLiteral("http://www.virtualbox.org/"), prefix.equals("") ? type.getSimpleName().toString() : prefix, methodName);

            if (!ksoap.get("thisReference").equals(""))
                writer.emitStatement("request.addProperty(%s, _uiud)", stringLiteral((String) ksoap.get("thisReference")));

            for (VariableElement parameter : method.getParameters()) {
                final Map<String, Object> pksoap = getAnnotation(KSOAP.class, parameter);
                if(pksoap==null) {
                    error("KSOAP Cannot be null.  Type: " + strippedTypeName, parameter);
                }
                //marshall parameters
                marshalParameter(writer, pksoap, parameter.asType(), parameter.getSimpleName().toString());
            }

            writer.emitStatement("SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11)");
            writer.emitStatement("envelope.setAddAdornments(false)");
            writer.emitStatement("envelope.setOutputSoapObject(request)");

            //if asynchronous then launch thread
            if(getAnnotation(Asyncronous.class, method)!=null) {
                writer.emitStatement("_vmgr.getExecutor().execute(new AsynchronousThread(VBoxSvc.NAMESPACE+request.getName(), envelope))");
            } else {
                if (!throwsIOException) {
                    writer.beginControlFlow("try");
                }
                writer.emitStatement("_vmgr.httpCall(VBoxSvc.NAMESPACE+request.getName(), envelope)");

                writer.beginControlFlow("if(envelope.bodyIn instanceof org.ksoap2.SoapFault)");
                writer.emitStatement("throw (org.ksoap2.SoapFault) envelope.bodyIn");
                writer.endControlFlow();

                //unmarshall return value
                if (method.getReturnType().getKind() != TypeKind.VOID) {
                    TypeMirror ω = getTypeUtils().getWildcardType(null, null);
                    TypeMirror listType = getTypeUtils().getDeclaredType(
                            getElementUtils().getTypeElement("java.util.Collection"), ω);
                    DeclaredType wildcardMap = getTypeUtils().getDeclaredType(
                            getElementUtils().getTypeElement("java.util.Map"), ω, ω);
                    boolean IS_COLLECTION = getTypeUtils().isAssignable(method.getReturnType(), listType);
                    boolean IS_MAP = getTypeUtils().isAssignable(method.getReturnType(), wildcardMap);
                    boolean IS_ARRAY = method.getReturnType().getKind().equals(TypeKind.ARRAY) && !((ArrayType) method.getReturnType()).getComponentType().getKind().equals(TypeKind.BYTE);

                    writer.emitStatement("KvmSerializable ks = (KvmSerializable) envelope.bodyIn");
                    if (!IS_MAP && !IS_COLLECTION && !method.getReturnType().getKind().isPrimitive()) {
                        writer.beginControlFlow("if(ks.getPropertyCount()==0)");
                        writer.emitStatement("return null");
                        writer.endControlFlow();
                    }

                    //Map
                    if (IS_MAP) {
                        TypeMirror keyType = Util.getGenericTypeArgument(method.getReturnType(), 0);
                        TypeMirror valueType = Util.getGenericTypeArgument(method.getReturnType(), 1);
                        writer.emitStatement("PropertyInfo info = new PropertyInfo()");
                        //Map<String, List<String>>
                        if (getTypeUtils().isAssignable(valueType, listType)) {
                            writer.emitStatement("Map<String, List<String>> map = new HashMap<String, List<String>>();");
                            writer.beginControlFlow("for (int i = 0; i < ks.getPropertyCount(); i++)");
                            writer.emitStatement("ks.getPropertyInfo(i, null, info);");
                            writer.beginControlFlow("if (!map.containsKey(info.getName()))");
                            writer.emitStatement("map.put(info.getName(), new ArrayList<String>())");
                            writer.endControlFlow();
                            writer.emitStatement("map.get(info.getName()).add(ks.getProperty(i).toString())");
                            writer.endControlFlow();
                        }
                        //Map<String, String>
                        else {
                            writer.emitStatement("Map<String, String> map = new HashMap<String, String>();");
                            writer.beginControlFlow("for (int i = 0; i < ks.getPropertyCount(); i++)");
                            writer.emitStatement("ks.getPropertyInfo(i, null, info);");
                            writer.emitStatement("map.put(info.getName(), ks.getProperty(i).toString())");
                            writer.endControlFlow();
                        }
                        writer.emitStatement("%s ret = map", returnType);
                    }
                    //List
                    else if (IS_COLLECTION) {
                        TypeMirror componentType = Util.getGenericTypeArgument(method.getReturnType(), 0);
                        writer.emitStatement("%s list = new ArrayList<%s>(ks.getPropertyCount())", returnType, typeToString(componentType));
                        writer.beginControlFlow("for(int i=0; i<ks.getPropertyCount(); i++)");
                        writer.beginControlFlow("if(ks.getProperty(i)!=null && !ks.getProperty(i).toString().equals(\"anyType{}\"))");
                        writer.emitStatement("list.add(%s)", unmarshal(writer, ksoap, componentType, "ks.getProperty(i)"));
                        writer.endControlFlow();
                        writer.endControlFlow();
                        writer.emitStatement("%s ret = list", returnType);
                    } else if (IS_ARRAY) {
                        TypeMirror componentType = ((ArrayType) method.getReturnType()).getComponentType();
                        writer.emitStatement("%s array = new %s[ks.getPropertyCount()]", returnType, typeToString(componentType));
                        writer.beginControlFlow("for(int i=0; i<ks.getPropertyCount(); i++)");
                        writer.beginControlFlow("if(ks.getProperty(i)!=null && !ks.getProperty(i).toString().equals(\"anyType{}\"))");
                        writer.emitStatement("array[i] = %s", unmarshal(writer, ksoap, componentType, "ks.getProperty(i)"));
                        writer.endControlFlow();
                        writer.endControlFlow();
                        writer.emitStatement("%s ret = array", returnType);
                    } else {
                        String unmarshallStmt = unmarshal(writer, ksoap, method.getReturnType(), "ks.getProperty(0)");
                        if(!method.getReturnType().getKind().isPrimitive()) {
                            writer.emitStatement("%s ret = null", returnType);
                            writer.beginControlFlow("if(ks.getProperty(0)!=null && !ks.getProperty(0).toString().equals(\"anyType{}\"))");
                            writer.emitStatement("ret = %s", unmarshallStmt);
                            writer.endControlFlow();
                        } else {
                            writer.emitStatement("%s ret = %s", returnType, unmarshallStmt);
                        }
                    }

                    if (cacheable) {
                        writer.emitStatement("_cache.put(cacheKey, ret)");
                    }

                    //update cache for simple property setters
//                if(methodName.startsWith("set")) {
//                    writer.emitStatement("_cache.put(\"get\"+name.substring(3), method.getParameters().get(0).getSimpleName().toString())");
//                }

                    writer.emitStatement("return ret");
                }

                if (!throwsIOException) {
                    writer.endControlFlow();
                    writer.beginControlFlow("catch(java.io.IOException e)");
                    writer.emitStatement("throw new RuntimeException(e)");
                    writer.endControlFlow();
                }
            }
            writer.endMethod();
        }
        writer.emitEmptyLine();

        writer.endType();
        writer.close();
    }

    /**
     * Generate code to unmarshall an element of return type
     * @param ksoap
     * @param returnType    return type
     * @param name      java code which has the actual value
     * @return the string of generated code
     * @throws IOException
     */
    private String unmarshal(JavaWriter writer, Map<String, Object> ksoap, TypeMirror returnType, String name) throws IOException {

        //Base64 array
        if(returnType.getKind().equals(TypeKind.ARRAY) && ((ArrayType)returnType).getComponentType().getKind().equals(TypeKind.BYTE)) {
            return String.format("android.util.Base64.decode(%s.toString().getBytes(), android.util.Base64.DEFAULT)", name);
        }
        //Proxy
        else if(getTypeUtils().isAssignable(returnType, getElementUtils().getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
            return String.format("_vmgr.getProxy(%s.class, %s.toString())", typeToString(returnType), name);
        }
        //Enum
        else if(Util.isEnum(returnType)) {
            return String.format("%s.fromValue(%s.toString())", typeToString(returnType), name);
        }
        else if(returnType.equals(getElementUtils().getTypeElement("java.lang.Integer").asType())
                || returnType.getKind().equals(TypeKind.INT)) {
            return String.format("%s instanceof Integer ? (Integer)%s : Integer.valueOf(%s.toString())", name, name, name);
        }
        else if(returnType.equals(getElementUtils().getTypeElement("java.lang.Long").asType())
                || returnType.getKind().equals(TypeKind.LONG)) {
            return String.format("%s instanceof Long ? (Long)%s : Long.valueOf(%s.toString())", name, name, name);
        }
        else if(returnType.equals(getElementUtils().getTypeElement("java.lang.Short").asType())
                || returnType.getKind().equals(TypeKind.SHORT)) {
            return String.format("%s instanceof Short ? (Short)%s : Short.valueOf(%s.toString())", name, name, name);
        }
        else if(returnType.equals(getElementUtils().getTypeElement("java.lang.Boolean").asType())
                || returnType.getKind().equals(TypeKind.BOOLEAN)) {
            return String.format("%s instanceof Boolean ? (Boolean)%s : Boolean.valueOf(%s.toString())", name, name, name);
        }
        else if(returnType.equals(getElementUtils().getTypeElement("java.lang.String").asType())) {
            return String.format("%s.toString()", name);
        }
        //Complex object
        Element element = getTypeUtils().asElement(returnType);
        if(element!=null && getAnnotation(KSoapObject.class, element) != null) {
            writer.emitStatement("SoapObject soapObject = (SoapObject)%s", name);
            writer.emitStatement("%s obj = new %s()", typeToString(returnType), typeToString(returnType));
            for(Element e : element.getEnclosedElements()) {
                if(e.getKind().equals(ElementKind.FIELD) && !e.getModifiers().contains(Modifier.STATIC) && getAnnotation(KSoapObject.class, e)==null) {
                    String field = e.getSimpleName().toString();
                    TypeMirror fieldType = e.asType();
                    String setter = new StringBuffer("set").append(field.substring(0,1).toUpperCase()).append(field.substring(1)).toString();
                    writer.beginControlFlow("if(soapObject.getProperty("+stringLiteral(field)+")!=null && !soapObject.getProperty("+stringLiteral(field)+").toString().equals(\"anyType{}\"))");
                    writer.emitStatement("obj.%s(%s)", setter, unmarshal(writer, ksoap, fieldType, "soapObject.getProperty("+stringLiteral(field)+")"));
                    writer.endControlFlow();
                }
            }
            return "obj";
        }
        return "null";
    }

    /**
     * Generate code to marshall an element. Recursive list/array handling.
     * @param ksoap
     * @param type
     * @param name
     */
    private void marshalParameter(JavaWriter writer, Map<String, Object> ksoap, TypeMirror type, String name) throws IOException {
        if(!type.getKind().isPrimitive())
            writer.beginControlFlow(String.format("if (%s!=null)", name));

        //Arrays
        if(type.getKind().equals(TypeKind.ARRAY)) {
            TypeMirror componentType = ((ArrayType)type).getComponentType();
            writer.beginControlFlow(String.format("for(%s element : %s)", typeToString(componentType), name));
            marshalParameter(writer, ksoap, componentType, "element");
            writer.endControlFlow();
        }
        //Collections
        else if(getTypeUtils().isAssignable(type, getTypeUtils().getDeclaredType(
                getElementUtils().getTypeElement("java.util.Collection"),
                getTypeUtils().getWildcardType(null, null)))) {
            TypeMirror componentType = Util.getGenericTypeArgument(type, 0);
            writer.beginControlFlow(String.format("for(%s element : %s)", typeToString(componentType), name));
            marshalParameter(writer, ksoap, componentType, "element");
            writer.endControlFlow();
        }
        //Annotation-specified simple type
        else if(!ksoap.get("type").equals("")) {
            writer.emitStatement("request.addProperty(%s, new SoapPrimitive(%s, %s, String.valueOf(%s)))",
                    stringLiteral((String)ksoap.get("value")),
                    stringLiteral((String)ksoap.get("namespace")),
                    stringLiteral((String)ksoap.get("type")),
                    name);
        }
        //Proxy
        else if(getTypeUtils().isAssignable(type, getElementUtils().getTypeElement("com.kedzie.vbox.api.IManagedObjectRef").asType())) {
            writer.emitStatement("request.addProperty(%s, %s.getIdRef())",
                    stringLiteral((String)ksoap.get("value")),
                    name);
        }
        //Enum
        else if(Util.isEnum(type)) {
            writer.emitStatement("request.addProperty(%s, new SoapPrimitive(NAMESPACE, %s.class.getSimpleName(), %s.value()))",
                    stringLiteral((String)ksoap.get("value")),
                    typeToString(type),
                    name);
        } else {
            writer.emitStatement("request.addProperty(%s, %s)",
                    stringLiteral((String)ksoap.get("value")),
                    name);
        }
        if(!type.getKind().isPrimitive())
            writer.endControlFlow();
    }

    private String strippedTypeName(String type, String packageName) {
        return type.substring(packageName.isEmpty() ? 0 : packageName.length() + 1);
    }

    private void error(String msg, Element element) {
        print(Diagnostic.Kind.ERROR, msg, element);
    }

    private void warning(String msg, Element element) {
        print(Diagnostic.Kind.WARNING, msg, element);
    }

    private void note(String msg, Element element) {
        print(Diagnostic.Kind.NOTE, msg, element);
    }

    private void print(Diagnostic.Kind kind, String msg, Element element) {
        if(element!=null)
            processingEnv.getMessager().printMessage(kind, msg, element);
    }

    private void print(Diagnostic.Kind kind, String msg) {
            processingEnv.getMessager().printMessage(kind, msg);
    }

    private Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    private Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }

    static class InjectedClass {
        final TypeElement type;
        final List<ExecutableElement> methods;

        InjectedClass(TypeElement type, List<ExecutableElement> methods) {
            this.type = type;
            this.methods = methods;
        }
    }
}
