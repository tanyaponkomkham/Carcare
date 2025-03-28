
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import org.json.*;
import java.text.SimpleDateFormat;

public class sellCarcare {

    public static HashMap<String, Integer> cart = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CategoryMenu());
    }

    public static void launchShop() {
        SwingUtilities.invokeLater(() -> new CategoryMenu());
    }

    public static int getTotalCartItems() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static double getTotalCartPrice() {
        double total = 0;
        try {
            String content = new String(Files.readAllBytes(Paths.get("products.json")));
            JSONArray products = new JSONArray(content);
            for (String productName : cart.keySet()) {
                for (int i = 0; i < products.length(); i++) {
                    JSONObject p = products.getJSONObject(i);
                    if (p.getString("product_name").equals(productName)) {
                        total += p.getDouble("price") * cart.get(productName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    public static void saveReceiptToFile(String paymentMethod) {
        JSONArray receiptArray;
        File file = new File("receiptSell.json");

        try {
            if (file.exists() && file.length() > 0) {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                receiptArray = new JSONArray(content);
            } else {
                receiptArray = new JSONArray();
            }

            JSONArray itemsArray = new JSONArray();
            double total = getTotalCartPrice();
            String content = new String(Files.readAllBytes(Paths.get("products.json")));
            JSONArray products = new JSONArray(content);

            for (String productName : cart.keySet()) {
                for (int i = 0; i < products.length(); i++) {
                    JSONObject p = products.getJSONObject(i);
                    if (p.getString("product_name").equals(productName)) {
                        int qty = cart.get(productName);
                        double unitPrice = p.getDouble("price");
                        double totalPrice = qty * unitPrice;

                        JSONObject item = new JSONObject();
                        item.put("name", productName);
                        item.put("quantity", qty);
                        item.put("unit_price", unitPrice);
                        item.put("total_price", totalPrice);
                        itemsArray.put(item);
                        break;
                    }
                }
            }

            JSONObject receipt = new JSONObject();
            receipt.put("receipt_id", "R" + String.format("%03d", receiptArray.length() + 1));
            receipt.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            receipt.put("items", itemsArray);
            receipt.put("total", total);
            receipt.put("payment_method", paymentMethod);

            receiptArray.put(receipt);
            Files.write(file.toPath(), receiptArray.toString(4).getBytes(StandardCharsets.UTF_8));

            showReceipt(receipt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showReceipt(JSONObject receipt) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append(String.format("%30s\n", "CAR CARE SHOP RECEIPT"));
        sb.append("==============================================\n");
        sb.append("Date: " + receipt.getString("date") + "\n");
        sb.append("Receipt ID: " + receipt.getString("receipt_id") + "\n");
        sb.append("----------------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s\n", "Product", "Qty", "Price"));
        sb.append("----------------------------------------------\n");

        JSONArray items = receipt.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            sb.append(String.format("%-20s %5d %10.2f\n", item.getString("name"), item.getInt("quantity"), item.getDouble("total_price")));
        }

        sb.append("----------------------------------------------\n");
        sb.append(String.format("%-20s : %,.2f\n", "Total", receipt.getDouble("total")));
        sb.append(String.format("%-20s : %s\n", "Payment", receipt.getString("payment_method")));
        sb.append("==============================================\n");
        sb.append(String.format("%30s\n", "Thank You!"));
        sb.append("==============================================\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));

        JDialog dialog = new JDialog();
        dialog.setTitle("Receipt");
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel panel = new JPanel();
        panel.add(close);
        dialog.add(panel, BorderLayout.SOUTH);

        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(null);
        // สุดท้ายของ showReceipt()
        dialog.setVisible(true);
        return;

    }
}

// ================= Category Menu =================
class CategoryMenu extends JFrame {

    public static boolean isOpen = false;

    public CategoryMenu() {
        if (isOpen) {
            return;
        }
        isOpen = true;

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                isOpen = false;
            }
        });

        setTitle("Product Categories");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // ===== Gradient Background Panel =====
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x2f2f2f), // เทาเข้ม
                        0, getHeight(), new Color(0x58508d) // ม่วงเข้ม
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // ===== Title =====
        JLabel titleLabel = new JLabel("Select Product Category", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ===== Category Buttons Panel =====
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        String[] categories = {
            "Car Cleaning Liquids",
            "Car Cleaning Accessories",
            "Car Parts & Maintenance",
            "Interior Care Products"
        };

        for (String category : categories) {
            JButton btn = createStyledButton(category);
            btn.addActionListener(e -> {
                if (!ProductList.isOpen) {
                    ProductList.isOpen = true;
                    dispose();
                    new ProductList(category);
                }
            });
            buttonPanel.add(btn);
        }

        // ===== Back Button =====
        JButton back = createStyledButton("Back");
        back.setBackground(new Color(0xbc5090)); // ม่วงชมพู
        back.setForeground(Color.WHITE);
        back.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "If you go back, your cart will be cleared.\nDo you want to continue?",
                    "Confirm Back",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                sellCarcare.cart.clear();
                dispose();
            }
        });

        buttonPanel.add(back);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(0xffa600)); // เหลืองทอง
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        btn.setPreferredSize(new Dimension(200, 50));

        // Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0xffd54f)); // เหลืองอ่อน
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0xffa600));
            }
        });

        return btn;
    }
}

