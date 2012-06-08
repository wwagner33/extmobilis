package com.mobilis.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mobilis.model.DiscussionPost;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

public class ParseJSON {

	private ContentValues[] parsedValues;
	private ArrayList<ContentValues> parsedPostValues;
	private SharedPreferences settings;
	private ArrayList<DiscussionPost> discussionPosts; // TTS

	public ParseJSON(Context context) {
		settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

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

				parsedValues[i].put("_id", (String) jsonObjects[i].get("id"));

				parsedValues[i].put("offer_id",
						(String) jsonObjects[i].get("offer_id"));

				parsedValues[i].put("group_id",
						(String) jsonObjects[i].get("group_id"));

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

				parsedValues[i].put("_id", (Long) jsonObjects[i].get("id"));

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

				parsedValues[i].put("_id", (Long) jsonObjects[i].get("id"));

				parsedValues[i]
						.put("name", (String) jsonObjects[i].get("name"));

				parsedValues[i].put("schedule_id",
						(Long) jsonObjects[i].get("schedule_id"));

				parsedValues[i].put("status",
						(String) jsonObjects[i].get("status"));

				// parsedValues[i].put("has_new_posts", false);

				SimpleDateFormat dbFormat = DateUtils.getDbFormat();
				SimpleDateFormat serverFormat = DateUtils.getServerFormat();

				try {
					Date formatedDate = serverFormat
							.parse((String) jsonObjects[i]
									.get("last_post_date"));

					String formatedDateString = dbFormat.format(formatedDate);
					parsedValues[i].put("last_post_date", formatedDateString);
					Log.i("FORMATED DATE", formatedDateString);
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException();
				} catch (NullPointerException e) {
					parsedValues[i].putNull("last_post_date");
				}
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

		JSONObject local = (JSONObject) jsonArray.get(0); // Joga exception
		String after = (String) local.get("after");
		String before = (String) local.get("before");
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("after", after);
		editor.putString("before", before);

		if (jsonArray.size() == 1) {
			return parsedPostValues;
		}

		for (int i = 1; i < jsonArray.size(); i++) {

			jsonObjects[i] = (JSONObject) jsonArray.get(i);

			Log.w("Object", jsonObjects[i].toJSONString());

			ContentValues rowItem = new ContentValues();

			String content = (String) jsonObjects[i].get("content");

			if (!content.equals("")) {
				Spanned markUpFirst = Html.fromHtml(content);
				rowItem.put("content", markUpFirst.toString());
			} else {
				rowItem.put("content", "");
			}

			rowItem.put("user_nick", (String) jsonObjects[i].get("user_nick"));

			rowItem.put("discussion_id",
					(Long) jsonObjects[i].get("discussion_id"));
			rowItem.put("_id", (Long) jsonObjects[i].get("id"));

			if (jsonObjects[i].get("parent_id") != null) {
				rowItem.put("parent_id", (Long) jsonObjects[i].get("parent_id"));
			}

			rowItem.put("profile_id", (Long) jsonObjects[i].get("profile_id"));
			rowItem.put("user_id", (Long) jsonObjects[i].get("user_id"));
			rowItem.put("level", (Long) jsonObjects[i].get("level"));

			SimpleDateFormat dbFormat = DateUtils.getDbFormat();
			SimpleDateFormat serverFormat = DateUtils.getServerFormat();

			try {
				String dateFromServer = (String) jsonObjects[i]
						.get("updated_at");
				dateFromServer = dateFromServer.substring(0, 19).replace("T",
						"");
				Date formatedDate = serverFormat.parse(dateFromServer);
				String formatedDateString = dbFormat.format(formatedDate);
				rowItem.put("updated_at", formatedDateString);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

			parsedPostValues.add(rowItem);
		}

		return parsedPostValues;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONObject buildTokenObject(String login, String password) {
		JSONObject jsonObject = new JSONObject();
		LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
		jsonMap.put("login", login);
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

	// TTS

	public int[] parseBeforeAndAfter(String source) {
		int[] result = new int[2];
		Object object = JSONValue.parse(source);
		JSONArray jsonArray = (JSONArray) object;
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);

		Long before = (Long) jsonObject.get("before");
		Long after = (Long) jsonObject.get("after");
		result[0] = before.intValue();
		result[1] = after.intValue();

		return result;
	}

	private DiscussionPost parsePost(JSONObject jsonObject) {
		Log.w("Object", jsonObject.toJSONString());

		DiscussionPost discussionPost = new DiscussionPost();

		String content = (String) jsonObject.get("content");
		if (content != null) {
			Spanned markUpFirst = Html.fromHtml(content);
			content = markUpFirst.toString();
		}
		discussionPost.setContent(content);

		// String contentLast = (String) jsonObject.get("content_last");
		// if (contentLast != null) {
		// Spanned markUpLast = Html.fromHtml(contentLast);
		// contentLast = markUpLast.toString();
		// }
		// discussionPost.setContentLast(contentLast);

		long discussionId = (Long) jsonObject.get("discussion_id");
		discussionPost.setDiscussionId(discussionId);

		long _id = (Long) jsonObject.get("id");
		discussionPost.setId(_id);

		long parentId = 0;
		if (jsonObject.get("parent_id") != null) {
			parentId = (Long) jsonObject.get("parent_id");
		}
		discussionPost.setParentId(parentId);

		long userId = (Long) jsonObject.get("user_id");
		discussionPost.setUserId(userId);

		String userNick = (String) jsonObject.get("user_nick");
		discussionPost.setUserNick(userNick);

		try {
			// Formatar a data.
			String date = (String) jsonObject.get("updated_at");
			date = date.substring(0, 19).replace("T", "");

			SimpleDateFormat serverFormat = DateUtils.getServerFormat();
			Date formatedDate = serverFormat.parse(date);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(formatedDate);
			discussionPost.setDate(calendar);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

		return discussionPost;
	}

	public ArrayList<DiscussionPost> parsePostsTTS(String source) {

		JSONArray jsonPosts = (JSONArray) JSONValue.parse(source);

		discussionPosts = new ArrayList<DiscussionPost>();

		Log.i("JsonArray", String.valueOf(jsonPosts.size()));
		for (int i = 1; i < jsonPosts.size(); i++) {
			DiscussionPost discussionPost = parsePost((JSONObject) jsonPosts
					.get(i));
			discussionPosts.add(discussionPost);
		}
		return discussionPosts;
	}

	public ArrayList<DiscussionPost> parseInvertedPosts(String source) {

		JSONArray jsonPosts = (JSONArray) JSONValue.parse(source);

		discussionPosts = new ArrayList<DiscussionPost>();

		Log.i("JsonArray", String.valueOf(jsonPosts.size()));
		int i = jsonPosts.size() - 1;
		while (i > 0) {
			DiscussionPost discussionPost = parsePost((JSONObject) jsonPosts
					.get(i));
			discussionPosts.add(discussionPost);
			i--;
		}
		return discussionPosts;
	}

}
