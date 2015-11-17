package pers.ly.demos.springframework.context.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pers.ly.demos.springframework.context.service.IMessageService;

@Component
public class MessagePrinter {

    final private IMessageService service;

    @Autowired
    public MessagePrinter(IMessageService service) {
        this.service = service;
    }

    public void printMessage() {
        System.out.println(this.service.getMessage());
    }
}
