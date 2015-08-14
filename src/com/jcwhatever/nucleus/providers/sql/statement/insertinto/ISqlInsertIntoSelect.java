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

package com.jcwhatever.nucleus.providers.sql.statement.insertinto;

import com.jcwhatever.nucleus.providers.sql.ISqlTable;
import com.jcwhatever.nucleus.providers.sql.statement.generators.IColumnNameGenerator;

/**
 * Insert into select clause.
 */
public interface ISqlInsertIntoSelect {

    /**
     * Select columns from the source table to insert.
     *
     * @param columnNames  The names of the columns, by order.
     */
    ISqlInsertIntoWhere select(String... columnNames);

    /**
     * Select columns from the source table to insert.
     *
     * @param nameGenerator  The generator that will supply the column names.
     */
    ISqlInsertIntoWhere select(IColumnNameGenerator nameGenerator);

    /**
     * Select a column from the specified table to insert.
     *
     * @param table       The table the specified column is from.
     * @param columnName  The name of the column.
     */
    ISqlInsertIntoReselect select(ISqlTable table, String columnName);
}
