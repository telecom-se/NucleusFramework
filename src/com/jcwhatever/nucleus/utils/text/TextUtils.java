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


package com.jcwhatever.nucleus.utils.text;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.SyncLocation;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.format.IFormatterAppendable;
import com.jcwhatever.nucleus.utils.text.format.ITagFormatter;
import com.jcwhatever.nucleus.utils.text.format.TextFormatter;
import com.jcwhatever.nucleus.utils.text.format.TextFormatterSettings;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * Static methods to aid in string related tasks
 */
public final class TextUtils {

    private TextUtils() {}

    @Localizable static final String _FORMAT_TEMPLATE_RAW = "{0}";
    @Localizable static final String _FORMAT_TEMPLATE_HEADER = "{AQUA}{0}";
    @Localizable static final String _FORMAT_TEMPLATE_SUB_HEADER = "{LIGHT_PURPLE}{ITALIC}{0}:";
    @Localizable static final String _FORMAT_TEMPLATE_ITEM = "{YELLOW}{0}";
    @Localizable static final String _FORMAT_TEMPLATE_DEFINITION = "{GOLD}{0}{AQUA} - {GRAY}{1}";
    @Localizable static final String _FORMAT_TEMPLATE_ITEM_DESCRIPTION = "{YELLOW}{0}{AQUA} - {GRAY}{1}";
    @Localizable static final String _LOCATION_FORMAT_COLOR =
            "{LIGHT_PURPLE}X:{GRAY}{0: x}{WHITE}, " +
                    "{LIGHT_PURPLE}Y:{GRAY}{1: y}{WHITE}, " +
                    "{LIGHT_PURPLE}Z:{GRAY}{2: x}{WHITE}, " +
                    "{LIGHT_PURPLE}W:{GRAY}{3: world}";
    @Localizable static final String _LOCATION_FORMAT =
            "X:{0: x}, Y:{1: y}, Z:{2: x}, W:{3: world}";

    @Localizable static final String _TITLE_CASE_EXCLUSIONS =
            "above, about, across, against, along, among, around, at, before, behind, below, beneath, " +
                    "beside, between, beyond, by, down, during, except, for, from, in, inside, into, like, " +
                    "near, of, off, on, since, to, toward, through, under, until, up, upon, with, within," +
                    "the, a, an, and, but, or, for, either, be, is";

            //"a,an,the,and,but,or,on,in,with,upon,of,to,at,under,near,upon,by";

    public static final Pattern PATTERN_DOT = Pattern.compile("\\.");
    public static final Pattern PATTERN_COMMA = Pattern.compile(",");
    public static final Pattern PATTERN_COLON = Pattern.compile(":");
    public static final Pattern PATTERN_SEMI_COLON = Pattern.compile(";");
    public static final Pattern PATTERN_DASH = Pattern.compile("-");
    public static final Pattern PATTERN_SPACE = Pattern.compile(" ");
    public static final Pattern PATTERN_EQUALS = Pattern.compile("=");
    public static final Pattern PATTERN_PERCENT = Pattern.compile("%");
    public static final Pattern PATTERN_PIPE = Pattern.compile("\\|");
    public static final Pattern PATTERN_NUMBERS = Pattern.compile("-?\\d+");
    public static final Pattern PATTERN_DECIMAL_NUMBERS = Pattern.compile("-?\\d+(\\.\\d+)?");
    public static final Pattern PATTERN_NAMES = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
    public static final Pattern PATTERN_NODE_NAMES = Pattern.compile("^[a-zA-Z0-9_-]*$");
    public static final Pattern PATTERN_NODE_PATHS = Pattern.compile("^[a-zA-Z0-9_.-]*$");
    public static final Pattern PATTERN_FILEPATH_SLASH = Pattern.compile("[\\/\\\\]");
    public static final Pattern PATTERN_UNDERSCORE = Pattern.compile("_");
    public static final Pattern PATTERN_DOUBLE_QUOTE = Pattern.compile("\"");
    public static final Pattern PATTERN_SINGLE_QUOTE = Pattern.compile("'");
    public static final Pattern PATTERN_NEW_LINE = Pattern.compile("\r\n|\r|\n");

    public static final TextFormatterSettings TEXT_FORMATTER_SETTINGS = new TextFormatterSettings();
    public static final TextFormatter TEXT_FORMATTER = new TextFormatter(TEXT_FORMATTER_SETTINGS);

    private static final Map<Plugin, TextFormatterSettings> _pluginFormatters = new WeakHashMap<>(10);
    private static Set<String> _titleCaseExclusions;

