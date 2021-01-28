package com.cursodesousa.libraryapi;

import com.cursodesousa.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

    @Autowired
    private EmailService emailService;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner runner(){
        return  args -> {
          List<String> emails = Arrays.asList("1b2a18e9c1-96f73e@inbox.mailtrap.io");
          emailService.sendMails("Testando serviço de emails.", emails);
          System.out.println("OK - enviado");
        };
    }

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
