
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;

import javax.swing.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

import javax.swing.*;
import java.awt.*;

public class CarCareServiceUI {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private boolean[] serviceBays = {true, true, true};
    private Queue<String> waitingQueue = new LinkedList<>();
    private JButton[] bayButtons = new JButton[3];
    private String[] currentCustomerIds = new String[3];
    private HashMap<Integer, String> bayCustomerMap = new HashMap<>();
    private HashMap<String, String> customerVehicleMap = new HashMap<>();

    private HashMap<String, JSONArray> customerServiceMap = new HashMap<>();
    private HashMap<String, Double> customerTotalMap = new HashMap<>();
    private boolean isBuyingProduct = false;
    private boolean queueCustomerIsBuying = false;
    public static boolean isOpen = false;
    public static CarCareServiceUI currentInstance;

    public CarCareServiceUI() {
        currentInstance = this;
        frame = new JFrame("Car Care Service");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(welcomePanel(), "Welcome");
        mainPanel.add(loginPanel(), "Login");
        // mainPanel.add(menuPanel(), "Menu");
        mainPanel.add(servicePanel(), "Service");
        mainPanel.add(productPanel(), "Product");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    class GradientPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = Color.decode("#003f5c"); // สีกรมเข้ม
            Color color2 = Color.decode("#58508d"); // สีม่วงเข้ม
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ฟังก์ชัน welcomePanel ที่สวยงามระดับปริญญาตรี
    private JPanel welcomePanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        // โลโก้ (images/key.png ต้องมีในโฟลเดอร์ images)
        ImageIcon icon = new ImageIcon("images/key.png");
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(img));
        gbc.gridy = 0;
        panel.add(logo, gbc);

        // ชื่อระบบ
        JLabel titleLabel = new JLabel("Welcome to Car Care Service System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        panel.add(titleLabel, gbc);

        JLabel welcomeMessage = new JLabel("Please log in to start working");
        welcomeMessage.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeMessage.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 2;
        panel.add(welcomeMessage, gbc);

        // ปุ่ม Login
        JButton loginButton = new JButton("Log in");
        loginButton.setPreferredSize(new Dimension(140, 40));
        loginButton.setBackground(new Color(255, 99, 97)); // #ff6361
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        gbc.gridy = 3;
        panel.add(loginButton, gbc);

        return panel;
    }

    private JPanel loginPanel() {
        // พาเนลหลักที่มี Gradient Background
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = Color.decode("#003f5c");
                Color color2 = Color.decode("#58508d");
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        // ===== Box ที่ครอบทั้งหมดไว้ในสีขาวขุ่นแบบ Card =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 240)); // สีขาวโปร่ง
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 10, 10, 10);
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Title =====
        JLabel titleLabel = new JLabel("Car Care Service - Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.decode("#003f5c"));
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.gridwidth = 2;
        formPanel.add(titleLabel, formGbc);

        // ===== Username =====
        formGbc.gridy++;
        formGbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.decode("#58508d"));
        formPanel.add(userLabel, formGbc);

        formGbc.gridx = 1;
        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#003f5c"), 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        formPanel.add(userField, formGbc);

        // ===== Password =====
        formGbc.gridy++;
        formGbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passLabel.setForeground(Color.decode("#58508d"));
        formPanel.add(passLabel, formGbc);

        formGbc.gridx = 1;
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#003f5c"), 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        formPanel.add(passField, formGbc);

