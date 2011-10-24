package org.slurry.cache4guice;

public interface Calculator {

	public int calculateSomethingWild(Integer number)
			throws InterruptedException;

	public int calculateSomethingWild(Integer number, Integer number2)
			throws InterruptedException;

	public BogusClass veryLooooooooooooooooooooooooooooooooongNaaaaaaaammeeeeSoDiskCacheHasProoooooooooooblem(
			Integer number, Integer number2) throws InterruptedException;

}