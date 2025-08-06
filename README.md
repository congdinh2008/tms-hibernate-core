# Task Management System (TMS) - Spring IoC Implementation

## Tổng quan

Dự án Task Management System đã được migrate từ Singleton pattern sang Spring Core IoC container để cải thiện kiến trúc và khả năng mở rộng.

## Kiến trúc Spring IoC

### Configuration Classes

#### ApplicationConfig.java
Root configuration class kết hợp tất cả cấu hình Spring:
```java
@Configuration
@ComponentScan(basePackages = "com.congdinh2008.tms")
@Import({DataSourceConfig.class, HibernateConfig.class})
public class ApplicationConfig {
    // Root Spring configuration
}
```

#### DataSourceConfig.java
Cấu hình HikariCP DataSource bean:
```java
@Configuration
@PropertySource("classpath:hibernate.properties")
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        // HikariCP configuration với property injection
    }
}
```

#### HibernateConfig.java
Cấu hình Hibernate SessionFactory bean:
```java
@Configuration
public class HibernateConfig {
    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        // Spring ORM LocalSessionFactoryBuilder
    }
}
```

### Service Layer

#### HibernateService.java
Service layer với dependency injection:
```java
@Service
public class HibernateService {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public HibernateService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @PostConstruct
    public void init() {
        // Bean initialization
    }
    
    @PreDestroy
    public void cleanup() {
        // Resource cleanup
    }
}
```

## Dependencies

### Spring Framework 6.2.1
```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.2.1</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>6.2.1</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>6.2.1</version>
</dependency>
```

### Jakarta Annotations API 2.1.1
```xml
<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
    <version>2.1.1</version>
</dependency>
```

## Usage Example

### Application.java
```java
public class Application {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = 
                 new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            
            HibernateService hibernateService = context.getBean(HibernateService.class);
            
            // Sử dụng HibernateService với dependency injection
            hibernateService.logConnectionPoolStats();
        }
    }
}
```

## Benefits của Spring IoC

### 1. Dependency Injection
- Tự động inject SessionFactory vào HibernateService
- Constructor injection đảm bảo immutable dependencies
- Loại bỏ việc quản lý singleton manually

### 2. Lifecycle Management
- Spring quản lý bean lifecycle tự động
- @PostConstruct để khởi tạo resources
- @PreDestroy để cleanup resources

### 3. Configuration Management
- Centralized configuration với @Configuration classes
- Property injection từ hibernate.properties
- Easy to test và mock dependencies

### 4. Testability
- Dễ dàng inject mock objects trong unit tests
- Spring Test framework support
- Isolated testing cho từng component

## Testing

### SpringConfigurationTest.java
Comprehensive test cho Spring IoC configuration:
- Test Spring context loading
- Test bean dependencies
- Test singleton behavior
- Test bean lifecycle

### ApplicationTest.java
Integration test cho application:
- Test Spring IoC integration
- Test application lifecycle
- Test dependency injection chain

## Build và Run

### Compile
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Run Application
```bash
mvn exec:java -Dexec.mainClass="com.congdinh2008.tms.Application"
```

## Migration từ Singleton

### Before (Singleton Pattern)
```java
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }
}
```

### After (Spring IoC)
```java
@Service
public class HibernateService {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public HibernateService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
```

## Next Steps

### Potential Enhancements
1. **Spring Boot**: Auto-configuration và embedded server
2. **Spring Data JPA**: Repository pattern với Spring Data
3. **Spring Transaction**: Declarative transaction management
4. **Spring Security**: Authentication và authorization
5. **Spring Web MVC**: RESTful API development

### Database Schema
Chuẩn bị implement entity classes và database schema theo yêu cầu TMS project.

---

**Version**: 2.0.0 (Spring IoC Migration)  
**Author**: Cong Dinh  
**Date**: January 2025