    public enum FormatTemplate {
        /**
         * No formatting.
         */
        RAW                   (_FORMAT_TEMPLATE_RAW),
        /**
         * A header in a list of items.
         */
        HEADER                (_FORMAT_TEMPLATE_HEADER),
        /**
         * A sub header in a list of items.
         */
        SUB_HEADER            (_FORMAT_TEMPLATE_SUB_HEADER),
        /**
         * A single item in a list.
         */
        LIST_ITEM             (_FORMAT_TEMPLATE_ITEM),
        /**
         * A single item in a list with a description.
         */
        LIST_ITEM_DESCRIPTION (_FORMAT_TEMPLATE_ITEM_DESCRIPTION),

        /**
         * A definition for an item that
         * generally does not change (i.e commands)
         */
        CONSTANT_DEFINITION   (_FORMAT_TEMPLATE_DEFINITION);

        private final String _template;

        FormatTemplate(String template) {
            _template = template;
        }

        @Override
        @Localized
        public String toString() {
            return NucLang.get(_template).toString();
        }
    }

    /**
     * Specify case sensitivity.
     */
    public enum CaseSensitivity {

        /**
         * Characters must be exact.
         */
        EXACT,
        /**
         * Ignore case, case insensitive.
         */
        IGNORE_CASE
    }

    /**
     * Specify optional title case parsing options.
     */
    public enum TitleCaseOption {
        /**
         * Forces lower case in all letters of a word except the first letter.
         */
        FORCE_CASING,
        /**
         * Forces title casing on words that are excluded from title casing.
         */
        IGNORE_EXCLUDED_WORDS,
        /**
         * Remove underscores and replace them with spaces.
         */
        REMOVE_UNDERSCORE,
        /**
         * Use all options.
         */
        ALL
    }

    /**
     * Determine if a string is valid for use as a name.
     *
     * <p>The string must begin with a letter and use only alphanumeric characters
     * and underscores.</p>
     *
     * <p>The string must also be at least 1 character in length and be no more
     * than 16 characters long.</p>
     *
     * @param name  The name to check.
     */
    public static boolean isValidName(String name) {
        return isValidName(name, 16);
    }

    /**
     * Determine if a string is valid for use as a name.
     *
     * <p>The string must begin with a letter and use only alphanumeric
     * characters and underscores.</p>
     *
     * <p>The string must also be at least 1 character in length.</p>
     *
     * @param name    The name to check.
     * @param maxLen  The max length of the name.
     */
    public static boolean isValidName(@Nullable String name, int maxLen) {
        PreCon.greaterThanZero(maxLen);

        return name != null && (maxLen == -1 || name.length() <= maxLen) &&
                name.length() > 0 && PATTERN_NAMES.matcher(name).matches();
    }

    /**
     * Search a collection of strings for candidates that start with the specified
     * prefix.
     *
     * @param prefix            The prefix to search for.
     * @param searchCandidates  The search candidates.
     */
    public static List<String> startsWith(String prefix, Collection<String> searchCandidates) {
        return startsWith(prefix, searchCandidates, CaseSensitivity.EXACT);
    }

    /**
     * Search a collection of strings for candidates that start with the specified
     * prefix.
     *
     * @param prefix            The prefix to search for.
     * @param searchCandidates  The search candidates.
     * @param casing            The case sensitivity of the search.
     */
    public static List<String> startsWith(String prefix,
                                          Collection<String> searchCandidates, CaseSensitivity casing) {
        PreCon.notNull(prefix);
        PreCon.notNull(searchCandidates);
        PreCon.notNull(casing);

        if (prefix.isEmpty()) {
            return new ArrayList<>(searchCandidates);
        }

        if (casing == CaseSensitivity.IGNORE_CASE) {
            prefix = prefix.toLowerCase();
        }

        List<String> result = new ArrayList<>(searchCandidates.size());

        for (String candidate : searchCandidates) {

            if ((casing == CaseSensitivity.IGNORE_CASE &&
                    candidate.toLowerCase().startsWith(prefix)) ||
                    candidate.startsWith(prefix)) {
                result.add(candidate);
            }
        }

        return result;
    }

    /**
     * Search a collection of strings for candidates that end with the specified
     * suffix.
     *
     * @param suffix            The suffix to search for.
     * @param searchCandidates  The search candidates.
     */
    public static List<String> endsWith(String suffix,
                                        Collection<String> searchCandidates) {
        return endsWith(suffix, searchCandidates, CaseSensitivity.EXACT);
    }

