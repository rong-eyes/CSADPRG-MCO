#include "exchange.h"

//not gonna put the PHP among the options (since its the base) so USD is gonna be choice 1 and so on
void recordExchangeRate(ExchangeRate toPHP, int choice, double rate){
    toPHP[choice] = rate;
}

//result = amt * (PHP per source) / (PHP per target)
double convertCurrency(ExchangeRate toPHP, int from, int to, double amt){
    double ret;

    if(toPHP[from - 1] != 0){
        if(toPHP[to - 1] != 0){
            ret = (amt * toPHP[from - 1]) / toPHP[to - 1];
        }else{
            ret = -2;                   //to currency has no recorded rate
        }
    }else if(toPHP[to - 1] == 0){
        ret = -3;                       //both currency has no recorded rate

    }else{
        ret = -1;                       //from currency has no recorded rate
    }
    
    
    return ret;
}
