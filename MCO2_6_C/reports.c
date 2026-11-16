#include "reports.h"

typedef struct{
    int totalAccounts;
    double totalDeposits;
    double totalWithdrawals;
    double totalBalance;
    double totalInterestExpense;
} SummaryStats;

//computes the five summary numbers; deposits and withdrawals come from the loaded rows, the rest from the per-account summaries
static void fillSummaryStats(const TransactionList *l, const AccountSummaryList *s, SummaryStats *st){
    st->totalAccounts = s->count;
    st->totalDeposits = 0;
    st->totalWithdrawals = 0;
    st->totalBalance = 0;
    st->totalInterestExpense = 0;

    for(int i = 0; i < l->count; i++){
        if(l->data[i].type == DEPOSIT){
            st->totalDeposits += l->data[i].amount;
        }else{
            st->totalWithdrawals += l->data[i].amount;
        }
    }

    for(int i = 0; i < s->count; i++){
        st->totalBalance += s->data[i].latestBalance;

        if(s->data[i].latestBalance >= FILTER2){            //only the accounts that appear in Report 2
            st->totalInterestExpense += s->data[i].expectedInterest;
        }
    }

    st->totalDeposits = roundDecimals(st->totalDeposits);
    st->totalWithdrawals = roundDecimals(st->totalWithdrawals);
    st->totalBalance = roundDecimals(st->totalBalance);
    st->totalInterestExpense = roundDecimals(st->totalInterestExpense);
}

//writes the summary as JSON to any stream (the file or the screen); numbers are plain, since commas would make the JSON invalid
static void printSummaryJson(FILE *out, const SummaryStats *st){
    fprintf(out, "{\n");
    fprintf(out, "  \"total_accounts\": %d,\n", st->totalAccounts);
    fprintf(out, "  \"total_deposits\": %.2f,\n", st->totalDeposits);
    fprintf(out, "  \"total_withdrawals\": %.2f,\n", st->totalWithdrawals);
    fprintf(out, "  \"total_balance\": %.2f,\n", st->totalBalance);
    fprintf(out, "  \"total_interest_expense\": %.2f\n", st->totalInterestExpense);     //no comma after the last entry
    fprintf(out, "}\n");
}

//saves the summary to summary.json; returns 1 on success
static int writeSummaryJson(const SummaryStats *st){
    FILE *fp = fopen(SUMMARY_FILE, "w");

    if(fp == NULL){
        printf("Could not create %s. Is it open in another program?\n", SUMMARY_FILE);
        return 0;
    }

    printSummaryJson(fp, st);

    if(fclose(fp) != 0){
        printf("Warning: %s may be incomplete.\n", SUMMARY_FILE);
        return 0;
    }
    return 1;
}

//generates all four reports and the summary; l must be the loaded list, sorted by account number then time
void generateAllReports(const TransactionList *l){
    AccountSummaryList summary;
    DailyBehaviorList daily;
    MonthlyFlowList monthly;
    SummaryStats stats;
    int month, year;

    if(l->count == 0){
        printf("No transactions loaded. Load the file first.\n");
        return;
    }

    printf("Generating reports...\n");
    printf("Outputs saved to individual files...\n\n");

    referenceMonth(l, &month, &year);

    //reports 1 and 2 (and the summary) share the per-account summaries
    if(!allocAccountSummary(l, &summary)){
        printf("Could not generate the reports (not enough memory).\n");
        return;
    }
    fillAccountSummary(l, &summary, month, year);

    printf("Report 1: Banking Privilege Tiers\n");
    if(generateReport1(&summary)) printf("(Full table exported to %s)\n", REPORT1);
    printf("\n");

    printf("Report 2: Interest Expense Projection\n");
    if(generateReport2(&summary)) printf("(Full table exported to %s)\n", REPORT2);
    printf("\n");

    //report 3 builds its own array, one entry per account per active day
    printf("Report 3: Suspicious Client Behavior\n");
    if(fillDailyBehavior(l, &daily)){
        if(generateReport3(&daily)) printf("(Full table exported to %s)\n", REPORT3);
        freeDailyBehavior(&daily);
    }else{
        printf("Could not build this report (not enough memory).\n");
    }
    printf("\n");

    //report 4 builds one entry per account per active month
    printf("Report 4: Monthly Net Cash Flow\n");
    if(fillMonthlyFlow(l, &monthly)){
        if(generateReport4(&monthly)) printf("(Full table exported to %s)\n", REPORT4);
        freeMonthlyFlow(&monthly);
    }else{
        printf("Could not build this report (not enough memory).\n");
    }
    printf("\n");

    fillSummaryStats(l, &summary, &stats);
    printf("Summary State (%s):\n", SUMMARY_FILE);
    writeSummaryJson(&stats);                               //prints its own message if the file can't be written
    printSummaryJson(stdout, &stats);

    freeAccountSummary(&summary);
}