    /**
     * Search a collection of strings for candidates that end with the specified
     * suffix.
     *
     * @param suffix            The suffix to search for.
     * @param searchCandidates  The search candidates.
     * @param casing            The case sensitivity of the search.
     */
    public static List<String> endsWith(String suffix,
                                        Collection<String> searchCandidates, CaseSensitivity casing) {
        PreCon.notNull(suffix);
        PreCon.notNull(searchCandidates);
        PreCon.notNull(casing);

        if (suffix.isEmpty()) {
            return new ArrayList<>(searchCandidates);
        }

        if (casing == CaseSensitivity.IGNORE_CASE) {
            suffix = suffix.toLowerCase();
        }

        List<String> result = new ArrayList<>(searchCandidates.size());

        for (String candidate : searchCandidates) {

            if ((casing == CaseSensitivity.IGNORE_CASE &&
                    candidate.toLowerCase().endsWith(suffix)) ||
                    candidate.endsWith(suffix)) {
                result.add(candidate);
            }
        }

        return result;
    }

    /**
     * Search a collection of strings for candidates that contain the specified
     * search text.
     *
     * @param searchText        The text to search for.
     * @param searchCandidates  The search candidates.
     */
    public static List<String> contains(String searchText,
                                        Collection<String> searchCandidates) {
        return contains(searchText, searchCandidates, CaseSensitivity.EXACT);

    }

    /**
     * Search a collection of strings for candidates that contain the specified
     * search text.
     *
     * @param searchText        The text to search for.
     * @param searchCandidates  The search candidates.
     * @param casing            The case sensitivity of the search.
     */
    public static List<String> contains(String searchText,
                                        Collection<String> searchCandidates, CaseSensitivity casing) {
        PreCon.notNull(searchText);
        PreCon.notNull(searchCandidates);
        PreCon.notNull(casing);

        if (searchText.isEmpty()) {
            return new ArrayList<>(searchCandidates);
        }

        if (casing == CaseSensitivity.IGNORE_CASE) {
            searchText = searchText.toLowerCase();
        }

        List<String> result = new ArrayList<>(searchCandidates.size());

        for (String candidate : searchCandidates) {

            if ((casing == CaseSensitivity.IGNORE_CASE &&
                    candidate.toLowerCase().contains(searchText)) ||
                    candidate.contains(searchText)) {
                result.add(candidate);
            }
        }

        return result;
    }

    /**
     * Search a collection of strings for valid candidates using an {@link IValidator}
     * to validate.
     *
     * @param searchCandidates  The search candidates.
     * @param entryValidator    The entry validator.
     */
    public static List<String> search(Collection<String> searchCandidates, IValidator<String> entryValidator) {
        return CollectionUtils.search(searchCandidates, entryValidator);
    }

    /**
     * Pad the right side of a string with the specified characters.
     *
     * @param s        The String to pad.
     * @param length   The number of characters to pad.
     * @param pad      The character to path with.
     */
    public static String padRight(String s, int length, char pad) {
        PreCon.notNull(s);
        PreCon.positiveNumber(length);

        StringBuilder buffy = new StringBuilder(s.length() + length);
        buffy.append(s);
        for (int i = 0; i < length; ++i) {
            buffy.append(pad);
        }
        return buffy.toString();
    }

    /**
     * Pad the right side of a string with spaces.
     *
     * @param s       The String to pad.
     * @param length  The number of spaces to pad with.
     */
    public static String padRight(String s, int length) {
        return TextUtils.padRight(s, length, ' ');
    }

    /**
     * Pad the left side of a string with the specified characters.
     *
     * @param s       The String to pad.
     * @param length  The number of characters to pad.
     * @param pad     The character to path with.
     */
    public static String padLeft(String s, int length, char pad) {
        PreCon.notNull(s);
        PreCon.positiveNumber(length);

        StringBuilder buffy = new StringBuilder(s.length() + length);
        for (int i = 0; i < length; i++) {
            buffy.append(pad);
        }
        buffy.append(s);
        return buffy.toString();
    }

    /**
     * Pad left side of specified string with spaces.
     *
     * @param s       The string to pad.
     * @param length  The number of spaces to pad with.
     */
    public static String padLeft(String s, int length) {
        return TextUtils.padLeft(s, length, ' ');
    }

    /**
     * Reduce the number of characters in a string by removing characters from the end.
     *
     * <p>Returns input string if input string length is less than or equal to 16
     * characters.</p>
     *
     * @param s       The string to truncate.
     * @param length  The new length of the string.
     */
    public static String truncate(String s, int length) {
        PreCon.notNull(s);
        PreCon.positiveNumber(length);
        PreCon.lessThan(length, s.length());

        if (s.length() > length)
            return s.substring(0, length);

        return s;
    }

