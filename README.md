#Fleabay Auction Application

This is the beginnings of an online auction application built to compete in the online auction arena.  In order to undercut the competition, the fee captured will be $1.00 or 1% of the auction value, whichever is greater.

### Decisions / Deviations

Because of the billing scheme suggested above, the minimum reserve and minimum bid are $1.00.

The creation of an auction contains the auction object in the response of the POST, not just the unique identifier.

Because MongoDB was chosen for the data persistence layer, the type of the auctionItemId was changed from an integer to a string, so that it can hold the ObjectId value used by MongoDB.

Implemented transaction style processing on bid updates with retries for now.  Would need to do in depth testing to see if the JPA @Version is enough for large amounts of concurrent requests.

### If I Had More Time...
- Get some better global settings on BigDecimal: Scale, Serialization, and Deserialization
- Enable SSL
- Implement pagination on the GET /auctionItems endpoint
- Hook outbid events into an SNS topic
- Work out the Cloud Formation template / CLI commands to deploy this application out to AWS using DocumentDB as the backend
- Figure out the strategy around auctions ending

### Other Notes

Testing names inspiration [here](https://www.youtube.com/watch?v=4UtlH6jkBXw). 