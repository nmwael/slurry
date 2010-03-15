package org.slurry.cache4guice;

import org.slurry.cache4guice.annotation.Cached;

public class CalculatorImpl implements Calculator {
	
	
	@Cached
	public int calculateSomethingWild(Integer number) throws InterruptedException {
		Thread.sleep(2000);
		
		return number;
		
	}

}
