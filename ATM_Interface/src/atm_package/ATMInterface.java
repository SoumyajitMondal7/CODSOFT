package atm_package;

public class ATMInterface {
    public static void main(String[] args) {
        System.out.println("Welcome to the ATM");

        // Initialize a bank account with an initial balance
        BankAccount account = new BankAccount(1000); // Initial balance for demonstration
        ATM atm = new ATM(account);

        // Main loop for the ATM interface
        while (true) {
            // Check session timeout (3 minutes inactivity)
            if (System.currentTimeMillis() - atm.getLastActivityTime() > 180000) {
                System.out.println("Session timed out due to inactivity. Exiting ATM.");
                break;
            }
            atm.handleAPIs();
        }
    }
}
