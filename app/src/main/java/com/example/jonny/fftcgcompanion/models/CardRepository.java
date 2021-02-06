package com.example.jonny.fftcgcompanion.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;
import com.example.jonny.fftcgcompanion.utils.GeneralUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class CardRepository
{
    private static final String s_jsonFileName = "json/cards.json";
    private static final String s_jsonOpus8FileName = "json/opus-VIII.json";
    private static final String s_collectionFileName = "collection.txt";
    private static final String s_decksFileName = "decks.json";

    private static Map<String, Card> m_cards = new HashMap<>();
    private static Map<String, Deck> m_decks = new HashMap<>();

    public static List<Card> getCardList(boolean sorted)
    {
        List<Card> cards = new ArrayList<Card>(m_cards.values());

        if (sorted)
        {
            Collections.sort(cards);
        }

        return cards;
    }

    public CardRepository()
    {
        // Load all cards info in from JSON
        loadAllCards();

        // Try loading in any collections
        loadAllCardCollections();

        // Try finding any saved decks
        loadAllDecks();
    }

    public static Card getCard(String id)
    {
        if (m_cards.containsKey(id))
        {
            return m_cards.get(id);
        }
        return null;
    }

    public static List<Deck> getDecks()
    {
        return new ArrayList<>(m_decks.values());
    }

    private void loadAllCards()
    {
        m_cards.clear();

        loadCardsFromJSONFile(s_jsonFileName);      // Opus 1-7 cards
        loadCardsFromJSONFile(s_jsonOpus8FileName); // Opus 8 cards
    }

    private void loadCardsFromJSONFile(String cardJSONFile)
    {
        String json = null;
        try
        {
            InputStream inputStream = FFTCGCompanionApplication.getAppContext().getAssets().open(cardJSONFile);
            int size = inputStream.available();
            byte[] fileBuffer = new byte[size];
            inputStream.read(fileBuffer);
            inputStream.close();
            json = new String(fileBuffer, "UTF-8");
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (json != null)
        {
            try
            {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("cards");

                for (int i = 0; i < jsonArray.length(); ++i)
                {
                    Card newCard = new Card(jsonArray.getJSONObject(i));
                    m_cards.put(newCard.getId(), newCard);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void loadAllCardCollections()
    {
        String cardCollection = loadCardCollection();
        if (cardCollection != null && !cardCollection.isEmpty())
        {
            String[] savedCards = cardCollection.split("[,]");
            for (int i = 0; i < savedCards.length; ++i)
            {
                String[] cardTokens = savedCards[i].split("[:]");
                if (cardTokens.length == 2)
                {
                    Card card = m_cards.get(cardTokens[0]);
                    if (card != null)
                    {
                        card.setCount(Integer.parseInt(cardTokens[1]));
                    }
                }
            }
        }
    }

    private String loadCardCollection()
    {
        String collectionFileContents = null;
        try
        {
            FileInputStream inputStream = FFTCGCompanionApplication.getAppContext().openFileInput(s_collectionFileName);
            int size = inputStream.available();
            byte[] fileBuffer = new byte[size];
            inputStream.read(fileBuffer);
            inputStream.close();
            collectionFileContents = new String(fileBuffer, "UTF-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return collectionFileContents;
    }

    public void saveCardCollection()
    {
        FileOutputStream outputStream;

        String collectionString = "";
        for (Map.Entry<String, Card> entry : m_cards.entrySet())
        {
            if (entry.getValue().isOwned())
            {
                collectionString += entry.getValue().getId() + ":" + entry.getValue().getCount() + ",";
            }
        }

        if (!collectionString.isEmpty())
        {
            // Remove last ',' char
            collectionString = collectionString.substring(0, collectionString.length() - 1);

            try
            {
                outputStream = FFTCGCompanionApplication.getAppContext().openFileOutput(s_collectionFileName, Context.MODE_PRIVATE);
                outputStream.write(collectionString.getBytes());
                outputStream.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean loadAllDecks()
    {
        if (!m_decks.isEmpty())
            return true;

        String deckFileContents = "";
        try
        {
            FileInputStream inputStream = FFTCGCompanionApplication.getAppContext().openFileInput(s_decksFileName);
            int size = inputStream.available();
            byte[] fileBuffer = new byte[size];
            inputStream.read(fileBuffer);
            inputStream.close();
            deckFileContents = new String(fileBuffer, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        JSONObject jsonObj = null;
        try
        {
            jsonObj = new JSONObject(deckFileContents);
        }
        catch (JSONException e)
        {
            Log.e("CardRepository", e.getMessage());
        }

        try
        {
            Iterator deckIterator = jsonObj.keys();
            while(deckIterator.hasNext())
            {
                String deckKey = deckIterator.next().toString();
                JSONObject deckObj = (JSONObject)jsonObj.get(deckKey);
                Deck deck = new Deck(deckObj);
                m_decks.put(deck.getName(), deck);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        return true;
    }

    public static Deck getDeck(String deckName)
    {
        if (m_decks.isEmpty())
            loadAllDecks();

        if (!m_decks.containsKey(deckName))
            return null;

        return m_decks.get(deckName);
    }

    public static boolean saveDeck(Deck deck)
    {
        // Check if a deck with the same name exists
        if (m_decks.containsKey(deck.getName()))
        {
            return false;
        }

        // Read in current decks and remove if already exists
        if (!m_decks.isEmpty())
        {
            if (m_decks.containsKey(deck.getName()))
            {
                m_decks.remove(deck.getName());
            }
        }

        m_decks.put(deck.getName(), deck);

        FileOutputStream outputStream;
        try
        {
            outputStream = FFTCGCompanionApplication.getAppContext().openFileOutput(s_decksFileName, Context.MODE_PRIVATE);
            outputStream.write(deck.convertToJSON().toString().getBytes());
            outputStream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean deleteDeck(Deck deck)
    {
        // Remove if  exists
        if (m_decks.containsKey(deck.getName()))
        {
            m_decks.remove(deck.getName());
            return true;
        }

        return false;
    }

    public void exportAsCSV(String filename)
    {
        if (!GeneralUtils.isExternalStorageWritable())
            return;

        String fileContents = getCollectionAsText();
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "");
        if (!dir.exists())
            dir.mkdirs();

        File exportFile = new File(dir, filename + ".csv");
        if (exportFile.exists())
            exportFile.delete();

        try
        {
            exportFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(exportFile);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void importCollectionFromCSV(Context context, Uri uri)
    {
        if (!GeneralUtils.isExternalStorageReadable())
            return;

        String fileContents = "";
        try
        {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            int size = inputStream.available();
            byte[] fileBuffer = new byte[size];
            inputStream.read(fileBuffer);
            inputStream.close();
            fileContents = new String(fileBuffer, "UTF-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Map<Card, Integer> importedCards = new HashMap<Card, Integer>();
        final String[] importedCardSplit = fileContents.split("\n");
        for (String importedCardStr : importedCardSplit)
        {
            String[] cardSplit = importedCardStr.split(",");
            if (cardSplit.length == 2)
            {
                Card card = m_cards.get(cardSplit[0]);
                importedCards.put(m_cards.get(cardSplit[0]), Integer.parseInt(cardSplit[1]));

                card.setCount(card.getCount() + Integer.parseInt(cardSplit[1]));
            }
        }
        //TODO: If the user only wishes to add certain cards from import... Return map and display activity for selecting which to add?
    }

    private String getCollectionAsText()
    {
        String returnString = "";
        for (Map.Entry<String, Card> entry : m_cards.entrySet())
        {
            if (entry.getValue().isOwned())
            {
                returnString += entry.getValue().getId() + "," + entry.getValue().getCount() + "\n";
            }
        }
        return returnString;
    }

    public boolean exportDeckAsXML(Context context, Deck deck)
    {
        if (!GeneralUtils.isExternalStorageWritable())
            return false;

        Document doc = null;
        try
        {
            doc = constructDeckXML(deck);
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
            return false;
        }

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "");
        if (!dir.exists())
            dir.mkdirs();

        File exportFile = new File(dir, deck.getName() + ".xml");
        if (exportFile.exists())
            exportFile.delete();

        try
        {
            exportFile.createNewFile();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(exportFile);
            transformer.transform(source, result);

            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        Toast.makeText(context, "Exported '" + deck.getName() + "' successfully to '" + exportFile.getPath() + "'", Toast.LENGTH_LONG).show();
        return true;
    }

    public void clearCardCollection()
    {
        FileOutputStream outputStream;

        for (Map.Entry<String, Card> cardEntry : m_cards.entrySet())
        {
            cardEntry.getValue().clearCount();
        }

        String collectionString = "";
        try
        {
            outputStream = FFTCGCompanionApplication.getAppContext().openFileOutput(s_collectionFileName, Context.MODE_PRIVATE);
            outputStream.write(collectionString.getBytes());
            outputStream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clearCachedCards(View view)
    {
        Context appContext = FFTCGCompanionApplication.getAppContext();
        for (Map.Entry<String, Card> cardEntry : m_cards.entrySet())
        {
            File artworkFile = new File(appContext.getFilesDir(), cardEntry.getValue().getId() + ".jpg");
            if (artworkFile.exists())
            {
                artworkFile.delete();
            }
        }
        Toast.makeText(view.getContext(), "Successfully removed all cached cards!", Toast.LENGTH_LONG).show();
    }



    private Document constructDeckXML(Deck deck) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbFact.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        // root elem
        Element rootElement = doc.createElement("deck");
        doc.appendChild(rootElement);

        // name elem
        Element nameElement = doc.createElement("name");
        nameElement.setNodeValue(deck.getName());
        rootElement.appendChild(nameElement);

        // notes elem
        Element notesElement = doc.createElement("notes");
        notesElement.setNodeValue(deck.getNotes());
        rootElement.appendChild(notesElement);

        // cards elem
        Element cardsElement = doc.createElement("cards");
        rootElement.appendChild(cardsElement);

        // card elems
        Map<String, Integer> cards = deck.getCards();
        Iterator<Map.Entry<String, Integer>> cardIter = cards.entrySet().iterator();
        while (cardIter.hasNext())
        {
            Map.Entry<String, Integer> cardEntry = cardIter.next();
            Element cardElement = doc.createElement("card");
            cardElement.setAttribute("id", cardEntry.getKey());
            cardElement.setAttribute("quantity", cardEntry.getValue().toString());
            cardsElement.appendChild(cardElement);
        }
        return doc;
    }


    public void importDeckFromXML(Context context, Uri data)
    {
        if (!GeneralUtils.isExternalStorageReadable())
            return;

        String fileContents = "";
        try
        {
            InputStream inputStream = context.getContentResolver().openInputStream(data);
            int size = inputStream.available();
            byte[] fileBuffer = new byte[size];
            inputStream.read(fileBuffer);
            inputStream.close();
            fileContents = new String(fileBuffer, "UTF-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        int i =0;
    }
}
