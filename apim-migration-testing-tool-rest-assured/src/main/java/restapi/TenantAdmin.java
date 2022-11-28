package restapi;

public class TenantAdmin {
    
    String userName;
    String password;

    public TenantAdmin(String userName, String password) {
        
        this.userName = userName;
        this.password = password;
        
    }
    
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
