package common;

public enum RequestType {
	// List of requests supported by server
	/* Errors: */
	REQUEST_FAILED, // data is null - Something went wrong on server side
	FORBIDDEN, // data is null - User is not authorized

	/* General: */
	PING, // Checks is server is alive. Should return unmodified request.

	/* UserManager: */
	GET_USER, ADD_USER, APPROVE_USER, REMOVE_USER, GET_USERS,GET_INCOME_REPORT,GET_INCOME_REPORT_BC,CHANGE_STATUS, LOG_OFF_USER, LOGIN,GET_ORDER_REPORT,

	/* ProductManager */
	GET_CATEGORIES, GET_PRODUCTS, ADD_PRODUCT, REMOVE_PRODUCT, GET_PRODUCT, GET_ORDERS, GET_ORDER, ADD_ORDER, GET_REFUND_AMOUNT, GET_ALL_COMPLAINTS, GET_ANSWERS_SURVEY, GET_USER_OREDERS, UPDATE_ORDER, DELETE_ORDER, SET_DISCOUNT, TOFROM_CATALOGUE, UPDATE_WALLET, GET_USER_WALLET, CANEL_REFUND, SET_COMPLAINT, GET_ANALYSE_SURVEY,

	/* OrderManager */
	/* AccountManager */
	/* etc. */
}
