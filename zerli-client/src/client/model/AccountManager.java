package client.model;

import common.Role;

/**
 * represents the buttons of left menu controller
 *
 * for each role there is different options,which are stored in an array of
 * strings
 * 
 * @author Katya, Aharon
 */
public class AccountManager {
	private static String[] guestViews = { "Account" };

	private static String[] customerViews = { "Orders", "Wallet Balance" };

	private static String[] managerViews = { "Orders", "Orders report", "Income report", "Complaints report",
			"Account settings", "New account" };
	private static String[] ownerViews = { "Orders report", "Income report", "Compare reports", "Complaints report" };
	private static String[] workerViews = { "Change Catalogue", "Surveys" };

	private static String[] supportViews = { "Complaints", "Complaints handler", "Discounts" };
	private static String[] deliveryManViews = { "Get order" };
	private static String[] serviceExpertViews = { "Surveys handler" };
	private Model model;

	public AccountManager(Model model) {
		this.model = model;
	}

	/**
	 * due to the role update the buttons in left menus
	 *
	 * long method descriptor
	 *
	 * @author Katya, Aharon
	 */
	public String[] getReports() {

		String username = model.getUsername();
		if (username == null) {
			return guestViews;
		}
		ClientUserManager userManager = model.getUserManager();
		if (userManager.getCurrentUser().userrole == Role.MANAGER) {
			return managerViews;
		}
		if (userManager.getCurrentUser().userrole == Role.OWNER) {
			return ownerViews;
		}
		if (userManager.getCurrentUser().userrole == Role.WORKER) {
			return workerViews;
		}
		if (userManager.getCurrentUser().userrole == Role.SUPPORT) {
			return supportViews;
		}
		if (userManager.getCurrentUser().userrole == Role.DELIVERY) {
			return deliveryManViews;
		}
		if (userManager.getCurrentUser().userrole == Role.SERVICE_EXPERT) {
			return serviceExpertViews;
		}
		return customerViews;
	}
}
