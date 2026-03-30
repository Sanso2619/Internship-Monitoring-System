package ui;

import db.DBConnection;
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

        // TITLE
        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // EMAIL
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // PASSWORD
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // LOGIN BUTTON
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(33, 150, 243));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        // SIGNUP BUTTON
        JButton signupBtn = new JButton("Create Account");
        signupBtn.setFocusPainted(false);
        signupBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy++;
        panel.add(signupBtn, gbc);

        add(panel);

        // ACTIONS
        loginBtn.addActionListener(e -> {
            System.out.println("Button clicked!");
            login();
        });

        signupBtn.addActionListener(e -> {
            dispose();
            new Signup();
        });

        setVisible(true);  // ← THIS WAS MISSING
        requestFocusInWindow();
    }

    private void login() {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both email and password.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            if (conn == null) {
                JOptionPane.showMessageDialog(this,
                    "Could not connect to database. Check DBConnection.java",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "SELECT user_id FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                JOptionPane.showMessageDialog(this,
                    "Login Successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new Dashboard(userId);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid email or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {  // ← catch ALL exceptions, not just SQLException
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}