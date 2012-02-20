/*
 * Copyright 2012 PRODYNA AG
 *
 * Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/eclipse-1.0.php or
 * http://www.nabucco.org/License.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nabucco.adapter.lucene.impl.connector.ra.spi.util;

/**
 * LuceneAccentConverter
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class LuceneAccentConverter {

    /**
     * Private constructor must not be invoked.
     */
    private LuceneAccentConverter() {
    }

    /**
     * Removes accents like 'ä', 'ö', 'ü' or 'ß' from the given string.
     * 
     * @param input
     *            the input string
     * 
     * @return the string without special characters
     */
    public final static String removeAccents(String input) {

        final StringBuffer output = new StringBuffer();

        for (int i = 0; i < input.length(); i++) {

            switch (input.charAt(i)) {
            case '\u00C0': // À
            case '\u00C1': // Á
            case '\u00C2': // Â
            case '\u00C3': // Ã
            case '\u00C5': // Å
                output.append("A");
                break;
            case '\u00C4': // Ä
            case '\u00C6': // Æ
                output.append("AE");
                break;
            case '\u00C7': // Ç
                output.append("C");
                break;
            case '\u00C8': // È
            case '\u00C9': // É
            case '\u00CA': // Ê
            case '\u00CB': // Ë
                output.append("E");
                break;
            case '\u00CC': // Ì
            case '\u00CD': // Í
            case '\u00CE': // Î
            case '\u00CF': // Ï
                output.append("I");
                break;
            case '\u00D0': // Ð
                output.append("D");
                break;
            case '\u00D1': // Ñ
                output.append("N");
                break;
            case '\u00D2': // Ò
            case '\u00D3': // Ó
            case '\u00D4': // Ô
            case '\u00D5': // Õ
            case '\u00D8': // Ø
                output.append("O");
                break;
            case '\u00D6': // Ö
            case '\u0152': // Œ
                output.append("OE");
                break;
            case '\u00DE': // Þ
                output.append("TH");
                break;
            case '\u00D9': // Ù
            case '\u00DA': // Ú
            case '\u00DB': // Û
                output.append("U");
                break;
            case '\u00DC': // Ü
                output.append("UE");
                break;
            case '\u00DD': // Ý
            case '\u0178': // Ÿ
                output.append("Y");
                break;
            case '\u00E0': // à
            case '\u00E1': // á
            case '\u00E2': // â
            case '\u00E3': // ã
            case '\u00E5': // å
                output.append("a");
                break;
            case '\u00E4': // ä
            case '\u00E6': // æ
                output.append("ae");
                break;
            case '\u00E7': // ç
                output.append("c");
                break;
            case '\u00E8': // è
            case '\u00E9': // é
            case '\u00EA': // ê
            case '\u00EB': // ë
                output.append("e");
                break;
            case '\u00EC': // ì
            case '\u00ED': // í
            case '\u00EE': // î
            case '\u00EF': // ï
                output.append("i");
                break;
            case '\u00F0': // ð
                output.append("d");
                break;
            case '\u00F1': // ñ
                output.append("n");
                break;
            case '\u00F2': // ò
            case '\u00F3': // ó
            case '\u00F4': // ô
            case '\u00F5': // õ
            case '\u00F8': // ø
                output.append("o");
                break;
            case '\u00F6': // ö
            case '\u0153': // œ
                output.append("oe");
                break;
            case '\u00DF': // ß
                output.append("ss");
                break;
            case '\u00FE': // þ
                output.append("th");
                break;
            case '\u00F9': // ù
            case '\u00FA': // ú
            case '\u00FB': // û
                output.append("u");
                break;
            case '\u00FC': // ü
                output.append("ue");
                break;
            case '\u00FD': // ý
            case '\u00FF': // ÿ
                output.append("y");
                break;
            default:
                output.append(input.charAt(i));
                break;
            }
        }
        return output.toString();
    }
}
