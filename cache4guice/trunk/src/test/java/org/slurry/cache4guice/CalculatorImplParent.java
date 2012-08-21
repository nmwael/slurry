package org.slurry.cache4guice;

import java.util.Random;

import org.slurry.cache4guice.annotation.Cached;

public class CalculatorImplParent implements Calculator {

	@Cached
	public int calculateSomethingWild(Integer number)
			throws InterruptedException {
		Thread.sleep(2000);

		return number;

	}

	@Cached
	public int calculateSomethingWild(Integer number, Integer number2)
			throws InterruptedException {
		Thread.sleep(2000);

		return number + number2;
	}

	@Cached(category=Names.cacheCategoryA)
	public BogusClass veryLooooooooooooooooooooooooooooooooongNaaaaaaaammeeeeSoDiskCacheHasProoooooooooooblem(
			Integer number, Integer number2) throws InterruptedException {
		Thread.sleep(2000);

		BogusClass bogusClass = new BogusClass();
		bogusClass.setResult(number + number2);
		return bogusClass;
	}

	// 70
	@Cached
	public BogusClass nr34567890123456789012345678901234567890123456789012345678901234567890(
			Integer number, Integer number2) throws InterruptedException {
		return null;
	}

	@Cached(name=Names.cacheNameOne,category=Names.cacheCategoryA)
	public int imNamed(Integer number){
		return 0;
	}
static int add=0;
	@Cached(SelfPopulatingScheduledCache=true,refreshTime=1500)
	public int serveStaleAndRefreshedData(Integer number1, Integer number2) {
		add+=number1+number2;
		add++;
		return add;
	}

}
