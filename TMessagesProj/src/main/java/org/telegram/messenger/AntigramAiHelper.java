/*
 * ANTIgram Gemini AI Helper
 */

package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AntigramAiHelper {

    public interface Callback {
        void onSuccess(String result);
        void onError(String error);
    }

    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    public static void generateResponse(String prompt, Callback callback) {
        if (!AntigramConfig.aiEnabled) {
            callback.onError("AI-модуль выключен в настройках ANTIgram");
            return;
        }
        if (AntigramConfig.aiApiKey == null || AntigramConfig.aiApiKey.trim().isEmpty()) {
            callback.onError("Укажите API ключ Gemini в настройках ANTIgram");
            return;
        }

        Utilities.globalQueue.postRunnable(() -> {
            HttpURLConnection connection = null;
            try {
                String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + AntigramConfig.aiApiKey.trim();
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setDoOutput(true);

                // Build request JSON
                JSONObject part = new JSONObject();
                part.put("text", prompt);

                JSONArray parts = new JSONArray();
                parts.put(part);

                JSONObject content = new JSONObject();
                content.put("parts", parts);

                JSONArray contents = new JSONArray();
                contents.put(content);

                JSONObject requestBody = new JSONObject();
                requestBody.put("contents", contents);

                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                InputStream is = (code >= 200 && code < 300) ? connection.getInputStream() : connection.getErrorStream();

                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                }

                if (code >= 200 && code < 300) {
                    JSONObject root = new JSONObject(response.toString());
                    JSONArray candidates = root.optJSONArray("candidates");
                    if (candidates != null && candidates.length() > 0) {
                        JSONObject first = candidates.getJSONObject(0);
                        JSONObject contentObj = first.getJSONObject("content");
                        JSONArray respParts = contentObj.getJSONArray("parts");
                        if (respParts.length() > 0) {
                            String resultText = respParts.getJSONObject(0).getString("text");
                            uiHandler.post(() -> callback.onSuccess(resultText.trim()));
                            return;
                        }
                    }
                    uiHandler.post(() -> callback.onError("Пустой ответ от AI модели"));
                } else {
                    uiHandler.post(() -> callback.onError("Ошибка Gemini API (" + code + "): " + response.toString()));
                }
            } catch (Exception e) {
                FileLog.e(e);
                uiHandler.post(() -> callback.onError("Ошибка сети: " + e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    public static void summarizeChat(String chatTranscript, Callback callback) {
        String prompt = "Кратко суммаризируй следующее обсуждение в чате Telegram. Выдели главное тезисно:\n\n" + chatTranscript;
        generateResponse(prompt, callback);
    }

    public static void smartReply(String contextMessage, Callback callback) {
        String prompt = "Предложи 3 коротких, емких и уместных варианта ответа на следующее сообщение в Telegram мессенджере:\n\n\"" + contextMessage + "\"";
        generateResponse(prompt, callback);
    }
}
