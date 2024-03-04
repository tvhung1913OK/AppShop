package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Card;

import java.util.List;

public class CardAdapter extends BaseAdapter {
    private Context context;
    private List<Card> cardList;

    public CardAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.card_item, null);
        }

        TextView tvCardSerial = view.findViewById(R.id.tvCardSerial);
        TextView tvCardPin = view.findViewById(R.id.tvCardPin);
        TextView tvCardProvider = view.findViewById(R.id.tvCardProvider);
        TextView tvCardValue = view.findViewById(R.id.tvCardValue);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvStatus = view.findViewById(R.id.tvStatus);

        Card card = cardList.get(position);

        tvCardSerial.setText("Seri thẻ: " + card.getCardSerial());
        tvCardPin.setText("Mã thẻ: " + card.getCardPin());
        tvCardProvider.setText("Hãng thẻ: " + card.getCardProvider());
        tvCardValue.setText("Mệnh giá: " + card.getCardValue());
        tvTime.setText("Thời gian: " + card.getTime());
        tvUsername.setText("Người dùng: " + card.getUsername());
        tvStatus.setText("Trạng thái: " + card.getStatus());

        return view;
    }
}
