package com.mycompany.cs.mini.project;

import javax.swing.*;
import java.awt.*;
import org.apache.commons.csv.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        JMenuItem addEmployeeItem = new JMenuItem("Add Employee");
        JMenuItem addFacilitatorItem = new JMenuItem("Add Facilitator");
        JMenuItem addWorkshopItem = new JMenuItem("Add Workshop");

        dataEntryMenu.add(addEmployeeItem);
        dataEntryMenu.add(addFacilitatorItem);
        dataEntryMenu.add(addWorkshopItem);
        menuBar.add(dataEntryMenu);
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
    }

    public static void appendToCSV(String filePath, String[] data) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT)) {
            csvPrinter.println();
            csvPrinter.printRecord((Object[]) data);
        }
    }

    public static List<List<String>> readCSV(String filePath) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (CSVParser csvParser = CSVParser.parse(filePath, CSVFormat.DEFAULT)) {
            for (CSVRecord csvRecord : csvParser) {
                List<String> record = new ArrayList<>();
                for (String field : csvRecord) {
                    record.add(field);
                }
                records.add(record);
            }
            return records;
        }
    }

    public static void loadIds(String filePath, List<Integer> ids) throws IOException {
        List<List<String>> records = readCSV(filePath);
        for (int i = 1; i < records.size(); i++) {
            ids.add(Integer.valueOf(records.get(i).get(0)));
        }
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
    private static final List<Integer> employeeIds = new ArrayList<>();

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
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while loading employee IDs!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        String name = nameField.getText().trim();
        String dept = deptField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || dept.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idE = generateUniqueID(employeeIds);
        String eID = "E" + Integer.toString(idE);

        String[] data = {eID, name, dept, email};
        try {
            CsMiniProject.appendToCSV("employees.csv", data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while saving the employee!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Employee saved successfully! ID: E" + idE);
    }

    private int generateUniqueID(List<Integer> existingIds) {
        Random random = new Random();
        int id;
        do {
            id = 1000 + random.nextInt(9000);
        } while (existingIds.contains(id));
        existingIds.add(id);
        return id;
    }
}

class FacilitatorPanel extends JFrame {
    private final JTextField nameField;
    private final JTextField emailField;
    private final JComboBox<String> expertiseBox;
    private final JButton saveButton;
    private static final List<Integer> facilitatorIds = new ArrayList<>();

    public FacilitatorPanel() {
        setTitle("Add Facilitator");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(null);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Expertise Area:"));
        String[] expertiseOptions = {"Leadership", "Data Analytics", "Communication Skills"};
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
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while loading facilitator IDs!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String expertise = (String) expertiseBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idF = generateUniqueID(facilitatorIds);
        String fID = "F" + Integer.toString(idF);

        String[] data = {fID, name, expertise, email};
        try {
            CsMiniProject.appendToCSV("facilitators.csv", data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while saving the facilitator!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Facilitator saved successfully! ID: " + fID);
    }

    private int generateUniqueID(List<Integer> existingIds) {
        Random random = new Random();
        int id;
        do {
            id = 1000 + random.nextInt(9000);
        } while (existingIds.contains(id));
        existingIds.add(id);
        return id;
    }
}

class WorkshopPanel extends JFrame {
    private final JTextField titleField;
    private final JComboBox<String> facilitatorBox, locationBox;
    private final JRadioButton morning, afternoon, fullDay;
    private final JButton saveButton;
    private static final List<Integer> workshopIds = new ArrayList<>();

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
        List<String> facilitators = loadFacilitatorIDs("facilitators.csv");
        facilitatorBox = new JComboBox<>(facilitators.toArray(new String[0]));
        add(facilitatorBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        String[] locations = {"Boardroom", "Conference Hall", "Training Room"};
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

    private void saveWorkshop() {
        String title = titleField.getText().trim();
        String facilitatorID = (String) facilitatorBox.getSelectedItem();
        String location = (String) locationBox.getSelectedItem();
        String timing = morning.isSelected() ? "Morning" : afternoon.isSelected() ? "Afternoon" : "Full Day";

        if (title.isEmpty() || facilitatorID == null || location == null || timing.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] data = {title, facilitatorID, location, timing};
        try {
            CsMiniProject.appendToCSV("workshops.txt", data);
            JOptionPane.showMessageDialog(this, "Workshop saved successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving workshop!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> loadFacilitatorIDs(String filePath) {
        List<String> facilitatorList = new ArrayList<>();
        try {
            List<List<String>> records = CsMiniProject.readCSV(filePath);
            for (List<String> record : records) {
                facilitatorList.add(record.get(0)); // Assuming first column is ID
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading facilitator data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return facilitatorList;
    }
}
