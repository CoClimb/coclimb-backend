package swm.s3.coclimb.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserJpaRepository;
import swm.s3.coclimb.config.security.JwtManager;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
public abstract class RestDocsTestSupport {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected JwtManager jwtManager;
    @Autowired
    protected UserJpaRepository userJpaRepository;
    @MockBean
    protected InstagramRestApiManager instagramRestApiManager;
}
