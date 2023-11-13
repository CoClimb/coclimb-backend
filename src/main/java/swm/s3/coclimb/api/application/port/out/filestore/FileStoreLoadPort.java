package swm.s3.coclimb.api.application.port.out.filestore;

import java.net.URL;

public interface FileStoreLoadPort {


    URL getUploadUrl(String key);
}
