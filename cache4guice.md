# Introduction #

cache4guice is a simple annotation based cache integration for http://ehcache.org/ .

# NEWS 30-08-2012 #
## Major new feature ##
**SelfPopulatingScheduledCache**
> A cache that gets updated via a quartz scheduled task. Internally it uses a eternal ehcache which gets refreshed from the quartz job.


# Example #


### Annotate method to be cached ###

```
	@Cached
	public int calculateSomethingWild(Integer number)
			throws InterruptedException {
		Thread.sleep(2000);

		return number;

	}


```

### Install Cache Module ###
```
Guice.createInjector(new CacheModule())

or install(new CacheModule);
```
### Override CacheKeyGenerator ###

To determine if a cache are hit or not, you need an keyGenerator.

Cache4Guice provides 3 different implementations, either a StringBasedKeyGenerator a Interface based or a compound based.

To override, subclass CacheModule and override getCacheKeyGenerator or roll your own module.

### Configure Cache Settings ###

Either roll your own cache provider or add the ehcache.xml to class path..

Checkout all the features here http://ehcache.org/documentation/getting_started.html