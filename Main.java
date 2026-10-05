import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final BankDAO bankDAO = new BankDAO();

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1 -> createAccount();

                case 2 -> login();

                case 3 -> {
                    System.out.println("Thank you!");
                    return;
                }

                default ->
                        System.out.println("Invalid choice.");
            }
        }
    }

    private static void createAccount() {

        System.out.println("\n----- Create Account -----");

        System.out.print("Enter name: ");
        String name = sc.nextLine();

        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        System.out.print("Create 6-digit PIN: ");
        String pin = sc.nextLine();

        if (!pin.matches("\\d{6}")) {
            System.out.println("PIN must contain exactly 6 digits.");
            return;
        }

        BankAccount account =
                new BankAccount(name, phone, pin);

        long accountNumber =
                bankDAO.createAccount(account);

        if (accountNumber != -1) {

            System.out.println(
                    "\nAccount created successfully!"
            );

            System.out.println(
                    "Your Account Number: "
                    + accountNumber
            );
        }
    }

    private static void login() {

        System.out.println("\n----- Login -----");

        System.out.print("Account Number: ");
        long accountNumber = sc.nextLong();
        sc.nextLine();

        System.out.print("PIN: ");
        String pin = sc.nextLine();

        BankAccount account =
                bankDAO.login(accountNumber, pin);

        if (account == null) {
            System.out.println("Invalid account number or PIN.");
            return;
        }

        System.out.println(
                "\nWelcome, " + account.getName() + "!"
        );

        accountMenu(account);
    }

    private static void accountMenu(BankAccount account) {

        while (true) {

            System.out.println("\n===== ACCOUNT MENU =====");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transaction History");
            System.out.println("5. Logout");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1 -> {

                    double balance =
                            bankDAO.getBalance(
                                    account.getAccountNumber()
                            );

                    System.out.printf(
                            "Current Balance: ₹%.2f%n",
                            balance
                    );
                }

                case 2 -> {

                    System.out.print("Enter amount: ₹");
                    double amount = sc.nextDouble();

                    if (bankDAO.deposit(
                            account.getAccountNumber(),
                            amount)) {

                        System.out.println(
                                "Amount deposited successfully."
                        );

                    } else {
                        System.out.println(
                                "Deposit failed."
                        );
                    }
                }

                case 3 -> {

                    System.out.print("Enter amount: ₹");
                    double amount = sc.nextDouble();

                    if (bankDAO.withdraw(
                            account.getAccountNumber(),
                            amount)) {

                        System.out.println(
                                "Amount withdrawn successfully."
                        );

                    } else {
                        System.out.println(
                                "Withdrawal failed. "
                                + "Check balance or amount."
                        );
                    }
                }

                case 4 ->
                        bankDAO.showTransactions(
                                account.getAccountNumber()
                        );

                case 5 -> {
                    System.out.println("Logged out.");
                    return;
                }

                default ->
                        System.out.println("Invalid choice.");
            }
        }
    }
}