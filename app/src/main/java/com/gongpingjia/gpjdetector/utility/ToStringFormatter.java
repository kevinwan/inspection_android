/*
 * Copyright (C) 2013 Friederike Wild <friederike.wild@devmob.de>
 * Created 26.10.2013
 * 
 * https://github.com/friederikewild/DroidLogger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gongpingjia.gpjdetector.utility;

import java.util.Collection;
import java.util.Map;

import android.database.Cursor;

/**
 * Formatter to create readable strings from common used objects that
 * lack their own useful toString method. Useful to log native arrays, Android Cursors or
 * objects that may be null. Works as well for collections like List or Map.
 * 
 * Call ToStringFormatter.getString() with any kind of object and let it handle the type check.
 * Get a readable string containing the available content instead of the default output that often only
 * contains the class name and the object id.
 * 
 * @author Friederike Wild
 */
public class ToStringFormatter
{
    public static String getConcatenatedString(Object object1, Object object2)
    {
        return new StringBuilder()
        .append(getString(object1))
        .append(getString(object2))
        .toString();
    }

    public static String getString(Collection<?> collection)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Collection: (");
        int entryCount = 0;
        for (Object entry : collection)
        {
            sb.append(getString(entry));
            entryCount++;
            addEntrySeparatorIfNeeded(entryCount, collection.size(), sb);
        }
        sb.append(")");
        return sb.toString();
    }

    public static String getString(Map<?, ?> map)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Map: {");
        int entryCount = 0;
        for (Object key : map.keySet())
        {
            sb.append(getString(key) + "=>" + getString(map.get(key)));
            entryCount++;
            addEntrySeparatorIfNeeded(entryCount, map.size(), sb);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String getString(Cursor c)
    {
        StringBuilder sb = new StringBuilder();

        if (c.getCount() == 0)
        {
            sb.append("Empty cursor {}");
        }
        else
        {
            sb.append("Cursor: {");
            // store current position
            int currentPos = c.getPosition();
            c.moveToFirst();

            String[] colNames = c.getColumnNames();
            do
            {
                int colCount = 0;
                for (String colName : colNames)
                {
                    int rowId = c.getColumnIndex(colName);
                    if (rowId != -1)
                    {
                        // With sqlite all column types are stored as string and can be requested as such
                        sb.append(colName + "=>" + c.getString(rowId));
                    }
                    colCount++;
                    addEntrySeparatorIfNeeded(colCount, colNames.length, sb);
                }

                if (!c.isLast())
                {
                    sb.append("; ");
                }

            }
            while (c.moveToNext());

            // reset position:
            c.moveToPosition(currentPos);

            sb.append("}");
        }
        return sb.toString();
    }

    public static String getString(Throwable t)
    {
        return android.util.Log.getStackTraceString(t);
    }

    /**
     * Overloaded method for all otherwise unmapped methods.
     * In case a specific typed object is stored in a "Object" variable
     * that overloaded method check is not working due to the determination
     * of the correct type at compile-time.
     * 
     * Therefore this method checks for all other types being instanceof as well.
     * 
     * @param var
     * @return
     */
    public static String getString(Object var)
    {
        if (var == null)
        {
            return "NULL";
        }

        // Java arrays always start with a open bracket followed by the type.
        if (var.getClass().toString().startsWith("class ["))
        {
            StringBuilder sb = new StringBuilder();
            try
            {
                dumpArrayToStringBuilder(var, sb);
            }
            catch (Throwable t)
            {
                // Reset any half written output
                sb.delete(0, sb.length());
                sb.append("Problem logging class with type " + var.getClass().toString());
            }

            return sb.toString();
        }

        else if (var instanceof Collection)
        {
            return getString((Collection<?>)var);
        }
        
        else if (var instanceof Map)
        {
            return getString((Map<?, ?>)var);
        }

        else if (var instanceof Cursor)
        {
            return getString((Cursor)var);
        }

        else if (var instanceof Throwable)
        {
            return getString((Throwable)var);
        }
        

        // Else: for all other classes call through to native toString method 
        return var.toString();
    }

    private static void dumpArrayToStringBuilder(Object var, StringBuilder sb)
    {
        sb.append("[");

        if (var.getClass().toString().startsWith("class [Z")) // Boolean array prefix
        {
            boolean[] convertedArray = (boolean[]) var;
            dumpBoolArrayToStringBuilder(convertedArray, sb);
        }
        else if (var.getClass().toString().startsWith("class [B")) // Byte array prefix
        {
            byte[] convertedArray = (byte[]) var;
            dumpByteArrayToStringBuilder(convertedArray, sb);
        }
        else if (var.getClass().toString().startsWith("class [S")) // Short array prefix
        {
            short[] convertedArray = (short[]) var;
            dumpShortArrayToStringBuilder(convertedArray, sb);
        }
        else if (var.getClass().toString().startsWith("class [I")) // Int array prefix
        {
            int[] convertedArray = (int[]) var;
            dumpIntArrayToStringBuilder(convertedArray, sb);
        }
        else if (var.getClass().toString().startsWith("class [J")) // Long array prefix
        {
            long[] convertedArray = (long[]) var;
            dumpLongArrayToStringBuilder(convertedArray, sb);
        }
        else if (var.getClass().toString().startsWith("class [F")) // Float array prefix
        {
            float[] convertedArray = (float[]) var;
            dumpFloatArrayToStringBuilder(convertedArray, sb);
        }
        else if (var.getClass().toString().startsWith("class [D")) // Double array prefix
        {
            double[] convertedArray = (double[]) var;
            dumpDoubleArrayToStringBuilder(convertedArray, sb);
        }
        else if (var.getClass().toString().startsWith("class [C")) // Char array prefix
        {
            char[] convertedArray = (char[]) var;
            dumpCharArrayToStringBuilder(convertedArray, sb);
        }
        else
        {
            // Assume the type is supporting toString
            Object[] convertedArray = (Object[]) var;
            dumpObjectArrayToStringBuilder(convertedArray, sb);
        }

        sb.append("]");
    }

    private static void dumpBoolArrayToStringBuilder(boolean[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpByteArrayToStringBuilder(byte[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpShortArrayToStringBuilder(short[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpIntArrayToStringBuilder(int[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpLongArrayToStringBuilder(long[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpFloatArrayToStringBuilder(float[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpDoubleArrayToStringBuilder(double[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpCharArrayToStringBuilder(char[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(String.valueOf(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void dumpObjectArrayToStringBuilder(Object[] convertedArray, StringBuilder sb)
    {
        for (int i = 0; i < convertedArray.length; i++)
        {
            sb.append(getString(convertedArray[i]));
            addEntrySeparatorIfNeeded(i, convertedArray.length - 1, sb);
        }
    }

    private static void addEntrySeparatorIfNeeded(int entryCount, int maxSize, StringBuilder sb)
    {
        if (entryCount < maxSize)
        {
            sb.append(", ");
        }
    }
}
