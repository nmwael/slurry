package org.slurry.cache4guice;

import static com.google.inject.name.Names.named;

import com.google.inject.Key;
import com.google.inject.PrivateModule;

public class GuiceMultibinderModule extends PrivateModule {

	public static final String calculatorId01 = "1";
	public static final String calculatorId02 = "2";

	@Override
	protected void configure() {
		
		bind(
				Key.get(Calculator.class,
						named(calculatorId01))).to(
								CalculatorImplChild.class).asEagerSingleton();

		
		expose(Key.get(Calculator.class,
				named(calculatorId01)));

		bind(
				Key.get(Calculator.class,
						named(calculatorId02))).to(
								CalculatorImplParent.class).asEagerSingleton();

		
		expose(Key.get(Calculator.class,
				named(calculatorId02)));


		
		
		
		

	}

}
