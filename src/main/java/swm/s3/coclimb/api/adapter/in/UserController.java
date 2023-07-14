package swm.s3.coclimb.api.adapter.in;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import swm.s3.coclimb.api.oauth.instagram.InstagramOAuthRecord;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final InstagramOAuthRecord instagramOAuthRecord;

    @GetMapping("/login/instagram")
    public ResponseEntity<?> login() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("https://api.instagram.com/oauth/authorize?client_id=" + instagramOAuthRecord.clientId()
                + "&redirect_uri=" + instagramOAuthRecord.redirectUri()
                + "&scope=user_profile,user_media&response_type=code"));

        return ResponseEntity
                .status(302)
                .headers(headers)
                .build();
    }
}
