Collection of helper tools, helpful as http://en.wikipedia.org/wiki/Slurry

Like Cache4Guice , ehcache for guice integration
Or the upcomming Quartz4Guice, Quartz scheduler guice integration

All projects use annotations for configuration and are focused on convenience.

Slurry has two maven repos one for snapshots and one for releases
Depending on if you want snapshot or releases add these:
```
<repository>
	<uniqueVersion>true</uniqueVersion>
	<id>slurry-release</id>
	<name>Slurry Release</name>
	<url>http://slurry.googlecode.com/svn/maven/release</url>
	<snapshots>
		<enabled>false</enabled>
	</snapshots>
	<releases>
		<enabled>true</enabled>
	</releases>
</repository>

```

```
<repository>
	<id>slurry-snapshot</id>
	<name>Slurry Snapshot</name>
	<url>http://slurry.googlecode.com/svn/maven/snapshot</url>
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
	<releases>
		<enabled>false</enabled>
	</releases>
</repository>
```