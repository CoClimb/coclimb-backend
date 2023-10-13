package swm.s3.coclimb.api.adapter.in.web.login;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import swm.s3.coclimb.api.adapter.in.web.login.dto.OAuthLoginRequest;
import swm.s3.coclimb.api.adapter.in.web.login.dto.LoginSuccessResponse;
import swm.s3.coclimb.api.adapter.in.web.login.dto.OAuthLoginPageResponse;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.adapter.out.oauth.kakao.KakaoOAuthRecord;
import swm.s3.coclimb.api.application.port.in.login.LoginCommand;
import swm.s3.coclimb.config.security.JwtManager;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginCommand loginCommand;
    private final InstagramOAuthRecord instagramOAuthRecord;
    private final KakaoOAuthRecord kakaoOAuthRecord;
    private final JwtManager jwtManager;

    @GetMapping("/login/instagram")
    public ResponseEntity<OAuthLoginPageResponse> getInstagramLoginPage() {
        return ResponseEntity.ok(OAuthLoginPageResponse.builder().loginPageUrl(
                "https://api.instagram.com/oauth/authorize?client_id=" + instagramOAuthRecord.getClientId()
                + "&redirect_uri=" + instagramOAuthRecord.getRedirectUri()
                + "&scope=user_profile,user_media&response_type=code").build());
    }

    @PostMapping("/login/instagram")
    public ResponseEntity<LoginSuccessResponse> loginWithInstagram(@RequestBody @Valid OAuthLoginRequest request) {
        Long userId = loginCommand.loginWithInstagram(request.getCode());
        return ResponseEntity.ok(
                LoginSuccessResponse.builder()
                        .accessToken(jwtManager.issueToken(userId.toString()))
                        .build());
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<OAuthLoginPageResponse> getKakaoLoginPage() {
        return ResponseEntity.ok(OAuthLoginPageResponse.builder().loginPageUrl(
                "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoOAuthRecord.getClientId()
                        + "&redirect_uri=" + kakaoOAuthRecord.getRedirectUri()
                        + "&response_type=code")
                .build());
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<LoginSuccessResponse> loginWithKakao(@RequestBody @Valid OAuthLoginRequest request) {
        Long userId = loginCommand.loginWithKakao(request.getCode());
        return ResponseEntity.ok(
                LoginSuccessResponse.builder()
                        .accessToken(jwtManager.issueToken(userId.toString()))
                        .build());
    }
}
