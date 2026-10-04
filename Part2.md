Part 1: Core Framework vs. Spring Boot
1. What is the difference between Spring Framework and Spring Boot?
	• Spring Framework: A comprehensive Java framework that provides core infrastructure support (like dependency injection, transaction management, and MVC). However, it requires massive amounts of manual configuration (XML or Java Config), manual dependency management, and an external servlet container (like Tomcat) to run web apps.
	• Spring Boot: An extension of the Spring Framework built on top of it. It eliminates boilerplate configuration through opinionated defaults, Auto-Configuration, Starter dependencies, and embedded servers (Tomcat, Jetty). It aims to get a production-ready application running instantly.
2. What does @SpringBootApplication do internally?
It is a convenience annotation that combines three core annotations:
	1. @SpringBootConfiguration: A specialized form of @Configuration, marking the class as a source of bean definitions.
	2. @EnableAuto-Configuration: Tells Spring Boot to automatically configure beans based on the dependencies present on the classpath.
	3. @ComponentScan: Directs Spring to scan the current package and its sub-packages for stereotype components (@Component, @Service, etc.).
3. How does Spring Boot Auto-Configuration work?
During startup, Spring Boot looks at your classpath for specific JAR files. It uses the @EnableAutoConfiguration annotation, which leverages the SpringFactoriesLoader mechanism.
	• It reads configuration classes listed in meta-files (like META-INF/spring.factories or standard AutoConfiguration.imports).
	• It evaluates conditional annotations like @ConditionalOnClass, @ConditionalOnMissingBean, and @ConditionalOnProperty.
	• If a condition matches (e.g., DataSource.class is on the classpath and no custom DataSource bean is defined), Spring Boot automatically creates and registers that bean for you.

Part 2: Component Scanning & Bean Creation
4. What is the difference between @Component, @Service, and @Repository?
All three are stereotype annotations, and @Service and @Repository are specialized aliases of @Component.
	• @Component: The generic stereotype for any Spring-managed component/bean.
	• @Service: Used in the service layer. It holds business logic and currently serves as a semantic marker (no extra behavior over @Component).
	• @Repository: Used in the data access layer. Beyond acting as a bean marker, it automatically translates database-specific exceptions into Spring’s uniform DataAccessException hierarchy.
5. What is @Bean vs @Component?
	• @Component is a class-level annotation used for automatic discovery via classpath scanning. You place it on your own classes when you have full access to the source code.
	• @Bean is a method-level annotation used inside a @Configuration class. You use it when you need to manually instantiate and configure a bean, or when wrapping third-party libraries where you cannot modify the source code to add @Component. Objectmapper eg
6. Difference between @Controller and @RestController?
	• @Controller: Used for traditional Spring MVC web applications. It returns a String that maps to a view (HTML/JSP) to be rendered by a view resolver. To return data directly, individual methods must be annotated with @ResponseBody.
	• @RestController: A convenience annotation that combines @Controller and @ResponseBody. Every handler method automatically serializes its return object directly into the HTTP response body (typically as JSON or XML).
7. What is @RestControllerAdvice vs @ControllerAdvice?
Similar to the controller distinction:
	• @ControllerAdvice: Used for global exception handling, model attributes, and data binding across multiple controllers. Methods inside typically return a view name.
	• @RestControllerAdvice: A combination of @ControllerAdvice and @ResponseBody. Any exception-handling method inside will automatically write its return object (e.g., an error payload) directly into the HTTP response body as JSON.

Part 3: Dependency Injection Internals
8. How does @Autowired work internally?
When the ApplicationContext loads, it registers a special BeanPostProcessor called AutowiredAnnotationBeanPostProcessor.
	1. During the bean initialization phase, this processor inspects beans using Java Reflection to find fields, constructors, or setter methods marked with @Autowired.
	2. It resolves the dependency by looking up a matching bean in the IoC container—firstly by Type.
	3. If multiple beans of the same type exist, it tries to narrow it down by Name (matching the field/parameter name to the bean ID). If it still cannot resolve a single unique bean, it throws a NoUniqueBeanDefinitionException.
9. Difference between @Autowired vs @Inject vs @Qualifier?
	• @Autowired: A Spring-native annotation used to inject dependencies. It supports a required attribute (defaults to true).
	• @Inject: Part of the standard Java CDI (JSR-330). It works almost identically to @Autowired but lacks the required attribute. It is useful if you ever intend to migrate away from Spring to another Java EE/Jakarta framework.
	• @Qualifier: Used alongside @Autowired or @Inject when multiple beans of the same type exist. It explicitly provides the exact name of the bean you want to inject to break the ambiguity.
10. What is Dependency Injection (IoC)? How does Spring perform DI using reflection?
	• Inversion of Control (IoC) is a design principle where the control of creating and managing objects is handed over to an external container (the Spring IoC Container) rather than the object instantiating its dependencies itself. Dependency Injection (DI) is the specific pattern used to implement IoC.
	• Reflection Mechanism: Spring reads your configuration or annotations, discovers class blueprints, and uses Java's Reflection API (Class.forName(), Constructor.newInstance()) to instantiate the objects. For field or setter injection, Spring uses reflection to bypass standard private access modifiers (field.setAccessible(true)) to dynamically inject dependency object references directly into the private properties of your bean.

