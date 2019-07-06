package org.byron4j.cookbook.mocketio.basic;

import org.byron4j.cookbook.springMVC.root.repo.JdbcTemplateDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ResttemplateTest {
    @Mock
    private RestTemplate restTemplate;

    @Autowired
    JdbcTemplateDao jdbcTemplateDao;

    @Test
    public void givenMockingIsDoneByMockito_whenGetIsCalled_shouldReturnMockedObject() {
        ResponseEntity<String> obj = restTemplate.postForEntity("http://localhost:8080/hello?name=111", null, String.class);
        System.out.println(obj.getStatusCode());
        System.out.println(obj.getHeaders());
        System.out.println(obj.getBody());
        System.out.println("==================over=============");
    }
}
