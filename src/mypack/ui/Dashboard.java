package mypack.ui;

import mypack.dao.*;
import mypack.exception.ApplicationException;

import java.awt.*;
import javax.swing.*;

public class Dashboard extends JFrame {

    JPanel centerPanel;

    StudentDAO studentDAO = new StudentDAO();
    InternshipDAO internshipDAO = new InternshipDAO();
    ApplicationDAO applicationDAO = new ApplicationDAO();

    int studentId;

    public Dashboard(int studentId) {
        this.studentId = studentId;

        setTitle("Internship Monitoring System");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Internship Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(33, 150, 243));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel sidePanel = new JPanel(new GridLayout(6, 1, 10, 10));
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

        centerPanel = new JPanel(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);

        // ACTIONS

        btnProfile.addActionListener(e ->
            showText(studentDAO.getStudentProfile(studentId))
        );

        btnSkills.addActionListener(e ->
            showText(studentDAO.getStudentSkillsString(studentId))
        );

        btnInternships.addActionListener(e -> {
            String[] columns = {"Title", "Company", "Stipend"};
            String[][] data = internshipDAO.getInternshipsTableData();
            showTable(columns, data);
        });

        btnRecommend.addActionListener(e ->
            showText(applicationDAO.getRecommendationsString(studentId))
        );

        btnApply.addActionListener(e -> {
            try {
                String input = JOptionPane.showInputDialog("Enter Internship ID:");

                if (input == null || input.trim().isEmpty()) {
                    throw new ApplicationException("Internship ID cannot be empty!");
                }

                int internshipId = Integer.parseInt(input);

                showText(applicationDAO.applyInternshipString(studentId, internshipId));

            } catch (ApplicationException ex) {
                showText("Custom Error: " + ex.getMessage());
            } catch (Exception ex) {
                showText("Invalid Input!");
            }
        });

        btnApplications.addActionListener(e ->
            showText(applicationDAO.getApplicationsString(studentId))
        );

        setVisible(true);
    }

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

    private void showText(String text) {
        centerPanel.removeAll();

        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);

        centerPanel.add(new JScrollPane(area), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void showTable(String[] columns, String[][] data) {
        centerPanel.removeAll();

        JTable table = new JTable(data, columns);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        centerPanel.revalidate();
        centerPanel.repaint();
    }
}