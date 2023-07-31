package swm.s3.coclimb.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import swm.s3.coclimb.api.exception.errortype.login.InvalidToken;
import swm.s3.coclimb.config.security.JwtManager;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtManager jwtManager;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //@Auth가 붙어있는 경우만 인증체크
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if(handlerMethod.getMethodAnnotation(Auth.class)==null){
                return true;
            }
        } catch (ClassCastException exception) {
            return true;
        }

        String accessToken = request.getHeader("Authorization");
        if(!jwtManager.isValid(accessToken)){
            throw new InvalidToken();
        }

        return true;
    }
}