// ================= Product List =================
class ProductList extends JFrame {

    public static boolean isOpen = false;
    private JButton cartBtn;

    public ProductList(String category) {
        setTitle(category);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        isOpen = true;
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                isOpen = false;
            }
        });

        getContentPane().setBackground(new Color(30, 30, 30)); // พื้นหลังเทาเข้ม

        JPanel productPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        productPanel.setBackground(new Color(30, 30, 30));
        productPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));

        try {
            String content = new String(Files.readAllBytes(Paths.get("products.json")));
            JSONArray products = new JSONArray(content);
            int imageIndex = getCategoryStartIndex(category);

            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                String name = product.getString("product_name");
                double price = product.getDouble("price");
                int stock = product.getInt("quantity");
                String productCategory = product.optString("category", "");

                if (matchesCategory(productCategory, category)) {
                    JPanel row = new JPanel(new BorderLayout(10, 10));
                    row.setBackground(new Color(45, 45, 45));
                    row.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
                    row.setPreferredSize(new Dimension(720, 120));

                    String imagePath = "images/images_" + imageIndex + ".jpg";
                    File imageFile = new File(imagePath);
                    ImageIcon icon = imageFile.exists()
                            ? new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH))
                            : new ImageIcon(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
                    JLabel imageLabel = new JLabel(icon);
                    imageLabel.setPreferredSize(new Dimension(100, 100));
                    row.add(imageLabel, BorderLayout.WEST);

                    JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                    infoPanel.setBackground(new Color(45, 45, 45));
                    JLabel nameLabel = new JLabel(name);
                    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    nameLabel.setForeground(new Color(255, 255, 255));
                    JLabel priceLabel = new JLabel("Price: " + price + "฿   Stock: " + stock);
                    priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    priceLabel.setForeground(new Color(200, 200, 200));
                    infoPanel.add(nameLabel);
                    infoPanel.add(priceLabel);
                    row.add(infoPanel, BorderLayout.CENTER);

                    JButton addBtn = new JButton("Add to Cart");
                    addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    addBtn.setPreferredSize(new Dimension(120, 40));
                    addBtn.setBackground(new Color(255, 166, 0)); // เหลืองสด
                    addBtn.setForeground(Color.BLACK);
                    addBtn.setFocusPainted(false);
                    addBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
                    addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    addBtn.addActionListener(e -> {
                        sellCarcare.cart.put(name, sellCarcare.cart.getOrDefault(name, 0) + 1);
                        updateCartCount();
                    });
                    row.add(addBtn, BorderLayout.EAST);

                    productPanel.add(row);
                    imageIndex++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(88, 80, 141)); // ม่วงเข้ม
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            new CategoryMenu();
        });

        cartBtn = new JButton("Cart (" + sellCarcare.getTotalCartItems() + ")");
        cartBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cartBtn.setBackground(new Color(255, 166, 0));
        cartBtn.setForeground(Color.BLACK);
        cartBtn.setFocusPainted(false);
        cartBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        cartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartBtn.addActionListener(e -> new CartWindow(() -> updateCartCount()));

        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(25, 25, 25));
        navbar.add(backBtn, BorderLayout.WEST);
        navbar.add(cartBtn, BorderLayout.EAST);
        navbar.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(navbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private boolean matchesCategory(String productCategory, String selectedCategory) {
        return productCategory.equalsIgnoreCase(selectedCategory);
    }

    private int getCategoryStartIndex(String category) {
        switch (category) {
            case "Car Cleaning Liquids":
                return 1;
            case "Car Cleaning Accessories":
                return 6;
            case "Car Parts & Maintenance":
                return 11;
            case "Interior Care Products":
                return 16;
            default:
                return 1;
        }
    }

    private void updateCartCount() {
        cartBtn.setText("Cart (" + sellCarcare.getTotalCartItems() + ")");
    }
}

