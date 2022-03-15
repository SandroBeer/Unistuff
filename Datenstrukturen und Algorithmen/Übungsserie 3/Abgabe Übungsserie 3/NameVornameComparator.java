import java.util.Comparator;

/**
 * File: NameVornameComparator.java
 *
 * Klasse zum Vergleichen zweier Objekte (Records) vom Typ StudentIn
 * bezueglich Name und Vorname.
 *
 */

public class NameVornameComparator implements Comparator<StudentIn> {
	
	/** Vergleicht Objekt a mit Objekt b und
	   *  liefert -1 (wenn a<b), 0 (wenn a=b) oder +1 (wenn a>b)
	   */
	public int compare(StudentIn a, StudentIn b) {
	    	String namea = a.getName() + " " + a.getVorname();
	    	String nameb = b.getName() + " " + b.getVorname();
	    	
	    	if (namea.compareToIgnoreCase(nameb) < 0) {
	    		return -1;
	    	}
	    	else if (namea.compareToIgnoreCase(nameb) == 0) {
	    		return 0;
	    	}
	    	else {
	    		return 1;
	    	}
	}
}