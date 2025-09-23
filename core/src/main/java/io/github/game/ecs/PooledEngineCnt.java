package io.github.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import lombok.Getter;

public class PooledEngineCnt extends PooledEngine {

    @Getter
    private int entityCount = 0;

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);
        this.entityCount++;
    }

    @Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);
        this.entityCount--;
    }

    @Override
    public void removeAllEntities() {
        super.removeAllEntities();
        this.entityCount = 0;
    }

    @Override
    public void clearPools() {
        super.clearPools();
        this.entityCount = 0;
    }

}
