package test.java.edu.univ.erp.fakes;

public class FakeAuthStore {

    public String getHashFor(String username) {
        // simulate hashed password (not actual bcrypt)
        return "HASH(admin123)";
    }

    public boolean validatePassword(String typed, String storedHash) {
        return storedHash.equals("HASH(" + typed + ")");
    }
}
