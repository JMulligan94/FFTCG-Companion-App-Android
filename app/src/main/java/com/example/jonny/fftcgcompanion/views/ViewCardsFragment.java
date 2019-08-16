package com.example.jonny.fftcgcompanion.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonny.fftcgcompanion.activities.CardDetailsActivity;
import com.example.jonny.fftcgcompanion.activities.CardFilterActivity;
import com.example.jonny.fftcgcompanion.activities.CardScannerActivity;
import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.databinding.ViewCardsFragmentBinding;
import com.example.jonny.fftcgcompanion.models.Card;
import com.example.jonny.fftcgcompanion.models.CardFilterParcel;
import com.example.jonny.fftcgcompanion.models.CardRepository;
import com.example.jonny.fftcgcompanion.utils.CardDiffCallback;
import com.example.jonny.fftcgcompanion.utils.OnCardSelectedListener;
import com.example.jonny.fftcgcompanion.viewmodels.ViewCardsViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ViewCardsFragment extends Fragment
{
    ////////////////////////////////////////////////
    // class CardRecylerAdapter
    ////////////////////////////////////////////////
    class CardRecylerAdapter extends RecyclerView.Adapter<CardRecylerAdapter.CardViewHolder> implements Filterable
    {
        private final Context m_context;
        private List<Card> m_cards;
        private List<Card> m_filteredCards;

        // This avoids memory-expensive calls to findViewById().
        public class CardViewHolder extends RecyclerView.ViewHolder
        {
            private final View rootView;
            private final TextView nameTextView;
            private final TextView idTextView;
            private final TextView altInfoTextView;
            private final TextView descTextView;
            private final ImageView artImageView;
            private final TextView cardCountTextView;

            public CardViewHolder(View cardView)
            {
                super(cardView);

                this.rootView = cardView.findViewById(R.id.card_root_layout);
                this.nameTextView = cardView.findViewById(R.id.card_name_textview);
                this.idTextView = cardView.findViewById(R.id.card_id_textview);
                this.altInfoTextView = cardView.findViewById(R.id.card_typejobcategory_textview);
                this.artImageView = cardView.findViewById(R.id.card_imageview);
                this.descTextView = cardView.findViewById(R.id.card_desc_textview);
                this.cardCountTextView = cardView.findViewById(R.id.card_count_textview);
            }
        }

        CardRecylerAdapter(Context context, List<Card> cardList)
        {
            m_context = context;
            m_cards = cardList;
            m_filteredCards = cardList;
        }

        public void setCards(List<Card> cardList)
        {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CardDiffCallback(m_cards, cardList));

            m_cards.clear();
            m_cards = cardList;
            m_filteredCards.clear();
            m_filteredCards = cardList;
            result.dispatchUpdatesTo(this);
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index)
        {
            final LayoutInflater layoutInflater = LayoutInflater.from(m_context);
            View view = layoutInflater.inflate(m_cardViewType == CardViewType.LIST_VIEW ? R.layout.card_listitem_layout : R.layout.card_griditem_layout, viewGroup, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int index)
        {
            final Card card = m_filteredCards.get(index);
            cardViewHolder.nameTextView.setText(card.getName());
            cardViewHolder.idTextView.setText(card.getId());

            Bitmap cardArtwork = card.getArtworkLocally(m_context);
            if (cardArtwork != null)
            {
                cardViewHolder.artImageView.setImageBitmap(card.getArtworkLocally(m_context));
            }
            else
            {
                // Default card artwork
                cardViewHolder.artImageView.setImageBitmap(BitmapFactory.decodeResource(m_context.getResources(), R.drawable.default_icon));
            }

            cardViewHolder.artImageView.setImageAlpha(card.isOwned() ? 255 : 130);

            if (cardViewHolder.altInfoTextView != null)
            {
                cardViewHolder.altInfoTextView.setText(card.getType().toString() + ", " + card.getJob() + ", " + card.getCategory());
            }

            if (cardViewHolder.descTextView != null)
            {
                cardViewHolder.descTextView.setText(card.getDescription());
            }

            cardViewHolder.cardCountTextView.setText(Integer.toString(card.getCount()));

            cardViewHolder.rootView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //card.incrementCount();
                    //notifyItemChanged(cardIndex);
                    openCardDetails(card);
                }
            });
        }

        @Override
        public long getItemId(int index)
        {
            return m_filteredCards.get(index).getId().hashCode();
        }

        @Override
        public int getItemCount()
        {
            return m_filteredCards.size();
        }

        @Override
        public Filter getFilter()
        {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results)
                {
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint)
                {
                    FilterResults results = new FilterResults();

                    if (!m_filterParcel.hasSearchParameters())
                    {
                        m_filteredCards = new ArrayList<>(m_cards);
                    }
                    else
                    {
                        m_filteredCards = m_filterParcel.searchCards();
                    }

                    results.count = m_filteredCards.size();
                    results.values = m_filteredCards;
                    return results;
                }
            };

            return filter;
        }
    }

    private enum CardViewType
    {
        GRID_3_VIEW,
        LIST_VIEW,
        GRID_2_VIEW
    }

    private static final int SEARCH_FILTER_CODE = 1;
    private static final int SCAN_CODE = 2;
    private static final int SELECT_CARD_CODE = 3;

    private RecyclerView m_cardsRecylerView;
    private EditText m_filterEditText;
    private Button m_cardTypeCycleButton;

    private ViewCardsViewModel m_viewModel;
    private ViewCardsFragmentBinding m_binding;
    private CardRecylerAdapter m_cardAdapter;
    private OnCardSelectedListener m_cardSelectedListener;

    private CardFilterParcel m_filterParcel;
    private CardViewType m_cardViewType = CardViewType.GRID_3_VIEW;
    private ViewCardsResult m_result = ViewCardsResult.NONE;

    public enum ViewCardsResult
    {
        NONE,
        SELECT_CARD
    }

    public static ViewCardsFragment newInstance()
    {
        return new ViewCardsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        m_binding = DataBindingUtil.inflate(inflater, R.layout.view_cards_fragment, container, false);
        m_viewModel = ViewModelProviders.of(this).get(ViewCardsViewModel.class);
        m_binding.setViewModel(m_viewModel);
        m_binding.setView(this);
        m_filterParcel = new CardFilterParcel();
        return m_binding.getRoot();
    }

    public void setCardSelectedListener(OnCardSelectedListener listener)
    {
        m_cardSelectedListener = listener;
    }

    public void setResultType(ViewCardsResult resultType)
    {
        m_result = resultType;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        final Context context = getContext();

        m_cardsRecylerView = (RecyclerView) getView().findViewById(R.id.view_cards_recyclerview);

        setLayoutManagerFromCardType();

        m_cardAdapter = new CardRecylerAdapter(context, new ArrayList<Card>());
        m_cardsRecylerView.setAdapter(m_cardAdapter);

        m_filterEditText = (EditText) getView().findViewById(R.id.filter_edittext);
        m_filterEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                m_filterParcel.ParcelKeywords.clear();
                // Split keywords by space
                for (String searchToken : s.toString().split(" "))
                {
                    m_filterParcel.ParcelKeywords.add(searchToken);
                }
                m_cardAdapter.getFilter().filter(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        m_cardTypeCycleButton = getView().findViewById(R.id.cardview_button);
        setButtonBackgroundFromCardType();

        m_viewModel = ViewModelProviders.of(this).get(ViewCardsViewModel.class);
        m_viewModel.getCards().observe(getViewLifecycleOwner(), new Observer<List<Card>>()
        {
            @Override
            public void onChanged(@Nullable List<Card> cards)
            {
                m_cardAdapter.setCards(cards);
            }
        });
    }

    private void setLayoutManagerFromCardType()
    {
        switch (m_cardViewType)
        {
            case GRID_3_VIEW:
            {
                m_cardsRecylerView.setLayoutManager(new GridLayoutManager(getView().getContext(), 3));
            }
            break;
            case LIST_VIEW:
            {
                m_cardsRecylerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
            }
            break;
            case GRID_2_VIEW:
            {
                m_cardsRecylerView.setLayoutManager(new GridLayoutManager(getView().getContext(), 2));
            }
            break;
        }
    }

    private void setButtonBackgroundFromCardType()
    {
        TypedArray cardTypeBackgrounds = getResources().obtainTypedArray(R.array.viewtype_backgrounds);
        int backgroundIndex = (m_cardViewType.ordinal() + 1) % CardViewType.values().length;
        m_cardTypeCycleButton.setBackgroundResource(cardTypeBackgrounds.getResourceId(backgroundIndex, 0));
    }

    private void openCardDetails(Card card)
    {
        Intent intent = new Intent(getContext(), CardDetailsActivity.class);
        intent.putExtra("CardID", card.getId());
        intent.putExtra("SelectCard", m_result == ViewCardsResult.SELECT_CARD);
        if (m_result == ViewCardsResult.SELECT_CARD)
        {
            startActivityForResult(intent, SELECT_CARD_CODE);
        }
        else
        {
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == SEARCH_FILTER_CODE)
            {
                m_filterParcel = (CardFilterParcel) data.getSerializableExtra("Filters");
                m_cardAdapter.getFilter().filter(null);
            }
            else if (requestCode == SCAN_CODE)
            {
                String cardID = data.getStringExtra("CardID");
                openCardDetails(CardRepository.getCard(cardID));
            }
            else if (requestCode == SELECT_CARD_CODE)
            {
                String cardID = data.getStringExtra("CardID");
                int quantity = data.getIntExtra("Quantity", 0);
                m_cardSelectedListener.onCardSelected(cardID, quantity);
            }
        }
    }

    public void onFilter(View view)
    {
        Intent intent = new Intent(view.getContext(), CardFilterActivity.class);
        intent.putExtra("FilterParcel", m_filterParcel);
        startActivityForResult(intent, SEARCH_FILTER_CODE);
    }


    public void onCardViewChanged(View view)
    {
        int newIndex = (m_cardViewType.ordinal() + 1) % CardViewType.values().length;
        m_cardViewType = CardViewType.values()[newIndex];

        setButtonBackgroundFromCardType();
        setLayoutManagerFromCardType();
        m_cardsRecylerView.setAdapter(m_cardAdapter);
    }

    public void onScanCard(View view)
    {
        Intent intent = new Intent(view.getContext(), CardScannerActivity.class);
        startActivityForResult(intent, SCAN_CODE);
    }
}
