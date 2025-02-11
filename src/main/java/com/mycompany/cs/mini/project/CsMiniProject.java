package com.mycompany.cs.mini.project;

import javax.swing.*;
import java.awt.*;
import org.apache.commons.csv.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CsMiniProject extends JFrame {
    public CsMiniProject() {
        setTitle("Workshop Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu dataEntryMenu = new JMenu("Data Entry");
        JMenu reportMenu = new JMenu("Reports");
        
        JMenuItem addEmployeeItem = new JMenuItem("Add Employee");
        JMenuItem addFacilitatorItem = new JMenuItem("Add Facilitator");
        JMenuItem addWorkshopItem = new JMenuItem("Add Workshop");
        JMenuItem assignEmployeesItem = new JMenuItem("Assign Employees to Workshops");
        JMenuItem viewReportsItem = new JMenuItem("View Reports");

        dataEntryMenu.add(addEmployeeItem);
        dataEntryMenu.add(addFacilitatorItem);
        dataEntryMenu.add(addWorkshopItem);
        dataEntryMenu.add(assignEmployeesItem);
        reportMenu.add(viewReportsItem);
        
        menuBar.add(dataEntryMenu);
        menuBar.add(reportMenu);
        setJMenuBar(menuBar);

        addEmployeeItem.addActionListener(e -> {
            EmployeePanel employeePanel = new EmployeePanel();
            employeePanel.setVisible(true);
        });

        addFacilitatorItem.addActionListener(e -> {
            FacilitatorPanel facilitatorPanel = new FacilitatorPanel();
            facilitatorPanel.setVisible(true);
        });

        addWorkshopItem.addActionListener(e -> {
            WorkshopPanel workshopPanel = new WorkshopPanel();
            workshopPanel.setVisible(true);
        });

        assignEmployeesItem.addActionListener(e -> {
            WorkshopAssignmentPanel assignmentPanel = new WorkshopAssignmentPanel();
            assignmentPanel.setVisible(true);
        });
        
        viewReportsItem.addActionListener(e -> {
            ReportPanel reportPanel = new ReportPanel();
            reportPanel.setVisible(true);
        });
    }

    public static void appendToCSV(String filePath, String[] data) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
                CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord((Object[]) data);
        }
    }

    public static List<List<String>> readCSV(String filePath) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            List<List<String>> result = new ArrayList<>();
            for (String[] record : records) {
                result.add(Arrays.asList(record));
            }
            return result;
        }
    }

    public static void loadIds(String filePath, List<String> ids) throws IOException, CsvException {
        List<List<String>> records = readCSV(filePath);
        for (int i = 1; i < records.size(); i++) {
            ids.add(records.get(i).get(0));
        }
    }

    public int generateUniqueID(List<String> existingIds) {
        Random random = new Random();
        int id;
        do {
            id = 1000 + random.nextInt(9000);
        } while (existingIds.contains(String.valueOf(id)));
        existingIds.add(String.valueOf(id));
        return id;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CsMiniProject().setVisible(true));
    }
}

class EmployeePanel extends JFrame {

    private final JTextField nameField;
    private final JTextField deptField;
    private final JTextField emailField;
    private final JButton saveButton;
    private static final List<String> employeeIds = new ArrayList<>();

