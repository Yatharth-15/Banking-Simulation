# Java Banking System Project

A complete Bank Management System developed in Java and MySQL. This project provides a simple GUI for users to deposit, withdraw, transfer funds, and apply for loans, while giving administrators access to a management panel.

## Features

- **Account Types**: Supports both Savings and Current accounts. Savings accounts get interest, and Current accounts have a negative overdraft limit.
- **Loan System**: Users can apply for a Home, Car, or Education loan. The system calculates the monthly EMI and the admin has to approve it before the money is deposited.
- **Admin Panel**: An admin can login to view all users, unblock accounts that got locked after wrong passwords, and approve loans.
- **Security**: Added a basic 2FA (Two-Factor Authentication) simulation for transfers over ₹10,000.
- **Transaction History**: Every time a user deposits or transfers money, it gets saved to a `transactions.log` file so they can view their mini-statement later.
- **Statement Download**: Users can export and download their mini-statements as CSV files (compatible with Excel). The system automatically saves new statements without overwriting previous downloads, ensuring a complete, persistent history of your exported records.

## Technologies Used
- Java (JDK)
- Java Swing (JFrame, JPanel, etc. for frontend)
- MySQL Database
- JDBC

## How to Run the Project

1. First, make sure you have MySQL installed and running.
   > **Note:** You do not need to create the tables manually! The Java application uses an auto-schema generation script (`ensureSchema`) to automatically build the accounts and loans tables upon the first launch.

2. **Configure Credentials:**
   If your MySQL username and password are not `root` / `Root`, you will need to update the credentials at the top of the following files:
   - `src/DB/AccountDAO.java`
   - `src/DB/LoanDAO.java`

3. **Running the Application:**
   A batch script is provided for easy compilation and execution on Windows machines. Open a terminal or command prompt in the project root directory and run the batch file:
   ```cmd
   .\run_app.bat
   ```

## 🔐 Default Admin Access
To access the Admin Panel to approve loans or manage users, use the following default credentials:
- **Admin ID**: `101`
- **Password**: `admin123`

## 📁 Project Structure (MVC Architecture)
- **`src/DB/`**: Data Access Objects handling all MySQL queries and schema building.
- **`src/gui/`**: Swing components (`LoginFrame`, `DashboardFrame`, `AdminFrame`).
- **`src/model/`**: Business logic objects (`Account`, `SavingsAccount`, `CurrentAccount`, `Loan`).
- **`src/service/`**: Transaction controllers ensuring secure fund movements.
- **`src/logging/`**: Handles File I/O operations for `transactions.log` and CSV statement exports.
