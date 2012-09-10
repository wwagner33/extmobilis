package com.mobilis.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.text.Html;
import android.text.Spanned;

import com.mobilis.model.Class;
import com.mobilis.model.Course;
import com.mobilis.model.Discussion;
import com.mobilis.model.Post;

public class ParseJSON {

	public ArrayList<? extends Object> parseJSON(String content, int parseId) {

		Object object = JSONValue.parse(content);
		JSONArray jsonArray = null;
		JSONObject jsonObjects[] = null;
		if (object instanceof JSONArray) {
			jsonArray = (JSONArray) object;
			jsonObjects = new JSONObject[jsonArray.size()];
		}

		switch (parseId) {

		case Constants.PARSE_TOKEN_ID:
			JSONObject globalJSON = (JSONObject) object;
			JSONObject sessionsJSON = (JSONObject) globalJSON.get("session");
			String tokenString = (String) sessionsJSON.get("auth_token");
			ArrayList<String> tokenHolder = new ArrayList<String>();
			tokenHolder.add(tokenString);
			return tokenHolder;

		case Constants.PARSE_COURSES_ID:
			ArrayList<Course> courses = new ArrayList<Course>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonObjects[i] = (JSONObject) jsonArray.get(i);
				Course course = new Course();
				course.set_id(((Long) jsonObjects[i].get("id")).intValue());
				course.setAllocationTagId(Integer
						.parseInt((String) jsonObjects[i]
								.get("allocation_tag_id")));
				course.setName((String) jsonObjects[i].get("name"));
				courses.add(course);
			}
			return courses;

		case Constants.PARSE_CLASSES_ID:

			ArrayList<Class> classes = new ArrayList<Class>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonObjects[i] = (JSONObject) jsonArray.get(i);

				Class mClass = new Class();

				mClass.set_id(((Long) jsonObjects[i].get("id")).intValue());
				mClass.setCode((String) jsonObjects[i].get("code"));
				mClass.setSemester((String) jsonObjects[i].get("semester"));
				classes.add(mClass);
			}
			return classes;

		case Constants.PARSE_TOPICS_ID:

			ArrayList<Discussion> discussions = new ArrayList<Discussion>();

			for (int i = 0; i < jsonArray.size(); i++) {

				jsonObjects[i] = (JSONObject) jsonArray.get(i);

				Discussion discussion = new Discussion();

				discussion.setId(((Long) jsonObjects[i].get("id")).intValue());
				discussion.setDescription((String) jsonObjects[i]
						.get("description"));
				discussion.setName((String) jsonObjects[i].get("name"));
				discussion.setStatus(Integer.parseInt((String) jsonObjects[i]
						.get("status")));

				SimpleDateFormat dbFormat = DateUtils.getDbFormat();
				SimpleDateFormat serverFormat = DateUtils.getServerFormat();

				try {
					Date lastPostDate = serverFormat
							.parse((String) jsonObjects[i]
									.get("last_post_date"));
					String lastPostDateFormatted = dbFormat
							.format(lastPostDate);
					discussion.setLastPostDate(lastPostDateFormatted);

					Date startDate = serverFormat.parse((String) jsonObjects[i]
							.get("start_date"));
					discussion.setStartDate(startDate);

					Date endDate = serverFormat.parse((String) jsonObjects[i]
							.get("end_date"));
					discussion.setEndDate(endDate);

				} catch (ParseException e) {
					throw new RuntimeException();
				} catch (NullPointerException e) {
				}
				discussions.add(discussion);
			}
			return discussions;

		case Constants.PARSE_TEXT_RESPONSE_ID:

			JSONObject jsonObject = (JSONObject) object;
			ArrayList<Integer> result = new ArrayList<Integer>();
			result.add(((Long) jsonObject.get("result")).intValue());
			result.add(((Long) jsonObject.get("post_id")).intValue());
			return result;

		case Constants.PARSE_POSTS_ID:
			ArrayList<Post> parsedPosts = new ArrayList<Post>();
			for (int i = 1; i < jsonArray.size(); i++) {

				jsonObjects[i] = (JSONObject) jsonArray.get(i);

				Post discussionPost = new Post();
				String data = (String) jsonObjects[i].get("content");

				if (!data.equals("")) {
					data = removeBlankSpace(data);
					Spanned markUpFirst = Html.fromHtml(data);
					discussionPost.setContent(markUpFirst.toString());
				}

				else {
					discussionPost.setContent("");
				}

				long discussionId = (Long) jsonObjects[i].get("discussion_id");
				discussionPost.setDiscussionId((int) discussionId);

				long _id = (Long) jsonObjects[i].get("id");
				discussionPost.setId((int) _id);

				long parentId = 0;
				if (jsonObjects[i].get("parent_id") != null) {
					parentId = (Long) jsonObjects[i].get("parent_id");
				}
				discussionPost.setParentId((int) parentId);

				long userId = (Long) jsonObjects[i].get("user_id");
				discussionPost.setUserId((int) userId);

				String userNick = (String) jsonObjects[i].get("user_nick");
				discussionPost.setUserNick(userNick);

				try {
					String date = (String) jsonObjects[i].get("updated_at");
					date = date.substring(0, 19).replace("T", "");
					SimpleDateFormat serverFormat = DateUtils.getServerFormat();
					Date formatedDate = serverFormat.parse(date);
					discussionPost.setDate(formatedDate);
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException();
				}
				parsedPosts.add(discussionPost);
			}
			return parsedPosts;

		default:
			return null;
		}
	}

	private String removeBlankSpace(String text) {
		return text.replaceAll("\\r\\n\\t|\\r|\\n|\\t", " ");
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

	@SuppressWarnings("unchecked")
	public JSONObject buildTextResponseWithoutParent(String content) {
		JSONObject responseJSON = new JSONObject();
		LinkedHashMap<String, String> jsonMap = new LinkedHashMap<String, String>();
		jsonMap.put("content", content);
		jsonMap.put("parent_id", Constants.noParentString);
		responseJSON.put("discussion_post", jsonMap);
		return responseJSON;
	}

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
}
