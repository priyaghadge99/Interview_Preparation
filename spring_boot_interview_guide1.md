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
