package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification.Notification;

/**
 * Return value for adding samples to the samples cart.
 */
public class CartUpdateResponse extends AjaxResponse {
	private final List<Notification> notifications = new ArrayList<>();

	/**
	 * Total number of samples in the cart after the update.
	 */
	private int count;

	/**
	 * Add a new notification to the response
	 * @param notification {@link Notification}
	 */
	public void addNotification(Notification notification) {
		this.notifications.add(notification);
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}
