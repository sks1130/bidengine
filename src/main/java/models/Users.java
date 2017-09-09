/**
 * 
 */
package models;

import java.util.HashSet;

/**
 * @author sachin.srivastava
 *
 */
public class Users {
	
	private Long id;
	private String name;
	private HashSet<Long> items = new HashSet<>();
	
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
	public HashSet<Long> getItems() {
		return items;
	}
	public void setItems(HashSet<Long> items) {
		this.items = items;
	}
}
