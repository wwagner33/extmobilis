package com.paulo.android.solarmobile.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.ContentValues;
import android.util.Log;

public class ParseJSON {

	private static final int PARSE_TOKEN_ID = 221;
	private static final int PARSE_COURSES_ID = 222;
	private static final int PARSE_CLASSES_ID = 223;
	private static final int PARSE_TOPICS_ID = 224;
	private static final int PARSE_POSTS_ID = 225;

	Object object;
	ContentValues[] parsedValues;
	// JSONArray jsonArray;
	JSONObject jsonObjects[];

	public void parseData(ContentValues[] container, String data, int position) {
		
		String year = data.substring(0, 4);
		String month = data.substring(4, 6);
		String day = data.substring(6, 8);
		String hour = data.substring(8, 10);
		String minute = data.substring(10, 12);
		String second = data.substring(12, 14);

		container[position].put("postYear", Integer.parseInt(year));
		container[position].put("postMonth", Integer.parseInt(month));
		container[position].put("postDay", Integer.parseInt(day));
		container[position].put("postDayString", day);
		container[position].put("postHour", hour);
		container[position].put("postMinute", minute);
		container[position].put("postSecond", second);
		
		Log.w("ANO", year);
		Log.w("Mês", month);
		Log.w("dia", day);
		Log.w("hora", hour);
		Log.w("minuto", minute);
		Log.w("segundo", second);

	}

	public ContentValues[] parseJSON(String source, int parseId) {

		if (parseId == PARSE_TOKEN_ID) {

			Object object = JSONValue.parse(source);

			JSONObject globalJSON = (JSONObject) object;

			JSONObject sessionsJSON = (JSONObject) globalJSON.get("session");

			String tokenString = (String) sessionsJSON.get("auth_token");

			Log.w("TokenString", tokenString);

			parsedValues = new ContentValues[1];
			parsedValues[0] = new ContentValues();
			parsedValues[0].put("token", tokenString);

			return parsedValues;

		}

		if (parseId == PARSE_COURSES_ID) {

			Log.w("InsideParser", "TRUE");

			Object object = JSONValue.parse(source);
			JSONArray jsonArray = (JSONArray) object;
			JSONObject jsonObjects[] = new JSONObject[jsonArray.size()];
			parsedValues = new ContentValues[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				jsonObjects[i] = (JSONObject) jsonArray.get(i);
				Log.w("Object", jsonObjects[i].toJSONString());

				parsedValues[i] = new ContentValues();

				parsedValues[i].put("group_id",
						(String) jsonObjects[i].get("group_id"));

				// Log.w("group_id",)

				parsedValues[i].put("offer_id",
						(String) jsonObjects[i].get("offer_id"));

				parsedValues[i].put("curriculum_unit_id",
						(String) jsonObjects[i].get("curriculum_unit_id"));

				parsedValues[i].put("semester",
						(String) jsonObjects[i].get("semester"));

				parsedValues[i].put("allocation_tag_id",
						(String) jsonObjects[i].get("allocation_tag_id"));

				parsedValues[i]
						.put("name", (String) jsonObjects[i].get("name"));

				// parsedValues[i].put("isSelect", false);
			}

			return parsedValues;
		}

		if (parseId == PARSE_CLASSES_ID) {
			Log.w("InsideParser", "TRUE");

			Object object = JSONValue.parse(source);
			JSONArray jsonArray = (JSONArray) object;
			JSONObject jsonObjects[] = new JSONObject[jsonArray.size()];
			parsedValues = new ContentValues[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				jsonObjects[i] = (JSONObject) jsonArray.get(i);
				Log.w("Object", jsonObjects[i].toJSONString());

				parsedValues[i] = new ContentValues();

				parsedValues[i].put("id", (String) jsonObjects[i].get("id"));

				parsedValues[i]
						.put("code", (String) jsonObjects[i].get("code"));

				parsedValues[i].put("semester",
						(String) jsonObjects[i].get("semester"));

			}

			return parsedValues;

		}

		if (parseId == PARSE_TOPICS_ID) {
			Log.w("InsideParser", "TRUE");

			Object object = JSONValue.parse(source);
			JSONArray jsonArray = (JSONArray) object;
			JSONObject jsonObjects[] = new JSONObject[jsonArray.size()];

			parsedValues = new ContentValues[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {

				jsonObjects[i] = (JSONObject) jsonArray.get(i);

				Log.w("Object", jsonObjects[i].toJSONString());

				JSONObject teste2 = (JSONObject) jsonObjects[i]
						.get("discussion");
				// Log.w("OBJECT 1", teste2.toJSONString());

				parsedValues[i] = new ContentValues();

				parsedValues[i].put("allocation_tag_id",
						(Long) teste2.get("allocation_tag_id"));

				parsedValues[i].put("description",
						(String) teste2.get("description"));

				parsedValues[i].put("id", (Long) teste2.get("id"));

				parsedValues[i].put("name", (String) teste2.get("name"));

				// Log.w("NAME", (String) jsonObjects[i].get("name"));

				parsedValues[i].put("schedule_id",
						(Long) teste2.get("schedule_id"));

			}
			return parsedValues;

		}

		if (parseId == PARSE_POSTS_ID) {
			Log.w("InsideParser", "TRUE");

			Object object = JSONValue.parse(source);
			JSONArray jsonArray = (JSONArray) object;
			JSONObject jsonObjects[] = new JSONObject[jsonArray.size()];

			parsedValues = new ContentValues[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {

				jsonObjects[i] = (JSONObject) jsonArray.get(i);

				Log.w("Object", jsonObjects[i].toJSONString());

				JSONObject teste2 = (JSONObject) jsonObjects[i]
						.get("discussion_post");
				Log.w("SINGLEOBJECT", teste2.toJSONString());

				parsedValues[i] = new ContentValues();

				parsedValues[i].put("content", (String) teste2.get("content"));

				parsedValues[i].put("created_at",
						(String) teste2.get("created_at"));

				parsedValues[i].put("discussion_id",
						(Long) teste2.get("discussion_id"));

				parsedValues[i].put("id", (Long) teste2.get("id"));
				Log.w("POST ID", String.valueOf(teste2.get("id")));

				parsedValues[i].putNull("parent_id");

				parsedValues[i].put("profile_id",
						(Long) teste2.get("profile_id"));

				parsedValues[i].put("updated_at",
						(String) teste2.get("updated_at"));

				parsedValues[i].put("user_id", (Long) teste2.get("user_id"));

				parsedValues[i].put("username",
						(String) teste2.get("user_username"));

				// parsedValues[i].put("postDate", (String)
				// teste2.get("updated"));

				parseData(parsedValues, (String) teste2.get("updated"), i);

			}

			return parsedValues;

		}

		return null;
	}

}
