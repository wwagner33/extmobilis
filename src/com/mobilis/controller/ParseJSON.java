package com.mobilis.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.ContentValues;
import android.util.Log;

public class ParseJSON {

	private ContentValues[] parsedValues;

	private ArrayList<ContentValues> parsedPostValues;

	public void parseData(ContentValues container, String data,
			int position) {

		String year = data.substring(0, 4);
		String month = data.substring(4, 6);
		String day = data.substring(6, 8);
		String hour = data.substring(8, 10);
		String minute = data.substring(10, 12);
		String second = data.substring(12, 14);

		container.put("postYear", Integer.parseInt(year));
		container.put("postMonth", Integer.parseInt(month));
		container.put("postDay", Integer.parseInt(day));
		container.put("postDayString", day);
		container.put("postHour", hour);
		container.put("postMinute", minute);
		container.put("postSecond", second);

		Log.w("ANO", year);
		Log.w("Mês", month);
		Log.w("dia", day);
		Log.w("hora", hour);
		Log.w("minuto", minute);
		Log.w("segundo", second);

	}

	public ContentValues[] parseJSON(String source, int parseId) {

		if (parseId == Constants.PARSE_TOKEN_ID) {

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

		if (parseId == Constants.PARSE_COURSES_ID) {

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

			}

			return parsedValues;
		}

		if (parseId == Constants.PARSE_CLASSES_ID) {
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

		if (parseId == Constants.PARSE_TOPICS_ID) {
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

				parsedValues[i] = new ContentValues();

				parsedValues[i].put("allocation_tag_id",
						(Long) teste2.get("allocation_tag_id"));

				parsedValues[i].put("description",
						(String) teste2.get("description"));

				parsedValues[i].put("id", (Long) teste2.get("id"));

				parsedValues[i].put("name", (String) teste2.get("name"));

				parsedValues[i].put("schedule_id",
						(Long) teste2.get("schedule_id"));

				parsedValues[i].put("isClosed", (String) teste2.get("closed"));

			}
			return parsedValues;

		}

		/*
		 * if (parseId == Constants.PARSE_POSTS_ID) { Log.w("InsideParser",
		 * "TRUE");
		 * 
		 * Object object = JSONValue.parse(source); JSONArray jsonArray =
		 * (JSONArray) object; JSONObject jsonObjects[] = new
		 * JSONObject[jsonArray.size()];
		 * 
		 * parsedValues = new ContentValues[jsonArray.size()];
		 * 
		 * for (int i = 0; i < jsonArray.size(); i++) {
		 * 
		 * jsonObjects[i] = (JSONObject) jsonArray.get(i);
		 * 
		 * Log.w("Object", jsonObjects[i].toJSONString());
		 * 
		 * JSONObject teste2 = (JSONObject) jsonObjects[i]
		 * .get("discussion_post"); Log.w("SINGLEOBJECT",
		 * teste2.toJSONString());
		 * 
		 * parsedValues[i] = new ContentValues();
		 * 
		 * parsedValues[i].put("content", (String) teste2.get("content"));
		 * 
		 * parsedValues[i].put("created_at", (String) teste2.get("created_at"));
		 * 
		 * parsedValues[i].put("discussion_id", (Long)
		 * teste2.get("discussion_id"));
		 * 
		 * parsedValues[i].put("id", (Long) teste2.get("id")); Log.w("POST ID",
		 * String.valueOf(teste2.get("id")));
		 * 
		 * parsedValues[i].putNull("parent_id");
		 * 
		 * parsedValues[i].put("profile_id", (Long) teste2.get("profile_id"));
		 * 
		 * parsedValues[i].put("updated_at", (String) teste2.get("updated_at"));
		 * 
		 * parsedValues[i].put("user_id", (Long) teste2.get("user_id"));
		 * 
		 * parsedValues[i].put("username", (String)
		 * teste2.get("user_username"));
		 * 
		 * parseData(parsedValues, (String) teste2.get("updated"), i);
		 * 
		 * }
		 * 
		 * return parsedValues;
		 * 
		 * }
		 */

		if (parseId == Constants.PARSE_TEXT_RESPONSE_ID) {
			Object object = JSONValue.parse(source);
			JSONObject jsonObject = (JSONObject) object;
			Log.w("OBJECT SIZE", String.valueOf(jsonObject.size()));

			parsedValues = new ContentValues[1];
			parsedValues[0] = new ContentValues();
			parsedValues[0].put("result", (Long) jsonObject.get("result"));
			Log.w("RESULTONPARSER",
					String.valueOf(parsedValues[0].getAsInteger("result")));

			parsedValues[0].put("post_id", (Long) jsonObject.get("post_id"));
			Log.w("POSTIDONPARSER",
					String.valueOf(parsedValues[0].getAsInteger("post_id")));
			return parsedValues;
		}

		/*
		 * if (parseId == Constants.PARSE_NEW_POSTS_ID) { Log.w("InsideParser",
		 * "TRUE"); Log.w("source", source); Object object =
		 * JSONValue.parse(source); JSONArray jsonArray = (JSONArray) object;
		 * JSONObject jsonObjects[] = new JSONObject[jsonArray.size()];
		 * 
		 * // parsedValues = new ArrayList<ContentValues>(jsonArray.size());
		 * parsedValues = new ArrayList<ContentValues>();
		 * 
		 * for (int i = 0; i < jsonArray.size(); i++) {
		 * 
		 * jsonObjects[i] = (JSONObject) jsonArray.get(i);
		 * 
		 * Log.w("Object", jsonObjects[i].toJSONString());
		 * 
		 * JSONObject teste2 = (JSONObject) jsonObjects[i]
		 * .get("discussion_post"); Log.w("SINGLEOBJECT",
		 * teste2.toJSONString());
		 * 
		 * ContentValues rowItem = new ContentValues();
		 * 
		 * rowItem.put("content_first", (String) teste2.get("content_first"));
		 * rowItem.put("content_last", (String) teste2.get("content_last"));
		 * rowItem.put("discussion_id", (Long) teste2.get("discussion_id"));
		 * rowItem.put("_id", (Long) teste2.get("id"));
		 * rowItem.putNull("parent_id"); rowItem.put("profile_id", (Long)
		 * teste2.get("profile_id")); rowItem.put("user_name", (String)
		 * teste2.get("user_name")); rowItem.put("user_username", (String)
		 * teste2.get("user_username"));
		 * 
		 * rowItem.put("updated", (String) teste2.get("updated"));
		 * parseData(parsedValues, (String) teste2.get("updated"), i);
		 * parsedValues.add(rowItem);
		 * 
		 * }
		 * 
		 * return parsedValues;
		 * 
		 * }
		 */

		return null;
	}

