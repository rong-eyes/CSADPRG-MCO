# Banking Deposit and Withdrawal Reports (MCO2)

**Language:** C (C99) | **Paradigm:** Procedural | **Group:** 6  Balbanero, Sandoval, Racimo, Yu

A console banking application. It registers accounts, records deposits and withdrawals in `bank_transactions.csv`, and generates four reports and a `summary.json` from that file.

---

## 1. How to run

**Requirements:** Windows with `gcc` (MinGW-w64) on the PATH. Keep all source files, `run.bat` and `bank_transactions.csv` in the same folder.

**One command** (cmd):

    run.bat

In PowerShell, use `.\run.bat`. The batch file compiles the program and starts it.

**Manual version** (the two commands `run.bat` runs):

    gcc -Wall -std=c99 main.c -o run.exe -lm
    run.exe

Only `main.c` is compiled. It includes the other `.c` files directly, so one `gcc` command builds everything. `-lm` links the math library.

**At startup** the program loads `bank_transactions.csv` into memory. If the file does not exist, it starts with an empty list and the first registered account creates the file.

### Main menu

| Option | What it does |
|---|---|
| [1] Register Account Name | Creates an account (PHP). Minimum initial deposit is 5,000.00. |
| [2] Deposit Amount | Adds money to an existing account. |
| [3] Withdraw Amount | Removes money from an existing account (cannot exceed the balance). |
| [4] Currency Exchange | MCO1 feature, unchanged. |
| [5] Record Exchange Rates | MCO1 feature, unchanged. |
| [6] Show Interest Amount | Day-by-day interest table for an account. |
| [7] Generate Reports | Sub-menu: [1] Load the file (re-reads the CSV and shows how many rows were loaded, kept and skipped), [2] Generate Reports. |
| [0] Exit Program | Quits. |

### Files the program creates (in the same folder)

- `report1_banking_privilege_tiers.csv`
- `report2_interest_expense_projection.csv`
- `report3_suspicious_client_behavior.csv`
- `report4_monthly_net_cash_flow.csv`
- `summary.json`

### Project files

| File | Purpose |
|---|---|
| `main.c` | Program entry, startup load, main menu loop |
| `csvData.c/.h` | Reading, validating, appending and looking up transactions |
| `transactions.c/.h` | Deposit, withdrawal and interest rules (from MCO1) |
| `reportCommon`, `accountSummary` | Shared report helpers (dates, number formatting, per-account figures) |
| `report1` to `report4`, `reports` | One file per report, plus `summary.json` and the entry point |
| `menuFunctions`, `printFunctions`, `inputCheck` | Menus, screen output, validated input |
| `exchange`, `type.h` | Currency exchange (MCO1), shared type definitions |
| `run.bat` | Builds and runs with one command |

---

## 2. Report 4: Monthly Net Cash Flow

**What it is.** For every account and every month in which it had activity, the report totals the money that went in (deposits) and out (withdrawals), shows the difference, and shows the balance at the end of that month.

| Column | Meaning |
|---|---|
| AccountName | The account holder |
| Month | `mm/yyyy` |
| TotalWithdrawalAmounts | Sum of withdrawals in that month |
| TotalDepositAmounts | Sum of deposits in that month |
| NetCashFlow | Deposits minus withdrawals (negative means more left the account than came in) |
| EndOfMonthBalance | Balance after the account's last transaction of that month |

**Sort order.** Oldest month first. Within a month, the largest net outflow (most negative NetCashFlow) comes first, then lower account number. Output file: `report4_monthly_net_cash_flow.csv`.

**Justification (REQ-0009).** The report shows which accounts are steadily drawing down their funds, which is a retention and liquidity signal for a bank. Reports 1 to 3 do not cover it: Report 1 ranks accounts by balance, Report 2 projects interest, and Report 3 flags suspicious days. On the provided data it covers 1,197 account-months, of which 456 have a net outflow. In October 2026, 39 of the 100 accounts had a net outflow.

---

## 3. Design decisions and why

### Data handling

- **One in-memory list is the only store of balances.** Transactions are held in a dynamic array that doubles its capacity when full (`realloc`). This replaced MCO1's account linked list, so there is no second copy to keep in sync. The CSV is the saved copy.
- **Save first, then update memory.** A new transaction is appended to the CSV first and added to the list only if the write succeeded. If the file cannot be written (for example, it is open in Excel), the balance is not changed and the user is told.
- **The list is kept sorted by account number, then time.** It is sorted after every load and every new transaction. This makes "latest row of an account" the last matching row, and puts each account's rows next to each other so the reports can work in a single pass.
- **Account numbers:** the highest existing number plus 1 (REQ-0001). If the file is empty or missing, numbering starts at 1092000001. They are stored as `long long` so any 10-digit number fits (`long` is only 32 bits on Windows).
- **PHP only.** Accounts are created in PHP, and rows in other currencies are filtered out when loading (REQ-0003).

