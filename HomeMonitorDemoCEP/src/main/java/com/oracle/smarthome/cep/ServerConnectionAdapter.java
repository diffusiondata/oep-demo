package com.oracle.smarthome.cep;

import com.pushtechnology.diffusion.api.Credentials;
import com.pushtechnology.diffusion.api.ServerConnection;
import com.pushtechnology.diffusion.api.ServerConnectionListener;
import com.pushtechnology.diffusion.api.message.TopicMessage;
import com.pushtechnology.diffusion.api.topic.TopicStatus;

public class ServerConnectionAdapter implements ServerConnectionListener {

    public void messageFromServer(ServerConnection serverConnection, TopicMessage topicMessage) {
    }

    public void serverConnected(ServerConnection serverConnection) {
    }

    public void serverDisconnected(ServerConnection serverConnection) {
    }

    public void serverRejectedCredentials(ServerConnection serverConnection, Credentials credentials) {
    }

    public void serverTopicStatusChanged(ServerConnection serverConnection, String name, TopicStatus topicStatus) {
    }

}