        // ===== Login Button =====
        formGbc.gridy++;
        formGbc.gridx = 0;
        formGbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(Color.decode("#ffa600"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(140, 40));
        formPanel.add(loginButton, formGbc);

        // Hover Effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(Color.decode("#ff6361"));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(Color.decode("#ffa600"));
            }
        });

        loginButton.addActionListener(e -> {
            String enteredID = userField.getText().trim();
            String enteredPass = new String(passField.getPassword());

            if (validateLogin(enteredID, enteredPass)) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                frame.dispose();
                new MainReportMenu();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Username or Password!");
            }
        });

        // ===== เพิ่ม formPanel ไปไว้ตรงกลาง panel หลัก =====
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(formPanel, gbc);

        return panel;
    }

    private boolean validateLogin(String username, String password) {
        try {
            FileInputStream fileInputStream = new FileInputStream("employees.json");
            InputStreamReader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);
            JSONTokener tokener = new JSONTokener(bufferedReader);
            JSONArray employeesArray = new JSONArray(tokener);
            bufferedReader.close();

            for (int i = 0; i < employeesArray.length(); i++) {
                JSONObject employee = employeesArray.getJSONObject(i);
                if (employee.getString("username").equals(username)
                        && employee.getString("password").equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public JPanel servicePanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, Color.decode("#2f2f2f"),
                        0, getHeight(), Color.decode("#4f4f4f")
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // ===== Header =====
        JLabel label = new JLabel("Service Bays", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(Color.decode("#fdd835")); // เหลืองทอง
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(label, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        for (int i = 0; i < bayButtons.length; i++) {
            final int index = i;

            JPanel bayPanel = new JPanel(new BorderLayout(5, 5));
            bayPanel.setOpaque(false);
            bayPanel.setPreferredSize(new Dimension(170, 200));

            bayButtons[i] = new JButton();
            bayButtons[i].setPreferredSize(new Dimension(160, 160));
            bayButtons[i].setBorder(BorderFactory.createLineBorder(Color.decode("#fdd835"), 2));
            bayButtons[i].setFocusPainted(false);
            bayButtons[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            bayButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    bayButtons[index].setBorder(BorderFactory.createLineBorder(Color.decode("#ffee58"), 3));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    bayButtons[index].setBorder(BorderFactory.createLineBorder(Color.decode("#fdd835"), 2));
                }
            });

            bayButtons[i].addActionListener(e -> handleBayClick(index));

            JLabel statusLabel = new JLabel("Bay " + (i + 1) + " Available", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            statusLabel.setForeground(Color.decode("#00FF00")); // ✅ เขียวนีออน
            statusLabel.setName("statusLabel" + i);

            bayPanel.add(bayButtons[i], BorderLayout.CENTER);
            bayPanel.add(statusLabel, BorderLayout.SOUTH);

            gbc.gridx = i;
            panel.add(bayPanel, gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JButton newCustomerButton = new JButton("New Customer");
        newCustomerButton.setPreferredSize(new Dimension(150, 40));
        newCustomerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newCustomerButton.setBackground(Color.decode("#fbc02d"));
        newCustomerButton.setForeground(Color.BLACK);
        newCustomerButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        newCustomerButton.setFocusPainted(false);
        newCustomerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        newCustomerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                newCustomerButton.setBackground(Color.decode("#fff176"));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                newCustomerButton.setBackground(Color.decode("#fbc02d"));
            }
        });

        newCustomerButton.addActionListener(e -> handleNewCustomer());
        panel.add(newCustomerButton, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(Color.decode("#616161")); // เทากลาง
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createLineBorder(Color.decode("#bdbdbd"), 2, true));
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(Color.decode("#757575"));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(Color.decode("#616161"));
            }
        });

        backButton.addActionListener(e -> new MainReportMenu());
        panel.add(backButton, gbc);

        updateBayStatus();
        return panel;
    }

    private JPanel productPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel label = new JLabel("Select Product to Purchase", SwingConstants.CENTER);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> new MainReportMenu());
        panel.add(label, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
        return panel;
    }

    private void selectServices(String customerId, String vehicleType) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream("services.json"), StandardCharsets.UTF_8))) {
            JSONArray all = new JSONArray(new JSONTokener(reader));
            JPanel panel = new JPanel(new GridLayout(0, 1));
            List<JCheckBox> checks = new ArrayList<>();

            for (int i = 0; i < all.length(); i++) {
                JSONObject svc = all.getJSONObject(i);
                if (svc.getJSONObject("prices").has(vehicleType)) {
                    double price = svc.getJSONObject("prices").getDouble(vehicleType);
                    JCheckBox cb = new JCheckBox(svc.getString("name") + " - " + price + " THB");
                    cb.putClientProperty("service_id", svc.getString("service_id"));
                    cb.putClientProperty("vehicle_type", vehicleType);
                    cb.putClientProperty("price", price);
                    panel.add(cb);
                    checks.add(cb);
                }
            }

            int result = JOptionPane.showConfirmDialog(frame, panel, "Select Services", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                JSONArray selected = new JSONArray();
                double total = 0;
                for (JCheckBox cb : checks) {
                    if (cb.isSelected()) {
                        JSONObject svc = new JSONObject();
                        svc.put("service_id", cb.getClientProperty("service_id"));
                        svc.put("vehicle_type", cb.getClientProperty("vehicle_type"));
                        svc.put("price", cb.getClientProperty("price"));
                        total += (double) cb.getClientProperty("price");
                        selected.put(svc);
                    }
                }

                JSONArray existingServices = customerServiceMap.getOrDefault(customerId, new JSONArray());
                double existingTotal = customerTotalMap.getOrDefault(customerId, 0.0);

                for (int i = 0; i < selected.length(); i++) {
                    existingServices.put(selected.getJSONObject(i));
                }

                double newTotal = existingTotal + total;
                customerServiceMap.put(customerId, existingServices);
                customerTotalMap.put(customerId, newTotal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNewCustomer() {
        int bay = getAvailableBay();

        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField plate = new JTextField();
        String[] types = {"Motorcycle", "Sedan", "SUV/Pickup/Van"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JPanel form = new JPanel(new GridLayout(4, 2));
        form.add(new JLabel("Full Name:"));
        form.add(name);
        form.add(new JLabel("Phone:"));
        form.add(phone);
        form.add(new JLabel("Plate No:"));
        form.add(plate);
        form.add(new JLabel("Vehicle Type:"));
        form.add(typeBox);

        int result = JOptionPane.showConfirmDialog(frame, form, "Customer Info", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String customerId = saveCustomerData(name.getText(), phone.getText(), plate.getText(),
                    (String) typeBox.getSelectedItem());

            if (bay != -1) {

                bayCustomerMap.put(bay, customerId);
                customerVehicleMap.put(customerId, (String) typeBox.getSelectedItem());
                serviceBays[bay] = false;

                String[] options = {"Use Services", "Buy Products"};
                int choice = JOptionPane.showOptionDialog(frame, "Select Action:", "Next Step",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    selectServices(customerId, (String) typeBox.getSelectedItem());
                } else if (choice == 1) {
                    new CategoryMenu();
                }

                updateBayStatus();
            } else {

                int waitConfirm = JOptionPane.showConfirmDialog(frame,
                        "All bays are full. Would you like to wait in queue?",
                        "Queue Waiting", JOptionPane.YES_NO_OPTION);

                if (waitConfirm == JOptionPane.YES_OPTION) {
                    waitingQueue.add(customerId);
                    saveQueueToFile();
                    JOptionPane.showMessageDialog(frame, "You've been added to the queue. Please wait.");
                } else {
                    JOptionPane.showMessageDialog(frame, "You chose not to wait. Thank you.");
                }
            }
        }
    }

    private String saveCustomerData(String name, String phone, String plate, String vehicle) {
        try {
            File file = new File("customers.json");
            JSONArray customersArray;

            if (file.exists() && file.length() != 0) {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader);
                JSONTokener tokener = new JSONTokener(bufferedReader);
                customersArray = new JSONArray(tokener);
                bufferedReader.close();
            } else {
                customersArray = new JSONArray();
            }

            String customerId = "C" + String.format("%03d", customersArray.length() + 1);

            JSONObject newCustomer = new JSONObject();
            newCustomer.put("customer_id", customerId);
            newCustomer.put("name", name);
            newCustomer.put("phone", phone);

            JSONObject vehicleData = new JSONObject();
            vehicleData.put("plate_number", plate);
            vehicleData.put("type", vehicle);
            newCustomer.put("vehicle", vehicleData);

            customersArray.put(newCustomer);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            writer.write(customersArray.toString(4));
            writer.flush();
            writer.close();

            return customerId;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeReceipt(String customerId, JSONArray services, double total) {
        int confirm = JOptionPane.showConfirmDialog(frame, "Confirm payment?", "Payment Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(frame, "Payment canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] paymentMethods = {"Cash", "Bank Transfer", "Credit Card"};
        String paymentMethod = (String) JOptionPane.showInputDialog(frame, "Select payment method:", "Payment",
                JOptionPane.QUESTION_MESSAGE, null, paymentMethods, paymentMethods[0]);

        if (paymentMethod == null) {
            JOptionPane.showMessageDialog(frame, "Payment method not selected. Cancelling transaction.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File file = new File("receipts.json");
            JSONArray arr = file.exists() && file.length() > 0
                    ? new JSONArray(
                            new JSONTokener(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)))
                    : new JSONArray();

            int totalMinutes = 0;
            String vehicleType = customerVehicleMap.get(customerId);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream("services.json"), StandardCharsets.UTF_8))) {
                JSONArray serviceData = new JSONArray(new JSONTokener(reader));

                for (int i = 0; i < services.length(); i++) {
                    JSONObject selectedService = services.getJSONObject(i);
                    String serviceId = selectedService.getString("service_id");

                    for (int j = 0; j < serviceData.length(); j++) {
                        JSONObject service = serviceData.getJSONObject(j);
                        if (service.getString("service_id").equals(serviceId)) {
                            String duration = service.getJSONObject("duration").getString(vehicleType);
                            selectedService.put("duration", duration);
                            totalMinutes += parseDurationToMinutes(duration);
                            break;
                        }
                    }
                }
            }

            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            String totalTime = (hours > 0 ? hours + " hours " : "") + (minutes > 0 ? minutes + " minutes" : "");
            String customerName = "Unknown";
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream("customers.json"), StandardCharsets.UTF_8))) {
                JSONArray customerData = new JSONArray(new JSONTokener(reader));
                for (int i = 0; i < customerData.length(); i++) {
                    JSONObject customer = customerData.getJSONObject(i);
                    if (customer.getString("customer_id").equals(customerId)) {
                        customerName = customer.getString("name");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject obj = new JSONObject();
            obj.put("receipt_id", "R" + String.format("%03d", arr.length() + 1));
            obj.put("customer_id", customerId);
            obj.put("customer", customerName); // เพิ่มชื่อลูกค้า
            obj.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            obj.put("services", services);
            obj.put("total", total);
            obj.put("total_time", totalTime);
            obj.put("payment_method", paymentMethod);
            arr.put(obj);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                    StandardCharsets.UTF_8)) {
                writer.write(arr.toString(4));
            }

            showReceipt(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int parseDurationToMinutes(String duration) {
        int minutes = 0;
        String[] parts = duration.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase("hour") || parts[i].equalsIgnoreCase("hours")) {
                minutes += Integer.parseInt(parts[i - 1]) * 60;
            } else if (parts[i].equalsIgnoreCase("minute") || parts[i].equalsIgnoreCase("minutes")) {
                minutes += Integer.parseInt(parts[i - 1]);
            }
        }
        return minutes;
    }

    private void showReceipt(JSONObject receipt) {

        String line = "==============================================";
        String subLine = "----------------------------------------------";
        StringBuilder sb = new StringBuilder();

        sb.append(line).append("\n");
        sb.append(String.format("%-46s\n", "            CAR CARE SERVICE"));
        sb.append(line).append("\n");
        sb.append(String.format("%-20s : %s\n", "Receipt ID", receipt.getString("receipt_id")));
        sb.append(String.format("%-20s : %s\n", "Date", receipt.getString("date")));
        sb.append(String.format("%-20s : %s\n", "Customer", receipt.optString("customer", "Unknown")));

        sb.append(subLine).append("\n");
        sb.append(String.format("%-30s %16s\n", "Service", "Price (THB)"));
        sb.append(subLine).append("\n");

        JSONArray services = receipt.getJSONArray("services");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream("services.json"), StandardCharsets.UTF_8))) {
            JSONArray serviceData = new JSONArray(new JSONTokener(reader));

            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                String serviceId = service.getString("service_id");
                double price = service.getDouble("price");
                String duration = service.getString("duration");
                String serviceName = "Unknown Service";

                for (int j = 0; j < serviceData.length(); j++) {
                    JSONObject serviceItem = serviceData.getJSONObject(j);
                    if (serviceItem.getString("service_id").equals(serviceId)) {
                        serviceName = serviceItem.getString("name");
                        break;
                    }
                }

                sb.append(String.format("%-30s %16.2f\n", serviceName, price));
                sb.append(String.format("  (%s)\n", duration));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append(subLine).append("\n");
        sb.append(String.format("%-20s : %s\n", "Total Time", receipt.getString("total_time")));
        sb.append(String.format("%-20s : %,.2f THB\n", "Total", receipt.getDouble("total")));
        sb.append(String.format("%-20s : %s\n", "Payment", receipt.getString("payment_method")));
        sb.append(line).append("\n");
        sb.append(String.format("%-46s\n", "      Thank You & Have a Nice Day!"));
        sb.append(line).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));

        JDialog dialog = new JDialog(frame, "Receipt", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            dialog.dispose();

            JOptionPane.showMessageDialog(frame, "Receipt saved for Customer ID: " + receipt.getString("customer_id"));

            int choice = JOptionPane.showConfirmDialog(frame, "Would you like to buy any products?", "Next Step", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                isBuyingProduct = true;

                CategoryMenu menu = new CategoryMenu();
                menu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                menu.setVisible(true);

                menu.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        isBuyingProduct = false;
                        releaseBay(receipt.getString("customer_id"));
                        updateBayStatus();
                        checkQueueAfterBayUpdate();
                    }
                });

            } else {
                isBuyingProduct = false;
                queueCustomerIsBuying = false;
                releaseBay(receipt.getString("customer_id"));
                updateBayStatus();
                checkQueueAfterBayUpdate();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);

    }

    private void releaseBay(String customerId) {
        for (Map.Entry<Integer, String> entry : bayCustomerMap.entrySet()) {
            if (entry.getValue().equals(customerId)) {
                int index = entry.getKey();
                serviceBays[index] = true;
                bayCustomerMap.remove(index);
                break;
            }
        }
    }

    // ใช้เพื่อหาช่องที่ว่าง (Bay) แรก
    private int getAvailableBay() {
        for (int i = 0; i < serviceBays.length; i++) {
            if (serviceBays[i]) {
                return i;
            }
        }
        return -1;
    }

    private void updateBayStatus() {
        for (int i = 0; i < serviceBays.length; i++) {
            String imagePath = serviceBays[i] ? "images/bayAv.png" : "images/bayOc.png";
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaled = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
            bayButtons[i].setIcon(new ImageIcon(scaled));
            bayButtons[i].setText("");

            Component[] components = bayButtons[i].getParent().getComponents();
            for (Component c : components) {
                if (c instanceof JLabel && ("statusLabel" + i).equals(((JLabel) c).getName())) {
                    JLabel label = (JLabel) c;
                    label.setFont(new Font("Arial", Font.BOLD, 13));
                    label.setForeground(serviceBays[i] ? new Color(0, 128, 0) : Color.RED);
                    label.setText("Bay " + (i + 1) + (serviceBays[i] ? " Available" : " Occupied"));
                }
            }
        }

        if (!isBuyingProduct) {
            checkQueueAfterBayUpdate();
        }
    }

    private String getVehicleTypeFromCustomerFile(String customerId) {
        try {
            File file = new File("customers.json");
            if (file.exists()) {
                JSONArray arr = new JSONArray(
                        new JSONTokener(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    if (obj.getString("customer_id").equals(customerId)) {
                        return obj.getJSONObject("vehicle").getString("type");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkQueueAfterBayUpdate() {

        if (isBuyingProduct || queueCustomerIsBuying || CategoryMenu.isOpen) {
            return;
        }

        if (!waitingQueue.isEmpty()) {
            int bay = getAvailableBay();
            if (bay != -1) {
                String customerId = waitingQueue.poll();
                saveQueueToFile();

                String vehicleType = getVehicleTypeFromCustomerFile(customerId);
                bayCustomerMap.put(bay, customerId);
                customerVehicleMap.put(customerId, vehicleType);
                serviceBays[bay] = false;

                updateBayStatus();
                JOptionPane.showMessageDialog(frame, "Queue customer moved to Bay " + (bay + 1));

                // ถามว่าจะใช้บริการหรือซื้อของ
                String[] options = {"Use Services", "Buy Products"};
                int choice = JOptionPane.showOptionDialog(frame, "Select Action for Queue Customer:", "Next Step",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    selectServices(customerId, vehicleType);
                } else if (choice == 1) {
                    isBuyingProduct = true;
                    CategoryMenu menu = new CategoryMenu();
                    menu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    menu.setVisible(true);

                    menu.addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            isBuyingProduct = false;
                            releaseBay(customerId);
                            updateBayStatus();
                            checkQueueAfterBayUpdate();
                        }
                    });
                }
            }
        }
    }

    private void handleBayClick(int index) {
        if (!serviceBays[index]) {
            String customerId = bayCustomerMap.get(index);
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Is the service for Bay " + (index + 1) + " completed?",
                    "Service Status",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                JSONArray services = customerServiceMap.getOrDefault(customerId, new JSONArray());
                double total = customerTotalMap.getOrDefault(customerId, 0.0);
                writeReceipt(customerId, services, total);

                // ❗ อย่าปล่อยให้ Bay ว่างที่นี่ รอจนกว่าจะจบบิลทั้งหมด
                // ปล่อยให้ showReceipt() เป็นคนจัดการ
            } else if (confirm == JOptionPane.NO_OPTION) {
                selectServices(customerId, customerVehicleMap.get(customerId));
            }
        }
    }

    private void saveQueueToFile() {
        try {
            File file = new File("queue.json");
            JSONArray queueArray = new JSONArray();
            int qnum = 1;
            for (String customerId : waitingQueue) {
                JSONObject obj = new JSONObject();
                obj.put("queue_number", qnum++);
                obj.put("customer_id", customerId);
                obj.put("status", "waiting");
                queueArray.put(obj);
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                    StandardCharsets.UTF_8)) {
                writer.write(queueArray.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel reportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel label = new JLabel("Sales and Inventory Report", SwingConstants.CENTER);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> new MainReportMenu());

        panel.add(label, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarCareServiceUI::new);
    }
}

class ProductBalanceReportUI extends JFrame {

    private JTable productTable;
    private JScrollPane scrollPane;

    public ProductBalanceReportUI() {
        setTitle("Product Balance Report");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Product Code", "Product Name", "Price", "Quantity"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);
        scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        try {
            JSONArray productData = loadProductData();
            for (int i = 0; i < productData.length(); i++) {
                JSONObject product = productData.getJSONObject(i);
                String productCode = product.getString("product_code");
                String productName = product.getString("product_name");
                double price = product.getDouble("price");
                int quantity = product.getInt("quantity");

                Object[] rowData = {productCode, productName, price, quantity};
                tableModel.addRow(rowData);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductBalanceReportUI frame = new ProductBalanceReportUI();
            frame.setVisible(true);
        });
    }

    public JSONArray loadProductData() throws IOException, JSONException {
        FileReader reader = new FileReader("products.json");
        StringBuilder jsonString = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            jsonString.append((char) ch);
        }
        reader.close();
        return new JSONArray(jsonString.toString());
    }
}

class MainReportMenu extends JFrame {

    public MainReportMenu() {
        setTitle("Car Care Service");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        CarCareServiceUI car = CarCareServiceUI.currentInstance;

        // ===== พื้นหลัง Gradient ฟ้าเข้ม → ม่วงหรู =====
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, Color.decode("#004d73"), // ฟ้าเข้ม
                        0, getHeight(), Color.decode("#7a74c0") // ม่วงหรู
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        // ===== Panel ปุ่มเมนู =====
        JPanel menuPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        menuPanel.setOpaque(false);

        // ===== Customer Service =====
        JButton customerServiceBtn = new JButton("Customer Service");
        styleMenuButton(customerServiceBtn);
        customerServiceBtn.addActionListener(e -> {
            JFrame frame = new JFrame("Customer Service");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(car.servicePanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // ===== Back Office Management =====
        JButton backOfficeBtn = new JButton("Back Office Management");
        styleMenuButton(backOfficeBtn);
        backOfficeBtn.addActionListener(e -> new BackOfficeMenu());

        menuPanel.add(customerServiceBtn);
        menuPanel.add(backOfficeBtn);

        // ===== ปุ่ม Back ด้านล่าง =====
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(Color.decode("#bc5090")); // ชมพูม่วงหรู
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(100, 35)); // 👉 ปรับขนาดให้พอดี
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        backButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);

        // ===== รวมทุกอย่าง =====
        backgroundPanel.add(menuPanel, BorderLayout.CENTER);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(backgroundPanel);

        setVisible(true);
    }

    private void styleMenuButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(Color.decode("#ffc107")); // เหลืองทอง
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(250, 60));
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.decode("#ffb300")); // Hover เหลืองเข้มขึ้น
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.decode("#ffc107"));
            }
        });
    }
}