// ================= Cart Window =================
class CartWindow extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalPriceLabel;
    private Runnable onCartCleared;
    public static boolean isOpen = false;

    public CartWindow(Runnable onCartCleared) {
        this.onCartCleared = onCartCleared;

        setTitle("Shopping Cart");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ====== Gradient Background ======
        JPanel background = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.decode("#003f5c"),
                        0, getHeight(), Color.decode("#58508d"));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("🛒 Shopping Cart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        background.add(titleLabel, BorderLayout.NORTH);

        // ====== Table ======
        String[] columnNames = {"Product Name", "Quantity", "Total Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(Color.decode("#ffc107"));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.decode("#ffa600"));
        table.getTableHeader().setForeground(Color.BLACK);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        background.add(tableScrollPane, BorderLayout.CENTER);

        // ====== Total & Buttons ======
        totalPriceLabel = new JLabel("Total: 0฿", SwingConstants.RIGHT);
        totalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalPriceLabel.setForeground(Color.WHITE);
        totalPriceLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        updateTable();

        JButton purchaseBtn = createStyledButton("Purchase", "#fdd835", "#002c54");
        purchaseBtn.addActionListener(e -> processPurchaseAndPrintReceipt());

        JButton cancelBtn = createStyledButton("Cancel", "#bc5090", "#ffffff");
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(purchaseBtn);
        buttonPanel.add(cancelBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(totalPriceLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        background.add(bottomPanel, BorderLayout.SOUTH);

        add(background);
        setVisible(true);
    }

    private JButton createStyledButton(String text, String bgColor, String fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(Color.decode(bgColor));
        button.setForeground(Color.decode(fgColor));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.decode("#ffd54f")); // Hover สีอ่อนลง
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.decode(bgColor));
            }
        });
        return button;
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        try {
            String content = new String(Files.readAllBytes(Paths.get("products.json")));
            JSONArray products = new JSONArray(content);
            for (String productName : sellCarcare.cart.keySet()) {
                for (int i = 0; i < products.length(); i++) {
                    JSONObject p = products.getJSONObject(i);
                    if (p.getString("product_name").equals(productName)) {
                        int qty = sellCarcare.cart.get(productName);
                        double price = p.getDouble("price") * qty;
                        tableModel.addRow(new Object[]{productName, qty, String.format("%.2f฿", price)});
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalPriceLabel.setText("Total: " + String.format("%.2f฿", sellCarcare.getTotalCartPrice()));
    }

    private void processPurchase() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("products.json")));
            JSONArray products = new JSONArray(content);

            for (String productName : sellCarcare.cart.keySet()) {
                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);
                    if (product.getString("product_name").equals(productName)) {
                        int newQty = product.getInt("quantity") - sellCarcare.cart.get(productName);
                        product.put("quantity", Math.max(newQty, 0));
                    }
                }
            }

            PrintWriter writer = new PrintWriter("products.json");
            writer.write(products.toString(4));
            writer.close();

            sellCarcare.cart.clear();
            JOptionPane.showMessageDialog(this, "Purchase Successful! Stock updated.");
            dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSellReceipt(JSONObject receipt) {
        StringBuilder sb = new StringBuilder();
        String line = "==============================================";
        String subLine = "----------------------------------------------";

        sb.append(line).append("\n");
        sb.append(String.format("%-46s\n", "           CAR CARE SHOP RECEIPT"));
        sb.append(line).append("\n");
        sb.append(String.format("%-20s : %s\n", "Receipt ID", receipt.getString("receipt_id")));
        sb.append(String.format("%-20s : %s\n", "Date", receipt.getString("date")));
        sb.append(subLine).append("\n");
        sb.append(String.format("%-30s %15s\n", "Product", "Total (THB)"));
        sb.append(subLine).append("\n");

        JSONArray items = receipt.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            sb.append(String.format("%-30s %15.2f\n", item.getString("product_name"), item.getDouble("total_price")));
            sb.append(String.format("  (%dx %.2f)\n", item.getInt("quantity"), item.getDouble("unit_price")));
        }

        sb.append(subLine).append("\n");
        sb.append(String.format("%-20s : %,.2f THB\n", "Total", receipt.getDouble("total")));
        sb.append(String.format("%-20s : %s\n", "Payment", receipt.getString("payment_method")));
        sb.append(line).append("\n");
        sb.append(String.format("%-46s\n", "      Thank you for shopping with us!"));
        sb.append(line).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));

        JDialog dialog = new JDialog(this, "Receipt", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void processPurchaseAndPrintReceipt() {
        double totalAmount = sellCarcare.getTotalCartPrice();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm payment of " + totalAmount + "฿?\nYou will not be able to modify the cart after this.",
                "Payment Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Payment cancelled.");
            return;
        }

        String[] paymentMethods = {"Cash", "Bank Transfer", "Credit Card"};
        String paymentMethod = (String) JOptionPane.showInputDialog(this,
                "Select payment method:", "Payment",
                JOptionPane.QUESTION_MESSAGE, null, paymentMethods, paymentMethods[0]);

        if (paymentMethod == null) {
            JOptionPane.showMessageDialog(this, "Payment method not selected. Cancelling transaction.");
            return;
        }

        JSONArray items = new JSONArray();
        double total = 0;

        try {
            String content = new String(Files.readAllBytes(Paths.get("products.json")));
            JSONArray products = new JSONArray(content);

            for (String productName : sellCarcare.cart.keySet()) {
                for (int i = 0; i < products.length(); i++) {
                    JSONObject p = products.getJSONObject(i);
                    if (p.getString("product_name").equals(productName)) {
                        int qty = sellCarcare.cart.get(productName);
                        double price = p.getDouble("price");
                        double totalPrice = price * qty;

                        int newQty = p.getInt("quantity") - qty;
                        p.put("quantity", Math.max(newQty, 0));

                        JSONObject item = new JSONObject();
                        item.put("product_name", productName);
                        item.put("quantity", qty);
                        item.put("unit_price", price);
                        item.put("total_price", totalPrice);
                        items.put(item);

                        total += totalPrice;
                        break;
                    }
                }
            }

            PrintWriter writer = new PrintWriter("products.json");
            writer.write(products.toString(4));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File file = new File("receiptSell.json");
            JSONArray receipts = file.exists() && file.length() > 0
                    ? new JSONArray(new JSONTokener(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)))
                    : new JSONArray();

            JSONObject receipt = new JSONObject();
            receipt.put("receipt_id", "S" + String.format("%03d", receipts.length() + 1));
            receipt.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            receipt.put("items", items);
            receipt.put("total", total);
            receipt.put("payment_method", paymentMethod);
            receipts.put(receipt);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                writer.write(receipts.toString(4));
            }

            showSellReceipt(receipt);
            sellCarcare.cart.clear();

            if (onCartCleared != null) {
                onCartCleared.run();
            }

            dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
