#ifndef MENU_FUNCTIONS_H
#define MENU_FUNCTIONS_H

#include "printFunctions.h"
#include "type.h"
#include "csvData.h"
#include "transactions.h"
#include "exchange.h"
#include "inputCheck.h"
#include <stdio.h>

#define MIN_INITIAL_DEPOSIT 5000.00


void registerAccountMenu(TransactionList *A, const char *path);
void depositAmountMenu(TransactionList *A, const char *path);
void withdrawAmountMenu(TransactionList *A, const char *path);
void currencyExchangeMenu(ExchangeRate toPHP);
void recordExchangeRateMenu(ExchangeRate toPHP);
void showInterestMenu(TransactionList *A);

#endif