package id.co.learn.ib.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SessionDto implements Serializable {

    private String username;
    private String fullname;
    private Long lastLogin;
    private String token;

}
