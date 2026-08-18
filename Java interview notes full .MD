# Java Backend Interview Notes — Full Answers

Complete reference with all questions and answers, covering Core Java, Collections, Java 8+, Multithreading, JVM Internals, and Coding Problems.

---

## Core Java & OOP

### 1. The 4 Pillars of OOP
- **Encapsulation (Data Hiding):** Wrapping data and code together into a single unit, keeping fields private and exposing them via public getters/setters.
  - *Example:* An ATM — you use a simple interface, but internal cash-counting and DB logic are hidden.
- **Abstraction (Implementation Hiding):** Hiding complex implementation, showing only essential features.
  - *Example:* Driving a car — you know accelerating works, not the engine internals.
- **Inheritance (Reusability):** A new class acquires properties/behaviors of an existing class.
  - *Example:* A smartphone inherits core telephone features and adds its own.
- **Polymorphism (Many Forms):** A single action/reference behaves differently depending on context.
  - *Example:* "Cut" — scissors for cloth, knife for cake, clipboard for text.

### 2. Abstract Class vs. Interface
| Feature | Abstract Class | Interface |
|---|---|---|
| Methods | Abstract, concrete, static | Historically abstract only; now default/static/private (Java 8+) |
| Variables | Instance variables allowed | Only `public static final` constants |
| Inheritance | Extend only one abstract class | Implement multiple interfaces |
| Speed | Slightly faster | Slightly slower |

**When to use:** Abstract class for closely related objects sharing state/behavior (Vehicle → Car, Truck). Interface for a contract/capability across unrelated classes (Flyable → Airplane, Bird).

### 3. Why Java Doesn't Support Multiple Inheritance with Classes
Avoids the **Diamond Problem**: if class A has `foo()`, and B & C both override it while inheriting A, class D inheriting both B & C wouldn't know which version to use. Java avoids this entirely for classes. For interfaces (default methods), Java forces the developer to manually resolve conflicts.

### 4. Polymorphism: Static vs. Dynamic
- **Static (Compile-time):** Method Overloading — same name, different signature. E.g., `Math.max(int,int)` vs `Math.max(double,double)`.
- **Dynamic (Runtime):** Method Overriding — subclass provides its own implementation; resolved via Dynamic Method Dispatch. E.g., `Animal a = new Dog(); a.makeSound();` calls Dog's version at runtime.

### 5. Abstraction vs. Encapsulation
- **Abstraction:** Design-level — focuses on *what* an object does (via interfaces/abstract classes).
- **Encapsulation:** Implementation-level — focuses on *how* data is secured (via private fields + getters/setters).

### 6. Aggregation vs. Composition
- **Aggregation (Weak):** Child can exist independently. E.g., Department has Professors — department closes, professors still exist.
- **Composition (Strong):** Child cannot exist without parent. E.g., House has Rooms — house demolished, rooms cease to exist.

### 7. Why is String Immutable in Java?
1. **String Pool:** Shared literals — mutability would corrupt all references.
2. **Security:** Strings used in URLs/paths/connections — mutability could allow tampering.
3. **Thread Safety:** Immutable = inherently thread-safe.
4. **Cached HashCode:** Computed once, fast for use as HashMap keys.

### 8. final vs. finally vs. finalize
- **final:** Restriction keyword — class can't be inherited, method can't be overridden, variable can't be reassigned.
- **finally:** Block in try-catch that always executes (except JVM crash/`System.exit()`), used for cleanup.
- **finalize():** Object class method called by GC before destruction (deprecated since Java 9).

### 9. Marker Interface & Real-time Use Cases
An empty interface (no methods/fields) used as a tag/metadata instruction.
- `Serializable` — object can be converted to byte stream.
- `Cloneable` — legal to field-for-field copy via `clone()`.
- `RandomAccess` — collection supports fast constant-time access (ArrayList vs LinkedList).

### 10. Can We Override a Static Method?
No — overriding relies on runtime dynamic binding; static methods use compile-time static binding (based on reference type). Redeclaring a static method in a child class is **Method Hiding**, not overriding.

