package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Utilty methods to determine if lists are in sorted order.
 *
 */
public class SortUtilities {
	/**
	 * Determines if the list of dates is sorted in ascending order
	 *
	 * @param dates
	 *            {@link List} of dates in strong format based on the passed
	 *            format
	 * @param format
	 *            Expected string format of the dates
	 * @return true if in ascending order
	 */
	public static boolean isDateSortedAsc(List<String> dates, String format) {
		boolean sorted = true;
		Iterator<String> iList = dates.iterator();
		String first = iList.next();
		try {
			Date curr = new SimpleDateFormat(format, Locale.ENGLISH).parse(first);
			while (iList.hasNext()) {
				String next = iList.next();
				Date nextDate = new SimpleDateFormat(format, Locale.ENGLISH).parse(next);
				if (curr.compareTo(nextDate) > 0) {
					sorted = false;
					break;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sorted;
	}

	/**
	 * Determines if the list of dates is sorted in descending order
	 * 
	 * @param dates
	 *            {@link List} of dates in strong format based on the passed
	 *            format
	 * @param format
	 *            Expected string format of the dates
	 * @return true if in descending order
	 */
	public static boolean isDateSortedDesc(List<String> dates, String format) {
		boolean sorted = true;
		Iterator<String> iList = dates.iterator();
		String first = iList.next();
		try {
			Date curr = new SimpleDateFormat(format, Locale.ENGLISH).parse(first);
			while (iList.hasNext()) {
				String next = iList.next();
				Date nextDate = new SimpleDateFormat(format, Locale.ENGLISH).parse(next);
				if (curr.compareTo(nextDate) < 0) {
					sorted = false;
					break;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sorted;
	}

	/**
	 * Determines of the data in a list is sorted in ascending order
	 *
	 * @param list
	 *            List of strings
	 * @return Whether the list is sorted id ascending order
	 */
	public static boolean isStringListSortedAsc(List<String> list) {
		boolean sorted = true;
		Iterator<String> iList = list.iterator();
		String curr = iList.next();
		while (iList.hasNext()) {
			String next = iList.next();
			if (curr.compareTo(next) > 0) {
				sorted = false;
				break;
			}
			curr = next;
		}
		return sorted;
	}

	/**
	 * Determines of the data in a list is sorted in descending order
	 * 
	 * @param list
	 *            List of strings
	 * @return Whether the list is sorted id descending order
	 */
	public static boolean isStringListSortedDesc(List<String> list) {
		boolean sorted = true;
		Iterator<String> iList = list.iterator();
		String curr = iList.next();
		while (iList.hasNext()) {
			String next = iList.next();
			if (curr.compareTo(next) < 0) {
				sorted = false;
				break;
			}
			curr = next;
		}
		return sorted;
	}
}
