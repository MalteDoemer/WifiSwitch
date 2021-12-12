package com.example.wifiswitch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.wifiswitch.databinding.RecyclerViewRowBinding;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final List<SmartSwitch> data;
    private final LayoutInflater inflater;
    private ItemClickListener itemClickListener;
    private DeleteButtonClickListener deleteButtonClickListener;


    public RecyclerViewAdapter(Context context, List<SmartSwitch> data) {
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmartSwitch smartSwitch = data.get(position);
        holder.getBinding().switchName.setText(smartSwitch.getName());
        holder.getBinding().switchHost.setText(smartSwitch.getHost());

        if (smartSwitch.getRelayState() == SmartSwitch.State.On) {
            holder.getBinding().switchIcon.setImageResource(R.drawable.power_on);
        } else if (smartSwitch.getRelayState() == SmartSwitch.State.Off) {
            holder.getBinding().switchIcon.setImageResource(R.drawable.power_off);
        } else if (smartSwitch.getRelayState() == SmartSwitch.State.Unknown) {
            holder.getBinding().switchIcon.setImageResource(R.drawable.question_mark);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItemClickListener(ItemClickListener clickListener) {
        this.itemClickListener = clickListener;
    }

    public void setDeleteButtonClickListener(DeleteButtonClickListener deleteButtonClickListener) {
        this.deleteButtonClickListener = deleteButtonClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerViewRowBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerViewRowBinding.bind(itemView);

            itemView.setOnClickListener(view -> {
                if (itemClickListener != null)
                    itemClickListener.onItemClicked(getBinding(), getAdapterPosition());
            });

            binding.clearButton.setOnClickListener(view -> {
                if (deleteButtonClickListener != null)
                    deleteButtonClickListener.onItemClicked(getBinding(), getAdapterPosition());
            });
        }

        public RecyclerViewRowBinding getBinding() {
            return binding;
        }
    }

    public interface ItemClickListener {
        void onItemClicked(RecyclerViewRowBinding binding, int pos);
    }

    public interface DeleteButtonClickListener {
        void onItemClicked(RecyclerViewRowBinding binding, int pos);
    }
}
