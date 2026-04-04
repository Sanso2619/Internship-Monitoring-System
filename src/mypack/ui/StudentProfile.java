package mypack.ui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import mypack.db.DBConnection;

public class StudentProfile extends JFrame {

    private int studentId;

    JTextField cgpaField, collegeField, graduationField;

    public StudentProfile(int studentId) {

        this.studentId = studentId;

        setTitle("My Profile");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TITLE
        JLabel title = new JLabel("My Profile", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // NAME (read only)
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(new JLabel("Name:"), gbc);

        JLabel nameLabel = new JLabel("Loading...");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        add(nameLabel, gbc);

        // EMAIL (read only)
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Email:"), gbc);

        JLabel emailLabel = new JLabel("Loading...");
        gbc.gridx = 1;
        add(emailLabel, gbc);

        // CGPA
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("CGPA:"), gbc);

        cgpaField = new JTextField(15);
        gbc.gridx = 1;
        add(cgpaField, gbc);

        // COLLEGE
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("College:"), gbc);

        collegeField = new JTextField(15);
        gbc.gridx = 1;
        add(collegeField, gbc);

        // GRADUATION YEAR
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Graduation Year:"), gbc);

        graduationField = new JTextField(15);
        gbc.gridx = 1;
        add(graduationField, gbc);

        // UPDATE BUTTON
        JButton updateBtn = new JButton("Update Profile");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(updateBtn, gbc);

        // BACK BUTTON
        JButton backBtn = new JButton("Back to Dashboard");
        gbc.gridy++;
        add(backBtn, gbc);

        // LOAD existing data
        loadProfile(nameLabel, emailLabel);

        // ACTIONS
        updateBtn.addActionListener(e -> updateProfile());

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(studentId);
        });

        setVisible(true);
    }

    // ================= LOAD PROFILE =================
    private void loadProfile(JLabel nameLabel, JLabel emailLabel) {

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT u.name, u.email, s.cgpa, s.college, s.graduation_year " +
                           "FROM users u JOIN students s ON u.user_id = s.student_id " +
                           "WHERE u.user_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameLabel.setText(rs.getString("name"));
                emailLabel.setText(rs.getString("email"));
                cgpaField.setText(String.valueOf(rs.getDouble("cgpa")));
                collegeField.setText(rs.getString("college"));
                graduationField.setText(String.valueOf(rs.getInt("graduation_year")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    // ================= UPDATE PROFILE =================
    private void updateProfile() {

        String cgpaText = cgpaField.getText().trim();
        String college = collegeField.getText().trim();
        String gradText = graduationField.getText().trim();

        // VALIDATION
        if (cgpaText.isEmpty() || college.isEmpty() || gradText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        double cgpa;
        int gradYear;

        try {
            cgpa = Double.parseDouble(cgpaText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "CGPA must be a number! Example: 8.5");
            return;
        }

        try {
            gradYear = Integer.parseInt(gradText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Graduation Year must be a number! Example: 2025");
            return;
        }

        if (cgpa < 0.0 || cgpa > 10.0) {
            JOptionPane.showMessageDialog(this, "CGPA must be between 0.0 and 10.0!");
            return;
        }

        if (gradYear < 2000 || gradYear > 2035) {
            JOptionPane.showMessageDialog(this, "Enter a valid Graduation Year!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            String query = "UPDATE students SET cgpa = ?, college = ?, graduation_year = ? " +
                           "WHERE student_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, cgpa);
            ps.setString(2, college);
            ps.setInt(3, gradYear);
            ps.setInt(4, studentId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed! Student not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
}