Part 4: Bean Lifecycle & Scopes
11. Difference between ApplicationContext and BeanFactory?
	• BeanFactory: The root interface for the Spring IoC container. It provides basic configuration capabilities and uses lazy loading (beans are instantiated only when explicitly requested via getBean()). Ideal for lightweight, resource-constrained environments.
	• ApplicationContext: A sub-interface of BeanFactory. It inherits all its functionality but adds enterprise-level features: eager loading of singleton beans during startup, built-in AOP integration, internationalization (MessageSource), and application event publication.
12. Explain the complete lifecycle of a Spring Bean.
A bean goes through the following high-level sequence:
	1. Instantiation: Spring instantiates the bean object using reflection.
	2. Populate Properties: Spring injects dependencies into the bean via setters or fields.
	3. Aware Interfaces: If the bean implements any *Aware interfaces (e.g., BeanNameAware, ApplicationContextAware), Spring passes the corresponding framework objects.
	4. BeanPostProcessor (Before Initialization): The postProcessBeforeInitialization() methods are executed (this handles annotations like @PostConstruct).
	5. Initialization (init): * If the bean implements InitializingBean, afterPropertiesSet() is called.
		○ Any custom init-method declared is invoked.
	6. BeanPostProcessor (After Initialization): The postProcessAfterInitialization() methods execute (this is where AOP proxies are created).
	7. Ready for Use: The bean is alive and used by the application.
	8. Destruction: When the container shuts down:
		○ Methods annotated with @PreDestroy run.
		○ If the bean implements DisposableBean, destroy() is called.
		○ Any custom declared destroy-method is executed.
13. What are Bean scopes?
	• singleton (Default): Scopes a single bean definition to a single object instance per Spring IoC container.
	• prototype: Forces the creation of a brand new bean instance every single time it is requested from the container.
	• request: Scopes a single bean definition to the lifecycle of a single HTTP request (only valid in a web-aware ApplicationContext).
	• session: Scopes a single bean definition to the lifecycle of an HTTP Session (only valid in a web-aware ApplicationContext).

Part 5: Configuration & Environments
14. What are Spring Boot Starters?
Starters are a set of convenient dependency descriptors that you can include in your application. They bundle all the transitively required dependencies, versions, and build setups for a specific technology feature under a single dependency name (e.g., spring-boot-starter-web pulls in Tomcat, Spring MVC, Jackson for JSON parsing, and validation libraries automatically, preventing version mismatch hell).
15. How do you externalize configuration in Spring Boot?
Spring Boot allows you to externalize your configuration so you can work with the same application code in different environments. It looks for properties in a specific hierarchy. A few of the most common locations (ordered from highest priority to lowest) are:
	1. Command-line arguments (e.g., --server.port=8081).
	2. Java System properties (-Dserver.port=8081).
	3. OS Environment Variables (SERVER_PORT=8081).
	4. Application properties/YAML files outside the packaged jar.
	5. Application properties/YAML files packaged inside the jar (src/main/resources/application.properties).
16. Different ways to read values from application.properties?
	1. @Value annotation: Direct field injection for single properties:
Java

@Value("${app.timeout}")
private int timeout;
	2. @ConfigurationProperties: Binds a group of hierarchical properties to a structured POJO (Type-safe configuration).
	3. Using Environment Object: Programmatic access via injection:
Java

@Autowired
private Environment env;
// env.getProperty("app.timeout");
17. How to bind configuration properties to a POJO using annotations?
Create a standard class with fields matching your properties, and annotate it with @ConfigurationProperties:
Java

