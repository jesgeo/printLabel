package applet;

public class Address {
	String name, address1, address2, town, county, postcode, country;

	public boolean validate() {
		if (name == null || name.length() < 3)
			return false;
		if (postcode == null)
			return false;
		if (address1 == null && address2 == null)
			return false;
		return true;
	}
}
