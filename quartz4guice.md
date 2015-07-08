#quartz4guice

# Introduction #

Quartz4Guice is a simple integration towards Quartz, to remove the boilerplate code.


# Details #

To setup a scheduled task just do this:

  * Install the schedule module
```
injector = Guice.createInjector(new ScheduleModule())
```
  * Annotate your class you wish to schedule, implement the job interface and bind it in your module

Annotate (using cron expression) and implement job:
```
@Scheduled(cron = "0/3 * * * * ?")
public class TimedTasks implements  Job {
```
Bind:
```
bind(InterfaceContainingTimedTask.class).to(TimedTasks.class);
```



Thats it :)