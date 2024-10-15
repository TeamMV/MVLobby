package dev.mv.lobby;

import org.bukkit.Location;
import org.joml.Matrix2d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PortalAction {
    private Location targetLocation;
    private List<ClickAction.Builder.Call> calls;

    public static class Builder<B> {
        private B parent;
        private Consumer<PortalAction> output;
        private Location targetLocation;
        private List<ClickAction.Builder.Call> calls;

        public Builder(B parent, Consumer<PortalAction> output) {
            this.parent = parent;
            this.output = output;
            calls = new ArrayList<>();
        }

        public Builder<B> withPlayerTp(Location targetLocation) {
            this.targetLocation = targetLocation;
            return this;
        }

        public Builder<B> withCall(String methodName, Class<?> clazz) {
            this.calls.add(new ClickAction.Builder.Call(methodName, clazz));
            return this;
        }

        public B build() {
            PortalAction action = new PortalAction();
            action.targetLocation = targetLocation;
            action.calls = calls;

            output.accept(action);
            return parent;
        }
    }
}
