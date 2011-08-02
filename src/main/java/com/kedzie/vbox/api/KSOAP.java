package com.kedzie.vbox.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ksoap2.serialization.SoapSerializationEnvelope;

@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface KSOAP {
	public String value() default "";;
	public String namespace() default SoapSerializationEnvelope.XSD;
	public String type() default "";
	public String prefix() default "";
	public String thisReference() default "_this";
}
