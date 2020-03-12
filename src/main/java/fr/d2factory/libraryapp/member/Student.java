package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.enumeration.DaysLimit;
import fr.d2factory.libraryapp.exception.HasDaysException;
import fr.d2factory.libraryapp.exception.HasRegistrationDateException;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class Student extends Member {
    private LocalDate registrationDate;

    @Override
    public void payBook(int numberOfDays) throws HasDaysException {
        /*
    Students are charged 10 cents a day (0.10 eu).
    Exception to that rule are students who are in their first year,
    who have 15 days of free period for each book.

    If a student, regardless of what year they are in, keeps a book
    for more than 30 days they are considered to be "late".
         */
        if(numberOfDays<1)
            throw new HasDaysException();

        float newWallet = getWallet() - getNumberOfDaysToCharge(numberOfDays) * getChargeAmount();
        setWallet(newWallet);
    }

    private float getNumberOfDaysToCharge(int numberOfDays) {
        int freeLimit = getFreeLimit();
        if (isFirstYear())
            return numberOfDays < freeLimit ? 0 : numberOfDays - freeLimit;
        else return numberOfDays;
    }

    private float getChargeAmount() {
        return 0.1f;
    }

    private boolean isFirstYear() throws HasRegistrationDateException {
        if(getRegistrationDate()==null || getRegistrationDate().isAfter(LocalDate.now()))
            throw new HasRegistrationDateException();
        return DAYS.between(getRegistrationDate(), LocalDate.now()) <= 1;
    }

    private LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    protected int getLateLimit() {
        return DaysLimit.DAYS_LIMIT_30.getLimit();
    }

    protected int getFreeLimit() {
        return DaysLimit.DAYS_LIMIT_15.getLimit();
    }
}
