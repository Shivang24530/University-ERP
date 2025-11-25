package test.java.edu.univ.erp.access;



import test.java.edu.univ.erp.fakes.FakeAccessChecker;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AccessCheckerTest {

    @Test
    @DisplayName("Student cannot access other student's data")
    void studentAccess() {
        FakeAccessChecker ac = new FakeAccessChecker();
        assertFalse(ac.canViewStudent("stu1", "stu2"));
    }

    @Test
    @DisplayName("Instructor cannot edit other instructor's section")
    void instructorWrongSection() {
        FakeAccessChecker ac = new FakeAccessChecker();
        assertFalse(ac.canEditSection("inst2", "SEC101"));
    }
}

