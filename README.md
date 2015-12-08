java-nonblocking-futures
==========
Asynchronous java processing inspired by [Akka/Scala promises and futures](http://doc.akka.io/docs/akka/snapshot/java/futures.html).


## Installation
Maven
```xml
<dependency>
	<groupId>com.github.gliviu</groupId>
	<artifactId>java-nonblocking-futures</artifactId>
	<version>1.0.0</version>
</dependency>
```

While the library is JDK 1.5 compatible, examples and unit tests are designed for 1.8. By default tests are disabled and have to be activated manually when JDK 1.8 is available.

```mvn test -Dmaven.test.skip=false```


## Future
Futures are objects that represent the results of asynchronous computations.

They are read-only and can be in any of these states once:
* pending - asynchronous code is executing
* fulfilled - operation completed successfully
* rejected - operation failed

Create new futures using
```java
Future.future(Callable, ExecutorService)
Future.successful(Object)
Future.failed(Throwable)
Future.timeout(int, TimeUnit)
```
Handlers are provided to process the result.
```java
onComplete(CompleteHandler)
onSuccess(Handler)
onFailure(Handler)
```
Composing futures can be achieved using
```java
flatMap(Mapper)
map(Mapper)
recover(RecoverHandler)
Future.all(Iterable)
Future.first(Iterable)
```
Use ``` waitResult() ``` to wait synchronously for future completion and isComplete() to check its state.

## Promise
Promise is very similar to Future, meaning it can have a completion status of success or failure.

However after creation it can be manually completed using ```success(Object)``` or ```failure(Throwable)``` while ```Future``` is read-only.

Create them with ```promise()```.
Get access to underlying Future using ```future()```.

## Examples

### Completion callbacks
```java
ExecutorService executor = Executors.newFixedThreadPool(4);
Future<String> future = Future.future(()->{
   sleep(100); 
   return "ready";
}, executor);
future.onComplete((fail, result)->{
   if(fail!=null){
      System.out.println("failure1: " + fail.getMessage());
   } else{
      System.out.println("success1: " + result);
   }
});
future.onSuccess(result -> System.out.println("success2: " + result));
future.onSuccess(result -> System.out.println("success3: " + result));
executor.shutdown();

>> success2: ready
>> success1: ready
>> success3: ready
```
Note that the order of executing callbacks is not guaranteed.

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
Future<Integer> future = Future.future(()->{
   sleep(200); 
   throw new RuntimeException("failure");
}, executor);
future.onSuccess(result -> System.out.println("success: " + result));
future.onFailure(fail -> System.out.println("fail: " + fail.getMessage()));
executor.shutdown();

>> fail: failure
```

### Create already completed futures
```java
Future.successful(3)
	.onSuccess(result -> System.out.println(result));
Future.failed(new RuntimeException("error"))
	.onFailure(fail -> System.out.println(fail.getMessage()));

>> 3
>> error
```

### Map

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
Future<Integer> future = Future.future(() -> {
   sleep(100);
   return 5;
}, executor);

future.map(result -> "res="+result)
   .onSuccess(result -> System.out.println(result));
executor.shutdown();

>> res=5
```

### Flat map
```java
Future<String> asyncProcess(Integer value){
   ExecutorService executor = Executors.newFixedThreadPool(4);
   Future<String> future = Future.future(() ->{
      sleep(200);
      return "res: " + value;
   }, executor);
   executor.shutdown();
   return future;
}
```
```java
ExecutorService executor = Executors.newFixedThreadPool(4);

Future<Integer> future = Future.future(() -> {
   sleep(100);
   return 5;
}, executor);
future.flatMap(result -> {
   return asyncProcess(result);
}).onSuccess(result -> System.out.println(result));
executor.shutdown();

>> res=5
```

### Wait for futures synchronously
```java
ExecutorService executor = Executors.newFixedThreadPool(4);

Future<Integer> future1 = Future.future(() -> {
   sleep(100);
   return 5;
}, executor);
Future<Integer> future2 = Future.future(() -> {
   sleep(200);
   throw new RuntimeException("error");
}, executor);

try{
   System.out.println(future1.waitResult());
   System.out.println(future2.waitResult());
}catch(Throwable e){
   System.out.println(e.getMessage());
}
executor.shutdown();

>> 5
>> error
```

### Wait for multiple futures to complete
```java
ExecutorService executor = Executors.newFixedThreadPool(4);

Future<Integer> future1 = Future.future(() -> {
   sleep(100);
   return 5;
}, executor);
Future<Integer> future2 = Future.future(() -> {
   sleep(200);
   return 2;
}, executor);

Future.all(future1, future2).onComplete((fail, result) -> {
   if(fail!=null){
      System.out.println(fail.getMessage());
   } else{
      for(Integer i : result){
         System.out.println(i);
      }
   }
});
executor.shutdown();

>> 5
>> 2
```

### Wait for first future to complete
```java
ExecutorService executor = Executors.newFixedThreadPool(4);

Future<Integer> future1 = Future.future(() -> {
	sleep(200);
	return 5;
}, executor);
Future<Integer> future2 = Future.future(() -> {
	sleep(100);
	return 2;
}, executor);

Future.first(future1, future2).onComplete((fail, result) -> {
	if(fail!=null){
		System.out.println(fail.getMessage());
	} else{
		System.out.println(result);
	}
});
executor.shutdown();

>> 2
```

### Timeouts
```java
ExecutorService executor = Executors.newFixedThreadPool(4);

Future<Integer> future = Future.future(() -> {
   sleep(2000);
   return 5;
}, executor);

Future<Integer> timeout = Future.timeout(1000, TimeUnit.MILLISECONDS);

Future.first(future, timeout).onComplete((fail, result) -> {
   if(fail!=null){
      if(fail instanceof TimeoutException){
         System.out.println("timeout occurred");
      } else{
         System.out.println(fail.getMessage());
      }
   } else{
      System.out.println(result);
   }
});
executor.shutdown();

>> timeout occurred
```


