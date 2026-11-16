#ifndef REPORT1_H
#define REPORT1_H

#include "accountSummary.h"

#define REPORT1 "report1_banking_privilege_tiers.csv"
#define FILTER1 100000.00  
#define SILVER_LIMIT 500000.00
#define GOLD_LIMIT 1000000.00

int generateReport1(AccountSummaryList *s);

#endif
