package com.pht;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pht.dto.RoleCode;
import com.pht.entity.UserEntity;
import com.pht.repo.UserRepo;

@SpringBootApplication
public class JavaMidtermApplication {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(JavaMidtermApplication.class);
		Connection connection = null;
		Statement statement = null;
		try {
			logger.debug("Creating database if not exist...");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "Truong2209@");
			statement = connection.createStatement();
			statement.executeQuery(
					"SELECT count(*) FROM pg_database WHERE datname = 'java_midterm'");
			ResultSet resultSet = statement.getResultSet();
			resultSet.next();
			int count = resultSet.getInt(1);

			if (count <= 0) {
				statement.executeUpdate("CREATE DATABASE java_midterm");
				logger.debug("Database created.");
			} else {
				logger.debug("Database already exist.");
			}
		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {
			try {
				if (statement != null) {
					statement.close();
					logger.debug("Closed Statement.");
				}
				if (connection != null) {
					logger.debug("Closed Connection.");
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
		}
		SpringApplication.run(JavaMidtermApplication.class, args);
	}
	
	@Bean
	CommandLineRunner commandLineRunner(PasswordEncoder encoder, UserRepo repo){
		return args -> {
			long countUser = repo.count();
			if(countUser == 0) {
				var admin = UserEntity.builder()
						.username("admin")
						.password(encoder.encode("12345678"))
						.address("Address 1")
						.phoneNumber("737345346")
						.role(RoleCode.ADMIN)
						.build();
				
				var user = UserEntity.builder()
						.username("user")
						.password(encoder.encode("12345678"))
						.address("Address 2")
						.phoneNumber("737345346")
						.role(RoleCode.USER)
						.build();
				
				var users = List.of(admin, user);
				repo.saveAll(users);
			}
		};
	}

}
