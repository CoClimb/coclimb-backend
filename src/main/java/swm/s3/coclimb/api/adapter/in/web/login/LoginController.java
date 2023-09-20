package swm.s3.coclimb.api.adapter.in.web.login;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import swm.s3.coclimb.api.adapter.in.web.login.dto.InstagramLoginPageResponse;
import swm.s3.coclimb.api.adapter.in.web.login.dto.InstagramLoginRequest;
import swm.s3.coclimb.api.adapter.in.web.login.dto.LoginResponse;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.application.port.in.login.LoginCommand;
import swm.s3.coclimb.config.security.JwtManager;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginCommand loginCommand;
    private final InstagramOAuthRecord instagramOAuthRecord;
    private final JwtManager jwtManager;
    @GetMapping("/login/instagram")
    public ResponseEntity<InstagramLoginPageResponse> redirectInstagramLoginPage() {
        return ResponseEntity.ok(InstagramLoginPageResponse.builder().loginPageUrl(
                "https://api.instagram.com/oauth/authorize?client_id=" + instagramOAuthRecord.getClientId()
                + "&redirect_uri=" + instagramOAuthRecord.getRedirectUri()
                + "&scope=user_profile,user_media&response_type=code").build());
    }

    @PostMapping("/login/instagram")
    public ResponseEntity<LoginResponse> loginWithInstagram(@RequestBody @Valid InstagramLoginRequest request) {
        Long userId = loginCommand.loginWithInstagram(request.getCode());
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .accessToken(jwtManager.issueToken(userId.toString()))
                        .build());
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

}
