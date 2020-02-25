package battleships.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginData {
    private String username;
    private String password;

    public LoginData(){}
    public LoginData(String username, String password){
        this.username = username;
        this.password = password;
    }
}
