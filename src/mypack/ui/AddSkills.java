package mypack.ui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import mypack.db.DBConnection;

public class AddSkills extends JFrame {

    private int studentId;

    JComboBox<String> skillBox;
    JTextField levelField;
    JTextArea displayArea;

    public AddSkills(int studentId) {

        this.studentId = studentId;

        setTitle("Manage Skills");
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ========== TOP PANEL - ADD SKILL ==========
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Add a Skill"));
        topPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // SKILL DROPDOWN
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Select Skill:"), gbc);

        skillBox = new JComboBox<>();
        loadAvailableSkills(); // load from DB
        gbc.gridx = 1;
        topPanel.add(skillBox, gbc);

        // LEVEL
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Level (1-10):"), gbc);

        levelField = new JTextField(10);
        gbc.gridx = 1;
        topPanel.add(levelField, gbc);

        // ADD BUTTON
        JButton addBtn = new JButton("Add Skill");
        addBtn.setBackground(new Color(33, 150, 243));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        topPanel.add(addBtn, gbc);

        add(topPanel, BorderLayout.NORTH);

        // ========== CENTER - MY SKILLS LIST ==========
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("My Current Skills"));

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        centerPanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ========== BOTTOM - REMOVE + BACK ==========
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JTextField removeField = new JTextField(15);
        removeField.setToolTipText("Enter skill name to remove");

        JButton removeBtn = new JButton("Remove Skill");
        removeBtn.setBackground(new Color(244, 67, 54));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(new Color(76, 175, 80));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        bottomPanel.add(new JLabel("Remove Skill:"));
        bottomPanel.add(removeField);
        bottomPanel.add(removeBtn);
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // ========== ACTIONS ==========

        addBtn.addActionListener(e -> addSkill());

        removeBtn.addActionListener(e -> removeSkill(removeField.getText().trim()));

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(studentId);
        });

        // load current skills on open
        loadMySkills();

        setVisible(true);
    }

    // ========== LOAD ALL SKILLS FROM skills TABLE ==========
    private void loadAvailableSkills() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT skill_name FROM skills ORDER BY skill_name";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            skillBox.removeAllItems();
            while (rs.next()) {
                skillBox.addItem(rs.getString("skill_name"));
            }

            if (skillBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No skills found in database!\nAsk admin to add skills first.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading skills: " + e.getMessage());
        }
    }

    // ========== LOAD STUDENT'S CURRENT SKILLS ==========
    private void loadMySkills() {
        try {
            Connection conn = DBConnection.getConnection();
            String query =
                "SELECT sk.skill_name, ss.level " +
                "FROM student_skills ss " +
                "JOIN skills sk ON ss.skill_id = sk.skill_id " +
                "WHERE ss.student_id = ? " +
                "ORDER BY sk.skill_name";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            boolean found = false;

            while (rs.next()) {
                found = true;
                sb.append("• ")
                  .append(rs.getString("skill_name"))
                  .append("  →  Level ")
                  .append(rs.getInt("level"))
                  .append("\n");
            }

            if (!found) {
                sb.append("No skills added yet. Use the form above to add skills!");
            }

            displayArea.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            displayArea.setText("Error loading your skills!");
        }
    }

    // ========== ADD SKILL ==========
    private void addSkill() {

        String selectedSkill = (String) skillBox.getSelectedItem();
        String levelText = levelField.getText().trim();

        // VALIDATION
        if (selectedSkill == null || selectedSkill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a skill!");
            return;
        }

        if (levelText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a level!");
            return;
        }

        int level;
        try {
            level = Integer.parseInt(levelText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Level must be a number between 1 and 10!");
            return;
        }

        if (level < 1 || level > 10) {
            JOptionPane.showMessageDialog(this, "Level must be between 1 and 10!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            // GET skill_id from skill_name
            String getIdQuery = "SELECT skill_id FROM skills WHERE skill_name = ?";
            PreparedStatement ps1 = conn.prepareStatement(getIdQuery);
            ps1.setString(1, selectedSkill);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Skill not found in database!");
                return;
            }

            int skillId = rs.getInt("skill_id");

            // CHECK if already added
            String checkQuery = "SELECT * FROM student_skills WHERE student_id = ? AND skill_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(checkQuery);
            ps2.setInt(1, studentId);
            ps2.setInt(2, skillId);
            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                // UPDATE level if already exists
                String updateQuery = "UPDATE student_skills SET level = ? WHERE student_id = ? AND skill_id = ?";
                PreparedStatement ps3 = conn.prepareStatement(updateQuery);
                ps3.setInt(1, level);
                ps3.setInt(2, studentId);
                ps3.setInt(3, skillId);
                ps3.executeUpdate();
                JOptionPane.showMessageDialog(this, "Skill level updated to " + level + "!");
            } else {
                // INSERT new skill
                String insertQuery = "INSERT INTO student_skills(student_id, skill_id, level) VALUES (?, ?, ?)";
                PreparedStatement ps3 = conn.prepareStatement(insertQuery);
                ps3.setInt(1, studentId);
                ps3.setInt(2, skillId);
                ps3.setInt(3, level);
                ps3.executeUpdate();
                JOptionPane.showMessageDialog(this, selectedSkill + " added successfully!");
            }

            levelField.setText("");
            loadMySkills(); // refresh list

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    // ========== REMOVE SKILL ==========
    private void removeSkill(String skillName) {

        if (skillName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the skill name to remove!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            // GET skill_id
            String getIdQuery = "SELECT skill_id FROM skills WHERE skill_name = ?";
            PreparedStatement ps1 = conn.prepareStatement(getIdQuery);
            ps1.setString(1, skillName);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Skill '" + skillName + "' not found!");
                return;
            }

            int skillId = rs.getInt("skill_id");

            // DELETE
            String deleteQuery = "DELETE FROM student_skills WHERE student_id = ? AND skill_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(deleteQuery);
            ps2.setInt(1, studentId);
            ps2.setInt(2, skillId);

            int rows = ps2.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Skill removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "You don't have this skill!");
            }

            loadMySkills(); // refresh list

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
}