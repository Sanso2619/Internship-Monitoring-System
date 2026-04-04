package mypack.ui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import mypack.db.DBConnection;
import mypack.exception.ApplicationException;

public class Login extends JFrame {

    JTextField emailField;
    JPasswordField passwordField;

    public Login() {

        setTitle("Login");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 🔵 TITLE
        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // 🔵 EMAIL
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // 🔵 PASSWORD
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // 🔵 LOGIN BUTTON
        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        // 🔵 SIGNUP BUTTON
        JButton signupBtn = new JButton("Create Account");
        gbc.gridy++;
        panel.add(signupBtn, gbc);

        add(panel);

        // 🔥 ACTIONS
        loginBtn.addActionListener(e -> login());

        signupBtn.addActionListener(e -> {
    try {
        new Signup().setVisible(true);  // 🔥 open first
        this.dispose();                 // 🔥 then close login
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Signup failed to open!");
    }
});

        setVisible(true);
    }

    // ================= LOGIN FUNCTION =================

    private void login() {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {

            // 🔴 VALIDATION
            if (email.isEmpty() || password.isEmpty()) {
                throw new ApplicationException("All fields are required!");
            }

            if (!email.contains("@")) {
                throw new ApplicationException("Invalid email format!");
            }

            Connection conn = DBConnection.getConnection();

            // 🔥 FETCH ROLE ALSO
            String query = "SELECT user_id, role FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("user_id");
                String role = rs.getString("role");

                // 🔥 CLEAN ROLE
                role = role.trim().toLowerCase();

                JOptionPane.showMessageDialog(this, "Login Successful!");

                // 🔥 DEBUG (optional)
                System.out.println("UserID: " + userId);
                System.out.println("Role: " + role);

                // ================= OPEN DASHBOARD =================

                if (role.equals("student")) {
                    new Dashboard(userId);
                }
                else if (role.equals("company")) {
                    new CompanyDashboard(userId);
                }
                else if (role.equals("admin")) {
                    new AdminDashboard();
                }
                else {
                    JOptionPane.showMessageDialog(this, "Unknown role!");
                    return;
                }

                // 🔥 CLOSE LOGIN WINDOW
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }

        } catch (ApplicationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!");
        }
    }
}