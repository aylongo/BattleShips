package com.example.project_battleships;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client extends AsyncTask<JSONObject, Void, JSONObject> {
    // Constants
    private final static String IP_ADDRESS = ""; // Host's IP Address
    private final static int PORT = 1225; // The port which will be used to transfer the data in it.
    private final static int SIZE = 1024;

    private JSONObject received;
    private JSONObject toSend;
    private Socket socket;
    private InputStreamReader inputStreamReader;
    private OutputStreamWriter outputStreamWriter;

    public Client(JSONObject object) {
        this.toSend = object;
    }

    private void send() {
        String data = this.toSend.toString();
        try {
            this.outputStreamWriter.write(data);
            this.outputStreamWriter.flush();
            System.out.println("Successfully sent data");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void receive() {
        try {
            char[] charBuffer = new char[SIZE];
            StringBuilder stringBuilder = new StringBuilder();
            this.inputStreamReader.read(charBuffer);
            stringBuilder.append(charBuffer);
            this.inputStreamReader.close();
            this.received = new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    protected JSONObject doInBackground(JSONObject... jsonObjects) {
        try {
            // Socket Setting
            this.socket = new Socket(IP_ADDRESS, PORT);
            this.inputStreamReader = new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8);
            this.outputStreamWriter = new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8);
            send();
            receive();
            this.socket.close();
            return this.received;
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return null;
    }
}