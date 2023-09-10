package swm.s3.coclimb.api.adapter.out.filedownload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
import swm.s3.coclimb.api.application.port.out.filedownload.FileDownloadPort;
import swm.s3.coclimb.api.exception.errortype.filedownload.FileDownloadFail;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;

@Component
public class FileDownloader implements FileDownloadPort {

    @Value("${file.download.path}")
    private String downloadPath;

    @Override
    public DownloadedFileDetail downloadFile(String url) {
        try {
            URL downloadUrl = new URL(url);
            String ext = downloadUrl.getPath().substring(downloadUrl.getPath().lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + ext;
            ReadableByteChannel readableByteChannel = Channels.newChannel(downloadUrl.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(downloadPath + fileName);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();

            return DownloadedFileDetail.builder()
                    .path(downloadPath + fileName)
                    .name(fileName)
                    .build();
        } catch (Exception e) {
            throw new FileDownloadFail();
        }
    }
}
