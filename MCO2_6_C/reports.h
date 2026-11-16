#ifndef REPORTS_H
#define REPORTS_H

#include "csvData.h"

#define SUMMARY_FILE "summary.json"

//generates the four reports and summary.json from the loaded list (sorted by account number, then time)
void generateAllReports(const TransactionList *l);

#endif
