package atm_package;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ATM {
    private BankAccount userAccount;
    private Scanner scanner;
    private double dailyWithdrawalLimit;
    private long lastActivityTime;

    public ATM(BankAccount account) {
        this.userAccount = account;
        this.scanner = new Scanner(System.in);
        this.dailyWithdrawalLimit = 5000.0; // Example daily withdrawal limit
        this.lastActivityTime = System.currentTimeMillis();
    }

    // Method to withdraw money from the ATM
    public boolean withdraw(double amount) {
        try {
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return false;
            }
            if (amount > userAccount.getBalance()) {
                System.out.println("Withdrawal failed. Insufficient funds.");
                return false;
            }
            if (amount > dailyWithdrawalLimit) {
                System.out.println("Withdrawal amount exceeds daily limit.");
                return false;
            }
            
            // Check if withdrawal amount is a multiple of 10 (for ATM notes)
            if (amount % 10 != 0) {
                System.out.println("Withdrawal amount must be in multiples of 10.");
                return false;
            }

            userAccount.withdraw(amount);
            dailyWithdrawalLimit -= amount;
            System.out.println("Withdrawal successful. Remaining balance: " + userAccount.getBalance());
            return true;
        } catch (InputMismatchException e) {
            System.out.println("Invalid input format. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
            return false;
        }
    }

    // Method to deposit money into the ATM
    public void deposit(double amount) {
        try {
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }
            userAccount.deposit(amount);
            System.out.println("Deposit successful. Updated balance: " + userAccount.getBalance());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input format. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    // Method to check balance in the ATM
    public void checkBalance() {
        System.out.println("Current balance: " + userAccount.getBalance());
    }

    // Method to handle API interactions and user inputs
    public void handleAPIs() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    double withdrawAmount = getUserInput("Enter amount to withdraw: ");
                    withdraw(withdrawAmount);
                    break;
                case 2:
                    double depositAmount = getUserInput("Enter amount to deposit: ");
                    deposit(depositAmount);
                    break;
                case 3:
                    checkBalance();
                    break;
                case 4:
                    printMiniStatement();
                    break;
                case 5:
                    changePIN();
                    break;
                case 6:
                    System.out.println("Exiting ATM. Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }

            // Update last activity time after each user interaction
            updateLastActivityTime();
        }
    }

    // Helper method to display the ATM menu
    private void displayMenu() {
        System.out.println("\nATM Interface\n");
        System.out.println("1. Withdraw");
        System.out.println("2. Deposit");
        System.out.println("3. Check Balance");
        System.out.println("4. Print Mini-Statement");
        System.out.println("5. Change PIN");
        System.out.println("6. Exit");
    }

    // Helper method to get user choice
    private int getUserChoice() {
        while (true) {
            try {
                System.out.print("\nEnter your choice: ");
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Helper method to get user input amount
    private double getUserInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Method to print mini-statement (last 5 transactions)
    private void printMiniStatement() {
        System.out.println("\nMini-Statement");
        System.out.println("------------------");
        // Assuming BankAccount maintains a list of transactions
        for (Transaction transaction : userAccount.getLastTransactions(5)) {
            System.out.println(transaction);
        }
        System.out.println("------------------");
    }

    // Method to change PIN (dummy implementation for demonstration)
    private void changePIN() {
        System.out.println("\nChange PIN");
        System.out.println("Enter current PIN:");
        // Dummy implementation for demonstration
        String currentPIN = scanner.next();
        System.out.println("Enter new PIN:");
        String newPIN = scanner.next();
        System.out.println("Confirm new PIN:");
        String confirmPIN = scanner.next();
        if (newPIN.equals(confirmPIN)) {
            System.out.println("PIN successfully changed.");
        } else {
            System.out.println("PIN change failed. PINs do not match.");
        }
    }

    // Method to update last activity time
    private void updateLastActivityTime() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    // Getter for last activity time (for session timeout)
    public long getLastActivityTime() {
        return lastActivityTime;
    }
}
