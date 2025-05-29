package ru.quick.sparrow.model;

import java.time.LocalDateTime;

public record Message(LocalDateTime dateTime, String message, String sender, String addressee) {
}
