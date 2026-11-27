/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.math.BigDecimal;
import java.math.RoundingMode;

// 5% annual, interest for each day = balance x 0.05 / 365 
public final class Interest{
    static final BigDecimal ANNUAL_RATE = new BigDecimal("0.05");
    static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);

    private Interest(){}

    static BigDecimal daily(BigDecimal balance){
        return balance.multiply(ANNUAL_RATE).divide(DAYS_IN_YEAR, 2, RoundingMode.HALF_UP);
    }

    static BigDecimal projected(BigDecimal balance, int days){
        BigDecimal total = BigDecimal.ZERO;

        for (int day = 1; day <= days; day++){
            BigDecimal interest = daily(balance);
            balance = balance.add(interest);
            total = total.add(interest);
        }

        return total;
    }
}
