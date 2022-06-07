package org.egov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

@Configuration
public class TestingDataSourceConfig {

//    @Bean
//    @Primary
//    public DataSource dataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .generateUniqueName(true)
//                .setType(H2)
//                .setScriptEncoding("UTF-8")
//                .ignoreFailedDrops(true)
//                .addScript("db/migration/main/V20220715173000__create_quartz.sql")
//                .addScripts("db/migration/main/V20220715203000__create_pspcl_ifix.sql", "db/migration/main/V20220715253000__index_pspcl_ifix.sql")
//                .build();
//    }
}
