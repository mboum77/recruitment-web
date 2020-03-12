package fr.d2factory.libraryapp.enumeration;

public enum ChargeAmount {
    CHARGE_AMOUNT_0_1(0.1f),
    CHARGE_AMOUNT_0_2(0.2f);

    public float amount;

    ChargeAmount(float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }
}