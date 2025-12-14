package org.example;

import javax.swing.*;
import java.awt.*;

public class ClinicAppFrame extends JFrame {

    private final ClinicFacade facade;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private final LoginPanel loginPanel;
    private final PatientPanel patientPanel;

    public ClinicAppFrame(ClinicFacade facade) {
        super("Clinic Appointment System");
        this.facade = facade;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(facade, this);
        patientPanel = new PatientPanel(facade, this);

        root.add(loginPanel, "login");
        root.add(patientPanel, "patient");

        setContentPane(root);
        showLogin();
    }

    public void showLogin() {
        cards.show(root, "login");
        loginPanel.onShow();
    }

    public void showPatient(int patientId) {
        patientPanel.setPatientId(patientId);
        cards.show(root, "patient");
        patientPanel.onShow();
    }
}
