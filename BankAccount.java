public class BankAccount {

    private long accountNumber;
    private String name;
    private String phone;
    private String pin;
    private double balance;

    public BankAccount() {
    }

    public BankAccount(String name, String phone, String pin) {
        this.name = name;
        this.phone = phone;
        this.pin = pin;
    }

    public BankAccount(long accountNumber, String name,
                       String phone, double balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.phone = phone;
        this.balance = balance;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}