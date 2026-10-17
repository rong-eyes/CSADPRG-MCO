#ifndef EXCHANGE_H
#define EXCHANGE_H

//0 = PHP, 1 = USD, 2 = JPY, 3 = GBP, 4 = EUR, 5 = CNY
typedef double ExchangeRate[6];

void recordExchangeRate(ExchangeRate toPHP, int choice, double rate);
double convertCurrency(ExchangeRate toPHP, int from, int to, double amt);

#endif