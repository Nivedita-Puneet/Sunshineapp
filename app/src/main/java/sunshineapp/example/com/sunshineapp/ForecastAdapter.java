package sunshineapp.example.com.sunshineapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by PUNEETU on 14-02-2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;
    private final ForecastAdapterOnClickHandler mClickHandler;

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler){

        mClickHandler = clickHandler;
    }

    public interface ForecastAdapterOnClickHandler{
        void clickListener(String weatherForToday);
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{



        public final TextView mWeatherTextView;
        ForecastAdapterViewHolder(View view){
            super(view);
            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mClickHandler.clickListener(mWeatherData[getAdapterPosition()]);
        }
    }
    @Override
    public ForecastAdapter.ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.forecast_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater.from(context).inflate(id,parent,shouldAttachToParentImmediately);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapter.ForecastAdapterViewHolder holder, int position) {
        holder.mWeatherTextView.setText(mWeatherData[position]);
    }

    @Override
    public int getItemCount() {
        if(null == mWeatherData){
            return 0;
        }else {
            int length = mWeatherData.length;
            Log.i(ForecastAdapter.class.getSimpleName(), ""+length);
            return  mWeatherData.length;
        }
    }

       public void setWeatherData(String[] weatherData){
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }
}
