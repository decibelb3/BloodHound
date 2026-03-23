package com.txstate.bloodhound.util;

import com.txstate.bloodhound.model.HealthRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal JSON serializer/parser tailored for HealthRecord arrays.
 */
public final class JsonUtil {
    private JsonUtil() {
    }

    /**
     * Serializes records as a JSON array string.
     *
     * @param records records to serialize
     * @return JSON array text
     */
    public static String toJson(List<HealthRecord> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < records.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append("\n  ").append(recordToJson(records.get(i)));
        }
        if (!records.isEmpty()) {
            builder.append("\n");
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Parses a JSON array string into records.
     *
     * @param json JSON array text
     * @return parsed records (possibly empty)
     * @throws IllegalArgumentException when JSON structure or numeric tokens are invalid
     */
    public static List<HealthRecord> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }

        String trimmed = json.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new IllegalArgumentException("Expected a JSON array.");
        }

        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isBlank()) {
            return new ArrayList<>();
        }

        List<String> objectJsonList = splitTopLevelObjects(body);
        List<HealthRecord> records = new ArrayList<>();
        for (String objectJson : objectJsonList) {
            records.add(jsonToRecord(objectJson));
        }
        return records;
    }

    private static String recordToJson(HealthRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        appendString(builder, "sessionId", record.getSessionId());
        appendLong(builder, "timestampEpochMillis", record.getTimestampEpochMillis());
        appendInteger(builder, "systolic", record.getSystolic());
        appendInteger(builder, "diastolic", record.getDiastolic());
        appendInteger(builder, "heartRate", record.getHeartRate());
        appendInteger(builder, "totalCholesterol", record.getTotalCholesterol());
        appendInteger(builder, "ldl", record.getLdl());
        appendInteger(builder, "hdl", record.getHdl());
        appendInteger(builder, "triglycerides", record.getTriglycerides());
        appendString(builder, "timeOfDay", record.getTimeOfDay());
        appendString(builder, "medTiming", record.getMedTiming());
        appendString(builder, "activityTiming", record.getActivityTiming());
        appendString(builder, "bloodPressureCategory", record.getBloodPressureCategory());
        appendString(builder, "totalCholesterolCategory", record.getTotalCholesterolCategory());
        appendString(builder, "ldlCategory", record.getLdlCategory());
        appendString(builder, "hdlCategory", record.getHdlCategory());
        appendString(builder, "triglyceridesCategory", record.getTriglyceridesCategory());
        appendString(builder, "lipidSummary", record.getLipidSummary());

        if (builder.charAt(builder.length() - 1) == ',') {
            builder.setLength(builder.length() - 1);
        }
        builder.append("}");
        return builder.toString();
    }

    private static HealthRecord jsonToRecord(String objectJson) {
        Map<String, String> fields = parseFlatObject(objectJson);
        HealthRecord record = new HealthRecord();
        record.setSessionId(fields.get("sessionId"));
        record.setTimestampEpochMillis(parseLong(fields.get("timestampEpochMillis"), 0L));
        record.setSystolic(parseInteger(fields.get("systolic")));
        record.setDiastolic(parseInteger(fields.get("diastolic")));
        record.setHeartRate(parseInteger(fields.get("heartRate")));
        record.setTotalCholesterol(parseInteger(fields.get("totalCholesterol")));
        record.setLdl(parseInteger(fields.get("ldl")));
        record.setHdl(parseInteger(fields.get("hdl")));
        record.setTriglycerides(parseInteger(fields.get("triglycerides")));
        record.setTimeOfDay(fields.get("timeOfDay"));
        record.setMedTiming(fields.get("medTiming"));
        record.setActivityTiming(fields.get("activityTiming"));
        record.setBloodPressureCategory(fields.get("bloodPressureCategory"));
        record.setTotalCholesterolCategory(fields.get("totalCholesterolCategory"));
        record.setLdlCategory(fields.get("ldlCategory"));
        record.setHdlCategory(fields.get("hdlCategory"));
        record.setTriglyceridesCategory(fields.get("triglyceridesCategory"));
        record.setLipidSummary(fields.get("lipidSummary"));
        return record;
    }

    private static Map<String, String> parseFlatObject(String objectJson) {
        String trimmed = objectJson.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON object.");
        }
        String body = trimmed.substring(1, trimmed.length() - 1);
        Map<String, String> map = new LinkedHashMap<>();
        int index = 0;
        while (index < body.length()) {
            index = skipWhitespace(body, index);
            if (index >= body.length()) {
                break;
            }

            ParseToken keyToken = parseStringToken(body, index);
            String key = keyToken.value;
            index = skipWhitespace(body, keyToken.nextIndex);
            if (index >= body.length() || body.charAt(index) != ':') {
                throw new IllegalArgumentException("Malformed JSON key/value pair.");
            }
            index++;
            index = skipWhitespace(body, index);
            if (index >= body.length()) {
                throw new IllegalArgumentException("Missing value for key " + key + ".");
            }

            ParseToken valueToken;
            char c = body.charAt(index);
            if (c == '"') {
                valueToken = parseStringToken(body, index);
            } else {
                valueToken = parseScalarToken(body, index);
            }
            map.put(key, valueToken.value);
            index = skipWhitespace(body, valueToken.nextIndex);
            if (index < body.length()) {
                char sep = body.charAt(index);
                if (sep != ',') {
                    throw new IllegalArgumentException("Expected comma separator.");
                }
                index++;
            }
        }
        return map;
    }

    private static List<String> splitTopLevelObjects(String body) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int objectStart = -1;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }

            if (c == '"') {
                inString = true;
                continue;
            }
            if (c == '{') {
                if (depth == 0) {
                    objectStart = i;
                }
                depth++;
                continue;
            }
            if (c == '}') {
                depth--;
                if (depth < 0) {
                    throw new IllegalArgumentException("Malformed JSON object boundaries.");
                }
                if (depth == 0) {
                    if (objectStart < 0) {
                        throw new IllegalArgumentException("Malformed JSON object start.");
                    }
                    objects.add(body.substring(objectStart, i + 1));
                    objectStart = -1;
                }
            }
        }

        if (depth != 0 || inString) {
            throw new IllegalArgumentException("Malformed JSON array content.");
        }
        return objects;
    }

    private static ParseToken parseStringToken(String input, int startIndex) {
        if (input.charAt(startIndex) != '"') {
            throw new IllegalArgumentException("Expected string token.");
        }
        StringBuilder builder = new StringBuilder();
        int i = startIndex + 1;
        boolean escaped = false;

        while (i < input.length()) {
            char c = input.charAt(i);
            if (escaped) {
                switch (c) {
                    case '"':
                    case '\\':
                    case '/':
                        builder.append(c);
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    default:
                        builder.append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                return new ParseToken(builder.toString(), i + 1);
            } else {
                builder.append(c);
            }
            i++;
        }
        throw new IllegalArgumentException("Unterminated string token.");
    }

    private static ParseToken parseScalarToken(String input, int startIndex) {
        int i = startIndex;
        while (i < input.length() && input.charAt(i) != ',' && input.charAt(i) != '}') {
            i++;
        }
        String token = input.substring(startIndex, i).trim();
        if ("null".equals(token)) {
            return new ParseToken(null, i);
        }
        return new ParseToken(token, i);
    }

    private static int skipWhitespace(String input, int index) {
        int i = index;
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }
        return i;
    }

    private static void appendString(StringBuilder builder, String key, String value) {
        appendKey(builder, key);
        if (value == null) {
            builder.append("null,");
        } else {
            builder.append("\"").append(escape(value)).append("\",");
        }
    }

    private static void appendInteger(StringBuilder builder, String key, Integer value) {
        appendKey(builder, key);
        builder.append(value == null ? "null" : value).append(",");
    }

    private static void appendLong(StringBuilder builder, String key, long value) {
        appendKey(builder, key);
        builder.append(value).append(",");
    }

    private static void appendKey(StringBuilder builder, String key) {
        builder.append("\"").append(escape(key)).append("\":");
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static Integer parseInteger(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer token: " + token, e);
        }
    }

    private static long parseLong(String token, long fallback) {
        if (token == null || token.isBlank()) {
            return fallback;
        }
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid long token: " + token, e);
        }
    }

    private static class ParseToken {
        private final String value;
        private final int nextIndex;

        private ParseToken(String value, int nextIndex) {
            this.value = value;
            this.nextIndex = nextIndex;
        }
    }
}
