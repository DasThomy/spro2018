package de.sopro.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

//	@Autowired
//	MockMvc mvc;
//
//	// Note that this value is overridden via injection from
//	// file application.properties
//	@Value("${home.welcome}")
//	String message;
//
//	@Test
//	public void getWelcomePage() throws Exception {
//
//		// Check if home page can be retrieved and contains
//		// the expected message
//
//		ResultMatcher model = MockMvcResultMatchers.model()
//			.attribute("message", message);
//
//		mvc.perform(get("/"))
//			.andExpect(status().isOk())
//			.andExpect(model);
//	}

    @Test
    public void testEmpty() {

    }

}
