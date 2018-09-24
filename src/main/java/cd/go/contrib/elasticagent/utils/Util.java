/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagent.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

import static cd.go.contrib.elasticagent.Constants.KUBERNETES_POD_CREATION_TIME_FORMAT;
import static cd.go.contrib.elasticagent.KubernetesPlugin.LOG;

public class Util {

    public static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(KUBERNETES_POD_CREATION_TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    public static String readResource(String resourceFile) {
        return new String(readResourceBytes(resourceFile), StandardCharsets.UTF_8);
    }

    public static byte[] readResourceBytes(String resourceFile) {
        try (InputStream is = Util.class.getResourceAsStream(resourceFile)) {
            return readFully(is);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }

    private static byte[] readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public static String pluginId() {
        String s = readResource("/plugin.properties");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(s));
            return (String) properties.get("pluginId");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String fullVersion() {
        String s = readResource("/version.properties");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(s));
            return properties.getProperty("fullVersion");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readableSize(Long memory) {
        Long size = memory * 1024;
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static TypeAdapter<Number> IntTypeAdapter = new TypeAdapter<Number>() {

        @Override
        public void write(JsonWriter out, Number value)
                throws IOException {
            out.value(value);
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                String result = in.nextString();
                if ("".equals(result)) {
                    return null;
                }
                return Integer.parseInt(result);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    };

    public static TypeAdapter<Boolean> BooleanTypeAdapter = new TypeAdapter<Boolean>() {

        @Override
        public void write(JsonWriter out, Boolean value)
                throws IOException {
            out.value(value);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return false;
            }
            String result = in.nextString();
            if ("".equals(result)) {
                return false;
            }
            return Boolean.parseBoolean(result);
        }
    };

    public static String catFile(String filePath) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            return new String(encoded, Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
