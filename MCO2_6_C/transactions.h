#ifndef TRANSACTIONS_H
#define TRANSACTIONS_H

#include <stdio.h>
#include <math.h>
#include "csvData.h"
#include "type.h"

#define INTEREST_RATE 0.05

int depositAmount(Transaction *account, double amt);
int withdrawAmount(Transaction *account, double amt);
int interestAmount(Transaction *account, int days);


#endif