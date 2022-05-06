//package com.k8sclient.demo.k8sutils;
//
//import com.google.gson.*;
//import com.google.gson.internal.bind.util.ISO8601Utils;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.stream.JsonWriter;
//import io.gsonfire.GsonFireBuilder;
//import io.kubernetes.client.gson.V1StatusPreProcessor;
//import io.kubernetes.client.openapi.models.V1Status;
//import okio.ByteString;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.lang.reflect.Type;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.ParsePosition;
//import java.time.LocalDate;
//import java.time.OffsetDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeFormatterBuilder;
//import java.time.format.DateTimeParseException;
//import java.time.temporal.ChronoField;
//import java.util.Date;
//import java.util.Map;
//
///**
// * @author liyang(leonasli)
// * @className JSON
// * @description TODO
// * @create 2022/4/27 15:29
// **/
//public class JSON {
//    private Gson gson;
//    private boolean isLenientOnJson = false;
//    private static final DateTimeFormatter RFC3339MICRO_FORMATTER;
//    private DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();
//    private SqlDateTypeAdapter sqlDateTypeAdapter = new SqlDateTypeAdapter();
//    private OffsetDateTimeTypeAdapter offsetDateTimeTypeAdapter;
//    private LocalDateTypeAdapter localDateTypeAdapter;
//    private ByteArrayAdapter byteArrayAdapter;
//
//    public static GsonBuilder createGson() {
//        GsonFireBuilder fireBuilder = new GsonFireBuilder();
//        GsonBuilder builder = fireBuilder.registerPreProcessor(V1Status.class, new V1StatusPreProcessor()).createGsonBuilder();
//        return builder;
//    }
//
//    private static String getDiscriminatorValue(JsonElement readElement, String discriminatorField) {
//        JsonElement element = readElement.getAsJsonObject().get(discriminatorField);
//        if (null == element) {
//            throw new IllegalArgumentException("missing discriminator field: <" + discriminatorField + ">");
//        } else {
//            return element.getAsString();
//        }
//    }
//
//    private static Class getClassByDiscriminator(Map classByDiscriminatorValue, String discriminatorValue) {
//        Class clazz = (Class)classByDiscriminatorValue.get(discriminatorValue);
//        if (null == clazz) {
//            throw new IllegalArgumentException("cannot determine model class of name: <" + discriminatorValue + ">");
//        } else {
//            return clazz;
//        }
//    }
//
//    public JSON() {
//        this.offsetDateTimeTypeAdapter = new OffsetDateTimeTypeAdapter(RFC3339MICRO_FORMATTER);
//        this.localDateTypeAdapter = new LocalDateTypeAdapter();
//        this.byteArrayAdapter = new ByteArrayAdapter();
//        this.gson = createGson().registerTypeAdapter(Date.class, this.dateTypeAdapter).registerTypeAdapter(java.sql.Date.class, this.sqlDateTypeAdapter).registerTypeAdapter(OffsetDateTime.class, this.offsetDateTimeTypeAdapter).registerTypeAdapter(LocalDate.class, this.localDateTypeAdapter).registerTypeAdapter(byte[].class, this.byteArrayAdapter).create();
//    }
//
//    public Gson getGson() {
//        return this.gson;
//    }
//
//    public io.kubernetes.client.openapi.JSON setGson(Gson gson) {
//        this.gson = gson;
//        return this;
//    }
//
//    public io.kubernetes.client.openapi.JSON setLenientOnJson(boolean lenientOnJson) {
//        this.isLenientOnJson = lenientOnJson;
//        return this;
//    }
//
//    public String serialize(Object obj) {
//        return this.gson.toJson(obj);
//    }
//
//    public <T> T deserialize(String body, Type returnType) {
//        try {
//            if (this.isLenientOnJson) {
//                JsonReader jsonReader = new JsonReader(new StringReader(body));
//                jsonReader.setLenient(true);
//                return this.gson.fromJson(jsonReader, returnType);
//            } else {
//                return this.gson.fromJson(body, returnType);
//            }
//        } catch (JsonParseException var4) {
//            if (returnType.equals(String.class)) {
//                return body;
//            } else {
//                throw var4;
//            }
//        }
//    }
//
//    public io.kubernetes.client.openapi.JSON setOffsetDateTimeFormat(DateTimeFormatter dateFormat) {
//        this.offsetDateTimeTypeAdapter.setFormat(dateFormat);
//        return this;
//    }
//
//    public io.kubernetes.client.openapi.JSON setLocalDateFormat(DateTimeFormatter dateFormat) {
//        this.localDateTypeAdapter.setFormat(dateFormat);
//        return this;
//    }
//
//    public io.kubernetes.client.openapi.JSON setDateFormat(DateFormat dateFormat) {
//        this.dateTypeAdapter.setFormat(dateFormat);
//        return this;
//    }
//
//    public io.kubernetes.client.openapi.JSON setSqlDateFormat(DateFormat dateFormat) {
//        this.sqlDateTypeAdapter.setFormat(dateFormat);
//        return this;
//    }
//
//    static {
//        RFC3339MICRO_FORMATTER = (new DateTimeFormatterBuilder()).parseDefaulting(ChronoField.OFFSET_SECONDS, 0L).append(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")).optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 6, 6, true).optionalEnd().appendLiteral("Z").toFormatter();
//    }
//
//    public static class DateTypeAdapter extends TypeAdapter<Date> {
//        private DateFormat dateFormat;
//
//        public DateTypeAdapter() {
//        }
//
//        public DateTypeAdapter(DateFormat dateFormat) {
//            this.dateFormat = dateFormat;
//        }
//
//        public void setFormat(DateFormat dateFormat) {
//            this.dateFormat = dateFormat;
//        }
//
//        public void write(JsonWriter out, Date date) throws IOException {
//            if (date == null) {
//                out.nullValue();
//            } else {
//                String value;
//                if (this.dateFormat != null) {
//                    value = this.dateFormat.format(date);
//                } else {
//                    value = ISO8601Utils.format(date, true);
//                }
//
//                out.value(value);
//            }
//
//        }
//
//        public Date read(JsonReader in) throws IOException {
//            try {
//                switch(in.peek()) {
//                    case NULL:
//                        in.nextNull();
//                        return null;
//                    default:
//                        String date = in.nextString();
//
//                        try {
//                            return this.dateFormat != null ? this.dateFormat.parse(date) : ISO8601Utils.parse(date, new ParsePosition(0));
//                        } catch (ParseException var4) {
//                            throw new JsonParseException(var4);
//                        }
//                }
//            } catch (IllegalArgumentException var5) {
//                throw new JsonParseException(var5);
//            }
//        }
//    }
//
//    public static class SqlDateTypeAdapter extends TypeAdapter<java.sql.Date> {
//        private DateFormat dateFormat;
//
//        public SqlDateTypeAdapter() {
//        }
//
//        public SqlDateTypeAdapter(DateFormat dateFormat) {
//            this.dateFormat = dateFormat;
//        }
//
//        public void setFormat(DateFormat dateFormat) {
//            this.dateFormat = dateFormat;
//        }
//
//        public void write(JsonWriter out, java.sql.Date date) throws IOException {
//            if (date == null) {
//                out.nullValue();
//            } else {
//                String value;
//                if (this.dateFormat != null) {
//                    value = this.dateFormat.format(date);
//                } else {
//                    value = date.toString();
//                }
//
//                out.value(value);
//            }
//
//        }
//
//        public java.sql.Date read(JsonReader in) throws IOException {
//            switch(in.peek()) {
//                case NULL:
//                    in.nextNull();
//                    return null;
//                default:
//                    String date = in.nextString();
//
//                    try {
//                        return this.dateFormat != null ? new java.sql.Date(this.dateFormat.parse(date).getTime()) : new java.sql.Date(ISO8601Utils.parse(date, new ParsePosition(0)).getTime());
//                    } catch (ParseException var4) {
//                        throw new JsonParseException(var4);
//                    }
//            }
//        }
//    }
//
//    public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
//        private DateTimeFormatter formatter;
//
//        public LocalDateTypeAdapter() {
//            this(DateTimeFormatter.ISO_LOCAL_DATE);
//        }
//
//        public LocalDateTypeAdapter(DateTimeFormatter formatter) {
//            this.formatter = formatter;
//        }
//
//        public void setFormat(DateTimeFormatter dateFormat) {
//            this.formatter = dateFormat;
//        }
//
//        public void write(JsonWriter out, LocalDate date) throws IOException {
//            if (date == null) {
//                out.nullValue();
//            } else {
//                out.value(this.formatter.format(date));
//            }
//
//        }
//
//        public LocalDate read(JsonReader in) throws IOException {
//            switch(in.peek()) {
//                case NULL:
//                    in.nextNull();
//                    return null;
//                default:
//                    String date = in.nextString();
//                    return LocalDate.parse(date, this.formatter);
//            }
//        }
//    }
//
//    public static class OffsetDateTimeTypeAdapter extends TypeAdapter<OffsetDateTime> {
//        private DateTimeFormatter formatter;
//
//        public OffsetDateTimeTypeAdapter() {
//            this(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//        }
//
//        public OffsetDateTimeTypeAdapter(DateTimeFormatter formatter) {
//            this.formatter = formatter;
//        }
//
//        public void setFormat(DateTimeFormatter dateFormat) {
//            this.formatter = dateFormat;
//        }
//
//        public void write(JsonWriter out, OffsetDateTime date) throws IOException {
//            if (date == null) {
//                out.nullValue();
//            } else {
//                out.value(this.formatter.format(date));
//            }
//
//        }
//
//        public OffsetDateTime read(JsonReader in) throws IOException {
//            switch(in.peek()) {
//                case NULL:
//                    in.nextNull();
//                    return null;
//                default:
//                    String date = in.nextString();
//                    if (date.endsWith("+0000")) {
//                        date = date.substring(0, date.length() - 5) + "Z";
//                    }
//
//                    try {
//                        return OffsetDateTime.parse(date, this.formatter);
//                    } catch (DateTimeParseException var4) {
//                        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//                    }
//            }
//        }
//    }
//
//    public class ByteArrayAdapter extends TypeAdapter<byte[]> {
//        public ByteArrayAdapter() {
//        }
//
//        public void write(JsonWriter out, byte[] value) throws IOException {
//            boolean oldHtmlSafe = out.isHtmlSafe();
//            out.setHtmlSafe(false);
//            if (value == null) {
//                out.nullValue();
//            } else {
//                out.value(ByteString.of(value).base64());
//            }
//
//            out.setHtmlSafe(oldHtmlSafe);
//        }
//
//        public byte[] read(JsonReader in) throws IOException {
//            switch(in.peek()) {
//                case NULL:
//                    in.nextNull();
//                    return null;
//                default:
//                    String bytesAsBase64 = in.nextString();
//                    ByteString byteString = ByteString.decodeBase64(bytesAsBase64);
//                    return byteString.toByteArray();
//            }
//        }
//    }
//}