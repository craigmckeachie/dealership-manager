package com.pluralsight.dealershipmanager.ui;

import com.pluralsight.dealershipmanager.data.DealershipFileManager;
import com.pluralsight.dealershipmanager.model.Dealership;
import com.pluralsight.dealershipmanager.model.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

// Solution: car-dealership, Phases 3 and 5 (pp. 7-9). The only class that prints or reads input.
// display() is the Home Screen; each process...Request() is an action.
public class UserInterface {
    private Dealership dealership;
    private Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    public void display() {
        init();

        String command;
        do {
            displayMenu();
            command = scanner.nextLine().trim();

            switch (command) {
                case "1" -> processGetByPriceRequest();
                case "2" -> processGetByMakeModelRequest();
                case "3" -> processGetByYearRequest();
                case "4" -> processGetByColorRequest();
                case "5" -> processGetByMileageRequest();
                case "6" -> processGetByVehicleTypeRequest();
                case "7" -> processGetAllVehiclesRequest();
                case "8" -> processAddVehicleRequest();
                case "9" -> processRemoveVehicleRequest();
                case "99" -> System.out.println("Goodbye!");
                default -> System.out.println("That isn't a command. Try again.");
            }
        } while (!command.equals("99"));
    }

    private void init() {
        DealershipFileManager fileManager = new DealershipFileManager();
        this.dealership = fileManager.getDealership();
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("=== " + dealership.getName() + " ===");
        System.out.println("1 - Find vehicles within a price range");
        System.out.println("2 - Find vehicles by make / model");
        System.out.println("3 - Find vehicles by year range");
        System.out.println("4 - Find vehicles by color");
        System.out.println("5 - Find vehicles by mileage range");
        System.out.println("6 - Find vehicles by type (car, truck, SUV, van)");
        System.out.println("7 - List ALL vehicles");
        System.out.println("8 - Add a vehicle");
        System.out.println("9 - Remove a vehicle");
        System.out.println("99 - Quit");
        System.out.print("Enter a command: ");
    }

    private void displayVehicles(ArrayList<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        } else {
            System.out.printf("%-8s %-6s %-10s %-12s %-8s %-8s %-10s %10s%n",
                    "VIN", "Year", "Make", "Model", "Type", "Color", "Odometer", "Price");
            for (Vehicle vehicle : vehicles) {
                System.out.printf("%-8d %-6d %-10s %-12s %-8s %-8s %-10d %10.2f%n",
                        vehicle.getVin(), vehicle.getYear(), vehicle.getMake(), vehicle.getModel(),
                        vehicle.getVehicleType(), vehicle.getColor(), vehicle.getOdometer(), vehicle.getPrice());
            }
        }
        pressEnterToContinue();
    }

    private void processGetByPriceRequest() {
        BigDecimal min = promptForBigDecimal("Minimum price: ");
        BigDecimal max = promptForBigDecimal("Maximum price: ");
        displayVehicles(dealership.getVehiclesByPrice(min, max));
    }

    private void processGetByMakeModelRequest() {
        String make = promptForString("Make: ");
        String model = promptForString("Model: ");
        displayVehicles(dealership.getVehiclesByMakeModel(make, model));
    }

    private void processGetByYearRequest() {
        int min = promptForInt("Earliest year: ");
        int max = promptForInt("Latest year: ");
        displayVehicles(dealership.getVehiclesByYear(min, max));
    }

    private void processGetByColorRequest() {
        String color = promptForString("Color: ");
        displayVehicles(dealership.getVehiclesByColor(color));
    }

    private void processGetByMileageRequest() {
        int min = promptForInt("Minimum mileage: ");
        int max = promptForInt("Maximum mileage: ");
        displayVehicles(dealership.getVehiclesByMileage(min, max));
    }

    private void processGetByVehicleTypeRequest() {
        String vehicleType = promptForString("Type (car, truck, SUV, van): ");
        displayVehicles(dealership.getVehiclesByType(vehicleType));
    }

    private void processGetAllVehiclesRequest() {
        displayVehicles(dealership.getAllVehicles());
    }

    private void processAddVehicleRequest() {
        int vin = promptForInt("VIN: ");
        int year = promptForInt("Year: ");
        String make = promptForString("Make: ");
        String model = promptForString("Model: ");
        String vehicleType = promptForString("Type (car, truck, SUV, van): ");
        String color = promptForString("Color: ");
        int odometer = promptForInt("Odometer: ");
        BigDecimal price = promptForBigDecimal("Price: ");

        dealership.addVehicle(new Vehicle(vin, year, make, model, vehicleType, color, odometer, price));
        saveDealership();

        System.out.println("Vehicle added.");
        pressEnterToContinue();
    }

    private void processRemoveVehicleRequest() {
        int vin = promptForInt("VIN of the vehicle to remove: ");

        Vehicle vehicleToRemove = null;
        for (Vehicle vehicle : dealership.getAllVehicles()) {
            if (vehicle.getVin() == vin) {
                vehicleToRemove = vehicle;
            }
        }

        if (vehicleToRemove == null) {
            System.out.println("No vehicle has that VIN.");
        } else {
            dealership.removeVehicle(vehicleToRemove);
            saveDealership();
            System.out.println("Vehicle removed.");
        }
        pressEnterToContinue();
    }

    private void saveDealership() {
        DealershipFileManager fileManager = new DealershipFileManager();
        fileManager.saveDealership(dealership);
    }

    // ---------- Helpers: the Validating Input pattern, as instance methods ----------

    private int promptForInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number, like 12.");
            }
        }
    }

    private BigDecimal promptForBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter an amount, like 995.00.");
            }
        }
    }

    // a | would break the file the next time it's read
    private String promptForString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim();
            if (answer.isEmpty() || answer.contains("|")) {
                System.out.println("Please type something, without a |.");
            } else {
                return answer;
            }
        }
    }

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
