/**
 * 
 */
package models;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import constants.Constants.ItemStatus;

/**
 * @author sachin.srivastava
 *
 */
public class Items{
	
	private Long id;//unique id
	private String name;
	private Long maxPrice;  //max desired price if reaches then bid will close to highest bidder.
	private Long expiryTime; // max expiry time for bid time to close
	private Long minPrice = 1L;  // setting a default minimum value to 1
	private Long currentPrice;
	private ItemStatus status = ItemStatus.Active;
	public PriorityBlockingQueue<BidNode> prioityQueue = new PriorityBlockingQueue<>(11,new Comparator<BidNode>() {
		//it will be act like a maxHeap and head would always give top bid user//deafult capacity 11
		@Override
		public int compare(BidNode o1, BidNode o2) {
			return o2.getBidPrice().compareTo(o1.getBidPrice());
		}
	
	});
	
	public Items(Long id , String name, Long maxPrice,Long expiryTime ) {
		this.id = id;
		this.name = name;
		this.maxPrice = maxPrice;
		this.expiryTime = expiryTime;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(Long minPrice) {
		this.minPrice = minPrice;
	}
	public Long getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(Long maxPrice) {
		this.maxPrice = maxPrice;
	}
	public Long getExpiryTime() {
		return expiryTime;
	}
	public void setExpiryTime(Long expiryTime) {
		this.expiryTime = expiryTime;
	}
	public Long getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(Long currentPrice) {
		this.currentPrice = currentPrice;
	}

	public ItemStatus getStatus() {
		return status;
	}

	public void setStatus(ItemStatus status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Items [id=" + id + ", name=" + name + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice
				+ ", expiryTime=" + expiryTime + ", currentPrice=" + currentPrice + "]";
	}

	public PriorityBlockingQueue<BidNode> getPrioityQueue() {
		return prioityQueue;
	}

	public void setPrioityQueue(PriorityBlockingQueue<BidNode> prioityQueue) {
		this.prioityQueue = prioityQueue;
	}
}
