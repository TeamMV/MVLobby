package dev.mv.lobby.components;

public abstract class LobbyComponent {
    protected String id;

    LobbyComponent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}