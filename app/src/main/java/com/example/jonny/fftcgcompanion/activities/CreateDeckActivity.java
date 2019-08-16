package com.example.jonny.fftcgcompanion.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.databinding.adapters.TextViewBindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.models.Card;
import com.example.jonny.fftcgcompanion.models.CardFilterParcel;
import com.example.jonny.fftcgcompanion.models.CardRepository;
import com.example.jonny.fftcgcompanion.models.Deck;
import com.example.jonny.fftcgcompanion.utils.CardDiffCallback;
import com.example.jonny.fftcgcompanion.views.ViewCardsFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//TODO: Add remove button with icon
public class CreateDeckActivity extends AppCompatActivity
{
	public class AddedCardsAdapter extends RecyclerView.Adapter<AddedCardsAdapter.CardViewHolder>
	{
		private final Context m_context;
		private List<Pair<String, Integer>> m_deckCards;

		// This avoids memory-expensive calls to findViewById().
		public class CardViewHolder extends RecyclerView.ViewHolder
		{
			private final View elementView;
			private final TextView nameTextView;
			private final TextView idTextView;
			private final ImageView artImageView;
			private final Spinner quantitySpinner;
			private final Button removeButton;

			public CardViewHolder(View cardView)
			{
				super(cardView);

				this.elementView = cardView.findViewById(R.id.elementcolour_view);
				this.nameTextView = cardView.findViewById(R.id.card_name_textview);
				this.idTextView = cardView.findViewById(R.id.card_id_textview);
				this.artImageView = cardView.findViewById(R.id.card_imageview);
				this.quantitySpinner = cardView.findViewById(R.id.quantity_spinner);
				this.removeButton = cardView.findViewById(R.id.remove_button);
			}
		}

		AddedCardsAdapter(@NonNull Context context, List<Pair<String, Integer>> cardList)
		{
			m_context = context;
			m_deckCards = cardList;
		}

		public void setCards(List<Pair<String, Integer>> cardList)
		{
			m_deckCards.clear();
			m_deckCards = cardList;
			notifyDataSetChanged();
		}

		@NonNull
		@Override
		public CreateDeckActivity.AddedCardsAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index)
		{
			final LayoutInflater layoutInflater = LayoutInflater.from(m_context);
			View view = layoutInflater.inflate(R.layout.deck_card_item_view, viewGroup, false);
			return new CreateDeckActivity.AddedCardsAdapter.CardViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int index)
		{
			final int cardIndex = index;
			final String cardID = m_deckCards.get(index).first;
			final Card card = CardRepository.getCard(cardID);
			final int quantity = m_deckCards.get(index).second;
			cardViewHolder.nameTextView.setText(card.getName());
			cardViewHolder.idTextView.setText(card.getId());
			cardViewHolder.elementView.setBackgroundColor(getResources().getIntArray(R.array.cardelement_colours)[card.getElement().ordinal()]);


			Bitmap cardArtwork = card.getArtworkLocally(m_context);
			if (cardArtwork != null)
			{
				Bitmap imageBitmap = Bitmap.createBitmap(cardArtwork.getWidth(), 200, Bitmap.Config.ARGB_8888);
				Canvas artworkCanvas = new Canvas(imageBitmap);

				Rect drawInto = new Rect(0,0, cardArtwork.getWidth(), 200);
				Point offset = new Point(75, 80);
				Rect artworkSection = new Rect(offset.x, offset.y, drawInto.width() - offset.x, 200);

				//Paint paint = new Paint();
				//paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
				//paint.setShader(new LinearGradient(0,0,100,0,0x00000000, 0xFFFFFFFF, Shader.TileMode.CLAMP));
				//artworkCanvas.drawRect(0,0, imageBitmap.getWidth(), imageBitmap.getHeight(), paint);
				artworkCanvas.drawBitmap(cardArtwork, artworkSection, drawInto, null);
				cardViewHolder.artImageView.setImageBitmap(imageBitmap);
			}

			ArrayAdapter<CharSequence> quantityAdapter = ArrayAdapter.createFromResource(m_context, R.array.quantityspinner_values, android.R.layout.simple_spinner_item);
			quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cardViewHolder.quantitySpinner.setAdapter(quantityAdapter);
			cardViewHolder.quantitySpinner.setSelection(quantity - 1);
			cardViewHolder.quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
				{
					m_deckCards.set(cardIndex, new Pair<>(cardID, position + 1));
					recalculateCardCount();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{
				}
			});

