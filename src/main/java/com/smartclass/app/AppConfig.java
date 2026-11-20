package com.smartclass.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.ZoneId;

@Configuration
public class AppConfig {
    static String setting(String name, String fallback) {
        String property = System.getProperty(name);
        if (property != null && !property.isBlank()) return property;
        String environment = System.getenv(name);
        return environment == null || environment.isBlank() ? fallback : environment;
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(setting("DB_URL", "jdbc:mysql://127.0.0.1:3306/smart_classroom_finder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"));
        config.setUsername(setting("DB_USER", "root"));
        config.setPassword(setting("DB_PASSWORD", ""));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(8);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10_000);
        config.setPoolName("SmartClassPool");
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ZoneId appZone() {
        return ZoneId.of(setting("APP_TIMEZONE", "Asia/Kolkata"));
    }

    @Bean
    public SmartClassService smartClassService(JdbcTemplate jdbc, DataSource dataSource,
                                                ObjectMapper mapper, ZoneId appZone) {
        return new SmartClassService(jdbc, dataSource, mapper, appZone);
    }
}