	public ArrayList<ContentValues> parsePosts(String source) {

		Log.w("InsideParser", "TRUE");
		Log.w("source", source);
		Object object = JSONValue.parse(source);
		JSONArray jsonArray = (JSONArray) object;
		JSONObject jsonObjects[] = new JSONObject[jsonArray.size()];

		// parsedValues = new ArrayList<ContentValues>(jsonArray.size());
		parsedPostValues = new ArrayList<ContentValues>();

		for (int i = 0; i < jsonArray.size(); i++) {

			jsonObjects[i] = (JSONObject) jsonArray.get(i);

			Log.w("Object", jsonObjects[i].toJSONString());

			JSONObject teste2 = (JSONObject) jsonObjects[i]
					.get("discussion_post");
			Log.w("SINGLEOBJECT", teste2.toJSONString());

			ContentValues rowItem = new ContentValues();

			rowItem.put("content_first", (String) teste2.get("content_first"));
			rowItem.put("content_last", (String) teste2.get("content_last"));
			rowItem.put("discussion_id", (Long) teste2.get("discussion_id"));
			rowItem.put("_id", (Long) teste2.get("id"));
			rowItem.putNull("parent_id");
			rowItem.put("profile_id", (Long) teste2.get("profile_id"));
			rowItem.put("user_name", (String) teste2.get("user_name"));
			rowItem.put("user_username", (String) teste2.get("user_username"));

			rowItem.put("updated", (String) teste2.get("updated"));
			parseData(rowItem, (String) teste2.get("updated"), i);
			parsedPostValues.add(rowItem);

		}

		return parsedPostValues;

	}

	// public boolean isResponseSuccesful() {
	// return true;
	// }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject buildTokenObject(String username, String password) {
		JSONObject jsonObject = new JSONObject();
		LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
		jsonMap.put("username", username);
		jsonMap.put("password", password);
		jsonObject.put("user", jsonMap);
		return jsonObject;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject buildTextResponseWithParentObject(String content,
			long parentId) {
		JSONObject responseJSON = new JSONObject();
		LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
		jsonMap.put("content", content);
		jsonMap.put("parent_id", parentId);
		responseJSON.put("discussion_post", jsonMap);
		return responseJSON;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject buildTextResponseWithoutParent(String content) {
		JSONObject responseJSON = new JSONObject();
		LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
		jsonMap.put("content", content);
		jsonMap.put("parent_id", Constants.noParentString);
		responseJSON.put("discussion_post", jsonMap);
		return responseJSON;
	}

}
