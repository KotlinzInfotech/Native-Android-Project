package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity.Activity_imageEdit;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.Effect;

import java.util.ArrayList;

public class EffectAdapter extends RecyclerView.Adapter<EffectAdapter.MyViewHolder> {
    int selected_pos = 0;
    private Activity_imageEdit activity;
    private ArrayList<Integer> effectList = new ArrayList();

    public EffectAdapter(Context context, ArrayList<Integer> effectList) {
        this.activity = (Activity_imageEdit) context;
        this.effectList = effectList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EffectAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.effect, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.imageView.setImageResource(this.effectList.get(position));
        if (selected_pos == position) {
            holder.llEffectBorder.setBackgroundResource(R.drawable.bg_border_press);
        } else {
            holder.llEffectBorder.setBackgroundResource(R.drawable.bg_border);
        }
        if (position == 0) {
            Effect.applyEffectNone(holder.imageView);
        }
        if (position == 1) {
            Effect.applyEffect1(holder.imageView);
        }
        if (position == 2) {
            Effect.applyEffect2(holder.imageView);
        }
        if (position == 3) {
            Effect.applyEffect4(holder.imageView);
        }
        if (position == 4) {
            Effect.applyEffect5(holder.imageView);
        }
        if (position == 5) {
            Effect.applyEffect6(holder.imageView);
        }
        if (position == 6) {
            Effect.applyEffect7(holder.imageView);
        }
        if (position == 7) {
            Effect.applyEffect9(holder.imageView);
        }
        if (position == 8) {
            Effect.applyEffect11(holder.imageView);
        }
        if (position == 9) {
            Effect.applyEffect12(holder.imageView);
        }
        if (position == 10) {
            Effect.applyEffect14(holder.imageView);
        }
        if (position == 11) {
            Effect.applyEffect15(holder.imageView);
        }
        if (position == 12) {
            Effect.applyEffect16(holder.imageView);
        }
        if (position == 13) {
            Effect.applyEffect17(holder.imageView);
        }
        if (position == 14) {
            Effect.applyEffect18(holder.imageView);
        }
        if (position == 15) {
            Effect.applyEffect19(holder.imageView);
        }
        if (position == 16) {
            Effect.applyEffect20(holder.imageView);
        }
        if (position == 17) {
            Effect.applyEffect21(holder.imageView);
        }
        if (position == 18) {
            Effect.applyEffect22(holder.imageView);
        }
        holder.llEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_pos = position;
                if (position == 0) {
                    Effect.applyEffectNone(Activity_imageEdit.img_gallrey1);
                }
                if (position == 1) {
                    Effect.applyEffect1(Activity_imageEdit.img_gallrey1);
                }
                if (position == 2) {
                    Effect.applyEffect2(Activity_imageEdit.img_gallrey1);
                }
                if (position == 3) {
                    Effect.applyEffect4(Activity_imageEdit.img_gallrey1);
                }
                if (position == 4) {
                    Effect.applyEffect5(Activity_imageEdit.img_gallrey1);
                }
                if (position == 5) {
                    Effect.applyEffect6(Activity_imageEdit.img_gallrey1);
                }
                if (position == 6) {
                    Effect.applyEffect7(Activity_imageEdit.img_gallrey1);
                }
                if (position == 7) {
                    Effect.applyEffect9(Activity_imageEdit.img_gallrey1);
                }
                if (position == 8) {
                    Effect.applyEffect11(Activity_imageEdit.img_gallrey1);
                }
                if (position == 9) {
                    Effect.applyEffect12(Activity_imageEdit.img_gallrey1);
                }
                if (position == 10) {
                    Effect.applyEffect14(Activity_imageEdit.img_gallrey1);
                }
                if (position == 11) {
                    Effect.applyEffect15(Activity_imageEdit.img_gallrey1);
                }
                if (position == 12) {
                    Effect.applyEffect16(Activity_imageEdit.img_gallrey1);
                }
                if (position == 13) {
                    Effect.applyEffect17(Activity_imageEdit.img_gallrey1);
                }
                if (position == 14) {
                    Effect.applyEffect18(Activity_imageEdit.img_gallrey1);
                }
                if (position == 15) {
                    Effect.applyEffect19(Activity_imageEdit.img_gallrey1);
                }
                if (position == 16) {
                    Effect.applyEffect20(Activity_imageEdit.img_gallrey1);
                }
                if (position == 17) {
                    Effect.applyEffect21(Activity_imageEdit.img_gallrey1);
                }
                if (position == 18) {
                    Effect.applyEffect22(Activity_imageEdit.img_gallrey1);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.effectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llEffect;
        LinearLayout llEffectBorder;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            llEffect=itemView.findViewById(R.id.ll_effect);
            llEffectBorder=itemView.findViewById(R.id.ll_effect_border);
            imageView=itemView.findViewById(R.id.img);
        }
    }
}
