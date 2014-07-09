package org.slurry.cache4guice;

import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Calculator.class).to(CalculatorImplChild.class).asEagerSingleton();


	}

}
