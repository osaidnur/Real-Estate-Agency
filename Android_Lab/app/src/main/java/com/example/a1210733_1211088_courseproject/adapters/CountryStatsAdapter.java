package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.a1210733_1211088_courseproject.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CountryStatsAdapter extends RecyclerView.Adapter<CountryStatsAdapter.CountryViewHolder> {
    
    private Context context;
    private List<CountryData> countriesList;
    
    public CountryStatsAdapter(Context context) {
        this.context = context;
        this.countriesList = new ArrayList<>();
    }    public void updateData(Map<String, Integer> countriesData) {
        countriesList.clear();
        
        android.util.Log.d("CountryStatsAdapter", "Received " + countriesData.size() + " countries");
        
        // Convert map to list and sort by count (descending)
        for (Map.Entry<String, Integer> entry : countriesData.entrySet()) {
            countriesList.add(new CountryData(entry.getKey(), entry.getValue()));
        }
          // Sort by count in descending order
        countriesList.sort((a, b) -> Integer.compare(b.count, a.count));
        
        // Show ALL countries (removed the limit to display all countries)
          android.util.Log.d("CountryStatsAdapter", "Final adapter list size: " + countriesList.size());
        
        notifyDataSetChanged();
        
        // Log final items for debugging
        for (int i = 0; i < countriesList.size(); i++) {
            CountryData country = countriesList.get(i);
            android.util.Log.d("CountryStatsAdapter", "Item " + i + ": " + country.name + " (" + country.count + ")");
        }
    }
    
    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country_stat, parent, false);
        return new CountryViewHolder(view);
    }    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        CountryData country = countriesList.get(position);
        
        // Get country flag emoji
        String flag = getCountryFlag(country.name);
        
        holder.tvCountryName.setText(flag + " " + country.name);
        holder.tvCustomerCount.setText(String.valueOf(country.count));
        holder.tvRank.setText("#" + (position + 1));
        
        // Set different colors for different ranks (cycle through colors for all countries)
        int[] colors = {
            context.getResources().getColor(R.color.stat_blue, null),
            context.getResources().getColor(R.color.stat_green, null),
            context.getResources().getColor(R.color.stat_orange, null),
            context.getResources().getColor(R.color.stat_purple, null),
            context.getResources().getColor(R.color.stat_red, null)
        };
        
        // Use modulo to cycle through colors for any number of countries
        int colorIndex = position % colors.length;
        holder.tvRank.setTextColor(colors[colorIndex]);
        holder.tvCustomerCount.setTextColor(colors[colorIndex]);
    }    @Override
    public int getItemCount() {
        return countriesList.size();
    }
      private String getCountryFlag(String countryName) {
        // Simple mapping of some common countries to their flag emojis
        switch (countryName.toLowerCase()) {
            case "palestine": return "🇵🇸";
            case "jordan": return "🇯🇴";
            case "syria": return "🇸🇾";
            case "lebanon": return "🇱🇧";
            case "iraq": return "🇮🇶";
            case "usa": case "united states": return "🇺🇸";
            case "canada": return "🇨🇦";
            case "uk": case "united kingdom": return "🇬🇧";
            case "germany": return "🇩🇪";
            case "france": return "🇫🇷";
            case "spain": return "🇪🇸";
            case "italy": return "🇮🇹";
            case "japan": return "🇯🇵";
            case "china": return "🇨🇳";
            case "india": return "🇮🇳";
            case "brazil": return "🇧🇷";
            case "australia": return "🇦🇺";
            case "south africa": return "🇿🇦";
            case "egypt": return "🇪🇬";
            case "saudi arabia": return "🇸🇦";
            case "uae": case "united arab emirates": return "🇦🇪";
            case "turkey": return "🇹🇷";            case "russia": return "🇷🇺";
            default: return "🌍"; // Default world emoji
        }
    }
    
    static class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvCountryName, tvCustomerCount;
        
        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvCountryName = itemView.findViewById(R.id.tv_country_name);
            tvCustomerCount = itemView.findViewById(R.id.tv_customer_count);
        }
    }
    
    static class CountryData {
        String name;
        int count;
        
        CountryData(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }
}