    /**
     * Reduce the number of characters in a string to 16.
     *
     * <p>Returns input string if input string length is less than or equal to
     * 16 characters.</p>
     *
     * @param s  The string to truncate.
     */
    public static String truncate(String s) {
        return TextUtils.truncate(s, 16);
    }

    /**
     * Converts supplied string to camel casing.
     *
     * @param s  The string to convert.
     */
    public static String camelCase(String s) {
        PreCon.notNull(s);

        if (s.length() < 2) {
            return s.toLowerCase();
        }

        String[] words = PATTERN_SPACE.split(s);
        StringBuilder resultSB = new StringBuilder(s.length() + 20);

        for (int i=0; i < words.length; i++) {
            String firstLetter = String.valueOf(words[i].charAt(0));

            resultSB.append(i == 0 ? firstLetter.toLowerCase() : firstLetter.toUpperCase());
            resultSB.append(words[i].substring(1).toLowerCase());
        }

        return resultSB.toString();
    }

    /**
     * Converts the casing of the supplied string for use as a title.
     *
     * <p>The normal operation is to change the first letter of every word over
     * 3 characters in length to upper case.</p>
     *
     * <p>Additional operations can be added.</p>
     *
     * @param text     The string to modify.
     * @param options  Parsing options.
     */
    public static String titleCase(String text, TitleCaseOption... options) {
        PreCon.notNull(text);

        if (text.length() < 2) {
            return text;
        }

        boolean forceCasing = false;
        boolean removeUnderscores = false;
        boolean ignoreExcluded = false;

        for (TitleCaseOption option : options) {
            switch (option) {
                case ALL:
                    removeUnderscores = true;
                    ignoreExcluded = true;
                    forceCasing = true;
                    break;
                case REMOVE_UNDERSCORE:
                    removeUnderscores = true;
                    break;
                case IGNORE_EXCLUDED_WORDS:
                    ignoreExcluded = true;
                    break;
                case FORCE_CASING:
                    forceCasing = true;
                    break;
            }
        }

        if (removeUnderscores) {
            text = text.replace('_', ' ');
        }

        String[] words = PATTERN_SPACE.split(text);
        StringBuilder resultSB = new StringBuilder(text.length() + 10);

        for (int i=0; i < words.length; i++) {

            if (i != 0)
                resultSB.append(' ');

            String word = words[i];

            String firstLetter = word.substring(0, 1);
            String wordEnd = word.substring(1, word.length());

            if (i == 0 || ignoreExcluded || !isTitleCaseExcluded(word)) {
                firstLetter = firstLetter.toUpperCase();
            }

            if (forceCasing) {
                wordEnd = wordEnd.toLowerCase();
            }

            resultSB.append(firstLetter);
            resultSB.append(wordEnd);
        }

        return resultSB.toString();
    }

    /**
     * Concatenates a collection into a single string using the specified separator
     * string.
     *
     * @param collection  The collection to concatenate.
     * @param separator   The string to insert between elements.
     */
    public static String concat(Collection<?> collection, String separator) {
        //noinspection ConstantConditions
        return concat(collection, separator, "");
    }

    /**
     * Concatenates a collection into a single string using the specified separator
     * string.
     *
     * @param collection  The collection to concatenate.
     * @param separator   The string to insert between elements.
     * @param emptyValue  The string to return if the collection is null or empty.
     */
    @Nullable
    public static String concat(Collection<?> collection, String separator, String emptyValue) {
        if (collection == null || collection.isEmpty()) {
            return emptyValue;
        }

        if (separator == null)
            separator = "";

        StringBuilder buffy = new StringBuilder(collection.size() * 25);
        for (Object o : collection) {
            buffy.append(separator);
            buffy.append(o.toString());
        }
        return buffy.substring(separator.length());
    }

    /**
     * Concatenates an array in a single string using the specified separator string.
     *
     * @param strArray   The array to concatenate.
     * @param separator  The string to insert between elements.
     */
    public static <T> String concat(T[] strArray, @Nullable String separator) {
        //noinspection ConstantConditions
        return concat(0, strArray.length, strArray, separator, "");
    }

    /**
     * Concatenates an array into a single string using the specified separator string.
     *
     * @param strArray    The array to concatenate.
     * @param separator   The string to insert between elements.
     * @param emptyValue  The string to return if the array is null or empty.
     */
    @Nullable
    public static <T> String concat(T[] strArray, @Nullable String separator, @Nullable String emptyValue) {
        return concat(0, strArray.length, strArray, separator, emptyValue);
    }

