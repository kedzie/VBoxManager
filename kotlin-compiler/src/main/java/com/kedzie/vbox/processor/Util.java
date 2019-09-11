/*
 * Copyright (C) 2013 Google, Inc.
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kedzie.vbox.processor;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

/**
 * Utilities for handling types in annotation processors
 */
final class Util {
    private Util() {
    }

    public static TypeMirror getGenericTypeArgument(TypeMirror type, Integer index)
    {
        return type.accept(new SimpleTypeVisitor6<TypeMirror, Integer>()
        {
            @Override
            public TypeMirror visitDeclared(DeclaredType declaredType, Integer index)
            {
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                return typeArguments.size()>index ? typeArguments.get(index) : null;
            }
        }, index);
    }

    public static PackageElement getPackage(Element type) {
        while (type.getKind() != ElementKind.PACKAGE) {
            type = type.getEnclosingElement();
        }
        return (PackageElement) type;
    }

    /** Returns a string for the raw type of {@code type}. Primitive types are always boxed. */
    public static String rawTypeToString(TypeMirror type, char innerClassSeparator) {
        if(type.getKind()==TypeKind.VOID)
            return "void";
//    if (!(type instanceof DeclaredType)) {
//      throw new IllegalArgumentException("Unexpected type: " + type);
//    }
        StringBuilder result = new StringBuilder();
        DeclaredType declaredType = (DeclaredType) type;
        rawTypeToString(result, (TypeElement) declaredType.asElement(), innerClassSeparator);
        return result.toString();
    }


    static void rawTypeToString(StringBuilder result, TypeElement type,
                                char innerClassSeparator) {
        String packageName = getPackage(type).getQualifiedName().toString();
        String qualifiedName = type.getQualifiedName().toString();
        if (packageName.isEmpty()) {
            result.append(qualifiedName.replace('.', innerClassSeparator));
        } else {
            result.append(packageName);
            result.append('.');
            result.append(
                    qualifiedName.substring(packageName.length() + 1).replace('.', innerClassSeparator));
        }
    }


    public static boolean isEnum(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == ElementKind.ENUM;
    }
}
