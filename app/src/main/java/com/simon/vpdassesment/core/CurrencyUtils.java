package com.simon.vpdassesment.core;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

/**
 * Android/Java class that allows for i18n and formatting of currency, both user-facing and storage-wise.
 * Written such that values to be stored in the database are returned as long, and user-facing strings are properly
 * formatted based upon the user's default locale.
 */
public class CurrencyUtils {

    /**
     * Assume that the input is an integer that has been multiplied by a power of 10 sufficient to not lose data. E.g. for
     * the US, this would be dollars * 100.
     *
     * This function properly divides the integer, and then formats it as a currency.
     *
     * <b>IMPORTANT</b>: Make sure you convert the <i>amount</i> of the currency, as needed. E.g. You stored dollars, but
     * are showing a value in Euros. This function assumes the amount is in the correct, current, locale money unit.
     *
     * @param amount The amount of money, as stored in an long (e.g. as cents)
     * @return A string formatted in the current locale that represents the monetary amount.
     */
    public static String longToCurrencyString(long amount) {
        return longToCurrencyString(amount, true);
    }

    /**
     * Assume that the input is an integer that has been multiplied by a power of 10 sufficient to not lose data. E.g. for
     * the US, this would be dollars * 100.
     *
     * This function properly divides the integer, and then formats it as a currency.
     *
     * <b>IMPORTANT</b>: Make sure you convert the <i>amount</i> of the currency, as needed. E.g. You stored dollars, but
     * are showing a value in Euros. This function assumes the amount is in the correct, current, locale money unit.
     *
     * @param amount The amount of money, as stored in an long (e.g. as cents)
     * @return A number that has be correctly divided to have the right number of fractional digits.
     */
    public static String longToCurrencyString(long amount, boolean bCurrencySign) {
        int scale = getFractionDigits();
        int divisor = scale > 0 ? (int) Math.pow(10, scale) : 1;
        BigDecimal b = new BigDecimal(amount * 100);
        b = b.divide(new BigDecimal(divisor), scale, RoundingMode.HALF_UP);
        return numberToCurrencyString(b, bCurrencySign);
    }

    /**
     * Returns default fraction digits according to the locale.
     *
     * @return The number of floating point digits.
     */
    public static int getFractionDigits() {
        Currency c = Currency.getInstance(Locale.getDefault());
        return c.getDefaultFractionDigits();
    }

    /**
     * Accepts a floating-point number (usually double or Double) that represents a currency amount. This function
     * properly rounds the amount, and returns a string formatted for the current locale (including a currency symbol).
     *
     * @param damount The floating-point amount to format.
     * @return A locale-specific string rounded and formatted to look like a currency.
     */
    public static String numberToCurrencyString(Number damount) {
        return numberToCurrencyString(damount, true);
    }

    /**
     * Get a locale-specific string representing the amount of currency provided. This is identical to
     * numberToCurrencyString(Number), but allows you to turn off the currency symbol.
     *
     * @param damount The amount
     * @param bCurrencySign whether to include the currency symbol in the string.
     * @return The currency string.
     */
    public static String numberToCurrencyString(Number damount, boolean bCurrencySign) {
        String rv = "";
        Locale nigeriaLocale = new Locale("en", "NG");
        DecimalFormat d = (DecimalFormat) NumberFormat.getCurrencyInstance(nigeriaLocale);
        if (damount.doubleValue() < 0) {
            if (d.getNegativePrefix().contains("("))
                d.setNegativePrefix(d.getNegativePrefix().replace("(", "-"));
            if (d.getNegativeSuffix().contains(")"))
                d.setNegativeSuffix(d.getNegativeSuffix().replace(")", ""));
        }
        if (!bCurrencySign) {
            d.setPositivePrefix("");
            d.setPositiveSuffix("");
            d.setNegativePrefix("-");
            d.setNegativeSuffix("");
        }
        rv = d.format(damount.doubleValue()).replace(String.valueOf((char) 160), "");
        return rv;
    }

    public static String numberToCurrencyString(Number damount, boolean bCurrencySign, boolean hide) {
        String rv = "";
        Locale nigeriaLocale = new Locale("en", "NG");
        if (hide) {
            try {
                // Get the currency symbol for the locale
                String currencySymbol = NumberFormat.getCurrencyInstance(nigeriaLocale)
                        .getCurrency()
                        .getSymbol(nigeriaLocale);
                return currencySymbol + "**********";
            } catch (Exception e) {
return "*********";
            }
        }
        DecimalFormat d = (DecimalFormat) NumberFormat.getCurrencyInstance(nigeriaLocale);
        if (damount.doubleValue() < 0) {
            if (d.getNegativePrefix().contains("("))
                d.setNegativePrefix(d.getNegativePrefix().replace("(", "-"));
            if (d.getNegativeSuffix().contains(")"))
                d.setNegativeSuffix(d.getNegativeSuffix().replace(")", ""));
        }
        if (!bCurrencySign) {
            d.setPositivePrefix("");
            d.setPositiveSuffix("");
            d.setNegativePrefix("-");
            d.setNegativeSuffix("");
        }
        rv = d.format(damount.doubleValue()).replace(String.valueOf((char) 160), "");
        return rv;
    }

