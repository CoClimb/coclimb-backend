package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaInfo;

import java.util.List;

@Getter
public class MediaPageResponse {
    private List<MediaInfo> medias;
    private int page;
    private int size;
    private int totalPage;

    @Builder
    private MediaPageResponse(List<MediaInfo> medias, int page, int size, int totalPage) {
        this.medias = medias;
        this.page = page;
        this.size = size;
        this.totalPage = totalPage;
    }

    public static MediaPageResponse of(Page<MediaInfo> pagedMedias) {
        return MediaPageResponse.builder()
                .medias(pagedMedias.getContent())
                .page(pagedMedias.getNumber())
                .size(pagedMedias.getSize())
                .totalPage(pagedMedias.getTotalPages())
                .build();
    }

}
