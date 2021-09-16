package org.egov.ifix.aggregate.config;

import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.DruidConfiguration;
import in.zapr.druid.druidry.client.DruidJerseyClient;
import in.zapr.druid.druidry.client.DruidQueryProtocol;
import in.zapr.druid.druidry.client.exception.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class FiscalEventConfiguration {

    @Autowired
    private ConfigProperties configProperties;

    @Bean
    public DruidClient getDruidClient() {
        DruidConfiguration config = DruidConfiguration
                .builder()
                .protocol(DruidQueryProtocol.HTTPS)
                .host(configProperties.getDruidHost())
                .endpoint(configProperties.getDruidEndPoint())
                .build();

        DruidClient client = new DruidJerseyClient(config);
        try {
            System.out.println("Druid Client : " + client);
            client.connect();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        //List<DruidResponse> responses = client.query(query, DruidResponse.class);
//		try {
//			client.close();
//		} catch (ConnectionException e) {
//			e.printStackTrace();
//		}
        return client;
    }

    public DataSource getDS() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(configProperties.getPostgresDatasourceDriverClassName());
        dataSource.setUrl(configProperties.getPostgresDatasourceUrl());
        dataSource.setUsername(configProperties.getPostgresDatasourceUsername());
        dataSource.setPassword(configProperties.getPostgresDatasourcePassword());
        return dataSource;
    }


    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDS());
    }
}
