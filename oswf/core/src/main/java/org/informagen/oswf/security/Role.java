package org.informagen.oswf.security;

import org.informagen.oswf.security.User;

import org.informagen.typedmap.TypedMap;

// Java - Security
import java.security.Principal;

// Java - Collections
import java.util.Set;

/* Inherited from java.security.Principal *****************
**
**    String getName(); 
*/

public interface Role extends Principal {
    
        // Add User to this Role
        void addUser(User user);
        
        // // Does Role contain User by name
        // boolean  containsUser(String user); 
        // 
        // // Does Role contain User
        // boolean containsUser(User user); 
        
        // Get all Users of this Role
        Set<User> getUsers(); 
                                        
        // Remove User from this Role
        void removeUser(User user); 

        // Extra properties associated with this Role
        TypedMap getTypedMap();
         

}