    /**
     * Take a string that represents a currency amount (with or without the currency symbol), multiplies it by the correct
     * power of 10 to push the fractional digits into an integer form, and returns the result as a long.
     * <p>
     * E.g. In US: 100.34 -> 10034<br/>
     * In France/Germany: 100,34 -> 10034<br/>
     * etc.<br/>
     * <p>
     * <b>This function is quite tolerant of user input</b>, and will accept anything that is "normal" when writing a
     * currency in that locale.
     *
     * <pre>
     * // locale is en
     * I.currencyStringToLong(&quot;$1,345.66&quot;, 0L); // returns 134566
     * I.currencyStringToLong(&quot;1345.66&quot;, 0L); // returns 134566
     * // locale is fr
     * I.currencyStringToLong(&quot;1 345,66&quot;, 0L); // returns 134566
     * I.currencyStringToLong(&quot;1345.66 &amp;euro&quot;, 0L); // returns 134566
     * </pre>
     *
     * @param amount The string representing a user-input amount of currency.
     * @param defaultValue The value to return if the parsing fails.
     * @return A long, multiplied by the correct power of 10 for the current fractional storage for the currency.
     */
    public static long currencyStringToLong(String amount, long defaultValue) {
        Number n = currencyStringToNumber(amount, defaultValue);
        int scale = getFractionDigits();
        int multiple = scale > 0 ? (int) Math.pow(10, scale) : 1;
        BigDecimal b = new BigDecimal(n.toString());
        return b.multiply(new BigDecimal(multiple)).longValue();
    }

    /**
     * Parse the given locale-specific currency string, and return a Number that represents the amount. The Number object
     * then easily allows conversion to primitives or even BigDecimal.
     * <p>
     * Use preferredCurrencyFormat() to get a help string that indicates the preferred input format for the currency.
     * <p>
     * <b>This function is quite tolerant of user input</b>, and will accept anything that is "normal" when writing a
     * currency in that locale.
     *
     * <pre>
     * // locale is en
     * I.currencyStringToNumber(&quot;$1,345.66&quot;, 0); // returns 1345.66
     * I.currencyStringToNumber(&quot;1345.66&quot;, 0); // returns 1345.66
     * // locale is fr
     * I.currencyStringToNumber(&quot;1 345,66&quot;, 0); // returns 1345.66
     * I.currencyStringToNumber(&quot;1345.66 &amp;euro&quot;, 0); // returns 1345.66
     * </pre>
     *
     *
     * @param amount The string (e.g. 100.34) to be parsed
     * @param defaultValue The Number to return if the parsing fails.
     * @param locale The locale to use when parsing
     * @return A Number (e.g. rv.toDouble() == 100.34), or defaultValue if the string isn't understandable.
     */
    public static Number currencyStringToNumber(String amount, Number defaultValue, Locale locale) {
        if (amount == null || amount.length() == 0)
            return defaultValue;
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        try {
            DecimalFormat d = (DecimalFormat) fmt;
            DecimalFormatSymbols symbols = d.getDecimalFormatSymbols();
            if (d.getNegativePrefix().length() > 0)
                amount = amount.replace(d.getNegativePrefix(), "-").trim();
            if (d.getNegativeSuffix().length() > 0)
                amount = amount.replace(d.getNegativeSuffix(), "").trim();
            if (d.getPositivePrefix().length() > 0)
                amount = amount.replace(d.getPositivePrefix(), "").trim();
            if (d.getPositiveSuffix().length() > 0)
                amount = amount.replace(d.getPositiveSuffix(), "").trim();
            d.setPositivePrefix("");
            d.setPositiveSuffix("");
            d.setNegativePrefix("-");
            d.setNegativeSuffix("");
            // In french, the official grouping separator is a Unicode thin space...convert ASCII spaces to thin
            // spaces keeps input conversion from failing....
            if (symbols.getGroupingSeparator() == '\u00a0')
                amount = amount.replace(" ", "\u00a0");
            return fmt.parse(amount);
        } catch (ParseException e) {
            Log.e("Failed to parse currency: " + amount, e.toString());

        }
        return defaultValue;
    }

