#Fleabay Auction Application

This is the beginnings of an online auction application built to compete in the online auction arena.  In order to undercut the competition, the fee captured will be $1.00 or 1% of the auction value, whichever is greater.

### Decisions / Deviations

Because of the billing method, the minimum reserve and bid was set to $1.00.

The creation of an auction contains the auction object in the response of the POST, not just the unique identifier.

Because MongoDB was chosen for the data persistence layer, the type of the auctionItemId was changed from an integer to a string, so that it can hold the ObjectId value used by MongoDB.

### If I Had More Time...
