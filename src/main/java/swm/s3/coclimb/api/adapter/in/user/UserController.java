package swm.s3.coclimb.api.adapter.in.user;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.s3.coclimb.api.adapter.in.user.dto.InstagramAuthRequest;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.in.user.UserQuery;
import swm.s3.coclimb.api.auth.SessionUser;
import swm.s3.coclimb.api.domain.User;
import swm.s3.coclimb.api.auth.instagram.InstagramOAuthRecord;

import java.net.URI;
import java.time.LocalDate;

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

    @PostMapping("/auto-login/instagram")
    public ResponseEntity<Void> autoLoginInstagram(@CookieValue(value = "instaAccessToken", required = false) String instaAccessToken,
                                                    @CookieValue(value = "instaUserId", required = false) String instaUserId,
                                                    HttpSession session) {
        if(session.getAttribute("user") != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        if(instaAccessToken == null || instaUserId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        User user = userQuery.findByInstaUserId(Long.parseLong(instaUserId));

        if(user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        if(!user.getInstaAccessToken().equals(instaAccessToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        if(user.getInstaTokenExpireDate().isBefore(LocalDate.now())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        session.setAttribute("user", SessionUser.builder()
                .UserId(user.getId())
                .instaUserId(user.getInstaUserId())
                .instaAccessToken(user.getInstaAccessToken())
                .build());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/auth/instagram")
    public ResponseEntity<Void> authInstagram(@RequestBody InstagramAuthRequest instagramAuthRequest, HttpSession session,
                                              HttpServletResponse response) {
        if(session.getAttribute("user") != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        User user = userCommand.loginInstagram(instagramAuthRequest.getCode());

        if(user != null) {
            session.setAttribute("user", SessionUser.builder()
                    .UserId(user.getId())
                    .instaUserId(user.getInstaUserId())
                    .instaAccessToken(user.getInstaAccessToken())
                    .build());

            Cookie instaAccessTokenCookie = new Cookie("instaAccessToken", user.getInstaAccessToken());
            instaAccessTokenCookie.setPath("/");
            instaAccessTokenCookie.setHttpOnly(true);
            instaAccessTokenCookie.setSecure(true);
            instaAccessTokenCookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(instaAccessTokenCookie);

            Cookie instaUserIdCookie = new Cookie("instaUserId", user.getInstaUserId().toString());
            instaUserIdCookie.setPath("/");
            instaUserIdCookie.setHttpOnly(true);
            instaUserIdCookie.setSecure(true);
            instaUserIdCookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(instaUserIdCookie);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/users/me")
    public ResponseEntity<User> getUser(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("user");
        if(sessionUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        User user = userQuery.findById(sessionUser.getUserId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session, HttpServletRequest request) {
        session.invalidate();

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("instaAccessToken") || cookie.getName().equals("instaUserId")) {
                cookie.setMaxAge(0);
            }
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
