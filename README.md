#Fleabay Auction Application

This is the beginnings of an online auction application built to compete in the online auction arena.  In order to undercut the competition, the fee captured will be $1.00 or 1% of the auction value, whichever is greater.

### Decisions / Deviations

Because of the billing scheme suggested above, the minimum reserve and minimum bid are $1.00.

The creation of an auction contains the auction object in the response of the POST, not just the unique identifier.

Because MongoDB was chosen for the data persistence layer, the type of the auctionItemId was changed from an integer to a string, so that it can hold the ObjectId value used by MongoDB.

MongoDB needs to be running locally in order to run this application locally as there is not a true embedded implementation.

Implemented transaction style processing on bid updates with retries for now.  Would need to do in depth testing to see if the JPA `@Version` is enough for large amounts of concurrent requests.

Decided to return an Auction object on the `/bids` POST to give the client an updated view of how their bid affected the auction.  Would consider moving this endpoint to `/auctionItems/{auctionItemId}/bids` to be more in line with RESTful conventions, but did not want to deviate that much from the specification given.

Because of the bid response, outbid events are only produced when the bidderName is changed at the persistence layer.

### If I Had More Time...
- Better error response messaging
- Get some better global settings on BigDecimal: Scale, Serialization, and Deserialization
- Enable SSL
- Implement pagination on the GET /auctionItems endpoint
- Hook outbid events into an SNS topic
- Work out the Cloud Formation template / CLI commands to deploy this application out to AWS using DocumentDB as the backend
- Figure out the strategy around auctions ending

### Other Notes

Testing names inspiration [here](https://www.youtube.com/watch?v=4UtlH6jkBXw). 