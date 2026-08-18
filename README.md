# Interview_Preparation

* https://leetcode.com/studyplan/top-interview-150/
* https://javaconceptoftheday.com/duplicate-characters-in-a-string-in-java/
* https://leetcode.com/studyplan/leetcode-75/


# Java Backend Interview Notes

A structured, quick-reference set of notes covering Core Java, Collections, Java 8+ features, Multithreading & Concurrency, JVM Internals, and common Coding Round problems — compiled for senior/lead Java backend interview prep.

## How to Use This

- Use the **Table of Contents** below to jump to a topic.
- Each section is self-contained — good for last-minute revision before a round.
- Sections 1–15: **Core Java & OOP**
- Sections 16–34: **Collections Framework**
- Sections 35–44: **Java 8+ Features (Streams, Lambdas, Optional)**
- Sections 45–59: **Multithreading & Concurrency**
- Sections 60–65: **JVM Internals & Memory Management**
- Sections 66–75: **Coding Round Problems (with Java solutions)**

---

## Table of Contents

### Core Java & OOP
1. [The 4 Pillars of OOP](#1-the-4-pillars-of-oop)
2. [Abstract Class vs. Interface](#2-abstract-class-vs-interface)
3. [Why Java Doesn't Support Multiple Inheritance with Classes](#3-why-java-doesnt-support-multiple-inheritance-with-classes)
4. [Polymorphism: Static vs. Dynamic](#4-polymorphism-static-vs-dynamic)
5. [Abstraction vs. Encapsulation](#5-abstraction-vs-encapsulation)
6. [Aggregation vs. Composition](#6-aggregation-vs-composition)
7. [Why is String Immutable in Java?](#7-why-is-string-immutable-in-java)
8. [final vs. finally vs. finalize](#8-final-vs-finally-vs-finalize)
9. [Marker Interface & Real-time Use Cases](#9-marker-interface--real-time-use-cases)
10. [Can We Override a Static Method?](#10-can-we-override-a-static-method)
11. [Effective Final Concept (Java 8+)](#11-effective-final-concept-java-8)
12. [Checked Exceptions: Pros & Cons](#12-checked-exceptions-pros--cons)
13. [String s = new String("hello") — Objects Created](#13-string-s--new-stringhello--objects-created)
14. [Dynamic Class Loading](#14-dynamic-class-loading)
15. [Methods Available in the Object Class](#15-methods-available-in-the-object-class)

### Collections Framework
16. [Collection vs. Collections](#16-collection-vs-collections)
17. [Why Map is not part of the Collection interface](#17-why-map-is-not-part-of-the-collection-interface)
18. [List vs. Set vs. Map](#18-list-vs-set-vs-map)
19. [ArrayList vs. LinkedList](#19-arraylist-vs-linkedlist)
20. [ArrayList vs. Vector, CopyOnWriteArrayList](#20-arraylist-vs-vector-copyonwritearraylist)
21. [HashSet vs. LinkedHashSet vs. TreeSet](#21-hashset-vs-linkedhashset-vs-treeset)
22. [How HashSet Prevents Duplicates Internally](#22-how-hashset-prevents-duplicates-internally)
23. [HashMap vs. LinkedHashMap vs. TreeMap](#23-hashmap-vs-linkedhashmap-vs-treemap)
24. [How HashMap Works Internally](#24-how-hashmap-works-internally)
25. [Why HashMap is Not Thread-Safe](#25-why-hashmap-is-not-thread-safe)
26. [Load Factor in HashMap](#26-load-factor-in-hashmap)
27. [HashMap vs. ConcurrentHashMap](#27-hashmap-vs-concurrenthashmap)
28. [How ConcurrentHashMap Works Internally](#28-how-concurrenthashmap-works-internally)
29. [Mutable Objects as HashMap Keys](#29-mutable-objects-as-hashmap-keys)
30. [How TreeMap Maintains Sorting](#30-how-treemap-maintains-sorting)
31. [Fail-Fast vs. Fail-Safe Iterators](#31-fail-fast-vs-fail-safe-iterators)
32. [Why equals() and hashCode() Matter in Collections](#32-why-equals-and-hashcode-matter-in-collections)
33. [Same hashCode() — Does equals() Return True?](#33-same-hashcode--does-equals-return-true)
34. [equals() True — Is hashCode() Always Same?](#34-equals-true--is-hashcode-always-same)

### Java 8+ Features
35. [Major Features Introduced in Java 8](#35-major-features-introduced-in-java-8)
36. [Functional Interfaces](#36-functional-interfaces)
37. [Default Methods in Interfaces](#37-default-methods-in-interfaces)
38. [Diamond Problem with Default Methods](#38-diamond-problem-with-default-methods)
39. [map() vs. flatMap() in Streams](#39-map-vs-flatmap-in-streams)
40. [Terminal vs. Intermediate Stream Operations](#40-terminal-vs-intermediate-stream-operations)
41. [What is Optional?](#41-what-is-optional)
42. [isPresent() vs. ifPresent()](#42-ispresent-vs-ifpresent)
43. [New Features in Java 11 and Java 17](#43-new-features-in-java-11-and-java-17)
44. [Stream vs. ParallelStream](#44-stream-vs-parallelstream)

### Multithreading & Concurrency
45. [Thread Lifecycle](#45-thread-lifecycle)
46. [Runnable vs. Callable](#46-runnable-vs-callable)
47. [Thread.start() vs. Thread.run()](#47-threadstart-vs-threadrun)
48. [Synchronized Block vs. Synchronized Method](#48-synchronized-block-vs-synchronized-method)
49. [Class-Level vs. Object-Level Locking](#49-class-level-vs-object-level-locking)
50. [The volatile Keyword](#50-the-volatile-keyword)
51. [volatile vs. Atomic Variables](#51-volatile-vs-atomic-variables)
52. [What is a Deadlock?](#52-what-is-a-deadlock)
53. [sleep() vs. wait() vs. notify()](#53-sleep-vs-wait-vs-notify)
54. [ExecutorService](#54-executorservice)
55. [ForkJoinPool](#55-forkjoinpool)
56. [CompletableFuture vs. Future](#56-completablefuture-vs-future)
57. [CountDownLatch](#57-countdownlatch)
58. [Daemon Thread](#58-daemon-thread)
59. [Alternatives to join()](#59-alternatives-to-join)

### JVM Internals & Memory Management
60. [JVM Memory Architecture](#60-jvm-memory-architecture)
61. [Class Loaders in JVM](#61-class-loaders-in-jvm)
62. [JVM Garbage Collection](#62-jvm-garbage-collection)
63. [Common Causes of OutOfMemoryError](#63-common-causes-of-outofmemoryerror)
64. [Detecting & Fixing Memory Leaks](#64-detecting--fixing-memory-leaks)
65. [Java Memory Model (JMM)](#65-java-memory-model-jmm)

### Coding Round Problems
66. Find the missing element in an array
67. Find the first non-repeated character in a string
68. Find the second highest number in an array
69. Reverse a sentence word by word
70. Find frequency of each character without HashMap
71. Find vowels from a string
72. Check if a number is prime
73. Find pair in an array with a target sum
74. Implement an LRU Cache
75. Producer-Consumer problem using wait()/notify()

---

## Quick Revision Priorities (Senior/Lead Level)

If short on time before an interview, prioritize:
- **HashMap internals** (#24), **ConcurrentHashMap internals** (#28) — almost always asked
- **JVM memory + GC** (#60–65) — expected at senior level
- **Multithreading fundamentals**: deadlocks (#52), ExecutorService (#54), CompletableFuture (#56)
- **Streams**: map vs flatMap (#39), terminal vs intermediate (#40)
- **LRU Cache** (#74) and **Producer-Consumer** (#75) — classic coding-round staples

## Source

Compiled from Gemini-generated notes (gemini.google.com/glic). Organized into this README for structured revision.
