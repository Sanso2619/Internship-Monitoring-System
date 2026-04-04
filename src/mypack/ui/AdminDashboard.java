package mypack.ui;

import javax.swing.*;
import java.awt.*;
import mypack.dao.AdminDAO;

public class AdminDashboard extends JFrame {

    AdminDAO dao = new AdminDAO();
    JPanel center;

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(900, 550);
        setLayout(new BorderLayout());

        // 🔵 HEADER
        JLabel title = new JLabel("Admin Panel - Placement Monitoring", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(new Color(33,150,243));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // 🔵 SIDE PANEL
        JPanel side = new JPanel(new GridLayout(6,1,10,10));
        side.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton btnStats = new JButton("System Stats");
        JButton btnUnplaced = new JButton("Unplaced Students");
        JButton btnRisk = new JButton("Risk Alerts");
        JButton btnInactive = new JButton("Inactive Companies");
        JButton btnAll = new JButton("All Applications"); // ⭐ MAIN FEATURE
        JButton btnRefresh = new JButton("Refresh");

        side.add(btnStats);
        side.add(btnUnplaced);
        side.add(btnRisk);
        side.add(btnInactive);
        side.add(btnAll);
        side.add(btnRefresh);

        add(side, BorderLayout.WEST);

        // 🔵 CENTER PANEL
        center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        // ================= ACTIONS =================

        btnStats.addActionListener(e -> showText(dao.getStats()));

        btnUnplaced.addActionListener(e -> showText(dao.getUnplacedStudents()));

        btnRisk.addActionListener(e -> showText(dao.getRiskStudents()));

        btnInactive.addActionListener(e -> showText(dao.getInactiveCompanies()));

        // ⭐ MAIN FEATURE: TABLE VIEW
        btnAll.addActionListener(e -> showAllApplications());

        btnRefresh.addActionListener(e -> {
            center.removeAll();
            center.revalidate();
            center.repaint();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ================= TEXT DISPLAY =================

    private void showText(String text) {

        center.removeAll();

        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);

        center.add(new JScrollPane(area), BorderLayout.CENTER);

        center.revalidate();
        center.repaint();
    }

    // ================= TABLE DISPLAY =================

    private void showAllApplications() {

        try {

            String[] columns = {"Student", "Company", "Internship", "Status"};
            String[][] data = dao.getAllApplicationsTable();

            center.removeAll();

            JTable table = new JTable(data, columns);
            table.setRowHeight(25);

            center.add(new JScrollPane(table), BorderLayout.CENTER);

            center.revalidate();
            center.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading application data!");
        }
    }
}