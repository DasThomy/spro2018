package de.sopro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sopro.model.Combination;
import de.sopro.model.CompatibilityDegree;
import de.sopro.model.Format;
import de.sopro.repository.FormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;


@SpringBootApplication
public class SpringBootWebApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    ExampleDatabase exampleModel;

    @Autowired
    FormatRepository formatRepository;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootWebApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {

        exampleModel.generate();

    }

}
