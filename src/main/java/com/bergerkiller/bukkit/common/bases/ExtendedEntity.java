package com.bergerkiller.bukkit.common.bases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityInsentientHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.SoundEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftSoundHandle;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTrackerEntry;

/**
 * Extends the methods provided by the Entity Bukkit class.
 *
 * @param <T> - type of Entity
 */
public class ExtendedEntity<T extends org.bukkit.entity.Entity> {

    public final LocationAbstract loc = new LocationAbstract() {
        public World getWorld() {
            return ExtendedEntity.this.getWorld();
        }

        public LocationAbstract setWorld(World world) {
            ExtendedEntity.this.setWorld(world);
            return this;
        }

        public double getX() {
            return ExtendedEntity.this.handle.getLocX();
        }

        public double getY() {
            return ExtendedEntity.this.handle.getLocY();
        }

        public double getZ() {
            return ExtendedEntity.this.handle.getLocZ();
        }

        public LocationAbstract setX(double x) {
            ExtendedEntity.this.handle.setLocX(x);
            return this;
        }

        public LocationAbstract setY(double y) {
            ExtendedEntity.this.handle.setLocY(y);
            return this;
        }

        public LocationAbstract setZ(double z) {
            ExtendedEntity.this.handle.setLocZ(z);
            return this;
        }

        public float getYaw() {
            return ExtendedEntity.this.handle.getYaw();
        }

        public float getPitch() {
            return ExtendedEntity.this.handle.getPitch();
        }

        public LocationAbstract setYaw(float yaw) {
            ExtendedEntity.this.handle.setYaw(yaw);
            return this;
        }

        public LocationAbstract setPitch(float pitch) {
            ExtendedEntity.this.handle.setPitch(pitch);
            return this;
        }
    };
    public final LocationAbstract last = new LocationAbstract() {
        public World getWorld() {
            return ExtendedEntity.this.getWorld();
        }

        public LocationAbstract setWorld(World world) {
            return this;
        }

        public double getX() {
            return ExtendedEntity.this.handle.getLastX();
        }

        public double getY() {
            return ExtendedEntity.this.handle.getLastY();
        }

        public double getZ() {
            return ExtendedEntity.this.handle.getLastZ();
        }

        public LocationAbstract setX(double x) {
            ExtendedEntity.this.handle.setLastX(x);
            return this;
        }

        public LocationAbstract setY(double y) {
            ExtendedEntity.this.handle.setLastY(y);
            return this;
        }

        public LocationAbstract setZ(double z) {
            ExtendedEntity.this.handle.setLastZ(z);
            return this;
        }

        public float getYaw() {
            return ExtendedEntity.this.handle.getLastYaw();
        }

        public float getPitch() {
            return ExtendedEntity.this.handle.getLastPitch();
        }

        public LocationAbstract setYaw(float yaw) {
            ExtendedEntity.this.handle.setLastYaw(yaw);
            return this;
        }

        public LocationAbstract setPitch(float pitch) {
            ExtendedEntity.this.handle.setLastPitch(pitch);
            return this;
        }
    };
    public final VectorAbstract vel = new VectorAbstract() {
        public double getX() {
            return ExtendedEntity.this.handle.getMotX();
        }

        public double getY() {
            return ExtendedEntity.this.handle.getMotY();
        }

        public double getZ() {
            return ExtendedEntity.this.handle.getMotZ();
        }

        public VectorAbstract setX(double x) {
            ExtendedEntity.this.handle.setMotX(x);
            return this;
        }

        public VectorAbstract setY(double y) {
            ExtendedEntity.this.handle.setMotY(y);
            return this;
        }

        public VectorAbstract setZ(double z) {
            ExtendedEntity.this.handle.setMotZ(z);
            return this;
        }
    };

    /**
     * The minimum x/y/z velocity distance, above which the entity is considered
     * to be moving
     */
    public static final double MIN_MOVE_SPEED = 0.001;
    /**
     * The internally-stored Bukkit Entity instance
     */
    protected T entity;
    /**
     * A reference to the internal net.minecraft.server.Entity (or its extension)
     */
    protected EntityHandle handle = new EntityHandle();

    /**
     * Constructs a new Extended Entity with the initial entity specified
     *
     * @param entity to use
     */
    public ExtendedEntity(T entity) {
        setEntity(entity);
    }

