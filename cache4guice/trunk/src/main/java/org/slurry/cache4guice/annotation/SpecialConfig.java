package org.slurry.cache4guice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For this to work Methods must be public, package-private or protected Methods
 * must be non-final Instances must be created by Guice by an @Inject-annotated
 * or no-argument constructor
 * 
 * Contains name of special configuration (typically used for selfpopulating cache) for a certain cache
 * @author nmw
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SpecialConfig {
	/**
	 * @return special configuration name
	 */
	String cacheConfigurationName();

}
