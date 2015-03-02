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

package com.jcwhatever.nucleus.views.workbench;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.EventListener;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.items.ItemFilterManager;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.views.ViewOpenReason;

import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * A workbench view that can allow or deny specific items to be crafted.
 */
public class FilteredWorkbenchView extends WorkbenchView {

    @Localizable
    static final String _NOT_CRAFTABLE_LORE = "{RED}Not craftable here.";
    @Localizable static final String _NOT_CRAFTABLE_CHAT = "{RED}You can't craft this item here.";

    private static AnvilEventListener _eventListener;
    private static Map<InventoryView, FilteredWorkbenchView> _viewMap = new WeakHashMap<>(10);

    private final ItemFilterManager _filter;

    /**
     * Constructor.
     *
     * @param plugin         The views owning plugin.
     * @param filterManager  The filter manager used to allow or deny specific items.
     */
    public FilteredWorkbenchView(Plugin plugin, ItemFilterManager filterManager) {
        super(plugin);

        _filter = filterManager;
    }

    /**
     * Get the views item filter manager.
     */
    @Nullable
    public ItemFilterManager getFilterManager() {
        return _filter;
    }

    @Override
    protected boolean openView(ViewOpenReason reason) {
        if (super.openView(reason)) {

            if (_eventListener == null) {
                _eventListener = new AnvilEventListener(Nucleus.getPlugin());
                Nucleus.getEventManager().register(_eventListener);
            }

            InventoryView inventory = getInventoryView();
            if (inventory == null)
                throw new AssertionError();

            _viewMap.put(inventory, this);

            return true;
        }
        return false;
    }

    /**
     * Anvil event listener
     */
    private static class AnvilEventListener extends EventListener {

        public AnvilEventListener(Plugin plugin) {
            super(plugin);
        }

        @EventMethod
        private void onPrepareItemCraft(PrepareItemCraftEvent event) {

            FilteredWorkbenchView workbench = _viewMap.get(event.getView());
            if (workbench == null)
                return;

            ItemStack result = event.getRecipe().getResult();

            ItemFilterManager filter = workbench.getFilterManager();
            if (filter == null)
                return;

            if (!filter.isValid(result)) {
                InventoryView invView = event.getView();
                if (invView != null) {
                    ItemStack stack = result.clone();
                    ItemStackUtils.setLore(stack, NucLang.get(workbench.getPlugin(), _NOT_CRAFTABLE_LORE));
                    invView.setItem(0, stack);
                }
            }
        }

        @EventMethod
        private void onCraftItem(CraftItemEvent event) {

            FilteredWorkbenchView workbench = _viewMap.get(event.getView());
            if (workbench == null)
                return;

            ItemFilterManager filter = workbench.getFilterManager();
            if (filter == null)
                return;

            ItemStack result = event.getRecipe().getResult();

            if (!filter.isValid(result)) {
                tellNoCraftMessage(workbench);
                event.setCancelled(true);
            }
        }

        @EventMethod
        private void onNucleusDisable(PluginDisableEvent event) {
            if (event.getPlugin() == Nucleus.getPlugin())
                _eventListener = null;
        }

        private void tellNoCraftMessage(FilteredWorkbenchView view) {
            NucMsg.tellNoSpam(view.getPlugin(), view.getPlayer(),
                    NucLang.get(view.getPlugin(), _NOT_CRAFTABLE_CHAT));
        }
    }
}