    /**
     * Sets the backing Bukkit Entity
     *
     * @param entity to set to
     */
    protected void setEntity(T entity) {
        this.entity = entity;
        this.handle = EntityHandle.fromBukkit(entity);
    }

    /**
     * Gets the backing Bukkit Entity
     *
     * @return entity
     */
    public T getEntity() {
        return entity;
    }

    /**
     * Gets the Entity handle
     *
     * @return the Entity handle
     */
    public Object getHandle() {
        return handle.getRaw();
    }

    /**
     * Gets the Entity handle, and automatically casts it to a given type
     *
     * @param type to cast to
     * @return the NMS entity handle, cast to the given type
     */
    public <H> H getHandle(Class<H> type) {
        return CommonUtil.tryCast(handle.getRaw(), type);
    }

    public int getChunkX() {
        return this.handle.getChunkX();
    }

    public void setChunkX(int value) {
        this.handle.setChunkX(value);
    }

    public int getChunkY() {
        return this.handle.getChunkY();
    }

    public void setChunkY(int value) {
        this.handle.setChunkY(value);
    }

    public int getChunkZ() {
        return this.handle.getChunkZ();
    }

    public void setChunkZ(int value) {
        this.handle.setChunkZ(value);
    }

    /**
     * Obtains the Entity head rotation angle, or 0.0 if this Entity has no
     * head.
     *
     * @return Head rotation, if available
     */
    public float getHeadRotation() {
        return this.handle.getHeadRotation();
    }

    public double getMovedX() {
        return this.handle.getLocX() - this.handle.getLastX();
    }

    public double getMovedY() {
        return this.handle.getLocY() - this.handle.getLastY();
    }

    public double getMovedZ() {
        return this.handle.getLocZ() - this.handle.getLastZ();
    }

    public boolean hasMovedHorizontally() {
        return Math.abs(this.getMovedX()) > MIN_MOVE_SPEED || Math.abs(this.getMovedZ()) > MIN_MOVE_SPEED;
    }

    public boolean hasMovedVertically() {
        return Math.abs(this.getMovedY()) > MIN_MOVE_SPEED;
    }

    public boolean hasMoved() {
        return hasMovedHorizontally() || hasMovedVertically();
    }

    public double getMovedXZDistance() {
        return MathUtil.length(getMovedX(), getMovedZ());
    }

    public double getMovedXZDistanceSquared() {
        return MathUtil.lengthSquared(getMovedX(), getMovedZ());
    }

    public double getMovedDistance() {
        return MathUtil.length(getMovedX(), getMovedY(), getMovedZ());
    }

    public double getMovedDistanceSquared() {
        return MathUtil.lengthSquared(getMovedX(), getMovedY(), getMovedZ());
    }

    public boolean isMovingHorizontally() {
        return vel.x.abs() > MIN_MOVE_SPEED || vel.z.abs() > MIN_MOVE_SPEED;
    }

    public boolean isMovingVertically() {
        return vel.y.abs() > MIN_MOVE_SPEED;
    }

    public boolean isMoving() {
        return isMovingHorizontally() || isMovingVertically();
    }

    public void setWorld(World world) {
        this.handle.setWorld(WorldHandle.createHandle(Conversion.toWorldHandle.convert(world)));
        this.handle.setDimension(this.handle.getWorld().getWorldData().getType().getDimension());
    }

    public void setDead(boolean dead) {
        this.handle.setDead(dead);
    }

    public float getHeight() {
        return this.handle.getLength();
    }

    public void setHeight(float height) {
        this.handle.setLength(height);
    }

    public float getLength() {
        return this.handle.getLength();
    }

    public void setLength(float length) {
        this.handle.setLength(length);
    }

    public boolean isOnGround() {
        return this.handle.isOnGround();
    }

    public void setOnGround(boolean onGround) {
        this.handle.setOnGround(onGround);
    }

    public boolean isPositionChanged() {
        return this.handle.isPositionChanged();
    }

    public void setPositionChanged(boolean changed) {
        this.handle.setPositionChanged(changed);
    }

    public boolean isVelocityChanged() {
        return this.handle.isVelocityChanged();
    }

    public void setVelocityChanged(boolean changed) {
        this.handle.setVelocityChanged(changed);
    }

    /**
     * Gets whether this Entity is stored in a loaded chunk
     *
     * @return True if loaded, False if not
     */
    public boolean isInLoadedChunk() {
        return this.handle.isLoaded();
    }

