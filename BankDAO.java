import java.sql.*;

public class BankDAO {

    public long createAccount(BankAccount account) {

        String sql = """
                INSERT INTO accounts(name, phone, pin)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, account.getName());
            ps.setString(2, account.getPhone());
            ps.setString(3, account.getPin());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }

        return -1;
    }

    public BankAccount login(long accountNumber, String pin) {

        String sql = """
                SELECT account_number, name, phone, balance
                FROM accounts
                WHERE account_number = ? AND pin = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, accountNumber);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new BankAccount(
                        rs.getLong("account_number"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getDouble("balance")
                );
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public boolean deposit(long accountNumber, double amount) {

        if (amount <= 0) {
            return false;
        }

        String updateAccount = """
                UPDATE accounts
                SET balance = balance + ?
                WHERE account_number = ?
                """;

        String insertTransaction = """
                INSERT INTO transactions
                (account_number, transaction_type, amount)
                VALUES (?, 'DEPOSIT', ?)
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps1 =
                         con.prepareStatement(updateAccount);
                 PreparedStatement ps2 =
                         con.prepareStatement(insertTransaction)) {

                ps1.setDouble(1, amount);
                ps1.setLong(2, accountNumber);

                int rows = ps1.executeUpdate();

                if (rows == 0) {
                    con.rollback();
                    return false;
                }

                ps2.setLong(1, accountNumber);
                ps2.setDouble(2, amount);
                ps2.executeUpdate();

                con.commit();

                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Deposit error: " + e.getMessage());
        }

        return false;
    }

    public boolean withdraw(long accountNumber, double amount) {

        if (amount <= 0) {
            return false;
        }

        String updateAccount = """
                UPDATE accounts
                SET balance = balance - ?
                WHERE account_number = ?
                AND balance >= ?
                """;

        String insertTransaction = """
                INSERT INTO transactions
                (account_number, transaction_type, amount)
                VALUES (?, 'WITHDRAW', ?)
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps1 =
                         con.prepareStatement(updateAccount);
                 PreparedStatement ps2 =
                         con.prepareStatement(insertTransaction)) {

                ps1.setDouble(1, amount);
                ps1.setLong(2, accountNumber);
                ps1.setDouble(3, amount);

                int rows = ps1.executeUpdate();

                if (rows == 0) {
                    con.rollback();
                    return false;
                }

                ps2.setLong(1, accountNumber);
                ps2.setDouble(2, amount);
                ps2.executeUpdate();

                con.commit();

                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Withdraw error: " + e.getMessage());
        }

        return false;
    }

    public double getBalance(long accountNumber) {

        String sql = """
                SELECT balance
                FROM accounts
                WHERE account_number = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, accountNumber);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }

        } catch (SQLException e) {
            System.out.println("Balance error: " + e.getMessage());
        }

        return -1;
    }

    public void showTransactions(long accountNumber) {

        String sql = """
                SELECT transaction_id,
                       transaction_type,
                       amount,
                       transaction_date
                FROM transactions
                WHERE account_number = ?
                ORDER BY transaction_date DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, accountNumber);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n----- Transaction History -----");

            while (rs.next()) {

                System.out.println(
                        "ID: " + rs.getLong("transaction_id")
                        + " | Type: "
                        + rs.getString("transaction_type")
                        + " | Amount: ₹"
                        + rs.getDouble("amount")
                        + " | Date: "
                        + rs.getTimestamp("transaction_date")
                );
            }

        } catch (SQLException e) {
            System.out.println("Transaction error: " + e.getMessage());
        }
    }
}