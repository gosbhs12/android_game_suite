package com.example.k_dev_master.memorygame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import com.example.k_dev_master.R;
import com.example.k_dev_master.databinding.ItemCardBinding;

public class MemoryGameAdapter extends RecyclerView.Adapter<MemoryGameAdapter.ViewHolder>{
    Vector<Card> cards = new Vector<>();

    private Activity activity;
    private Context context;

    private int width = 0, height = 0;

    private boolean startAnimate = false;

    MemoryGameAdapter(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardBinding binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCardBinding binding = holder.binding;
        // binding 받아와서 itemPictureBinding 으로 지정
        // 16칸 전체를 셋업하기

        // 항상 업데이트 해주는 역할
        if (height != 0 && width != 0) {
            binding.cardLayout.getLayoutParams().width = width;
            binding.cardLayout.getLayoutParams().height = height;
        }

        Card card = cards.get(position);
        int check = card.getCheck(); // get check
        binding.cardTxtView.setImageDrawable(ContextCompat.getDrawable(context, card.getDisplay()));
        // display에 해당하는 사진으로 set image
        Log.e("check[" + position + "] = ", String.valueOf(check));
        // 첫시작
        if (check == 0 && startAnimate) {
            binding.cardTxtView.animate()
                    .rotationY(90)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            binding.cardTxtView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.card_blank));

                            binding.cardTxtView.animate()
                                    .rotationYBy(90)
                                    .rotationY(180)
                                    .setDuration(200)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            card.setCheck(1);
                                            // check == 0 인것을 뒤집음 check = 1
                                        }
                                    })
                                    .start();
                        }
                    })
                    .start();
        } else if (check == 2) {
            binding.cardTxtView.setBackgroundColor(Color.BLACK);
            // if these cards are already correctly matched, set color black
        } else {

        }

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    void setImage(int pos, int image) {
        cards.get(pos).setDisplay(image);
        notifyItemChanged(pos);
    }

    void setUpPicture(Vector<Card> pictures) {
        this.cards = pictures;
        notifyDataSetChanged();
    }

    void setLength(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void setStartAnimate(boolean startAnimate) {
        this.startAnimate = startAnimate;
        notifyDataSetChanged();
    }

    void update(int pos, int check) {
        cards.get(pos).setCheck(check);
        notifyItemChanged(pos);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemCardBinding binding;

        ViewHolder(ItemCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
