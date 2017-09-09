/**
 * 
 */
package constants;

/**
 * @author sachin.srivastava
 * Its a class of constants used in the projects.
 *
 */
public class Constants {

	public enum ItemStatus {
		Active , Expired
	}
	public enum BidExchangeCommands{
		addItem,
		placeBid,
		viewItem,
		notifyBidders,
		soldItem,
		expiryOfBid,
		showTopBids,
		showAllBidsAndItems;
	}
	public enum Messages{
		
		ItemAdded("Item added in the list "),
		ItemExist("Item already exist in the list"),
		ItemNotExist("Item not exist in the list"),
		ItemExpired("Item is expired so no order can be placed"),
		PlaceBidItemNotExist("You can't place order as item doesn't exist"),
		BidPriceNotInRange("bid price not in the range of the item auction price range"),
		OrderPlaced("bid order is placed with the price: "),
		NotifyBidders("All the bidders are being notified new item price: "),
		ItemSold("item is sold out at the price: "),
		NoItemAndBids("No items and bids to show.");
		
		private String name;
		private Messages(String name) {
			this.name = name;
		}
		public String getValue() {
			return name;
		}
	}
}
