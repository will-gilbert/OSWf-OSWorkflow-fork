package org.informagen.oswf.security;

import org.informagen.oswf.security.User;

import org.informagen.typedmap.TypedMap;
import org.informagen.typedmap.MemoryTypedMap;

// Java - Security
import java.security.Principal;

// Java - Collections
import java.util.HashSet;
import java.util.Set;

/* Inherited from java.security.Principal *****************
**
**    String getName(); 
*/

public class DefaultRole implements Role {

        final Set<User> users = new HashSet<User>();
        final String name;
        TypedMap propertySet = null;

        public DefaultRole(String name) {
            this.name = name;
        }

        public DefaultRole(String name, TypedMap propertySet) {
            this.name = name;
            this.propertySet = propertySet;
        }
 
        public String getName() {
            return name;
        }
   
        // Add User to this Role
        public void addUser(User user) {
            users.add(user);
            user.addToRole(this);
        }
        
        // Does this Role contain a User by username
        // public boolean containsUser(String userName) { 
        //     for(User user : users)
        //         if(userName.equals(user.getName()))
        //             return true;
        //     
        //     return false; 
        // } 
        // 
        // // Does Role contain User
        // public boolean containsUser(User user) {
        //     return users.contains(user); 
        // }
        
        // Get all Users of this Role
        public Set<User> getUsers() {
            return users;
        }
                                        
        // Remove User from this Role
        public void removeUser(User user) {

        } 

        // Extra properties associated with this Role
        public TypedMap getTypedMap() {
            if(propertySet == null)
                propertySet = new MemoryTypedMap();

            return propertySet;
        }

        public String toString() {
            return new StringBuffer("org.informagen.oswf.security.Role: ")
                .append("name: ").append(getName()).append(", ")
                .append("users: ").append(getUsers().size())
                .toString();
        }
         

}
