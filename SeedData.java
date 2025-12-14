package org.example;

import java.util.List;

public class SeedData {
    private SeedData(){}

    public static void seedDoctorsIfNeeded(ClinicFacade facade){
        List<String> specs = facade.getAllSpecializations();
        if (!specs.isEmpty()) {
            return; // doctors already exist
        }
        facade.addDoctor("Dr. Alice", "therapist");
        facade.addDoctor("Dr. Bob", "surgeon");
        facade.addDoctor("Dr. John", "therapist");
        facade.addDoctor("Dr. Emma", "surgeon");
        facade.addDoctor("Dr. David", "therapist");
        facade.addDoctor("Dr. Smith", "cardiologist");
        facade.addDoctor("Dr. Anna", "cardiologist");
    }
}
