package swm.s3.coclimb.config.argumentresolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import swm.s3.coclimb.api.application.port.out.persistence.user.UserLoadPort;
import swm.s3.coclimb.domain.user.User;
import swm.s3.coclimb.config.security.JwtManager;

@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtManager jwtManager;
    private final UserLoadPort userLoadPort;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        boolean hasMemberType = User.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = request.getHeader("Authorization");
        Long userId = Long.valueOf(jwtManager.getSubject(accessToken));
        return userLoadPort.getById(userId);
    }
}
