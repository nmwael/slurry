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

	public Integer getSloowOperationCounter();

	public int serveStaleAndRefreshedData(Integer number1,Integer number2);

	public String delegatedMethod();

	public int serveStaleAndRefreshedDataSometimesThrowError(int i, int j);
	
	public Integer serveStaleAndRefreshedDataSometimesThrowError();

    public int serveStaleAndRefreshedDataWithWait(Integer numberA, Integer numberB, Integer sleep);
	
	


}