@Component
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String host;
    private int port;
    // Getters and Setters are required
}
(Make sure to have @EnableConfigurationProperties(MailProperties.class) or @ConfigurationPropertiesScan active if you don't use @Component on the POJO).
18. How to load values from a custom properties file in Spring Boot?
You can use the @PropertySource annotation alongside a @Configuration class:
Java

@Configuration
@PropertySource("classpath:custom.properties")
public class CustomConfig {
    @Value("${custom.feature.enabled}")
    private boolean enabled;
}
Note: @PropertySource does not load YAML files by default; it expects standard .properties formatting.

Part 6: Production Features & Server Customization
19. What is Spring Boot Actuator? What are its most used endpoints?
Actuator brings production-ready features to your application, allowing you to monitor, gather metrics, and interact with your app in production. Most used built-in endpoints include:
	• /actuator/health: Shows application health status (UP/DOWN, disk space, DB connections).
	• /actuator/info: Displays arbitrary application info.
	• /actuator/metrics: Exposes various application metrics (JVM memory, CPU usage, HTTP request counts).
	• /actuator/env: Exposes the current properties available from Spring’s Environment.
	• /actuator/loggers: Allows fetching and dynamically changing logging levels at runtime.
20. Difference between CommandLineRunner and ApplicationRunner?
Both are functional interfaces used to execute specific blocks of code right after the ApplicationContext is fully initialized and before startup completes. The only difference is how they handle arguments passed to the application:
	• CommandLineRunner: Accepts raw string arguments as a varargs array (String... args).
	• ApplicationRunner: Accepts encapsulated ApplicationArguments objects, which makes parsing option arguments (like --foo=bar) much cleaner.
21. How to apply code changes without restarting the server (hot reload)?
	1. Spring Boot DevTools: The easiest method. Add the spring-boot-devtools dependency. It monitors your classpath for changes; whenever a file updates, it triggers a lightning-fast application restart using an isolated class loader setup.
	2. JRebel: A commercial, advanced tool that reloads class files instantly on the fly without restarting the container context at all.
22. How to switch embedded server from Tomcat to Jetty?
You must exclude the default Tomcat dependency from spring-boot-starter-web and explicitly declare the Jetty starter. In a Maven pom.xml, it looks like this:
XML

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

Part 7: Advanced Customization & Logging
23. How to create custom annotations in Spring?
You combine standard Java meta-annotations (@Target, @Retention) with Spring's core annotations to build a custom composite annotation:
Java

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service // Combines behavior
@Transactional // Injects transactional behavior automatically
public @interface CustomTransactionalService {
}
24. What are Profiles and environment configurations in Spring Boot?
Profiles allow you to segregate parts of your application configuration and make them available only in specific environments (e.g., dev, test, prod).
	• Naming convention: You name your properties files application-{profile}.properties (e.g., application-dev.properties).
	• Activation: You activate a profile via command line (-Dspring.profiles.active=prod) or in your main properties file.
	• Bean Filtering: You can use the @Profile("prod") annotation on classes or methods to ensure they only register as beans when that exact profile is active.
25. How does Spring Boot handle logging? What is SLF4J and Log4j?
	• Spring Boot Handling: By default, Spring Boot uses Logback for routing internal logging statements, routed via an SLF4J abstraction facade. It automatically outputs formatted console logging out-of-the-box.
	• SLF4J (Simple Logging Facade for Java): It is not an actual logging implementation. It is an abstraction layer/API that allows you to write your code independent of underlying framework logic. If you decide to switch frameworks later, your log code does not break.
	• Log4j / Log4j2: Actual logging implementation frameworks. If you prefer to use Log4j2 instead of the default Logback implementation, you can simply exclude the spring-boot-starter-logging artifact and replace it with spring-boot-starter-log4j2.




• log4j2.
Here is the step-by-step breakdown of core Spring Boot Security principles, focusing on JWT architectures, OAuth2 paradigms, and data protection best practices.

26. How to Implement JWT Authentication Step-by-Step?
Implementing stateless JSON Web Token (JWT) authentication in Spring Security involves intercepting incoming requests, validating the token, and setting the security context.
	1. Add Dependencies: Include spring-boot-starter-security and a JWT library (like io.jsonwebtoken:jjwt-api).
	2. Create the JWT Utility Class (JwtUtils): Write methods to generate a token (using a secret signing key, expiration time, and subjects/claims) and to parse/validate the token from an incoming string.
	3. Build a Custom JWT Filter (OncePerRequestFilter): * Intercept every incoming HTTP request.
		○ Extract the token from the Authorization header (Bearer <token>).
		○ Validate the token using JwtUtils.
		○ If valid, extract the username, load user details via UserDetailsService, and construct an UsernamePasswordAuthenticationToken.
		○ Inject this authentication object into Spring’s SecurityContextHolder.
	4. Configure Security Filter Chain: In your @Configuration class, inject your custom JWT filter and configure the SecurityFilterChain bean:
Java

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    return http
        .csrf(csrf -> csrf.disable()) // Disable CSRF as JWT is stateless
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll() // Whitelist login/register
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
	5. Create Authentication Endpoint: Write a controller method that accepts login credentials, authenticates them via AuthenticationManager, and returns a freshly minted JWT string to the client.

27. How Do You Validate Whether the Same User Is Hitting the Request Using JWT?
Because JWTs are entirely stateless, the server does not check a database or session cache for every request. Instead, it relies on cryptographic validation:
	• Signature Verification: The incoming JWT contains three parts split by dots: Header, Payload, and Signature. The filter takes the Header and Payload, signs them again using the server's private secret key, and matches it against the incoming signature. If a malicious user changes the username inside the payload, the signatures will not match, and the request is rejected immediately.
	• Context Extracted Integrity: Once the token signature is verified, the filter extracts the username (subject) embedded inside the payload. Spring Security populates the SecurityContextHolder with this exact username.
	• Controller Level Validation: Inside your endpoints, you can ensure that a resource matches the caller by pulling the principal directly from the security context using the @AuthenticationPrincipal annotation:
Java

@GetMapping("/users/{id}")
public ResponseEntity<?> getUserData(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
    // Validate that the ID belongs to the currentUser's username before returning data
}

28. Difference Between OAuth2 and JWT?
It is common to confuse the two, but they are completely different conceptual entities:
Feature	JWT (JSON Web Token)	OAuth2
What is it?	A self-contained data token format structured as JSON.	An industry-standard Authorization Framework/Protocol.
Purpose	Used to securely transmit verifiable claims or identity info between parties.	Used to delegate resource access to third-party apps without sharing user passwords.
State	Stateless. All authorization data is bundled directly inside the token.	Can be stateful or stateless. Relies on architectural components (Auth Server, Resource Server).
Relationship	Often used as the token format generated by an OAuth2 server.	Defines the flow of how tokens are requested, issued, and used.
Export to Sheets

29. How Does OAuth2 Work (Authentication vs Authorization)?
OAuth2 is inherently an Authorization framework, meaning its main purpose is to determine what a client application is permitted to do. However, protocols built on top of it (like OpenID Connect / OIDC) add an identity layer to handle Authentication (who the user is).
The 4 Pillars of an OAuth2 Flow:
	1. Resource Owner: The end-user who owns the account/data.
	2. Client: The third-party application trying to access the user's data.
	3. Authorization Server: The system managing credentials, issuing authorization codes, and rendering tokens (e.g., Okta, Keycloak, Google Auth).
	4. Resource Server: The backend API holding the protected user data (your Spring Boot backend).
Basic Authorization Grant Flow:
[ Client App ] ──(1) Redirect to Auth Server──> [ Authorization Server ]
[ Client App ] <──(2) Grants Auth Code ──────── [ Authorization Server ] (After user logs in)
[ Client App ] ──(3) Exchange Auth Code ──────> [ Authorization Server ]
[ Client App ] <──(4) Returns Access Token ──── [ Authorization Server ]
[ Client App ] ──(5) Send Access Token ───────> [ Resource Server (Your Spring App) ]

Your Spring Boot Resource Server receives the Access Token in Step 5, verifies it with the Authorization Server (or validates its JWT signature locally), and grants access to the data.

30. How Do You Implement Role-Based Access Control (RBAC)?
Spring Boot allows you to enforce access controls at both the HTTP request routing layer and directly on individual Java methods.
Step 1: Map Roles in User Details
Your user object must expose granted authorities prefixed with ROLE_ (e.g., ROLE_ADMIN, ROLE_USER).
Step 2: Global Configuration Routing
You can restrict entire URL path patterns inside the SecurityFilterChain:
Java

auth.requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
Step 3: Method-Level Security
Enable method annotations by adding @EnableMethodSecurity to a configuration class. Now, you can lock down individual service or controller methods using Expression Language:
Java

@Service
public class OrderService {
@PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(Long id) { ... }
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Order getOrderDetails(Long id) { ... }
}

31. How Do You Secure REST Endpoints?
Securing REST endpoints requires defensive coding practices combined with framework configuration:
	• Enforce HTTPS Always: Force encryption in transit (server.ssl.enabled=true) to block Man-in-the-Middle (MitM) packet sniffing.
	• Disable CSRF selectively: If your API is entirely stateless (using JWT headers), Cross-Site Request Forgery is typically disabled (csrf.disable()). If using stateful cookies, keep CSRF active.
	• Configure Strict CORS: Set tight Cross-Origin Resource Sharing rules. Do not allow wildcard (*) origins in production; explicitly lock down origins to authorized frontend domains.
	• Input Validation: Always use @Valid and @NotNull/@Size on RequestBodies to block basic injection attacks before authentication logic evaluates.
	• Hide Error Stacktraces: Do not leak database exceptions or structural stack traces to endpoints. Use a custom @RestControllerAdvice to map internal exceptions to clean error payloads.

32. How Do You Store Passwords in an Application? (Hashing Best Practices)
Never store plain text or simple encrypted passwords. If a database is breached, reversible encryption keys or un-salted MD5 arrays are easily cracked via pre-computed rainbow tables.
Best Practices:
	• Use Cryptographic One-Way Hashing: Use adaptive hashing algorithms designed to slow down brute force attacks (like BCrypt, SCrypt, or Argon2).
	• Salting: Every password must be combined with a unique, cryptographically random sequence of bits (a salt) before being hashed. This ensures that two users with the exact same password string will end up with entirely different hash strings in your database.
Spring Security Implementation:
Spring manages this cleanly using the PasswordEncoder interface. The industry standard choice is BCrypt:
Java

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Automatically handles salting internally
    }
}
	• When saving a user: Inject PasswordEncoder and call passwordEncoder.encode(rawPassword). Store the resulting output string in the database.
	• When logging in: Spring Security automatically fetches the stored hash and compares it against the incoming login credentials using passwordEncoder.matches(rawPassword, encodedPassword).



