package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.Library;

/**
 * A member is a person who can borrow and return books to a {@link Library}
 * A member can be either a student or a resident
 */
public abstract class Member {
    /**
     * An initial sum of money the member has
     */
    private float wallet;

    /**
     * The member has book returned late
     */
    private boolean late;

    /**
     * The member should pay their books when they are returned to the library
     *
     * @param numberOfDays the number of days they kept the book
     */
    public abstract void payBook(int numberOfDays);

    /**
     * Wallet Accessor
     *
     * @return wallet
     */
    public float getWallet() {
        return wallet;
    }

    /**
     * Wallet Mutator
     *
     * @param wallet wallet member float value
     */
    public void setWallet(float wallet) {
        this.wallet = wallet;
    }

    /**
     * late status Accessor
     *
     * @return late Status
     */
    public boolean isLate() {
        return late;
    }

    /**
     * Late Status Mutator
     *
     * @param late late status value
     */
    public void setLate(boolean late) {
        this.late = late;
    }

    /**
     * Late Status Mutator with limit depending on member type
     *
     * @param borrowedBookDuration number of days the book has been borrowed
     * @param hasOtherBooks        considering if the member has other borrowed book to return
     */
    public void setLate(int borrowedBookDuration, boolean hasOtherBooks) {
/*
        If a member (student or resident) is late with their books they cannot
        borrow any new books before returning the previous ones.
                */
        if (borrowedBookDuration > getLateLimit() && hasOtherBooks)
            setLate(true);
        else if (!hasOtherBooks)
            setLate(false);
    }

    /**
     * Accessor late limit  value depending of the member type
     *
     * @return limit
     */
    protected abstract int getLateLimit();

}