    /**
     * Concatenates an array into a single string using the specified separator string.
     *
     * @param startIndex  The index to start concatenating at.
     * @param strArray    The array to concatenate.
     * @param separator   The separator to insert between elements.
     */
    public static <T> String concat(int startIndex, T[] strArray, @Nullable String separator) {
        //noinspection ConstantConditions
        return concat(startIndex, strArray.length, strArray, separator, "");
    }

    /**
     * Concatenates an array into a single string using the specified separator string.
     *
     * @param startIndex  The index to start concatenating at.
     * @param strArray    The array to concatenate.
     * @param separator   The separator to insert between elements.
     * @param emptyValue  The value to return if the array is empty or null.
     */
    @Nullable
    public static <T> String concat(int startIndex, T[] strArray,
                                    @Nullable String separator, @Nullable String emptyValue) {
        return concat(startIndex, strArray.length, strArray, separator, emptyValue);
    }

    /**
     * Concatenates an array into a single string using the specified separator string.
     *
     * @param startIndex  The index to start concatenating at.
     * @param endIndexP1  The index to stop concatenating at (+1, add 1 to the end index).
     * @param strArray    The array to concatenate.
     */
    public static <T> String concat(int startIndex, int endIndexP1, T[] strArray) {
        //noinspection ConstantConditions
        return concat(startIndex, endIndexP1, strArray, null, "");
    }


    /**
     * Concatenates an array into a single string using the specified separator string.
     *
     * @param startIndex  The index to start concatenating at.
     * @param endIndexP1  The index to stop concatenating at (+1, add 1 to the end index).
     * @param strArray    The array to concatenate.
     * @param separator   The separator to insert between elements.
     */
    public static <T> String concat(int startIndex, int endIndexP1,
                                    T[] strArray, @Nullable String separator) {
        //noinspection ConstantConditions
        return concat(startIndex, endIndexP1, strArray, separator, "");
    }


    /**
     * Concatenates an array into a single string using the specified separator string.
     *
     * @param startIndex  The index to start concatenating at.
     * @param endIndexP1  The index to stop concatenating at (+1, add 1 to the end index).
     * @param strArray    The array to concatenate.
     */
    @Nullable
    public static <T> String concat(int startIndex, int endIndexP1, T[] strArray,
                                    @Nullable String separator, @Nullable String emptyValue) {
        PreCon.notNull(strArray);

        if (strArray.length == 0 || startIndex == endIndexP1)
            return emptyValue;

        if (separator == null)
            separator = "";

        StringBuilder buffy = new StringBuilder((endIndexP1 - startIndex) * 25);
        boolean isEnum = strArray[0] instanceof Enum<?>;
        for (int i = startIndex; i < endIndexP1; i++) {
            T str = strArray[i];
            if (str == null)
                continue;

            buffy.append(separator);
            if (isEnum) {
                Enum<?> en = (Enum<?>)str;
                buffy.append(en.name());
            }
            else {
                buffy.append(str);
            }
        }
        return buffy.substring(separator.length());
    }

    /**
     * Splits a string into multiple string based on max character length specified
     * for a line.
     *
     * @param str                   The string to split/paginate.
     * @param maxLineLen            The max length of a line.
     * @param excludeColorsFromLen  True to exclude color characters from length
     *                              calculations.
     */
    public static List<String> paginateString(String str, int maxLineLen, boolean excludeColorsFromLen) {
        return paginateString(str, null, maxLineLen, excludeColorsFromLen);
    }

