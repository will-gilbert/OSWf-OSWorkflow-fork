package tests

// OSWf Security
import org.informagen.oswf.SecurityManager
import org.informagen.oswf.security.User
import org.informagen.oswf.security.Role

// OSWf Security - Default implementations
import org.informagen.oswf.security.DefaultUser
import org.informagen.oswf.security.DefaultRole

// Java - Reflection
import java.lang.reflect.Field

// JUnit 4.x testing
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail


class SecurityManagerTest {


    @Before
    void resetSecurityManager() throws Exception {

        // Access the Singleton static field 'instance'
        Class clazz = Class.forName("org.informagen.oswf.SecurityManager")
        Field field = clazz.getDeclaredField("instance")
        field.setAccessible(true)
         
        // Sets the 'instance' to null so that a new test creates a new one
        field.set(field.get(clazz), null)
    }


    @Test
    void getInstance()  {
        def securityManager = SecurityManager.getInstance()
        assert securityManager
    }

    @Test
    void createUsers() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
        
        securityManager.createUser("Alice")
        assert 1 == securityManager.getUsers().size()

        securityManager.createUser("Bob")
        assert 2 == securityManager.getUsers().size()

        // Users are unique by name or other identifier
        securityManager.createUser("Alice")
        assert 2 == securityManager.getUsers().size()
        
    }

    @Test
    void addUsers() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
        
        User alice = new DefaultUser("Alice")
        User bob = new DefaultUser("Bob")

        securityManager.addUser(alice)
        assert 1 == securityManager.getUsers().size()

        securityManager.addUser(bob)
        assert 2 == securityManager.getUsers().size()

        // Should not be able to add alice twice
        securityManager.addUser(alice)
        assert 2 == securityManager.getUsers().size()
    }
    
    @Test
    void addUsersUsingFluentInterface() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
        
        User alice = new DefaultUser("Alice")
        User bob = new DefaultUser("Bob")

        securityManager
            .addUser(alice)
            .addUser(bob)
            .addUser(new DefaultUser("Charlie"))
        
        
        assert 3 == securityManager.getUsers().size()
    }

    @Test
    void getUserByName() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
    
        securityManager
            .addUser(new DefaultUser("Alice"))
            .addUser(new DefaultUser("Bob"))
            .addUser(new DefaultUser("Charlie"))
        

        User user = securityManager.getUser("Alice")
        assert "Alice" == user.getName()
        
    }
    
    @Test
    void createRoles() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
        
        securityManager.createRole("Foo")
        assert 1 == securityManager.getRoles().size()

        securityManager.createRole("Bar")
        assert 2 == securityManager.getRoles().size()

        securityManager.createRole("Foo")
        assert 2 == securityManager.getRoles().size()
        
    }

    @Test
    void addRoles() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
       
        Role foo = new DefaultRole("Foo")
        Role bar = new DefaultRole("Bar")

        securityManager.addRole(foo)
        assert 1 == securityManager.getRoles().size()

        securityManager.addRole(bar)
        assert 2 == securityManager.getRoles().size()

        // Should not be able to add foo twice
        securityManager.addRole(foo)
        assert 2 == securityManager.getRoles().size()
    }
    
    @Test
    void addRolesUsingFluentInterface() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
        
        Role foo = new DefaultRole("Foo")
        Role bar = new DefaultRole("Bar")

        securityManager
            .addRole(foo)
            .addRole(bar)
            .addRole(new DefaultRole("Baz"))
        
        
        assert 3 == securityManager.getRoles().size()
    }

    @Test
    void getRole() {
        
        SecurityManager securityManager = SecurityManager.getInstance()
    
        securityManager
            .addRole(new DefaultRole("foo"))
            .addRole(new DefaultRole("bar"))
            .addRole(new DefaultRole("baz"))
        

        Role role = securityManager.getRole("foo")
        assert "foo" == role.getName()
        
    }

    @Test
    void addUserWithRoles() {
        
        User user = new DefaultUser("testuser")
        
        user.addToRole(new DefaultRole("foo"))
        user.addToRole(new DefaultRole("bar"))

        SecurityManager securityManager = SecurityManager.getInstance()
        securityManager.addUser(user)

        assert 1 == securityManager.getUsers().size()
        assert 2 == securityManager.getRoles().size()
        
    }

    @Test
    void addRoleWithUsers() {
        
        Role role = new DefaultRole("foo")
        
        role.addUser(new DefaultUser("Alice"))
        role.addUser(new DefaultUser("Bob"))

        SecurityManager securityManager = SecurityManager.getInstance()
        securityManager.addRole(role)

        assert 1 == securityManager.getRoles().size()
        assert 2 == securityManager.getUsers().size()
        
    }

    
    
    
}
