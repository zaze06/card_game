package me.alien.card.game.server;

import me.alien.card.game.util.DataPacket;
import me.alien.card.game.util.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static me.alien.card.game.Main.GSON;

public class Client {
    private final Socket socket;
    private final BufferedReader br;
    private final PrintWriter out;
    private final ArrayList<DataPacket> dataPackets = new ArrayList<>();
    private final ArrayList<String> outData = new ArrayList<>();
    private final InputDataThread inputDataThread;
    private final OutputDataThread outputDataThread;
    public Client(Socket socket) throws IOException {
        this.socket = socket;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        inputDataThread = new InputDataThread();
        inputDataThread.start();
        outputDataThread = new OutputDataThread();
        outputDataThread.start();

    }

    public void send(String data){
        outData.add(data);
    }

    public void send(DataPacket data){
        send(GSON.toJson(data));
    }

    private class InputDataThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    synchronized (dataPackets) {
                        String data = br.readLine();
                        if(data == null) continue;
                        dataPackets.add(GSON.fromJson(data, DataPacket.class));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class OutputDataThread extends Thread{
        @Override
        public void run() {
            while (true){
                synchronized (outData){
                    synchronized (dataPackets){
                        boolean ready = false;
                        for(DataPacket dataPacket : dataPackets){
                            if(dataPacket.getType() == Type.INFO){
                                if(dataPacket.getData() instanceof String str){
                                    if(str.equalsIgnoreCase("ready")){
                                        ready = true;
                                        dataPackets.remove(dataPacket);
                                        break;
                                    }
                                }
                            }
                            out.println(outData.get(0));
                            outData.remove(0);
                        }
                    }
                }
            }
        }
    }
}
