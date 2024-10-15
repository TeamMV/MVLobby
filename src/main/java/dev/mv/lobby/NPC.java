package dev.mv.lobby;

import dev.mv.ptk.utils.display.DisplayName;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class NPC extends LobbyComponent {
    private Entity entity;

    private NPC(String id, Entity entity) {
        super(id);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class Builder {
        private String id;
        private Entity entity;
        private DisplayName name;
        private ClickAction clickAction;

        Builder(String id) {
            this.id = id;
        }

        public EntityBuilder withNewEntity() {
            return new EntityBuilder(this);
        }

        public Builder withExistingEntity(Entity entity) {
            this.entity = entity;
            return this;
        }

        public DisplayName.Builder<Builder> withDisplayName() {
            return new DisplayName.Builder<>(this, dsp -> name = dsp);
        }

        public ClickAction.Builder<Builder> withClickAction() {
            return new ClickAction.Builder<>(this, clk -> clickAction = clk);
        }

        public NPC build() {
            name.applyTo(entity);
            clickAction.applyTo(entity);
            return new NPC(id, entity);
        }

        public static class EntityBuilder {
            private Builder builder;
            private Location location;
            private EntityType entityType;
            private World world;

            private EntityBuilder(Builder builder) {
                this.builder = builder;
            }

            public EntityBuilder withPosition(Location location) {
                this.location = location;
                this.world = location.getWorld();
                return this;
            }

            public EntityBuilder withType(EntityType type) {
                this.entityType = type;
                return this;
            }

            public Builder build() {
                builder.entity = world.spawnEntity(location, entityType);
                return builder;
            }
        }
    }
}
