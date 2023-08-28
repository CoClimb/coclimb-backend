package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import swm.s3.coclimb.domain.media.Media;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MediaPageResponse {
    private List<MediaSubInfo> medias;
    private int page;
    private int size;
    private int totalPage;

    @Builder
    private MediaPageResponse(List<Media> medias, int page, int size, int totalPage) {
        this.medias = medias.stream().map(MediaSubInfo::new).collect(Collectors.toList());
        this.page = page;
        this.size = size;
        this.totalPage = totalPage;
    }

    public static MediaPageResponse of(Page<Media> pagedMedias) {
        return MediaPageResponse.builder()
                .medias(pagedMedias.getContent())
                .page(pagedMedias.getNumber())
                .size(pagedMedias.getSize())
                .totalPage(pagedMedias.getTotalPages())
                .build();
    }

    @Getter
    private class MediaSubInfo {
        private Long id;
        private String thumbnailUrl;
        private String gymName;
        private String problemColor;
        private String username;

        private MediaSubInfo(Media media) {
            this.id = media.getId();
            this.thumbnailUrl = media.getThumbnailUrl();
            this.gymName = media.getMediaProblemInfo().getGymName();
            this.problemColor = media.getMediaProblemInfo().getColor();
            this.username = media.getUser().getName();
        }
    }
}