==================================================================

Part 1: Transactions & @Transactional Internals
33. How does @Transactional work internally? Explain Propagation and Isolation levels.
Internal Mechanism (Spring AOP Proxies)
When Spring starts, it scans for classes or methods annotated with @Transactional. It creates a dynamic AOP Proxy around that bean.
	1. Intercepting the Call: When a client calls a transactional method, it doesn't call your actual bean directly; it calls the proxy.
	2. Opening the Connection: The proxy interacts with a PlatformTransactionManager (like DataSourceTransactionManager or JpaTransactionManager) to acquire a database connection and turn off auto-commit (connection.setAutoCommit(false)).
	3. Execution & Target Binding: The proxy binds the connection to the current execution thread using TransactionSynchronizationManager and then executes your actual target method.
	4. Commit or Rollback: * If the method executes successfully, the proxy invokes connection.commit().
		○ If a Runtime/Unchecked Exception (RuntimeException or Error) is thrown, the proxy calls connection.rollback().
		○ Warning: By default, Checked Exceptions (Exception) do not trigger a rollback unless explicitly configured via @Transactional(rollbackFor = Exception.class).
		○ Self-Invocation Pitfall: If method A calls method B within the same class, method B's @Transactional annotation is ignored because the call bypasses the AOP proxy completely.
