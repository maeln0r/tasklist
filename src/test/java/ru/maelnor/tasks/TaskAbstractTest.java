package ru.maelnor.tasks;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ru.maelnor.tasks.service.TaskService;

import java.util.HashSet;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
// Без очистки контекста между тестами, redis помирает
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TaskAbstractTest {
    protected UserEntity user;
    protected UserEntity manager;
    protected UserEntity admin;
    protected TaskEntity task;
    protected TaskEntity adminTask;

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
    protected JpaTaskRepository taskRepository;

    @Autowired
    protected JpaUserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected TaskService taskService;

    @BeforeEach
    @Commit
    void setUp() {
        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("user")
                .password(passwordEncoder.encode("user"))
                .email("user")
                .build();

        user.setRoles(new HashSet<>() {{
            add(RoleType.ROLE_USER);
        }});

        userRepository.save(user);

        manager = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("manager")
                .password(passwordEncoder.encode("manager"))
                .email("manager")
                .build();

        manager.setRoles(new HashSet<>() {{
            add(RoleType.ROLE_USER);
            add(RoleType.ROLE_MANAGER);
        }});

        userRepository.save(manager);

        admin = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .email("admin")
                .build();

        admin.setRoles(new HashSet<>() {{
            add(RoleType.ROLE_USER);
            add(RoleType.ROLE_ADMIN);
        }});

        userRepository.save(admin);

        task = new TaskEntity();
        task.setId(UUID.randomUUID());
        task.setName("Test Task");
        task.setCompleted(false);
        task.setDescription("Test Description");
        task.setOwner(user);
        task = taskRepository.save(task);

        adminTask = new TaskEntity();
        adminTask.setId(UUID.randomUUID());
        adminTask.setName("Admin Test Task");
        adminTask.setCompleted(false);
        adminTask.setDescription("Admin Test Description");
        adminTask.setOwner(admin);
        adminTask = taskRepository.save(adminTask);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }
}
