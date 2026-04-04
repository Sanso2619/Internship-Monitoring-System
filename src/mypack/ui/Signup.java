package mypack.ui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import mypack.db.DBConnection;

public class Signup extends JFrame {

    JTextField nameField, emailField;
    JPasswordField passwordField;
    JComboBox<String> roleBox;

    public Signup() {

        setTitle("Signup");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // NAME
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(new JLabel("Name:"), gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        add(nameField, gbc);

        // EMAIL
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Email:"), gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        add(emailField, gbc);

        // PASSWORD
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // ROLE SELECTION ✅ admin added
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Role:"), gbc);

        roleBox = new JComboBox<>(new String[]{"student", "company", "admin"}); // ✅ admin added
        gbc.gridx = 1;
        add(roleBox, gbc);

        // SIGNUP BUTTON
        JButton signupBtn = new JButton("Signup");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(signupBtn, gbc);

        // BACK TO LOGIN BUTTON
        JButton backBtn = new JButton("Back to Login");
        gbc.gridy++;
        add(backBtn, gbc);

        signupBtn.addActionListener(e -> signup());

        backBtn.addActionListener(e -> {
            dispose();
            new Login();
        });

        setVisible(true);
    }

    private void signup() {

        String name = nameField.getText().trim().toUpperCase();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleBox.getSelectedItem().toString();

        // ✅ VALIDATION - shown BEFORE any DB call
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!");
            return;
        }

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to database! Check DBConnection.");
                return;
            }

            // ✅ TEST: print to confirm button is working
            System.out.println("Signup clicked! Name=" + name + " Email=" + email + " Role=" + role);

            // INSERT INTO USERS
            String query = "INSERT INTO users(name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);

            int rows = ps.executeUpdate();

            System.out.println("Rows inserted into users: " + rows); // ✅ debug

            ResultSet rs = ps.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            System.out.println("Generated userId: " + userId); // ✅ debug

            // ROLE BASED INSERT
            if (role.equals("student")) {

                String studentQuery =
                    "INSERT INTO students(student_id, cgpa, college, graduation_year) " +
                    "VALUES (?, 0.0, 'Not Set', 2025)";

                PreparedStatement ps2 = conn.prepareStatement(studentQuery);
                ps2.setInt(1, userId);
                ps2.executeUpdate();
                System.out.println("Student row inserted!"); // ✅ debug

            } else if (role.equals("company")) {

                String companyQuery =
                    "INSERT INTO companies(company_id, company_name, location, industry) " +
                    "VALUES (?, ?, 'Not Set', 'Not Set')";

                PreparedStatement ps2 = conn.prepareStatement(companyQuery);
                ps2.setInt(1, userId);
                ps2.setString(2, name);
                ps2.executeUpdate();
                System.out.println("Company row inserted!"); // ✅ debug

            } else if (role.equals("admin")) {
                // ✅ admin only needs users table entry, no extra table needed
                System.out.println("Admin user created - no extra table needed.");
            }

            JOptionPane.showMessageDialog(this, "Signup Successful!");
            dispose();
            new Login();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Email already exists! Use a different email.");
            e.printStackTrace();
        } catch (SQLException e) {
            // ✅ shows exact SQL error so you know what failed
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // ✅ shows exact error instead of hiding it
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}