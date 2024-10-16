package dev.mv.lobby.components;

import org.bukkit.Location;

public class Portal extends LobbyComponent {
    private Location location;
    private PortalAction action;

    private Portal(String id) {
        super(id);
    }

    public static Builder create(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private String id;
        private Location location;
        private PortalAction action;

        private Builder(String id) {
            this.id = id;
        }

        public Builder withPortal(Location location) {
            this.location = location;
            return this;
        }

        public PortalAction.Builder<Builder> withPortalAction() {
            return new PortalAction.Builder<>(this, action -> this.action = action);
        }

        public Portal build() {
            Portal portal = new Portal(id);
            portal.location = location;
            portal.action = action;
            return portal;
        }
    }
}
