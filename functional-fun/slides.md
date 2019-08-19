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