    /**
     * Splits a string into multiple string based on max character length specified
     * for a line.
     *
     * @param str                   The string to split/paginate.
     * @param linePrefix            The prefix to append to each line.
     * @param maxLineLen            The max length of a line. Must be greater than 1.
     * @param excludeColorsFromLen  True to exclude color characters from length calculations.
     */
    public static List<String> paginateString(String str, @Nullable String linePrefix,
                                              int maxLineLen, boolean excludeColorsFromLen) {
        PreCon.notNull(str);
        PreCon.isValid(maxLineLen > 1);

        if (linePrefix != null)
            str = str.replace(linePrefix, "");
        else
            linePrefix = "";

        String[] words = PATTERN_SPACE.split(str);

        List<String> results = new ArrayList<String>(str.length() / maxLineLen);

        StringBuilder line = new StringBuilder(maxLineLen);
        line.append(linePrefix);

        int prefixSize = linePrefix.length();

        boolean wordAddedToLine = false;

        for (int i=0; i < words.length; i++) {

            int lineLength = excludeColorsFromLen
                    ? TextColor.remove(line).length()
                    : line.length();

            int wordLength = excludeColorsFromLen
                    ? TextColor.remove(words[i]).length()
                    : words[i].length();

            if (lineLength + wordLength + 1 <= maxLineLen &&
                    prefixSize + wordLength < maxLineLen) { // append to current line

                if (wordAddedToLine)
                    line.append(' ');

                line.append(words[i]);
                wordAddedToLine = true;
            }
            else { // create new line

                String format = null;

                if (line.length() != 0 && i != 0) {
                    String finishedLine = line.toString();
                    format = ChatColor.getLastColors(finishedLine);
                    results.add(finishedLine);
                }

                line.setLength(0);
                line.append(linePrefix);
                if (format != null)
                    line.append(format);

                if (prefixSize + wordLength >= maxLineLen) {
                    line.append(words[i]);
                    wordAddedToLine = true;
                }
                else {

                    if (i != 0) {
                        i = i - 1;
                    }

                    wordAddedToLine = false;
                }
            }
        }

        // make sure last line is added.
        // get left behind if number of words runs out
        // and there is still room for more.
        if (wordAddedToLine) {
            results.add(line.toString());
        }

        return results;
    }

    /**
     * Format a location into a human readable string.
     *
     * @param loc       The location to format.
     * @param addColor  True to add color.
     */
    public static String formatLocation(Location loc, boolean addColor) {
        if (loc == null)
            return "null";

        String worldName;

        if (loc.getWorld() == null) {
            worldName = loc instanceof SyncLocation ? ((SyncLocation) loc).getWorldName() : "null";
        }
        else {
            worldName = loc.getWorld().getName();
        }

        DecimalFormat format = new DecimalFormat("###.#");
        return TextUtils.format(addColor ? _LOCATION_FORMAT_COLOR : _LOCATION_FORMAT,
                format.format(loc.getX()),
                format.format(loc.getY()),
                format.format(loc.getZ()),
                worldName).toString();
    }

    /**
     * Format text string by replacing placeholders with the information about the
     * specified plugin.
     *
     * <p>Placeholders: {plugin-version}, {plugin-name}, {plugin-full-name},
     * {plugin-author}, {plugin-command}</p>
     *
     * @param plugin  The plugin.
     * @param msg     The message to format plugin info into.
     * @param args    Optional message format arguments.
     */
    public static IChatMessage formatPluginInfo(Plugin plugin, CharSequence msg, Object... args) {
        return formatPluginInfo(plugin, null, msg, args);
    }

    /**
     * Format text string by replacing placeholders with the information about the
     * specified plugin.
     *
     * <p>Placeholders: {plugin-version}, {plugin-name}, {plugin-full-name},
     * {plugin-author}, {plugin-command}</p>
     *
     * @param plugin    The plugin.
     * @param settings  The settings to use.
     * @param msg       The message to format plugin info into.
     * @param args      Optional message format arguments.
     */
    public static IChatMessage formatPluginInfo(Plugin plugin,
                                          @Nullable TextFormatterSettings settings,
                                          CharSequence msg, Object... args) {
        PreCon.notNull(plugin);
        PreCon.notNull(msg);
        PreCon.notNull(args);

        TextFormatterSettings formatters;

        if (settings == null) {
            formatters = _pluginFormatters.get(plugin);
            if (formatters == null) {
                formatters = getPluginFormatters(plugin, null);
                _pluginFormatters.put(plugin, formatters);
            }
        }
        else {
            formatters = getPluginFormatters(plugin, settings);
        }

        return TEXT_FORMATTER.format(formatters, msg, args);
    }

    /**
     * Formats text string by replacing placeholders (i.e {0}) with the string
     * representation of the objects provided.
     *
     * <p>The index order of the object params as provided is mapped to the number
     * inside the placeholder. </p>
     *
     * <p>Color can be specified using {@link TextColor} enum names in curly brackets.
     * (i.e. {GREEN})</p>
     *
     * @param template  An object whose {@link #toString} method yields the message template.
     * @param args      Optional format arguments.
     */
    public static IChatMessage format(Object template, Object... args) {
        return template instanceof CharSequence
                ? format((CharSequence) template, args)
                : format(template.toString(), args);
    }

