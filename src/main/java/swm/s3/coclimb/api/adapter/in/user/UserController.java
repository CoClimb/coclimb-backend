package swm.s3.coclimb.api.adapter.in.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.s3.coclimb.api.adapter.in.user.dto.InstagramAuthRequest;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.oauth.instagram.InstagramOAuthRecord;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserCommand userCommand;
    private final InstagramOAuthRecord instagramOAuthRecord;

    @GetMapping("/login/instagram")
    public ResponseEntity<?> loginInstagram() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("https://api.instagram.com/oauth/authorize?client_id=" + instagramOAuthRecord.clientId()
                + "&redirect_uri=" + instagramOAuthRecord.redirectUri()
                + "&scope=user_profile,user_media&response_type=code"));

        return ResponseEntity
                .status(302)
                .headers(headers)
                .build();
    }

    @PostMapping("/auth/instagram")
    public ResponseEntity<Void> authInstagram(@RequestBody InstagramAuthRequest instagramAuthRequest) {
        userCommand.loginInstagram(instagramAuthRequest.getCode());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
