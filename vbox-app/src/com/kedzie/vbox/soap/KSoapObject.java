package com.kedzie.vbox.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies type as a complex object marshalled by KSoap.
 * Must specify namespace and type name.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface KSoapObject {
    /** XSD Namespace */
    public String namespace() default "http://www.virtualbox.org";
    /** Name of XSD type*/
    public String value();
}
