/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.events.sounds;


import com.jcwhatever.bukkit.generic.sounds.ResourceSound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayResourceSoundEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Player _player;
	private ResourceSound _sound;
	private boolean _isCancelled;
	private Location _location;
	private float _volume;
	
	public PlayResourceSoundEvent(Player p, ResourceSound sound, Location location, float volume) {
		_player = p;
		_sound = sound;
		_location = location;
		_volume = volume;
	}
	
	public Player getPlayer() {
		return _player;
	}
	
	public ResourceSound getResourceSound() {
		return _sound;
	}
	
	public Location getLocation() {
		return _location;
	}
	
	public float getVolume() {
		return _volume;
	}
	
	public void setResourceSound(ResourceSound sound) {
		_sound = sound;
	}
	
	public void setLocation(Location location) {
		_location = location;
	}
	
	public void setVolume(float volume) {
		_volume = volume;
	}
	
	public boolean isCancelled() {
		return _isCancelled;
	}
	
	public void setIsCancelled(boolean isCancelled) {
		_isCancelled = isCancelled;
	}
	 
	@Override
    public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}