package com.example.pjaidmobile.presentation.features.report;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pjaidmobile.R;
import com.example.pjaidmobile.data.model.ReportItem;

/**
 * Adapter for displaying a list of requests (reports) in RecyclerView.
 * Supports sorting, comparing and clicking on a single item.
 */

public class ReportListAdapter extends ListAdapter<ReportItem, ReportListAdapter.ReportViewHolder> {

    public ReportListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);

    }

    /**
     * Fills the ViewHolder with data from the ReportItem object.
     */

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem item = getItem(position);
        holder.description.setText(item.getTitle());
        holder.date.setText(item.getDate());
        holder.technician.setText("Technik: " + item.getTechnicianName());
        holder.status.setText("Status: " + item.getStatus());


        String status = item.getStatus().toLowerCase().replace("_", " ").trim();

        String displayedStatus = status.length() > 0
                ? status.substring(0, 1).toUpperCase() + status.substring(1)
                : "Nieznany";

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString label = new SpannableString("Status: ");
        label.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        label.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString value = new SpannableString(displayedStatus);
        int color;

        if (status.contains("nowe")) {
            color = 0xFFE53935;
            holder.container.setBackgroundResource(R.drawable.bg_status_new);
        } else if (status.contains("w trakcie")) {
            color = 0xFFFFA000;
            holder.container.setBackgroundResource(R.drawable.bg_status_in_progress);
        } else if (status.contains("zakończone") || status.contains("zamkniete")) {
            color = 0xFF43A047;
            holder.container.setBackgroundResource(R.drawable.bg_status_closed);
        } else {
            color = Color.DKGRAY;
            holder.container.setBackgroundResource(R.drawable.rounded_background);
        }

        value.setSpan(new ForegroundColorSpan(color), 0, value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.append(label).append(value);
        holder.status.setText(builder);




        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("reportId", item.getId());
            context.startActivity(intent);
        });
    }



    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView title, technician, status, description, date;
        LinearLayout container;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.textViewDescription);
            date = itemView.findViewById(R.id.textViewDate);
            technician = itemView.findViewById(R.id.textViewTechnician);
            status = itemView.findViewById(R.id.textViewStatus);
            container = itemView.findViewById(R.id.item_container);
        }
    }

    /**
     * ViewHolder holding views of a single list item.
     */

    private static final DiffUtil.ItemCallback<ReportItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ReportItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReportItem oldItem, @NonNull ReportItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReportItem oldItem, @NonNull ReportItem newItem) {
            return oldItem.equals(newItem);
        }
    };

}