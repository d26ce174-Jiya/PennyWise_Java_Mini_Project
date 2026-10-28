import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Smart Expense Tracker - a small real-world Java Swing mini project.
 * Uses core Java, OOP, Swing, file handling, and CSV persistence.
 */
public class SmartExpenseTracker extends JFrame {
    private static final Color NAVY = new Color(24, 35, 58);
    private static final Color BLUE = new Color(74, 108, 247);
    private static final Color BG = new Color(245, 247, 252);
    private static final Color MUTED = new Color(112, 122, 145);
    private static final String FILE_NAME = "expenses.csv";

    private final List<Expense> expenses = new ArrayList<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Date", "Title", "Category", "Payment", "Amount (₹)"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private JTable table;
    private JLabel totalValue, monthValue, countValue;
    private JTextField titleField, amountField, dateField, searchField;
    private JComboBox<String> categoryBox, paymentBox;
    private JLabel statusLabel;

    public SmartExpenseTracker() {
        setTitle("PennyWise | Smart Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 680));
        setSize(1120, 760);
        setLocationRelativeTo(null);
        buildUI();
        loadExpenses();
        refreshTable("");
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        setContentPane(root);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(28, 20, 24, 20));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("✦ PennyWise");
        brand.setFont(new Font("SansSerif", Font.BOLD, 23));
        brand.setForeground(Color.WHITE);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(8));
        JLabel tagline = new JLabel("<html><font color='#AAB5D1'>Your money, in focus.</font></html>");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sidebar.add(tagline);
        sidebar.add(Box.createVerticalStrut(42));
        sidebar.add(sideItem("▦", "Overview", true));
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(sideItem("＋", "Add an expense", false));
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(sideItem("◷", "Recent activity", false));
        sidebar.add(Box.createVerticalGlue());

