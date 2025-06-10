import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AppGUI extends JFrame implements ActionListener {
    private final JLabel studentIdLabel, firstNameLabel, lastNameLabel, majorLabel, phoneLabel, gpaLabel, dobLabel;
    private final JTextField studentIdField, firstNameField, lastNameField, majorField, phoneField, gpaField, dobField;
    private final JButton addButton, displayButton, sortButton, searchButton, modifyButton;
    private Connection conn;
    private Statement stmt;

    public AppGUI() {
        super("Student Database");
        JPanel panel = new JPanel();

        // Labels
        studentIdLabel = new JLabel("Student ID:");
        firstNameLabel = new JLabel("First Name:");
        lastNameLabel = new JLabel("Last Name:");
        majorLabel = new JLabel("Major:");
        phoneLabel = new JLabel("Phone:");
        gpaLabel = new JLabel("GPA:");
        dobLabel = new JLabel("Date of Birth (yyyy-mm-dd):");

        // Fields
        studentIdField = new JTextField(10);
        firstNameField = new JTextField(10);
        lastNameField = new JTextField(10);
        majorField = new JTextField(10);
        phoneField = new JTextField(10);
        gpaField = new JTextField(10);
        dobField = new JTextField(10);

        // Buttons
        addButton = new JButton("Add");
        displayButton = new JButton("Display");
        sortButton = new JButton("Sort");
        searchButton = new JButton("Search");
        modifyButton = new JButton("Modify");

        // Action listeners
        addButton.addActionListener(this);
        displayButton.addActionListener(this);
        sortButton.addActionListener(this);
        searchButton.addActionListener(this);
        modifyButton.addActionListener(this);

        // Add to panel
        panel.add(studentIdLabel); panel.add(studentIdField);
        panel.add(firstNameLabel); panel.add(firstNameField);
        panel.add(lastNameLabel); panel.add(lastNameField);
        panel.add(majorLabel); panel.add(majorField);
        panel.add(phoneLabel); panel.add(phoneField);
        panel.add(gpaLabel); panel.add(gpaField);
        panel.add(dobLabel); panel.add(dobField);
        panel.add(addButton); panel.add(displayButton);
        panel.add(sortButton); panel.add(searchButton);
        panel.add(modifyButton);

        // Frame settings
        add(panel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            dbConnect db = new dbConnect();
            conn = db.getConnection();
            stmt = conn.createStatement();
        } catch (Exception e) {
            showError("Database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (conn == null || stmt == null) {
            showError("Database not connected.");
            return;
        }

        Table tb = new Table();

        if (e.getSource() == addButton) {
            addStudent();
        } else if (e.getSource() == displayButton) {
            displayStudents(tb);
        } else if (e.getSource() == sortButton) {
            sortStudents(tb);
        } else if (e.getSource() == searchButton) {
            searchStudent(tb);
        } else if (e.getSource() == modifyButton) {
            modifyStudent();
        }
    }

    private void addStudent() {
        if (anyFieldEmpty()) {
            showError("Please fill all fields before adding.");
            return;
        }

        String sql = "INSERT INTO sdata VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentIdField.getText());
            ps.setString(2, firstNameField.getText());
            ps.setString(3, lastNameField.getText());
            ps.setString(4, majorField.getText());
            ps.setString(5, phoneField.getText());
            ps.setString(6, gpaField.getText());
            ps.setString(7, dobField.getText());
            ps.executeUpdate();
            showInfo("Student added successfully.");
        } catch (SQLException ex) {
            showError("Failed to add student: " + ex.getMessage());
        }
    }

    private void displayStudents(Table tb) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM sdata");
            JTable table = new JTable(tb.buildTableModel(rs));
            JOptionPane.showMessageDialog(null, new JScrollPane(table));
        } catch (SQLException ex) {
            showError("Error displaying data: " + ex.getMessage());
        }
    }

    private void sortStudents(Table tb) {
        String[] options = {"First Name", "Last Name", "Major"};
        int choice = JOptionPane.showOptionDialog(null, "Sort by:", "Sort",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        String[] columns = {"first_name", "last_name", "major"};
        if (choice < 0 || choice >= columns.length) return;

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM sdata ORDER BY " + columns[choice]);
            JTable table = new JTable(tb.buildTableModel(rs));
            JOptionPane.showMessageDialog(null, new JScrollPane(table));
        } catch (SQLException ex) {
            showError("Sort failed: " + ex.getMessage());
        }
    }

    private void searchStudent(Table tb) {
        String[] options = {"Student ID", "Last Name", "Major"};
        int choice = JOptionPane.showOptionDialog(null, "Search by:", "Search",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        String[] columns = {"student_id", "last_name", "major"};
        if (choice < 0 || choice >= columns.length) return;

        String term = JOptionPane.showInputDialog("Enter search term:");
        if (term == null || term.isEmpty()) return;

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM sdata WHERE " + columns[choice] + " LIKE '%" + term + "%'");
            JTable table = new JTable(tb.buildTableModel(rs));
            JOptionPane.showMessageDialog(null, new JScrollPane(table));
        } catch (SQLException ex) {
            showError("Search failed: " + ex.getMessage());
        }
    }

    private void modifyStudent() {
        String studentId = JOptionPane.showInputDialog("Enter student ID to modify:");
        if (studentId == null || studentId.isEmpty()) return;

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM sdata WHERE student_id = '" + studentId + "'");
            if (!rs.next()) {
                showInfo("Student not found.");
                return;
            }

            String[] fields = {"first_name", "last_name", "major", "phone", "gpa", "date_of_birth"};
            String[] labels = {"First Name", "Last Name", "Major", "Phone", "GPA", "Date of Birth"};

            int choice = JOptionPane.showOptionDialog(null, "Select field to modify:", "Modify",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, labels, labels[0]);
            if (choice < 0 || choice >= fields.length) return;

            String newValue = JOptionPane.showInputDialog("Enter new value:");
            if (newValue == null || newValue.isEmpty()) return;

            String sql = "UPDATE sdata SET " + fields[choice] + " = ? WHERE student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newValue);
                ps.setString(2, studentId);
                ps.executeUpdate();
                showInfo("Student data updated.");
            }
        } catch (SQLException ex) {
            showError("Modification failed: " + ex.getMessage());
        }
    }

    private boolean anyFieldEmpty() {
        return studentIdField.getText().isEmpty() || firstNameField.getText().isEmpty()
                || lastNameField.getText().isEmpty() || majorField.getText().isEmpty()
                || phoneField.getText().isEmpty() || gpaField.getText().isEmpty()
                || dobField.getText().isEmpty();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
