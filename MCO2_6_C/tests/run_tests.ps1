# Automated test runner for MCO2 (C version).
# Builds the program, then runs every test case below in its own folder under tests\work\
# by feeding a prepared input file to stdin (as if a user typed it) and checking the results.
# Results are printed and saved to tests\test_results.txt.

$ErrorActionPreference = 'Continue'
$TestDir = $PSScriptRoot
$ProjDir = Split-Path $TestDir -Parent
$WorkDir = Join-Path $TestDir 'work'
$Exe     = Join-Path $WorkDir 'run.exe'
$TimeoutMs = 20000                          # a case that waits forever for input is counted as a failure

# ---------------------------------------------------------------------------
# Test cases
#   Fixture   : CSV copied in as bank_transactions.csv (path relative to tests\), or '' for "no file"
#   Inputs    : one or more stdin files from tests\inputs\; more than one = program is restarted between them
#   Console   : text that must appear in the console output of the LAST run
#   CsvHas    : text that must appear in bank_transactions.csv afterwards
#   CsvHasNot : text that must NOT appear in bank_transactions.csv afterwards
#   Every file in tests\expected\<Id>\ is compared line by line with the file the program wrote.
# ---------------------------------------------------------------------------
$Cases = @(
    # ---- Menu / input handling ----
    @{ Id='TC-M-01';   Desc='Main menu rejects out-of-range, text and empty choices';
       Fixture='fixtures\base.csv'; Inputs=@('main_menu_invalid.txt');
       Console=@('Invalid option. Try again!', 'Exiting the program.') },
    @{ Id='TC-M-02';   Desc='Reports sub-menu rejects invalid choices';
       Fixture='fixtures\base.csv'; Inputs=@('reports_menu_invalid.txt');
       Console=@('Invalid input. Try again!', '(4 rows loaded, 4 filtered for PHP currency, 0 invalid rows skipped)') },

    # ---- REQ-0001 Register ----
    @{ Id='TC-REG-01'; Desc='Register: new account gets highest existing number + 1 (not count + 1)';
       Fixture='fixtures\base.csv'; Inputs=@('register_valid.txt');
       Console=@('Account created successfully!');
       CsvHas=@('Pedro Penduko,1092000006,', ',PHP,10000.00,deposit,10000.00') },
    @{ Id='TC-REG-02'; Desc='Register: 4,999.99 / text / negative rejected, exactly 5,000.00 accepted';
       Fixture='fixtures\base.csv'; Inputs=@('register_min_deposit.txt');
       Console=@('Invalid amount! Minimum initial deposit is 5000.00. Try again!', 'Account created successfully!');
       CsvHas=@('Low Deposit,1092000006,', ',PHP,5000.00,deposit,5000.00') },
    @{ Id='TC-REG-03'; Desc='Register: duplicate account name is refused';
       Fixture='fixtures\base.csv'; Inputs=@('register_duplicate.txt');
       Console=@('Account already exists.');
       CsvHasNot=@('1092000006') },
    @{ Id='TC-REG-04'; Desc='Register: empty name, name with comma, 101-char name rejected';
       Fixture='fixtures\base.csv'; Inputs=@('register_invalid_names.txt');
       Console=@('Invalid name (empty, over 100 characters, or contains a comma). Try again!', 'Account created successfully!');
       CsvHas=@('Juan Dela Cruz Jr,1092000006,');
       CsvHasNot=@('Dela Cruz, Juan', 'AAAAAAAAAA') },
    @{ Id='TC-REG-05'; Desc='Register: name of exactly 100 characters is accepted';
       Fixture='fixtures\base.csv'; Inputs=@('register_100_char_name.txt');
       Console=@('Account created successfully!');
       CsvHas=@(('B' * 100) + ',1092000006,') },
    @{ Id='TC-REG-06'; Desc='Register: deposit 5000.999 is rounded DOWN to 5000.99';
       Fixture='fixtures\base.csv'; Inputs=@('register_rounding.txt');
       CsvHas=@('Rounding Test,1092000006,', ',PHP,5000.99,deposit,5000.99') },
    @{ Id='TC-REG-07'; Desc='Missing CSV: load/generate fail gracefully, first account = 1092000001 and file is created';
       Fixture=''; Inputs=@('missing_file.txt');
       Console=@('Could not open bank_transactions.csv', 'No transactions loaded. Load the file first.', 'Account created successfully!');
       CsvHas=@('account_name,account_number,date,time,currency,amount,type,current_balance', 'Juan Dela Cruz,1092000001,', ',PHP,10000.00,deposit,10000.00') },

    # ---- REQ-0002 Deposit / Withdraw ----
    @{ Id='TC-DEP-01'; Desc='Deposit 500 -> balance 10,500.00 and row saved';
       Fixture='fixtures\base.csv'; Inputs=@('deposit_valid.txt');
       Console=@('Current Balance: 10000.00', 'Currency: PHP', 'Updated Balance: 10500.00');
       CsvHas=@(',PHP,500.00,deposit,10500.00') },
    @{ Id='TC-DEP-02'; Desc='Deposit: 0, negative, text rejected; 100.009 rounded down to 100.00';
       Fixture='fixtures\base.csv'; Inputs=@('deposit_invalid_amounts.txt');
       Console=@('Invalid amount. Try again!', 'Updated Balance: 10100.00');
       CsvHas=@(',PHP,100.00,deposit,10100.00') },
    @{ Id='TC-DEP-03'; Desc='Deposit: unknown account and wrong letter case are refused';
       Fixture='fixtures\base.csv'; Inputs=@('deposit_unknown_account.txt');
       Console=@('Account does not exist.') },
    @{ Id='TC-WIT-01'; Desc='Withdraw 2,500 -> balance 7,500.00 and row saved';
       Fixture='fixtures\base.csv'; Inputs=@('withdraw_valid.txt');
       Console=@('Updated Balance: 7500.00');
       CsvHas=@(',PHP,2500.00,withdrawal,7500.00') },
    @{ Id='TC-WIT-02'; Desc='Withdraw: 0.01 over balance refused, 0 refused, exact full balance allowed';
       Fixture='fixtures\base.csv'; Inputs=@('withdraw_full_balance.txt');
       Console=@('Insufficient funds. Try again!', 'Invalid amount. Try again!', 'Updated Balance: 0.00');
       CsvHas=@(',PHP,10000.00,withdrawal,0.00') },
    @{ Id='TC-WIT-03'; Desc='Withdraw from an account with 0.00 balance is blocked';
       Fixture='fixtures\base.csv'; Inputs=@('withdraw_zero_balance.txt');
       Console=@('Cannot withdraw. Account has no funds.') },
    @{ Id='TC-WIT-04'; Desc='Withdraw 0.001 is rounded UP to 0.01';
       Fixture='fixtures\base.csv'; Inputs=@('withdraw_rounding.txt');
       Console=@('Updated Balance: 9999.99');
       CsvHas=@(',PHP,0.01,withdrawal,9999.99') },
    @{ Id='TC-PER-01'; Desc='Persistence: deposit, restart program, balance is still 10,500.00';
       Fixture='fixtures\base.csv'; Inputs=@('deposit_valid.txt', 'withdraw_after_restart.txt');
       Console=@('Current Balance: 10500.00', 'Insufficient funds. Try again!', 'Updated Balance: 0.00');
       CsvHas=@(',PHP,500.00,deposit,10500.00', ',PHP,10500.00,withdrawal,0.00') },
    @{ Id='TC-INT-01'; Desc='Show interest: 0 days rejected; 3-day table compounds 1.37/day';
       Fixture='fixtures\base.csv'; Inputs=@('interest_table.txt');
       Console=@('Invalid days. Try again!', '| 1.37     | 10001.37', '| 1.37     | 10004.11') },

    # ---- REQ-0003 to REQ-0005 Data ingestion ----
    @{ Id='TC-V-01';   Desc='Validation: 21 rows -> 3 valid PHP, 2 non-PHP filtered, 16 invalid skipped';
       Fixture='fixtures\validation.csv'; Inputs=@('generate_reports.txt');
       Console=@('(21 rows loaded, 3 filtered for PHP currency, 16 invalid rows skipped)', '"total_accounts": 3,', '"total_deposits": 30000.00,') },

    # ---- REQ-0006 to REQ-0010 Reports ----
    @{ Id='TC-R1-01';  Desc='Report 1: filter at 100,000, tier boundaries, partial month, same-day rows, ties';
       Fixture='fixtures\report1_tiers.csv'; Inputs=@('generate_reports.txt') },
    @{ Id='TC-R1-02';  Desc='Report 1: unfinished newest month -> previous month is used';
       Fixture='fixtures\report1_incomplete_month.csv'; Inputs=@('generate_reports.txt') },
    @{ Id='TC-R1-03';  Desc='Report 1: leap-year February (29 days) counted as a complete month';
       Fixture='fixtures\report1_leap_february.csv'; Inputs=@('generate_reports.txt') },
    @{ Id='TC-R2-01';  Desc='Report 2 + summary.json: 10,000 filter, latest balance used, totals';
       Fixture='fixtures\report2_interest.csv'; Inputs=@('generate_reports.txt') },
    @{ Id='TC-R3-01';  Desc='Report 3: 500,000 and 90% boundaries, N/A velocity, sort order';
       Fixture='fixtures\report3_suspicious.csv'; Inputs=@('generate_reports.txt') },
    @{ Id='TC-R4-01';  Desc='Report 4: year boundary, negative net flow, sort; empty Report 1 keeps header';
       Fixture='fixtures\report4_cashflow.csv'; Inputs=@('generate_reports.txt') },

    # ---- Full provided dataset (regression / reproducibility, REQ-0012) ----
    @{ Id='TC-FULL-01'; Desc='Full dataset: counts, all 4 reports and summary.json identical to baseline';
       Fixture='..\bank_transactions.csv'; Inputs=@('generate_reports.txt');
       Console=@('(9,189 rows loaded, 9,189 filtered for PHP currency, 0 invalid rows skipped)') }
)

