#ifndef MENU_FUNCTIONS_H
#define MENU_FUNCTIONS_H

#include "printFunctions.h"
#include "accounts.h"
#include "transactions.h"
#include "exchange.h"
#include "inputCheck.h"
#include <stdio.h>

void registerAccountMenu(AccountList *A);
void depositAmountMenu(AccountList *A);
void withdrawAmountMenu(AccountList *A);
void currencyExchangeMenu(ExchangeRate toPHP);
void recordExchangeRateMenu(ExchangeRate toPHP);
void showInterestMenu(AccountList *A);

#endif