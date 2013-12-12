package info.jcalfee.xsi.client;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    /**
     */
    private static final long serialVersionUID = 1L;

    private String key;
    
    private Date created = new Date();
    
    private String authDomain;//example: gmail.com
    
    private String externalId;

    private String name;
    
    private String email;
    
    private boolean admin;
    

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getAuthDomain() {
        return authDomain;
    }

    public void setAuthDomain(String authDomain) {
        this.authDomain = authDomain;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
    
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String toString() {
        return getEmail();
    }
    
}
