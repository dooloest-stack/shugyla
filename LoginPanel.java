package org.example;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final ClinicFacade facade;
    private final ClinicAppFrame frame;

    private final JTextField tfPatientId = new JTextField();
    private final JTextField tfName = new JTextField();
    private final JTextField tfPhone = new JTextField();

    public LoginPanel(ClinicFacade facade, ClinicAppFrame frame) {
        this.facade = facade;
        this.frame = frame;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Clinic App");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));

        center.add(buildLoginCard());
        center.add(buildRegisterCard());

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JComponent buildLoginCard() {
        JPanel card = new JPanel();
        card.setBorder(BorderFactory.createTitledBorder("Login (Patient)"));
        card.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.gridx = 0; c.gridy = 0;
        card.add(new JLabel("Patient ID:"), c);

        c.gridy = 1;
        card.add(tfPatientId, c);

        JButton btn = new JButton("Login");
        btn.addActionListener(e -> doLogin());

        c.gridy = 2;
        card.add(btn, c);

        JLabel hint = new JLabel("Tip: if you don't have an ID, register on the right.");
        hint.setFont(hint.getFont().deriveFont(12f));
        c.gridy = 3;
        card.add(hint, c);

        return card;
    }

    private JComponent buildRegisterCard() {
        JPanel card = new JPanel();
        card.setBorder(BorderFactory.createTitledBorder("Register (New patient)"));
        card.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.gridx = 0; c.gridy = 0;
        card.add(new JLabel("Name:"), c);
        c.gridy = 1;
        card.add(tfName, c);

        c.gridy = 2;
        card.add(new JLabel("Phone:"), c);
        c.gridy = 3;
        card.add(tfPhone, c);

        JButton btn = new JButton("Register");
        btn.addActionListener(e -> doRegister());

        c.gridy = 4;
        card.add(btn, c);

        return card;
    }

    private void doLogin() {
        try {
            int id = Integer.parseInt(tfPatientId.getText().trim());
            if (!facade.patientExists(id)) {
                JOptionPane.showMessageDialog(this, "Patient not found. Please register first.");
                return;
            }
            frame.showPatient(id);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Patient ID must be a number.");
        }
    }

    private void doRegister() {
        String name = tfName.getText().trim();
        String phone = tfPhone.getText().trim();
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill name and phone.");
            return;
        }

        int newId = facade.registerPatient(name, phone);
        JOptionPane.showMessageDialog(this, "Registered successfully. Your Patient ID: " + newId);
        tfPatientId.setText(String.valueOf(newId));
        tfName.setText("");
        tfPhone.setText("");
    }

    public void onShow() {
        tfPatientId.requestFocusInWindow();
    }
}
