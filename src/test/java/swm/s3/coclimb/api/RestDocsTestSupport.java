package swm.s3.coclimb.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import swm.s3.coclimb.config.security.JwtManager;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
public abstract class RestDocsTestSupport extends IntegrationTestSupport{
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected JwtManager jwtManager;

    @AfterEach
    void clearDB() {
        gymLikeJpaRepository.deleteAllInBatch();
        mediaJpaRepository.deleteAllInBatch();
        gymJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }
}