    /**
     * Gets whether the entity is hitting something, like an Entity or Block. If
     * this returns True, then the Entity is unable to move freely.
     *
     * @return True if movement is impaired, False if not
     */
    public boolean isMovementImpaired() {
        return this.handle.isHorizontalMovementImpaired();
    }

    /**
     * Sets whether the entity is hitting something, and as a result can not
     * move freely
     *
     * @param impaired state to set to
     */
    public void setMovementImpaired(boolean impaired) {
        this.handle.setHorizontalMovementImpaired(impaired);
    }

    public Random getRandom() {
        return this.handle.getRandom();
    }

    /**
     * Plays a sound for the Entity, but with a slightly random pitch
     *
     * @param sound to play
     * @param volume to play at
     * @param pitch (average) to play at
     */
    public void makeRandomSound(Sound sound, float volume, float pitch) {
        makeRandomSound(CraftSoundHandle.getSoundName(sound), volume, pitch);
    }

    /**
     * Plays a sound for the Entity, but with a slightly random pitch
     *
     * @param soundName to play
     * @param volume to play at
     * @param pitch (average) to play at
     */
    public void makeRandomSound(String soundName, float volume, float pitch) {
        final Random rand = getRandom();
        makeSound(soundName, volume, MathUtil.clamp(pitch + 0.4f * (rand.nextFloat() - rand.nextFloat()), 0.0f, 1.0f));
    }

    public void makeSound(Sound sound, float volume, float pitch) {
        makeSound(CraftSoundHandle.getSoundName(sound), volume, pitch);
    }

    public void makeSound(String soundName, float volume, float pitch) {
        Object soundEffectKey = MinecraftKeyHandle.createNew(soundName).getRaw();
        SoundEffectHandle soundEffectHandle = SoundEffectHandle.createHandle(SoundEffectHandle.REGISTRY.get(soundEffectKey));
        if (soundEffectHandle != null) {
            handle.makeSound(soundEffectHandle, volume, pitch);
        }
    }

    public void makeStepSound(org.bukkit.block.Block block) {
        makeStepSound(block.getX(), block.getY(), block.getZ(), block.getType());
    }

    public void makeStepSound(int blockX, int blockY, int blockZ, Material type) {
        this.handle.playStepSound(new IntVector3(blockX, blockY, blockZ), BlockData.fromMaterial(type));
    }

    @Deprecated
    public void makeStepSound(int blockX, int blockY, int blockZ, int typeId) {
        makeStepSound(blockX, blockY, blockZ, Material.getMaterial(typeId));
    }

    public List<MetadataValue> getMetadata(String arg0) {
        return entity.getMetadata(arg0);
    }

    public boolean hasMetadata(String arg0) {
        return entity.hasMetadata(arg0);
    }

    public void removeMetadata(String arg0, Plugin arg1) {
        entity.removeMetadata(arg0, arg1);
    }

    public void setMetadata(String arg0, MetadataValue arg1) {
        entity.setMetadata(arg0, arg1);
    }

    public boolean eject() {
        return entity.eject();
    }

    public int getEntityId() {
        return entity.getEntityId();
    }

    public float getFallDistance() {
        return entity.getFallDistance();
    }

    public int getFireTicks() {
        return entity.getFireTicks();
    }

    public EntityDamageEvent getLastDamageCause() {
        return entity.getLastDamageCause();
    }

    public Location getLocation() {
        return entity.getLocation();
    }

    public Location getLastLocation() {
        return last.toLocation();
    }

    public Location getLocation(Location arg0) {
        return entity.getLocation(arg0);
    }

    public int getMaxFireTicks() {
        return entity.getMaxFireTicks();
    }

    public void setFootLocation(double x, double y, double z, float yaw, float pitch) {
        handle.setPositionRotation(x, y, z, yaw, pitch);
    }

    public void setLocation(double x, double y, double z, float yaw, float pitch) {
        handle.setLocation(x, y, z, yaw, pitch);
    }

    /**
     * Sets the yaw and pitch rotation, while ensuring that there are no
     * 360-turns
     *
     * @param yaw to set to
     * @param pitch to set to
     */
    public void setRotation(float yaw, float pitch) {
        this.handle.setRotation(yaw, pitch);
    }

    public void setPosition(double x, double y, double z) {
        handle.setPosition(x, y, z);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double radius) {
        return this.getNearbyEntities(radius, radius, radius);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double radX, double radY, double radZ) {
        return WorldUtil.getNearbyEntities(this.getEntity(), radX, radY, radZ);
    }

