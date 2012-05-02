package java.com.sharepast.mvc.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/22/12
 * Time: 12:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewUserForm {

    @NotEmpty
    @Size(min = 1, max = 50)
    private String userName;

    @NotEmpty
    @Size(min = 1, max = 20)
    private String email;

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
