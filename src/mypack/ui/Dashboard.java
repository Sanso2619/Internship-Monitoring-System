package mypack.ui;

import java.awt.*;
import javax.swing.*;
import mypack.dao.*;
import mypack.exception.ApplicationException;

public class Dashboard extends JFrame {

    JPanel centerPanel;

    StudentDAO studentDAO = new StudentDAO();
    InternshipDAO internshipDAO = new InternshipDAO();
    ApplicationDAO applicationDAO = new ApplicationDAO();

    int studentId;

    JTable internshipTable; // 🔥 important

    public Dashboard(int studentId) {

        this.studentId = studentId;

        setTitle("Internship Monitoring System");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔵 TITLE
        JLabel title = new JLabel("Internship Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(33, 150, 243));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // 🔵 SIDE PANEL
        JPanel sidePanel = new JPanel(new GridLayout(6, 1, 10, 10));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setBackground(new Color(240, 240, 240));

        JButton btnProfile = createButton("Profile");
        JButton btnSkills = createButton("Skills");
        JButton btnInternships = createButton("Internships");
        JButton btnRecommend = createButton("Recommendations");
        JButton btnApply = createButton("Apply Selected");
        JButton btnApplications = createButton("Applications");

        sidePanel.add(btnProfile);
        sidePanel.add(btnSkills);
        sidePanel.add(btnInternships);
        sidePanel.add(btnRecommend);
        sidePanel.add(btnApply);
        sidePanel.add(btnApplications);

        add(sidePanel, BorderLayout.WEST);

        // 🔵 CENTER PANEL
        centerPanel = new JPanel(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);

        // ================= ACTIONS =================

        btnProfile.addActionListener(e ->
            showText(studentDAO.getStudentProfile(studentId))
        );

        btnSkills.addActionListener(e ->
            showText(studentDAO.getStudentSkillsString(studentId))
        );

        // 🔥 SHOW INTERNSHIP TABLE
        btnInternships.addActionListener(e -> {
            String[] columns = {"ID", "Title", "Company", "Stipend"};
            String[][] data = internshipDAO.getInternshipsTableDataWithId();

            centerPanel.removeAll();

            internshipTable = new JTable(data, columns);
            internshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            centerPanel.add(new JScrollPane(internshipTable), BorderLayout.CENTER);

            centerPanel.revalidate();
            centerPanel.repaint();
        });

        btnRecommend.addActionListener(e ->
            showText(applicationDAO.getRecommendationsString(studentId))
        );

        // 🔥 APPLY USING SELECTED ROW
        btnApply.addActionListener(e -> {

    try {

        if (internshipTable == null || internshipTable.getSelectedRow() == -1) {
            throw new ApplicationException("⚠️ Please select an internship first!");
        }

        int row = internshipTable.getSelectedRow();

        // ✅ FIXED: get ID from table
        int internshipId = Integer.parseInt(
            internshipTable.getValueAt(row, 0).toString()
        );

        String result = applicationDAO.applyInternshipString(studentId, internshipId);

        JOptionPane.showMessageDialog(this, result);

    } catch (ApplicationException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid Action!");
    }

       
});

        btnApplications.addActionListener(e ->
            showText(applicationDAO.getApplicationsString(studentId))
        );

        setVisible(true);
        setLocationRelativeTo(null);

         JOptionPane.showMessageDialog(this,
    "💡 Tip: Higher CGPA increases your chances!");
    }

    // 🔵 BUTTON STYLE
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

    // 🔵 TEXT VIEW
    private void showText(String text) {
        centerPanel.removeAll();

        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);

        centerPanel.add(new JScrollPane(area), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    // 🔵 TABLE VIEW
    private void showTable(String[] columns, String[][] data) {
        centerPanel.removeAll();

        JTable table = new JTable(data, columns);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        centerPanel.revalidate();
        centerPanel.repaint();
    }
}