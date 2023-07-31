package swm.s3.coclimb.api.application.port.in.login;

public interface LoginCommand {

    Long loginWithInstagram(String code);
}
