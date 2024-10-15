package dev.mv.lobby;

import dev.mv.ptk.gui.InventoryInterface;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClickAction {
    private InventoryInterface ui;
    private List<Builder.Call> calls;
    private long currentCallback;

    private ClickAction(InventoryInterface ui) {
        this.ui = ui;
        calls = new ArrayList<>();
    }

    public void applyTo(Entity entity) {
        currentCallback = PluginListener.interactionCallback(e -> {
            if (e.getRightClicked().getEntityId() == entity.getEntityId()) {
                if (ui != null) {
                    ui.open(e.getPlayer());
                }
                calls.forEach(c -> c.invoke(e.getPlayer()));
            }
        });
    }

    public static class Builder<B> {
        private InventoryInterface ui;
        private List<Call> calls;

        private final Consumer<ClickAction> out;
        private B parent;

        public Builder(B parent, Consumer<ClickAction> out) {
            this.parent = parent;
            this.out = out;
            calls = new ArrayList<>();
        }

        public Builder<B> withInventoryOpen(InventoryInterface ui) {
            this.ui = ui;
            return this;
        }

        public Builder<B> withCall(String methodName, Class<?> clazz) {
            this.calls.add(new Call(methodName, clazz));
            return this;
        }

        public static class Call {
            private Method method;

            public Call(String methodName, Class<?> clazz) {
                try {
                    method = clazz.getMethod(methodName, Player.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }

            public void invoke(Player player) {
                try {
                    method.invoke(null, player);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        public B build() {
            ClickAction action = new ClickAction(ui);
            out.accept(action);
            action.calls = calls;
            return parent;
        }
    }
}
