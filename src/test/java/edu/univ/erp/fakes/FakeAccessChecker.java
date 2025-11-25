package test.java.edu.univ.erp.fakes;

public class FakeAccessChecker {

    public boolean canViewStudent(String who, String target) {
        return who.equals(target);
    }

    public boolean canEditSection(String instructorId, String sectionId) {
        return instructorId.equals("inst1") && sectionId.equals("SEC101");
    }
}

