package tests;

// OSWf Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

// OSWf Security - Default implementations
import org.informagen.oswf.security.DefaultUser;
import org.informagen.oswf.security.DefaultRole;

// Java - Reflection
import java.lang.reflect.Field;

// JUnit 4.x testing
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class SecurityManagerTest {


    @After
    public void resetSecurityManager() throws Exception {

        // Access the Singleton static field 'instance'
        Class clazz = Class.forName("org.informagen.oswf.SecurityManager");
        Field field = clazz.getDeclaredField("instance");
        field.setAccessible(true);
         
        // Sets the 'instance' to null so that a new test creates a new one
        field.set(field.get(clazz), null);
    }


    @Test
    public void getInstance()  {
        SecurityManager securityManager = SecurityManager.getInstance();
        assertNotNull(securityManager);
    }

    @Test
    public void createUsersByName() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
        
        securityManager.createUser("Alice");
        assertEquals(1, securityManager.getUsers().size());
        securityManager.createUser("Bob");
        assertEquals(2, securityManager.getUsers().size());

        securityManager.createUser("Alice");
        assertEquals(2, securityManager.getUsers().size());
        
    }

    @Test
    public void createUsersByUser() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
        
        User alice = new DefaultUser("Alice");
        User bob = new DefaultUser("Bob");

        securityManager.addUser(alice);
        assertEquals(1, securityManager.getUsers().size());
        securityManager.addUser(bob);
        assertEquals(2, securityManager.getUsers().size());

        // Should not be able to add alice twice; but no exceptions please
        securityManager.addUser(alice);
        assertEquals(2, securityManager.getUsers().size());
    }
    
    @Test
    public void addUsersUsingFluentInterface() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
        
        User alice = new DefaultUser("Alice");
        User bob = new DefaultUser("Bob");

        securityManager
            .addUser(alice)
            .addUser(bob)
            .addUser(new DefaultUser("Charlie"))
        ;
        
        assertEquals(3, securityManager.getUsers().size());
    }

    @Test
    public void getUserByName() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
    
        securityManager
            .addUser(new DefaultUser("Alice"))
            .addUser(new DefaultUser("Bob"))
            .addUser(new DefaultUser("Charlie"))
        ;

        User user = securityManager.getUser("Alice");
        assertEquals("Alice", user.getName());
        
    }
    
    @Test
    public void createRolesByName() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
        
        securityManager.createRole("Foo");
        assertEquals(1, securityManager.getRoles().size());
        securityManager.createRole("Bar");
        assertEquals(2, securityManager.getRoles().size());

        securityManager.createRole("Foo");
        assertEquals(2, securityManager.getRoles().size());
        
    }

    @Test
    public void createRolesByUser() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
       
        Role foo = new DefaultRole("Foo");
        Role bar = new DefaultRole("Bar");

        securityManager.addRole(foo);
        assertEquals(1, securityManager.getRoles().size());
        securityManager.addRole(bar);
        assertEquals(2, securityManager.getRoles().size());

        // Should not be able to add foo twice; but no exceptions please
        securityManager.addRole(foo);
        assertEquals(2, securityManager.getRoles().size());
    }
    
    @Test
    public void addRolesUsingFluentInterface() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
        
        Role foo = new DefaultRole("Foo");
        Role bar = new DefaultRole("Bar");

        securityManager
            .addRole(foo)
            .addRole(bar)
            .addRole(new DefaultRole("Baz"))
        ;
        
        assertEquals(3, securityManager.getRoles().size());
    }

    @Test
    public void getRoleByName() {
        
        SecurityManager securityManager = SecurityManager.getInstance();
    
        securityManager
            .addRole(new DefaultRole("foo"))
            .addRole(new DefaultRole("bar"))
            .addRole(new DefaultRole("baz"))
        ;

        Role role = securityManager.getRole("foo");
        assertEquals("foo", role.getName());
        
    }

    @Test
    public void addUserWithRoles() {
        
        User user = new DefaultUser("testuser");
        
        user.addToRole(new DefaultRole("foo"));
        user.addToRole(new DefaultRole("bar"));

        SecurityManager securityManager = SecurityManager.getInstance();
        securityManager.addUser(user);

        assertEquals(1, securityManager.getUsers().size());
        assertEquals(2, securityManager.getRoles().size());
        
    }

    @Test
    public void addRoleWithUsers() {
        
        Role role = new DefaultRole("foo");
        
        role.addUser(new DefaultUser("Alice"));
        role.addUser(new DefaultUser("Bob"));

        SecurityManager securityManager = SecurityManager.getInstance();
        securityManager.addRole(role);

        assertEquals(1, securityManager.getRoles().size());
        assertEquals(2, securityManager.getUsers().size());
        
    }

    
    
    
}
