package com.example.jonny.fftcgcompanion.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

public class Card implements Comparable
{
    // AsyncTask for Image Bitmap
    private class BitmapFromUrlAsync extends AsyncTask<String, Void, Bitmap>
    {
        private WeakReference<Context> m_contextRef;
        private WeakReference<Card> m_cardRef;

        public BitmapFromUrlAsync(Context context, Card card)
        {
            m_contextRef = new WeakReference<>(context);
            m_cardRef = new WeakReference<>(card);
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            return loadImageFromNetwork(m_contextRef.get(), params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            m_cardRef.get().onArtworkFromNetworkFinished(m_contextRef.get(), bitmap);
        }

        private Bitmap loadImageFromNetwork(Context context, String urlStr)
        {
            Bitmap bitmap = null;

            URL url = null;
            try
            {
                url = new URL(urlStr);
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
                return bitmap;
            }

            try
            {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    int i =0;

                }
                InputStream inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    public interface OnCardArtworkRetrievalListener
    {
        void OnCardArtworkRetrieved(Bitmap artwork);
    }

    public enum CardType
    {
        FORWARD,
        BACKUP,
        SUMMON,
        MONSTER
    }

    public enum CardElement
    {
        FIRE,
        ICE,
        WIND,
        LIGHTNING,
        EARTH,
        WATER,
        LIGHT,
        DARK
    }

    public enum CardOpus
    {
        OPUS_I,
        OPUS_II,
        OPUS_III,
        OPUS_IV,
        OPUS_V,
        OPUS_VI,
        OPUS_VII,
        OPUS_VIII
    }

    public enum CardOwned
    {
        CARD_OWNED,
        CARD_UNOWNED
    }

    private String m_id;
    private CardOpus m_opus;
    private int m_cost;
    private String m_name;
    private String m_description;
    private CardType m_type;
    private CardElement m_element;
    private String m_job;
    private String m_category;
    private int m_power;
    private String m_picUrl;

    private int m_count;

    private Bitmap m_artwork = null;

    private boolean m_retrievingArtwork = false;

    private HashSet<OnCardArtworkRetrievalListener> m_queuedListeners;

    public Card(JSONObject jsonObj)
    {
        try
        {
            m_id = jsonObj.getString("id");
            m_opus = CardOpus.values()[Integer.parseInt(jsonObj.getString("opus")) - 1];
            m_cost = Integer.parseInt(jsonObj.getString("cost"));
            m_name = jsonObj.getString("name");
            m_description = jsonObj.getString("description");
            m_type = CardType.values()[Integer.parseInt(jsonObj.getString("type"))];
            m_element = CardElement.values()[Integer.parseInt(jsonObj.getString("element"))];
            m_job = jsonObj.getString("job");
            m_category = jsonObj.getString("category");
            String powerString = jsonObj.getString("power");
            if (!powerString.isEmpty() && powerString.indexOf('/') != -1)
            {
                m_power = Integer.parseInt(powerString.substring(0, powerString.indexOf('/')));
            }
            m_picUrl = jsonObj.getString("picUrl");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        m_count = 0;

        m_queuedListeners = new HashSet<OnCardArtworkRetrievalListener>();
    }

    public String getId()
    {
        return m_id;
    }

    public String getName()
    {
        return m_name;
    }

    public CardElement getElement()
    {
        return m_element;
    }

    public CardType getType()
    {
        return m_type;
    }

    public CardOpus getOpus()
    {
        return m_opus;
    }

    public String getCategory()
    {
        return m_category;
    }

    public String getJob()
    {
        return m_job;
    }

    public int getCost()
    {
        return m_cost;
    }

    public int getPower()
    {
        return m_power;
    }

    public String getDescription()
    {
        return m_description;
    }

    public String getImageURL()
    {
        return m_picUrl;
    }

    public void decrementCount()
    {
        if (m_count > 0)
        {
            m_count--;
            FFTCGCompanionApplication.getCardRepository().saveCardCollection();
        }
    }

    public void incrementCount()
    {
        m_count++;
        FFTCGCompanionApplication.getCardRepository().saveCardCollection();
    }

    public void setCount(int count)
    {
        m_count = count;
    }

    public void clearCount()
    {
        m_count = 0;
    }

    public int getCount()
    {
        return m_count;
    }

    public boolean isOwned()
    {
        return m_count > 0;
    }

    public Bitmap getArtworkLocally(final Context context)
    {
        if (m_artwork == null)
        {
            // Try getting saved image first
            Bitmap bitmap = loadImageFromInternalStorage(context);
            if (bitmap != null)
            {
                m_artwork = bitmap;
            }
        }
        return m_artwork;
    }

    public void retrieveArtwork(final Context context, final OnCardArtworkRetrievalListener retrievalListener)
    {
        if (getArtworkLocally(context) != null)
        {
            retrievalListener.OnCardArtworkRetrieved(m_artwork);
            return;
        }

        m_queuedListeners.add(retrievalListener);

        // If we've already sent a request to get the artwork, just return after queueing up new listener
        if (m_retrievingArtwork)
        {
            return;
        }

        // Otherwise, get from network as a last resort
        new BitmapFromUrlAsync(context, this).execute(m_picUrl);
    }

    public void onArtworkFromNetworkFinished(Context context, Bitmap bitmap)
    {
        if (bitmap != null)
        {
            m_artwork = bitmap;
            try
            {
                saveImageToInternalStorage(context, bitmap);
            } catch (IOException e)
            {
                e.printStackTrace();
                return;
            }

            for (OnCardArtworkRetrievalListener listener : m_queuedListeners)
            {
                listener.OnCardArtworkRetrieved(bitmap);
            }
            m_queuedListeners.clear();
        }
    }

    @Override
    public int compareTo(@NonNull Object other)
    {
        return m_id.compareTo(((Card)other).getId());
    }

    private Bitmap loadImageFromInternalStorage(Context context)
    {
        Bitmap bitmap = null;
        try
        {
            FileInputStream inputStream = context.openFileInput(m_id + ".jpg");
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        }
        catch (FileNotFoundException e)
        {
            //e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void saveImageToInternalStorage(Context context, Bitmap bitmap) throws IOException
    {
        if (bitmap != null)
        {
            // Store image locally
            FileOutputStream outputStream = context.openFileOutput(m_id + ".jpg", Context.MODE_PRIVATE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            outputStream.write(stream.toByteArray());
            outputStream.close();
        }
    }
}