class Report1 extends JFrame {

    public Report1() {
        setTitle("Product Balance Report");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== พาเนลหลัก =====
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#d8f3dc")); // เขียวอ่อน
        panel.setBorder(BorderFactory.createLineBorder(Color.decode("#95d5b2"), 2)); // กรอบเขียวกลาง

        // ===== Header =====
        JLabel reportLabel = new JLabel("Product Balance Report", JLabel.CENTER);
        reportLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        reportLabel.setForeground(Color.decode("#1b4332")); // เขียวเข้ม
        reportLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(reportLabel, BorderLayout.NORTH);

        // ===== ตารางข้อมูล =====
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Product Code");
        model.addColumn("Product Name");
        model.addColumn("Product Price");
        model.addColumn("Quantity Available");

        JTable table = new JTable(model);
        table.setBackground(Color.decode("#fefae0")); // เหลืองอ่อน
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(Color.decode("#ccd5ae"));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.decode("#95d5b2"));
        table.getTableHeader().setForeground(Color.decode("#1b4332"));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ===== โหลดข้อมูล =====
        try {
            FileReader reader = new FileReader("products.json");
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray products = new JSONArray(tokener);

            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                model.addRow(new Object[]{
                    product.getString("product_code"),
                    product.getString("product_name"),
                    product.getInt("price"),
                    product.getInt("quantity")
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ===== ปุ่ม Back =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.decode("#d8f3dc"));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(Color.decode("#f9c74f")); // เหลืองทอง
        backButton.setForeground(Color.BLACK);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createLineBorder(Color.decode("#bc5090"), 2, true));
        backButton.addActionListener(e -> dispose());

        bottomPanel.add(backButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
}

class Report2 extends JFrame {

    public Report2() {
        setTitle("Sales Report");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== พาเนลหลัก =====
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#e9f5db")); // เขียวอ่อนพาสเทล
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Header Title =====
        JLabel titleLabel = new JLabel("Sales Report (Services & Products)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.decode("#1b4332"));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // ===== ตาราง =====
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Type");
        model.addColumn("Name");
        model.addColumn("Quantity Sold");
        model.addColumn("Unit Price");
        model.addColumn("Total Sales");

        JTable table = new JTable(model);
        table.setBackground(Color.decode("#fefae0")); // เหลืองครีม
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(Color.decode("#ccd5ae"));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.decode("#95d5b2"));
        table.getTableHeader().setForeground(Color.decode("#1b4332"));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        double totalRevenue = 0;

        try {
            // ===== ข้อมูลจาก receipts.json =====
            File serviceFile = new File("receipts.json");
            if (serviceFile.exists()) {
                JSONArray receipts = new JSONArray(new JSONTokener(
                        new InputStreamReader(new FileInputStream(serviceFile), StandardCharsets.UTF_8)));

                Map<String, Integer> serviceQtyMap = new HashMap<>();
                Map<String, Double> servicePriceMap = new HashMap<>();
                Map<String, Double> serviceTotalMap = new HashMap<>();

                for (int i = 0; i < receipts.length(); i++) {
                    JSONObject receipt = receipts.getJSONObject(i);
                    JSONArray services = receipt.getJSONArray("services");

                    for (int j = 0; j < services.length(); j++) {
                        JSONObject svc = services.getJSONObject(j);
                        String serviceId = svc.getString("service_id");
                        double price = svc.getDouble("price");

                        serviceQtyMap.put(serviceId, serviceQtyMap.getOrDefault(serviceId, 0) + 1);
                        servicePriceMap.put(serviceId, price);
                        serviceTotalMap.put(serviceId, serviceTotalMap.getOrDefault(serviceId, 0.0) + price);
                        totalRevenue += price;
                    }
                }

                Map<String, String> serviceNameMap = new HashMap<>();
                File servicesJson = new File("services.json");
                if (servicesJson.exists()) {
                    JSONArray services = new JSONArray(new JSONTokener(
                            new InputStreamReader(new FileInputStream(servicesJson), StandardCharsets.UTF_8)));
                    for (int i = 0; i < services.length(); i++) {
                        JSONObject svc = services.getJSONObject(i);
                        serviceNameMap.put(svc.getString("service_id"), svc.getString("name"));
                    }
                }

                for (String id : serviceQtyMap.keySet()) {
                    model.addRow(new Object[]{
                        "Service",
                        serviceNameMap.getOrDefault(id, id),
                        serviceQtyMap.get(id),
                        String.format("%.2f", servicePriceMap.get(id)),
                        String.format("%.2f", serviceTotalMap.get(id))
                    });
                }
            }

            // ===== ข้อมูลสินค้าจาก receiptSell.json =====
            File productFile = new File("receiptSell.json");
            if (productFile.exists()) {
                JSONArray receipts = new JSONArray(new JSONTokener(
                        new InputStreamReader(new FileInputStream(productFile), StandardCharsets.UTF_8)));

                Map<String, Integer> productQtyMap = new HashMap<>();
                Map<String, Double> productPriceMap = new HashMap<>();
                Map<String, Double> productTotalMap = new HashMap<>();

                for (int i = 0; i < receipts.length(); i++) {
                    JSONObject receipt = receipts.getJSONObject(i);
                    JSONArray items = receipt.getJSONArray("items");

                    for (int j = 0; j < items.length(); j++) {
                        JSONObject item = items.getJSONObject(j);
                        String name = item.getString("product_name");
                        int qty = item.getInt("quantity");
                        double unit = item.getDouble("unit_price");
                        double total = item.getDouble("total_price");

                        productQtyMap.put(name, productQtyMap.getOrDefault(name, 0) + qty);
                        productPriceMap.put(name, unit);
                        productTotalMap.put(name, productTotalMap.getOrDefault(name, 0.0) + total);
                        totalRevenue += total;
                    }
                }

                for (String name : productQtyMap.keySet()) {
                    model.addRow(new Object[]{
                        "Product",
                        name,
                        productQtyMap.get(name),
                        String.format("%.2f", productPriceMap.get(name)),
                        String.format("%.2f", productTotalMap.get(name))
                    });
                }
            }

            model.addRow(new Object[]{"", "", "", "Total Revenue:", String.format("%.2f", totalRevenue)});

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading data: " + e.getMessage());
        }

        // ===== ปุ่ม Close =====
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(Color.decode("#f9c74f"));
        closeButton.setForeground(Color.BLACK);
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createLineBorder(Color.decode("#bc5090"), 2, true));
        closeButton.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.decode("#e9f5db"));
        bottom.add(closeButton);
        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}

class BackOfficeMenu extends JFrame {

