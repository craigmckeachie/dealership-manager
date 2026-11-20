package com.pluralsight.dealershipmanager.data;

import com.pluralsight.dealershipmanager.model.Dealership;
import com.pluralsight.dealershipmanager.model.Vehicle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

// Solution: car-dealership, Phases 2 and 5 (p. 6)
public class DealershipFileManager {
    // Students use inventory.csv. Search Inventory's file already has that name in this project.
    private static final String FILE_NAME = "dealership-inventory.csv";

    public Dealership getDealership() {
        Dealership dealership = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));

            // the first line is the dealership
            String[] dealershipParts = bufferedReader.readLine().split("\\|");
            dealership = new Dealership(dealershipParts[0], dealershipParts[1], dealershipParts[2]);

            // every other line is a vehicle
            String line = bufferedReader.readLine();
            while (line != null) {
                if (!line.isBlank()) {
                    dealership.addVehicle(parseVehicle(line));
                }
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dealership;
    }

    private Vehicle parseVehicle(String line) {
        String[] parts = line.split("\\|");
        int vin = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        int odometer = Integer.parseInt(parts[6]);
        BigDecimal price = new BigDecimal(parts[7]);
        return new Vehicle(vin, year, parts[2], parts[3], parts[4], parts[5], odometer, price);
    }

    // Rewrites the whole file, dealership line first
    public void saveDealership(Dealership dealership) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME));

            bufferedWriter.write(String.format("%s|%s|%s\n",
                    dealership.getName(), dealership.getAddress(), dealership.getPhone()));

            for (Vehicle vehicle : dealership.getAllVehicles()) {
                bufferedWriter.write(String.format("%d|%d|%s|%s|%s|%s|%d|%.2f\n",
                        vehicle.getVin(), vehicle.getYear(), vehicle.getMake(), vehicle.getModel(),
                        vehicle.getVehicleType(), vehicle.getColor(), vehicle.getOdometer(), vehicle.getPrice()));
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
