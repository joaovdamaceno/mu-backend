package br.unioeste.mu.mu_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=0123456789abcdef0123456789abcdef")
class MuBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
