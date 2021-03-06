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

package com.jcwhatever.nucleus.internal.providers.economy;

import com.google.common.collect.MapMaker;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IBank;
import com.jcwhatever.nucleus.providers.economy.IBankEconomyProvider;
import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.providers.economy.IEconomyTransaction;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * NucleusFramework's simple economy provider
 */
public final class NucleusEconomyProvider extends Provider implements IBankEconomyProvider {

    public static final String NAME = "NucleusEconomy";

    private final IDataNode _globalAccountNode;

    private final Map<UUID, NucleusAccount> _accounts =
            new MapMaker().weakValues().concurrencyLevel(2).initialCapacity(100).makeMap();

    private final Map<String, IBank> _banks = new HashMap<>(25);
    private final NucleusCurrency _currency;

    private final Object _accountSync = new Object();
    private final Object _bankSync = new Object();

    public NucleusEconomyProvider(Plugin plugin) {
        PreCon.notNull(plugin);

        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default economy provider."));


        _globalAccountNode = new YamlDataNode(plugin, getDataPath("global"));

        _globalAccountNode.load();

        IDataNode dataNode = getDataNode();
        dataNode.setDefaultsSaved(true);
        String singular = dataNode.getString("currency-singular", "Dollar");
        String plural = dataNode.getString("currency-plural", "Dollars");
        String formatTemplate = dataNode.getString("format-template", "{0: amount} {1: currencyName}");
        String decimalFormat = dataNode.getString("decimal-format", "###,###,###,###.00");
        dataNode.save();

        assert decimalFormat != null;
        DecimalFormat formatter = new DecimalFormat(decimalFormat);

        _currency = new NucleusCurrency(singular, plural, 1.0D, formatTemplate, formatter);
    }

    @Override
    public ICurrency getCurrency() {
        return _currency;
    }

    @Override
    public IAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        synchronized (_accountSync) {
            NucleusAccount account = _accounts.get(playerId);

            if (account == null) {
                account = new NucleusAccount(
                        this, playerId, null, _globalAccountNode.getNode(playerId.toString()));
                _accounts.put(playerId, account);
            }

            return account;
        }
    }

    @Override
    public IEconomyTransaction createTransaction() {
        return new NucleusTransaction();
    }

    @Override
    public Object getHandle() {
        return this;
    }

    @Override
    public List<IBank> getBanks() {
        synchronized (_bankSync) {
            return new ArrayList<>(_banks.values());
        }
    }

    @Nullable
    @Override
    public IBank getBank(String bankName) {
        PreCon.notNull(bankName);

        synchronized (_bankSync) {
            return _banks.get(bankName);
        }
    }

    @Nullable
    @Override
    public IBank createBank(String bankName) {
        return createBank(bankName, null);
    }

    @Nullable
    @Override
    public IBank createBank(String bankName, @Nullable UUID playerId) {
        PreCon.notNullOrEmpty(bankName);

        synchronized (_bankSync) {
            if (_banks.containsKey(bankName)) {
                return null;
            }
        }

        IDataNode node = new YamlDataNode(Nucleus.getPlugin(), getDataPath("banks." + bankName));
        node.load();

        NucleusBank bank = new NucleusBank(this, bankName, playerId, node);

        synchronized (_bankSync) {
            _banks.put(bankName, bank);
        }

        return bank;
    }

    @Override
    public boolean deleteBank(String bankName) {
        PreCon.notNullOrEmpty(bankName);

        IBank bank;

        synchronized (_bankSync) {
            bank = _banks.remove(bankName);
            if (bank == null)
                return false;
        }

        File bankFolder = new File(getDataFolder(), "banks");
        File accountFile = new File(bankFolder, bank.getName() + ".yml");

        return accountFile.delete();
    }
}