# ---------------------------------------------------------------------------
function Get-Lines([string]$path) {
    # reads a file as lines, ignoring CRLF vs LF differences
    return @([System.IO.File]::ReadAllText($path) -split "`r?`n" | Where-Object { $_ -ne '' })
}

function Invoke-Program([string]$dir, [string]$inputFile, [string]$consoleFile) {
    $p = Start-Process -FilePath $Exe -WorkingDirectory $dir -NoNewWindow -PassThru `
         -RedirectStandardInput $inputFile -RedirectStandardOutput $consoleFile
    if (-not $p.WaitForExit($TimeoutMs)) {
        try { $p.Kill() } catch {}
        return $false
    }
    return $true
}

# ---------------------------------------------------------------------------
# Build
if (Test-Path $WorkDir) { Remove-Item $WorkDir -Recurse -Force }
New-Item -ItemType Directory $WorkDir | Out-Null

if (-not (Get-Command gcc -ErrorAction SilentlyContinue)) {
    Write-Host 'gcc was not found. Install MinGW-w64 and add its bin folder to PATH.' -ForegroundColor Red
    exit 1
}
Write-Host 'Building...'
& gcc -Wall -std=c99 (Join-Path $ProjDir 'main.c') -o $Exe -lm
if ($LASTEXITCODE -ne 0) { Write-Host 'Build failed.' -ForegroundColor Red; exit 1 }

# ---------------------------------------------------------------------------
# Run
$report = New-Object System.Collections.Generic.List[string]
$report.Add("MCO2 test results - $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')")
$report.Add("Compiler: $((& gcc --version | Select-Object -First 1))")
$report.Add('')
$passed = 0; $failed = 0

foreach ($c in $Cases) {
    $caseDir = Join-Path $WorkDir $c.Id
    New-Item -ItemType Directory $caseDir | Out-Null
    $csv = Join-Path $caseDir 'bank_transactions.csv'
    if ($c.Fixture) { Copy-Item (Join-Path $TestDir $c.Fixture) $csv }

    $problems = New-Object System.Collections.Generic.List[string]
    $console = ''
    $run = 0
    foreach ($in in $c.Inputs) {
        $run++
        $consoleFile = Join-Path $caseDir "console_run$run.txt"
        if (-not (Invoke-Program $caseDir (Join-Path $TestDir "inputs\$in") $consoleFile)) {
            $problems.Add("run $run timed out (program kept waiting for input)")
        }
        $console = [System.IO.File]::ReadAllText($consoleFile)
    }

    foreach ($s in @($c.Console)) {
        if ($s -and -not $console.Contains($s)) { $problems.Add("console is missing: $s") }
    }

    $csvText = if (Test-Path $csv) { [System.IO.File]::ReadAllText($csv) } else { '' }
    foreach ($s in @($c.CsvHas)) {
        if ($s -and -not $csvText.Contains($s)) { $problems.Add("bank_transactions.csv is missing: $s") }
    }
    foreach ($s in @($c.CsvHasNot)) {
        if ($s -and $csvText.Contains($s)) { $problems.Add("bank_transactions.csv should not contain: $s") }
    }

    $expDir = Join-Path $TestDir "expected\$($c.Id)"
    if (Test-Path $expDir) {
        foreach ($exp in Get-ChildItem $expDir -File) {
            $actPath = Join-Path $caseDir $exp.Name
            if (-not (Test-Path $actPath)) { $problems.Add("$($exp.Name) was not created"); continue }
            $e = Get-Lines $exp.FullName
            $a = Get-Lines $actPath
            $n = [Math]::Max($e.Count, $a.Count)
            for ($i = 0; $i -lt $n; $i++) {
                $el = if ($i -lt $e.Count) { $e[$i] } else { '<no line>' }
                $al = if ($i -lt $a.Count) { $a[$i] } else { '<no line>' }
                if ($el -cne $al) {
                    $problems.Add("$($exp.Name) line $($i + 1): expected [$el] got [$al]")
                    break
                }
            }
        }
    }

    if ($problems.Count -eq 0) {
        $passed++
        Write-Host ("PASS  {0,-11} {1}" -f $c.Id, $c.Desc) -ForegroundColor Green
        $report.Add(("PASS  {0,-11} {1}" -f $c.Id, $c.Desc))
    } else {
        $failed++
        Write-Host ("FAIL  {0,-11} {1}" -f $c.Id, $c.Desc) -ForegroundColor Red
        $report.Add(("FAIL  {0,-11} {1}" -f $c.Id, $c.Desc))
        foreach ($p in $problems) {
            Write-Host "        - $p" -ForegroundColor Yellow
            $report.Add("        - $p")
        }
    }
}

$report.Add('')
$report.Add("Total: $($Cases.Count)  Passed: $passed  Failed: $failed")
$report | Set-Content -Encoding UTF8 (Join-Path $TestDir 'test_results.txt')

Write-Host ''
Write-Host "Total: $($Cases.Count)  Passed: $passed  Failed: $failed"
Write-Host "Console logs and output files for each case are in tests\work\<case id>\"
Write-Host 'Summary saved to tests\test_results.txt'
exit $failed