    /**
     * Formats text string by replacing placeholders (i.e {0}) with the string
     * representation of the objects provided.
     *
     * <p>The index order of the object params as provided is mapped to the number
     * inside the placeholder. </p>
     *
     * <p>Color can be specified using {@link TextColor} enum names in curly brackets.
     * (i.e. {GREEN})</p>
     *
     * @param settings  A custom set of settings to use.
     * @param template  An object whose {@link #toString} method yields the message template.
     * @param args      Optional format arguments.
     */
    public static IChatMessage format(TextFormatterSettings settings, Object template, Object... args) {
        PreCon.notNull(settings);
        PreCon.notNull(template);
        PreCon.notNull(args);

        return TEXT_FORMATTER.format(settings, template.toString(), args);
    }

    /**
     * Formats text string by replacing placeholders (i.e {0}) with the string
     * representation of the object params provided.
     *
     * <p>The index order of the objects as provided is mapped to the number
     * inside the placeholder. </p>
     *
     * <p>Color can be specified using {@link TextColor} names in curly brackets.
     * (i.e. {GREEN})</p>
     *
     * @param msg   The message to format.
     * @param args  Optional format arguments.
     */
    public static IChatMessage format(CharSequence msg, Object... args) {
        PreCon.notNull(msg);
        PreCon.notNull(args);

        return TEXT_FORMATTER.format(msg, args);
    }

    /**
     * Creates a new {@link TextFormatterSettings} that contains the settings from the
     * specified {@link TextFormatterSettings} and additionally formatters for the specified
     * plugin.
     */
    public static TextFormatterSettings getPluginFormatters(final Plugin plugin,
                                                            @Nullable TextFormatterSettings settings) {
        PreCon.notNull(plugin);

        Map<String, ITagFormatter> formatters = new HashMap<>(10);

        // Plugin Version
        formatters.put("plugin-version", new ITagFormatter() {
            @Override
            public String getTag() {
                return "plugin-version";
            }

            @Override
            public void append(IFormatterAppendable output, String rawTag) {
                output.append(plugin.getDescription().getVersion());
            }
        });

        // Plugin Name
        formatters.put("plugin-name", new ITagFormatter() {
            @Override
            public String getTag() {
                return "plugin-name";
            }

            @Override
            public void append(IFormatterAppendable output, String rawTag) {
                output.append(plugin.getDescription().getName());
            }
        });

        // Plugin Full Name
        formatters.put("plugin-full-name", new ITagFormatter() {
            @Override
            public String getTag() {
                return "plugin-full-name";
            }

            @Override
            public void append(IFormatterAppendable output, String rawTag) {
                output.append(plugin.getDescription().getFullName());
            }
        });

        // Plugin Author
        formatters.put("plugin-author", new ITagFormatter() {
            @Override
            public String getTag() {
                return "plugin-author";
            }

            @Override
            public void append(IFormatterAppendable output, String rawTag) {
                output.append(TextUtils.concat(plugin.getDescription().getAuthors(), ", "));
            }
        });

        // Plugin Command
        formatters.put("plugin-command", new ITagFormatter() {
            @Override
            public String getTag() {
                return "plugin-command";
            }

            @Override
            public void append(IFormatterAppendable output, String rawTag) {
                Map<String, Map<String, Object>> commands = plugin.getDescription().getCommands();
                if (commands != null) {

                    Set<String> commandKeys = commands.keySet();

                    if (!commandKeys.isEmpty()) {
                        String cmd = commandKeys.iterator().next();

                        output.append(cmd);
                    }
                }
            }
        });

        return settings != null
                ? new TextFormatterSettings(settings, formatters)
                : new TextFormatterSettings(formatters);
    }

    /**
     * Parse a boolean from a string and include "yes" and "1" as values that
     * return true.
     *
     * @param string  The string to parse.
     */
    public static boolean parseBoolean(@Nullable String string) {
        if (string == null)
            return false;

        switch (string.toLowerCase()) {
            case "true":
            case "yes":
            case "1":
                return true;
            default:
                return false;
        }
    }

