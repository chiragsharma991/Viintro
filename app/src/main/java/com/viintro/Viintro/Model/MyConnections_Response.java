package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasai on 03/03/17.
 */

public class MyConnections_Response extends JSONObject{

    private int connection_count;
    private ArrayList<Connections> connections;


    public int getConnection_count() {
        return connection_count;
    }

    public void setConnection_count(int connection_count) {
        this.connection_count = connection_count;
    }

    public ArrayList<Connections> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connections> connections) {
        this.connections = connections;
    }
}
