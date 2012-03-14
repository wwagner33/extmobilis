package com.mobilis.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.mobilis.model.DBAdapter;

public class ParseJSON {

	private Context context;
	private ContentValues[] parsedValues;
	private DBAdapter adapter;
	private ArrayList<ContentValues> parsedPostValues;

	public ParseJSON(Context context) {
		this.context = context;
		adapter = new DBAdapter(this.context);
	}

	public void parseData(ContentValues container, String data, int position) {

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

				JSONObject innerObject = (JSONObject) jsonObjects[i]
						.get("discussion");

				parsedValues[i] = new ContentValues();

				parsedValues[i].put("allocation_tag_id",
						(Long) innerObject.get("allocation_tag_id"));

				parsedValues[i].put("description",
						(String) innerObject.get("description"));

				parsedValues[i].put("id", (Long) innerObject.get("id"));

				parsedValues[i].put("name", (String) innerObject.get("name"));

				parsedValues[i].put("schedule_id",
						(Long) innerObject.get("schedule_id"));

				parsedValues[i].put("isClosed",
						(String) innerObject.get("closed"));

				parsedValues[i].put("last_post_date",
						(String) innerObject.get("last_post_date"));

				adapter.open();

				if (!adapter.topicExists((Long) innerObject.get("id"))) {
					ContentValues dbValues = new ContentValues();
					dbValues.putNull("_id");
					dbValues.put("topic_id", (Long) innerObject.get("id"));

					dbValues.put("name", (String) innerObject.get("name"));
					dbValues.put("oldestPost",
							(String) innerObject.get("last_post_date"));
					dbValues.putNull("posts");
					adapter.insertNewTopic(dbValues);

				}

				adapter.close();

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

		for (int i = 0; i < jsonArray.size(); i++) {

			jsonObjects[i] = (JSONObject) jsonArray.get(i);

			Log.w("Object", jsonObjects[i].toJSONString());

			JSONObject innerObject = (JSONObject) jsonObjects[i]
					.get("discussion_post");
			Log.w("SINGLEOBJECT", innerObject.toJSONString());

			ContentValues rowItem = new ContentValues();

			rowItem.put("content_first",
					(String) innerObject.get("content_first"));
			rowItem.put("content_last",
					(String) innerObject.get("content_last"));
			rowItem.put("discussion_id",
					(Long) innerObject.get("discussion_id"));
			rowItem.put("_id", (Long) innerObject.get("id"));
			rowItem.putNull("parent_id");
			rowItem.put("profile_id", (Long) innerObject.get("profile_id"));
			rowItem.put("user_name", (String) innerObject.get("user_name"));
			rowItem.put("user_username",
					(String) innerObject.get("user_username"));

			rowItem.put("updated", (String) innerObject.get("updated"));
			parseData(rowItem, (String) innerObject.get("updated"), i);
			parsedPostValues.add(rowItem);

			/*
			 * adapter.open(); if (!adapter.postExists((Long)
			 * innerObject.get("id"))) { ContentValues bankValues = new
			 * ContentValues(); bankValues.putNull("_id");
			 * bankValues.put("post_id", (Long) innerObject.get("id"));
			 * bankValues.put("topic_id", (Long)
			 * innerObject.get("discussion_id")); bankValues.put("posts",
			 * source); adapter.insertNewPost(bankValues);
			 * 
			 * // }
			 * 
			 * // adapter.close();
			 */
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