### 11. Effective Final Concept (Java 8+)
A variable not explicitly `final` but never reassigned after initialization. Introduced so local variables can be used inside lambdas/anonymous classes without explicitly writing `final`.

### 12. Checked Exceptions: Pros & Cons
Checked at compile-time (e.g., `IOException`, `SQLException`) — must be handled via try-catch or declared with `throws`.
- **Pros:** Forces developers to anticipate and handle predictable external failures.
- **Cons:** Boilerplate clutter; can leak implementation details up the call stack.

### 13. `String s = new String("hello");` — Objects Created
Creates **2 objects**:
1. One in Heap (due to `new`).
2. One in the String Constant Pool (only if `"hello"` isn't already there; otherwise 1 object total).

### 14. Dynamic Class Loading
Loading a class into memory at runtime (not compile-time) via `Class.forName()` or a custom `ClassLoader`.
- **Uses:** JDBC drivers (`Class.forName("com.mysql.cj.jdbc.Driver")`), plugin architectures (IDEs, Tomcat loading extensions without restart).

### 15. Methods Available in the Object Class
`java.lang.Object` has 11 methods:
1. `getClass()` — runtime class
2. `hashCode()` — hash value
3. `equals(Object obj)` — equality check
4. `clone()` — copy object
5. `toString()` — string representation
6. `notify()` — wake one waiting thread
7. `notifyAll()` — wake all waiting threads
8. `wait()` — wait until notified
9. `wait(long timeout)` — wait for duration
10. `wait(long timeout, int nanos)` — wait precisely
11. `finalize()` — GC cleanup hook (deprecated)

---

## Collections Framework

### 16. Collection vs. Collections
- **Collection:** Root interface (`java.util.Collection`) for List, Set, Queue.
- **Collections:** Utility class of static methods (`sort()`, `reverse()`, `unmodifiableList()`).

### 17. Why Map is not part of the Collection interface
`Collection` handles single elements; `Map` handles key-value pairs with different method signatures (`put(K,V)` vs `add(E)`), making it structurally incompatible.

### 18. List vs. Set vs. Map
| Feature | List | Set | Map |
|---|---|---|---|
| Element Type | Single | Single | Key-Value |
| Duplicates | Allowed | Not allowed | Unique keys; dup values OK |
| Order | Insertion order | Depends on impl | Depends on impl |
| Null | Multiple allowed | At most one (usually) | One null key, multiple null values |

### 19. ArrayList vs. LinkedList
- **ArrayList:** Dynamic array. `get()` = O(1). Insert/delete = O(n) worst case (shifting).
- **LinkedList:** Doubly-linked list. `get()` = O(n) (traversal). Insert/delete = O(1) once position found.

### 20. ArrayList vs. Vector, CopyOnWriteArrayList
- **ArrayList:** Not synchronized, fast, grows 50%.
- **Vector:** Synchronized, legacy, slower, doubles (100%) on growth.
- **CopyOnWriteArrayList:** Best for read-heavy multi-threaded use — writes clone the array (expensive), reads are lock-free.

### 21. HashSet vs. LinkedHashSet vs. TreeSet
- **HashSet:** Best performance, unpredictable order.
- **LinkedHashSet:** Maintains insertion order via linked list over hash buckets.
- **TreeSet:** Sorted (natural/Comparator), backed by Red-Black tree.

### 22. How HashSet Prevents Duplicates Internally
Internally backed by a `HashMap`. `add(element)` calls `map.put(element, PRESENT)` — since map keys must be unique, duplicates are rejected.

### 23. HashMap vs. LinkedHashMap vs. TreeMap
- **HashMap:** No order guarantee, fastest (average O(1)).
- **LinkedHashMap:** Maintains insertion/access order.
- **TreeMap:** Sorted keys (natural or Comparator), O(log n).

### 24. How HashMap Works Internally
1. **Hashing:** `hashCode()` of key → hash function → bucket index.
2. **Buckets:** Array of nodes.
3. **Collision:** Same bucket → stored as LinkedList; if bucket size > 8 and capacity ≥ 64, converts to Red-Black Tree (Java 8+) for O(log n) worst case.
4. **Resizing:** When threshold exceeded, array doubles and all elements are rehashed.

### 25. Why is HashMap not Thread-Safe
Methods aren't synchronized — concurrent modification (especially during resize) can cause data corruption, race conditions, or even cyclic linked-list loops causing infinite loops on `get()`.

### 26. Load Factor in HashMap
Default **0.75** — ratio of fullness that triggers resize. When exceeded, array size doubles and all keys are rehashed to new bucket positions.

### 27. HashMap vs. ConcurrentHashMap
- **HashMap:** Not thread-safe; would need full-object synchronization.
- **ConcurrentHashMap:** Thread-safe, highly concurrent, no global lock — multiple threads read/write simultaneously.

### 28. How ConcurrentHashMap Works Internally
Uses fine-grained locking (`synchronized` on individual bucket heads) + CAS operations. Empty bucket → lock-free CAS insert. Populated bucket → locks only that bucket, letting other threads modify different buckets concurrently.

### 29. Mutable Objects as HashMap Keys
**Discouraged.** If a key's fields mutate after insertion, its `hashCode()` changes — future lookups search the wrong bucket, making the value unretrievable (silent memory leak).

### 30. How TreeMap Maintains Sorting
Uses a **Red-Black tree** — every insertion is placed in sorted position via `compareTo()` (Comparable) or a custom `Comparator`.

### 31. Fail-Fast vs. Fail-Safe Iterators
- **Fail-Fast:** Iterates the actual structure; throws `ConcurrentModificationException` if structurally modified during iteration (e.g., ArrayList).
- **Fail-Safe:** Iterates a clone/view; no exception on concurrent modification (e.g., CopyOnWriteArrayList).

### 32. Why equals() and hashCode() Matter in Collections
They define object identity/bucket allocation for hashed collections. `hashCode()` picks the bucket; `equals()` confirms the exact match within it. Broken implementations break HashMap/HashSet lookups.

### 33. Same hashCode() — Does equals() Return True?
Can be **either**. A hash collision means different objects share a hash code; `equals()` then determines actual structural equality.

### 34. equals() True — Is hashCode() Always Same?
**Yes**, per the Java contract — if `equals()` says two objects are equal, `hashCode()` must return the same value for both.

---

## Java 8+ Features

### 35. Major Features Introduced in Java 8
- Lambda Expressions
- Stream API
- Functional Interfaces (`@FunctionalInterface`)
- Default & Static methods in interfaces
- `Optional` class
- New Date/Time API (`java.time`)

### 36. Functional Interfaces
Interface with exactly **one abstract method** (plus any default/static methods).
- `Predicate<T>` — `test(T)` → boolean
- `Function<T,R>` — `apply(T)` → R
- `Consumer<T>` — `accept(T)` → void
- `Supplier<T>` — `get()` → T

### 37. Default Methods in Interfaces
Methods with a body inside an interface (`default` keyword) — lets you add new methods without breaking existing implementers.
**Before Java 8:** Abstract classes or companion utility classes (e.g., `Collection` + `Collections`).

### 38. Diamond Problem with Default Methods
If two interfaces have the same default method and a class implements both → **compiler error**. Must explicitly resolve:
```java
@Override
public void log() {
    InterfaceA.super.log(); // explicitly choose
}
```

### 39. map() vs. flatMap() in Streams
- **map():** 1-to-1 transformation (e.g., `Stream<String>` → `Stream<Integer>`).
- **flatMap():** 1-to-many transformation + flattening (e.g., `Stream<List<String>>` → flat `Stream<String>`).

### 40. Terminal vs. Intermediate Stream Operations
- **Intermediate:** Transform stream → stream (`filter()`, `map()`, `sorted()`); lazy, don't execute until terminal op called.
- **Terminal:** Produce result/side-effect (`collect()`, `forEach()`, `count()`, `reduce()`); triggers pipeline execution, consumes the stream.

### 41. What is Optional?
A container that may or may not hold a non-null value. Solves the problem of explicit null-checks, reducing `NullPointerException` risk.

### 42. isPresent() vs. ifPresent()
- `isPresent()` — boolean check, needs separate `.get()` call (imperative).
- `ifPresent(Consumer)` — runs a lambda only if value present (functional, no explicit branch).

### 43. New Features in Java 11 and Java 17
**Java 11:**
- `var` in lambda parameters
- New String methods: `isBlank()`, `lines()`, `strip()`, `repeat(n)`
- Native HTTP Client (HTTP/2)
- Run source files directly (`java HelloWorld.java`)

**Java 17 (LTS):**
- Sealed Classes (`sealed ... permits`)
- Records (auto-generated constructors/getters/equals/hashCode/toString)
- Pattern Matching for switch
- Text Blocks (`"""` multi-line strings)

### 44. Stream vs. ParallelStream
- **Stream:** Sequential, single thread.
- **ParallelStream:** Uses `ForkJoinPool.commonPool()`, splits work across threads.
- **Real usage:** Good for CPU-bound tasks on isolated datasets (e.g., parsing large read-only data). Avoid for I/O/network/DB calls — can block the shared pool and degrade performance.

---

## Multithreading & Concurrency

### 45. Thread Lifecycle
1. **New** — created, `start()` not called.
2. **Runnable** — `start()` called; executing or waiting for CPU.
3. **Blocked** — waiting for a monitor lock.
4. **Waiting** — waiting indefinitely (`wait()`, `join()`, `LockSupport.park()`).
5. **Timed Waiting** — waiting with timeout (`sleep(ms)`, `wait(ms)`, `join(ms)`).
6. **Terminated** — finished or aborted.

### 46. Runnable vs. Callable
- **Runnable:** `run()` returns void, no checked exceptions.
- **Callable:** `call()` returns a value `V`, can throw checked exceptions; works with Futures/thread pools.

### 47. Thread.start() vs. Thread.run()
- `start()` — spawns a new OS thread, runs `run()` asynchronously.
- `run()` — plain synchronous method call, no new thread.

### 48. Synchronized Block vs. Synchronized Method
- **Method:** Locks entire method — implicit lock on `this` (instance) or Class object (static).
- **Block:** Locks specific code, explicit lock target — finer granularity, better performance.

### 49. Class-Level vs. Object-Level Locking
- **Object-level:** Protects non-static code; each instance has its own monitor — different instances can run concurrently.
- **Class-level:** Protects static code (`synchronized(MyClass.class)`); one lock per class JVM-wide.

### 50. The volatile Keyword
Guarantees reads/writes go directly to main memory (bypassing caches/registers) — ensures **visibility** across threads. Used for flags (`volatile boolean keepRunning`). Does **not** provide atomicity (`count++` is unsafe even with volatile).

### 51. volatile vs. Atomic Variables
- **volatile:** Visibility only — compound operations unsafe.
- **Atomic (e.g., AtomicInteger):** Visibility + atomicity via CAS — lock-free atomic ops like `incrementAndGet()`.

### 52. What is a Deadlock?
Thread 1 holds Lock A, waits for Lock B; Thread 2 holds Lock B, waits for Lock A — both wait forever.
- **Detection:** `jstack`, VisualVM, `ThreadMXBean.findDeadlockedThreads()`.
- **Prevention:** Acquire locks in consistent order; use `tryLock(timeout)`; minimize nested locks.

### 53. sleep() vs. wait() vs. notify()
- `Thread.sleep(ms)` — pauses thread, doesn't release locks.
- `Object.wait()` — releases lock, waits for notification (must own monitor).
- `Object.notify()/notifyAll()` — wakes one/all waiting threads (must be in synchronized context).

### 54. ExecutorService
High-level async execution framework abstracting thread management. Better than manual threads because native thread creation is expensive; ExecutorService reuses threads, manages a task queue, and limits resource usage.

### 55. ForkJoinPool
Specialized ExecutorService for divide-and-conquer algorithms using work-stealing (idle threads steal tasks from busy ones). Powers `parallelStream()` and `CompletableFuture`.

### 56. CompletableFuture vs. Future
- **Future (Java 5):** Blocking `.get()` or polling; no chaining.
- **CompletableFuture (Java 8):** Non-blocking, functional chaining (`.thenApply().thenCompose().exceptionally()`), async callbacks.

### 57. CountDownLatch
Blocks one/more threads until a set of operations by other threads completes. Initialized with a count; workers call `countDown()`; waiters call `await()` and resume when count hits zero.

### 58. Daemon Thread
Low-priority background thread (e.g., GC thread). JVM exits once all user threads finish, abruptly killing daemon threads. Set via `thread.setDaemon(true)` before `start()`.

### 59. Alternatives to join()
- `CountDownLatch` — block until worker count completes.
- `CyclicBarrier` — common barrier point for multiple threads.
- `ExecutorService.invokeAll()` / `awaitTermination()` — wait for pool completion.
- `CompletableFuture.allOf(...).join()` — coordinate multiple futures.

---

## JVM Internals & Memory Management

### 60. JVM Memory Architecture
- **Heap:** Shared area for all objects; primary GC target. Split into Young Gen (Eden, S1, S2) and Old Gen.
- **Stack:** Thread-specific; stores method frames (local vars, primitives, object references). O(1) push/pop.
- **Metaspace:** Replaced PermGen (Java 8+); native off-heap memory for class metadata, method structures, constant pool. Expands dynamically unless bounded.

### 61. Class Loaders in JVM
Hierarchical, follows the **Delegation Principle** (parent-first). Three types:
1. **Bootstrap** — loads core JDK classes (`java.lang.*`), native code.
2. **Platform (Extension)** — loads platform-specific/optional packages.
3. **Application (System)** — loads app classes from classpath.

### 62. JVM Garbage Collection
1. **Allocation:** New objects go into Eden.
2. **Minor GC:** Eden fills → live objects moved to Survivor space; repeated survivors promoted to Old Gen.
3. **Major/Full GC:** Cleans Old Gen when full.
- **Algorithms:** G1 (Garbage-First), ZGC — region-based, concurrent compaction, minimal stop-the-world pauses.

### 63. Common Causes of OutOfMemoryError
- **Heap space:** Held references / memory leaks maxing out heap.
- **Metaspace:** Too many dynamically loaded classes (reflection/CGLIB-heavy frameworks).
- **Unable to create native thread:** OS out of memory/file handles.

**Troubleshooting:**
1. `-XX:+HeapDumpOnOutOfMemoryError` to capture `.hprof` snapshot.
2. Analyze with Eclipse MAT or VisualVM.
3. Check "Leak Suspects", trace references back to roots.

### 64. Detecting & Fixing Memory Leaks
- **Detection:** APM tools (Dynatrace, New Relic) or `jstat`/`JConsole` — watch for continuous upward saw-tooth heap usage that doesn't drop after Full GC.
- **Culprits & Fixes:**
  - Unbounded static Collections → add eviction bounds/clear explicitly.
  - Unclosed resources (DB/file streams) → use try-with-resources.
  - Broken `equals()`/`hashCode()` on keys → implement correctly with immutable keys.

### 65. Java Memory Model (JMM)
Specification defining how JVM interacts with hardware memory (caches, registers, RAM). Guarantees:
- **Visibility:** Updates by one thread seen by others (via `volatile`/synchronization).
- **Ordering:** Prevents reordering from breaking thread logic (Happens-Before relationships).

---

## Coding Round Problems

### 66. Find the missing element in an array
Use sum formula: `Sum = n*(n+1)/2`.
```java
public int findMissingNumber(int[] nums, int n) {
    int expectedSum = n * (n + 1) / 2;
    int actualSum = 0;
    for (int num : nums) actualSum += num;
    return expectedSum - actualSum; // O(n) time, O(1) space
}
```

### 67. Find the first non-repeated character in a string
```java
public char firstNonRepeatedChar(String str) {
    int[] counts = new int[256];
    for (int i = 0; i < str.length(); i++) counts[str.charAt(i)]++;
    for (int i = 0; i < str.length(); i++) {
        if (counts[str.charAt(i)] == 1) return str.charAt(i);
    }
    return '\0';
}
```

### 68. Find the second highest number in an array
```java
public int findSecondHighest(int[] nums) {
    int highest = Integer.MIN_VALUE, secondHighest = Integer.MIN_VALUE;
    for (int num : nums) {
        if (num > highest) {
            secondHighest = highest;
            highest = num;
        } else if (num > secondHighest && num != highest) {
            secondHighest = num;
        }
    }
    return secondHighest;
}
```

### 69. Reverse a sentence word by word
```java
public String reverseSentence(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) return sentence;
    String[] words = sentence.trim().split("\\s+");
    StringBuilder sb = new StringBuilder();
    for (int i = words.length - 1; i >= 0; i--) {
        sb.append(words[i]);
        if (i > 0) sb.append(" ");
    }
    return sb.toString();
}
```

### 70. Find frequency of each character without HashMap
```java
public void printCharFrequency(String str) {
    int[] freq = new int[256];
    for (char c : str.toCharArray()) freq[c]++;
    for (int i = 0; i < freq.length; i++) {
        if (freq[i] > 0) System.out.println((char) i + ": " + freq[i]);
    }
}
```

### 71. Find vowels from a string
```java
public void printVowels(String str) {
    String vowels = "aeiouAEIOU";
    for (char c : str.toCharArray()) {
        if (vowels.indexOf(c) != -1) System.out.print(c + " ");
    }
}
```

### 72. Check if a number is prime
```java
public boolean isPrime(int n) {
    if (n <= 1) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0 || n % 3 == 0) return false;
    for (int i = 5; i * i <= n; i += 6) {
        if (n % i == 0 || n % (i + 2) == 0) return false;
    }
    return true; // O(sqrt(n))
}
```

### 73. Find pair in an array with a target sum
```java
public void findPair(int[] nums, int target) {
    Set<Integer> seen = new HashSet<>();
    for (int num : nums) {
        int complement = target - num;
        if (seen.contains(complement)) {
            System.out.println("Pair found: (" + complement + ", " + num + ")");
            return;
        }
        seen.add(num);
    }
}
```

### 74. Implement an LRU Cache
O(1) `get`/`put` using HashMap + Doubly Linked List.
```java
import java.util.HashMap;

public class LRUCache {
    class Node {
        int key, value;
        Node prev, next;
        Node(int k, int v) { this.key = k; this.value = v; }
    }

    private final int capacity;
    private final HashMap<Integer, Node> map;
    private final Node head, tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        remove(node);
        insertToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) remove(map.get(key));
        if (map.size() == capacity) {
            map.remove(tail.prev.key);
            remove(tail.prev);
        }
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        insertToHead(newNode);
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertToHead(Node node) {
        node.next = head.next;
        node.next.prev = node;
        head.next = node;
        node.prev = head;
    }
}
```

### 75. Producer-Consumer problem using wait()/notify()
```java
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int CAPACITY = 5;

    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (this) {
                while (buffer.size() == CAPACITY) wait();
                System.out.println("Produced: " + value);
                buffer.add(value++);
                notify();
                Thread.sleep(1000);
            }
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (buffer.isEmpty()) wait();
                int val = buffer.poll();
                System.out.println("Consumed: " + val);
                notify();
                Thread.sleep(1000);
            }
        }
    }
}
```

---

## Quick Revision Priorities (Senior/Lead Level)

If short on time before an interview, prioritize:
- **HashMap internals** (#24), **ConcurrentHashMap internals** (#28) — almost always asked
- **JVM memory + GC** (#60–65) — expected at senior level
- **Multithreading fundamentals**: deadlocks (#52), ExecutorService (#54), CompletableFuture (#56)
- **Streams**: map vs flatMap (#39), terminal vs intermediate (#40)
- **LRU Cache** (#74) and **Producer-Consumer** (#75) — classic coding-round staples

## Source
Compiled from Gemini-generated notes (gemini.google.com/glic).
