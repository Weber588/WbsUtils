package wbs.utils.util.entities.state;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.exceptions.CyclicDependencyException;

import java.util.*;

class EntityStateGraph<T extends Entity> {

    private final List<EntityStateNode> nodes = new LinkedList<>();

    public void addTracker(EntityState<? super T> toTrack) {
        List<EntityStateNode> nodes = new LinkedList<>(this.nodes);
        for (EntityStateNode node : nodes) {
            if (node.entityState.getClass() == toTrack.getClass()) {
                this.nodes.remove(node);
            }
        }

        this.nodes.add(new EntityStateNode(toTrack));
    }

    public void addIfAbsent(EntityState<? super T> toTrack) {
        for (EntityStateNode node : nodes) {
            if (node.entityState.getClass() == toTrack.getClass()) {
                return;
            }
        }

        nodes.add(new EntityStateNode(toTrack));
    }

    public void restoreState(T target) {
        for (EntityStateNode toRestore : getRestoreOrder()) {
            toRestore.entityState.restoreState(target);
        }
    }

    public void captureState(T target) {
        List<EntityStateNode> captureOrder = getRestoreOrder();
        Collections.reverse(captureOrder);
        for (EntityStateNode toCapture : captureOrder) {
            toCapture.entityState.captureState(target);
        }
    }

    private List<EntityStateNode> getRestoreOrder() {
        List<EntityStateNode> restoreOrder = new LinkedList<>();
        if (nodes.size() == 0) return restoreOrder;

        // Resolve recursively, but using each node as a root because
        // the graph is disconnected.
        for (EntityStateNode node : nodes) {
            resolve(node, restoreOrder, new LinkedList<>());
        }

        return restoreOrder;
    }

    private void resolve(EntityStateNode node, List<EntityStateNode> resolved, List<EntityStateNode> traversed) {
        traversed.add(node);

        for (EntityStateNode restoreAfter : node.restoreAfter) {
            if (!resolved.contains(restoreAfter)) {
                if (traversed.contains(restoreAfter)) {
                    throw new CyclicDependencyException("Circular load order: " +
                            node.entityState.getClass().getSimpleName() + " before " +
                            restoreAfter.entityState.getClass().getSimpleName());
                }
                resolve(restoreAfter, resolved, traversed);
            }
        }

        resolved.add(node);
    }

    private class EntityStateNode {
        @NotNull
        private final EntityState<? super T> entityState;
        private final Set<EntityStateNode> restoreAfter = new HashSet<>();

        public EntityStateNode(@NotNull EntityState<? super T> entityState) {
            this.entityState = entityState;
            for (EntityStateNode node : nodes) {
                for (Class<? extends EntityState<?>> clazz : entityState.restoreAfter()) {
                    if (clazz.isInstance(node.entityState)) {
                        restoreAfter.add(node);
                    }
                }
            }
        }
    }
}
