package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.dto.TaskAnalyzeRequest;
import org.example.dto.TaskAnalyzeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TaskController(ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeTask(@RequestBody TaskAnalyzeRequest request) {
        // Убедимся, что юзер авторизован (проверка из нашего AuthFilter)
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Юзер {} запросил анализ текста: {}", principal, request.prompt());

        if (request.prompt() == null || request.prompt().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Промт не может быть пустым"));
        }

        String requestId = UUID.randomUUID().toString();

        // Собираем payload для Python-воркера (строго структуры из его main.py)
        Map<String, Object> kafkaPayload = new HashMap<>();
        kafkaPayload.put("request_id", requestId);
        kafkaPayload.put("prompt", request.prompt());

        try {
            String jsonStringPayload = objectMapper.writeValueAsString(kafkaPayload);

            // Создаем запись для отправки в топик ai_prompts
            ProducerRecord<String, String> record = new ProducerRecord<>("ai_prompts", requestId, jsonStringPayload);

            // Указываем, куда слать ответ (заголовок для Kafka)
            record.headers().add(KafkaHeaders.REPLY_TOPIC, "ai_responses".getBytes());

            log.info("[ID: {}] Отправляем запрос в Kafka...", requestId);

            // Отправляем и асинхронно ждем результат
            RequestReplyFuture<String, String, String> replyFuture = replyingKafkaTemplate.sendAndReceive(record);

            // Блокируем поток максимум на 60 секунд в ожидании ответа от ИИ
            var consumerRecord = replyFuture.get(60, TimeUnit.SECONDS);

            log.info("[ID: {}] Получен ответ от ИИ воркера!", requestId);

            // Парсим то, что вернул Python.
            // Python возвращает JSON: {'request_id': ..., 'status': 'success', 'response': '{"title":...}'}
            JsonNode rootNode = objectMapper.readTree(consumerRecord.value());
            String aiRawResponse = rootNode.get("response").asText();

            // Теперь парс им саму внутреннюю строку от ИИ в н аш красивый TaskAnalyzeResponse
            TaskAnalyzeResponse responseDto = objectMapper.readValue(aiRawResponse, TaskAnalyzeResponse.class);

            return ResponseEntity.ok(responseDto);

        } catch (java.util.concurrent.TimeoutException e) {
            log.error("[ID: {}] ИИ воркер не ответил за отведенное время", requestId);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(Map.of("error", "Нейросеть слишком долго думала. Попробуйте еще раз."));
        } catch (Exception e) {
            log.error("[ID: {}] Ошибка при обработке запроса ИИ", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка сервера: " + e.getMessage()));
        }
    }
}