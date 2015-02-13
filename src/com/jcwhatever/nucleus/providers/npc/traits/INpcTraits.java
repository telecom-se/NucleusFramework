/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.nucleus.providers.npc.traits;

import com.jcwhatever.nucleus.providers.npc.INpc;

import org.bukkit.entity.EntityType;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for a trait manager whose instances are paired
 * to an {@link com.jcwhatever.nucleus.providers.npc.INpc} instance.
 */
public interface INpcTraits {

    /**
     * Get the owning {@code INpc} instance.
     */
    INpc getNpc();

    /**
     * Determine if the NPC is invulnerable to damage.
     */
    boolean isInvulnerable();

    /**
     * Make the NPC invulnerable to damage.
     *
     * @return  Self for chaining.
     */
    INpcTraits invulnerable();

    /**
     * Make the NPC vulnerable to damage.
     *
     * @return  Self for chaining.
     */
    INpcTraits vulnerable();

    /**
     * Get the NPC entity type.
     */
    EntityType getType();

    /**
     * Set the NPC entity type.
     *
     * @param type  The entity type.
     *
     * @return  Self for chaining.
     */
    INpcTraits setType(EntityType type);

    /**
     * Get all of the NPC's traits.
     */
    Collection<NpcTrait> all();

    /**
     * Add a new trait to the NPC.
     *
     * @param name  The name of the trait type to add. The trait type must be registered with
     *              the NPC provider or the NPC's registry.
     *
     * @return  The instance of the trait or null if the trait type was not found.
     */
    @Nullable
    NpcTrait add(String name);

    /**
     * Add a new trait to the NPC.
     *
     * @param trait  The trait instance.
     *
     * @return  Self for chaining.
     */
    INpcTraits add(NpcTrait trait);

    /**
     * Get a trait from the NPC by trait type name.
     *
     * @param name  The name of the trait type.
     *
     * @return  The trait or null if not found.
     */
    @Nullable
    NpcTrait get(String name);

    /**
     * Determine if the NPC has a trait by the specified name.
     *
     * @param name  The name of the trait to check.
     */
    boolean has(String name);

    /**
     * Remove a trait from the NPC.
     *
     * @param name  The name of the trait to remove.
     *
     * @return  True if the trait was found and removed.
     */
    boolean remove(String name);
}