    /**
     * Convenience call to the above method, automatically uses the default locale
     * @see {@link #currencyStringToNumber(String, Number, Locale) currencyStringToNumber()}
     *
     * @param amount The string (e.g. 100.34) to be parsed
     * @param defaultValue The Number to return if the parsing fails.
     * @return A Number (e.g. rv.toDouble() == 100.34), or defaultValue if the string isn't understandable.
     */
    public static Number currencyStringToNumber(String amount, Number defaultValue) {
        return currencyStringToNumber(amount, defaultValue, Locale.getDefault());
    }

    /**
     * Get a help string that indicates the desired currency input format. This is useful in UI forms:
     *
     * <pre>
     * &lt;input type="text" name="amount"> &lt;%= I.preferredCurrencyFormat() %>
     * </pre>
     * <p>
     * would show something like this:<br>
     * &nbsp;&nbsp;<input type="text">&nbsp;#,###.##
     *
     * @return A String of the form #,###.##
     */
    public static String preferredCurrencyFormat() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.getDefault());
        StringBuilder rv = new StringBuilder();
        if (fmt instanceof DecimalFormat) {
            DecimalFormat cfmt = ((DecimalFormat) fmt);
            int fractional = cfmt.getMaximumFractionDigits();
            int groupSize = cfmt.getGroupingSize();
            DecimalFormatSymbols symbols = cfmt.getDecimalFormatSymbols();
            rv.append("#");
            rv.append(symbols.getGroupingSeparator());
            for (int i = 0; i < groupSize; i++)
                rv.append("#");
            rv.append(symbols.getDecimalSeparator());
            for (int i = 0; i < fractional; i++)
                rv.append("#");
        }
        return rv.toString();
    }

    /**
     * Parse the given locale-specific number, and return a Number object that represents the value.
     * <p>
     * Use preferredNumberFormat() to get a help string that indicates the preferred input format for numbers.
     * <p>
     * In general, this function is very tolerant of user input. Digit groupings are optional, but the fractional
     * separator must be correct.
     *
     * <pre>
     * // in en locale
     * I.stringToNumber(&quot;1,534,100.34&quot;, 0); // returns 1534100.34
     * I.stringToNumber(&quot;1534100.34&quot;, 0); // returns 1534100.34
     * // in fr locale
     * I.stringToNumber(&quot;1 534 100,34&quot;, 0); // returns 1534100.34
     * I.stringToNumber(&quot;1534100,34&quot;, 0); // returns 1534100.34
     * </pre>
     *
     * @param value The string (e.g. 100.34) to be parsed
     * @param defaultValue The Number to return if the parsing fails.
     * @return A Number (e.g. rv.toDouble() == 100.34), or defaultValue if the string isn't understandable.
     */
    public static Number stringToNumber(String value, Number defaultValue) {
        if (value == null || value.length() == 0)
            return defaultValue;
        NumberFormat fmt = NumberFormat.getInstance(Locale.getDefault());
        try {
            value = value.replace(" ", "");
            return fmt.parse(value);
        } catch (ParseException e) {
            Log.d("Failed to parse number: " + value, e.toString());
        }
        return defaultValue;
    }

    /**
     * Used when we need to get a string with symbols and separators from a string without them.
     *
     * @param value String WITHOUT symbols and separators
     * @return String WITH symbols and separators
     */
    public static String stringToString(String value) {
        return numberToCurrencyString(currencyStringToNumber(value, 0));
    }

    /**
     * Takes a string value formatted using the en_US locale and returns it properly formatted in the default locale.
     * This is used to set defaults should the user fail to provide them, so that the default is always the correct string.
     * NOTE!! "Formatted" in this case doesn't include symbols!
     *
     * @param enUS_FormattedString Amount string formatted using en_US
     * @return Amount string formatted using default locale WITHOUT symbols
     */
    public static String getStringFromString(String enUS_FormattedString) {
        final Locale en_US_Locale = new Locale("en_US");
        return numberToCurrencyString(currencyStringToNumber(enUS_FormattedString, 0, en_US_Locale), false);
    }

    /**
     * Returns a help string that indicates the recommended number input format for the current locale. This will be the
     * locale-specific format. The input functions all tolerate plain math numbers (without groupings), though the
     * locale-specific fraction separator is required.
     *
     * @param nFractional Indicate the number of fractional digits wanted. 0 means you want an integer.
     * @return A string representing the recommended number input for the locale.
     * @see CurrencyUtils#preferredCurrencyFormat()
     */
    public static String preferredNumberFormat(int nFractional) {
        NumberFormat fmt = NumberFormat.getInstance(Locale.getDefault());
        StringBuilder rv = new StringBuilder();
        if (fmt instanceof DecimalFormat) {
            DecimalFormat cfmt = ((DecimalFormat) fmt);
            int groupSize = cfmt.getGroupingSize();
            DecimalFormatSymbols symbols = cfmt.getDecimalFormatSymbols();
            rv.append("#");
            rv.append(symbols.getGroupingSeparator());
            for (int i = 0; i < groupSize; i++)
                rv.append("#");
            if (nFractional > 0) {
                rv.append(symbols.getDecimalSeparator());
                for (int i = 0; i < nFractional; i++)
                    rv.append("#");
            }
        }
        return rv.toString();
    }

    /**
     * Convert a number to a string. The returned string is formatted according to the locale to include digit groupings
     * for easy reading.
     *
     * <pre>
     * // in en locale
     * I.numberToString(1294855.234); // returns &quot;1,294,855.234&quot;
     * // in fr locale
     * I.numberToString(1294855.234); // returns &quot;1 294 855,234&quot;
     * // in de locale
     * I.numberToString(1294855.234); // returns &quot;1.294.855,234&quot;
     * </pre>
     *
     * @param d The number to format.
     * @return The number as a string. Tolerates null input (returns 0)
     */
    public static String numberToString(Number d) {
        if (d == null)
            return "0";
        NumberFormat fmt = NumberFormat.getInstance(Locale.getDefault());
        return fmt.format(d).replace("\u00a0", " ");
    }

    /**
     * Get the currency symbol for the current locale.
     *
     * @return A string containing the currency symbol in the current locale.
     */
    public static String currencySign() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.getDefault());
        DecimalFormat d = (DecimalFormat) fmt;
        return d.getDecimalFormatSymbols().getCurrencySymbol();
    }

    /**
     *
     * @param number should be in kobo
     * @return
     */


    /**
     * Use the current locale's understanding of currency to round a double to the correct number of fractional digits.
     *
     * <p>
     * <b>CAUTION:</b> Currency calculations must be done in a way that is consistent with accounting practices. Usually,
     * this means rounding at the end of a sequence of operations (so that rounding errors don't accumulate). The basic
     * rule is to round any currency amount that becomes visible to the user. So, for example:
     *
     * <pre>
     * double discount = 0.0123 * amount; // 1.23% discount
     * String userMessage = I.trf(&quot;You get a {0,currency} discount!&quot;, discount); // currency formatting will show it rounded
     * double roundedDiscount = I.roundCurrency(discount);
     * // now use the rounded number, since that is what they expect.
     * double price = price - roundedDiscount;
     * </pre>
     *
     * @param unroundedNum The number to round
     * @return The same value, but rounded to the correct number of significant fractional digits for the locale's currency.
     */
    public static String roundCurrency(String unroundedNum) {
        DecimalFormat fmt = (DecimalFormat) NumberFormat.getCurrencyInstance();
        // We don't wanna deal with this junk, strip it out
        String unroundedNumber = unroundedNum.replace(fmt.getCurrency().getSymbol(), "")
                .replace(String.valueOf(fmt.getDecimalFormatSymbols().getGroupingSeparator()), "")
                .replace(String.valueOf((char)160), "") // Be sure we get rid of NBSP's
                .trim();
        if(unroundedNumber.equals("-")){
            // In case the user types a '-' then decides to do something that triggers this method, otherwise we crash
            return unroundedNumber;
        }
        // Make sure we only have one decimal separator, if there are more than one then strip all but the first one out
        char decimalSeparator = fmt.getDecimalFormatSymbols().getDecimalSeparator();
        int firstOccurance = unroundedNumber.indexOf(decimalSeparator);
        while(unroundedNumber.lastIndexOf(decimalSeparator) != firstOccurance){
            int lastOccurance = unroundedNumber.lastIndexOf(decimalSeparator);
            unroundedNumber = new StringBuilder(unroundedNumber).deleteCharAt(lastOccurance).toString();
        }
        if(unroundedNumber.equals(String.valueOf(decimalSeparator))) {
            return "";
        }
        if(unroundedNumber.equals("")){
            return unroundedNumber;
        }
        unroundedNumber = unroundedNumber.replace(String.valueOf(fmt.getDecimalFormatSymbols().getDecimalSeparator()), ".");
        fmt.setNegativePrefix("-");
        fmt.setNegativeSuffix("");
        BigDecimal v_bigDec = new BigDecimal(unroundedNumber.trim());
        v_bigDec = v_bigDec.setScale(getFractionDigits(), BigDecimal.ROUND_HALF_UP);
        return fmt.format(v_bigDec.doubleValue())
                .replace(fmt.getCurrency().getSymbol(), "")
                .replace(String.valueOf(fmt.getDecimalFormatSymbols().getGroupingSeparator()), "")
                .replace(String.valueOf((char)160), "") // Once again, get rid of these NBSP's
                .trim();
    }

    /**
     * Checks if a currency string has a value of zero
     * @param n Currency string
     * @return Boolean has value of zero
     */
    public static boolean checkZero(String n) {
        String num = roundCurrency(n);
        if(new BigDecimal(num).compareTo(BigDecimal.ZERO) == 0){
            return true;
        } else {
            return false;
        }
    }

}