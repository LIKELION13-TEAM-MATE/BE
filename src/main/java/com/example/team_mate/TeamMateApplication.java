package com.example.team_mate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TeamMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamMateApplication.class, args);
	}

}
