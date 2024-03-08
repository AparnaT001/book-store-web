package com.demo.bookstore.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManager", transactionManagerRef = "transactionManager", basePackages = "com.demo.bookstore.repository")
public class DataSourceConfig {

	public static final Integer DB_POOL_MAX_CONNECTIONS = 2;
	public static final Integer DB_POOL_MIN_IDLE_CONNECTIONS = 2;
	public static final Long DB_POOL_MIN_IDLE_TIMEOUT_MILLIS = 30000L;
	public static final Long DB_POOL_MAX_LIFETIME_MILLIS = 30000L;
	public static final String DB_POOL_NAME = "PrimaryPool";
	
    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;
    
	@Bean(name = "dataSource")
	public DataSource dataDBDataSource() {

		HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create()
				.url(url)
				.username(username)
				.password(password).build();
		dataSource.setMaximumPoolSize(DB_POOL_MAX_CONNECTIONS);
		dataSource.setMinimumIdle(DB_POOL_MIN_IDLE_CONNECTIONS);
		dataSource.setIdleTimeout(DB_POOL_MIN_IDLE_TIMEOUT_MILLIS);
		dataSource.setMaxLifetime(DB_POOL_MAX_LIFETIME_MILLIS);
		dataSource.setPoolName(DB_POOL_NAME);

		return dataSource;
	}

	@Bean(name = "entityManager")
	public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource") DataSource dataSource) {

		return builder.dataSource(dataSource).packages("com.demo.bookstore.entity").build();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager masterTransactionManager(
			@Qualifier("entityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