    public EmployeePanel() {
        setTitle("Add Employee");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(null);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Department:"));
        deptField = new JTextField();
        add(deptField);

        add(new JLabel("Email ID:"));
        emailField = new JTextField();
        add(emailField);

        saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> saveEmployee());
    }

    private void saveEmployee() {
        try {
            CsMiniProject.loadIds("employees.csv", employeeIds);
        } catch (IOException | CsvException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while loading employee IDs!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        String name = nameField.getText().trim();
        String dept = deptField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || dept.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CsMiniProject project = new CsMiniProject();
        int idE = project.generateUniqueID(employeeIds);
        String eID = "E" + Integer.toString(idE);

        String[] data = { eID, name, dept, email };
        try {
            CsMiniProject.appendToCSV("employees.csv", data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while saving the employee!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Employee saved successfully! ID: E" + idE);
    }

}

class FacilitatorPanel extends JFrame {
    private final JTextField nameField;
    private final JTextField emailField;
    private final JComboBox<String> expertiseBox;
    private final JButton saveButton;
    private static final List<String> facilitatorIds = new ArrayList<>();

    public FacilitatorPanel() {
        setTitle("Add Facilitator");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(null);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Expertise Area:"));
        String[] expertiseOptions = { "Leadership", "Data Analytics", "Communication Skills" };
        expertiseBox = new JComboBox<>(expertiseOptions);
        add(expertiseBox);

        add(new JLabel("Email ID:"));
        emailField = new JTextField();
        add(emailField);

        saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> saveFacilitator());
    }

    private void saveFacilitator() {
        try {
            CsMiniProject.loadIds("facilitators.csv", facilitatorIds);
        } catch (IOException | CsvException ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while loading facilitator IDs!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String expertise = (String) expertiseBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CsMiniProject project = new CsMiniProject();
        int idF = project.generateUniqueID(facilitatorIds);
        String fID = "F" + Integer.toString(idF);

        String[] data = { fID, name, expertise, email };
        try {
            CsMiniProject.appendToCSV("facilitators.csv", data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while saving the facilitator!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Facilitator saved successfully! ID: " + fID);
    }
}

class WorkshopPanel extends JFrame {
    private final JTextField titleField;
    private final JComboBox<String> facilitatorBox, locationBox;
    private List<String> facilitators = new ArrayList<>();
    private List<String> facilitatorIDs = new ArrayList<>();
    private final JRadioButton morning, afternoon, fullDay;
    private final JButton saveButton;
    private static final List<String> workshopIds = new ArrayList<>();

    public WorkshopPanel() {
        setTitle("Add Workshop");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Workshop Title:"), gbc);

        gbc.gridx = 1;
        titleField = new JTextField(20);
        add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Facilitator ID:"), gbc);

        gbc.gridx = 1;


        try {
            loadFacilitatorIDs("facilitators.csv", facilitators, facilitatorIDs);
        } catch (IOException | CsvException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while loading facilitators!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        facilitatorBox = new JComboBox<>(facilitatorIDs.toArray(new String[0]));
        add(facilitatorBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        String[] locations = { "Boardroom", "Conference Hall", "Training Room" };
        locationBox = new JComboBox<>(locations);
        add(locationBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Timing:"), gbc);

        gbc.gridx = 1;
        JPanel timingPanel = new JPanel();
        morning = new JRadioButton("Morning");
        afternoon = new JRadioButton("Afternoon");
        fullDay = new JRadioButton("Full Day");
        ButtonGroup timingGroup = new ButtonGroup();
        timingGroup.add(morning);
        timingGroup.add(afternoon);
        timingGroup.add(fullDay);
        timingPanel.add(morning);
        timingPanel.add(afternoon);
        timingPanel.add(fullDay);
        add(timingPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        saveButton = new JButton("Save");
        add(saveButton, gbc);

        saveButton.addActionListener(e -> saveWorkshop());
    }

    private static void loadFacilitatorIDs(String filePath, List<String> facilitators, List<String> facilitatorsIds) throws IOException, CsvException {
        List<List<String>> records = CsMiniProject.readCSV(filePath);
        List<List<String>> used = CsMiniProject.readCSV("workshops.csv");
        List<String> usedFacilitators = new ArrayList<>();
        for (int i = 0; i < used.size(); i++) {
            if (i != 0 && used.get(i).size() > 0) {
                System.err.println(used.get(i).size());
                usedFacilitators.add(used.get(i).get(2));

            }
        }
        for (int i = 1; i < records.size(); i++) {
            if (records.get(i).size() > 1 && !usedFacilitators.contains(records.get(i).get(0))) {
                facilitatorsIds.add(records.get(i).get(0));
                facilitators.add(records.get(i).get(1));
            }
        }
    }

    private void saveWorkshop() {
        String title = titleField.getText().trim();
        String facilitatorID = (String) facilitatorBox.getSelectedItem();
        int facilitatorIndex = facilitatorIDs.indexOf(facilitatorID);
        String facilitatorName = facilitators.get(facilitatorIndex);
        String location = (String) locationBox.getSelectedItem();
        String timing = morning.isSelected() ? "Morning" : afternoon.isSelected() ? "Afternoon" : "Full Day";
        CsMiniProject project = new CsMiniProject();
        int idW = project.generateUniqueID(workshopIds);
        String WID = "W" + Integer.toString(idW);

        if (title.isEmpty() || facilitatorID == null || location == null || timing.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] data = { WID, title, facilitatorID, facilitatorName, location, timing };
        try {
            CsMiniProject.appendToCSV("workshops.csv", data);
            JOptionPane.showMessageDialog(this, "Workshop saved successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving workshop!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class WorkshopAssignmentPanel extends JFrame {
        JComboBox<String> workshopBox;
        JComboBox<String> employeeBox;
        private List<String> workshops = new ArrayList<>();
        private List<String> employeeIds = new ArrayList<>();
        JButton assignButton;

        public WorkshopAssignmentPanel() {
            setTitle("Assign Employees to Workshops");
            setSize(400, 200);
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            try {
                loadWorkshopData();
                loadEmployeeData();
            } catch (IOException | CsvException ex) {
                JOptionPane.showMessageDialog(this, "Error loading workshop and employee data!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(new JLabel("Workshop ID:"), gbc);

            gbc.gridx = 1;
            workshopBox = new JComboBox<>(workshops.toArray(new String[0]));
            add(workshopBox, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(new JLabel("Employee ID:"), gbc);

            gbc.gridx = 1;
            employeeBox = new JComboBox<>(employeeIds.toArray(new String[0]));
            add(employeeBox, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            assignButton = new JButton("Assign");
            add(assignButton, gbc);

            assignButton.addActionListener(e -> assignEmployee());
        }

        private void loadWorkshopData() throws IOException, CsvException {
            List<List<String>> records = CsMiniProject.readCSV("workshops.csv");
            workshops.clear();
            for (int i = 1; i < records.size(); i++) {
                if (records.get(i).size() > 0) {
                    workshops.add(records.get(i).get(0));
                }
            }
        }

        private void loadEmployeeData() throws IOException, CsvException {
            List<List<String>> records = CsMiniProject.readCSV("employees.csv");
            employeeIds.clear();
            for (int i = 1; i < records.size(); i++) {
                if (records.get(i).size() > 0) {
                    employeeIds.add(records.get(i).get(0));
                }
            }
        }

        private void assignEmployee() {
            String workshopId = (String) workshopBox.getSelectedItem();
            String employeeId = (String) employeeBox.getSelectedItem();

            if (workshopId == null || employeeId == null) {
                JOptionPane.showMessageDialog(this, "Please select both workshop and employee!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] data = {workshopId, employeeId};
            try {
                CsMiniProject.appendToCSV("assigned_workshops.csv", data);
                JOptionPane.showMessageDialog(this, "Assignment successful!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving assignment!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

class ReportPanel extends JFrame {
    JTextArea reportArea;
    JComboBox<String> reportTypeBox;
    JComboBox<String> criteriaBox;
    JButton generateButton;
    
    public ReportPanel() {
        setTitle("Generate Reports");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        reportTypeBox = new JComboBox<>(new String[]{"Employees by Workshop", "Workshop Details by Employee", "Employees by Time Slot"});
        criteriaBox = new JComboBox<>();
        generateButton = new JButton("Generate Report");

        selectionPanel.add(new JLabel("Select Report Type:"));
        selectionPanel.add(reportTypeBox);
        selectionPanel.add(new JLabel("Select Criteria:"));
        selectionPanel.add(criteriaBox);
        selectionPanel.add(generateButton);
        
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(reportArea);

        add(selectionPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        reportTypeBox.addActionListener(e -> updateCriteriaOptions());
        generateButton.addActionListener(e -> generateReport(reportTypeBox.getSelectedIndex()));
    }
    
    private void updateCriteriaOptions() {
        criteriaBox.removeAllItems();
        int selectedIndex = reportTypeBox.getSelectedIndex();
        List<String> options = getCriteriaOptions(selectedIndex);
        for (String option : options) {
            criteriaBox.addItem(option);
        }
    }
    
    private List<String> getCriteriaOptions(int reportType) {
        List<String> options = new ArrayList<>();
        try {
            switch (reportType) {
                case 0: options = readCSVColumn("workshops.csv", 0); break;
                case 1: options = readCSVColumn("employees.csv", 0); break;
                case 2: options.add("Morning"); options.add("Afternoon"); options.add("Full Day"); break;
            }
        } catch (IOException | CsvException e) {
            JOptionPane.showMessageDialog(this, "Error loading data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return options;
    }
    
    private List<String> readCSVColumn(String filePath, int columnIndex) throws IOException, CsvException {
        List<String> columnData = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                if (record.length > columnIndex) {
                    columnData.add(record[columnIndex]);
                }
            }
        }
        return columnData;
    }
    
    private void generateReport(int reportType) {
        String selectedCriteria = (String) criteriaBox.getSelectedItem();
        if (selectedCriteria == null) {
            reportArea.setText("Please select a valid criteria.");
            return;
        }
        reportArea.setText("Generating Report for " + selectedCriteria + "\n\nPLACEHOLDER FOR REPORT");
    }
}