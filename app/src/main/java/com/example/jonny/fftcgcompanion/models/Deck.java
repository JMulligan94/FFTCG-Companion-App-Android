package com.example.jonny.fftcgcompanion.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Deck implements Serializable
{
	private String m_name;
	private String m_notes;
	private Map<String, Integer> m_cards;
	private String m_date;

	public Deck(String name, String notes, Map<String, Integer> cards, String date)
	{
		m_name = name;
		m_notes = notes;
		m_cards = cards;
		m_date = date;
	}

	public Deck(JSONObject deckObj) throws JSONException
	{
		m_name = deckObj.getString("name");
		m_notes = deckObj.getString("notes");
		m_date = deckObj.getString("date");
		m_cards = new HashMap<>();

		JSONArray cardsArray = deckObj.getJSONArray("cards");
		for (int i = 0; i < cardsArray.length(); ++i)
		{
			JSONObject cardObject = cardsArray.getJSONObject(i);
			String cardKey = cardObject.keys().next();
			int cardQuantity = cardObject.getInt(cardKey);

			m_cards.put(cardKey, cardQuantity);
		}
	}

	public String getName()
	{
		return m_name;
	}

	public String getNotes()
	{
		return m_notes;
	}

	public String getDate()
	{
		return m_date;
	}

	public JSONObject convertToJSON()
	{
		JSONObject jsonObj = new JSONObject();

		try
		{
			jsonObj.put("name", m_name);
			jsonObj.put("notes", m_notes);
			jsonObj.put("date", m_date);

			JSONArray cardsArray = new JSONArray();
			Iterator<Map.Entry<String, Integer>> cardIter = m_cards.entrySet().iterator();
			while (cardIter.hasNext())
			{
				Map.Entry<String, Integer> mapElem = cardIter.next();
				JSONObject cardObj = new JSONObject();
				cardObj.put(mapElem.getKey(), mapElem.getValue().toString());
				cardsArray.put(cardObj);
			}
			jsonObj.put("cards", cardsArray);
		} catch (JSONException e)
		{
			Log.e("JSON", e.getMessage());
		}
		return jsonObj;
	}

	public Map<String, Integer> getCards()
	{
		return m_cards;
	}
}
