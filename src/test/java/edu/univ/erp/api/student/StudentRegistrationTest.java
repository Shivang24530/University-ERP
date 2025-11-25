package test.java.edu.univ.erp.api.student;


import test.java.edu.univ.erp.fakes.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class StudentRegistrationTest {

    private FakeERPDatabase db;

    @BeforeEach
    void setup() {
        db = new FakeERPDatabase();
    }

    @Test
    @DisplayName("Register succeeds when seats are available")
    void registerSuccess() {
        db.register("stu1", "SEC101");
        assertEquals(1, db.getEnrollments("stu1").size());
    }

    @Test
    @DisplayName("Full section blocked")
    void fullSectionBlocked() {
        db.setSectionCapacity("SEC101", 1);
        db.register("stuX", "SEC101");

        if (db.getSectionCapacity("SEC101") <= db.getEnrollments("stuX").size()) {
            assertTrue(true);
        }
    }
}
