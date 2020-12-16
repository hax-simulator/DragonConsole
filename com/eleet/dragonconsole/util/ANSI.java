/*
 * Copyright (c) 2010 3l33t Software Developers, L.L.C.
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

package com.eleet.dragonconsole.util;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * ANSI is a helper class that will process and create a
 * SimpleAttributeSet that has the color settings from a given String and
 * return it to the calling function. This is used to allow the DragonConsole
 * compatability with ANSI.
 *
 * This method will also convert DragonConsole color codes into ANSI compatible
 * codes (although if no compatible color can be found it will use the default
 * colors.
 * @author Brandon E Buck
 */
public class ANSI {
    public static final String ESCAPE = "\033"; // ANSI Escape Character that starts commands

    public static final Color[] COLOR_TABLE;

    public static final Color BLACK;
    public static final Color RED;
    public static final Color GREEN;
    public static final Color YELLOW;
    public static final Color BLUE;
    public static final Color MAGENTA;
    public static final Color CYAN;
    public static final Color WHITE;

    public static final Color INTENSE_BLACK;
    public static final Color INTENSE_RED;
    public static final Color INTENSE_GREEN;
    public static final Color INTENSE_YELLOW;
    public static final Color INTENSE_BLUE;
    public static final Color INTENSE_MAGENTA;
    public static final Color INTENSE_CYAN;
    public static final Color INTENSE_WHITE;


    public static final Color INTENSE_ORANGE;
    public static final Color ORANGE;

    public static final Color INTENSE_PURPLE;
    public static final Color PURPLE;
    public static final Color INTENSE_GOLD;
    public static final Color GOLD;

