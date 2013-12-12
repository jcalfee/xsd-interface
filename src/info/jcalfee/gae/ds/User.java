package info.jcalfee.gae.ds;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User implements Serializable {

    /**
     */
    private static final long serialVersionUID = 1L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private Date created = new Date();
    
    @Persistent
    private String authDomain;//example: gmail.com
    
    @Persistent
    private String externalId;

    @Persistent
    private String name;
    
    @Persistent
    private String email;
    
    @Persistent
    private boolean admin;
    
    public void from(com.google.appengine.api.users.User u) {
        setAuthDomain(u.getAuthDomain());
        setExternalId(u.getUserId());
        setName(u.getNickname());
        setEmail(u.getEmail());
    }
    
    public boolean to(User u) {
        if(u.equals(this))
            return false;
        
        u.setAuthDomain(getAuthDomain());
        u.setExternalId(getExternalId());
        u.setName(getName());
        u.setEmail(u.getEmail());
        u.setAdmin(u.isAdmin());
        return true;
    }
    
    public info.jcalfee.xsi.client.User toClientUser() {
        info.jcalfee.xsi.client.User clientUser = new info.jcalfee.xsi.client.User();
        clientUser.setAdmin(admin);
        clientUser.setAuthDomain(authDomain);
        clientUser.setCreated(created);
        clientUser.setEmail(email);
        clientUser.setExternalId(externalId);
        clientUser.setKey(key.toString());
        clientUser.setName(name);
        return clientUser;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((authDomain == null) ? 0 : authDomain.hashCode());
        result = prime * result
                + ((externalId == null) ? 0 : externalId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (authDomain == null) {
            if (other.authDomain != null)
                return false;
        } else if (!authDomain.equals(other.authDomain))
            return false;
        if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
            return false;
        return true;
    }
    
    
    
}
