# Statement-Analyzer-GUI-V2
Statement Analyzer is a JavaFX-based personal accounting system that allows users to efficiently categorize transactions that
appear on credit card and banking statements. Users can create their own categories and populate them with transactions by
creating keyword maps that pull all categories containing a specific keyword.
## User Instructions
*Note: Statement Analyzer has only been tested on TD credit card and banking statements. The long term goal is to make
Statement Analyzer compatible with all banks.*

The user must begin by downloading all of their banking data from the TD website in a CSV format. After downloading, all
statements must be put in a single directory containing no other files.

1. Open the program and select File -> Import statements. Select the directory containing all banking CSV statement files.
2. Once imported, all transactions should appear under "Unclassified".
3. Start by creating categories using the toolbar on the right-hand-side.
4. Once categories are created, the user can start creating substring maps using the same toolbar. This will move all
transactions containing a desired keyword into the specified category.
5. The user can also move transactions into categories by selecting the transaction and pressing "C" on the keyboard. This
will bring up a list of all available categories from which the user can select by pressing "Enter".
6. After moving transactions, be sure to SAVE the configuration by selecting File -> Save in the menu bar.
6. Once all transactions have been categoriezed, the user may choose to view a monthly summary by selecting Statistics ->
Monthly Summary in the top menu bar.

## Current Version
- Added monthly summary view and created static currency format
- Added substring and id map viewing and editing capability

## Future Improvements
- Add annual summary view
- Create informative graphs
- Improve system styling
- Modularize statistics calculations
