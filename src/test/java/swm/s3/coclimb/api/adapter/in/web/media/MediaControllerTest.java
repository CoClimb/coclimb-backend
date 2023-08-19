package swm.s3.coclimb.api.adapter.in.web.media;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import swm.s3.coclimb.api.ControllerTestSupport;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateProblemInfo;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateRequest;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.User;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MediaControllerTest extends ControllerTestSupport {
    @Autowired
    MediaController mediaController;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController)
                .setCustomArgumentResolvers(loginUserArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("미디어를 등록할 수 있다.")
    void createMedia() throws Exception {
        //given
        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(User.builder().instagramUserInfo(InstagramUserInfo.builder().build()).build());
        given(loginUserArgumentResolver.supportsParameter(any())).willReturn(true);
        willDoNothing().given(mediaCommand).createMedia(any());

        MediaCreateRequest request = MediaCreateRequest.builder()
                .mediaType("VIDEO")
                .mediaUrl("mediaUrl")
                .platform("platform")
                .thumbnailUrl("thumbnailUrl")
                .problem(MediaCreateProblemInfo.builder().gymName("gym").build())
                .build();

        //when
        //then
        mockMvc.perform(post("/medias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("전체 미디어를 페이징 조회할 수 있다.")
    void getPagedMedias() throws Exception {
        //given
        int pageSize = 5;

        Page<Media> page = new PageImpl<>(IntStream.range(0, pageSize).mapToObj(i -> Media.builder()
                .mediaProblemInfo(MediaProblemInfo.builder().gymName("암장" + String.valueOf(i)).build())
                .build())
                .collect(Collectors.toList()));

        given(mediaQuery.getPagedMedias(any())).willReturn(page);

        //when
        //then
        mockMvc.perform(get("/medias").param("page", "0").param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(pageSize))
                .andExpect(jsonPath("$.medias[0].gymName").value("암장0"))
                .andExpect(jsonPath("$.medias[1].gymName").value("암장1"))
                .andExpect(jsonPath("$.medias[2].gymName").value("암장2"))
                .andExpect(jsonPath("$.medias[3].gymName").value("암장3"))
                .andExpect(jsonPath("$.medias[4].gymName").value("암장4"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(pageSize))
                .andExpect(jsonPath("$.totalPage").value(1));
    }

    @Test
    @DisplayName("내 미디어를 페이징 조회할 수 있다.")
    void getPagedMediasByUserId() throws Exception {
        //given
        int pageSize = 5;

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(User.builder().build());
        given(loginUserArgumentResolver.supportsParameter(any())).willReturn(true);

        Page<Media> page = new PageImpl<>(IntStream.range(0, pageSize).mapToObj(i -> Media.builder()
                        .mediaProblemInfo(MediaProblemInfo.builder().gymName("암장" + String.valueOf(i)).build())
                        .build())
                .collect(Collectors.toList()));

        given(mediaQuery.getPagedMediasByUserId(any(), any())).willReturn(page);

        //when
        //then
        mockMvc.perform(get("/medias/me").param("page", "0").param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(pageSize))
                .andExpect(jsonPath("$.medias[0].gymName").value("암장0"))
                .andExpect(jsonPath("$.medias[1].gymName").value("암장1"))
                .andExpect(jsonPath("$.medias[2].gymName").value("암장2"))
                .andExpect(jsonPath("$.medias[3].gymName").value("암장3"))
                .andExpect(jsonPath("$.medias[4].gymName").value("암장4"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(pageSize))
                .andExpect(jsonPath("$.totalPage").value(1));
    }
}