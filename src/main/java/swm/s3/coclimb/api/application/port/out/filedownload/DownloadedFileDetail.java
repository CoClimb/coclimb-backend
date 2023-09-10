package swm.s3.coclimb.api.application.port.out.filedownload;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DownloadedFileDetail {
    String path;
    String name;
}
