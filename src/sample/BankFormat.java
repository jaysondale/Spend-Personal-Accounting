package sample;

/**
 * BankFormat
 * Enum used to classify formats of bank statements from different banks
 */
public enum BankFormat {
    // TODO: Add custom date formats
    TD (5,0,1,2,3,4); // TD Bank Format

    private final int lineSize;
    private final int dateIndex;
    private final int idIndex;
    private final int debitIndex;
    private final int creditIndex;
    private final int balanceIndex;

    /**
     * Constructor
     * @param lineSize Total number of items to appear on each line of the statement (this is used to handle commas appearing in the id string)
     * @param dateIndex Index of transaction date
     * @param idIndex Index of transaction id
     * @param debitIndex Index of transaction debit amount
     * @param creditIndex Index of transaction credit amount
     * @param balanceIndex Index of statement balance amount
     */
    BankFormat(int lineSize, int dateIndex, int idIndex, int debitIndex, int creditIndex, int balanceIndex) {
        this.lineSize = lineSize;
        this.dateIndex = dateIndex;
        this.idIndex = idIndex;
        this.debitIndex = debitIndex;
        this.creditIndex = creditIndex;
        this.balanceIndex = balanceIndex;
    }

    // Getters
    public int getBalanceIndex() {
        return balanceIndex;
    }

    public int getCreditIndex() {
        return creditIndex;
    }

    public int getDateIndex() {
        return dateIndex;
    }

    public int getDebitIndex() {
        return debitIndex;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public int getLineSize() {
        return lineSize;
    }
}
