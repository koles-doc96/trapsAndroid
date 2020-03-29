package kolesnikov.ru.traps.gui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;
import kolesnikov.ru.traps.Utils.DateUtils;
import kolesnikov.ru.traps.gui.TrapsActivity;

public class RecycleViewTrapsAdapter extends RecyclerView.Adapter<RecycleViewTrapsAdapter.TrapsViewHolder> {

    private  List<Trap> traps = new ArrayList<>();
    private  Context context;

    public RecycleViewTrapsAdapter(List<Trap> traps, Context context) {
        this.traps = traps;
        this.context = context;
    }

    @NonNull
    @Override
    public TrapsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TrapsViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemViewGroup = inflater.inflate(R.layout.item_layout_trap, parent, false);
        viewHolder = new TrapsViewHolder(itemViewGroup);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrapsViewHolder holder, int position) {

        final Trap trap = traps.get(position);
        holder.number.setText("Ловушка №" + trap.getId());
        Date date = DateUtils.stringToDate(trap.getDateInspection());
        holder.date.setText(DateUtils.simpleDateFormat.format(date));

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrapsActivity.class);
                intent.putExtra("id", trap.getId());
                intent.putExtra("date", trap.getDateInspection());
                intent.putExtra("traceBittes", trap.isTraceBittes());
                intent.putExtra("adhesivePlateReplacement", trap.isAdhesivePlateReplacement());
                intent.putExtra("numberPests", trap.getNumberPests());
                intent.putExtra("isTrapDamage", trap.isTrapDamage());
                intent.putExtra("isTrapReplacement", trap.isTrapReplacement());
                intent.putExtra("isTrapReplacementDo", trap.isTrapReplacementDo());
                intent.putExtra("photo", trap.getPhoto());
                intent.putExtra("barCode", trap.getBarCode());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return traps.size();
    }

    public class TrapsViewHolder extends RecyclerView.ViewHolder {


        private final TextView number;
        private final TextView date;
        private final TextView comment;
        private final View container;

        public TrapsViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.Number);
            date = itemView.findViewById(R.id.txtDate);
            comment = itemView.findViewById(R.id.comment);
            container = itemView.findViewById(R.id.container);

        }
    }
}
