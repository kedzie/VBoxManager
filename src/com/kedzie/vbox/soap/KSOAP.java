package com.kedzie.vbox.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ksoap2.serialization.SoapSerializationEnvelope;

/**
 * Specifies marshalling into SOAP Envelope.
 * Can be used as
 * <dl>
 * <dt>Method Arguments</dt>
 * <dd>
 * <ul>
 * <li>SOAP property name</li>
 * <li>Namespace/Datatype of marshalled parameter,  (<code>namespace=SoapSerializationEnvelope.XSD, type="int"</code>)
 * </ul>
 * </dd>
 * <dt>Method</dt>
 * <dd>
 * <ul>
 * <li>SOAP method prefix and name</li>
 * <li>How return value is unmarshalled into Java object</li>
 * <li>Whether the return value is stored in the In-Memory Cache (<code>cacheable=true</code>)</li>
 * </ul>
 * </dd>
 * @author Marek Kędzierski
 * @apiviz.landmark
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface KSOAP {
	public String value() default "";
	public String namespace() default SoapSerializationEnvelope.XSD;
	public String type() default "";
	public String prefix() default "";
	public String thisReference() default "_this";
	public boolean cacheable() default false;
}
