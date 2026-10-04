# Part 3: Microservices Architecture — Interview Prep Guide

A reference of 16 microservices interview questions, plus a bonus section on CORS in a microservices setup.

## Table of Contents

- [1. Architecture Core & Infrastructure (Q1–Q3)](#1-architecture-core--infrastructure)
- [2. Inter-Service Communication (Q4–Q7)](#2-inter-service-communication)
- [3. Configuration & Scaling Management (Q8–Q9)](#3-configuration--scaling-management)
- [4. Distributed Observability & Monitoring (Q10–Q15)](#4-distributed-observability--monitoring)
- [5. Deployment Strategies (Q16)](#5-deployment-strategies)
- [Bonus: CORS in Microservices](#bonus-cors-in-microservices)

---

# 1. Architecture Core & Infrastructure

## 1. Monolithic vs Microservices architecture?

**Monolithic**

```
┌──────────────────────────────────────────────────┐
│  UI ───> [ Monolith: Auth + Orders + Inventory ] │ ───> [ Single DB ]
└──────────────────────────────────────────────────┘
```

**Microservices**

```
          ┌───> [ Auth Service ]      ───> [ Auth DB ]
UI ───> [GW] ├──> [ Orders Service ]    ───> [ Orders DB ]
          └───> [ Inventory Service ] ───> [ Inventory DB ]
```

| | Monolithic | Microservices |
|---|---|---|
| **Definition** | One unified unit: all business modules (auth, orders, payments) bundled, compiled, and deployed together; shared database and runtime | Collection of small, loosely coupled, independently deployable services organized around business domains; **Database-per-Service** |
| **Advantages** | Simpler to build, test, log, profile, and deploy initially; zero network latency between components | High fault isolation (payment failure doesn't block catalog browsing); independent scaling of bottleneck services; tech-stack flexibility (Java for finance, Python for ML); faster release cycles per team |
| **Challenges** | Single point of failure (a memory leak in one module crashes everything); codebase becomes a "spaghetti" tangle; locked to one tech stack; must scale the *whole* app | Heavy operational complexity; network latency; hard distributed data integrity (no simple ACID joins); harder cross-service debugging |

## 2. What is Service Discovery? How does Eureka/Consul work?

In cloud/container environments, instances scale up and down dynamically, so IPs and ports change constantly — hardcoding URLs is impossible. **Service Discovery** provides a dynamic routing registry.

**How Netflix Eureka works**

1. **Service registration:** on boot, a microservice sends its network location (IP, port, service ID) to the Eureka Server.
2. **Heartbeats:** it sends periodic pings (typically every 30 seconds). If Eureka doesn't hear from an instance for a set duration, it removes it from the active pool.
3. **Discovery (fetch registry):** when Service A wants to call Service B, it asks Eureka for the healthy instances of "Service-B" and **caches the registry locally** for fast subsequent calls.

## 3. What is an API Gateway? Why is it required?

An API Gateway is a **single entry point (reverse proxy)** that routes all external client traffic to internal downstream microservices.

**Why it's required**

- **Decoupling / abstraction:** clients talk only to the gateway (e.g., `api.company.com`) instead of tracking dozens of service URLs.
- **Cross-cutting concerns:** centralizes authentication/authorization (validate JWTs once at the edge), rate limiting (DDoS protection), CORS, and SSL termination.
- **Protocol translation:** maps client-friendly HTTP/REST to faster internal protocols such as gRPC.

**Core responsibilities**

| Responsibility | Description |
|---|---|
| Routing | Directs requests to the right service based on URL, method, or headers |
| Protocol translation | Converts between protocols (HTTP ↔ gRPC/WebSocket) |
| Request aggregation | Combines multiple backend calls into one response, reducing client requests (but can increase latency since the gateway waits on several services) |
| Authentication & authorization | Validates identity and permissions |
| Rate limiting & throttling | Prevents abuse; keeps the system stable |
| Load balancing | Distributes requests across service instances |
| Caching | Stores frequent responses to improve performance |
| Monitoring & logging | Captures metrics and logs for observability |

**Best practices**

- **Security:** SSL/TLS, strong authn/authz, IP whitelisting, rate limiting.
- **Performance:** caching, compression, efficient routing.
- **Scalability:** horizontal scaling, load balancing, metric-driven scaling.
- **Monitoring & logging:** track performance metrics; integrate centralized logging.
- **Error handling:** robust handling with standardized error codes/messages.
- **Versioning & documentation:** maintain backward compatibility via versioning; keep API docs current.

---

# 2. Inter-Service Communication

## 4. How do microservices communicate with each other?

| Style | Model | Characteristics |
|---|---|---|
| **REST (HTTP/JSON)** | Synchronous | Readable and standard, but header-parsing overhead and blocks threads while waiting |
| **Kafka (event-driven)** | Asynchronous | Services emit events to topics; consumers process independently. Highly decoupled, resilient to downstream outages, massive throughput |
| **gRPC** | Synchronous / streaming | Built on HTTP/2 with compressed binary payloads (Protocol Buffers). Much faster and lighter than REST; contract enforced natively |

## 5. RestTemplate vs Feign Client vs WebClient?

| Client | Style | Model | Pros | Cons |
|---|---|---|---|---|
| **RestTemplate** | Imperative | Synchronous (blocking) | Simple, battle-tested | Legacy (maintained but no new features); blocks one thread per request, scales poorly under high concurrency |
| **OpenFeign** | Declarative | Synchronous (blocking) | Cleanest code — write an interface with Spring MVC annotations (`@GetMapping`) and Feign generates the implementation | Blocking by default under the hood |
| **WebClient** | Reactive | Asynchronous (non-blocking) | Part of Spring WebFlux; high performance, no thread blocking, very flexible | Steep learning curve (Project Reactor) |

## 6. Feign Client vs Kafka for inter-service communication?

- **Feign (synchronous request-response):** Service A calls Service B and **blocks** until B responds. Use when you need the data immediately to proceed (e.g., checkout verifying a card has funds).
  - **Risk:** if Service B is slow or down, A's thread pool exhausts quickly → **cascading outage**.
- **Kafka (asynchronous, event-driven):** Service A publishes an `OrderCreated` event and immediately returns success. Service B consumes it when it has capacity. A doesn't wait for a response. Use to maximize throughput and decouple uptime (e.g., inventory deduction, email notifications).

## 7. You have 5 services running and deploy a new instance. How does the system detect it and route traffic?

Service Discovery + load balancing:

1. The 6th instance finishes booting.
2. It registers with the Service Registry (Eureka/Consul), announcing its metadata.
3. The API Gateway or consumer services (using client-side load balancers like **Spring Cloud LoadBalancer**) receive the update or periodically refresh their cached registry.
4. The load balancer adds the new IP to its round-robin list and gradually shifts traffic to it — with no code changes or restarts anywhere in the cluster.

---

# 3. Configuration & Scaling Management

## 8. How do you handle configuration management in microservices?

Instead of packaging properties in every JAR, use a centralized setup such as **Spring Cloud Config Server**.

```
[ Git Repo ] ──(push changes)──> [ Config Server ] <──(fetch runtime properties)── [ Microservices ]
```

1. **Central storage:** all configuration (`application.yml` for dev/QA/prod profiles) lives in a version-controlled private Git repo or HashiCorp Vault.
2. **Config Server:** a dedicated Spring Boot app annotated with `@EnableConfigServer` that maps to that storage.
3. **Bootstrapping clients:** on startup, services call the Config Server first to load environment-specific settings.
4. **Dynamic refresh:** when a property changes in Git, send an HTTP `POST` to `/actuator/refresh`, or broadcast an event with **Spring Cloud Bus** (RabbitMQ/Kafka). Targeted services reload changes into memory without a restart.

## 9. What is load balancing? Client-side vs server-side?

Load balancing distributes incoming traffic across a group of backend servers so no single instance becomes a bottleneck.

| | Server-side | Client-side |
|---|---|---|
| **How** | Clients send requests to a single hardware/software load balancer (AWS ALB, F5, Nginx), which forwards to an instance | The consumer itself (e.g., Spring Cloud LoadBalancer) downloads the healthy instance registry from Service Discovery and picks an instance (Round-Robin, Weighted Random) |
| **Pros** | Traditional, well understood | No extra proxy hop; highly resilient |
| **Cons** | Extra network hop; central point of failure if misconfigured | Logic lives in every client |

---

# 4. Distributed Observability & Monitoring

## 10. What is distributed tracing? Which tools have you used?

In a monolith a request lives on one thread and one log. In microservices a single click may cross 10 servers; pinpointing a failure or slowdown is hard. **Distributed tracing** injects tracking tokens into request headers.

- **Trace ID:** a unique global token assigned when a request enters the system (API Gateway); stays constant across every downstream hop.
- **Span ID:** identifies a unit of work inside one service (e.g., a SQL query or method call).
- **Tooling:** traditionally **Spring Cloud Sleuth + Zipkin** (timeline UI for bottlenecks). In Spring Boot 3.x, Sleuth is replaced by **Micrometer Tracing**, typically exporting to Zipkin or an OpenTelemetry backend.

## 11. How do you monitor and alert on microservices health?

A multi-tier stack:

1. **Collection endpoints:** every service exposes Spring Boot Actuator `/actuator/health` and `/actuator/prometheus`.
2. **Metric scraping/storage:** **Prometheus** polls these endpoints on a schedule (e.g., every 5s) capturing CPU, memory, exception counts, and HTTP latency.
3. **Visualization:** **Grafana** uses Prometheus as a data source for real-time dashboards.
4. **Alerting:** **Alertmanager** (or Grafana Alerts) with explicit thresholds — e.g., *"if HTTP 5xx exceeds 2% over 5 minutes on any service, notify Slack/PagerDuty."*

## 12. What is Observability? Logs vs metrics vs traces?

Monitoring tells you *whether* a system is broken; **observability** gives enough internal context to understand *why* — without deploying new code. Three pillars:

| Pillar | Role | Details |
|---|---|---|
| **Logs** (the narrative) | Structured text records (JSON, timestamped) of actions/exceptions, e.g., *"User 456 failed password check"* | Hard to parse in bulk but critical for forensic detail |
| **Metrics** (the numbers) | Aggregatable numeric data over time: JVM GC pauses, memory %, requests/sec | Low storage footprint; ideal for dashboards and alerts |
| **Traces** (the map) | End-to-end timeline of the exact multi-service path of one transaction | Shows where time is spent across services |

## 13. How do you debug latency issues in production?

1. **Isolate with distributed tracing:** open the slow request's trace and find which service span consumes (say) 80% of total duration.
2. **Analyze metrics:** check that service's Grafana dashboards for resource contention — JVM GC pause spikes, CPU throttling, saturated DB connection pool.
3. **Inspect database logs:** if infrastructure looks fine, look for unindexed full-table scans or Hibernate **N+1** bugs in that call path.

## 14. What is OpenTelemetry?

**OpenTelemetry (OTel)** is an open-source, vendor-neutral CNCF framework providing standard APIs, SDKs, and instrumentation agents to generate, collect, and export **logs, metrics, and traces**.

- **Why it matters:** historically, using Zipkin meant coding against Zipkin client libraries; switching to Datadog or New Relic meant rewriting instrumentation. With OTel you instrument once (e.g., the standard OTel Java agent) and send telemetry to any backend through **configuration changes alone**.

## 15. What is a Service Mesh? Why is it useful?

A service mesh (**Istio**, **Linkerd**) is a dedicated infrastructure layer on containerized platforms (Kubernetes) that handles secure service-to-service communication.

- **How it works (sidecar architecture):** each pod gets a lightweight proxy container (e.g., **Envoy**) alongside the Java app. All inbound/outbound traffic flows through these proxies (**data plane**), governed by a central **control plane**.
- **Why it's useful:** takes network governance out of application code. No need to write retry logic, circuit breakers, mutual TLS (mTLS), or tracing instrumentation in Spring — the infrastructure enforces them.

---

# 5. Deployment Strategies

## 16. Canary vs Blue-Green deployments?

### Blue-Green (all-or-nothing switch)

Two identical production environments: **Blue** (live traffic) and **Green** (the new version).

```
             ┌───> [ Blue Environment  (v1.0) ]  (Live traffic)
[ Router ] ──┤
             └───> [ Green Environment (v2.1) ]  (Staging / warm-up)
```

- **The switch:** once Green passes post-deployment smoke tests, the router sends **100%** of traffic from Blue to Green instantly.
- **Rollback:** if a critical error appears, switch the router back to Blue — near-zero downtime.

### Canary (incremental release)

The new version runs beside the primary fleet and receives a small share of real traffic.

```
             ┌───> [ Primary Fleet (v1.0) ] ───> 95% of users
[ Router ] ──┤
             └───> [ Canary Node   (v2.1) ] ───> 5% of users (test group)
```

- **The switch:** route a small percentage (e.g., 5%) to the canary; the rest stays on the old version.
- **Progression:** monitor error rates and logs; if stable for several hours, scale up (10% → 25% → 50% → 100%). This limits the **blast radius** of a bug.

| | Blue-Green | Canary |
|---|---|---|
| Traffic shift | Instant, 100% | Gradual |
| Rollback | Switch router back | Route canary traffic back to 0% |
| Risk exposure | All users at cutover | Small user subset first |
| Infrastructure cost | Two full environments | Small extra capacity |

---

# Bonus: CORS in Microservices

## What is CORS?

**CORS (Cross-Origin Resource Sharing)** is a browser-enforced security protocol that safely loosens the browser's strict **Same-Origin Policy (SOP)**. By default SOP stops a frontend on `https://myfrontend.com` from fetching data from an API on `https://myapi.com`. CORS uses HTTP headers to tell the browser it's safe for a specific site to access the resource.

## How it works

**1. Simple requests** (basic `GET`/standard `POST`)

- The browser sends the call directly, attaching `Origin: https://myfrontend.com`.
- The API checks the origin against a whitelist and replies with `Access-Control-Allow-Origin: https://myfrontend.com`.
- If it matches, JavaScript can read the response; otherwise the browser blocks it.

**2. Preflight requests** (`PUT`, `DELETE`, custom JSON/Auth headers)

- The browser first sends an automatic `OPTIONS` request: *"Are you okay with an incoming DELETE from `https://myfrontend.com`?"*
- The server approves with headers such as `Access-Control-Allow-Origin` and `Access-Control-Allow-Methods: GET, POST, PUT, DELETE`.
- If preflight succeeds, the browser fires the real request.

## The API Gateway CORS pattern

Configuring CORS in every microservice is a maintenance burden. Instead, enforce it once at the gateway:

```
                ┌─── [ API Gateway ] ───┐
                │ (enforces CORS policy)│
                └───────────┬───────────┘
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
 [Order Service]    [Payment Service]   [Inventory Service]
```

1. **Centralization:** allowed domains, headers, and methods are configured at the gateway.
2. **Cleaner microservices:** internal services stay free of CORS logic.
3. **Internal freedom:** service-to-service calls (e.g., in a Saga) are server-to-server and never subject to browser CORS restrictions.