    /**
     * Takes an ANSI Code as a String and breaks it apart and then creates a
     * SimpleAttributeSet with the proper Foreground and Background color.
     * @param old The current ANSI Style so any changes not specified are
     *  carried over.
     * @param string The String containing the ANSI code that needs to be
     *  processed.
     * @param defaultStyle The Default Style for the Console, used for ANSI
     *  codes 39 and 49.
     * @return Returns the SimpleAttributeSet with the proper colors of the
     *  ANSI code.
     */
    public static SimpleAttributeSet getANSIAttribute(SimpleAttributeSet old,
            String string, Style defaultStyle) {
        SimpleAttributeSet ANSI = old;

        if (ANSI == null)
            ANSI = new SimpleAttributeSet();

        if (string.length() > 3) {
            string = string.substring(2); // Cut off the "\033[";
            string = string.substring(0, string.length() - 1); // Remove the "m" from the end

        } else
            return null;

        String codes[] = string.split(";");

        boolean brighter = false;
        for (int i = 0; i < codes.length; i++) {

            if (codes[i].matches("[\\d]*")) {
                int code = Integer.parseInt(codes[i]);

                switch (code) {
                    case 0:
                        brighter = false;
                        ANSI = new SimpleAttributeSet();
                        break;
                    case 1:
                        brighter = true;
                        break;
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                        StyleConstants.setForeground(ANSI, getColorFromANSICode(code, brighter));
                        break;
                    case 38:
                    	try {
                    		if ("5".equals(codes[i+1])) {
                    			StyleConstants.setForeground(ANSI, COLOR_TABLE[Integer.parseInt(codes[i+2])]);
                    			i+=2;
                    		} else if ("2".equals(codes[i+1])) {
                    			StyleConstants.setForeground(ANSI, new Color(Integer.parseInt(codes[i+2]), Integer.parseInt(codes[i+3]), Integer.parseInt(codes[i+4])));
                    			i+=4;
                    		}
                    	} catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
                    	}
                    	break;
                    case 39:
                        StyleConstants.setForeground(ANSI, StyleConstants.getForeground(defaultStyle));
                        break;
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                        StyleConstants.setBackground(ANSI, getColorFromANSICode(code, false));
                        break;
                    case 48:
                    	try {
                    		if ("5".equals(codes[i+1])) {
                    			StyleConstants.setBackground(ANSI, COLOR_TABLE[Integer.parseInt(codes[i+2])]);
                    			i+=2;
                    		} else if ("2".equals(codes[i+1])) {
                    			StyleConstants.setBackground(ANSI, new Color(Integer.parseInt(codes[i+2]), Integer.parseInt(codes[i+3]), Integer.parseInt(codes[i+4])));
                    			i+=4;
                    		}
                    	} catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
                    	}
                    	break;
                    case 49:
                        StyleConstants.setBackground(ANSI, StyleConstants.getBackground(defaultStyle));
                        break;
                }
            }
        }

        return ANSI;
    }

    /**
     * Takes a String containing a DCCC and convert it into its equivalent
     * ANSI Color Code. It does this by comparing the color associated with
     * the characters in the DCCC to the ANSI colors until it finds a match and
     * then creates the proper code, adding it to the return String. If no match
     * is made it will use the default color code 39 (for FOREGROUND) or 49
     * (for BACKGROUND).
     * @param DCCode The String containing the three character DCCC.
     * @param colors The ArrayList of TextColors that have been added to the
     *  console.
     * @return The ANSI equivalent to the given DCCC.
     */
    public static String getANSICodeFromDCCode(String DCCode, ArrayList<TextColor> colors) {
        if (DCCode.length() == 3) {
            DCCode = DCCode.substring(1); // Remove the & from the code
            char foreground = DCCode.charAt(0);
            char background = DCCode.charAt(1);

            String code = ESCAPE + "[0;";

            if (foreground == '0')
                code += "0";
            else if (foreground == '-')
                code += "";
            else {
                for (int i = 0; i < colors.size(); i++) {
                    TextColor tc = null;
                    if (colors.get(i).equals(TextColor.getTestTextColor(foreground))) {
                        tc = colors.get(i);
                        for (int x = 0; x < COLOR_TABLE.length; x++) {
                        	if (x < 8) {
                        		if (tc.getColor().equals(COLOR_TABLE[x])) {
                                    code += "3" + x;
                                    break; // Found, no need to continue

                                } else if (tc.getColor().equals(COLOR_TABLE[x+8])) {
                                    code += "1;3" + x;
                                    break; // Found, no need to continue
                                }
                        	} else if (x == 8) {
                        		x = 15;
                        	} else if (x >= 16) {
                        		if (tc.getColor().equals(COLOR_TABLE[x])) {
                        			code += "38;5;" + x;
                                    break; // Found, no need to continue
                        		}
                        	}
                        }
                        break; // Found, no need to continue
                    }
                }
            }

            if (code.charAt(code.length() - 1) != ';')
                code += ";";

            if (background == '0')
                code += "0";
            else if (background == '-')
                code += "";
            else {
                for (int i = 0; i < colors.size(); i++) {
                    TextColor tc = null;
                    if (colors.get(i).equals(TextColor.getTestTextColor(background))) {
                        tc = colors.get(i);
                        for (int x = 0; x < COLOR_TABLE.length; x++) {
                        	if (x < 8) {
                        		if (tc.getColor().equals(COLOR_TABLE[x])) {
                                    code += "4" + x;
                                    break; // Found, no need to continue

                                } else if (tc.getColor().equals(COLOR_TABLE[x+8])) {
                                    code += "1;4" + x;
                                    break; // Found, no need to continue
                                }
                        	} else if (x == 8) {
                            	x = 15;
                        	} else if (x >= 16) {
                        		if (tc.getColor().equals(COLOR_TABLE[x])) {
                        			code += "48;5;" + x;
                                    break; // Found, no need to continue
                        		}
                        	}
                        }
                        break; // Found, no need to continue
                    }
                }
            }

            if (code.charAt(code.length() - 1) == ';')
                code = code.substring(0, code.length() - 1);

            if (code.equals(ESCAPE + "[0;"))
                return ESCAPE + "[39;49m";
            else
                return code + "m";
        }

        return ESCAPE + "[39;49m";
    }

    /**
     * Takes a String containing multiple DCCCs (like standard output for the
     * console) and replaces the DCCC with it's equivalent ANSI Code and returns
     * the String containing the ANSI Codes. This method uses
     * <code>getANSICodeFromDCCode()</code> to convert each DCCC into it's
     * ANSI code.
     * @param string The String that the programmer wishes to convert.
     * @param colors The ArrayList of TextColors that have been added to the
     *  console.
     * @param colorCodeChar The default colorCodeChar (used for determining a
     *  DCCC).
     * @return Returns the String after each DCCC has been converted into it's
     *  ANSI equivalent.
     */
    public static String convertDCtoANSIColors(String string, ArrayList<TextColor> colors, char colorCodeChar) {
        StringBuilder buffer = new StringBuilder(string);

        for (int i = 0; i < buffer.length(); i++) {
            if (buffer.charAt(i) == colorCodeChar) {
                if ((i + 1) < buffer.length() && (buffer.charAt(i + 1) == colorCodeChar)) {
                    i += 1; // Jump past the &&

                } else {
                    String code = buffer.substring(i, (i + 3));

                    code = getANSICodeFromDCCode(code, colors);
                    int length = code.length();

                    buffer = buffer.replace(i, (i + 3), code);
                    i = i + length - 1;
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Takes a String containing an ANSI Code and returns it's equivalent DCCC.
     * It does this by comparing the Color associated to the ANSI code to each
     * code contained in the list of TextColors and upon a successful match it
     * pulls the Character out of the TextColor and adds it to the DCCC String
     * it returns. If no match is found it uses the character representing the
     * default style set by the console.
     * @param code The ANSI code to convert into a DCCC.
     * @param colors The ArrayList of TextColors that have been added to the
     *  console.
     * @param colorCodeChar The colorCodeChar to place at the beginning of each
     *  DCCC.
     * @param defaultStyle The Default Style set in the Console, used if no
     *  match can be found.
     * @return Returns the DCCC equivalent of the given ANSI code.
     */
    private static String getDCCodeFromANSICode(String code, ArrayList<TextColor> colors, char colorCodeChar, String defaultStyle) {
        boolean brighter = false;
        code = code.substring(2, code.length()); // Cut off the "\033[" and "m"
        char foreground = ' ';
        char background = ' ';

        String colorCodes[] = code.split(";");
        for (int i = 0; i < colorCodes.length; i++) {
            if (colorCodes[i].matches("[\\d]*")) {
                int colorCode = Integer.parseInt(colorCodes[i]);
                if (colorCode == 0)
                    brighter = false;
                else if (colorCode == 1)
                    brighter = true;
                else if (colorCode >= 30 && colorCode <= 39) {
                    if (colorCode == 39)
                        foreground = defaultStyle.charAt(0);
                    else if (colorCode >= 30 && colorCode <= 37) {
                        foreground = getDCCharCodeFromColor(getColorFromANSICode(colorCode, brighter), colors);
                        brighter = false;
                    }
                } else if (colorCode >= 40 && colorCode <= 49) {
                    if (colorCode == 49)
                        background = defaultStyle.charAt(1);
                    else if (colorCode >= 40 && colorCode <= 47)
                        background = getDCCharCodeFromColor(getColorFromANSICode(colorCode, false), colors);
                }
            }
        }

        if (foreground == ' ')
            foreground = defaultStyle.charAt(0);
        if (background == ' ')
            background = defaultStyle.charAt(1);

        return "" + colorCodeChar + foreground + background;
    }

    /**
     * Takes a Color and finds it's corresponding TextColor in the list of
     * TextColors that have been added to the Console (if there is one) and
     * returns the <code>charCode</code> associated with the Color given. If
     * no Color can be matched then it returns a blank character (' ').
     * @param color The Color to find in the list of TextColors.
     * @param colors The ArrayList of TextColors that have been added to the
     *  console.
     * @return The <code>charCode</code> associated with the given color, or
     *  a blank char (' ') if no match is found.
     */
    private static char getDCCharCodeFromColor(Color color,
            ArrayList<TextColor> colors) {
        TextColor test = TextColor.getTestTextColor(color);

        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).equals(test)) {
                return colors.get(i).getCharCode();
            }
        }

        return ' ';
    }

    /**
     * Takes a String containing ANSI Codes and converts replaces the ANSI code
     * with it's equivalent DCCC.
     * @param string The String containing then ANSI code the programmer wishes
     *  to convert to DCCCs.
     * @param colors The ArrayList of TextColors that have been added to the
     *  Console.
     * @param colorCodeChar The <code>colorCodeChar</code> set in the
     *  console.
     * @param defaultStyle The two character Default Style set in the console.
     * @return Returns the String after each ANSI Code has been converted to
     *  it's DCCC equivalent.
     */
    public static String convertANSIToDCColors(String string,
            ArrayList<TextColor> colors, char colorCodeChar,
            String defaultStyle) {
        StringBuilder buffer = new StringBuilder(string);

        for (int i = 0; i < buffer.length(); i++) {
            if (buffer.charAt(i) == '\033') {
                if (buffer.indexOf("m", i) < buffer.length()) {
                    String code = buffer.substring(i, buffer.indexOf("m", i)); // The Color Code
                    int end = i + code.length() + 1;
                    code = getDCCodeFromANSICode(code, colors, colorCodeChar,
                            defaultStyle);

                    buffer = buffer.replace(i, end, code);
                    i = i + 2;
                }
            }
        }

        return buffer.toString();
    }

    /**
     * This method returns the Color associated with the given ANSI Code, it
     * also takes a boolean to test for color intensity. Returns null if
     * no color is found.
     * @param code The ANSI Code the color is requested for.
     * @param brighter <code>true</code> if intensity is set to bold or
     *  <code>false</code> if not.
     * @return Returns the Color associated with the ANSI code or null if none
     *  is found.
     */
    private static Color getColorFromANSICode(int code, boolean brighter) {
        switch (code) {
            case 30:
            case 40:
                if (brighter)
                    return INTENSE_BLACK;
                else
                    return BLACK;
            case 31:
            case 41:
                if (brighter)
                    return INTENSE_RED;
                else
                    return RED;
            case 32:
            case 42:
                if (brighter)
                    return INTENSE_GREEN;
                else
                    return GREEN;
            case 33:
            case 43:
                if (brighter)
                    return INTENSE_YELLOW;
                else
                    return YELLOW;
            case 34:
            case 44:
                if (brighter)
                    return INTENSE_BLUE;
                else
                    return BLUE;
            case 35:
            case 45:
                if (brighter)
                    return INTENSE_MAGENTA;
                else
                    return MAGENTA;
            case 36:
            case 46:
                if (brighter)
                    return INTENSE_CYAN;
                else
                    return CYAN;
            case 37:
            case 47:
                if (brighter)
                    return INTENSE_WHITE;
                else
                    return WHITE;
            default:
                return null;
        }
    }

    static {
    	COLOR_TABLE = new Color[256];
    	int index = 0;
    	COLOR_TABLE[index++] = Color.BLACK;
    	COLOR_TABLE[index++] = Color.RED.darker();
    	COLOR_TABLE[index++] = Color.GREEN.darker();
    	COLOR_TABLE[index++] = Color.YELLOW.darker();
    	COLOR_TABLE[index++] = new Color(66, 66, 255).darker();
    	COLOR_TABLE[index++] = Color.MAGENTA.darker();
    	COLOR_TABLE[index++] = Color.CYAN.darker();
    	COLOR_TABLE[index++] = Color.GRAY.brighter();

    	COLOR_TABLE[index++] = Color.GRAY.darker();
    	COLOR_TABLE[index++] = Color.RED;
    	COLOR_TABLE[index++] = Color.GREEN;
    	COLOR_TABLE[index++] = Color.YELLOW;
    	COLOR_TABLE[index++] = new Color(66, 66, 255);
    	COLOR_TABLE[index++] = Color.MAGENTA;
    	COLOR_TABLE[index++] = Color.CYAN;
    	COLOR_TABLE[index++] = Color.WHITE;

    	int[] rgbValues = new int[] {0,95,135,175,215,255};
    	for (int r = 0; r < rgbValues.length; r++)
    		for (int g = 0; g < rgbValues.length; g++)
    			for (int b = 0; b < rgbValues.length; b++)
    				COLOR_TABLE[index++] = new Color(rgbValues[r], rgbValues[g], rgbValues[b]);

    	for (int rgb = 8; rgb <= 238; rgb+=10)
    		COLOR_TABLE[index++] = new Color(rgb, rgb, rgb);

    	BLACK = COLOR_TABLE[0];
	    RED = COLOR_TABLE[1];
	    GREEN = COLOR_TABLE[2];
	    YELLOW = COLOR_TABLE[3];
	    BLUE = COLOR_TABLE[4];
	    MAGENTA = COLOR_TABLE[5];
	    CYAN = COLOR_TABLE[6];
	    WHITE = COLOR_TABLE[7];

	    INTENSE_BLACK = COLOR_TABLE[8];
	    INTENSE_RED = COLOR_TABLE[9];
	    INTENSE_GREEN = COLOR_TABLE[10];
	    INTENSE_YELLOW = COLOR_TABLE[11];
	    INTENSE_BLUE = COLOR_TABLE[12];
	    INTENSE_MAGENTA = COLOR_TABLE[13];
	    INTENSE_CYAN = COLOR_TABLE[14];
	    INTENSE_WHITE = COLOR_TABLE[15];

	    INTENSE_ORANGE = COLOR_TABLE[214];
	    ORANGE = COLOR_TABLE[136];
	    INTENSE_PURPLE = COLOR_TABLE[93];
	    PURPLE = COLOR_TABLE[54];
	    INTENSE_GOLD = COLOR_TABLE[227];
	    GOLD = COLOR_TABLE[143];
    }
}
