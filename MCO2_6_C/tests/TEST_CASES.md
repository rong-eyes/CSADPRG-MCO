# Testing and Validation (MCO2, C version)

All tests are automated. Each one feeds a prepared input file to the program's stdin (exactly what a user would type), runs it against a small CSV built for that test, and checks the console output, `bank_transactions.csv` and the report files.

## How to run

From the project folder (needs `gcc` on PATH, same as `run.bat`):

    tests\run_tests.bat

The script:

1. Compiles `main.c` into `tests\work\run.exe` (same flags as `run.bat`: `-Wall -std=c99 -lm`).
2. For each test case, creates `tests\work\<ID>\`, copies that test's fixture in as `bank_transactions.csv`, and runs the program with `tests\inputs\<file>.txt` as keyboard input.
3. Checks the results, prints `PASS`/`FAIL` with the reason, and saves a summary to `tests\test_results.txt`.

Everything each run produced (console log, CSV, reports, `summary.json`) stays in `tests\work\<ID>\`, so any result can be opened and checked by hand or shown in the demo. A case that waits forever for input fails after 20 seconds instead of hanging.

| Folder | Contents |
|---|---|
| `fixtures\` | Small hand-made CSV files, one per scenario |
| `inputs\` | Keyboard input for each run, one value per line |
| `expected\<ID>\` | Exact report files the program must produce, compared line by line |

## Why small fixtures

The provided 9,189-row file cannot be checked by hand, and it has no rows at the boundaries (for example, no account with an average of exactly 100,000.00). Each fixture has only a few rows, chosen so that **every expected value can be worked out with a calculator**, and most rows sit at or one centavo away from a rule's boundary. The full file is still tested (TC-FULL-01) for counts, totals and reproducibility.

---

## 1. Menus and input handling

| ID | Input | Expected | Edge case covered |
|---|---|---|---|
| TC-M-01 | Main menu: `9`, `abc`, empty line, then `0` | `Invalid option. Try again!` three times, then exits | Out-of-range, non-numeric and empty input do not crash or loop forever |
| TC-M-02 | Reports menu: `3`, `abc`, then `1` | `Invalid input. Try again!`, then `(4 rows loaded, 4 filtered for PHP currency, 0 invalid rows skipped)` | Sub-menu validation |

## 2. Register account (REQ-0001)

Base fixture `base.csv` has accounts **1092000001, 1092000003, 1092000005** (stored out of order and with gaps), so "highest number + 1" (1092000006) differs from "count + 1" (1092000004).

| ID | Input | Expected | Edge case covered |
|---|---|---|---|
| TC-REG-01 | `Pedro Penduko`, `10000` | Row `Pedro Penduko,1092000006,…,PHP,10000.00,deposit,10000.00` | Account number = highest existing + 1, even with gaps and unsorted file |
| TC-REG-02 | `4999.99`, `abc`, `-5`, then `5000` | 3× `Invalid amount! Minimum initial deposit is 5000.00`, then account created with 5,000.00 | Minimum is inclusive; one centavo below is rejected |
| TC-REG-03 | `Juan Dela Cruz` (already exists) | `Account already exists.`, no new row | Duplicate names |
| TC-REG-04 | empty name, `Dela Cruz, Juan`, 101 × `A`, then `Juan Dela Cruz Jr` | Invalid-name message for the first three; fourth saved | Empty input, comma (would break the CSV), over-length name |
| TC-REG-05 | 100 × `B` | Account created | Longest allowed name (100 chars) is accepted |
| TC-REG-06 | Initial deposit `5000.999` | Saved as `5000.99` | Deposits round down to the cent |
| TC-REG-07 | No `bank_transactions.csv` at all; load, generate, then register | `Could not open bank_transactions.csv…`, `No transactions loaded…`, then file created with header and account **1092000001** | Missing file; first account number |

## 3. Deposit, withdraw, interest (REQ-0002)

`Juan Dela Cruz` starts at 10,000.00; `Empty Wallet` at 0.00.

| ID | Input | Expected | Edge case covered |
|---|---|---|---|
| TC-DEP-01 | Deposit `500` | `Current Balance: 10000.00`, `Updated Balance: 10500.00`; row `…,PHP,500.00,deposit,10500.00` | Normal case; CurrentBalance = previous + amount |
| TC-DEP-02 | `0`, `-100`, `abc`, then `100.009` | `Invalid amount` ×3, then balance 10,100.00 (100.009 → 100.00) | Zero, negative, text; rounding down |
| TC-DEP-03 | `No Such Person`, then `juan dela cruz` | `Account does not exist.` both times | Unknown account; names are case-sensitive |
| TC-WIT-01 | Withdraw `2500` | Balance 7,500.00; row `…,PHP,2500.00,withdrawal,7500.00` | Normal case; CurrentBalance = previous − amount |
| TC-WIT-02 | `10000.01`, `0`, then `10000` | `Insufficient funds`, `Invalid amount`, then balance 0.00 | One centavo over the balance; withdrawing the whole balance |
| TC-WIT-03 | `Empty Wallet` | `Cannot withdraw. Account has no funds.` | Zero balance |
| TC-WIT-04 | Withdraw `0.001` | Rounded **up** to 0.01; balance 9,999.99 | Withdrawals round up to the cent |
| TC-PER-01 | Run 1: deposit 500. **Restart.** Run 2: withdraw `10500.01`, then `10500` | Run 2 shows `Current Balance: 10500.00`, refuses 10,500.01, accepts 10,500 | Data survives a restart (CSV is the saved copy) |
| TC-INT-01 | Interest: `0` days, then `3` | `Invalid days`; table shows 1.37 per day, balance 10,001.37 → 10,004.11 | 10,000 × 0.05 / 365 = 1.3699 → 1.37 |

## 4. Data ingestion and validation (REQ-0003 to REQ-0005)

TC-V-01 uses `validation.csv`: 21 data rows, one problem per row.

| Row | Problem | Counted as |
|---|---|---|
| Valid Row | none | kept |
| Leap Day Valid | 29/02/**2024** (leap year) | kept |
| Single Digit Hour | time `8:05:00` (same style as the provided file) | kept |
| Dollar Account, Euro Account | USD / EUR currency | filtered out (valid, but not PHP) |
| Not A Leap Year | 29/02/**2025** | invalid |
| April Has 30 Days | 31/04/2026 | invalid |
| Month Thirteen | month 13 | invalid |
| Wrong Date Format | `2026-01-05` | invalid |
| Bad Hour | `24:00:00` | invalid |
| Missing Amount | empty field | invalid |
| (no name) | empty account name | invalid |
| Too Few Fields | 7 fields instead of 8 | invalid |
| (blank line) | empty row | invalid |
| Negative Amount / Zero Amount / Text Amount | `-500.00`, `0.00`, `abc` | invalid |
| Text Account Number | `abc` | invalid |
| Unknown Type | `transfer` | invalid |
| Negative Balance | `-500.00` | invalid |
| Short Currency | `PH` | invalid |

**Expected:** `(21 rows loaded, 3 filtered for PHP currency, 16 invalid rows skipped)` (3 kept + 2 non-PHP + 16 invalid = 21), and reports still generate from the 3 good rows: `"total_accounts": 3`, `"total_deposits": 30000.00`.

## 5. Reports (REQ-0006 to REQ-0010)

For these cases the program's output file must match `expected\<ID>\` **exactly** (same rows, order, quoting and number format).

### TC-R1-01 Report 1: Banking Privilege Tiers (`report1_tiers.csv`)

The data ends on 31/10/2026, the last day of the month, so the reference month is **October 2026 (31 days)**. Most accounts were opened 01/09/2026 and have no October activity, so their ADB equals their balance. AccountAge = 30 (rest of Sept) + 30 (to 31 Oct) = **60**.

| Account | Why it's there | Hand calculation | Expected |
|---|---|---|---|
| Below Filter | 99,999.99 | below 100,000 | **not listed** |
| Exact Filter | exactly 100,000.00 | filter is `>=` | listed, Silver |
| Silver Top / Gold Bottom | 499,999.99 / 500,000.00 | Silver is `< 500,000` | Silver / Gold |
| Gold Top / Platinum Bottom | 999,999.99 / 1,000,000.00 | Platinum is `>= 1,000,000` | Gold / Platinum |
| Mid Month Opener | opened 16/10 with 310,000 | days 1–15 = 0, days 16–31 = 310,000 → 16 × 310,000 / 31 = **160,000.00**; age 31 − 16 = **15** | Silver |
| Withdraw Mid Month | 200,000; on 11/10: 09:00 withdraw 100,000, 15:00 deposit 38,000 (stored **out of time order** in the file) | 10 × 200,000 + 21 × 138,000 = 4,898,000 / 31 = **158,000.00**; end-of-day uses the 15:00 row | Silver, CurrentBalance 138,000.00 |
| Tie Breaker | same ADB as Exact Filter | equal ADB → lower account number first | after Exact Filter |
| Sentinel Account | 5,000 on 31/10 | 5,000 / 31 = 161.29 | not listed |

Expected rows are sorted by ADB, highest first: Platinum Bottom, Gold Top, Gold Bottom, Silver Top, Mid Month Opener, Withdraw Mid Month, Exact Filter, Tie Breaker.

### TC-R1-02 Report 1: newest month not finished

`October Rich` deposits 200,000 on 01/10/2026 and withdraws 150,000 on 15/11/2026, where the data ends. November is incomplete, so October is used: ADB **200,000.00**, CurrentBalance **200,000.00**, age **30**. Using November by mistake would give a different ADB.

### TC-R1-03 Report 1: leap-year February

Data ends 29/02/2024, so February 2024 is complete and has **29** days.
- `Leap Saver`: 290,000 from 01/02 → ADB 290,000.00, age 28.
- `Leap Day Opener`: 5,800,000 on 29/02 only → 5,800,000 / 29 = **200,000.00**, age 0, CurrentBalance 5,800,000.00. Dividing by 28 would give 207,142.86.

### TC-R2-01 Report 2: Interest Expense Projection + summary.json (`report2_interest.csv`)

| Account | Latest balance | Expected |
|---|---|---|
| Below Interest Filter | 9,999.99 | not listed |
| Exact Interest Filter | 10,000.00 | listed, interest **41.12** |
| Big Saver | 365,000.00 | interest **1,502.98** (day 1: 365,000 × 0.05/365 = 50.00) |
| Was Poor Now Rich | 5,000 then +95,000 = 100,000.00 | listed, interest **411.77** |
| Was Rich Now Poor | 500,000 then −495,000 = 5,000.00 | **not listed**: the latest balance counts, not the highest |
| Zero Balance | 0.00 | not listed |

Interest is compounded daily for 30 days at 5%/365, rounded to the cent each day (see README). With simple interest the values would be 41.10 / 1,500.00 / 410.96.

`summary.json` expected: 6 accounts; deposits 9,999.99 + 10,000 + 365,000 + 500,000 + 5,000 + 95,000 + 5,000 = **989,999.99**; withdrawals **500,000.00**; total balance **489,999.99** (= deposits − withdrawals); interest 1,502.98 + 411.77 + 41.12 = **1,955.87** (only accounts shown in Report 2).

### TC-R3-01 Report 3: Suspicious Client Behavior (`report3_suspicious.csv`)

Five accounts open on 01/11/2026 (deposits only, velocity 0.00, Normal). On 02/11/2026 (rows stored out of order):

| Account | 02/11 activity | Velocity | Expected flag | Rule tested |
|---|---|---|---|---|
| Big Withdrawer | withdraw 500,000.00, no deposit | N/A | **Suspicious** | withdrawal exactly at 500,000 |
| Almost Big | withdraw 499,999.99, no deposit | N/A | Normal | one centavo below |
| Velocity Exact | deposit 10,000, withdraw 9,000 | **90.00** | **Suspicious** | velocity exactly 90% |
| Velocity Below | deposit 10,000, withdraw 8,999 | **89.99** | Normal | just below 90% |
| Velocity Over | deposits 600 + 400 (count = **2**), withdraw 5,000 | **500.00** | Suspicious | several deposits in a day; over 100% |

Expected order: by date ascending; within 02/11, Suspicious rows first (Big Withdrawer, Velocity Exact, Velocity Over), then Normal (Almost Big, Velocity Below), each by account number.

### TC-R4-01 Report 4: Monthly Net Cash Flow (`report4_cashflow.csv`)

| Account | Activity | 12/2025 | 01/2026 |
|---|---|---|---|
| Saver | +50,000 (15/12), +10,000 (20/12), −20,000 (05/01), +5,000 (31/01) | net +60,000, end 60,000 | W 20,000, D 5,000, net **−15,000**, end 45,000 |
| Spender | +30,000 at 31/12/2025 **23:59:59**, −25,000 at 01/01/2026 **00:00:00** | net +30,000, end 30,000 | net **−25,000**, end 5,000 |
| Quiet | +8,000 (10/12) | net +8,000 | no row (no activity) |

Covers the year boundary, transactions one second apart across months, negative net flow, and a month with no activity. Order: 12/2025 then 01/2026; within a month, the most negative net flow first.

The same fixture also checks that **Report 1 still produces a valid file with only the header** when no account reaches 100,000.

### TC-FULL-01 Full provided dataset

| Check | Expected | How the expected value was obtained |
|---|---|---|
| Load counts | `9,189 rows loaded, 9,189 filtered for PHP currency, 0 invalid rows skipped` | `wc -l` on the file (9,190 lines − 1 header) |
| summary.json | 100 accounts; deposits 114,503,599.90; withdrawals 87,394,448.90; balance 27,109,151.00; interest 111,628.99 | Accounts, deposits and withdrawals checked independently with `awk` on the raw CSV; balance = deposits − withdrawals |
| Report row counts | R1 85, R2 100, R3 8,062 (325 Suspicious), R4 1,197 | Counted from the baseline output |
| All 5 output files | Byte-for-byte equal (ignoring line endings) to `expected\TC-FULL-01\` | Baseline saved from a verified run; any change in output is caught (REQ-0012: identical, reproducible files) |

Independent check used for the summary totals (Git Bash):

    awk -F, 'NR>1{n[$2]=1; if($7=="deposit")d+=$6; else w+=$6} END{printf "%d %.2f %.2f\n", length(n), d, w}' bank_transactions.csv
    # 100 114503599.90 87394448.90

---

## 6. Results

    PASS  TC-M-01     Main menu rejects out-of-range, text and empty choices
    PASS  TC-M-02     Reports sub-menu rejects invalid choices
    PASS  TC-REG-01   Register: new account gets highest existing number + 1 (not count + 1)
    PASS  TC-REG-02   Register: 4,999.99 / text / negative rejected, exactly 5,000.00 accepted
    PASS  TC-REG-03   Register: duplicate account name is refused
    PASS  TC-REG-04   Register: empty name, name with comma, 101-char name rejected
    PASS  TC-REG-05   Register: name of exactly 100 characters is accepted
    PASS  TC-REG-06   Register: deposit 5000.999 is rounded DOWN to 5000.99
    PASS  TC-REG-07   Missing CSV: load/generate fail gracefully, first account = 1092000001 and file is created
    PASS  TC-DEP-01   Deposit 500 -> balance 10,500.00 and row saved
    PASS  TC-DEP-02   Deposit: 0, negative, text rejected; 100.009 rounded down to 100.00
    PASS  TC-DEP-03   Deposit: unknown account and wrong letter case are refused
    PASS  TC-WIT-01   Withdraw 2,500 -> balance 7,500.00 and row saved
    PASS  TC-WIT-02   Withdraw: 0.01 over balance refused, 0 refused, exact full balance allowed
    PASS  TC-WIT-03   Withdraw from an account with 0.00 balance is blocked
    PASS  TC-WIT-04   Withdraw 0.001 is rounded UP to 0.01
    PASS  TC-PER-01   Persistence: deposit, restart program, balance is still 10,500.00
    PASS  TC-INT-01   Show interest: 0 days rejected; 3-day table compounds 1.37/day
    PASS  TC-V-01     Validation: 21 rows -> 3 valid PHP, 2 non-PHP filtered, 16 invalid skipped
    PASS  TC-R1-01    Report 1: filter at 100,000, tier boundaries, partial month, same-day rows, ties
    PASS  TC-R1-02    Report 1: unfinished newest month -> previous month is used
    PASS  TC-R1-03    Report 1: leap-year February (29 days) counted as a complete month
    PASS  TC-R2-01    Report 2 + summary.json: 10,000 filter, latest balance used, totals
    PASS  TC-R3-01    Report 3: 500,000 and 90% boundaries, N/A velocity, sort order
    PASS  TC-R4-01    Report 4: year boundary, negative net flow, sort; empty Report 1 keeps header
    PASS  TC-FULL-01  Full dataset: counts, all 4 reports and summary.json identical to baseline

    Total: 26  Passed: 26  Failed: 0

## 7. Limitations found while testing

- **Amount parsing is lenient.** `atof` reads `12abc` as 12, so a value with trailing junk passes validation. A stricter check with `strtod` and its end pointer would catch it.
- **Names are case-sensitive.** `juan dela cruz` does not find `Juan Dela Cruz` (TC-DEP-03). This is intentional, but a user could mistype.
- **New transactions use the system clock.** Menu tests use fixtures dated 2020 so that new rows always sort after the old ones, whatever the computer's date.
- **Quoted CSV fields are not supported**, which is why commas in names are rejected (TC-REG-04).
