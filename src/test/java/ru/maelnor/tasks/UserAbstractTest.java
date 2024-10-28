package ru.maelnor.tasks;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.maelnor.tasks.entity.RoleType;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.repository.JpaTaskRepository;
import ru.maelnor.tasks.repository.JpaUserRepository;
import ru.maelnor.tasks.security.SecurityService;
import ru.maelnor.tasks.service.TaskService;

import java.util.HashSet;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
// Без очистки контекста между тестами, redis помирает
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserAbstractTest {
    protected UserEntity user;
    protected UserEntity manager;
    protected UserEntity admin;

    @Container
    protected static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    protected static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:7.4.1"))
                    .withExposedPorts(6379);

    @Container
    protected static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1")).withExposedPorts(9093);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JpaUserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected SecurityService securityService;

    @BeforeEach
    @Commit
    void setUp() {
        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("user")
                .password(passwordEncoder.encode("user_password"))
                .email("user@example.com")
                .build();

        user.setRoles(new HashSet<>() {{
            add(RoleType.ROLE_USER);
        }});
        userRepository.save(user);

        manager = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("manager")
                .password(passwordEncoder.encode("manager_password"))
                .email("manager@example.com")
                .build();

        manager.setRoles(new HashSet<>() {{
            add(RoleType.ROLE_USER);
            add(RoleType.ROLE_MANAGER);
        }});

        userRepository.save(manager);

        admin = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password(passwordEncoder.encode("admin_password"))
                .email("admin@example.com")
                .build();

        admin.setRoles(new HashSet<>() {{
            add(RoleType.ROLE_USER);
            add(RoleType.ROLE_ADMIN);
        }});
        userRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @BeforeAll
    public static void startContainer() {
        redisContainer.start();
    }

    @AfterAll
    public static void stopContainer() {
        redisContainer.stop();
    }
}
