package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PatientPanel extends JPanel {

    private final ClinicFacade facade;
    private final ClinicAppFrame frame;

    private int patientId = -1;

    // Booking UI
    private final JComboBox<String> cbSpecialization = new JComboBox<>();
    private final DefaultListModel<Doctor> doctorsModel = new DefaultListModel<>();
    private final JList<Doctor> listDoctors = new JList<>(doctorsModel);

    private final JTextField tfDate = new JTextField("2025-12-31");
    private final DefaultListModel<LocalTime> slotsModel = new DefaultListModel<>();
    private final JList<LocalTime> listSlots = new JList<>(slotsModel);

    // Appointments table
    private final DefaultTableModel apptTableModel = new DefaultTableModel(
            new Object[]{"ID", "Doctor ID", "Date/Time", "Status"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tableAppointments = new JTable(apptTableModel);

    public PatientPanel(ClinicFacade facade, ClinicAppFrame frame) {
        this.facade = facade;
        this.frame = frame;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Patient Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> frame.showLogin());

        bar.add(title, BorderLayout.WEST);
        bar.add(logout, BorderLayout.EAST);
        return bar;
    }

    private JComponent buildBody() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.45);

        split.setLeftComponent(buildBookingCard());
        split.setRightComponent(buildAppointmentsCard());

        return split;
    }

    private JComponent buildBookingCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createTitledBorder("Book appointment"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Specialization:"), c);
        c.gridy = 1;
        form.add(cbSpecialization, c);

        JButton btnLoadDoctors = new JButton("Load doctors");
        btnLoadDoctors.addActionListener(e -> loadDoctors());
        c.gridy = 2;
        form.add(btnLoadDoctors, c);

        c.gridy = 3;
        form.add(new JLabel("Doctors:"), c);

        listDoctors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listDoctors.setVisibleRowCount(8);
        JScrollPane spDoctors = new JScrollPane(listDoctors);
        c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        form.add(spDoctors, c);

        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        form.add(new JLabel("Date (YYYY-MM-DD):"), c);

        c.gridy = 6;
        form.add(tfDate, c);

        JButton btnLoadSlots = new JButton("Load available slots");
        btnLoadSlots.addActionListener(e -> loadSlots());
        c.gridy = 7;
        form.add(btnLoadSlots, c);

        c.gridy = 8;
        form.add(new JLabel("Available times:"), c);

        listSlots.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSlots.setVisibleRowCount(8);
        JScrollPane spSlots = new JScrollPane(listSlots);
        c.gridy = 9;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        form.add(spSlots, c);

        JButton btnBook = new JButton("Book selected time");
        btnBook.addActionListener(e -> bookSelectedSlot());
        c.gridy = 10;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        form.add(btnBook, c);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildAppointmentsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createTitledBorder("My appointments"));

        tableAppointments.setRowHeight(24);
        JScrollPane sp = new JScrollPane(tableAppointments);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshAppointments());

        JButton cancel = new JButton("Cancel selected");
        cancel.addActionListener(e -> cancelSelected());

        JButton complete = new JButton("Complete selected");
        complete.addActionListener(e -> completeSelected());

        buttons.add(refresh);
        buttons.add(cancel);
        buttons.add(complete);

        card.add(buttons, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void onShow() {
        loadSpecializations();
        refreshAppointments();
    }

    private void loadSpecializations() {
        cbSpecialization.removeAllItems();
        for (String s : facade.getAllSpecializations()) {
            cbSpecialization.addItem(s);
        }
        doctorsModel.clear();
        slotsModel.clear();
    }

    private void loadDoctors() {
        doctorsModel.clear();
        String spec = (String) cbSpecialization.getSelectedItem();
        if (spec == null) return;

        List<Doctor> docs = facade.getDoctorsBySpecialization(spec);
        for (Doctor d : docs) doctorsModel.addElement(d);

        if (!docs.isEmpty()) listDoctors.setSelectedIndex(0);
    }

    private void loadSlots() {
        slotsModel.clear();

        Doctor d = listDoctors.getSelectedValue();
        if (d == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor first.");
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(tfDate.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        List<LocalTime> slots = facade.getAvailableSlots(d.getId(), date);
        if (slots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available slots (maybe weekend or fully booked).");
        }
        for (LocalTime t : slots) slotsModel.addElement(t);

        if (!slots.isEmpty()) listSlots.setSelectedIndex(0);
    }

    private void bookSelectedSlot() {
        Doctor d = listDoctors.getSelectedValue();
        LocalTime t = listSlots.getSelectedValue();

        if (d == null || t == null) {
            JOptionPane.showMessageDialog(this, "Select doctor and time slot first.");
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(tfDate.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        boolean ok = facade.bookAppointmentInSlot(patientId, d.getId(), date, t);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Booked successfully.");
            refreshAppointments();
            loadSlots();
        } else {
            JOptionPane.showMessageDialog(this, "Booking failed. Check console/logs.");
        }
    }

    private void refreshAppointments() {
        apptTableModel.setRowCount(0);
        List<Appointment> list = facade.getAppointmentsForPatient(patientId);
        for (Appointment a : list) {
            apptTableModel.addRow(new Object[]{
                    a.getId(),
                    a.getDoctorId(),
                    a.getDateTime(),
                    a.getStatus()
            });
        }
    }

    private Integer getSelectedAppointmentId() {
        int row = tableAppointments.getSelectedRow();
        if (row < 0) return null;
        Object v = apptTableModel.getValueAt(row, 0);
        if (v == null) return null;
        return Integer.parseInt(String.valueOf(v));
    }

    private void cancelSelected() {
        Integer id = getSelectedAppointmentId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment in the table.");
            return;
        }
        facade.cancelAppointment(id);
        refreshAppointments();
    }

    private void completeSelected() {
        Integer id = getSelectedAppointmentId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment in the table.");
            return;
        }
        facade.completeAppointment(id);
        refreshAppointments();
    }
}
