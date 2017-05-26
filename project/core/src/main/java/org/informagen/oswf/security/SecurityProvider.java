package org.informagen.oswf.security;

import org.informagen.oswf.security.Role;
import org.informagen.oswf.security.User;

// Java - Collections

import java.util.Collection;
import java.util.Set;

public interface SecurityProvider {

    // R O L E S ===========================================================================

    Role createRole(String roleName);
    
    void addRole(Role role);
    void addRoles(Collection<Role> roles);
    Set<Role> getRoles(); 
    
    // Return Role by name
    Role getRole(String name);

    // U S E R S ==============================================================================

    User createUser(String userName);

    void addUser(User user);
    void addUsers(Collection<User> users);
    Set<User> getUsers(); 

    // Return user by username
    User getUser(String userName); 


}
