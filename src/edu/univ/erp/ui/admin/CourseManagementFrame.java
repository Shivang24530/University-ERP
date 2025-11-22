package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import edu.univ.erp.service.adminService;
import edu.univ.erp.data.CourseDAO; // We need this to get the course list
import edu.univ.erp.domain.Course; // To hold the course data

public class CourseManagementFrame {

    private JFrame frame;
    private adminService service;
    
    // Components for "Create Course"
    private JTextField courseCodeText;
    private JTextField courseTitleText;
    private JTextField creditsText;

    // Components for "Create Section"
    private JComboBox<Course> courseCombo;
    private JTextField dayTimeText;
    private JTextField roomText;
    private JTextField capacityText;
    private JTextField semesterText;
    private JTextField yearText;

    public CourseManagementFrame() {
        this.service = new adminService();

        frame = new JFrame("Course & Section Management");
        frame.setSize(800, 450);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton backButton = new JButton("Back");
        backButton.setBounds(710, 10, 70, 25);
        frame.add(backButton);
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new adminDashboard();
                frame.dispose();
            }
        });

        // --- Panel 1: Create New Course ---
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(null);
        coursePanel.setBorder(BorderFactory.createTitledBorder("Create New Base Course (e.g., CSE201)"));
        coursePanel.setBounds(20, 50, 360, 340);
        frame.add(coursePanel);

        JLabel codeLabel = new JLabel("Course Code:");
        codeLabel.setBounds(20, 40, 100, 30);
        coursePanel.add(codeLabel);
        courseCodeText = new JTextField();
        courseCodeText.setBounds(130, 40, 210, 30);
        coursePanel.add(courseCodeText);

        JLabel titleLabel = new JLabel("Course Title:");
        titleLabel.setBounds(20, 90, 100, 30);
        coursePanel.add(titleLabel);
        courseTitleText = new JTextField();
        courseTitleText.setBounds(130, 90, 210, 30);
        coursePanel.add(courseTitleText);

        JLabel creditsLabel = new JLabel("Credits:");
        creditsLabel.setBounds(20, 140, 100, 30);
        coursePanel.add(creditsLabel);
        creditsText = new JTextField();
        creditsText.setBounds(130, 140, 210, 30);
        coursePanel.add(creditsText);

        JButton createCourseButton = new JButton("Create Course");
        createCourseButton.setBounds(130, 200, 210, 30);
        coursePanel.add(createCourseButton);

        // --- Panel 2: Create New Section ---
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(null);
        sectionPanel.setBorder(BorderFactory.createTitledBorder("Create New Section (e.g., Monsoon 2025)"));
        sectionPanel.setBounds(400, 50, 380, 340);
        frame.add(sectionPanel);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setBounds(20, 40, 100, 30);
        sectionPanel.add(courseLabel);
        courseCombo = new JComboBox<Course>(); // Will be populated
        courseCombo.setBounds(130, 40, 230, 30);
        sectionPanel.add(courseCombo);

        JLabel dayTimeLabel = new JLabel("Day/Time:");
        dayTimeLabel.setBounds(20, 90, 100, 30);
        sectionPanel.add(dayTimeLabel);
        dayTimeText = new JTextField("e.g., Tue/Thu 15:00");
        dayTimeText.setBounds(130, 90, 230, 30);
        sectionPanel.add(dayTimeText);

        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setBounds(20, 140, 100, 30);
        sectionPanel.add(roomLabel);
        roomText = new JTextField();
        roomText.setBounds(130, 140, 230, 30);
        sectionPanel.add(roomText);

        JLabel capLabel = new JLabel("Capacity:");
        capLabel.setBounds(20, 190, 100, 30);
        sectionPanel.add(capLabel);
        capacityText = new JTextField();
        capacityText.setBounds(130, 190, 230, 30);
        sectionPanel.add(capacityText);

        JLabel semLabel = new JLabel("Semester:");
        semLabel.setBounds(20, 240, 100, 30);
        sectionPanel.add(semLabel);
        semesterText = new JTextField("e.g., Monsoon");
        semesterText.setBounds(130, 240, 110, 30);
        sectionPanel.add(semesterText);
        
        yearText = new JTextField("e.g., 2025");
        yearText.setBounds(250, 240, 110, 30);
        sectionPanel.add(yearText);

        JButton createSectionButton = new JButton("Create Section");
        createSectionButton.setBounds(130, 290, 230, 30);
        sectionPanel.add(createSectionButton);

        frame.setVisible(true);

        // --- Action Listeners ---
        
        // Create Course Button
        createCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Get data from form
                String code = courseCodeText.getText();
                String title = courseTitleText.getText();
                String creditsStr = creditsText.getText();

                // 2. Validate
                if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all course fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int credits;
                try {
                    credits = Integer.parseInt(creditsStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Credits must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 3. Call service
                String error = service.createNewCourse(code, title, credits);

                // 4. Show result
                if (error == null) {
                    JOptionPane.showMessageDialog(frame, "Course '" + code + "' created successfully!");
                    courseCodeText.setText("");
                    courseTitleText.setText("");
                    creditsText.setText("");
                    // Refresh the dropdown list in the *other* panel
                    populateCourseComboBox();
                } else {
                    JOptionPane.showMessageDialog(frame, error, "Creation Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create Section Button
        createSectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Get data from form
                Course selectedCourse = (Course) courseCombo.getSelectedItem();
                String dayTime = dayTimeText.getText();
                String room = roomText.getText();
                String capStr = capacityText.getText();
                String semester = semesterText.getText();
                String yearStr = yearText.getText();
                
                // 2. Validate
                if (selectedCourse == null || dayTime.isEmpty() || room.isEmpty() || capStr.isEmpty() || semester.isEmpty() || yearStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all section fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int capacity;
                int year;
                try {
                    capacity = Integer.parseInt(capStr);
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Capacity and Year must be numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int courseId = selectedCourse.getCourseId(); // Get ID from the object

                // 3. Call service
                String error = service.createNewSection(courseId, dayTime, room, capacity, semester, year);

                // 4. Show result
                if (error == null) {
                    JOptionPane.showMessageDialog(frame, "Section for " + selectedCourse.getCode() + " created!");
                    // Clear fields
                    dayTimeText.setText("");
                    roomText.setText("");
                    capacityText.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, error, "Creation Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- Load Initial Data ---
        populateCourseComboBox();
    }

    /**
     * --- NEW METHOD ---
     * Fetches the list of all courses and populates the JComboBox.
     */
    private void populateCourseComboBox() {
        // We need a new CourseDAO instance just for this
        CourseDAO dao = new CourseDAO();
        List<Course> courses = dao.getAllCourses();
        
        // Clear the old list
        courseCombo.removeAllItems();
        
        // Add all courses
        if (courses.isEmpty()) {
            courseCombo.addItem(new Course(-1, "No courses found", "Create one first", 0));
        } else {
            for (Course course : courses) {
                // The JComboBox will use the course.toString() method
                courseCombo.addItem(course);
            }
        }
    }
}