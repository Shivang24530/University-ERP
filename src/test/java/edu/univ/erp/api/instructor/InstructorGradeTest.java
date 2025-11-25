package test.java.edu.univ.erp.api.instructor;

import test.java.edu.univ.erp.fakes.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class InstructorGradeTest {

    private FakeERPDatabase db;

    @BeforeEach
    void setup() {
        db = new FakeERPDatabase();
    }

    @Test
    @DisplayName("Instructor can enter scores")
    void enterScores() {
        db.saveScore("stu1", "SEC101", "quiz", 20);
        assertEquals(20, db.getScore("stu1", "SEC101", "quiz"));
    }

    @Test
    @DisplayName("Compute final grade (20/30/50 rule)")
    void computeFinalGrade() {
        db.saveScore("stu1", "SEC101", "quiz", 20);
        db.saveScore("stu1", "SEC101", "mid", 25);
        db.saveScore("stu1", "SEC101", "endsem", 45);

        double finalGrade = 
              20 * 0.2
            + 25 * 0.3
            + 45 * 0.5;

        assertEquals(finalGrade, 20*0.2 + 25*0.3 + 45*0.5);
    }
}
