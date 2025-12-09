package com.jyoxin.smartshop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Should hash password with bcrypt salt")
    void shouldHashPassword() {
        String rawPassword = "password";

        String hash = PasswordUtil.hash(rawPassword);

        assertNotNull(hash);
        assertNotEquals(rawPassword, hash);
        assertTrue(hash.length() > rawPassword.length());
    }

    @Test
    @DisplayName("Should verify password correctly")
    void shouldVerifyPassword() {
        String rawPassword = "password";
        String hash = PasswordUtil.hash(rawPassword);

        assertTrue(PasswordUtil.verify(rawPassword, hash));
        assertFalse(PasswordUtil.verify("wrongPassword", hash));
    }

    @Test
    @DisplayName("Should return false when verifying with null or invalid inputs")
    void shouldReturnFalseForInvalidInputs() {
        assertFalse(PasswordUtil.verify(null, "hash"));
        assertFalse(PasswordUtil.verify("password", null));
        assertFalse(PasswordUtil.verify(null, null));
        assertFalse(PasswordUtil.verify("password", "invalid-hash"));
    }

    @Test
    @DisplayName("Should throw exception for null or blank password when hashing")
    void shouldThrowExceptionForInvalidPasswordWhenHashing() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(""));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash("   "));
    }
}
