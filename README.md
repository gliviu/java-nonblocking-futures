java-nonblocking-futures
==========
Asynchronous java processing inspired by [Akka/Scala promises and futures](http://doc.akka.io/docs/akka/snapshot/java/futures.html).


## Installation
For the moment copy Future.java and optionally Promise.java in your project. 

## API

### Future
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
Future.timeout(Duration)
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

### Promise
Promise is very similar to Future, meaning it can have a completion status of success or failure.

However after creation it can be manually completed using ```success(Object)``` or ```failure(Throwable)``` while ```Future``` is read-only.

Get access to underlying Future by using ```future()```.

## Examples

### Completion callbacks
```java
ExecutorService executor = Executors.newFixedThreadPool(4);
Future future1 = Future.future(()->{
	sleep(100); 
	return "ready";
}, executor);
Future future2 = Future.future<Integer>(()->{
	sleep(200); 
	throw new RuntimeException("failure");
}, executor);
future1.onComple((fail, result)->{
	if(fail!=null){
		System.out.println("failure1: " + fail.getMessage());
	} else{
		System.out.println("success1: " + result);
	}
});
future1.onSuccess(result -> System.out.println("success2: " + result));
future2.onSuccess(result -> System.out.println("success: " + result));
future2.onFailure(fail -> System.out.println("fail: " + fail.getMessage()));

>> success1: ready
>> success2: ready
>> fail: failure
```


### Create already completed futures
```java
Future.successfull(3)
	.onSuccess(result -> System.out.println(result));
Future.failed(new RuntimeException("error"))
	.onFailed(fail -> System.out.println(fail.getMessage()));

>> 3
>> error
```

### Map

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
Future future = Future.future(() -> {
	sleep(100);
	return 5
}, executor);

future.map(result -> return "res="+result)
	.onSuccess(result -> System.out.println(result));

>> res=5
```

### Flat map
```java
Future<String> asyncProcess(Integer value){
	ExecutorService executor = Executors.newFixedThreadPool(4);
	return Future.future(() ->{
		sleep(200);
		return "res: " + value;
	});
}
```
```java
ExecutorService executor = Executors.newFixedThreadPool(4);

Future<Integer> future = Future.future(() -> {
	sleep(100);
	return 5
}, executor);
future.flatMap(result -> {
	return asyncProcess(result);
}).onSuccess(result -> System.out.println(result));

>> res=5
```
