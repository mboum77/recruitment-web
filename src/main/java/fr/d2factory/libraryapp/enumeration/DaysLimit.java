package fr.d2factory.libraryapp.enumeration;

public enum DaysLimit {
    DAYS_LIMIT_15(15),
    DAYS_LIMIT_30(30),
    DAYS_LIMIT_60(60);

    public int limit;

    DaysLimit(int durationLimit) {
        this.limit = durationLimit;
    }

    public int getLimit() {
        return limit;
    }
}