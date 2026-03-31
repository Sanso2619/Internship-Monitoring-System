package mypack.ui;

import mypack.db.DBConnection;
import mypack.exception.ApplicationException;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        String name = nameField.getText().trim().toUpperCase();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                throw new ApplicationException("All fields are required!");
            }

            if (!email.contains("@")) {
                throw new ApplicationException("Invalid email format!");
            }

            Connection conn = DBConnection.getConnection();

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

            String studentQuery = "INSERT INTO students(student_id, cgpa, college, graduation_year) VALUES (?, 0.0, 'Not Set', 2025)";
            PreparedStatement ps2 = conn.prepareStatement(studentQuery);
            ps2.setInt(1, userId);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Signup Successful!");
            dispose();
            new Login();

        } catch (ApplicationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Email already exists!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}