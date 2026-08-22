package com.homework4.workapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.elasticsearch.post-search.ensure-index-on-startup=false")
class WorkapiApplicationTests {

	@Test
	void contextLoads() {
	}

}
