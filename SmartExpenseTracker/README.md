# PennyWise — Smart Expense Tracker (Java Mini Project)

A polished desktop expense tracker built with **Core Java and Swing**, based on a real-life use case: recording everyday spending.

## Features
- Dashboard cards for total spending, this month's spending, and transaction count
- Add expenses with a title, amount, category, payment method, and date
- Input validation for amount and date
- Search transactions by title, category, or payment method
- Delete a selected transaction
- Automatically saves and reloads records in a local `expenses.csv` file
- Clean desktop UI; no external libraries or database required

## Requirements
- JDK 8 or newer
- Windows, macOS, or Linux with a desktop environment

## Run on Windows
1. Extract the ZIP.
2. Open the extracted `SmartExpenseTracker` folder.
3. Double-click `run.bat`, or open Command Prompt in this folder and run:
   ```bat
   run.bat
   ```

## Run from terminal
From the project folder:
```bash
javac -encoding UTF-8 -d out src/SmartExpenseTracker.java
java -cp out SmartExpenseTracker
```

## Where data is stored
The program creates `expenses.csv` in the folder from which it is launched. Keep this file if you want to retain your expense history.

## Java concepts demonstrated
- Classes and objects (`Expense`, `SmartExpenseTracker`)
- Encapsulation through fields and methods
- Collections (`ArrayList`)
- Exception handling
- File handling (`FileReader`, `FileWriter`)
- GUI event handling with Swing
- Table models and input validation

## Suggested mini-project explanation
**Problem:** People often lose track of small daily expenses.
**Solution:** PennyWise lets users record, search, review, and delete expenses, while showing useful spending summaries.
**Future improvements:** Monthly budgets, category-wise charts, CSV export, and password protection.