### Validation and cleaning (REQ-0004, REQ-0005)

- Each row is checked for: 8 fields, no empty field, a real calendar date (including leap years), a valid time, a 3-letter currency, a positive amount, a known type (deposit or withdrawal), and a non-negative balance.
- Invalid rows are counted and skipped. The load screen shows rows loaded, rows kept for PHP, and invalid rows skipped.
- Rows are split with a small custom splitter instead of `strtok`, because `strtok` skips empty fields and would hide missing values.
- The input parser has no support for quoted fields, so account names cannot contain commas. This is enforced when a name is typed.

### Money and rounding

- Deposits are rounded **down** and withdrawals **up** to the cent (the MCO1 rule: the customer never gets extra or pays less). The amount written to the CSV is the amount actually applied, so the amount and balance in each row always agree.

### Report definitions

- **Report 1 reference month.** "Last month" is the newest **complete** month in the data, found from the data itself and not the computer's clock, so the output is the same on any day. For the provided file that is October 2026 (the data ends on 29/11/2026, so November is not complete).
- **Report 1 AverageDailyBalance.** The end-of-day balance is the balance after the day's last transaction. Days without transactions carry the previous balance forward, and days before the account existed count as 0. The average is the sum over the month divided by the number of days in the month.
- **Report 1 CurrentBalance** shows the balance at the end of the reference month, to stay consistent with the rest of that report. Report 2 and `summary.json` use the latest balance, as the spec says for Report 2.
- **Report 1 AccountAge** is the number of days from the account's first transaction (its opening, since the file has no open date) to the end of the reference month.
- **Filters and tiers** compare values already rounded to cents, so what is shown always agrees with the filter. Tiers: Silver below 500,000.00, Gold below 1,000,000.00, Platinum 1,000,000.00 and above.
- **Report 2 interest.** Interest is credited daily at 5%/365 and compounded for 30 days, rounded to the cent each day, like the MCO1 interest menu. ExpectedInterest is the final amount minus the starting balance. Simple interest would give slightly lower numbers (for a balance of 364,808.10: 1,499.21 instead of 1,502.19).
- **Report 3 velocity.** When an account has no deposits on a day, TurnoverVelocity cannot be computed (division by zero). It is shown as `N/A` and the flag depends only on the 500,000.00 withdrawal rule. Flagging those days would mark 3,370 of the 8,062 account-days and hide the 325 genuinely suspicious ones.
- **summary.json:** `total_accounts` is the number of distinct accounts. `total_deposits` and `total_withdrawals` are sums over all loaded rows. `total_balance` is the sum of each account's latest balance. `total_interest_expense` is the sum of Report 2's ExpectedInterest. As a check, deposits minus withdrawals equals the total balance.

### Output format (REQ-0012)

- Numbers in the report CSVs have thousands separators and 2 decimals (`1,234,567.89`). Because those values contain commas, they are wrapped in double quotes so the files stay valid CSV.
- `summary.json` uses plain numbers (`1234567.89`), because commas would make the JSON invalid.
- Rows with equal sort values are ordered by account number, so every run produces identical files.

### Build

- `main.c` includes the other `.c` files, so a single `gcc main.c` compiles the whole program (the same command style as MCO1). Because of that, every function name is unique across all files, even `static` ones.
- The code compiles with no warnings under `-Wall -std=c99`.

### Known limitations

- **System clock.** New transactions are stamped with the computer's clock. The provided data runs until 29/11/2026 19:38. If the clock is earlier than an existing account's last transaction, a new transaction for that account sorts before it and the account's balance appears stale. New accounts are not affected, and nothing is affected once the clock passes that time.
- Rows are limited to 511 characters, names to 100 characters, and the first line of the CSV is always treated as the header.

---

## 4. Results on the provided file (for checking)

| Output | Expected |
|---|---|
| Rows loaded / kept for PHP / invalid | 9,189 / 9,189 / 0 |
| Report 1 | 85 rows (reference month 10/2026) |
| Report 2 | 100 rows |
| Report 3 | 8,062 rows, 325 Suspicious |
| Report 4 | 1,197 rows |
| `summary.json` | 100 accounts; deposits 114503599.90; withdrawals 87394448.90; balance 27109151.00; interest expense 111628.99 |
