class: center, middle

# Functional fun

.center[![doccore](dogs_small.jpg)]

Jan Ypma

`jyp@tradeshift.com`

Slides: [http://jypma.github.io](http://jypma.github.io)

---
# Functional programming

- What is functional programming?

--

John Hughes:

> Functions with *no side effects* operating on *immutable data structures*

Wikipedia:

> [...] *avoids changing state* and mutable data
> [...] expressions or *declarations* instead of statements

---
# Pure functions

- A function has **side effects** if it has an observable effect besides returning a value

- A function with no side effects is a **pure function**

- A pure function will always return the **same result** for the **same arguments**, since history cannot affect it

---
# Pure functions

What can we expect from this function?

```js
function add(a, b) {
  // [...]
}
```

---
# Pure functions

What can we expect from this function?

```js
function add(a, b) {
  return a + b;
}
```

---
# Pure functions

What can we expect from this function?

```js
function add(a, b) {
  return "It was " + a + " and " + b;
}
```

---
# Pure functions

What can we expect from this function?

```js
var found;

function add(a, b) {
  found += a;
  found += b;
  return found;
}
```

---
# Pure functions

What can we expect from this function?

```js
var found;

function add(a, b) {
  found += a;
  found += b;
}
```

---
# Pure functions: other languages

What can we expect from this function?

_Scala_

```scala
def add(a: Int, b: Int): Int = {
  a + b
}
```

--

_Haskell_

```haskell
add :: Int -> Int -> Int
add a b = a + b
```

---
# Pure functions

Advantages:

--

- Testable
- Composable (with enough compiler help)

---
# Higher-order functions

Say we have a list of numbers:
```scala
val nums: Seq[Int] = Seq(1, 2, 3)
```

But we want to put it into a function that wants a list of strings:
```scala
def count(lines: Seq[String]): Int
```

--
We could go old-school:
```scala
val result = collection.mutable.Seq.empty[String]

for (i <- nums) {
  result += i.toString
}

count(result.toSeq)
```

---
# Higher-order functions

We want to apply a function to every element in the list.

Assume the following exists:
```scala
def applyToEach(list: Seq[Int], function: Int => String): Seq[String]
```

We can now write
```scala
val nums: Seq[Int] = Seq(1, 2, 3)

def numberToString(num: Int): String = num.toString

count(applyToEach(nums, numberToString))
```

---
# Higher-order functions

Having to introduce a name for `numberToString` is quite tedious. Most languages don't require this these days.

A **lambda function** is a function with no name, which is typically written as a literal in an argument list
where a function is expected.

These exist in Scala (`=>`), Java (`->`), Haskell (`\`), Clojure (`fn`), ...

Our example now becomes
```scala
count(applyToEach(nums, i => i.toString))
```

or even

```scala
count(applyToEach(nums, _.toString))
```

---
# Higher-order functions

Most languages have the "apply this to everything" operator in their standard library.

It's usually called `map`.

_Scala_
```scala
count(nums.map(_.toString))
```

_JavaScript_ (immutableJS)
```js
count(nums.map(i => i.toString()))
```

_Haskell_
```haskell
count (map show nums)

-- or the equivalent :
count $ map show nums
```

---
# Immutable data structures

- Writing pure functions requires guarantees that their arguments aren't being modified
- Can't use `array`, `java.util.Collection`, `scala.collection.mutable`, ...

An **immutable data structure** is an object that codifies a data structure, but can't be modified after initial creation.

- In order to effectuate change, a copy has to be created.

---
# Immutable data structures

### Naive example

Let's wrap an array, and copy it when we append, so it's nice and immutable:

```scala
class MyList(private items: Array[String]) {
  def add(s: String): MyList = {
    val newItems = new Array[String](items.size + 1)
    Array.copy(items, 0, newItems, 0, items.size)
    newItems(newItems.size - 1) = s
    new MyList(newItems)
  }
}
```

--
...that's pretty expensive!

---
# Immutable data structures

A linked list is a pretty cool, immutable data structure:

```scala
trait LinkedList
object Empty extends LinkedList
case class Item(head: String, tail: LinkedList) extends LinkedList
```

--
- We can always prepend to the list by just creating a new object
- All the existing list is actually re-used

A **persistent data structure** is an immutable data structure that re-uses (part of) existing immutable instances.

---
# Exercise 1: Pure functions and randomness

We have three different functions that each return a welcome message based on a `name` parameter.

Write a **pure** function that, given a `name` argument (and possibly other arguments), randomly invokes one of the three target functions.

---
# Exercise 2: Immutable data structures

Write an **immutable, persistent list-like** data structure that is:
- Order-preserving
- Does not check for duplicates
- Has an efficient operation for appending at the end
- Has an efficient operation for prepending at the beginning
- Has an efficient operation for iterating through the list in order

---
# Why are lambda functions called that way?

It's... complicated. From
[stackexchange](https://math.stackexchange.com/questions/64468/why-is-lambda-calculus-named-after-that-specific-greek-letter-why-not-rho-calc):

> We end this introduction by telling what seems to be the story how the letter 'λ' was chosen to denote
> function abstraction. In _Principia Mathematica_ the notation for the function `f` with `f(x)=2x+1` is
> `2x̂+1`. Church originally intended to use the notation `x̂.2x+1`. The typesetter could not position the hat on
> top of the _x_ and placed it in front of it, resulting in `∧x.2x+1`. Then another typesetter changed it into
> `λx.2x+1`.

---
class: center, middle
# Functional fun

.center[![doccore](lambduh_small.jpg)]

Session 2

Jan Ypma

`jyp@tradeshift.com`

Slides: [http://jypma.github.io](http://jypma.github.io)

---
# Exercise 1: Pure functions and randomness

Write a **pure** function that, given a `name` argument (and possibly other arguments), randomly invokes one of three target functions.

--

```scala
def welcome1(name: String): String = ???
def welcome2(name: String): String = ???
def welcome3(name: String): String = ???

def welcome(name: String, ???): String = ???
```

--

What can we use to represent "randomness" ? What is _randomness_, really?

---
# Exercise 1: Pure functions and randomness

Psuedo-random number generator:
- Take a _seed_ (or some other entropy)
- Generate a next random number on request

--

Let's formalize that in an immutable data structure:

```scala
case class RNG(entropy: ByteString) {
  def nextLong(max: Long): (Long, RNG) = ???
}
```

---
# Exercise 1: Pure functions and randomness

Let's use it in our function:

```scala
def welcome(name: String, random: RNG): (String, RNG) = {
  random.nextLong(3) match {
    case (0, r) => (welcome1(name), r)
    case (1, r) => (welcome2(name), r)
    case (2, r) => (welcome3(name), r)
  }
}
```

--

Notes
- Randomness can be represented as an immutable data structure
  - (to some extent)
- Code doesn't look so nice yet
  - Let's revisit it after we deal with the "m" word.

---
# Exercise 2: Immutable data structures

Write an **immutable, persistent list-like** data structure that can performantly _append_ AND _prepend_.

--
- A linked list can efficiently prepend
- What if we postpone all appends until later?
  - Store append operations in a function
- From Haskell: [DiffList](http://h2.jaguarpaw.co.uk/posts/demystifying-dlist/)

---
# Exercise 2: Immutable data structures

.floatleft[

```scala
class DiffList[A](make: List[A] => List[T]) {
  // prepend: O(1)
  def +:(a: A) = new DiffList[A] (
    z => a :: make(z)
  )

  // append: O(1)
  def :+(a: A) = new DiffList[A] (
    z => make(a :: z)
  )

  // appendAll: O(1)
  def ++=(as: DiffList[A]) = new DiffList[A] (
    z => make(as.make(z))
  )

  // O(n)
  def toList = make(Nil)
}

object DiffList {
  def empty[A] = new DiffList(z => z)
}
```
]

--

Rough sketch as to why this works:

```
empty[Int] :
  make = z => z

3 +: empty[Int] :
  make = z => 3 :: (empty[Int].make(z))
       = z => 3 :: z

3 +: empty[Int] :+ 4 :
  make = z => (3 +: empty[Int]).make(4 :: z)
       = z => 3 :: 4 :: z
```

.floatleft[
.small[source: https://blog.tmorris.net/posts/list-with-o1-cons-and-snoc-in-scala/]
]

---
# Exercise 2: Immutable data structures

Notes
- Alternative: [Finger tree](http://andrew.gibiansky.com/blog/haskell/finger-trees/)

---
# Asynchronous programming

- What is _asynchronous programming_?

--

- Why would you want to program like that?
  - Default Java 8 thread stack size is 1M
  - 200 connections is 200MB memory, _just for the stacks_

- But also, threads can be a performance bottleneck *

.smallright[`* on compiled code]

---

# Latency in computer systems

This is how long certain operations take for a CPU (2012, [source](https://people.eecs.berkeley.edu/~rcs/research/interactive_latency.html))

```text
L1 cache reference                           0.5 ns
Branch mispredict                            5   ns
L2 cache reference                           7   ns
Mutex lock/unlock                           25   ns
Main memory reference                      100   ns
Compress 1K bytes with Zippy             3,000   ns
*Context switch                          10,000   ns (if working set in L2)
Send 1K bytes over 1 Gbps network       10,000   ns
*Context switch                        >100,000   ns (bigger working set)
Read 4K randomly from SSD*             150,000   ns
Read 1 MB sequentially from memory     250,000   ns
Round trip within same datacenter      500,000   ns
Read 1 MB sequentially from SSD*     1,000,000   ns
Disk seek                           10,000,000   ns
Read 1 MB sequentially from disk    20,000,000   ns
Send packet CA->Netherlands->CA    150,000,000   ns
```

---
# Threads and mutexes

- **Thread** : logical thread of execution, scheduled by OS scheduler
- All threads share one memory space
- **Mutex** : _mutually exclusive_ flag that prevents threads from accessing shared resources

--

Advantages
- Close to how the hardware actually works
- The only model generally tought in school
- Blocking on I/O natually equivalent to "this, then this"

Disadvantages
- Expensive (~1M stack)
- Slow to create/destroy
- Deadlock


---
# Futures and callbacks

- **Future** is a handle to a calculation which _eventually_ yields a result
  - Scala, Finagle, Q.js, ... : _Future_ / _Promise_
  - Java 8: `CompletionStage<T>` / `CompletableFuture<T>`
- Can be chained, causing further processing (and further futures) after a stage
  completes
- Can be merged, waiting for several futures' results before commencing

```java
CompletionStage<Done> =

http.request(GET("/tweets"))
    .thenApply(response -> response.getBody())
    .thenApply(body -> parseJSON(body))
    .thenCompose(tweets ->
        http.request(PUT("/subscribe?topic=" + tweets.getMostPopular())))
    .thenApply(response -> Done.getInstance())
    .whenComplete((r,x) -> {
        if (x != null) {
            log.error("Well something went wrong", x);
        }
    });
```

---
# Futures and callbacks

Advantages
- Simple to grasp

Disadvantages
- only one value: no obvious way to stream data
- "callback hell" (remember `RNG`?)
- brittle error handling
- not (easily) cancellable
- still needs locking for shared resources (unless using a single thread)

---
# Functional asynchronous programming

Functional programming:
> Functions with *no side effects* operating on *immutable data structures*

- No side effects? `http.request(...)`
--

- We need to postpone side effects: _laziness_

```
                    | eager                        | lazy
--------------------------------------------------------------------
synchronous         | T                            | =>T         (scala)
                    |                              | Supplier[T] (java)
----------------------------------------------------------------
asyncronous         | Future[T]          (scala)   | IO[T, E]    (zio)
                    | CompletionStage[T] (java)    | Task[T]     (scalaz 7, monix)
```
---
# Programming with zio

- [ZIO](https://zio.dev/docs/overview/overview_index) is a modern Scala library for asynchronous and lazy
  computations

- Defines a single type `ZIO[R, E, A]`
  - `A` is the thing being lazilly, eventually computed
  - `E` is the type of any error (e.g. `IOException`, just `String`, or even `Nothing`)
  - `R` is an environment that's needed for the computation

- _takes an `R` and turns it, eventually, either into an `A` or an `E`_

--

- Type aliases
  - `Task[A] = ZIO[Any, Throwable, A]` is a computation with no environment requirements, and may fail with
    any `Throwable`
  - `IO[E,A] = ZIO[Any, E, A]` is a computation with no environment requirements
  - `UIO[A] = ZIO[Any, Nothing, A]` is a computation that's guaranteed to never fail

---
# Programming with zio

- Let's write a bit of hello world

```scala
import scalaz.zio._
import scalaz.zio.console._
import java.io.IOException

val readName: ZIO[Any, IOException, String] = getStrLn
val greet: ZIO[Any, IOException, Unit] = readName.flatMap(name => welcome(name))

object HelloWorld extends App {
  override def run(args: List[String]): IO[DefaultRuntime, Exception, ExitStatus] =
    greet.map(_ => ExitStatus.ExitNow(0))
}
```

---
# Programming with zio

- Some other interesting abstractions
  - delaying execution:
  ```scala
  def sleep(duration: Duration): ZIO[Clock, Nothing, Unit]
  ```
  - using the current date/time:
  ```scala
  def currentDateTime: ZIO[Any, Nothing, OffsetDateTime]
  ```
  - random numbers:
  ```scala
  def nextLong(n: Long): ZIO[Random, Nothing, Long]
  ```

---
# Exercise 3

- Pick an asynchronous, lazy computation library for your language
  - **Scala**: [ZIO](https://zio.dev/docs/overview/overview_index) [Monix](https://monix.io/docs/3x/eval/task.html) [Cats-effect](https://typelevel.org/cats-effect/datatypes/io.html)
  - **Javascript**: [Fluture](https://github.com/fluture-js/Fluture)
  - **Java**: ...didn't find anything. `Supplier<CompletionStage<T>>` perhaps. Or write-it-yourself.

- Google keywords: `<language-name> asynchronous io monad`

- Using your library, write a program that **recursively** searches a directory and all its files, and **counts** the
  number of **characters**, **words**, and **lines** in those files.

---
# Exercise 4

- Using the same library, write a program that recursively searches a directory and all its files for lines
  containing a given string.
  - Your program must be able to read from several files at once

- Optional extra parts
  - Make sure your program doesn't crash when encountering really long files with no line breaks
  - Make sure your program doesn't hang when encountering symbolic link cycles
  - Extend your program to find expressions spanning multiple lines

---
Notes
```
- Nullability
- Lambdas vs. functions vs. methods
  - Separation of concerns example
- Data structure transformations
  - Persistent data structures
- Asynchronous programming
  - The problems with `Future`
  - Introducing `IO` / `Task`
- Streams
- Abstracting over semantics
  - Type classes
    - vs. interfaces (can't express empty, return type of combine)
  - Functor, Applicative, Monad (and how to lift primitives into these) ```
http://adit.io/posts/2013-04-17-functors,_applicatives,_and_monads_in_pictures.html
