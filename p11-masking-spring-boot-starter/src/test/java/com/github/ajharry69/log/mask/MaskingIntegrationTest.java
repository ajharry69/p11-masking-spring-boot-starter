package com.github.ajharry69.log.mask;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(properties = {
        "p11.masking.enabled=true",
        "p11.masking.fields[0]=email",
        "p11.masking.fields[1]=phoneNumber",
        "p11.masking.mask-style=PARTIAL",
        "p11.masking.mask-character=*"
})
class MaskingIntegrationTest {

    private record TestDto(String title, String author, String email, String phoneNumber) {}

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldMaskSensitiveFieldsInJson() {
        var dto = new TestDto("Title", "Author", "test@test.com", "1234567890");

        var json = objectMapper.writeValueAsString(dto);

        assertThat(json, containsString("""
                "email":"t***@test.com\""""));
        assertThat(json, containsString("""
                "title":"Title\""""));
    }
}