package swm.s3.coclimb.api;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import swm.s3.coclimb.api.adapter.out.elasticsearch.ElasticsearchClientManager;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApi;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gymlike.GymLikeJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.media.MediaJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.media.MediaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserRepository;
import swm.s3.coclimb.api.application.service.GymService;
import swm.s3.coclimb.api.application.service.LoginService;
import swm.s3.coclimb.api.application.service.MediaService;
import swm.s3.coclimb.api.application.service.UserService;
import swm.s3.coclimb.config.AppConfig;
import swm.s3.coclimb.config.ServerClock;
import swm.s3.coclimb.config.security.JwtManager;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestSupport extends TestContainerSupport{
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
    protected ElasticsearchClientManager elasticsearchClientManager = new ElasticsearchClientManager(getTestEsClient());

    @AfterEach
    void clearDB() {
        gymLikeJpaRepository.deleteAllInBatch();
        mediaJpaRepository.deleteAllInBatch();
        gymJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }



}
