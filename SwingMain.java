package org.example;

import javax.swing.*;

public class SwingMain {
    public static void main(String[] args) {
        // Init DB and seed doctors (reuse your existing logic)
        DatabaseInitializer.init();
        ClinicFacade facade = new ClinicFacade();

        SeedData.seedDoctorsIfNeeded(facade);

        SwingUtilities.invokeLater(() -> new ClinicAppFrame(facade).setVisible(true));


        // If you already have seedDoctorsIfNeeded in Main, you can call it.
        // Here we call it directly via Main.seedDoctorsIfNeeded if you keep it public.
        // Otherwise, you can simply rely on DatabaseInitializer.init() to seed.
        try {
            // If Main has seedDoctorsIfNeeded as private, this will not compile; in that case, delete this block.
            // Main.seedDoctorsIfNeeded(facade);
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) { }

            ClinicAppFrame frame = new ClinicAppFrame(facade);
            frame.setVisible(true);
        });
    }
}