Transaction Propagation Levels
Propagation defines how a transaction boundary behaves if another transaction context is already active when the method is called.
	• REQUIRED (Default): Joins the existing transaction if one exists; creates a new one if none exists.
	• REQUIRES_NEW: Suspends the current transaction, opens a completely independent new transaction, and resumes the outer transaction once the inner one completes.
	• NESTED: Creates a savepoint within the current transaction. If the nested transaction fails, it rolls back only to this savepoint, leaving the outer transaction intact to decide how to proceed.
	• MANDATORY: Requires an active existing transaction. Throws an exception if none is present.
	• NOT_SUPPORTED / NEVER: Executes non-transactionally; suspends an existing transaction or throws an exception if one exists.
Transaction Isolation Levels
Isolation levels define how visible data changes made by one concurrent transaction are to other concurrent transactions.
	• READ_UNCOMMITTED: Lowest isolation. A transaction can read uncommitted data from another transaction (Dirty Reads).
	• READ_COMMITTED: Prevents dirty reads. A transaction can only read data that has been committed. However, if it reads the same row twice, it might see different data if another transaction updates and commits in between (Non-Repeatable Reads).
	• REPEATABLE_READ: Guarantees that any data read remains identical throughout the transaction. Prevents non-repeatable reads, but if another transaction inserts new rows matching a query criteria, they will appear upon re-querying (Phantom Reads).
	• SERIALIZABLE: Highest isolation. Locks tables completely, executing transactions sequentially. Eliminates all anomalies but introduces significant performance bottlenecks.

Part 2: Exception Handling
34. How do you implement global exception handling in Spring Boot?
Global exception handling is best implemented using a centralized component annotated with @RestControllerAdvice. This decouples error mapping from your actual controller logic.
Java

