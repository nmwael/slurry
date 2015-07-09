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
 * @author nmw
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
	/**
	 * Optional custom name, if none specified a name derived from method signature will be created.
	 * @return
	 */
	String name() default "";
	
	/**
	 * Optional category name
	 * @see org.slurry.cache4guice.cache.util.getCaches(String category)
	 * @return
	 */
	String category() default "";
	
	/**
	 * Optional should the cache self update
	 **/
	boolean SelfPopulatingScheduledCache() default false;
	
	/**
	 * Used together with SelfPopulatingScheduledCache to specify the time between updates
	 * specified in milliseconds
	 * Will Have a grace time of 20 milliseconds used to detecting timeout
	 * @return
	 */
	long refreshTime() default 120;
        
        /**
         * Used together with SelfPopulatingScheduledCache , if true the updating job will be placed in the "slow" scheduler with the rest of the slow operations
         * @return 
         */
        boolean slowOperation() default false;
        
        
        
	

}
