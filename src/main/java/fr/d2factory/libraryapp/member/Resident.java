package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.enumeration.ChargeAmount;
import fr.d2factory.libraryapp.enumeration.DaysLimit;

public class Resident extends Member {
    @Override
    public void payBook(int numberOfDays) {
        /*
 Residents are allowed to borrow books for a period of 60 days and are
    charged 10 cents a day (0.10 eu) for each day they keep the book
    If a resident keeps a book for more than 60 days they are obliged
    to pay 20 cents (0.20 eu) for each day after the initial 60 days and
    they are considered to be "late".
         */
        float newWallet = getWallet();
        float[] chargeRates = getChargeRate();
        float[] numberOfDaysToCharge = getNumberOfDaysToCharge(numberOfDays);
        for (int i = 0; i < 2; i++) {
            newWallet -= chargeRates[i] * numberOfDaysToCharge[i];
        }
        setWallet(newWallet);
    }

    private float[] getNumberOfDaysToCharge(int numberOfDays) {
        float[] daysToCharge = new float[2];

        daysToCharge[0] = Math.min(numberOfDays, getLateLimit());
        daysToCharge[1] = Math.max(numberOfDays - getLateLimit(), 0);

        return daysToCharge;
    }

    private float[] getChargeRate() {
        return new float[]{ChargeAmount.CHARGE_AMOUNT_0_1.getAmount(), ChargeAmount.CHARGE_AMOUNT_0_2.getAmount()};
    }

    @Override
    protected int getLateLimit() {
        return DaysLimit.DAYS_LIMIT_60.getLimit();
    }

}
