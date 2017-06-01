package org.informagen.oswf.security;

import org.informagen.oswf.security.Role;
import org.informagen.typedmap.TypedMap;

import java.security.Principal;

// Java - Collections
import java.util.HashSet;
import java.util.Set;

/* Inherited from java.security.Principal *****************
**
**    // Returns the name of this principal.
**    String	getName(); 
*/

public interface User extends Principal {
    
        Set<Role> roles = new HashSet<Role>();

        // Add this User to a Role
        void addToRole(Role role);
         
        // Get all Roles that User is a member of
        Set<Role> getRoles();

        // Is User a member of a Role
        boolean	hasRole(Role role); 

        // Is User a member of a Role by role name
        boolean	hasRole(String roleName); 

        // Remove this User from a Role
        void removeFromRole(Role role); 

        // Convenience methods to access property
        void setEmail(String email); 

        // Convenience method to access property
        String getEmail();

        // Convenience method to access property
        void setFullName(String fullName); 

        // Convenience method to access property
        String getFullName(); 

        // Extra properties associated with entity
        TypedMap getTypedMap();


}
