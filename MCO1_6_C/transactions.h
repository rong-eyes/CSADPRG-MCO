#ifndef TRANSACTIONS_H
#define TRANSACTIONS_H

#include <stdio.h>
#include <math.h>
#include "accounts.h"

#define INTEREST_RATE 0.05

int depositAmount(AccountNode *account, double amt);
int withdrawAmount(AccountNode *account, double amt);
int interestAmount(AccountNode *account, int days);


#endif