    /**
     * Use getpassengers instead!
     */
    @Deprecated()
	public org.bukkit.entity.Entity getPassenger() {
        return entity.getPassenger();
    }

    /**
     * Use getPlayerPassengers() instead!
     */
    @Deprecated
    public Player getPlayerPassenger() {
        return CommonUtil.tryCast(getPassenger(), Player.class);
    }

    /**
     * Retrieves a list of passenger entities
     * 
     * @return passenger entity list
     */
    public List<org.bukkit.entity.Entity> getPassengers() {
        if (com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.getPassengers.isAvailable()) {
            return com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.getPassengers.invoke(entity);
        }

        // Fallback for older version of Bukkit
        @SuppressWarnings("deprecation")
        org.bukkit.entity.Entity p = entity.getPassenger();
        if (p == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(p);
        }
    }

    /**
     * Retrieves a list of passengers that are players
     * 
     * @return list of player passengers
     */
    public List<Player> getPlayerPassengers() {
        List<org.bukkit.entity.Entity> passengers = this.getPassengers();
        List<Player> playerPassengers = new ArrayList<Player>(passengers.size());
        for (org.bukkit.entity.Entity entity : passengers) {
            if (entity instanceof Player) {
                playerPassengers.add((Player) entity);
            }
        }
        return playerPassengers;
    }

    public boolean hasPassenger() {
        return !LogicUtil.nullOrEmpty(handle.getPassengers());
    }

