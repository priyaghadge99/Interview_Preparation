# Complete Java Backend Interview Guide

1. [Spring Boot, Security, JPA & SQL](#spring-boot-security-jpa--sql--interview-prep-guide)
2. [Part 3: Microservices](#part-3-microservices-architecture--interview-prep-guide)
3. [Apache Kafka](#apache-kafka--interview-prep-guide)
4. [Design Patterns](#design-patterns-for-microservices--java--interview-prep-guide)


---

# Spring Boot, Security, JPA & SQL — Interview Prep Guide

A consolidated reference of 66 interview questions with concise answers and code samples.

## Table of Contents

- [Section A: Spring Core & Spring Boot (Q1–Q25)](#section-a-spring-core--spring-boot)
  - [A1. Core Framework vs. Spring Boot](#a1-core-framework-vs-spring-boot-q1q3)
  - [A2. Component Scanning & Bean Creation](#a2-component-scanning--bean-creation-q4q7)
  - [A3. Dependency Injection Internals](#a3-dependency-injection-internals-q8q10)
  - [A4. Bean Lifecycle & Scopes](#a4-bean-lifecycle--scopes-q11q13)
  - [A5. Configuration & Environments](#a5-configuration--environments-q14q18)
  - [A6. Production Features & Server Customization](#a6-production-features--server-customization-q19q22)
  - [A7. Advanced Customization & Logging](#a7-advanced-customization--logging-q23q25)
- [Section B: Spring Security (Q26–Q32)](#section-b-spring-security)
- [Section C: Transactions, Exceptions & REST Design (Q33–Q39)](#section-c-transactions-exceptions--rest-design)
- [Section D: JPA & Hibernate (Q40–Q53)](#section-d-jpa--hibernate)
- [Section E: Database Theory & SQL (Q54–Q66)](#section-e-database-theory--sql)

---

# Section A: Spring Core & Spring Boot

## A1. Core Framework vs. Spring Boot (Q1–Q3)

### 1. What is the difference between Spring Framework and Spring Boot?

| | Spring Framework | Spring Boot |
|---|---|---|
| **What it is** | Comprehensive Java framework providing core infrastructure: dependency injection, transaction management, MVC | Extension built on top of Spring Framework |
| **Configuration** | Heavy manual configuration (XML or Java Config) | Opinionated defaults and **Auto-Configuration** |
| **Dependencies** | Managed manually | **Starter** dependencies |
| **Server** | Needs an external servlet container (e.g., Tomcat) | **Embedded** server (Tomcat, Jetty) |
| **Goal** | Flexibility and power | Production-ready app running instantly |

### 2. What does `@SpringBootApplication` do internally?

It is a convenience annotation combining three annotations:

1. **`@SpringBootConfiguration`** — a specialized `@Configuration`; marks the class as a source of bean definitions.
2. **`@EnableAutoConfiguration`** — auto-configures beans based on the dependencies on the classpath.
3. **`@ComponentScan`** — scans the current package and sub-packages for stereotype components (`@Component`, `@Service`, etc.).

### 3. How does Spring Boot Auto-Configuration work?

At startup, Spring Boot inspects the classpath. `@EnableAutoConfiguration` leverages the `SpringFactoriesLoader` mechanism:

- It reads configuration classes listed in meta-files (`META-INF/spring.factories`, or the newer `AutoConfiguration.imports`).
- It evaluates conditional annotations such as `@ConditionalOnClass`, `@ConditionalOnMissingBean`, and `@ConditionalOnProperty`.
- If the conditions match (e.g., `DataSource.class` is on the classpath and no custom `DataSource` bean is defined), Spring Boot creates and registers the bean automatically.

---

## A2. Component Scanning & Bean Creation (Q4–Q7)

### 4. Difference between `@Component`, `@Service`, and `@Repository`?

All three are stereotype annotations; `@Service` and `@Repository` are specializations of `@Component`.

- **`@Component`** — generic stereotype for any Spring-managed bean.
- **`@Service`** — service layer / business logic. Currently a semantic marker only (no extra behavior).
- **`@Repository`** — data access layer. Also **translates database-specific exceptions** into Spring's uniform `DataAccessException` hierarchy.

### 5. `@Bean` vs `@Component`?

- **`@Component`** — class-level; discovered via classpath scanning. Use on your own classes where you control the source.
- **`@Bean`** — method-level, used inside a `@Configuration` class. Use it to manually instantiate/configure a bean, or for **third-party classes** you cannot annotate (e.g., `ObjectMapper`).

```java
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

### 6. Difference between `@Controller` and `@RestController`?

- **`@Controller`** — traditional Spring MVC. Returns a `String` that maps to a view (HTML/JSP) resolved by a view resolver. To return data directly, annotate methods with `@ResponseBody`.
- **`@RestController`** — `@Controller` + `@ResponseBody`. Every handler method serializes its return object directly into the HTTP response body (JSON/XML).

### 7. `@RestControllerAdvice` vs `@ControllerAdvice`?

- **`@ControllerAdvice`** — global exception handling, model attributes, and data binding across controllers. Methods typically return a view name.
- **`@RestControllerAdvice`** — `@ControllerAdvice` + `@ResponseBody`. Handler methods write their return object (e.g., an error payload) directly as JSON.

---

## A3. Dependency Injection Internals (Q8–Q10)

### 8. How does `@Autowired` work internally?

When the `ApplicationContext` loads, it registers the `AutowiredAnnotationBeanPostProcessor`.

1. During bean initialization, it uses **reflection** to find fields, constructors, or setters marked `@Autowired`.
2. It resolves the dependency by looking up a matching bean in the IoC container — first by **type**.
3. If multiple beans of the same type exist, it narrows by **name** (field/parameter name vs bean ID). If still ambiguous, it throws `NoUniqueBeanDefinitionException`.

### 9. `@Autowired` vs `@Inject` vs `@Qualifier`?

| Annotation | Origin | Notes |
|---|---|---|
| `@Autowired` | Spring-native | Has a `required` attribute (default `true`) |
| `@Inject` | Java CDI (JSR-330) | Almost identical but no `required` attribute; useful for portability to other Jakarta frameworks |
| `@Qualifier` | Spring | Used with `@Autowired`/`@Inject` to name the exact bean when several of the same type exist |

### 10. What is Dependency Injection (IoC)? How does Spring perform DI using reflection?

- **Inversion of Control (IoC):** control of creating and managing objects is handed to an external container (the Spring IoC container) instead of objects instantiating their own dependencies.
- **Dependency Injection (DI):** the specific pattern that implements IoC.
- **Reflection mechanism:** Spring reads configuration/annotations, discovers class blueprints, and uses the Reflection API (`Class.forName()`, `Constructor.newInstance()`) to create objects. For field/setter injection it bypasses `private` access (`field.setAccessible(true)`) to inject references directly.

---

## A4. Bean Lifecycle & Scopes (Q11–Q13)

### 11. `ApplicationContext` vs `BeanFactory`?

- **`BeanFactory`** — root IoC container interface. Basic features, **lazy loading** (beans created on `getBean()`). Suited to lightweight, resource-constrained environments.
- **`ApplicationContext`** — sub-interface of `BeanFactory`. Adds **eager loading** of singletons at startup, AOP integration, internationalization (`MessageSource`), and application event publication.

### 12. Explain the complete lifecycle of a Spring Bean.

1. **Instantiation** — Spring creates the bean via reflection.
2. **Populate properties** — dependencies injected via setters/fields.
3. **Aware interfaces** — `BeanNameAware`, `ApplicationContextAware`, etc. receive framework objects.
4. **`BeanPostProcessor` (before init)** — `postProcessBeforeInitialization()` runs (handles `@PostConstruct`).
5. **Initialization**
   - `InitializingBean.afterPropertiesSet()`
   - Custom `init-method`
6. **`BeanPostProcessor` (after init)** — `postProcessAfterInitialization()` runs (**AOP proxies are created here**).
7. **Ready for use.**
8. **Destruction** (on container shutdown)
   - `@PreDestroy` methods
   - `DisposableBean.destroy()`
   - Custom `destroy-method`

### 13. What are Bean scopes?

| Scope | Description |
|---|---|
| `singleton` (default) | One instance per Spring IoC container |
| `prototype` | New instance every time it is requested |
| `request` | One instance per HTTP request (web-aware context only) |
| `session` | One instance per HTTP session (web-aware context only) |

---

## A5. Configuration & Environments (Q14–Q18)

### 14. What are Spring Boot Starters?

Dependency descriptors that bundle all transitively required dependencies, versions, and build setup for a feature under one name. For example, `spring-boot-starter-web` pulls in Tomcat, Spring MVC, Jackson, and validation libraries — avoiding version-mismatch problems.

### 15. How do you externalize configuration in Spring Boot?

Properties are resolved in a priority hierarchy (highest → lowest, common sources):

1. Command-line arguments (`--server.port=8081`)
2. Java system properties (`-Dserver.port=8081`)
3. OS environment variables (`SERVER_PORT=8081`)
4. `application.properties`/`.yml` **outside** the packaged jar
5. `application.properties`/`.yml` **inside** the jar (`src/main/resources`)

### 16. Different ways to read values from `application.properties`?

1. **`@Value`** — single properties:
   ```java
   @Value("${app.timeout}")
   private int timeout;
   ```
2. **`@ConfigurationProperties`** — binds a group of hierarchical properties to a POJO (type-safe).
3. **`Environment`** — programmatic access:
   ```java
   @Autowired
   private Environment env;
   // env.getProperty("app.timeout");
   ```

### 17. How to bind configuration properties to a POJO?

```java
@Component
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String host;
    private int port;
    // Getters and setters are required
}
```

> If you don't use `@Component` on the POJO, enable it with `@EnableConfigurationProperties(MailProperties.class)` or `@ConfigurationPropertiesScan`.

### 18. How to load values from a custom properties file?

```java
@Configuration
@PropertySource("classpath:custom.properties")
public class CustomConfig {
    @Value("${custom.feature.enabled}")
    private boolean enabled;
}
```

> **Note:** `@PropertySource` does not load YAML by default — it expects `.properties` format.

---

## A6. Production Features & Server Customization (Q19–Q22)

### 19. What is Spring Boot Actuator? Most-used endpoints?

Actuator adds production-ready features: monitoring, metrics, and runtime interaction.

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Health status (UP/DOWN, disk space, DB connections) |
| `/actuator/info` | Arbitrary application info |
| `/actuator/metrics` | JVM memory, CPU, HTTP request counts, etc. |
| `/actuator/env` | Current properties from Spring's `Environment` |
| `/actuator/loggers` | View and change logging levels at runtime |

### 20. `CommandLineRunner` vs `ApplicationRunner`?

Both run code right after the `ApplicationContext` is initialized, before startup completes. The only difference is argument handling:

- **`CommandLineRunner`** — raw `String... args`.
- **`ApplicationRunner`** — `ApplicationArguments` object, making option parsing (`--foo=bar`) cleaner.

### 21. How to apply code changes without restarting the server?

1. **Spring Boot DevTools** — add `spring-boot-devtools`; monitors the classpath and triggers a fast restart using an isolated classloader.
2. **JRebel** — commercial tool that reloads classes on the fly without restarting the context.

### 22. How to switch the embedded server from Tomcat to Jetty?

Exclude Tomcat from `spring-boot-starter-web` and add the Jetty starter:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

---

## A7. Advanced Customization & Logging (Q23–Q25)

### 23. How to create custom annotations in Spring?

Combine Java meta-annotations (`@Target`, `@Retention`) with Spring annotations to build a composite annotation:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service          // Combines behavior
@Transactional    // Adds transactional behavior automatically
public @interface CustomTransactionalService {}
```

### 24. What are Profiles in Spring Boot?

Profiles segregate configuration so it applies only in specific environments (`dev`, `test`, `prod`).

- **Naming:** `application-{profile}.properties` (e.g., `application-dev.properties`).
- **Activation:** `-Dspring.profiles.active=prod`, or in the main properties file.
- **Bean filtering:** `@Profile("prod")` on classes/methods registers beans only when that profile is active.

### 25. How does Spring Boot handle logging? What are SLF4J and Log4j?

- **Spring Boot default:** **Logback**, accessed through the **SLF4J** facade; formatted console logging works out of the box.
- **SLF4J (Simple Logging Facade for Java):** *not* an implementation — an abstraction API so your code is independent of the underlying logging framework.
- **Log4j / Log4j2:** actual logging implementations. To use Log4j2, exclude `spring-boot-starter-logging` and add `spring-boot-starter-log4j2`.

---

# Section B: Spring Security

Core principles of JWT architectures, OAuth2, and data protection.

### 26. How to implement JWT authentication step by step?

Stateless JWT authentication intercepts requests, validates the token, and sets the security context.

1. **Add dependencies:** `spring-boot-starter-security` and a JWT library (e.g., `io.jsonwebtoken:jjwt-api`).
2. **Create a `JwtUtils` class:** methods to generate a token (secret key, expiry, subject/claims) and parse/validate one.
3. **Build a custom filter (`OncePerRequestFilter`):**
   - Intercept every request.
   - Extract the token from the `Authorization: Bearer <token>` header.
   - Validate it with `JwtUtils`.
   - If valid, extract the username, load user details via `UserDetailsService`, and build a `UsernamePasswordAuthenticationToken`.
   - Set it in `SecurityContextHolder`.
4. **Configure the `SecurityFilterChain`:**
   ```java
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                  JwtAuthenticationFilter jwtFilter) throws Exception {
       return http
           .csrf(csrf -> csrf.disable()) // Stateless JWT, so CSRF is disabled
           .sessionManagement(session ->
               session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
           .authorizeHttpRequests(auth -> auth
               .requestMatchers("/api/auth/**").permitAll() // Whitelist login/register
               .anyRequest().authenticated()
           )
           .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
           .build();
   }
   ```
5. **Create an authentication endpoint:** accepts credentials, authenticates via `AuthenticationManager`, and returns a newly minted JWT.

### 27. How do you validate that the same user is making the request using JWT?

JWTs are stateless — the server doesn't check a DB or session cache on each request. It relies on cryptographic validation:

- **Signature verification:** a JWT has three dot-separated parts — Header, Payload, Signature. The filter re-signs Header + Payload with the server's secret key and compares it to the incoming signature. If someone alters the username in the payload, the signatures won't match and the request is rejected.
- **Context integrity:** after verification, the filter extracts the subject (username) and Spring Security populates `SecurityContextHolder` with it.
- **Controller-level validation:** use `@AuthenticationPrincipal` to confirm a resource belongs to the caller:
  ```java
  @GetMapping("/users/{id}")
  public ResponseEntity<?> getUserData(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails currentUser) {
      // Validate that the ID belongs to currentUser before returning data
  }
  ```

### 28. Difference between OAuth2 and JWT?

| Feature | JWT (JSON Web Token) | OAuth2 |
|---|---|---|
| **What is it?** | Self-contained token **format** (JSON) | Industry-standard authorization **framework/protocol** |
| **Purpose** | Securely transmit verifiable claims/identity info | Delegate resource access to third-party apps without sharing passwords |
| **State** | Stateless — all data is inside the token | Can be stateful or stateless; relies on Auth Server + Resource Server |
| **Relationship** | Often the token format issued by an OAuth2 server | Defines *how* tokens are requested, issued, and used |

### 29. How does OAuth2 work (authentication vs authorization)?

OAuth2 is fundamentally an **authorization** framework — it determines what a client app may do. Protocols built on top (e.g., **OpenID Connect / OIDC**) add an identity layer for **authentication** (who the user is).

**The 4 roles in an OAuth2 flow:**

1. **Resource Owner** — the end-user who owns the data.
2. **Client** — the third-party app requesting access.
3. **Authorization Server** — manages credentials, issues codes and tokens (Okta, Keycloak, Google).
4. **Resource Server** — the backend API holding protected data (your Spring Boot app).

**Authorization Code flow:**

```
[ Client App ] ──(1) Redirect to Auth Server──────> [ Authorization Server ]
[ Client App ] <──(2) Grants Auth Code ──────────── [ Authorization Server ]  (after user logs in)
[ Client App ] ──(3) Exchange Auth Code ──────────> [ Authorization Server ]
[ Client App ] <──(4) Returns Access Token ──────── [ Authorization Server ]
[ Client App ] ──(5) Send Access Token ───────────> [ Resource Server (Your Spring App) ]
```

In step 5, your Resource Server verifies the token with the Authorization Server (or validates the JWT signature locally) and grants access.

### 30. How do you implement Role-Based Access Control (RBAC)?

**Step 1 — Map roles in user details.** Expose granted authorities prefixed with `ROLE_` (e.g., `ROLE_ADMIN`, `ROLE_USER`).

**Step 2 — URL-level rules in the `SecurityFilterChain`:**

```java
auth.requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
```

**Step 3 — Method-level security.** Add `@EnableMethodSecurity` to a config class, then:

```java
@Service
public class OrderService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(Long id) { ... }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Order getOrderDetails(Long id) { ... }
}
```

### 31. How do you secure REST endpoints?

- **Enforce HTTPS always** (`server.ssl.enabled=true`) to prevent man-in-the-middle sniffing.
- **Disable CSRF selectively:** safe to disable for fully stateless APIs using JWT headers; keep it enabled when using stateful cookies.
- **Strict CORS:** no wildcard (`*`) origins in production — whitelist authorized frontend domains.
- **Input validation:** use `@Valid`, `@NotNull`, `@Size` on request bodies to block basic injection attacks.
- **Hide stack traces:** never leak DB exceptions; use a `@RestControllerAdvice` to return clean error payloads.

### 32. How do you store passwords? (Hashing best practices)

Never store plain text or reversibly encrypted passwords. Reversible keys and unsalted MD5 are easily cracked via precomputed rainbow tables.

- **Use one-way adaptive hashing:** BCrypt, SCrypt, or Argon2 — designed to slow brute-force attacks.
- **Salting:** each password is combined with a unique random salt before hashing, so identical passwords yield different hashes.

**Spring implementation** (BCrypt handles salting internally):

```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- **Saving a user:** `passwordEncoder.encode(rawPassword)` → store the result.
- **Login:** Spring Security compares via `passwordEncoder.matches(rawPassword, encodedPassword)`.

---

# Section C: Transactions, Exceptions & REST Design

## C1. Transactions & `@Transactional` Internals

### 33. How does `@Transactional` work internally? Explain propagation and isolation levels.

**Internal mechanism (Spring AOP proxies).** Spring scans for `@Transactional` and wraps the bean in a dynamic AOP proxy.

1. **Intercepting the call:** clients call the proxy, not your bean directly.
2. **Opening the connection:** the proxy asks a `PlatformTransactionManager` (`DataSourceTransactionManager`, `JpaTransactionManager`) for a connection and turns off auto-commit (`connection.setAutoCommit(false)`).
3. **Execution & binding:** the connection is bound to the current thread via `TransactionSynchronizationManager`, then your target method runs.
4. **Commit or rollback:**
   - Success → `connection.commit()`.
   - `RuntimeException` or `Error` → `connection.rollback()`.

> ⚠️ **Checked exceptions do not roll back by default.** Use `@Transactional(rollbackFor = Exception.class)` to change that.
>
> ⚠️ **Self-invocation pitfall:** if method A calls method B in the *same class*, B's `@Transactional` is ignored because the call bypasses the proxy.

**Propagation levels**

| Level | Behavior |
|---|---|
| `REQUIRED` (default) | Joins the existing transaction, or creates one if none exists |
| `REQUIRES_NEW` | Suspends the current transaction, runs in a new independent one, then resumes the outer |
| `NESTED` | Creates a **savepoint**; if the nested part fails, rolls back only to the savepoint |
| `MANDATORY` | Requires an existing transaction; throws if none |
| `NOT_SUPPORTED` / `NEVER` | Runs non-transactionally; suspends an existing transaction / throws if one exists |

**Isolation levels**

| Level | Prevents | Remaining anomaly |
|---|---|---|
| `READ_UNCOMMITTED` | — | Dirty reads |
| `READ_COMMITTED` | Dirty reads | Non-repeatable reads |
| `REPEATABLE_READ` | Dirty + non-repeatable reads | Phantom reads |
| `SERIALIZABLE` | All anomalies | Significant performance cost (sequential execution) |

## C2. Exception Handling

### 34. How do you implement global exception handling in Spring Boot?

Use a centralized `@RestControllerAdvice` component, decoupling error mapping from controller logic.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Specific domain exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex,
                                                            WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Fallback for any uncaught exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,
                                                               WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now(),
            "An unexpected error occurred internal to the server.",
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### 35. Purpose of `@ControllerAdvice` and `@ExceptionHandler`?

- **`@ExceptionHandler`** — method-level; declares which method handles specific exceptions. Instead of large try-catch blocks, exceptions bubble up and Spring routes them here.
- **`@ControllerAdvice`** — class-level interceptor. An `@ExceptionHandler` inside a normal controller only covers that controller; `@ControllerAdvice` applies globally, giving unified formatting, structures, and status codes across the API.

## C3. REST API Design

### 36. PUT vs PATCH?

| | PUT | PATCH |
|---|---|---|
| **Semantics** | Full replacement | Partial update |
| **Payload** | Entire resource representation | Only the fields to change |
| **Missing fields** | Overwritten with null/default | Left untouched |
| **Idempotency** | Strictly idempotent | Not guaranteed — depends on design (e.g., JSON Patch / JSON Merge Patch); appending to an array is non-idempotent |

### 37. Can we perform an update using HTTP POST?

**Yes.** HTTP methods are semantic guidelines, not technical enforcers.

- **Technically viable:** a POST has a body and hits a URL; the controller and DB don't care about the verb and will run an `UPDATE`.
- **Why it's sometimes done:** complex operations that don't map to direct resource modification (e.g., `/api/orders/123/calculate-discounts-and-reprocess`) — treat the command as a pseudo-resource.
- **Why to avoid it for standard updates:** it breaks REST conventions. POST is non-idempotent, so gateways, proxies, and caches won't cache or automatically retry it, whereas idempotent methods can be retried safely.

### 38. How do you design pagination and sorting in a REST API?

Pass them as **query parameters** (not bodies on GET):

```http
GET /api/products?page=0&size=20&sort=price,desc&sort=name,asc
```

Spring Data's `Pageable` abstraction parses these automatically:

```java
@GetMapping("/products")
public ResponseEntity<Page<ProductDTO>> getProducts(Pageable pageable) {
    // page, size, and sort query strings are bound into Pageable
    Page<ProductDTO> products = productService.findAllProducts(pageable);
    return ResponseEntity.ok(products);
}
```

In the repository, extend `JpaRepository` / `PagingAndSortingRepository`, which appends `LIMIT`, `OFFSET`, and `ORDER BY` for you:

```java
Page<Product> findAll(Pageable pageable);
```

## C4. API Strategy & Microservices

### 39. Why is an API contract important in microservices? How do teams collaborate using Swagger?

**Importance.** In a microservice mesh, teams release independently. If the Inventory team renames a payload field without telling the Checkout team, checkout breaks in production. An **API contract** — a platform-agnostic blueprint (OpenAPI YAML/JSON) — defines:

- Every active endpoint
- Required auth parameters
- Mandatory query strings and data structures
- Data types and schemas of response bodies and error models

As long as a service adheres to its contract, it can be refactored or redeployed without breaking consumers.

**Collaboration using Swagger / OpenAPI**

1. **Design-first (recommended for collaboration):** teams write the OpenAPI YAML together (e.g., in SwaggerHub) *before* writing code.
   - *Parallel tracks:* the consumer team spins up a mock server from the spec immediately.
   - *Implementation:* the backend team uses OpenAPI Generator to scaffold Spring controllers/interfaces.
2. **Code-first:** the backend team writes Spring Boot code and annotates endpoints (via `springdoc-openapi`).
   - The app exposes a JSON spec and a `/swagger-ui/index.html` dashboard.
   - Other teams live-test endpoints, explore models, and download the schema to generate client SDKs.

---

# Section D: JPA & Hibernate

## D1. ORM Frameworks & Entity Management

### 40. What is JPA? How is it different from Hibernate?

- **JPA (Jakarta Persistence API):** a **specification** — rules, interfaces, and annotations (`@Entity`, `@Table`, `@Id`) for ORM in Java. It cannot execute queries on its own.
- **Hibernate:** a **framework implementing** the JPA spec. It's the engine that compiles JPQL into native SQL, manages sessions, and talks to JDBC drivers.

### 41. What is the role of `EntityManager`?

The core interface for interacting with the **Persistence Context** (a first-level cache of entity instances in a transaction/session). It handles the entity CRUD lifecycle: `find()`, `persist()`, `merge()`, `remove()`.

### 42. `save()` vs `persist()` vs `merge()` vs `saveAndFlush()`?

| Method | Origin | Behavior |
|---|---|---|
| `persist(entity)` | JPA standard | Makes a transient instance persistent. Doesn't issue INSERT immediately — scheduled for commit. Returns `void`. |
| `save(entity)` | Hibernate / Spring Data | Like `persist()` but **returns the entity**. If the entity has an ID, decides between update and insert. |
| `merge(entity)` | JPA standard | Copies the state of a **detached** entity onto a managed instance; fetches or creates one if not in the context. |
| `saveAndFlush(entity)` | Spring Data JPA | Saves and **immediately flushes** all pending changes to the DB rather than waiting for the transaction boundary. |

### 43. What are Hibernate entity states?

```
[ Transient ] ──(persist / save)──> [ Persistent ] ──(close / detach)──> [ Detached ]
                                         │                                   │
                                      (remove)                            (merge)
                                         ▼                                   ▼
                                    [ Removed ]                        [ Persistent ]
```

- **Transient:** created in memory (`new User()`), no DB identity, not tied to a persistence context.
- **Persistent:** managed by the persistence context; field changes are tracked and synced on commit (**dirty checking**).
- **Detached:** was persistent, but the session closed or it was detached via `entityManager.detach()`; state no longer tracked.
- **Removed:** marked for deletion via `entityManager.remove()`; the SQL `DELETE` runs on flush.

## D2. Performance Tuning & Relationships

### 44. Lazy vs eager loading? When can EAGER hurt performance?

- **Lazy (`FetchType.LAZY`):** fetches associated data only when accessed (e.g., `user.getOrders()`); uses a proxy initially.
- **Eager (`FetchType.EAGER`):** fetches the entity and its associations immediately (often via outer joins).
- **Performance impact:** eager loading of collections (e.g., 100 Users each with an EAGER list of Orders) forces massive joins, bloats memory, and easily triggers the **N+1 select problem**.

### 45. What is the N+1 problem? How do you solve it?

The ORM runs **1** query to fetch N parent entities, then **N** extra queries to fetch each parent's children.

**Solutions:**

1. **`JOIN FETCH` (JPQL)** — single query with an explicit join:
   ```sql
   SELECT u FROM User u JOIN FETCH u.orders
   ```
2. **`@EntityGraph`** — declarative template of attributes to load eagerly for a specific repository method.
3. **`@BatchSize`** — loads collections in batches (e.g., 20 at a time) with an `IN` clause instead of one by one.

### 46. 1st-level vs 2nd-level cache in Hibernate?

| | 1st-level cache | 2nd-level cache |
|---|---|---|
| **Scope** | Session (per transaction) | SessionFactory (application-wide) |
| **Default** | Always on; cannot be disabled | Optional |
| **Effect** | Same entity fetched repeatedly in a transaction hits the DB once | Caches across transactions, sessions, and threads |
| **Providers** | Built-in | Third-party: Ehcache, Redis, etc. |

### 47. Explain `@OneToMany` and `@ManyToOne` with an example.

A relationship has an **owning side** (holds the foreign key) and a **referencing side** (uses `mappedBy`). This prevents duplicate mapping tables.

```java
@Entity
public class Company { // Referencing side
    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
}

@Entity
public class Employee { // Owning side (holds FK: company_id)
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
```

### 48. How do you implement composite primary keys?

Use `@IdClass` or `@EmbeddedId`. Example with `@EmbeddedId`:

```java
@Embeddable
public class OrderId implements Serializable {
    private Long orderId;
    private Long productId;
    // must override equals() and hashCode()
}

@Entity
public class OrderItem {
    @EmbeddedId
    private OrderId id;

    private int quantity;
}
```

### 49. How do you use Enums in an entity?

Always use `@Enumerated(EnumType.STRING)`. Avoid `EnumType.ORDINAL`: inserting a new enum value anywhere except the end silently corrupts existing mappings.

```java
@Enumerated(EnumType.STRING)
private StatusType status; // Stores 'ACTIVE' instead of 0
```

## D3. Querying & Advanced Config

### 50. How to fetch only selected columns using JPQL?

Use a **projection** with a constructor expression mapping directly to a DTO:

```java
@Query("SELECT new com.example.UserDTO(u.id, u.email) FROM User u")
List<UserDTO> fetchUserDetails();
```

### 51. JPQL vs native SQL?

- **JPQL:** standard CRUD and domain operations. Database-agnostic (Hibernate translates via the configured Dialect) and respects entity lifecycle/cache state.
- **Native SQL:** database-specific optimizations (index hints, spatial extensions, recursive CTEs) or syntax JPQL doesn't support.

### 52. How do you call a stored procedure from JPA?

Use `@Procedure` in a Spring Data repository:

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Procedure(procedureName = "calculate_bonus")
    void calculateUserBonus(@Param("userId") Long userId);
}
```

### 53. How do you write derived queries in Spring Data JPA?

Spring parses the method name using a predefined grammar:

```java
// SELECT * FROM users WHERE email_address = ? AND status = ?
List<User> findByEmailAddressAndStatus(String email, String status);
```

---

# Section E: Database Theory & SQL

## E1. Database Theory & Optimization

### 54. What is a database view? Materialized vs non-materialized?

A **view** is a virtual table representing the result of a predefined SQL query.

- **Non-materialized:** stores no data; runs its defining query against the base tables each time it's queried.
- **Materialized:** physically stores the query result on disk. Greatly speeds up slow, complex queries but must be **refreshed** when base data changes.

### 55. When should you create a view?

- **Simplify complexity** — hide large multi-table joins from application developers.
- **Security layer** — expose only a view that omits sensitive columns (credit card, salary) while showing public profile data.

### 56. What is a connection pool? How do you decide its size?

A pool (e.g., **HikariCP**) keeps a cache of open DB connections. Reusing them avoids the network cost of opening and tearing down TCP connections on every request.

**Sizing heuristic:**

```
connections = (core_count × 2) + effective_spindle_count
```

> An oversized pool hurts performance through CPU context switching and disk contention.

### 57. What are ACID properties? (Bank-transfer example: $100 from A to B)

| Property | Meaning | Example |
|---|---|---|
| **Atomicity** | All or nothing | If A is debited but the system crashes before B is credited, everything rolls back |
| **Consistency** | Moves from one valid state to another | Total balance across both accounts is unchanged |
| **Isolation** | Concurrent transactions can't see each other's partial work | Transaction 2 can't see A's reduced balance until Transaction 1 commits |
| **Durability** | Committed data is permanent | Even if power fails a millisecond later, logs ensure the update persists |

### 58. How do you handle concurrent updates to the same record?

- **Optimistic locking:** assumes conflicts are rare. A `@Version` column is checked on save: if two transactions read version 5 and the first saves (→ 6), the second's save fails with `OptimisticLockException` and must retry.
- **Pessimistic locking:** assumes conflicts are common. Locks rows on selection (`SELECT ... FOR UPDATE`); nobody else can read/modify them until the lock-holding transaction commits.

### 59. How do you optimize a slow SQL query?

1. Run **`EXPLAIN ANALYZE`** to find bottlenecks (e.g., full-table scans vs index scans).
2. Add **indexes** on columns used in `WHERE`, `JOIN`, and `ORDER BY`.
3. **Avoid `SELECT *`** — query only needed columns.
4. Normalize structural bottlenecks, or intentionally denormalize into **materialized views** for reporting-heavy workloads.

### 60. What is indexing? How does it help? Types?

An index is a lookup structure (typically a **B-Tree**) over table columns. It improves search from linear **O(n)** to logarithmic **O(log n)**.

- **Clustered index:** dictates the physical row order on disk (typically the primary key). Only **one per table**.
- **Non-clustered index:** a separate structure mapping column key values to row pointers.

## E2. SQL Query Challenges

### 61. `INNER JOIN` vs `LEFT JOIN`?

- **INNER JOIN:** only rows with matches in **both** tables.
- **LEFT JOIN:** **all** rows from the left table plus matching rows from the right; unmatched right columns are `NULL`.

*Example (Users and Orders):* INNER JOIN returns only users who placed an order; LEFT JOIN returns all users regardless.

### 62. `WHERE` vs `HAVING`?

- **`WHERE`** — filters individual rows **before** aggregation; cannot use aggregate functions (`SUM()`, `AVG()`).
- **`HAVING`** — filters groups **after** `GROUP BY`; operates on aggregates.

### 63. Query to find the second-highest salary

```sql
SELECT MAX(salary)
FROM employee
WHERE salary < (SELECT MAX(salary) FROM employee);
```

### 64. Query using `GROUP BY` + `HAVING`

*Goal: departments with more than 5 employees.*

```sql
SELECT department_id, COUNT(id) AS employee_count
FROM employee
GROUP BY department_id
HAVING COUNT(id) > 5;
```

### 65. `DELETE` vs `TRUNCATE` vs `DROP`?

| Command | Type | Behavior |
|---|---|---|
| `DELETE` | DML | Removes rows (optional `WHERE`); fires row-level triggers; logs each deletion; can be rolled back; slower |
| `TRUNCATE` | DDL | Empties the whole table by deallocating data pages; no row triggers; minimal logging; very fast; hard to undo |
| `DROP` | DDL | Permanently removes the table — data, columns, indexes, and permissions |

## E3. Enterprise Configuration

### 66. How to configure multiple databases in a Spring Boot app?

Disable auto-configuration of the single data source and declare separate properties, `DataSource`s, `EntityManagerFactory`s, and `TransactionManager`s.

**Step 1 — Properties (`application.properties`)**

```properties
spring.datasource.primary.jdbc-url=jdbc:postgresql://localhost:5432/primary_db
spring.datasource.primary.username=postgres
spring.datasource.primary.password=secret

spring.datasource.secondary.jdbc-url=jdbc:mysql://localhost:3306/secondary_db
spring.datasource.secondary.username=root
spring.datasource.secondary.password=secret
```

**Step 2 — Java config for each data source** (mark one `@Primary`)

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.repository.primary",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDbConfig {

    @Primary
    @Bean(name = "primaryProperties")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource dataSource(
            @Qualifier("primaryProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.model.primary")
                .persistenceUnit("primary")
                .build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
```

> Replicate this structure in a second class — omit `@Primary` and point it at the secondary packages and property prefix.


---

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


---

# Apache Kafka — Interview Prep Guide

A deep-dive reference covering Spring Kafka configuration for Sagas, consumer lag, cross-cluster replication, producer `acks`, log segments, and a full producer/consumer config cheat sheet.

## Table of Contents

1. [Spring Kafka Properties for Saga Orchestration](#1-spring-kafka-properties-for-saga-orchestration)
2. [Consumer Lag: Diagnosis & Fixes](#2-consumer-lag-diagnosis--fixes)
3. [Replicating Data Across Kafka Clusters](#3-replicating-data-across-kafka-clusters)
4. [Producer `acks` Explained](#4-producer-acks-explained)
5. [Log Segments](#5-log-segments)
6. [Producer & Consumer Configuration Reference](#6-producer--consumer-configuration-reference)

---

# 1. Spring Kafka Properties for Saga Orchestration

For a Spring Boot 3.x + Kafka + Micrometer Tracing architecture, these properties control tracing propagation, reliability, and data integrity.

## 1.1 Tracing & Observability

These let Micrometer Tracing inject trace context into Kafka message headers and extract it across services, keeping distributed traces intact.

| Property | What it does |
|---|---|
| `spring.kafka.template.observation-enabled: true` | The `KafkaTemplate` (producer) records observations and injects tracing metadata (e.g., W3C `traceparent`) into record headers before publishing |
| `spring.kafka.listener.observation-enabled: true` | The `@KafkaListener` container (consumer) reads tracing headers from incoming messages and creates a child span, keeping logs connected |

## 1.2 Core Operational Properties

| Property | What it does |
|---|---|
| `spring.kafka.bootstrap-servers` | Comma-separated host/port list (e.g., `localhost:9092`) for the initial cluster connection |
| `spring.kafka.consumer.group-id` | Unique string identifying the consumer group. E.g., `payment-service` uses its own group ID so Kafka can distribute partitions and track offsets for payment processing |
| `spring.kafka.consumer.auto-offset-reset` | What to do when there's no initial offset (or it no longer exists). `earliest` → read from the beginning of the topic; `latest` → only new events from now on |

## 1.3 Reliability & Data Integrity (Crucial for Sagas)

Saga orchestrators need precise delivery guarantees so transactions aren't processed twice or dropped.

| Property | What it does / why it matters |
|---|---|
| `spring.kafka.producer.acks` | How many replicas must acknowledge a write. For financial or critical inventory updates, use **`all`** — the message is replicated across all in-sync replicas, preventing loss if a broker crashes |
| `spring.kafka.consumer.enable-auto-commit: false` | Disables automatic offset commits. A Saga worker should commit an offset **only after** business logic (e.g., a MySQL update) fully succeeds. Commit manually, or let the Spring container commit after execution, to avoid losing messages in a crash |

## 1.4 Production-Ready `application.yml`

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092

    # 1. Tracing propagation
    template:
      observation-enabled: true
    listener:
      observation-enabled: true

    # 2. Producer reliability
    producer:
      acks: all
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

    # 3. Consumer reliability
    consumer:
      group-id: inventory-service-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.saga.dto"
```

---

# 2. Consumer Lag: Diagnosis & Fixes

## What is consumer lag?

The gap between the **latest offset produced** to a partition and the **offset your consumer group has processed**. Growing lag means consumers can't keep up with incoming volume.

## Step 1: Diagnose before fixing

*"How do you identify consumer lag?"*

- Use `kafka-consumer-groups.sh --describe --group <group>`, or tools like Burrow, Kafka Manager, Confluent Control Center, or metrics piped into Splunk/Grafana.
- Check lag **per partition**, not just in aggregate — one slow partition can hide behind healthy ones.
- Determine whether lag grows **steadily** (systemic issue) or **spikes periodically** (traffic bursts).

## Step 2: Common causes & fixes

### 1. Not enough consumer parallelism
- **Cause:** fewer active consumers than partitions, so one consumer handles too much.
- **Fix:** scale consumers up to the partition count (one consumer per partition is the maximum useful parallelism in a group). If already maxed, increase the topic's partition count — this can affect ordering guarantees for existing keys, so plan carefully.

### 2. Slow per-message processing
- **Cause:** expensive synchronous work per message — blocking DB call, external API call, heavy computation inside the listener.
- **Fix:**
  - Move non-critical work (analytics, notifications) to async processing or a separate downstream topic.
  - **Batch operations** — instead of one DB write per message, batch writes using `max.poll.records`.
  - **Cache** frequently looked-up data (e.g., Redis) to avoid repeated DB round-trips.

### 3. Poor batch/poll configuration
- **Cause:** defaults not tuned for your throughput — `max.poll.records` too low/high, or `max.poll.interval.ms` too short, causing rebalances mid-processing.
- **Fix:** tune `fetch.min.bytes`, `max.poll.records`, and `max.poll.interval.ms` to actual per-batch processing time.

### 4. Frequent consumer rebalances
- **Cause:** consumers dropping out (crashes, processing exceeding `max.poll.interval.ms`, deployments) trigger rebalances that pause the whole group.
- **Fix:** increase `max.poll.interval.ms` if processing legitimately takes longer; use incremental cooperative rebalancing (`CooperativeStickyAssignor`) instead of the default eager strategy to reduce pause impact.

### 5. Producer throughput exceeds consumer capacity
- **Cause:** an upstream traffic spike outpaces steady-state consumer capacity.
- **Fix:** autoscale consumers on lag metrics (commonly **KEDA** in Kubernetes), or apply backpressure/throttling upstream.

### 6. GC pauses / resource starvation
- **Cause:** JVM garbage collection pauses or CPU/memory starvation on the consumer pod.
- **Fix:** tune JVM heap/GC settings, set adequate Kubernetes resource limits, and correlate OOMs or long GC pauses with lag spikes.

## Sample interview answer

> "I treat consumer lag as a signal to investigate at the partition level first, not just the aggregate group level — one hot or skewed partition can hide behind otherwise healthy ones. Once I identify the pattern — steady growth versus periodic spikes — I look at whether it's a parallelism issue (not enough consumers for the partition count) or a processing-time issue (a blocking call inside the consumer loop).
>
> For example, if a consumer was doing a synchronous DB lookup per message, I'd offload it to an async path or introduce caching to cut per-message latency. I also make sure offset commits are manual and only happen after successful processing — combined with `max.poll.interval.ms` tuning — so I'm not triggering unnecessary rebalances that pause the whole group.
>
> If the root cause is genuinely higher throughput than the current consumer count can handle, I'd scale consumers to match partition count, and if that still isn't enough, increase partitions — keeping in mind that changes ordering guarantees for a key going forward, so it has to be a deliberate decision, not a reflexive fix."

---

# 3. Replicating Data Across Kafka Clusters

## Why replicate across clusters?

Disaster recovery, multi-region/multi-datacenter deployments, migrating from on-prem to cloud, or isolating workloads (e.g., replicating production data to a staging cluster).

## Primary tool: Kafka MirrorMaker 2 (MM2)

MM2 is Kafka's built-in cross-cluster replication tool, built on **Kafka Connect**, and the standard answer to this question.

**How it works**

- Runs as a Kafka Connect cluster with a `MirrorSourceConnector` and `MirrorCheckpointConnector`.
- Reads topics from a source cluster and replicates to a target cluster, preserving partitioning, offsets (via **offset translation**), and topic configs.
- Supports **active-passive** (one-way, common for DR) and **active-active** (bidirectional, common for multi-region) topologies.

**Key features**

- **Topic renaming:** by default replicated topics get the source cluster alias as a prefix (e.g., `sourceCluster.orders`) to avoid naming collisions in active-active setups.
- **Offset translation:** MM2 keeps a mapping between source and target offsets via internal checkpoint topics, so consumers can fail over and resume roughly where they left off (translated equivalents, not identical offsets).
- **Consumer group replication:** group offsets can be replicated too, so a failover doesn't restart every topic from the beginning.

## Other options

| Tool | Notes |
|---|---|
| **Confluent Replicator** | Confluent's commercial alternative to MM2; more polished tooling on Confluent Platform |
| **uReplicator (Uber)** | Older alternative with better rebalancing behavior; less common now that MM2 has matured |
| **Custom producer-consumer bridge** | Consume from cluster A, produce to cluster B. What MM2 does under the hood — rarely worth building yourself |

## Points that show depth

1. **Ordering / exactly-once caveats:** cross-cluster replication doesn't guarantee exactly-once end to end by default — duplicates are possible on failover, so target-side consumers should be **idempotent**.
2. **Replication lag monitoring:** monitor lag between clusters just like consumer lag — it determines your actual **RPO (recovery point objective)** in a DR scenario.
3. **Topic config sync:** partition counts, retention settings, and ACLs must be kept in sync or explicitly configured. MM2 can sync configs, but it has to be set up deliberately.

## Sample interview answer

> "The standard way to replicate data between Kafka clusters is MirrorMaker 2, which runs on top of Kafka Connect. It reads from a source cluster and writes to a target, and depending on the use case you'd set it up as active-passive — for disaster recovery, where the target is a standby — or active-active for multi-region setups where both clusters serve traffic.
>
> A few things I'd pay attention to: MM2 does offset translation rather than copying identical offsets, since source and target don't share the same offset numbering — so consumer groups can resume in the right place after failover, but it's translated, not identical. I'd also treat target-side consumers as needing to be idempotent, since replication doesn't guarantee exactly-once delivery, especially around a failover.
>
> And just like consumer lag within a single cluster, I'd monitor replication lag between clusters, since that tells you your real recovery point objective — how much data you could lose if the source cluster went down right now."

---

# 4. Producer `acks` Explained

## What is `acks`?

A producer setting that controls **how many broker replicas must confirm receipt** of a message before the write is considered successful. It's the durability vs. latency/throughput trade-off knob.

## The three values

| Value | Behavior | Trade-offs |
|---|---|---|
| **`acks=0`** | Producer doesn't wait for any acknowledgment — fire and forget | Fastest, but no durability guarantee; the producer can't know if the message was lost. Only for cases where occasional loss is acceptable (e.g., high-volume metrics/logs) |
| **`acks=1`** (default in older Kafka versions) | Waits for the **leader** replica only | Middle ground; if the leader crashes before followers replicate the message, it's lost |
| **`acks=all`** (or `-1`) | Waits for **all in-sync replicas (ISRs)** | Strongest guarantee — a message is committed only when every ISR has it, so another ISR can take over without loss. Slower due to extra round-trips; use when data loss is unacceptable |

## How it connects to other configs

`acks=all` alone isn't the full durability story:

- **`min.insync.replicas`** — the minimum number of ISRs that must acknowledge for a write to succeed. With `acks=all` but `min.insync.replicas=1`, you get little extra protection. A common production combination:
  - **replication factor 3 + `min.insync.replicas=2` + `acks=all`** → tolerates one broker failure with zero data loss.
- **`enable.idempotence=true`** — pairs with `acks=all` to prevent duplicate writes on retries. Kafka **requires** `acks=all` when idempotence is enabled.

## Sample interview answer

> "`acks` controls how many replicas must confirm a write before the producer treats it as successful — it's essentially Kafka's durability dial. `acks=0` doesn't wait for confirmation, so it's fastest but you can silently lose messages. `acks=1` waits for just the leader — a reasonable middle ground, but there's still a window where you could lose data if the leader crashes after acknowledging but before followers replicate.
>
> I used `acks=all`, which waits for all in-sync replicas, so a message is only committed once it's safely replicated. I paired it with `enable.idempotence=true`, since Kafka requires `acks=all` for idempotence — together they gave durability plus protection against duplicate writes from retries.
>
> `acks=all` alone isn't the complete picture — it works with `min.insync.replicas`. With replication factor 3 but `min.insync.replicas=1`, you're not getting much extra protection. For critical data I'd use replication factor 3 with `min.insync.replicas=2`, which tolerates one broker going down with zero message loss."

---

# 5. Log Segments

## What is a log segment?

Each Kafka partition is stored on disk as an **append-only commit log**. Instead of one giant file, Kafka splits it into smaller chunks called **log segments** — think of the partition as a book and each segment as a chapter.

## Why segments instead of one big file?

1. **Efficient deletion/retention:** Kafka deletes data by retention policy (time or size). Rather than rewriting a huge file to drop old messages, it deletes the whole expired segment file — a cheap, atomic OS-level operation.
2. **Efficient reads:** smaller files are faster to search, index, and cache in the OS page cache.
3. **Manageable file sizes:** a single unbounded file would be unwieldy for the filesystem and for operations like compaction.

## What files make up a segment?

Each segment is a set of files sharing a **base offset** as their filename (e.g., `00000000000000368769`):

| File | Purpose |
|---|---|
| `.log` | The actual message data — the append-only file containing the records |
| `.index` | **Offset index** — maps message offsets to physical byte positions in the `.log`, enabling fast lookups without scanning the whole file |
| `.timeindex` | **Time index** — maps timestamps to offsets for lookups like "messages after this timestamp" (used by the `offsetsForTimes` API) |

## How segments are created and rolled

- Kafka writes to the **active segment** (the newest one) of each partition.
- A new segment is **rolled** when either:
  - `log.segment.bytes` is reached (default **1 GB**), or
  - `log.roll.ms` / `log.roll.hours` is reached (default **7 days**), even if not full.
- Only the active segment is writable; older segments are read-only.

## Ties to retention & compaction

- **Time/size-based retention** (`log.retention.hours`, `log.retention.bytes`): Kafka evaluates closed segments as a whole — if a segment's newest message is older than the retention period, the entire file is deleted.
- **Log compaction** (`cleanup.policy=compact`): also works segment by segment — the cleaner thread processes older closed segments, keeping only the latest value per key, and writes a new compacted segment to replace them.

## Sample interview answer

> "A Kafka partition's data on disk isn't one continuous file — it's broken into log segments. Each has a `.log` file with the message data, plus `.index` and `.timeindex` files that let Kafka quickly find an offset or timestamp without scanning the whole file.
>
> Only one segment per partition — the active segment — is open for writes at a time. New segments are rolled when the current one hits `log.segment.bytes` or `log.roll.ms`, whichever comes first.
>
> This matters because it makes retention and compaction efficient. Kafka doesn't rewrite a giant file to strip old messages; it deletes whole segment files once everything in them is older than the retention window. Compaction likewise processes and rewrites segments rather than the entire log, which is how Kafka handles high-throughput, long-retention topics without cleanup becoming a bottleneck."

---

# 6. Producer & Consumer Configuration Reference

## Producer configurations

| Property | Meaning |
|---|---|
| `bootstrap.servers` | Initial broker `host:port` list used to discover the full cluster |
| `acks` | Durability control — `0` (no wait), `1` (leader only), `all`/`-1` (all in-sync replicas) |
| `key.serializer` / `value.serializer` | Converts Java key/value objects into bytes (e.g., `StringSerializer`, `JsonSerializer`) |
| `enable.idempotence` | When `true`, prevents duplicates from producer retries via per-partition sequence numbers. Requires `acks=all` |
| `retries` | Number of retries on failed sends (transient broker errors). Effectively "retry until timeout" by default in modern Kafka |
| `retry.backoff.ms` | Wait between retry attempts |
| `max.in.flight.requests.per.connection` | Unacknowledged requests allowed before blocking. Must be ≤ 5 to preserve ordering when idempotence is enabled |
| `batch.size` | Max bytes batched per partition before sending — larger batches improve throughput at the cost of latency |
| `linger.ms` | How long to wait to accumulate more messages into a batch, even if `batch.size` isn't reached — trades a little latency for throughput |
| `compression.type` | Compresses batches (`gzip`, `snappy`, `lz4`, `zstd`) — less network/disk usage at some CPU cost |
| `buffer.memory` | Total memory the producer can use to buffer unsent messages; if exceeded, `send()` blocks or throws depending on `max.block.ms` |
| `partitioner.class` | Chooses the partition when none is set — default hashes the key (sticky partitioner for null keys) |
| `request.timeout.ms` | Max wait for a broker response before treating the request as failed |
| `delivery.timeout.ms` | Upper bound on total time from `send()` to success or failure, including retries |
| `transactional.id` | Enables exactly-once semantics across partitions/topics via Kafka transactions (`initTransactions()`, `beginTransaction()`, `commitTransaction()`) |

## Consumer configurations

| Property | Meaning |
|---|---|
| `bootstrap.servers` | Initial broker list (same as producer) |
| `group.id` | Identifies the consumer group — consumers sharing a `group.id` split partitions among themselves |
| `key.deserializer` / `value.deserializer` | Converts bytes back into Java objects (inverse of the serializer) |
| `enable.auto.commit` | If `true`, offsets are committed on an interval regardless of whether processing succeeded — risk of message loss on crash. Use `false` with manual commits for reliability |
| `auto.commit.interval.ms` | How often auto-commit fires, if enabled |
| `auto.offset.reset` | Behavior when there's no committed offset: `earliest`, `latest`, or `none` (throw an exception) |
| `max.poll.records` | Max records returned per `poll()` — controls processing batch size |
| `max.poll.interval.ms` | Max time between `poll()` calls before the consumer is considered dead and a rebalance triggers — tune when processing is slow |
| `session.timeout.ms` | How long the broker waits without a heartbeat before declaring the consumer dead and rebalancing |
| `heartbeat.interval.ms` | Heartbeat frequency to the group coordinator (roughly 1/3 of `session.timeout.ms`) |
| `fetch.min.bytes` | Minimum data the broker should have before answering a fetch — lets the consumer batch more per request |
| `fetch.max.wait.ms` | Max time the broker waits to satisfy `fetch.min.bytes` before responding anyway — bounds latency |
| `partition.assignment.strategy` | Partition assignment algorithm: `RangeAssignor`, `RoundRobinAssignor`, `StickyAssignor`, or `CooperativeStickyAssignor` (reduces rebalance disruption) |
| `isolation.level` | `read_committed` vs `read_uncommitted` — whether the consumer sees messages from in-progress/aborted transactions (relevant with transactional producers) |
| `client.id` | Identifies the consumer instance in logs/metrics for debugging |

## Sample interview framing

> "On the producer side, the configs I paid most attention to were `acks` and `enable.idempotence` for durability, `linger.ms` and `batch.size` for throughput tuning, and `retries` with `delivery.timeout.ms` to handle transient failures without silently dropping messages.
>
> On the consumer side, the most important ones were `enable.auto.commit` — which I disabled in favor of manual commits — `max.poll.interval.ms` and `session.timeout.ms`, which I tuned to avoid unnecessary rebalances when processing took longer than the defaults expected, and `auto.offset.reset`, since choosing `earliest` vs `latest` determines whether a new consumer replays history or only sees new messages."


---

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