    /**
     * Parse a byte value from a string and return a default value if parsing fails.
     *
     * @param string      The string to parse.
     * @param defaultVal  The default value to return if parsing fails.
     */
    public static byte parseByte(@Nullable String string, byte defaultVal) {
        if (string == null)
            return defaultVal;

        try {
            return Byte.parseByte(string);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parse a short value from a string and return a default value if parsing fails.
     *
     * @param string      The string to parse.
     * @param defaultVal  The default value to return if parsing fails.
     */
    public static short parseShort(@Nullable String string, short defaultVal) {
        if (string == null)
            return defaultVal;

        try {
            return Short.parseShort(string);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parse an integer value from a string and return a default value if parsing
     * fails.
     *
     * @param string      The string to parse.
     * @param defaultVal  The default value to return if parsing fails.
     */
    public static int parseInt(@Nullable String string, int defaultVal) {
        if (string == null)
            return defaultVal;

        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parse a long value from a string and return a default value if parsing fails.
     *
     * @param string      The string to parse.
     * @param defaultVal  The default value to return if parsing fails.
     */
    public static long parseLong(@Nullable String string, long defaultVal) {
        if (string == null)
            return defaultVal;

        try {
            return Long.parseLong(string);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parse a float value from a string and return a default value if parsing fails.
     *
     * @param string      The string to parse.
     * @param defaultVal  The default value to return if parsing fails.
     */
    public static float parseFloat(@Nullable String string, float defaultVal) {
        if (string == null)
            return defaultVal;

        try {
            return Float.parseFloat(string);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parse a double value from a string and return a default value if parsing fails.
     *
     * @param string      The string to parse.
     * @param defaultVal  The default value to return if parsing fails.
     */
    public static double parseDouble(@Nullable String string, double defaultVal) {
        if (string == null)
            return defaultVal;

        try {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parse the {@link java.util.UUID} from the supplied string.
     * If parsing fails, null is returned.
     *
     * @param string  The string to parse.
     */
    @Nullable
    public static UUID parseUUID(String string) {
        if (string == null)
            return null;

        try {
            return UUID.fromString(string);
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
    }

    /**
     * Parse the {@link java.util.UUID}'s from the supplied collection of strings.
     *
     * <p>If a string cannot be parsed, it is not included in the results.</p>
     *
     * <p>Failure to parse one or more results can be detected by compare the size
     * of the result with the size of the input collection.</p>
     *
     * @param strings  The string collection to parse.
     */
    public static List<UUID> parseUUID(Collection<String> strings) {

        List<UUID> results = new ArrayList<UUID>(strings.size());

        for (String raw : strings) {
            UUID id = parseUUID(raw);
            if (id == null)
                continue;

            results.add(id);
        }
        return results;
    }

    /**
     * Parse the {@link java.util.UUID}'s from the supplied string array.
     *
     * <p>If a string cannot be parsed, it is not included in the results.</p>
     *
     * <p>Failure to parse one or more results can be detected by compare the
     * size of the result with the size of the input collection.</p>
     *
     * @param strings  The string array to parse.
     */
    public static List<UUID> parseUUID(String[] strings) {

        List<UUID> results = new ArrayList<UUID>(strings.length);

        for (String raw : strings) {
            UUID id = parseUUID(raw);
            if (id == null)
                continue;

            results.add(id);
        }
        return results;
    }

    /**
     * Convert a UUID to a string without dashes.
     *
     * @param uuid  The UUID to convert.
     */
    public static String uuidToString(UUID uuid) {
        return TextUtils.PATTERN_DASH.matcher(
                uuid.toString()).replaceAll("");
    }

    /**
     * Print an array of {@link java.lang.StackTraceElement} to an {@link java.lang.Appendable}.
     *
     * @param stackTrace  The stack trace to append.
     * @param appendable  The output to print to.
     *
     * @throws java.io.IOException if the {@link Appendable} throws the exception.
     */
    public static void printStackTrace(StackTraceElement[] stackTrace, Appendable appendable)
            throws IOException {

        PreCon.notNull(stackTrace);
        PreCon.notNull(appendable);

        for (StackTraceElement element : stackTrace) {

            if (element.getClassName().equals("java.lang.Thread") &&
                    element.getMethodName().equals("getStackTrace")) {
                continue; // skip call to getStackTrace
            }

            appendable.append("    at ");
            appendable.append(element.getClassName());
            appendable.append('.');
            appendable.append(element.getMethodName());
            appendable.append(" (");
            appendable.append(element.getFileName());
            appendable.append(':');
            appendable.append(String.valueOf(element.getLineNumber()));
            appendable.append(")\n");
        }
    }

    private static boolean isTitleCaseExcluded(String word) {
        if (_titleCaseExclusions == null) {
            IChatMessage exclusionString = NucLang.get(_TITLE_CASE_EXCLUSIONS);

            String[] excl = PATTERN_COMMA.split(exclusionString);
            _titleCaseExclusions = new HashSet<>(excl.length + 10);
            for (String excluded : excl) {
                _titleCaseExclusions.add(excluded.trim().toLowerCase());
            }
        }

        return _titleCaseExclusions.contains(word.toLowerCase());
    }
}

