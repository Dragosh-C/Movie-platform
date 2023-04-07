package implementation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentials {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private int balance;

    public UserCredentials() {

    }
    public UserCredentials(UserCredentials credentials) {
        this.name = credentials.getName();
        this.password = credentials.getPassword();
        this.accountType = credentials.getAccountType();
        this.country = credentials.getCountry();
        this.balance = credentials.getBalance();
    }


}
