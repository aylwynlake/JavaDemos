package ly.test.webflow;

public class LoginService {
    public String performLogin(LoginCredentials loginCredentials) throws IncorrectLoginCredentialsException {

        if (loginCredentials.getLoginName() != null 
                && loginCredentials.getLoginName().trim().equalsIgnoreCase("test") 
                && loginCredentials.getPassword() != null 
                && loginCredentials.getPassword().trim().equalsIgnoreCase("test")) {
            // user successfully logged in
            return "success";
        } else {
            throw new IncorrectLoginCredentialsException();
        }
    }

}
