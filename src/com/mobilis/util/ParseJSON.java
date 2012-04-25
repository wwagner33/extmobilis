package com.mobilis.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mobilis.controller.Constants;

import android.content.ContentValues;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

public class ParseJSON {

	private ContentValues[] parsedValues;
	private ArrayList<ContentValues> parsedPostValues;

	public void parseDate(ContentValues container, String data, int position) {

		String year = data.substring(0, 4);
		String month = data.substring(4, 6);
		String day = data.substring(6, 8);
		String hour = data.substring(8, 10);
		String minute = data.substring(10, 12);
		String second = data.substring(12, 14);

		container.put("post_year", Integer.parseInt(year));
		container.put("post_month", Integer.parseInt(month));
		container.put("post_day", Integer.parseInt(day));
		container.put("post_hour", Integer.parseInt(hour));
		container.put("post_minute", Integer.parseInt(minute));
		container.put("post_second", Integer.parseInt(second));

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

				parsedValues[i].put("_id",
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

				parsedValues[i].put("_id", (String) jsonObjects[i].get("id"));

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
			Log.i("jsonArray", String.valueOf(jsonArray.size()));

			for (int i = 0; i < jsonArray.size(); i++) {

				jsonObjects[i] = (JSONObject) jsonArray.get(i);

				parsedValues[i] = new ContentValues();

				parsedValues[i].put("allocation_tag_id",
						(Long) jsonObjects[i].get("allocation_tag_id"));

				parsedValues[i].put("description",
						(String) jsonObjects[i].get("description"));

				parsedValues[i].put("_id",
						Long.parseLong((String) jsonObjects[i].get("id")));

				parsedValues[i]
						.put("name", (String) jsonObjects[i].get("name"));

				parsedValues[i].put("schedule_id",
						(Long) jsonObjects[i].get("schedule_id"));

				parsedValues[i].put("closed",
						(String) jsonObjects[i].get("closed"));

				parsedValues[i].put("last_post_date",
						(String) jsonObjects[i].get("last_post_date"));

			}

			return parsedValues;

		}

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

		return null;
	}

	public ArrayList<ContentValues> parsePosts(String source) {

		Object object = JSONValue.parse(source);
		JSONArray jsonArray = (JSONArray) object;
		JSONObject jsonObjects[] = new JSONObject[jsonArray.size()];

		parsedPostValues = new ArrayList<ContentValues>();

		Log.i("JsonArray", String.valueOf(jsonArray.size()));

		for (int i = 0; i < jsonArray.size(); i++) {

			jsonObjects[i] = (JSONObject) jsonArray.get(i);

			Log.w("Object", jsonObjects[i].toJSONString());

			// JSONObject innerObject = (JSONObject) jsonObjects[i]
			// .get("discussion_post");

			ContentValues rowItem = new ContentValues();

			String contentFirst = (String) jsonObjects[i].get("content_first");
			String contentLast = (String) jsonObjects[i].get("content_last");

			if (contentFirst != null) {
				Spanned markUpFirst = Html.fromHtml(contentFirst);
				rowItem.put("content_first", markUpFirst.toString());
			} else {
				rowItem.put("content_first", "");
			}

			if (contentLast != null) {
				Spanned markUpLast = Html.fromHtml(contentLast);
				rowItem.put("content_last", markUpLast.toString());
			} else {
				rowItem.put("content_last", "");
			}

			rowItem.put("discussion_id", Long.parseLong((String) jsonObjects[i]
					.get("discussion_id")));
			rowItem.put("_id",
					Long.parseLong((String) jsonObjects[i].get("id")));

			if ((String) jsonObjects[i].get("parent_id") != null) {
				rowItem.put("parent_id", Long.parseLong((String) jsonObjects[i]
						.get("parent_id")));
			} else {
				rowItem.putNull("parent_id");
			}

			rowItem.put("user_id",
					Long.parseLong((String) jsonObjects[i].get("user_id")));
			rowItem.put("profile_id",
					Long.parseLong((String) jsonObjects[i].get("profile_id")));
			rowItem.put("user_nick", (String) jsonObjects[i].get("user_nick"));
			// rowItem.put("updated", (String) jsonObjects[i].get("updated"));

			SimpleDateFormat systemFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dbFormat = new SimpleDateFormat("yyyyMMddHHmmss");

			try {
				Date formatedDate = dbFormat.parse((String) jsonObjects[i]
						.get("updated"));

				String formatedDateString = systemFormat.format(formatedDate);
				rowItem.put("updated", formatedDateString);
				Log.i("FORMATED DATE", formatedDateString);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

			// parseDate(rowItem, (String) jsonObjects[i].get("updated"), i);
			parsedPostValues.add(rowItem);
		}

		return parsedPostValues;
	}

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
