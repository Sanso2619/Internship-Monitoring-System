package mypack.ui;

import java.awt.*;
import javax.swing.*;
import mypack.dao.CompanyDAO;

public class CompanyDashboard extends JFrame {

    int companyId;
    CompanyDAO dao;

    JTable applicantsTable;
    JTable internshipsTable;

    JPanel applicantsPanel;
    JPanel internshipsPanel;

    JTabbedPane tabs;

    public CompanyDashboard(int companyId) {

        this.companyId = companyId;

        try {
            dao = new CompanyDAO();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "DAO Error: " + e.getMessage());
            return;
        }

        setTitle("Company Dashboard");
        setSize(900, 550);
        setLayout(new BorderLayout());

        // 🔵 HEADER
        JLabel titleLabel = new JLabel("Company Panel", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(33, 150, 243));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 🔵 TABBED PANE
        tabs = new JTabbedPane();

        internshipsPanel = new JPanel(new BorderLayout());
        tabs.addTab("My Internships", internshipsPanel);

        applicantsPanel = new JPanel(new BorderLayout());
        tabs.addTab("Applicants", applicantsPanel);

        add(tabs, BorderLayout.CENTER);

        // 🔵 BUTTON PANEL
        JPanel bottom = new JPanel();

        JButton btnAdd          = new JButton("Add Internship");
        JButton btnAutoEvaluate = new JButton("Auto Evaluate (CGPA)");
        JButton btnShortlist    = new JButton("Shortlist");
        JButton btnReject       = new JButton("Reject");
        JButton btnRefresh      = new JButton("Refresh");
        JButton btnDelete       = new JButton("Remove Internship");

        bottom.add(btnAdd);
        bottom.add(btnDelete);
        bottom.add(btnAutoEvaluate);
        bottom.add(btnShortlist);
        bottom.add(btnReject);
        bottom.add(btnRefresh);

        add(bottom, BorderLayout.SOUTH);

        // 🔥 LOAD BOTH ON STARTUP
        loadInternships();
        loadApplicants();

        // ================= ACTIONS =================

        btnRefresh.addActionListener(e -> {
            loadInternships();
            loadApplicants();
        });

        // 🔥 AUTO EVALUATE — only works from My Internships tab
        btnAutoEvaluate.addActionListener(e -> autoEvaluate());

        btnDelete.addActionListener(e -> deleteInternship());
        
        // 🔥 SHORTLIST — manual, works from Applicants tab
        btnShortlist.addActionListener(e -> handleStatusUpdate("shortlisted"));

        // 🔥 REJECT — manual, works from Applicants tab
        btnReject.addActionListener(e -> handleStatusUpdate("rejected"));

