package com.totem.food.framework.adapters.in.queue.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.totem.food.application.ports.in.dtos.event.AWSMessage;
import com.totem.food.application.ports.in.event.IReceiveEventPort;
import com.totem.food.application.ports.out.dtos.EmailNotificationDto;
import com.totem.food.application.ports.out.email.ISendEmailPort;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class EmailQueueAdapter implements IReceiveEventPort<Message<AWSMessage>, Void> {

    private final ISendEmailPort<EmailNotificationDto, Boolean> iSendEmailPort;
    private final ObjectMapper objectMapper;

    @SqsListener(value = "${ms.internal.queue.email}")
    @Override
    public Void receiveMessage(Message<AWSMessage> message) {
        try {
            var emailNotificationDto = objectMapper.readValue(message.getPayload().getMessage(), EmailNotificationDto.class);
            iSendEmailPort.sendEmail(emailNotificationDto);
            Acknowledgement.acknowledge(message);
        } catch (JsonProcessingException e) {
            log.error("Error to processing Event with body {}", message);
        }
        return null;
    }
}
