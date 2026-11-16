#include "report2.h"

//helper comparison function for qsort to sort by expected interest
static int cmpByExpectedInterest(const void *a, const void *b){
    const AccountSummary *x = a, *y = b;

    //descending expected interest
    int c = (x->expectedInterest < y->expectedInterest) - (x->expectedInterest > y->expectedInterest);
    if(c) return c;

    //same interest: lower account number first
    return (x->accountNumber > y->accountNumber) - (x->accountNumber < y->accountNumber);
}

//helper function to sort in a descending order of expected interest
static void sortSummary2(AccountSummaryList *s){
    qsort(s->data, s->count, sizeof(AccountSummary), cmpByExpectedInterest);
}

//function to generate report 2
int generateReport2(AccountSummaryList *s){
    char bal[NUM_LEN+ 2], ei[NUM_LEN + 2];
    int shown = 0;                                          //rows previewed on screen so far
    FILE *fp = openReportFile(REPORT2, "AccountName,CurrentBalance,ExpectedInterest");
    if(fp == NULL) return 0;

    sortSummary2(s);                            //sort descending order of ADB

    for(int i = 0; i < s->count; i++){
        AccountSummary *a = &s->data[i];
        if(a->latestBalance >= FILTER2){              //must be >= P10,000.00  ;
            formatNumberCsv(a->latestBalance, bal);
            formatNumberCsv(a->expectedInterest, ei);
            
            fprintf(fp, "%s,%s,%s\n", a->accountName, bal, ei);
            
            if(shown < 2){                                      //the first two rows double as the on-screen preview
                formatNumber(a->latestBalance, bal);
                formatNumber(a->expectedInterest, ei);
                
                if(shown == 0){
                    printf("%-20s %-18s %-20s\n", "AccountName", "CurrentBalance", "ExpectedInterest");
                }
                printf("%-20s %-18s %-20s\n", a->accountName, bal, ei);
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
