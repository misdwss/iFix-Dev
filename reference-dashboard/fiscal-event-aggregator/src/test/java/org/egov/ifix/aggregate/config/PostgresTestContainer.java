package org.egov.ifix.aggregate.config;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {

//    public static final String IMAGE_VERSION = "postgres:11.1";
//    public static final String DATABASE_NAME = "test";
//    public static PostgresTestContainer container;
//
//    public PostgresTestContainer() {
//        super(IMAGE_VERSION);
//    }
//
//    public static PostgresTestContainer getInstance() {
//        if (container == null) {
//            container = new PostgresTestContainer().withDatabaseName(DATABASE_NAME);
//        }
//        return container;
//    }
//
//    @Override
//    public void start() {
//        super.start();
//        System.setProperty("spring.datasource.url", container.getJdbcUrl());
//        System.setProperty("spring.datasource.username", container.getUsername());
//        System.setProperty("spring.datasource.password", container.getPassword());
//    }
//
////    @DynamicPropertySource
////    static void properties(DynamicPropertyRegistry registry) {
////        registry.add("spring.datasource.url", container::getJdbcUrl);
////        registry.add("spring.datasource.password", container::getPassword);
////        registry.add("spring.datasource.username", container::getUsername);
////    }
//
//    @Override
//    public void stop() {
//    }
}
