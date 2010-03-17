package org.slurry.cache4guice;

import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Calculator.class).to(CalculatorImpl.class);

	}

}
