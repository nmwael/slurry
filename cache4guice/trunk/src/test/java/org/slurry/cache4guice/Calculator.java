package org.slurry.cache4guice;

public interface Calculator {

	public int calculateSomethingWild(Integer number)
			throws InterruptedException;

	public int calculateSomethingWild(Integer number, Integer number2)
			throws InterruptedException;

	public BogusClass veryLooooooooooooooooooooooooooooooooongNaaaaaaaammeeeeSoDiskCacheHasProoooooooooooblem(
			Integer number, Integer number2) throws InterruptedException;
	
	public int imNamed(Integer number);
	
	public Integer sloowOperation(Integer number);
	
	public int serveStaleAndRefreshedData(Integer number1,Integer number2);


}