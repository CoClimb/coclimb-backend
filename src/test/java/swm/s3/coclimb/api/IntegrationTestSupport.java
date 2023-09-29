package swm.s3.coclimb.api;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import swm.s3.coclimb.api.adapter.out.elasticsearch.ElasticsearchClientManager;
import swm.s3.coclimb.api.adapter.out.elasticsearch.GymElasticsearchQuery;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApi;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gymlike.GymLikeJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.media.MediaJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.media.MediaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.report.ReportJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.report.ReportRepository;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserRepository;
import swm.s3.coclimb.api.application.service.*;
import swm.s3.coclimb.config.AppConfig;
import swm.s3.coclimb.config.ServerClock;
import swm.s3.coclimb.config.security.JwtManager;
import swm.s3.coclimb.docker.DockerComposeRunner;

import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public abstract class IntegrationTestSupport{
    static DockerComposeRunner dockerRunner = new DockerComposeRunner();
    @BeforeAll
    static void setUpContainer() {
        dockerRunner.runTestContainers();
    }

    // User
    @Autowired protected UserService userService;
    @Autowired protected UserJpaRepository userJpaRepository;
    @Autowired protected UserRepository userRepository;

    // Gym
    @Autowired protected GymService gymService;
    @Autowired protected GymJpaRepository gymJpaRepository;
    @Autowired protected GymRepository gymRepository;
    @Autowired protected GymLikeJpaRepository gymLikeJpaRepository;

    // Media
    @Autowired protected MediaService mediaService;
    @Autowired protected MediaJpaRepository mediaJpaRepository;
    @Autowired protected MediaRepository mediaRepository;

    // Login
    @Autowired protected LoginService loginService;

    // Report
    @Autowired protected ReportService reportService;
    @Autowired protected ReportJpaRepository reportJpaRepository;
    @Autowired protected ReportRepository reportRepository;

    // Config
    @Autowired protected AppConfig appConfig;
    @Autowired protected ServerClock serverClock;

    // Login
    @Autowired protected JwtManager jwtManager;

    // Instagram
    @Autowired protected InstagramOAuthRecord instagramOAuthRecord;
    @Autowired protected InstagramRestApiManager instagramRestApiManager;
    @Autowired protected InstagramRestApi instagramRestApi;

    // elasticsearch
    @Autowired protected ElasticsearchClient esClient;
    @Autowired protected ElasticsearchClientManager elasticsearchClientManager;
    @Autowired protected GymElasticsearchQuery gymElasticsearchQuery;

    @BeforeEach
    void setUp() throws Exception{
        Reader input = new StringReader(Files.readString(Path.of("src/test/resources/docker/elastic/gyms.json")));
        esClient.indices().create(c -> c
                .index("gyms")
                .withJson(input));
        esClient.indices().refresh();
    }

    @AfterEach
    void clearDB() throws Exception {
        reportJpaRepository.deleteAllInBatch();
        gymLikeJpaRepository.deleteAllInBatch();
        mediaJpaRepository.deleteAllInBatch();
        gymJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
        esClient.indices().delete(d -> d.index("gyms"));
        esClient.indices().refresh();
    }

}
