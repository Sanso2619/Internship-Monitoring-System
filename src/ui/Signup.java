package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Signup extends JFrame {

    JTextField nameField, emailField;
    JPasswordField passwordField;

    public Signup() {

        setTitle("Signup");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 🔥 TITLE
        JLabel title = new JLabel("Signup", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // 🔥 NAME
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("Name:"), gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        add(nameField, gbc);

        // 🔥 EMAIL
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Email:"), gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        add(emailField, gbc);

        // 🔥 PASSWORD
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // 🔥 BUTTON
        JButton signupBtn = new JButton("Signup");

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(signupBtn, gbc);

        signupBtn.addActionListener(e -> signup());

        setVisible(true);
    }

    private void signup() {

        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DBConnection.getConnection();

            // 🔥 INSERT INTO USERS
            String query = "INSERT INTO users(name, email, password, role) VALUES (?, ?, ?, 'student')";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            // 🔥 GET GENERATED USER ID
            ResultSet rs = ps.getGeneratedKeys();
            int userId = 0;

            if (rs.next()) {
                userId = rs.getInt(1);
            }

            // 🔥 INSERT INTO STUDENTS
            String studentQuery = "INSERT INTO students(student_id, cgpa, college, graduation_year) VALUES (?, 0.0, 'Not Set', 2025)";
            PreparedStatement ps2 = conn.prepareStatement(studentQuery);

            ps2.setInt(1, userId);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Signup Successful!");

            dispose();
            new Login();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}