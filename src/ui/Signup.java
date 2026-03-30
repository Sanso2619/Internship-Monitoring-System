package ui;

import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class Signup extends JFrame {

    JTextField nameField, emailField;
    JPasswordField passwordField;

    public Signup() {

        setTitle("Signup");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ✅ FIX 1: Added this
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Signup", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Email:"), gbc);
        emailField = new JTextField(15);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton signupBtn = new JButton("Signup");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(signupBtn, gbc);

        signupBtn.addActionListener(e -> signup());

        setVisible(true);
    }

    private void signup() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // ✅ FIX 2: Validate inputs before hitting the DB
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            // ✅ FIX 3: Check if connection is null (same pattern as Login.java)
            if (conn == null) {
                JOptionPane.showMessageDialog(this,
                    "Could not connect to database. Check DBConnection.java",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO users(name, email, password, role) VALUES (?, ?, ?, 'student')";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            // ✅ FIX 4: Check if userId was actually generated
            if (userId == 0) {
                JOptionPane.showMessageDialog(this,
                    "Signup failed: could not retrieve user ID.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String studentQuery = "INSERT INTO students(student_id, cgpa, college, graduation_year) VALUES (?, 0.0, 'Not Set', 2025)";
            PreparedStatement ps2 = conn.prepareStatement(studentQuery);
            ps2.setInt(1, userId);
            ps2.executeUpdate();

            // ✅ FIX 5: Close resources
            rs.close();
            ps.close();
            ps2.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Signup Successful!");
            dispose();
            new Login();

        } catch (SQLIntegrityConstraintViolationException e) {
            // ✅ FIX 6: Catch duplicate email specifically
            JOptionPane.showMessageDialog(this,
                "That email is already registered. Please use a different email.",
                "Signup Failed",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // ✅ FIX 7: Show the actual error to the user instead of silent failure
            JOptionPane.showMessageDialog(this,
                "Signup error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}