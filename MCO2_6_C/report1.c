#include "report1.h"

//helper comparison function for qsort to sort by average daily balance (ADB)
static int cmpByAverageDailyBalance(const void *a, const void *b){
    const AccountSummary *x = a, *y = b;

    //descending ADB: positive when x is smaller, so the larger balance goes first
    int c = (x->avgDailyBalance < y->avgDailyBalance) - (x->avgDailyBalance > y->avgDailyBalance);
    if(c) return c;                                     //different ADB, so the order is decided

    //same ADB: lower account number first
    return (x->accountNumber > y->accountNumber) - (x->accountNumber < y->accountNumber);
}

//helper function to sort in a descending order of ADB
static void sortSummary1(AccountSummaryList *s){
    qsort(s->data, s->count, sizeof(AccountSummary), cmpByAverageDailyBalance);
}

//helper function for return the tiers depending on the conditions
static char* privilegeTiers(double adb){
    if(adb < SILVER_LIMIT) return "Silver";
    if(adb < GOLD_LIMIT) return "Gold";
    return "Platinum";
}

//function to generate report 1
int generateReport1(AccountSummaryList *s){
    char bal[NUM_LEN + 2], adb[NUM_LEN + 2];
    int shown = 0;                                          //rows previewed on screen so far
    FILE *fp = openReportFile(REPORT1, "AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier");
    if(fp == NULL) return 0;

    sortSummary1(s);                            //sort descending order of ADB

    for(int i = 0; i < s->count; i++){
        AccountSummary *a = &s->data[i];
        if(a->avgDailyBalance >= FILTER1){              //must be >= P100,000.00  
            formatNumberCsv(a->monthEndBalance, bal);
            formatNumberCsv(a->avgDailyBalance, adb);
            
            fprintf(fp, "%s,%d,%s,%s,%s\n", a->accountName, a->accountAge, bal, adb, privilegeTiers(a->avgDailyBalance));
            
            if(shown < 2){                                      //the first two rows double as the on-screen preview
                formatNumber(a->monthEndBalance, bal);
                formatNumber(a->avgDailyBalance, adb);
                
                if(shown == 0){
                    printf("%-20s %-11s %-18s %-20s %s\n", "AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier");
                }
                printf("%-20s %-11d %-18s %-20s %s\n", a->accountName, a->accountAge, bal, adb, privilegeTiers(a->avgDailyBalance));
                shown++;
            }
        } 
    }
    if(!closeReportFile(fp)){
        printf("Warning: the report file may be incomplete.\n");
        return 0;
    }
    return 1;
}
