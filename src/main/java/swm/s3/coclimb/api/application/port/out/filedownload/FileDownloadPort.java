package swm.s3.coclimb.api.application.port.out.filedownload;

public interface FileDownloadPort {

    DownloadedFileDetail downloadFile(String url);
}
