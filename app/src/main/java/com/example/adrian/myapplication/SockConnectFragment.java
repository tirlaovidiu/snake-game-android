package com.example.adrian.myapplication;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adrian.myapplication.socket.ConnectionStatus;
import com.example.adrian.myapplication.socket.SingletonClient;
import com.example.adrian.myapplication.socket.WebSocketClient;

import static android.os.SystemClock.sleep;
import static com.example.adrian.myapplication.util.Constants.SERVER_ADDRESS;


/**
 * A simple {@link Fragment} subclass.
 */
public class SockConnectFragment extends Fragment {

    private TextView helloTextView;

    public SockConnectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sock_connect, container, false);

        helloTextView = (TextView) rootView.findViewById(R.id.hello);
        helloTextView.setText("Working");


        return rootView;
    }


}
