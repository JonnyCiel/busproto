package co.jonnycielodev.busprototipo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.jonnycielodev.busprototipo.entities.Bus;

/**
 * Created by Jonny on 10/10/2017.
 */

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.myViewHolder> {

    private ArrayList<Bus> mBusArrayList;
    private Context mContext;
    private MainActivityAdapterClick listener;

    public MainActivityAdapter(ArrayList<Bus> busArrayList, Context context, MainActivityAdapterClick listener) {
        mBusArrayList = busArrayList;
        mContext = context;
        this.listener = listener;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item_row, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Bus currentBus = mBusArrayList.get(position);

        holder.onClickManagment(currentBus, listener);

        holder.placa.setText(currentBus.getBusPlaca());
        holder.numero.setText("" + currentBus.getRutaNumero());



    }

    @Override
    public int getItemCount() {
        return mBusArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        private TextView placa, numero;
        private Button btnRutas;
        private LinearLayout container;
        public myViewHolder(View itemView) {
            super(itemView);
            placa = itemView.findViewById(R.id.itemRowPlacaBusTextView);
            numero = itemView.findViewById(R.id.itemRowNumeroBusTextView);
            btnRutas = itemView.findViewById(R.id.itemRowLugaresTextView);
            container = itemView.findViewById(R.id.itemRowcontainer);
        }

        private void onClickManagment(final Bus bus, final MainActivityAdapterClick listener){
            btnRutas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRutasClick(bus);
                }
            });

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContainerClick(bus);
                }
            });
        }
    }
}
