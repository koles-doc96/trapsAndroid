package kolesnikov.ru.traps.gui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;
import kolesnikov.ru.traps.servers.Server;
import kolesnikov.ru.traps.gui.adapters.RecycleViewTrapsAdapter;
import kolesnikov.ru.traps.gui.parsers.TrapsParser;

public class FirstFragment extends Fragment  {

    private Server server = new Server(getActivity());
    private RecyclerView rvTraps;
    private List<Trap> traps = new ArrayList<>();
    private ProgressDialog dialog;
    private Handler handler;
    private RecycleViewTrapsAdapter recycleViewTrapsAdapter;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        getDataServer();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    hideDialog();
                    recycleViewTrapsAdapter = new RecycleViewTrapsAdapter(traps, getActivity());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    rvTraps.setLayoutManager(layoutManager);
                    rvTraps.setAdapter(recycleViewTrapsAdapter);
                }
            }
        };
        init(view);

    }
    private void hideDialog() {
        if(!Objects.requireNonNull(getActivity()).isFinishing() && !Objects.requireNonNull(getActivity()).isDestroyed() && dialog != null){
            dialog.dismiss();
        }
    }
    private void getDataServer(){
//        dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("Синхронизация данных...");
//        dialog.setIndeterminate(true);
//        dialog.setCancelable(false);
//        dialog.show();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String line = server.getTraps();
//                traps = TrapsParser.parseTraps(line);
//                handler.sendEmptyMessage(0);
//            }
//        }).start();
    }

    private void init(View view){
        rvTraps = view.findViewById(R.id.rv_traps);
    }

}
