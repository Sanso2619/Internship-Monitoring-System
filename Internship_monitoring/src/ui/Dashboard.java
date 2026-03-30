package ui;

import dao.*;
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    JPanel centerPanel;

    StudentDAO studentDAO = new StudentDAO();
    InternshipDAO internshipDAO = new InternshipDAO();
    ApplicationDAO applicationDAO = new ApplicationDAO();

    int studentId = 1;

    public Dashboard() {

        setTitle("Internship Monitoring System");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔥 HEADER
        JLabel title = new JLabel("Internship Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(33, 150, 243));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // 🔥 SIDE PANEL
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(6, 1, 10, 10));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setBackground(new Color(240, 240, 240));

        JButton btnProfile = createButton("Profile");
        JButton btnSkills = createButton("Skills");
        JButton btnInternships = createButton("Internships");
        JButton btnRecommend = createButton("Recommendations");
        JButton btnApply = createButton("Apply");
        JButton btnApplications = createButton("Applications");

        sidePanel.add(btnProfile);
        sidePanel.add(btnSkills);
        sidePanel.add(btnInternships);
        sidePanel.add(btnRecommend);
        sidePanel.add(btnApply);
        sidePanel.add(btnApplications);

        add(sidePanel, BorderLayout.WEST);

        // 🔥 CENTER PANEL (MAIN CONTENT AREA)
        centerPanel = new JPanel(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);

        // ===============================
        // 🔥 BUTTON ACTIONS
        // ===============================

        // 👤 PROFILE
        btnProfile.addActionListener(e -> {
            showText(studentDAO.getStudentProfile(studentId));
        });

        // 🧠 SKILLS
        btnSkills.addActionListener(e -> {
            showText(studentDAO.getStudentSkillsString(studentId));
        });

        // 💼 INTERNSHIPS (TABLE)
        btnInternships.addActionListener(e -> {
            String[] columns = {"Title", "Company", "Stipend"};
            String[][] data = internshipDAO.getInternshipsTableData();
            showTable(columns, data);
        });

        // 🔥 RECOMMENDATIONS
        btnRecommend.addActionListener(e -> {
            showText(applicationDAO.getRecommendationsString(studentId));
        });

        // 🧾 APPLY
        btnApply.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter Internship ID:");

            if (input != null) {
                int internshipId = Integer.parseInt(input);
                showText(applicationDAO.applyInternshipString(studentId, internshipId));
            }
        });

        // 📊 APPLICATIONS
        btnApplications.addActionListener(e -> {
            showText(applicationDAO.getApplicationsString());
        });

        setVisible(true);
    }

    // 🔥 BUTTON STYLE
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 🔥 SHOW TEXT (FOR PROFILE, SKILLS, ETC.)
    private void showText(String text) {
        centerPanel.removeAll();

        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(new JScrollPane(area), BorderLayout.CENTER);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    // 🔥 SHOW TABLE (FOR INTERNSHIPS)
    private void showTable(String[] columns, String[][] data) {
        centerPanel.removeAll();

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.revalidate();
        centerPanel.repaint();
    }
}