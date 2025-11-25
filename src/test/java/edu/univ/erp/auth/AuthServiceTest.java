package test.java.edu.univ.erp.auth;

import test.java.edu.univ.erp.fakes.FakeAuthStore;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    @DisplayName("Wrong password rejected")
    void wrongPassword() {
        FakeAuthStore store = new FakeAuthStore();
        assertFalse(store.validatePassword("wrong", store.getHashFor("admin")));
    }

    @Test
    @DisplayName("Correct password accepted")
    void correctPassword() {
        FakeAuthStore store = new FakeAuthStore();
        assertTrue(store.validatePassword("admin123", store.getHashFor("admin")));
    }
}