@RestControllerAdvice
public class GlobalExceptionHandler {
// Handles specific domain exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
// Fallback handler for any uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now(),
            "An unexpected error occurred internal to the server.",
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
35. What is the purpose of @ControllerAdvice and @ExceptionHandler?
	• @ExceptionHandler: A method-level annotation. It declares that the marked method is responsible for handling specific exceptions thrown within a controller. Instead of wrapping your business logic in massive try-catch blocks, you let exceptions bubble up naturally, and Spring routes them directly here.
	• @ControllerAdvice: A class-level interceptor component. By default, an @ExceptionHandler inside a normal controller only handles errors for that specific class. @ControllerAdvice acts as an interceptor globally across all controllers in your application, providing unified formatting, response structures, and status codes across the entire API surface area.

Part 3: REST API Design Paradigms
36. What is the difference between PUT and PATCH in REST APIs?
	• PUT (Full Replacement / Idempotent): Used to update a resource by sending the entire updated payload representation. If your resource has 10 fields and you only submit 2, the missing 8 fields will either be overwritten with null or reset to default values. It is strictly idempotent; executing the identical PUT request 10 times results in the exact same resource state as executing it once.
	• PATCH (Partial Update / Non-Idempotent by default): Used to apply partial modifications to a resource. You send only the explicit fields you intend to change, leaving the rest untouched. Depending on the payload structure design (e.g., JSON Patch JSR-369 formats vs JSON Merge Patch), it can technically be non-idempotent if performing relative mutation actions (like appending to a collection array field).
37. Can we perform an update operation using HTTP POST? Explain.
Yes. HTTP methods are semantic guidelines rather than rigid technical enforcers.
	• Technical Viability: A POST request includes a request body and sends data to a processing URL endpoint. The underlying Spring controller and database do not care what the HTTP verb is; they will parse the body and run an UPDATE SQL statement regardless.
	• Why we do it sometimes: If your update mutations are highly complex structural operations that don't map naturally to direct resource modifications (e.g., /api/orders/123/calculate-discounts-and-reprocess), using POST to treat the command action as a pseudo-resource is common architecture practice.
	• Why we avoid it for standard updates: It breaks REST constraints. Unlike PUT, POST is explicitly marked as non-idempotent by web infrastructure specs. Gateways, proxies, and caches will never attempt to cache or automatically retry a failed POST network packet, whereas they might gracefully retry standard safe/idempotent configurations.
38. How do you design pagination and sorting in a REST API?
Best practice dictates passing pagination and sorting instructions as Query Parameters, avoiding body payloads on GET requests.
API Request Format
HTTP

GET /api/products?page=0&size=20&sort=price,desc&sort=name,asc
Spring Boot Implementation
Spring Data framework streamlines this via the Pageable abstraction parameter injected directly into your controller:
Java

@GetMapping("/products")
public ResponseEntity<Page<ProductDTO>> getProducts(Pageable pageable) {
    // Spring automatically parses the page, size, and sort query strings into this object
    Page<ProductDTO> products = productService.findAllProducts(pageable);
    return ResponseEntity.ok(products);
}
Inside your repository layer, you extend JpaRepository or PagingAndSortingRepository which natively handles appending the LIMIT, OFFSET, and ORDER BY syntax to your SQL queries under the hood:
Java

public Page<Product> findAll(Pageable pageable);

Part 4: API Strategy & Microservices
39. Why is an API contract important in microservices? How do teams collaborate using Swagger?
The Importance of an API Contract
In a distributed microservice mesh, teams run autonomous release cycles. If the Inventory Team changes a payload variable name without telling the Checkout Team, the checkout pipeline instantly shatters in production. An API Contract is a formalized, platform-agnostic blueprint (typically documented in OpenAPI specification YAML or JSON format) that precisely states:
	• Every active endpoint pathway.
	• Required authentication parameters.
	• Mandatory query strings and data structures.
	• The precise data types and schemas of response bodies and error models.
It serves as a legally binding interface agreement between services, guaranteeing that as long as a microservice adheres strictly to its contract schema, it can be refactored, rebuilt, or deployed without accidentally breaking upstream consumers.
Cross-Team Collaboration using Swagger / OpenAPI
Teams typically collaborate via two common design patterns using Swagger:
	1. Design-First Architecture (Recommended for Collaboration): Before writing a single line of Java code, both teams meet and write an OpenAPI specification YAML file inside a collaborative tool like SwaggerHub.
		○ Parallel Tracks: Once the contract YAML is finalized and merged, the frontend/consumer team can configure a mock server instantly based directly on that specification.
		○ Implementation: Simultaneously, the backend team uses tools like OpenAPI Generator to auto-scaffold their Spring Controllers and interfaces. Both teams develop concurrently with absolute predictability.
	2. Code-First Architecture: The backend team writes the functional Spring Boot code and decorates their endpoints with Swagger annotations (via the springdoc-openapi-ui dependency).
		○ When the app builds, Spring Boot dynamically evaluates these components and exposes a hosted JSON file and a visual /swagger-ui/index.html dashboard.
		○ Other engineering teams visit this URL to live-test endpoints with a built-in interactive client, explore nested structural models, and download the schema directly into their own UI codebases to auto-generate client-side API SDK integrations.



Part 1: ORM Frameworks & Entity Management
40. What is JPA? How is it different from Hibernate?
	• JPA (Jakarta Persistence API): A specification or standard interface. It defines a set of rules, interfaces, and annotations (like @Entity, @Table, @Id) for Object-Relational Mapping (ORM) in Java. It cannot execute database queries on its own.
	• Hibernate: A framework that implements the JPA specification. It provides the concrete underlying engine that compiles JPQL into native SQL, manages sessions, and communicates with JDBC drivers.
41. What is the role of EntityManager in JPA?
The EntityManager is the core interface used to interact with the Persistence Context (a first-level cache holding entity instances managed in a database transaction session). It provides methods to handle the CRUD lifecycle of entities (find(), persist(), merge(), remove()).
42. Difference between save(), persist(), merge(), and saveAndFlush()?
	• persist(entity): (JPA Standard) Makes a transient instance persistent. It does not immediately issue a SQL INSERT; it schedules it for commit time. It returns void.
	• save(entity): (Hibernate Native/Spring Data) Similar to persist(), but it returns the persisted entity object. If the entity has an ID, it checks if it needs to update or insert.
	• merge(entity): (JPA Standard) Copies the state of a detached entity into an active managed entity instance. If the entity doesn't exist in the context, it fetches it or creates a new one.
	• saveAndFlush(entity): (Spring Data JPA Special) Saves the entity and immediately forces Hibernate to flush all pending changes out to the database disk during the transaction, rather than waiting for the transaction boundary to close.
43. What are Hibernate Entity states?
  [ Transient ] ──( persist / save )──> [ Persistent ] ──( close / detach )──> [ Detached ]
                                               │                                   │
                                            ( remove )                           ( merge )
                                               ▼                                   ▼
                                           [ Removed ]                       [ Persistent ]

