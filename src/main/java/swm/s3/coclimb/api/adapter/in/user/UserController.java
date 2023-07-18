package swm.s3.coclimb.api.adapter.in.user;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.s3.coclimb.api.adapter.in.user.dto.InstagramAuthRequest;
import swm.s3.coclimb.api.adapter.in.user.dto.InstagramAuthResponse;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.in.user.UserQuery;
import swm.s3.coclimb.api.auth.SessionUser;
import swm.s3.coclimb.api.domain.User;
import swm.s3.coclimb.api.auth.instagram.InstagramOAuthRecord;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserCommand userCommand;
    private final UserQuery userQuery;
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
    public ResponseEntity<InstagramAuthResponse> authInstagram(@RequestBody InstagramAuthRequest instagramAuthRequest, HttpSession session) {
        if(session.getAttribute("user") != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        User user = userCommand.loginInstagram(instagramAuthRequest.getCode());

        if(user != null) {
            session.setAttribute("user", SessionUser.builder()
                    .UserId(user.getId())
                    .instaUserId(user.getInstaUserId())
                    .instaAccessToken(user.getInstaAccessToken())
                    .build());
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(InstagramAuthResponse.builder()
                .instaUserId(user.getInstaUserId())
                .instaAccessToken(user.getInstaAccessToken())
                .build());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("user");
        if(sessionUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
        if(sessionUser.getUserId() != id) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        User user = userQuery.findById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