			cardViewHolder.removeButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					removeCard(cardID);
				}
			});
		}

		@Override
		public long getItemId(int index)
		{
			return m_deckCards.get(index).first.hashCode();
		}

		@Override
		public int getItemCount()
		{
			return m_deckCards.size();
		}
	}

	private static final int ADD_CARD_CODE = 1;

	private static final int NUM_CARDS = 3;
	//private static final int NUM_CARDS = 50;

	private List<Pair<String, Integer>> m_addedCards = new ArrayList<>();
	private AddedCardsAdapter m_adapter;
	private boolean m_editingDeck;

	private int m_cardCount = 0;

	private RecyclerView m_addedCardsView;
	private EditText m_deckNameEditText;
	private TextView m_typeInfoTextView;
	private TextView m_elementInfoTextView;
	private Button m_saveButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_deck);

		m_deckNameEditText = findViewById(R.id.deckname_edittext);
		m_deckNameEditText.setText("New Deck");

		m_editingDeck = getIntent().getBooleanExtra("editing", false);

		if (m_editingDeck)
		{
			restoreCards();
		}
		m_adapter = new AddedCardsAdapter(this, m_addedCards);

		m_addedCardsView = findViewById(R.id.addedCards_view);

		m_addedCardsView.setLayoutManager(new LinearLayoutManager(this));
		m_addedCardsView.setAdapter(m_adapter);

		m_typeInfoTextView = findViewById(R.id.typeinfo_textview);
		m_elementInfoTextView = findViewById(R.id.elementinfo_textview);

		m_saveButton = findViewById(R.id.save_button);
		String buttonText = "(" + m_cardCount + "/" + NUM_CARDS + ") cards";
		m_saveButton.setText(buttonText);
		m_saveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String deckName = m_deckNameEditText.getText().toString();
				String notes = ""; // TODO: Add notes to decks - through ViewDeck activity?
				if (deckName.length() == 0)
				{
					Toast.makeText(v.getContext(), "Please enter a name for the deck!", Toast.LENGTH_LONG).show();
					return;
				}

				Map<String, Integer> cardMap = new HashMap<>();
				for (Pair<String, Integer> cardPair: m_addedCards)
				{
					cardMap.put(cardPair.first, cardPair.second);
				}

				// Get current date/time
				@SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String dateStr = simpleDateFormat.format(Calendar.getInstance().getTime());

				// Create new Deck object and try to save to storage
				Deck newDeck = new Deck(deckName, notes, cardMap, dateStr);
				boolean successfulSave = CardRepository.saveDeck(newDeck);

				String toastText = "";
				if(successfulSave)
				{
					toastText = "Saved '" + deckName + "' successfully!";
				}
				else
				{
					toastText = "There was a problem saving the deck. Please check the amount of available storage space and try again.";
				}
				Toast.makeText(v.getContext(), toastText, Toast.LENGTH_LONG).show();

				// If saved successfully, exit activity
				if (successfulSave)
					finish();
			}
		});

		recalculateCardCount();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == ADD_CARD_CODE)
			{
				String selectedCardID = data.getStringExtra("SelectedCardID");
				int quantity = data.getIntExtra("Quantity", 1);
				m_addedCards.add(new Pair<>(selectedCardID, quantity));
				m_addedCardsView.setAdapter(m_adapter);
			}
		}
	}

	public void onAddCard(View view)
	{
		Intent intent = new Intent(this, AddCardActivity.class);
		startActivityForResult(intent, ADD_CARD_CODE);

		m_cardCount++;
	}

	public void onSortCard(View view)
	{
		//TODO: Do sorting

	}

	private void removeCard(String cardID)
	{
		for (int i = 0; i < m_addedCards.size(); ++i)
		{
			if (m_addedCards.get(i).first == cardID)
			{
				m_cardCount -= m_addedCards.get(i).second;
				m_addedCards.remove(i);

				break;
			}
		}
		m_addedCardsView.setAdapter(m_adapter);
		Card removedCard = CardRepository.getCard(cardID);
		Toast.makeText(this, "Removed " + removedCard.getName() + " (" + removedCard.getId() + ")", Toast.LENGTH_LONG).show();

		recalculateCardCount();
	}

	private void recalculateCardCount()
	{
		int[] typeCount = new int[4];
		int[] elementCount = new int[8];
		int count = 0;
		for (int i = 0; i < m_addedCards.size(); ++i)
		{
			Card card = CardRepository.getCard(m_addedCards.get(i).first);
			int quantity = m_addedCards.get(i).second;
			count += quantity;
			typeCount[card.getType().ordinal()] += quantity;
			elementCount[card.getElement().ordinal()] += quantity;
		}
		m_cardCount = count;

		String buttonText = "Save";
		if (m_cardCount == NUM_CARDS)
		{
			m_saveButton.setEnabled(true);
		}
		else
		{
			buttonText = "(" + m_cardCount + "/" + NUM_CARDS + ") cards";
			m_saveButton.setEnabled(false);
		}
		m_saveButton.setText(buttonText);

		String typeString = "";
		String elementString = "";
		if (m_cardCount == 0)
		{
			typeString = "None";
			elementString = "None";
		}
		else
		{
			// Build type string
			StringBuilder typeStringBuilder = new StringBuilder();
			String[] typeShortNames = getResources().getStringArray(R.array.cardtype_names);
			for (int i = 0; i < typeCount.length; ++i)
			{
				if (typeCount[i] > 0)
				{
					typeStringBuilder.append(typeCount[i] + " " + typeShortNames[i] + "/");
				}
			}

			// Remove trailing '/' character
			if (typeStringBuilder.length() > 0)
				typeStringBuilder.deleteCharAt(typeStringBuilder.length() - 1);
			typeString = typeStringBuilder.toString();


			// Build element string
			StringBuilder elementStringBuilder = new StringBuilder();
			String[] elementNames = getResources().getStringArray(R.array.cardelement_names);
			for (int i = 0; i < elementCount.length; ++i)
			{
				if (elementCount[i] > 0)
				{
					elementStringBuilder.append(elementCount[i] + " " + elementNames[i] + "/");
				}
			}

			// Remove trailing '/' character
			if (elementStringBuilder.length() > 0)
				elementStringBuilder.deleteCharAt(elementStringBuilder.length() - 1);
			elementString = elementStringBuilder.toString();
		}
		m_typeInfoTextView.setText(typeString);
		m_elementInfoTextView.setText(elementString);
	}

	private void restoreCards()
	{
		Deck deck = (Deck) getIntent().getSerializableExtra("deck");

		Map<String, Integer> deckCards = deck.getCards();
		Iterator<Map.Entry<String, Integer>> cardIter = deckCards.entrySet().iterator();
		while (cardIter.hasNext())
		{
			Map.Entry<String, Integer> cardEntry = cardIter.next();
			m_addedCards.add(new Pair<>(cardEntry.getKey(), cardEntry.getValue()));
		}

		m_deckNameEditText.setText(deck.getName());
	}
}
