package bidengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import constants.Constants.BidExchangeCommands;
import constants.Constants.ItemStatus;
import constants.Constants.Messages;
import models.BidNode;
import models.Items;

/**
 * @author sachin.srivastava
 * This class is starting point of execution of bidding engine platform 
 * @params either it accepts the inputs from the console in the form of command and inputs and return the output
 * assumptions are taken that User are registered and stored in the DB and authenticated so no extra map of users is maintained
 */
public class BiddingExchange {

	// this map will act like cache , it can be extended to a distributed cache
	// or Couchbase/Memcached/redis
	// here so constant time retrival in O(1) time and when an auction completes
	// it will be evicted
	private static Map<Long, Items> auctionItems = new ConcurrentHashMap<>();
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	public static void main(String[] args) {
		try {
			System.out.println("***Welcome to the Bidding Engine Platform****");
			System.out.println("commands to be used for inputs::" + Arrays.toString(BidExchangeCommands.values()));
			System.out.println(
					"these commands can be translated into a MVC controller method if using spring , struts or somother MVC framework.");
			Scanner scan = new Scanner(System.in);
			String nextLine = scan.nextLine();
			accept(nextLine, scan);
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void accept(String input, Scanner scan) {
		try {
			if (input == null || input.isEmpty()) {
				// when no filename is entered then w'll go by default console input
				// and output till the loop ends when Input : is blank entered
				System.out.println("Enter the commands to perform actions::");
				System.out.println();
				System.out.println("Input:");
				String commandInput = scan.nextLine();
				System.out.println();
				System.out.println("Output:");
				System.out.println(inputCommand(commandInput));
				while (!commandInput.isEmpty()) {
					System.out.println();
					System.out.println("Input:");
					String commandInput2 = scan.nextLine();
					commandInput = commandInput2;
					if (commandInput.isEmpty()) {
						// break the loop and exit from the console when enter any
						// empty
						System.out.println("Exiting from the system");
						break;
					}
					System.out.println();
					System.out.println("Output:");
					System.out.println(inputCommand(commandInput));
				}

			} else {
				// when filename is entered if absolute path is provided then it
				// will look into the its root src directory else read file from the
				// absolute path given
				File file = new File(input);
				if (file.exists() && !file.isDirectory()) {
					// read the file and show the output
					try (BufferedReader br = new BufferedReader(new FileReader(input))) {
						String commandInput = br.readLine();
						System.out.println(inputCommand(commandInput));
						while (commandInput != null) {
							commandInput = br.readLine();
							System.out.println(inputCommand(commandInput));
						}
					}
				} else {
					System.out.println("File not found");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private static String addItem(Long id, String name, Long maxPrice, Long expiry) {
		try {
			// adding item in the Auction items
			lock.writeLock().lock();
			Items item = new Items(id, name, maxPrice, expiry);
			if (!auctionItems.containsKey(id)) {
				auctionItems.put(id, item);
				// here we can have any KEY-VALUE caching engines would work
				// --memcached or couchbase or redis or best would be distributed
				// cache
				return Messages.ItemAdded.getValue() + item.toString();
			}
			return Messages.ItemExist.getValue();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}finally{
			lock.writeLock().unlock();
		}
	}

	public static Items getItem(Long id) {
		try {
			lock.readLock().lock();
			return auctionItems.get(id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			lock.readLock().unlock();
		}
		
	}

	public static int totalItems() {
		return auctionItems.size();
	}

	private static String placeBid(Long itemId, Long userId, Long price) {
		try {
			lock.writeLock().lock();
			Items item = getItem(itemId);
			if (item == null) {
				return Messages.PlaceBidItemNotExist.getValue();
			}
			if (!item.getStatus().equals(ItemStatus.Active)) {
				return Messages.ItemExpired.getValue();
			}
			if (price > item.getMaxPrice() || price < item.getMinPrice()) {
				return Messages.BidPriceNotInRange.getValue();
			}
			if (price >= item.getMaxPrice()) {
				return soldItem(item, userId, price); // item would be sold at this
														// point of time
			}
			BidNode head = item.getPrioityQueue().peek();
			if (head != null && price > head.getBidPrice()) {
				// if a price is higher than the head price then notify all the
				// bidders
				// call the notify method
				notifyBidders(item, price);
			}
			BidNode node = new BidNode(userId, price);
			item.getPrioityQueue().add(node);// adding bid in the maxHeap so as to
												// have head highest bidder
			return Messages.OrderPlaced.getValue() + price;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			lock.writeLock().unlock();
		}
	}

	private static String soldItem(Items item, Long userId, Long price) {
		try {
			// evict the item from the auctionItems map
			// mark item as expired
			// notify Bidders about the item is sold
			item.setStatus(ItemStatus.Expired);
			auctionItems.remove(item.getId());
			notifyBidders(item, price);
			return Messages.ItemSold.getValue() + price +  " and userId=" + userId + " " + item.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static void notifyBidders(Items item, Long price) {
		try {
			// we can add here Message Queues to implement this notification module
			if (price >= item.getMaxPrice()) {
				System.out.println(
						"Notifying to the bidders that " + Messages.ItemSold.getValue() + price + " " + item.toString());
			} else {
				System.out.println(Messages.NotifyBidders.getValue() + price + " " + item.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void showAllBidsAndItems() {
		try {
			// show all the active bids and auction items in the system
			System.out.println("****showing all the items and bids***");
			if (auctionItems.size() != 0) {
				for (Map.Entry<Long, Items> entry : auctionItems.entrySet()) {
					System.out.println(entry.getValue().toString());
					PriorityBlockingQueue<BidNode> pq = entry.getValue().getPrioityQueue();
					Iterator<BidNode> it = pq.iterator();
					if (it.hasNext()) {
						System.out.println("And Bids details are::");
					} else {
						System.out.println("No Bids details to show");
					}
					while (it.hasNext()) {
						BidNode node = it.next();
						System.out.println(node.toString());
					}
				}
			} else {
				System.out.println(Messages.NoItemAndBids.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void showTopBids(Long itemId, Long top) {
		if (top < 0) {
			System.out.println("incorrect top value: " + top);
			return;
		}
		try {
			if (auctionItems.size() != 0) {
				Items item = getItem(itemId);
				if (item == null) {
					System.out.println(Messages.NoItemAndBids.getValue());
				}
				System.out.println(item.toString());
				PriorityBlockingQueue<BidNode> pq = item.getPrioityQueue();
				Iterator<BidNode> it = pq.iterator();
				if (pq.size() > 0) {
					System.out.println("And Bids details are::");
				} else {
					System.out.println("No Bids details to show");
				}
				List<BidNode> list = new ArrayList<>();
				while (it.hasNext()) {
					list.add(it.next());
				}
				list.sort((node1, node2) -> node2.getBidPrice().compareTo(node1.getBidPrice()));//
				int size = list.size();
				// total time O(nlogn) time would take to select the top K bids
				for (int i = 0; i < size; i++) {
					if (i < top) {
						BidNode node = list.get(i);
						System.out.println(node.toString());
					} else {
						break;
					}
				}
			} else {
				System.out.println(Messages.NoItemAndBids.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String inputCommand(String commandInput) {
		if (commandInput == null || commandInput.isEmpty()) {
			return "";
		}
		try {
			return executeCommand(commandInput);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String executeCommand(String commandInput) throws Exception {
		String[] arr = commandInput.split(" "); // 0th element would always be
												// command
		String output = null;
		if (arr.length != 0 && arr[0] != null && !arr[0].isEmpty()) {
			switch (arr[0]) {
			case "addItem":
				output = addItem(Long.parseLong(arr[1]), arr[2], Long.parseLong(arr[3]), Long.parseLong(arr[4]));
				break;
			case "placeBid":
				output = placeBid(Long.parseLong(arr[1]), Long.parseLong(arr[2]), Long.parseLong(arr[3]));
				break;
			case "showAllBidsAndItems":
				showAllBidsAndItems();
				output = "";
				break;
			case "showTopBids":
				showTopBids(Long.parseLong(arr[1]), Long.parseLong(arr[2]));
				output = "";
				break;
			case "expiryOfBid":
				output = expiryOfBid(Long.parseLong(arr[1]));
				break;
			default:
				output = "";
				break;
			}
			return output;
		} else {
			return "";
		}
	}

	private static String expiryOfBid(Long itemId) {
		try {
			Items item = getItem(itemId);
			if (item == null) {
				return Messages.ItemNotExist.getValue();
			}
			BidNode head = item.getPrioityQueue().peek();
			return soldItem(item, head.getUserId(), head.getBidPrice());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
