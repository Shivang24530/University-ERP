package test.java.edu.univ.erp.fakes;

import java.util.*;

public class FakeERPDatabase {

    private boolean maintenance = false;

    // Map: student -> list of section IDs
    private Map<String, List<String>> enrollments = new HashMap<>();

    // Map: section -> capacity
    private Map<String, Integer> sectionCapacity = new HashMap<>();

    // Scores: (student, section, component) -> score
    private Map<String, Integer> scores = new HashMap<>();

    public FakeERPDatabase() {
        sectionCapacity.put("SEC101", 10);
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean value) {
        this.maintenance = value;
    }

    public void register(String studentId, String sectionId) {
        enrollments.computeIfAbsent(studentId, k -> new ArrayList<>()).add(sectionId);
    }

    public List<String> getEnrollments(String studentId) {
        return enrollments.getOrDefault(studentId, Collections.emptyList());
    }

    public void setSectionCapacity(String sectionId, int cap) {
        sectionCapacity.put(sectionId, cap);
    }

    public int getSectionCapacity(String sectionId) {
        return sectionCapacity.getOrDefault(sectionId, 0);
    }

    public void saveScore(String stu, String sec, String comp, int score) {
        scores.put(stu + "|" + sec + "|" + comp, score);
    }

    public int getScore(String stu, String sec, String comp) {
        return scores.getOrDefault(stu + "|" + sec + "|" + comp, -1);
    }
}
