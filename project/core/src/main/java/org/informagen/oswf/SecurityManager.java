package org.informagen.oswf;

import org.informagen.oswf.security.Role;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.SecurityProvider;
import org.informagen.oswf.security.DefaultSecurityProvider;

import org.informagen.oswf.propertyset.PropertySet;

// Java - Collections
import java.util.Set;

public class SecurityManager {

    static SecurityManager instance = null;
    protected SecurityProvider securityProvider = null;

    // Singleton - use static 'getInstance' method
    private SecurityManager() {}

    public static SecurityManager getInstance() {
        if(instance == null)
            instance = new SecurityManager();
            
        return instance;
    }

    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }

    // G R O U P S ===========================================================================

    public Role createRole(String name) {
        return getSecurityProvider().createRole(name);
    }

    public SecurityManager addRole(Role role) {
        getSecurityProvider().addRole(role);
        return instance;
    }

    public Role getRole(String name) {
        return getSecurityProvider().getRole(name);
    }

    public Set<Role> getRoles() {
        return getSecurityProvider().getRoles();
    } 

    // U S E R S ==============================================================================

    public User createUser(String name) {
        return getSecurityProvider().createUser(name);
    }

    public SecurityManager addUser(User user) {
        getSecurityProvider().addUser(user);
        return instance;
    }

    public User getUser(String name) {
        return getSecurityProvider().getUser(name);
    }

    public Set<User> getUsers() {
        return getSecurityProvider().getUsers();
    } 


    // PROTECTED ==============================================================================

    protected SecurityProvider getSecurityProvider() {
        if(securityProvider == null)
            securityProvider = new DefaultSecurityProvider();
        return securityProvider;
    }


}
