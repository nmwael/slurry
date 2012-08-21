package org.slurry.cache4guice;

import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Calculator.class).to(CalculatorImplChild.class).asEagerSingleton();
		bind(Calculator.class).to(CalculatorImplChild.class).asEagerSingleton();

	}

}