	• Transient: The object is created in Java memory (User u = new User()) but has no database identity (ID) and is not associated with a persistence context.
	• Persistent: The instance is actively managed by the persistence context. Changes made to this object's fields are automatically tracked and synchronized with the database upon commit (Dirty Checking).
	• Detached: The instance was persistent, but its session/context was closed, or it was manually detached via entityManager.detach(). Its state is no longer tracked.
	• Removed: The instance is marked for deletion using entityManager.remove(). The actual SQL DELETE executes when the context is flushed.

Part 2: Performance Tuning & Relationships
44. What is lazy loading vs eager loading? When can EAGER loading impact performance?
	• Lazy Loading (FetchType.LAZY): Delays fetching associated data from the database until it is explicitly accessed in the code (e.g., calling user.getOrders()). It creates a proxy object initially.
	• Eager Loading (FetchType.EAGER): Automatically fetches the target entity along with all its associated collections/entities immediately in a single query (often via SQL outer joins).
	• Performance Impact: Eager loading can decimate performance when fetching collections of entities (e.g., fetching 100 Users, each having an EAGER collection of Orders). It forces unnecessary massive data joins, bloats memory allocation, and easily triggers the N+1 select problem.
45. What is the N+1 problem? How do you solve it?
The N+1 problem occurs when an ORM framework executes 1 initial query to fetch a list of parent entities, and then executes N separate individual queries to fetch the child associations for each of those N parents.
	• Solutions: 1. JOIN FETCH (JPQL): Forces a single query with an explicit inner/outer join:
SELECT u FROM User u JOIN FETCH u.orders
2. Entity Graphs (@EntityGraph): Declarative template specifying which attributes to load eagerly for a specific repository method.
3. Batch Loading (@BatchSize): Instructs Hibernate to load collections in batches (e.g., 20 at a time) using an IN clause instead of 1 by 1.
46. Difference between 1st level cache and 2nd level cache in Hibernate?
	• 1st Level Cache: A Session-scoped cache. It is active by default and cannot be disabled. It ensures that within a single database transaction, fetching the same entity multiple times only hits the database once.
	• 2nd Level Cache: An optional, SessionFactory-scoped (application-wide) cache shared across multiple threads and sessions. It caches data across transactions using third-party providers like Ehcache or Redis.
47. Explain @OneToMany and @ManyToOne mapping with a practical example.
To prevent duplicate mapping tables, the relationship must have an owning side (which holds the foreign key column) and a referencing side (which maps back to the owner using mappedBy).
Java

@Entity
public class Company { // Referencing Side
    @Id @GeneratedValue private Long id;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
}
@Entity
public class Employee { // Owning Side (Holds foreign key: company_id)
    @Id @GeneratedValue private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
48. How do you implement composite primary keys in an entity?
Use the @IdClass or @EmbeddedId annotation.
Using @EmbeddedId:
Java

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
49. How to use Enums in an entity class?
Always use @Enumerated(EnumType.STRING). Avoid EnumType.ORDINAL because if you insert a new enum value anywhere except the end of the list, your existing database indices/mappings will corrupt silently.
Java

@Enumerated(EnumType.STRING)
private StatusType status; // Stores 'ACTIVE' instead of 0

Part 3: Querying, Spocs & Advanced Config
50. How to fetch only selected columns using JPQL instead of the entire entity?
Use JPQL Projections to return either an Object array or map values directly into a clean DTO object using a constructor expression:
Java

@Query("SELECT new com.example.UserDTO(u.id, u.email) FROM User u")
List<UserDTO> fetchUserDetails();
51. When should you use JPQL vs native SQL?
	• JPQL: Use for standard CRUD and domain operations. It is database-agnostic (Hibernate translates it based on the configured Dialect) and respects the entity lifecycle/cache states.
	• Native SQL: Use when utilizing database-specific performance optimization features (like Index hints, spatial extensions, recursive CTEs), or complex syntax not supported by standard JPQL parsing.
52. How do you call a Stored Procedure from JPA?
You use the @Procedure annotation inside your Spring Data Repository interface:
Java

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Procedure(procedureName = "calculate_bonus")
    void calculateUserBonus(@Param("userId") Long userId);
}
53. How do you write derived queries in Spring Data JPA?
Spring parses the method name structurally based on a pre-defined grammar.
Java

// Translates to: SELECT * FROM users WHERE email_address = ? AND status = 'ACTIVE'
List<User> findByEmailAddressAndStatus(String email, String status);

Part 4: Database Theory & Optimization
54. What is a Database View? Types: Materialized vs Non-materialized?
A View is a virtual table representing the output result of a pre-defined SQL query.
	• Non-materialized View: Does not store any actual data on the disk. When queried, it dynamically runs its underlying definition query against the physical tables on the fly.
	• Materialized View: Physically computes and stores the query output table directly to disk. It dramatically speeds up slow complex queries but requires maintenance because data must be updated via a refresh schedule when baseline physical tables change.
55. When should you create a View?
	• To simplify complex query definitions (hiding massive multi-table inner joins from developer applications).
	• To act as a Security layer (granting users access only to a View that screens out sensitive credit card or salary columns while exposing public profiles).
56. What is a Connection Pool? How do you decide the connection pool size?
A connection pool (like HikariCP) maintains a cache of active database connections. Reusing pre-established connections prevents the heavy network latency cost of opening and tearing down physical TCP connections on every HTTP request.
	• Sizing Formula: Size should be strictly optimized using the standard database connection sizing heuristic:
$$Connections = ((CoreCount \times 2) + EffectiveSpindleCount)$$
	• Note: Setting the pool size unnecessarily large slows down performance due to CPU context switching and disk-head contention bottlenecks.
57. What are ACID properties? Explain using a banking transaction example.
Consider transferring $100 from Account A to Account B:
	• Atomicity: The entire operation must succeed or completely fail together. If Account A is debited $100 but the system crashes before Account B is credited, the whole operation rolls back.
	• Consistency: The system moves from one valid state to another. The net global balance across both accounts must remain identical before and after execution.
	• Isolation: If two transfers happen at the same time, they cannot see each other's partial calculations. Transaction 2 cannot see Account A's reduced balance until Transaction 1 formally commits.
	• Durability: Once committed, the transaction is permanent. Even if a massive power failure hits the servers a millisecond later, the logs guarantee data updates remain on the disk storage.
58. How do you handle concurrent updates to the same database record?
	• Optimistic Locking: Assumes conflict is rare. Uses a @Version integer column on the entity class. If two transactions read version 5, and the first saves, the database version increments to 6. When the second transaction attempts to save, its version mismatch triggers an OptimisticLockException, and it is forced to retry.
	• Pessimistic Locking: Assumes conflict is common. Locks the database rows immediately upon selection (SELECT ... FOR UPDATE). No other connection can read or modify the record until the active lock transaction commits.
59. How do you optimize a slow-performing SQL query?
	1. Run EXPLAIN ANALYZE on the query string to map out performance bottlenecks (e.g., identifying full-table scans instead of index scans).
	2. Add missing targeted Indexes on columns used inside WHERE, JOIN, or ORDER BY clauses.
	3. Remove unnecessary wildcards (avoid SELECT *), querying only exact columns needed.
	4. Normalize structural bottlenecks or denormalize intentionally into Materialized views for reporting heavy operations.
60. What is Indexing? How does it improve performance? Types of indexes?
Indexing creates a pointer-based lookup map framework (typically structured as a B-Tree) over explicit table columns. It improves performance by changing scanning complexity from a linear search cost ($O(n)$) to a logarithmic search cost ($O(\log n)$).
	• Clustered Index: Physically dictates the actual storage layout order of rows on the disk (typically the Primary Key). Only one per table.
	• Non-Clustered Index: Maintains a separate physical lookup table mapping the column key values directly to the physical row pointer location.

Part 5: SQL Query Challenges
61. Difference between INNER JOIN and LEFT JOIN (with example)?
	• INNER JOIN: Returns records that have matching values in both tables.
	• LEFT JOIN: Returns all records from the left table, plus the matching records from the right table. If no match exists, NULL values are returned for the right table columns.
Example: A User table and an Orders table.
	• INNER JOIN returns only users who have made an order.
	• LEFT JOIN returns all users regardless of whether they have made an order.
62. Difference between WHERE and HAVING?
	• WHERE: Filters records before any aggregation operations execute. It operates on individual row items. It cannot contain aggregate functions (like SUM(), AVG()).
	• HAVING: Filters record groups after a GROUP BY clause completes execution. It operates on aggregated summaries.
63. Write a SQL query to find the second highest salary.
SQL

SELECT MAX(salary) 
FROM employee 
WHERE salary < (SELECT MAX(salary) FROM employee);
64. Write a SQL query using GROUP BY + HAVING.
Goal: Find departments with more than 5 employees.
SQL

SELECT department_id, COUNT(id) AS employee_count
FROM employee
GROUP BY department_id
HAVING COUNT(id) > 5;
65. Difference between DELETE, TRUNCATE, and DROP?
	• DELETE: (DML) Removes rows based on an optional WHERE clause. It triggers row-level triggers and writes detailed transaction logs for each deletion, allowing rollback. It is slower.
	• TRUNCATE: (DDL) Empties an entire table completely by de-allocating its physical storage data pages. It skips individual row triggers and writes minimal logs. It is extremely fast but cannot be undone easily.
	• DROP: (DDL) Completely deletes the table data, columns, structural indices, and permissions permanently from the schema architecture.

Part 6: Enterprise Configuration
66. How to configure multiple databases in a Spring Boot application?
To handle multiple databases, disable Spring Boot's automatic auto-configuration for data sources and manually declare distinct configuration properties, DataSources, EntityManagerFactories, and TransactionManagers.
Step 1: Define properties in application.properties
Properties

spring.datasource.primary.jdbc-url=jdbc:postgresql://localhost:5432/primary_db
spring.datasource.primary.username=postgres
spring.datasource.primary.password=secret
spring.datasource.secondary.jdbc-url=jdbc:mysql://localhost:3306/secondary_db
spring.datasource.secondary.username=root
spring.datasource.secondary.password=secret
Step 2: Build the Java Configuration Class for a Datasource
Create separate configuration classes for each database. Mark one explicitly as @Primary.
Java

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
    public DataSource dataSource(@Qualifier("primaryProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
@Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("primaryDataSource") DataSource dataSource) {
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
(You replicate this exact structure in a second class—omitting @Primary and pointing its parameters directly to the secondary packages and property configurations).

From <https://gemini.google.com/glic?hl=en-US> 


