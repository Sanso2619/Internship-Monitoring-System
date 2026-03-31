package mypack.ui;

import mypack.db.DBConnection;
import mypack.exception.ApplicationException;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

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

        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        JButton signupBtn = new JButton("Create Account");
        gbc.gridy++;
        panel.add(signupBtn, gbc);

        add(panel);

        loginBtn.addActionListener(e -> login());

        signupBtn.addActionListener(e -> {
            dispose();
            new Signup();
        });

        setVisible(true);
    }

    private void login() {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {

            if (email.isEmpty() || password.isEmpty()) {
                throw new ApplicationException("All fields are required!");
            }

            if (!email.contains("@")) {
                throw new ApplicationException("Invalid email format!");
            }

            Connection conn = DBConnection.getConnection();

            String query = "SELECT user_id FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new Dashboard(userId);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }

        } catch (ApplicationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}