    public BackOfficeMenu() {
        setTitle("Back Office Management");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== พื้นหลัง Gradient ให้เหมือนหน้าแรก =====
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, Color.decode("#003f5c"),
                        0, getHeight(), Color.decode("#58508d"));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ===== Header =====
        JLabel titleLabel = new JLabel("Report Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE); // สีตัดกับพื้นหลัง

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);

        // ===== ปุ่ม Report =====
        JPanel panel = new JPanel(new GridLayout(2, 1, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JButton report1Btn = new JButton("Product Balance Report");
        styleButton(report1Btn, "#ffa600", "#002c54");

        JButton report2Btn = new JButton("Report sales of each service and product and total amount");
        styleButton(report2Btn, "#ff6361", "#002c54");

        report1Btn.addActionListener(e -> new Report1());
        report2Btn.addActionListener(e -> new Report2());

        panel.add(report1Btn);
        panel.add(report2Btn);

        // ===== ปุ่ม Back =====
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(Color.decode("#bc5090")); // ชมพูม่วง
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        backButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);

        // ===== รวมทุกส่วน =====
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void styleButton(JButton btn, String bgColor, String fgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(Color.decode(bgColor));
        btn.setForeground(Color.decode(fgColor));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.decode("#ffd166")); // Hover สีเหลืองอ่อน
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.decode(bgColor));
            }
        });
    }
}
