package swm.s3.coclimb.api.adapter.out.filestore;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.IntegrationTestSupport;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class AwsS3ManagerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("")
    void getUploadUrl() throws Exception {
        // given
        String key = "test/getUploadUrl.txt";

        // when
        URL sut = awsS3Manager.getUploadUrl(key);
        System.out.println("sut = " + sut.toString());

        // then
        Assertions.assertThat(uploadByPreSignedUrl(sut)).isEqualTo(200);
    }

    public Integer uploadByPreSignedUrl(URL url) {
        try {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // HTTP PUT 메서드를 설정. presigned URL은 특정 HTTP 메서드와 연결됩니다.
            connection.setRequestMethod("PUT");
            // 출력을 위한 연결을 할 수 있도록 설정.
            connection.setDoOutput(true);
            // 텍스트 컨텐츠 유형 설정.
            connection.setRequestProperty("Content-Type", "text/plain");

            try (OutputStream out = connection.getOutputStream()) {
                // 텍스트 데이터를 바이트로 변환하고 출력 스트림에 기록.
                out.write("textToUpload".getBytes("UTF-8"));
            }

            // 응답 코드를 받음. 200 OK는 성공을 의미.
            System.out.println("HTTP response code: " + connection.getResponseCode());
            return connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}