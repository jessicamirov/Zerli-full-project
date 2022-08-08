package common.request_data;
/**
 *Shop  enum class savingInformation about the stores 
 *there is 2 stores in our system-Nahariya,Haifa
 *this class also saves  the permissions that can be made to the store
 * a manger- have permission to his store and a owner to all shops
 *long method descripton
*@author Jessica, Yarden
 */
public enum Shop {
	/* User role Manager gets a shop, Owner gets all shops*/
	ALL("All"),
	NONE("None"),
	HAIFA("Haifa"),
	NAHARIYA("Nahariya");
	
	
	private String s;

	Shop(String s) {
		this.s = s;
	}

	public String toString() {
		return s;
	}

	
    public static Shop fromString(String text) {
        for (Shop b : Shop.values()) {
            if (b.s.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

}