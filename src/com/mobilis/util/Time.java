package com.mobilis.util;

public class Time {

	public static String getMonthAsText(int postMonth) {

		if (postMonth == 1)
			return "Jan";
		if (postMonth == 2)

			return "Fev";
		if (postMonth == 3)

			return "Mar";
		if (postMonth == 4)

			return "Abr";
		if (postMonth == 5)

			return "Mai";
		if (postMonth == 6)

			return "Jun";
		if (postMonth == 7)

			return "Jul";
		if (postMonth == 8)

			return "Ago";
		if (postMonth == 9)

			return "Set";
		if (postMonth == 10)

			return "Out";
		if (postMonth == 11)

			return "Nov";
		if (postMonth == 12)

			return "Dez";

		return "?";

	}

}
