package com.example.jonny.fftcgcompanion.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.models.CardRepository;
import com.example.jonny.fftcgcompanion.models.Deck;
import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;
import com.example.jonny.fftcgcompanion.utils.GeneralUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViewDecksActivity extends Activity
{

    public class DecksAdapter extends RecyclerView.Adapter<ViewDecksActivity.DecksAdapter.DeckViewHolder>
    {
        private final Context m_context;
        private List<Deck> m_decks;

        // This avoids memory-expensive calls to findViewById().
        public class DeckViewHolder extends RecyclerView.ViewHolder
        {
            private final LinearLayout rootView;
            private final TextView nameTextView;
            private final TextView dateTextView;
            private final Button exportButton;
            private final Button removeButton;

            public DeckViewHolder(View deckView)
            {
                super(deckView);

                this.rootView = deckView.findViewById(R.id.deck_root_layout);
                this.nameTextView = deckView.findViewById(R.id.deck_name_textview);
                this.dateTextView = deckView.findViewById(R.id.deck_date_textview);
                this.exportButton = deckView.findViewById(R.id.deck_export_button);
                this.removeButton = deckView.findViewById(R.id.deck_delete_button);
            }
        }

        DecksAdapter(@NonNull Context context, List<Deck> decks)
        {
            m_context = context;
            m_decks = decks;
        }

        public void setDecks(List<Deck> decks)
        {
            m_decks.clear();
            m_decks = decks;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewDecksActivity.DecksAdapter.DeckViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index)
        {
            final LayoutInflater layoutInflater = LayoutInflater.from(m_context);
            View view = layoutInflater.inflate(R.layout.view_deck_listitem, viewGroup, false);
            return new ViewDecksActivity.DecksAdapter.DeckViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewDecksActivity.DecksAdapter.DeckViewHolder deckViewHolder, int index)
        {
            final int cardIndex = index;
            final Deck deck = m_decks.get(index);
            deckViewHolder.nameTextView.setText(deck.getName());
            deckViewHolder.dateTextView.setText(deck.getDate());

            deckViewHolder.exportButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // Export deck to csv
                    if (!GeneralUtils.isExternalStorageWritable())
                        return;

                    if (!FFTCGCompanionApplication.getCardRepository().exportDeckAsXML(m_context, deck))
                        Toast.makeText(m_context, "Failed to export '" + deck.getName() + "'", Toast.LENGTH_LONG).show();

                }
            });

            deckViewHolder.removeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(CardRepository.deleteDeck(deck))
                    {
                        m_decks.remove(deck);
                    }
                }
            });

            deckViewHolder.rootView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(m_context, CreateDeckActivity.class);
                    intent.putExtra("editing", true);
                    intent.putExtra("deck", deck);
                    startActivity(intent);
                }
            });
        }

        @Override
        public long getItemId(int index)
        {
            return CardRepository.getDecks().get(index).getName().hashCode();
        }

        @Override
        public int getItemCount()
        {
            return CardRepository.getDecks().size();
        }
    }

    private RecyclerView m_decksRecyclerView;
    private DecksAdapter m_decksAdapter;
    private Button m_importDeckButton;

    private static final int IMPORT_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_decks);

        boolean decksLoaded = CardRepository.loadAllDecks();

        if (decksLoaded)
        {
            m_decksAdapter = new DecksAdapter(this, CardRepository.getDecks());
            m_decksRecyclerView = findViewById(R.id.decks_listview);
            m_decksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            m_decksRecyclerView.setAdapter(m_decksAdapter);
        }
        else
        {
            findViewById(R.id.nodecks_layout).setVisibility(View.VISIBLE);

            Button addDeckButton = findViewById(R.id.adddeck_button);
            addDeckButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(v.getContext(), CreateDeckActivity.class);
                    startActivity(intent);
                }
            });
        }

        m_importDeckButton = findViewById(R.id.import_deck_button);
        m_importDeckButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                importDeck();
            }
        });
    }

    private void importDeck()
    {
        // If SDK >= 19, use standard file manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            startActivityForResult(intent, IMPORT_RESULT_CODE);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            startActivityForResult(intent, IMPORT_RESULT_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == IMPORT_RESULT_CODE)
        {
            if (resultCode == RESULT_OK)
            {
				FFTCGCompanionApplication.getCardRepository().importDeckFromXML(this, data.getData());
            }
        }
    }
}
