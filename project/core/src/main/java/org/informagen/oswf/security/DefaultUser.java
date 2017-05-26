package org.informagen.oswf.security;

import org.informagen.oswf.security.Role;
import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.MemoryPropertySet;

import java.security.Principal;

// Java - Collections
import java.util.HashSet;
import java.util.Set;

/* Inherited from java.security.Principal *****************
**
**    // Returns the name of this principal.
**    String	getName(); 
*/

public class DefaultUser implements User {

        final static String EMAIL = "EMail";
        final static String FULLNAME = "FullName";

        final Set<Role> roles = new HashSet<Role>();
        final String name;
        PropertySet propertySet = null;


        public DefaultUser(String name) {
            this.name = name;
        }

        public DefaultUser(String name, PropertySet propertySet) {
            this.name = name;
            this.propertySet = propertySet;
        }

        public String getName() {
            return name;
        }

        // Add this User to a Role
        public void addToRole(Role role) {
            roles.add(role);
        }
         
        // Get all Roles that User is a member of
        public Set<Role> getRoles() {
            return roles;
        }

        // Is User a member of a Role
        public boolean hasRole(Role role) {
            return roles.contains(role);
        }

        // Is User a member of a Role by role name
        public boolean hasRole(String roleName) {
            for(Role role : roles)
                if(roleName.equals(role.getName()))
                    return true;
            
            return false;
        }

        // Remove this User from a Role
        public void removeFromRole(Role role) {} 

        // Convenience methods to access property
        public void setEmail(String email) {
            getPropertySet().setString(EMAIL, email);
        }

        // Convenience method to access property
        public String getEmail() {
            return getPropertySet().getString(EMAIL);
        }

        // Convenience method to access property
        public void setFullName(String fullName) {
            getPropertySet().setString(FULLNAME, fullName);
        }

        // Convenience method to access property
        public String getFullName() {
            return getPropertySet().getString(FULLNAME);
        }


        // Extra properties associated with entity
        public PropertySet getPropertySet() {
            if(propertySet == null)
                propertySet = new MemoryPropertySet();

            return propertySet;
        }


        public String toString() {
            return new StringBuffer("org.informagen.oswf.security.User: ")
                .append("name: ").append(getName()).append(", ")
                .append("roles: ").append(getRoles().size())
                .toString();
        }

}
