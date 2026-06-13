package co.ke.xently.demo.books;

import co.ke.xently.log.mask.Mask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

record AuthResultRecord(String token) {
}

@AllArgsConstructor
@ToString
@Getter
final class AuthResult {
    private final String token;
}

@AllArgsConstructor
@Getter
final class AuthResultAnnotation {
    @Mask
    private final String token;

    @Override
    public String toString() {
        return getToken();
    }
}

@Slf4j
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ExtendWith({OutputCaptureExtension.class})
class ApplicationTests {

    @Test
    void contextLoads(CapturedOutput output) {
        // language=JSON
        var response = """
                {
                  "access_token": "eyJleHAiOjE3NzcyMDUzODgsImlhdCI6MTc3NzIwNTA4",
                  "expires_in": 300,
                  "refresh_expires_in": 1800,
                  "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2l",
                  "token_type": "Bearer",
                  "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6",
                  "not-before-policy": 0,
                  "session_state": "ta15irl9lXT7Vx3K96i-TLgi",
                  "scope": "openid"
                }""";
        log.info("Test custom regex for my auth result record: {} => {}", new AuthResultRecord("mytoken"), response);
        log.info("Test custom regex for my auth result regular class: {} => {}", new AuthResult("mytoken"), response);
        log.info("Test custom regex for my auth result annotation: {} => {}", new AuthResultAnnotation("mytoken"), response);

        assertThat(
                output.getOut(),
                allOf(
                        containsString("""
                                Test custom regex for my auth result record: AuthResultRecord[token=m********] => {
                                  "access_token": "e********",
                                  "expires_in": 300,
                                  "refresh_expires_in": 1800,
                                  "refresh_token": "e********",
                                  "token_type": "B********",
                                  "id_token": "e********",
                                  "not-before-policy": 0,
                                  "session_state": "ta15irl9lXT7Vx3K96i-TLgi",
                                  "scope": "openid"
                                }"""),
                        containsString("""
                                Test custom regex for my auth result regular class: AuthResult(token=m********) => {
                                  "access_token": "e********",
                                  "expires_in": 300,
                                  "refresh_expires_in": 1800,
                                  "refresh_token": "e********",
                                  "token_type": "B********",
                                  "id_token": "e********",
                                  "not-before-policy": 0,
                                  "session_state": "ta15irl9lXT7Vx3K96i-TLgi",
                                  "scope": "openid"
                                }"""),
                        containsString("""
                                Test custom regex for my auth result annotation: m******** => {
                                  "access_token": "e********",
                                  "expires_in": 300,
                                  "refresh_expires_in": 1800,
                                  "refresh_token": "e********",
                                  "token_type": "B********",
                                  "id_token": "e********",
                                  "not-before-policy": 0,
                                  "session_state": "ta15irl9lXT7Vx3K96i-TLgi",
                                  "scope": "openid"
                                }""")
                )
        );
    }

}
