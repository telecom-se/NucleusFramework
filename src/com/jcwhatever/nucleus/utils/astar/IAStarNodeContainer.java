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

package com.jcwhatever.nucleus.utils.astar;

import javax.annotation.Nullable;

/**
 * A-Star node container.
 *
 * @see AStar
 * @see AStar#search
 */
public interface IAStarNodeContainer {

    /**
     * Reset the node container for reuse.
     */
    void reset();

    /**
     * Get the {@link IAStarNodeFactory} implementation to use
     * for the container.
     */
    IAStarNodeFactory getNodeFactory();

    /**
     * Get the number of open nodes.
     */
    int openSize();

    /**
     * Get the number of closed nodes.
     */
    int closeSize();

    /**
     * Open a node using the specified parent.
     *
     * @param parent  The parent node.
     * @param node    The node to open.
     */
    void open(@Nullable AStarNode parent, AStarNode node);

    /**
     * Determine if a node is open.
     *
     * @param node  The node to check.
     */
    boolean isOpen(AStarNode node);

    /**
     * Determine if a node is closed.
     *
     * @param node  The node to check.
     */
    boolean isClosed(AStarNode node);

    /**
     * Determine if a node is open or closed.
     *
     * @param node  The node to check.
     */
    boolean contains(AStarNode node);

    /**
     * Close the best open node and return.
     */
    @Nullable
    AStarNode closeBest();
}
