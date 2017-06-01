package org.informagen.oswf.security;

import org.informagen.oswf.security.Role;
import org.informagen.oswf.security.User;

// Java - Collections
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class DefaultSecurityProvider implements SecurityProvider {

    Set<Role> roles = new HashSet<Role>();
    Set<User> users = new HashSet<User>();

    // G R O U P S ===========================================================================

    public Role createRole(String roleName) {
        Role role = getRole(roleName);
        if(role != null)
            return role;
            
        role = new DefaultRole(roleName);
        addRole(role);
        
        return role;
    }

    public void addRole(Role role) {
        roles.add(role);
        users.addAll(role.getUsers());
    }

    public void addRoles(Collection<Role> roles) {
        this.roles.addAll(roles);
    }

    public Set<Role> getRoles() {
        return roles;
    } 

    // Return Role with given name
    public Role getRole(String roleName) {
        for(Role role : roles)
            if(role.getName().equals(roleName))
                return role;
                
        return null;
    }

    // U S E R S ==============================================================================

    public User createUser(String userName) {
        User user = getUser(userName);
        if(user != null)
            return user;
            
        user = new DefaultUser(userName);
        addUser(user);
        
        return user;
    }

    // When adding a user also add their roles
    public void addUser(User user) {
        users.add(user);
        roles.addAll(user.getRoles());
    }
    
    public void addUsers(Collection<User> users) {
        for(User user : users)
            addUser(user);
    }
    
    public Set<User> getUsers() {
        return users;
    }


    // Return user with given name.
    public User getUser(String userName) {
        for(User user : users)
            if(user.getName().equals(userName))
                return user;
                
        return null;
    } 


}
