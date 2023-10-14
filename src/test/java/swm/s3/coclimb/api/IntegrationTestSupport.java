package swm.s3.coclimb.api;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.sts.model.Credentials;
import swm.s3.coclimb.api.adapter.out.aws.AwsS3Manager;
import swm.s3.coclimb.api.adapter.out.aws.AwsSTSManager;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymDocumentRepository;
import swm.s3.coclimb.api.adapter.out.filedownload.FileDownloader;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.InstagramRestApi;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gymlike.GymLikeJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gymlike.GymLikeRepository;
import swm.s3.coclimb.api.adapter.out.persistence.media.MediaJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.media.MediaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.report.ReportJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.report.ReportRepository;
import swm.s3.coclimb.api.adapter.out.persistence.search.SearchManager;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserDocumentRepository;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserJpaRepository;
import swm.s3.coclimb.api.adapter.out.persistence.user.UserRepository;
import swm.s3.coclimb.api.application.service.*;
import swm.s3.coclimb.api.exception.errortype.aws.S3UploadFail;
import swm.s3.coclimb.config.AppConfig;
import swm.s3.coclimb.config.ServerClock;
import swm.s3.coclimb.config.security.JwtManager;
import swm.s3.coclimb.docker.DockerComposeRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public abstract class IntegrationTestSupport {
    static DockerComposeRunner dockerRunner = new DockerComposeRunner();

    @BeforeAll
    static void setUpContainer() {
        dockerRunner.runTestContainers();
    }

    // User
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserJpaRepository userJpaRepository;
    @Autowired
    protected UserRepository userRepository;

    // Gym
    @Autowired
    protected GymService gymService;
    @Autowired
    protected GymJpaRepository gymJpaRepository;
    @Autowired
    protected GymRepository gymRepository;

    // GymLike
    @Autowired
    protected GymLikeRepository gymLikeRepository;
    @Autowired
    protected GymLikeJpaRepository gymLikeJpaRepository;

    // Media
    @Autowired
    protected MediaService mediaService;
    @Autowired
    protected MediaJpaRepository mediaJpaRepository;
    @Autowired
    protected MediaRepository mediaRepository;

    // Login
    @Autowired
    protected LoginService loginService;

    // Report
    @Autowired
    protected ReportService reportService;
    @Autowired
    protected ReportJpaRepository reportJpaRepository;
    @Autowired
    protected ReportRepository reportRepository;

    // Config
    @Autowired
    protected AppConfig appConfig;
    @Autowired
    protected ServerClock serverClock;

    // Login
    @Autowired
    protected JwtManager jwtManager;

    // Instagram
    @Autowired
    protected InstagramOAuthRecord instagramOAuthRecord;
    @Autowired
    protected InstagramRestApiManager instagramRestApiManager;
    @Autowired
    protected InstagramRestApi instagramRestApi;

    // Aws
    @Autowired
    protected AwsS3Manager awsS3Manager;
    @Autowired
    protected FileDownloader fileDownloader;
    @Autowired
    protected AwsSTSManager awsSTSManager;

    // elasticsearch
    @Autowired
    protected ElasticsearchClient esClient;
    @Autowired
    protected GymDocumentRepository gymDocumentRepository;
    @Autowired
    protected UserDocumentRepository userDocumentRepository;

    // Search
    @Autowired
    protected SearchManager searchManager;
    @Autowired
    protected SearchService searchService;

    @BeforeEach
    void setUp() throws Exception {
        esClient.indices().delete(d -> d.index("gyms"));
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
        gymDocumentRepository.deleteAll();
        userDocumentRepository.deleteAll();
    }
    protected List<String> readFileToList(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    protected void uploadToSpecificResource(String bucket, String key, Credentials credentials) {

        BasicSessionCredentials awsCredentials = new BasicSessionCredentials(credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken());
        AmazonS3Client amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion("ap-northeast-2")
                .build();

        try {
            amazonS3Client.putObject(bucket, key, "test");
        } catch (Exception e) {
            e.printStackTrace();
            throw new S3UploadFail();
        }
    }
}