        // 🔥 ADD INTERNSHIP
        btnAdd.addActionListener(e -> {
            try {
                String internshipTitle = JOptionPane.showInputDialog(this, "Enter Internship Title:");
                if (internshipTitle == null || internshipTitle.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title cannot be empty!");
                    return;
                }

                String cgpaInput = JOptionPane.showInputDialog(this, "Min CGPA (0.0 - 10.0):");
                if (cgpaInput == null) return;
                double cgpa = Double.parseDouble(cgpaInput.trim());

                if (cgpa < 0 || cgpa > 10) {
                    JOptionPane.showMessageDialog(this, "CGPA must be between 0 and 10!");
                    return;
                }

                String stipendInput = JOptionPane.showInputDialog(this, "Stipend (monthly):");
                if (stipendInput == null) return;
                int stipend = Integer.parseInt(stipendInput.trim());

                if (stipend < 0) {
                    JOptionPane.showMessageDialog(this, "Stipend cannot be negative!");
                    return;
                }

                String result = dao.addInternship(companyId, internshipTitle, cgpa, stipend);
                JOptionPane.showMessageDialog(this, result);

                loadInternships();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for CGPA or Stipend!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ================= AUTO EVALUATE BY CGPA =================

    private void autoEvaluate() {
        try {
            // 🔥 Must be on My Internships tab
            if (tabs.getSelectedIndex() != 0) {
                JOptionPane.showMessageDialog(this,
                    "Please go to 'My Internships' tab and select an internship first!");
                return;
            }

            if (internshipsTable == null) {
                JOptionPane.showMessageDialog(this, "No internships loaded!");
                return;
            }

            int row = internshipsTable.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                    "Select an internship from the list to auto-evaluate!");
                return;
            }

            int internshipId = Integer.parseInt(
                internshipsTable.getValueAt(row, 0).toString()
            );
            String internshipTitle = internshipsTable.getValueAt(row, 1).toString();
            String minCgpa = internshipsTable.getValueAt(row, 2).toString();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Auto-evaluate applicants for '" + internshipTitle + "'?\n" +
                "Min CGPA required: " + minCgpa + "\n\n" +
                "✅ Students with CGPA >= " + minCgpa + " → Shortlisted\n" +
                "❌ Students with CGPA < "  + minCgpa + " → Rejected",
                "Confirm Auto Evaluate",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            String result = dao.autoShortlistByInternship(internshipId);
            JOptionPane.showMessageDialog(this, result);

            // 🔥 Refresh both and switch to Applicants to see result
            loadInternships();
            loadApplicants();
            tabs.setSelectedIndex(1);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ================= MANUAL STATUS HANDLER =================

    private void handleStatusUpdate(String status) {

        // 🔥 Manual shortlist/reject only on Applicants tab
        if (tabs.getSelectedIndex() != 1) {
            JOptionPane.showMessageDialog(this,
                "Go to 'Applicants' tab to manually shortlist or reject.\n" +
                "Or use 'Auto Evaluate' on the Internships tab!");
            return;
        }

        updateStatusByApplication(status);
    }

    // ================= UPDATE SINGLE APPLICANT =================

    private void updateStatusByApplication(String status) {
        try {
            if (applicantsTable == null) {
                JOptionPane.showMessageDialog(this, "No applicants loaded!");
                return;
            }

            int row = applicantsTable.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an application first!");
                return;
            }

            String studentName = applicantsTable.getValueAt(row, 1).toString();
            String internship  = applicantsTable.getValueAt(row, 2).toString();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Mark " + studentName + " applying for '" + internship + "' as " + status + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            int appId = Integer.parseInt(applicantsTable.getValueAt(row, 0).toString());

            String result = dao.updateApplicationStatus(appId, status);
            JOptionPane.showMessageDialog(this, result);

            loadApplicants();
            tabs.setSelectedIndex(1);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ================= LOAD INTERNSHIPS =================

    private void loadInternships() {
        try {
            String[] columns = {"Internship ID", "Title", "Min CGPA", "Stipend"};
            String[][] data = dao.getInternshipsTableData(companyId);

            internshipsPanel.removeAll();
            internshipsTable = new JTable(data, columns);
            internshipsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            internshipsPanel.add(new JScrollPane(internshipsTable), BorderLayout.CENTER);
            internshipsPanel.revalidate();
            internshipsPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading internships: " + e.getMessage());
        }
    }
    private void deleteInternship() {
    try {

        // 🔴 Must be on internships tab
        if (tabs.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(this,
                "Go to 'My Internships' tab to remove internship!");
            return;
        }

        if (internshipsTable == null) {
            JOptionPane.showMessageDialog(this, "No internships loaded!");
            return;
        }

        int row = internshipsTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an internship first!");
            return;
        }

        int internshipId = Integer.parseInt(
            internshipsTable.getValueAt(row, 0).toString()
        );

        String title = internshipsTable.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete '" + title + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String result = dao.deleteInternship(internshipId);

        JOptionPane.showMessageDialog(this, result);

        loadInternships(); // 🔄 refresh

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

    // ================= LOAD APPLICANTS =================

    private void loadApplicants() {
        try {
            String[] columns = {"App ID", "Student", "Internship", "Status"};
            String[][] data = dao.getApplicantsTableData(companyId);

            applicantsPanel.removeAll();
            applicantsTable = new JTable(data, columns);
            applicantsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            applicantsPanel.add(new JScrollPane(applicantsTable), BorderLayout.CENTER);
            applicantsPanel.revalidate();
            applicantsPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading applicants: " + e.getMessage());
        }
    }
}