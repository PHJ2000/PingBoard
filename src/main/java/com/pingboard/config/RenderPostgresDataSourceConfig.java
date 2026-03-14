package com.pingboard.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

@Configuration
@Profile("postgres")
public class RenderPostgresDataSourceConfig {

    @Bean
    DataSource dataSource(org.springframework.core.env.Environment environment) {
        String rawUrl = environment.getProperty("spring.datasource.url");
        String explicitUsername = System.getenv("SPRING_DATASOURCE_USERNAME");
        String explicitPassword = System.getenv("SPRING_DATASOURCE_PASSWORD");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        String driverClassName = environment.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);

        if (rawUrl != null && rawUrl.startsWith("postgresql://")) {
            ParsedPostgresUrl parsed = ParsedPostgresUrl.parse(rawUrl);
            dataSource.setJdbcUrl(parsed.jdbcUrl());
            dataSource.setUsername(StringUtils.hasText(explicitUsername) ? explicitUsername : parsed.username());
            dataSource.setPassword(StringUtils.hasText(explicitPassword) ? explicitPassword : parsed.password());
            return dataSource;
        }

        dataSource.setJdbcUrl(rawUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    record ParsedPostgresUrl(String jdbcUrl, String username, String password) {
        static ParsedPostgresUrl parse(String rawUrl) {
            try {
                URI uri = new URI(rawUrl);
                String[] credentials = uri.getUserInfo().split(":", 2);
                String username = credentials[0];
                String password = credentials.length > 1 ? credentials[1] : "";
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();
                return new ParsedPostgresUrl(jdbcUrl, username, password);
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Invalid PostgreSQL connection string", ex);
            }
        }
    }
}
