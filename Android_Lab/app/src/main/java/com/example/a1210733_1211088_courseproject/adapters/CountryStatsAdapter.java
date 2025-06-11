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
    }
    
    public void updateData(Map<String, Integer> countriesData) {
        countriesList.clear();
        
        // Convert map to list and sort by count (descending)
        for (Map.Entry<String, Integer> entry : countriesData.entrySet()) {
            countriesList.add(new CountryData(entry.getKey(), entry.getValue()));
        }
        
        // Sort by count in descending order
        countriesList.sort((a, b) -> Integer.compare(b.count, a.count));
        
        // Limit to top 5 countries
        if (countriesList.size() > 5) {
            countriesList = countriesList.subList(0, 5);
        }
        
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country_stat, parent, false);
        return new CountryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        CountryData country = countriesList.get(position);
        
        // Get country flag emoji
        String flag = getCountryFlag(country.name);
        
        holder.tvCountryName.setText(flag + " " + country.name);
        holder.tvCustomerCount.setText(String.valueOf(country.count));
        holder.tvRank.setText("#" + (position + 1));
        
        // Set different colors for different ranks
        int[] colors = {
            context.getResources().getColor(R.color.stat_blue, null),
            context.getResources().getColor(R.color.stat_green, null),
            context.getResources().getColor(R.color.stat_orange, null),
            context.getResources().getColor(R.color.stat_purple, null),
            context.getResources().getColor(R.color.stat_red, null)
        };
        
        if (position < colors.length) {
            holder.tvRank.setTextColor(colors[position]);
            holder.tvCustomerCount.setTextColor(colors[position]);
        }
    }
    
    @Override
    public int getItemCount() {
        return countriesList.size();
    }
    
    private String getCountryFlag(String countryName) {
        // Simple mapping of some common countries to their flag emojis
        switch (countryName.toLowerCase()) {
            case "palestine": return "🇵🇸";
            case "jordan": return "🇯🇴";
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
            case "turkey": return "🇹🇷";
            case "russia": return "🇷🇺";
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