        JPanel tip = new JPanel();
        tip.setBackground(new Color(36, 50, 80));
        tip.setLayout(new BoxLayout(tip, BoxLayout.Y_AXIS));
        tip.setBorder(new EmptyBorder(15, 14, 15, 14));
        tip.add(new JLabel("<html><font color='#FFFFFF'><b>Small steps add up</b></font></html>"));
        tip.add(Box.createVerticalStrut(7));
        tip.add(new JLabel("<html><font color='#B8C4E0'>Track today.<br>Build better habits.</font></html>"));
        sidebar.add(tip);

        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(26, 30, 22, 30));
        root.add(sidebar, BorderLayout.WEST);
        root.add(main, BorderLayout.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Expense overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(NAVY);
        JLabel subtitle = new JLabel("A clear picture of where your money goes.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);
        header.add(heading, BorderLayout.WEST);
        JLabel today = new JLabel("TODAY  ·  " + LocalDate.now());
        today.setForeground(MUTED);
        today.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.add(today, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        main.add(content, BorderLayout.CENTER);

        JPanel topContent = new JPanel(new BorderLayout(0, 16));
        topContent.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setOpaque(false);
        totalValue = new JLabel("₹0.00");
        monthValue = new JLabel("₹0.00");
        countValue = new JLabel("0");
        cards.add(statCard("TOTAL SPENDING", totalValue, "All recorded expenses", new Color(74, 108, 247)));
        cards.add(statCard("THIS MONTH", monthValue, "Current month total", new Color(27, 166, 129)));
        cards.add(statCard("TRANSACTIONS", countValue, "Expenses recorded", new Color(235, 151, 64)));
        topContent.add(cards, BorderLayout.NORTH);

        JPanel formCard = new JPanel(new BorderLayout(0, 14));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 243)),
                new EmptyBorder(18, 20, 18, 20)));
        JLabel formTitle = new JLabel("＋  Add a new expense");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        formTitle.setForeground(NAVY);
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        titleField = field("e.g. Coffee with friends");
        amountField = field("e.g. 180.00");
        dateField = field(LocalDate.now().toString());
        categoryBox = new JComboBox<>(new String[]{"Food & Drinks", "Transport", "Shopping", "Bills", "Health", "Education", "Entertainment", "Other"});
        paymentBox = new JComboBox<>(new String[]{"UPI", "Cash", "Card", "Net Banking"});
        styleCombo(categoryBox);
        styleCombo(paymentBox);

        addFormField(form, c, 0, 0, "Expense name", titleField);
        addFormField(form, c, 1, 0, "Amount (₹)", amountField);
        addFormField(form, c, 0, 1, "Category", categoryBox);
        addFormField(form, c, 1, 1, "Payment method", paymentBox);
        addFormField(form, c, 0, 2, "Date (YYYY-MM-DD)", dateField);

        JButton addButton = new JButton("Add expense  →");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(BLUE);
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(12, 20, 12, 20));
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addExpense());
        c.gridx = 1; c.gridy = 2; c.gridwidth = 1; c.weightx = 0;
        form.add(addButton, c);
        formCard.add(form, BorderLayout.CENTER);
        topContent.add(formCard, BorderLayout.CENTER);
        content.add(topContent, BorderLayout.NORTH);

        JPanel listCard = new JPanel(new BorderLayout(0, 12));
        listCard.setBackground(Color.WHITE);
        listCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 243)),
                new EmptyBorder(18, 20, 14, 20)));
        JPanel listHeader = new JPanel(new BorderLayout(12, 0));
        listHeader.setOpaque(false);
        JLabel recent = new JLabel("Recent transactions");
        recent.setFont(new Font("SansSerif", Font.BOLD, 17));
        recent.setForeground(NAVY);
        listHeader.add(recent, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        searchField = new JTextField(14);
        searchField.setPreferredSize(new Dimension(170, 34));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setToolTipText("Search title or category");
        searchField.putClientProperty("JTextField.placeholderText", "Search expenses...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshTable(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshTable(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshTable(searchField.getText()); }
        });
        JButton deleteButton = new JButton("Delete selected");
        styleSecondaryButton(deleteButton);
        deleteButton.addActionListener(e -> deleteSelected());
        actions.add(searchField);
        actions.add(deleteButton);
        listHeader.add(actions, BorderLayout.EAST);
        listCard.add(listHeader, BorderLayout.NORTH);

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(new Color(47, 57, 78));
        table.setGridColor(new Color(237, 240, 246));
        table.setSelectionBackground(new Color(231, 237, 255));
        table.setSelectionForeground(NAVY);
        table.setShowVerticalLines(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setBackground(new Color(249, 250, 253));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(237, 240, 246)));
        listCard.add(scroll, BorderLayout.CENTER);
        statusLabel = new JLabel("Your expenses are saved on this computer.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(MUTED);
        listCard.add(statusLabel, BorderLayout.SOUTH);
        content.add(listCard, BorderLayout.CENTER);
    }

    private JPanel sideItem(String symbol, String text, boolean active) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 11));
        p.setMaximumSize(new Dimension(190, 43));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBackground(active ? new Color(56, 72, 111) : NAVY);
        JLabel icon = new JLabel(symbol);
        icon.setForeground(active ? Color.WHITE : new Color(165, 178, 208));
        icon.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel label = new JLabel(text);
        label.setForeground(active ? Color.WHITE : new Color(190, 199, 220));
        label.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 13));
        p.add(icon); p.add(label);
        return p;
    }

    private JPanel statCard(String title, JLabel value, String caption, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 243)),
                new EmptyBorder(16, 17, 16, 17)));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MUTED);
        value.setFont(new Font("SansSerif", Font.BOLD, 25));
        value.setForeground(NAVY);
        JLabel foot = new JLabel(caption);
        foot.setFont(new Font("SansSerif", Font.PLAIN, 11));
        foot.setForeground(MUTED);
        JPanel lower = new JPanel();
        lower.setOpaque(false);
        lower.setLayout(new BoxLayout(lower, BoxLayout.Y_AXIS));
        lower.add(value); lower.add(Box.createVerticalStrut(5)); lower.add(foot);
        card.add(label, BorderLayout.NORTH);
        card.add(lower, BorderLayout.CENTER);
        JPanel line = new JPanel();
        line.setBackground(accent);
        line.setPreferredSize(new Dimension(4, 0));
        card.add(line, BorderLayout.WEST);
        return card;
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.setText(placeholder);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setPreferredSize(new Dimension(180, 35));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 236)),
                new EmptyBorder(7, 9, 7, 9)));
        return f;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(180, 35));
    }

    private void addFormField(JPanel panel, GridBagConstraints c, int x, int y, String label, JComponent field) {
        JPanel wrap = new JPanel(new BorderLayout(0, 5));
        wrap.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(MUTED);
        wrap.add(l, BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        c.gridx = x; c.gridy = y; c.gridwidth = 1; c.weightx = 1;
        panel.add(wrap, c);
    }

    private void styleSecondaryButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setForeground(NAVY);
        b.setBackground(new Color(246, 248, 253));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 227, 238)),
                new EmptyBorder(8, 11, 8, 11)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void addExpense() {
        String title = titleField.getText().trim();
        String amountText = amountField.getText().trim();
        String dateText = dateField.getText().trim();
        if (title.isEmpty() || title.startsWith("e.g.")) {
            showError("Please enter an expense name."); return;
        }
        double amount;
        LocalDate date;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) { showError("Amount must be greater than zero."); return; }
        } catch (NumberFormatException ex) {
            showError("Enter a valid amount, for example 180.50."); return;
        }
        try {
            date = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            showError("Enter the date in YYYY-MM-DD format."); return;
        }

        Expense expense = new Expense(title, (String) categoryBox.getSelectedItem(),
                (String) paymentBox.getSelectedItem(), amount, date);
        expenses.add(0, expense);
        saveExpenses();
        refreshTable(searchField.getText());
        titleField.setText("");
        amountField.setText("");
        dateField.setText(LocalDate.now().toString());
        statusLabel.setText("Expense added and saved successfully.");
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a transaction from the table first.",
                    "Nothing selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String visibleTitle = String.valueOf(tableModel.getValueAt(row, 1));
        String visibleDate = String.valueOf(tableModel.getValueAt(row, 0));
        for (int i = 0; i < expenses.size(); i++) {
            Expense ex = expenses.get(i);
            if (ex.title.equals(visibleTitle) && ex.date.toString().equals(visibleDate)) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete \"" + ex.title + "\"?",
                        "Confirm deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    expenses.remove(i);
                    saveExpenses();
                    refreshTable(searchField.getText());
                    statusLabel.setText("Selected expense deleted.");
                }
                return;
            }
        }
    }

    private void refreshTable(String query) {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        String q = query == null ? "" : query.trim().toLowerCase();
        double total = 0, month = 0;
        int count = 0;
        LocalDate now = LocalDate.now();
        for (Expense e : expenses) {
            total += e.amount;
            if (e.date.getYear() == now.getYear() && e.date.getMonth() == now.getMonth()) month += e.amount;
            if (e.title.toLowerCase().contains(q) || e.category.toLowerCase().contains(q) || e.payment.toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{e.date.toString(), e.title, e.category, e.payment, String.format("%.2f", e.amount)});
                count++;
            }
        }
        totalValue.setText("₹" + String.format("%,.2f", total));
        monthValue.setText("₹" + String.format("%,.2f", month));
        countValue.setText(String.valueOf(expenses.size()));
        if (!q.isEmpty()) statusLabel.setText(count + " matching transaction(s).");
        else statusLabel.setText("Your expenses are saved on this computer.");
    }

    private void saveExpenses() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                out.println(csv(e.date.toString()) + "," + csv(e.title) + "," + csv(e.category) + "," +
                        csv(e.payment) + "," + e.amount);
            }
        } catch (IOException ex) {
            statusLabel.setText("Could not save expenses: " + ex.getMessage());
        }
    }

    private String csv(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private void loadExpenses() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> parts = parseCsvLine(line);
                if (parts.size() == 5) {
                    try {
                        expenses.add(new Expense(parts.get(1), parts.get(2), parts.get(3),
                                Double.parseDouble(parts.get(4)), LocalDate.parse(parts.get(0))));
                    } catch (Exception ignored) { }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load saved expenses.", "File warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); i++;
                } else quoted = !quoted;
            } else if (ch == ',' && !quoted) {
                result.add(current.toString()); current.setLength(0);
            } else current.append(ch);
        }
        result.add(current.toString());
        return result;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Check your input", JOptionPane.WARNING_MESSAGE);
    }

    private static class Expense {
        private final String title, category, payment;
        private final double amount;
        private final LocalDate date;
        Expense(String title, String category, String payment, double amount, LocalDate date) {
            this.title = title; this.category = category; this.payment = payment;
            this.amount = amount; this.date = date;
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new SmartExpenseTracker().setVisible(true));
    }
}
