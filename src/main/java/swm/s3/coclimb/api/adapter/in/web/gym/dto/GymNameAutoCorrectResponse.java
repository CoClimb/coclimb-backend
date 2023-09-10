package swm.s3.coclimb.api.adapter.in.web.gym.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GymNameAutoCorrectResponse {
    List<String> gymNames;
    private int count;

    public GymNameAutoCorrectResponse(List<String> gymNames) {
        this.gymNames = gymNames;
        this.count = gymNames.size();
    }
}
