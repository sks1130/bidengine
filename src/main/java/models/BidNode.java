/**
 * 
 */
package models;

/**
 * @author sachin
 *
 */
public class BidNode {
	private Long userId;   //user id
	private Long bidPrice;   //bidPrice
	public BidNode(Long userId ,Long bidPrice) {
		this.userId = userId;
		this.bidPrice = bidPrice;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(Long bidPrice) {
		this.bidPrice = bidPrice;
	}

	@Override
	public String toString() {
		return "BidNode [userId=" + userId + ", bidPrice=" + bidPrice + "]";
	}
}
