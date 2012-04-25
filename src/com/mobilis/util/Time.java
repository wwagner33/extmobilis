package com.mobilis.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time {

	public static String getMonthAsText(int postMonth) {

		postMonth++;

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

	public static String getCurrentDate() {
		StringBuilder builder = new StringBuilder();
		Calendar calendar = Calendar.getInstance();

		String year;
		String month;
		String day;

		year = String.valueOf(calendar.get(Calendar.YEAR));
		month = String.valueOf(calendar.get(Calendar.MONTH));
		day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

		builder.append(year);
		builder.append("/");
		builder.append(month);
		builder.append("/");
		builder.append(day);

		return builder.toString();
	}

	public static SimpleDateFormat getDbFormat() {
		SimpleDateFormat serverFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return serverFormat;
	}

	public static SimpleDateFormat getServerFormat() {
		SimpleDateFormat serverFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return serverFormat;
	}
}
