# Design Patterns for Microservices & Java — Interview Prep Guide

Covers the **Saga**, **Proxy**, **Event Sourcing**, and **Singleton** patterns with real-world examples.

## Table of Contents

1. [Saga Pattern](#1-saga-pattern)
2. [Proxy Pattern (API Gateway)](#2-proxy-pattern-in-microservices)
3. [Event Sourcing Pattern](#3-event-sourcing-pattern)
4. [Singleton Pattern](#4-singleton-pattern)

---

# 1. Saga Pattern

## Real-world scenario: placing an order

A user buys a smartphone. The workflow spans three microservices, each with its own database:

1. **Order Service** — creates the order with `PENDING` status.
2. **Inventory Service** — reserves/deducts stock.
3. **Payment Service** — charges the customer's card.

Because each service owns its data, there's no single ACID transaction. A **Saga** is a sequence of local transactions where each step has a **compensating transaction** to undo it if a later step fails.

## Example 1: Choreography Saga (decentralized)

No central coordinator. Services publish and listen to events on an event bus (e.g., Apache Kafka).

```
[Order Service] ──(Order_Created)──> [Inventory Service] ──(Inventory_Reserved)──> [Payment Service]
```

**Happy path**

1. Order Service saves the order as `PENDING` and emits `Order_Created`.
2. Inventory Service listens to `Order_Created`, deducts 1 smartphone, and emits `Inventory_Reserved`.
3. Payment Service listens to `Inventory_Reserved`, charges the card, and emits `Payment_Successful`.
4. Order Service listens to `Payment_Successful` and sets the order to `CONFIRMED`.

**Failure path & compensating transactions** (e.g., insufficient funds)

1. Payment Service emits `Payment_Failed`.
2. Inventory Service listens to `Payment_Failed` and runs its compensation: **adds 1 smartphone back** to stock.
3. Order Service listens to `Payment_Failed` and runs its compensation: sets the order to `CANCELLED`.

## Example 2: Orchestration Saga (centralized)

A central **Orchestrator** (the conductor) explicitly tells each participant what to do, step by step.

```
               ┌── 1. Create Order ──> [Order Service]
               ├── 2. Reserve Stock ─> [Inventory Service]
[Orchestrator] ┼── 3. Charge Card ───> [Payment Service]
               └── 4. If 3 fails ────> [Compensate: Release Stock & Cancel Order]
```

**Happy path**

1. Orchestrator → Order Service: "Create a pending order." (Success)
2. Orchestrator → Inventory Service: "Deduct 1 smartphone." (Success)
3. Orchestrator → Payment Service: "Process $500 payment." (Success)
4. Orchestrator finalizes the workflow and tells Order Service to mark the order `CONFIRMED`.

**Failure path & compensating transactions** (Payment fails at step 3)

1. Payment Service replies to the Orchestrator with a failure.
2. The Orchestrator halts the forward flow and fires backward logic:
   - Inventory Service: "Run compensation — add 1 smartphone back to stock."
   - Order Service: "Run compensation — set order status to `CANCELLED`."

## Choreography vs Orchestration

| | Choreography | Orchestration |
|---|---|---|
| **Control** | Decentralized — services react to events | Centralized — orchestrator directs every step |
| **Coupling** | Loose, but implicit event dependencies | Services depend on the orchestrator |
| **Visibility** | Flow is spread across services; harder to trace | Whole workflow visible in one place |
| **Best for** | Simple flows with few steps | Complex flows with many steps and branching |

## Key execution mechanics

- **Idempotency:** if the network glitches while the Orchestrator tells Payment Service to charge a card, it may retry. Payment Service must use an **Idempotency-Key** (e.g., `Order_ID`) to detect an already-processed charge and prevent double billing.
- **Retries on transient failures:** for brief network timeouts (not business failures like "out of stock"), retry a few times with **exponential backoff** before giving up and triggering the rollback sequence.

---

# 2. Proxy Pattern in Microservices

## Real-world manifestation: the API Gateway

The ultimate real-world proxy in microservices is an **API Gateway** (Spring Cloud Gateway, Netflix Zuul, Kong, AWS API Gateway).

Imagine a mobile app that needs a user dashboard, with data from the User, Order, and Recommendation services. Instead of calling all three backends directly — exposing internal IPs and requiring complex client-side security — the app talks to a gateway acting as a **reverse proxy**.

```
              ┌─── [ API Gateway Proxy ] ───┐
              │  - Checks JWT token         │
              │  - Enforces rate limiting   │
              └──────────────┬──────────────┘
                             │ (forwards valid traffic)
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
  [User Service]       [Order Service]     [Inventory Service]
```

## How different proxy types solve microservice problems

### 1. Protection Proxy — gateway security & rate limiting

- **Problem:** you don't want attackers bombarding sensitive services (e.g., Payment) with invalid traffic.
- **Solution:** the gateway intercepts first, validates the **JWT**, and applies a **rate limiter** (e.g., a Redis-backed filter). If a user exceeds 10 requests/second, the proxy rejects with **HTTP 429 Too Many Requests**. The payment service never sees the attack.

### 2. Remote Proxy — service-to-service calls (Feign clients)

- **Problem:** hand-writing HTTP connection code every time Service A calls Service B is tedious and error-prone.
- **Solution:** Spring Cloud **OpenFeign** creates a remote proxy from a plain interface:

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/charge")
    void processPayment(PaymentRequest request);
}
```

Calling `paymentClient.processPayment(request)` feels like a local method call. Under the hood the Feign proxy performs **service discovery** (finds the payment service's address), serializes to JSON, makes the HTTP call, and returns the response.

### 3. Cache Proxy — performance offloading

- **Problem:** the Product Catalog Service is hammered by identical "Top Products" requests that hit a slow database each time.
- **Solution:** place a caching proxy (Redis or Nginx) in front. If the product list is cached, it's returned instantly without touching the backend; the request is forwarded only when the cache expires.

## Summary of benefits

- **Separation of concerns:** microservices focus on business logic — no token checks, rate limits, or routing code.
- **Location transparency:** clients know one URL (the gateway's domain), while backends can scale, move containers, or change IPs behind it.

---

# 3. Event Sourcing Pattern

## Real-world example: a bank account

### Traditional approach (state-based storage)

An `Accounts` table stores the current balance:

| Account ID | Customer Name | Balance |
|---|---|---|
| ACC-999 | Alex | $150 |

Withdrawing $50 runs `UPDATE Accounts SET Balance = 100 WHERE Account_ID = 999`.

- **Problem:** the database erases the fact that you ever had $150. History is lost unless you build separate, complicated audit logs.

### Event Sourcing approach

Never `UPDATE` or `DELETE` — only `INSERT` events into an **Event Store**. The database is a ledger, not a snapshot:

| Sequence | Event Type | Data | Timestamp |
|---|---|---|---|
| 1 | `AccountOpened` | `{ initialBalance: $0 }` | 10:00 AM |
| 2 | `MoneyDeposited` | `{ amount: $200 }` | 10:05 AM |
| 3 | `MoneyWithdrawn` | `{ amount: $50 }` | 10:15 AM |

**Current balance:** the system replays events 1 → 3: `$0 + $200 − $50 = $100`.

## Pros and cons

| 👍 Pros | 👎 Cons |
|---|---|
| **Perfect audit log** — accurate, unalterable history (valuable for finance, healthcare, legal) | **Higher complexity** — requires a mindset shift: events, projections, state reconstruction |
| **Time travel** — reconstruct the system state at any past moment by replaying events up to that timestamp | **Schema evolution** — changing an event's structure (e.g., a new field on `MoneyDeposited`) requires careful backward-compatible handling of old events |
| **High-performance writes** — appending to a log is faster than complex updates and relational locks | |

---

# 4. Singleton Pattern

The **Singleton** is a creational design pattern that ensures a class has **only one instance** and provides a **global point of access** to it.

## Purpose & benefits

Provides a single access point to shared resources like database connections, sockets, caches, and configuration.

- **Memory efficient:** created once and reused, reducing memory usage and object-creation overhead.
- **Resource control:** manages shared resources such as caching, logging, thread pools, and database connectivity.
- **Thread safety:** ensures one thread or connection accesses a shared resource at a time in multi-threaded environments.

## Steps to create a Singleton in Java

1. Create a **private constructor** to prevent direct instantiation from outside the class.
2. Declare a **private static variable** to hold the single instance.
3. Provide a **public static method** (commonly `getInstance()`) that returns the same instance every time.

## Part 1: Types of Singleton implementation

### 1. Eager Initialization

The instance is created at **class-loading time**.

```java
public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();

    private EagerSingleton() {} // Private constructor

    public static EagerSingleton getInstance() {
        return instance;
    }
}
```

- **Pros:** very simple, inherently thread-safe.
- **Cons:** the instance is created even if never used, wasting memory.

### 2. Lazy Initialization (not thread-safe)

The instance is created on the first call to `getInstance()`.

```java
public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
```

- **Pros:** saves memory.
- **Cons:** **not thread-safe** — two threads entering the `if (instance == null)` block simultaneously can create two instances.

### 3. Thread-Safe / Double-Checked Locking (recommended for lazy)

Safe lazy loading without major performance bottlenecks, using `volatile`.

```java
public class ThreadSafeSingleton {
    private static volatile ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {}

    public static ThreadSafeSingleton getInstance() {
        if (instance == null) {                          // Check 1
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) {                  // Check 2
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}
```

- **Why it works:** `volatile` makes writes by one thread immediately visible to others, preventing partially-initialized object errors.

### 4. Bill Pugh Singleton (holder class)

Uses a static inner helper class that isn't loaded until `getInstance()` is called.

```java
public class BillPughSingleton {
    private BillPughSingleton() {}

    private static class SingletonHolder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

- **Pros:** highly recommended — thread-safe, lazy-loaded, and fast without `synchronized` blocks.

## Part 2: How to "break" a Singleton (and how to fix it)

Some Java features can bypass the private constructor and create duplicate instances.

### 1. Reflection

Reflection lets code inspect and manipulate private fields/constructors at runtime.

**How it breaks:**

```java
Constructor<ThreadSafeSingleton> constructor =
        ThreadSafeSingleton.class.getDeclaredConstructor();
constructor.setAccessible(true); // Bypasses "private"!
ThreadSafeSingleton instanceTwo = constructor.newInstance();
```

**Fix:** throw an exception in the private constructor if an instance already exists.

```java
private ThreadSafeSingleton() {
    if (instance != null) {
        throw new RuntimeException("Use getInstance() method to get the single instance.");
    }
}
```

### 2. Serialization / Deserialization

If the Singleton implements `Serializable`, writing it to a file and reading it back creates a **new object** — Java builds a fresh instance without calling your constructor.

**Fix:** implement `readResolve()`. Java invokes it automatically and swaps the new object for your true singleton.

```java
protected Object readResolve() {
    return getInstance();
}
```

### 3. Cloning

If the class (or a superclass) implements `Cloneable`, calling `.clone()` bypasses the constructor and duplicates the object.

**Fix:** override `clone()` to reject the operation.

```java
@Override
protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Cloning a singleton is not allowed.");
}
```

## The ultimate fix: Enum Singleton

An **enum** is immune to Reflection, Serialization, and Cloning attacks out of the box — Java guarantees enum values are instantiated exactly once.

```java
public enum UltimateSingleton {
    INSTANCE;

    // Add any database connection or config logic here
    public void executeTask() {
        System.out.println("Executing safely!");
    }
}
```

## Comparison of implementations

| Implementation | Lazy? | Thread-safe? | Breakable by reflection/serialization/cloning? |
|---|---|---|---|
| Eager | No | Yes | Yes |
| Lazy (basic) | Yes | **No** | Yes |
| Double-checked locking | Yes | Yes | Yes (needs the fixes above) |
| Bill Pugh | Yes | Yes | Yes (needs the fixes above) |
| **Enum** | Effectively yes (on first use) | Yes | **No** |

## Real-time example: database connection pool

Establishing a network connection to a database is a heavy, slow operation. If every request created a new connection, the database would quickly run out of memory and crash.

Instead, use a **Singleton connection pool** (like **HikariCP**):

- **Singleton role:** the whole application shares a single pool manager.
- **Workflow:** when a service needs to run a query, it asks the pool for an already-open idle connection and returns it when done. Because the manager is a strict Singleton, connection limits are enforced globally, memory stays stable, and thread safety is tightly maintained.