    public boolean hasPlayerPassenger() {
        List<EntityHandle> passengers = handle.getPassengers();
        if (passengers != null) {
            for (EntityHandle handle : passengers) {
                if (EntityPlayerHandle.T.isAssignableFrom(handle.getRaw())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets whether this type of Entity is a type of Vehicle, allowing entities
     * to enter it
     *
     * @return True if this is a Vehicle, False if not
     */
    public boolean isVehicle() {
        return entity instanceof Vehicle;
    }

    public Server getServer() {
        return entity.getServer();
    }

    public int getTicksLived() {
        return entity.getTicksLived();
    }

    public EntityType getType() {
        return entity.getType();
    }

    public UUID getUniqueId() {
        return entity.getUniqueId();
    }

    public org.bukkit.entity.Entity getVehicle() {
        return entity.getVehicle();
    }

    public Vector getVelocity() {
        return entity.getVelocity();
    }

    /**
     * Checks whether this Entity is spawned in the world
     *
     * @return True if the Entity is spawned, False if not
     */
    public boolean isSpawned() {
        return handle != null && handle.getWorld() != null && handle.getWorld().getEntityList().contains(handle);
    }

    public World getWorld() {
        WorldHandle wHandle = handle.getWorld();
        return wHandle != null ? wHandle.getWorld() : null;
    }

    public boolean isDead() {
        return entity.isDead();
    }

    public boolean isEmpty() {
        return entity.isEmpty();
    }

    public boolean isInsideVehicle() {
        return entity.isInsideVehicle();
    }

    /**
     * Gets the Entity that is holding this Entity by a leash. If this Entity
     * does not support leashing, or the Entity is not on a leash, null is
     * returned instead.
     *
     * @return Leash holder
     */
    public org.bukkit.entity.Entity getLeashHolder() {
        if (handle.isInstanceOf(EntityInsentientHandle.T)) {
            EntityInsentientHandle insHandle = EntityInsentientHandle.createHandle(handle.getRaw());
            EntityHandle holder = insHandle.getLeashHolder();
            if (holder != null) {
                return holder.toBukkit();
            }
        }
        return null;
    }

    public boolean isValid() {
        return entity.isValid();
    }

    /**
     * Gets whether the Entity is submerged in water (subsequently, extinguising
     * any fire). This method does not update the state, it merely reads it.
     *
     * @return True if this Entity is in water, False if not
     */
    public boolean isInWater() {
        return isInWater(false);
    }

    /**
     * Gets whether the Entity is submerged in water (subsequently, extinguising
     * any fire)
     *
     * @param update option: True to update this state by checking for water
     * blocks nearby
     * @return True if this Entity is in water, False if not
     */
    public boolean isInWater(boolean update) {
        if (update) {
            return this.handle.isInWaterUpdate();
        } else {
            return this.handle.isInWater();
        }
    }

    /**
     * Sets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     *
     * @param allow whether to allow portal entering
     */
    public void setAllowTeleportation(boolean allow) {
        EntityUtil.setAllowTeleportation(entity, allow);
    }

    /**
     * Gets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     */
    public boolean getAllowTeleportation() {
        return EntityUtil.getAllowTeleportation(entity);
    }

    /**
     * Gets the entity portal enter cooldown ticks
     *
     * @return entity cooldown ticks
     */
    public int getPortalCooldown() {
        return EntityUtil.getPortalCooldown(entity);
    }

    /**
     * Sets the entity portal enter cooldown ticks
     *
     * @param cooldownTicks to set to
     */
    public void setPortalCooldown(int cooldownTicks) {
        EntityUtil.setPortalCooldown(entity, cooldownTicks);
    }

    /**
     * Gets the maximum portal cooldown ticks. This is the value applied right
     * after entering a portal.
     *
     * @return entity maximum portal cooldown ticks
     */
    public int getPortalCooldownMaximum() {
        return EntityUtil.getPortalCooldownMaximum(entity);
    }

    public boolean leaveVehicle() {
        return entity.leaveVehicle();
    }

    public void playEffect(EntityEffect arg0) {
        entity.playEffect(arg0);
    }

    public void remove() {
        entity.remove();
    }

    public void setFallDistance(float arg0) {
        entity.setFallDistance(arg0);
    }

    public void setFireTicks(int arg0) {
        entity.setFireTicks(arg0);
    }

    public void setLastDamageCause(EntityDamageEvent arg0) {
        entity.setLastDamageCause(arg0);
    }

    /**
     * Deprecated! Use add/remove passenger instead!
     */
    @Deprecated
	public boolean setPassenger(org.bukkit.entity.Entity passenger) {
        return passenger == null ? entity.eject() : entity.setPassenger(passenger);
    }

    /**
     * Checks whether a certain entity is a passenger of this entity
     * 
     * @param passenger to check
     * @return true if a passenger, false if not
     */
    public boolean isPassenger(org.bukkit.entity.Entity passenger) {
        for (org.bukkit.entity.Entity currPassenger : this.getPassengers()) {
            if (currPassenger.getUniqueId().equals(passenger.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a passenger to this Vehicle, while throwing possible events. If the
     * entering didn't happen, False is returned.
     *
     * @param passenger to add
     * @return True if the passenger was successfully set, False if not
     */
	public boolean addPassenger(org.bukkit.entity.Entity passenger) {
	    if (com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.addPassenger.isAvailable()) {
	        return com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.addPassenger.invoke(entity, passenger);
	    } else {
	        return entity.setPassenger(passenger);
	    }
	}

    /**
     * Removes a passenger from this Vehicle, while throwing possible events. If the
     * exiting didn't happen, False is returned.
     *
     * @param passenger to add
     * @return True if the passenger was successfully removed, False if not
     */
    public boolean removePassenger(org.bukkit.entity.Entity passenger) {
        if (com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.removePassenger.isAvailable()) {
            return com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.removePassenger.invoke(entity, passenger);
        } else if (entity.getPassenger() == passenger) {
            return entity.setPassenger(null);
        } else {
            return false;
        }
    }

    /**
     * Use setPassengersSilent instead!
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void setPassengerSilent(org.bukkit.entity.Entity newPassenger) {
        setPassengersSilent((newPassenger == null) ? Collections.EMPTY_LIST : Arrays.asList(newPassenger));
    }

    /**
     * Sets the passengers of this Vehicle without raising any events
     *
     * @param newPassenger to set to
     */
    public void setPassengersSilent(List<org.bukkit.entity.Entity> newPassengers) {
        final EntityHandle handle = this.handle;

        // Generate a difference view between the expected list of passengers, and the current
        List<EntityHandle> removedPassengers = new ArrayList<EntityHandle>(handle.getPassengers().size());
        List<org.bukkit.entity.Entity> keptPassengers = new ArrayList<org.bukkit.entity.Entity>(newPassengers.size());
        for (EntityHandle oldPassenger : handle.getPassengers()) {
            boolean found = false;
            for (org.bukkit.entity.Entity p : newPassengers) {
                if (oldPassenger.getRaw() == Conversion.toEntityHandle.convert(p)) {
                    found = true;
                    keptPassengers.add(p);
                    break;
                }
            }
            if (!found) {
                removedPassengers.add(oldPassenger);
            }
        }

        // Update network info with the removed entities
        /*
        {
            CommonPacket packet = NMSPacketTypes.OUT_MOUNT.newInstance(entity, keptPassengers);
            PacketUtil.broadcastEntityPacket(entity, packet);
        }
        */

        // Remove vehicle information and passenger information for all removed passengers
        for (EntityHandle passenger : removedPassengers) {
            passenger.setVehicle(null);
            handle.getPassengers().remove(passenger);
        }

        // Add new passengers as required
        List<EntityHandle> currPassengers = handle.getPassengers();
        for (org.bukkit.entity.Entity p : newPassengers) {
            EntityHandle passengerHandle = EntityHandle.fromBukkit(p);
            if (!currPassengers.contains(passengerHandle)) {
                currPassengers.add(passengerHandle);
                passengerHandle.setVehicle(handle);

                // Send mount packet
                CommonPacket packet = PacketType.OUT_MOUNT.newInstance(entity, keptPassengers);
                PacketUtil.broadcastEntityPacket(entity, packet);
            }
        }

        CommonPacket packet = PacketType.OUT_MOUNT.newInstanceHandles(entity, currPassengers);
        PacketUtil.broadcastEntityPacket(entity, packet);

        // Synchronize entity tracker of the vehicle to make sure it does not try to synchronize a second time
        Object entry = WorldUtil.getTracker(entity.getWorld()).getEntry(entity);
        if (entry != null) {
            NMSEntityTrackerEntry.passengers.set(entry, newPassengers);
        }
    }

    /**
     * Sends a packet to all nearby players in view of this Entity
     *
     * @param packet to send
     */
    public void sendPacketNearby(CommonPacket packet) {
        WorldUtil.getTracker(getWorld()).sendPacket(entity, packet);
    }

    public void setTicksLived(int arg0) {
        entity.setTicksLived(arg0);
    }

    public void setVelocity(Vector arg0) {
        entity.setVelocity(arg0);
    }

    public void setVelocity(double motX, double motY, double motZ) {
        handle.setMotX(motX);
        handle.setMotY(motY);
        handle.setMotZ(motZ);
    }

    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, TeleportCause cause) {
        return entity.teleport(location, cause);
    }

    public boolean teleport(org.bukkit.entity.Entity destination) {
        return teleport(destination.getLocation());
    }

    public boolean teleport(org.bukkit.entity.Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    /**
     * Spawns an item as if dropped by this Entity
     *
     * @param material of the item to drop
     * @param amount of the material to drop
     * @param force to drop at
     * @return the dropped Item
     */
    public Item spawnItemDrop(Material material, int amount, float force) {
        return handle.dropItem(material, amount, force);
    }

    /**
     * Spawns an item as if dropped by this Entity
     *
     * @param item to drop
     * @param force to drop at
     * @return the dropped Item
     */
    public Item spawnItemDrop(org.bukkit.inventory.ItemStack item, float force) {
        return handle.dropItemStack(item, force);
    }

    /**
     * Obtains the DataWatcher to update and keep track of Entity metadata
     *
     * @return Entity meta data watcher
     */
    public DataWatcher getMetaData() {
        return handle.getDataWatcher();
    }

    /**
     * Gets the datawatcher used to synchronize certain entity properties with the viewing clients
     * 
     * @return datawatcher
     */
    public DataWatcher getDataWatcher() {
        return handle.getDataWatcher();
    }

    /**
     * Creates a helper DataWatcher Item class for accessing a Metadata field for this ExtendedEntity
     * 
     * @param key for the metadata
     * @return DataWatcher Item
     */
    public <V> DataWatcher.EntityItem<V> getDataItem(DataWatcher.Key<V> key) {
        return new DataWatcher.EntityItem<V>(this, key);
    }

    /**
     * Reads an NMS field value
     * 
     * @param field to read
     * @return field value
     */
    public <V> V read(FieldAccessor<V> field) {
        return field.get(getHandle());
    }

    /**
     * Writes an NMS field value
     * 
     * @param field to write
     * @param value to write
     */
    public <V> void write(FieldAccessor<V> field, V value) {
        field.set(getHandle(), value);
    }

    @Override
    public int hashCode() {
        return entity == null ? super.hashCode() : entity.hashCode();
    }

    @Override
    public String toString() {
        return entity == null ? "null" : entity.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ExtendedEntity) {
            return ((ExtendedEntity<?>) object).entity == this.entity;
        } else {
            return false;
        }
    }
}
