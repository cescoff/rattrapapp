package com.edraw;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ValidationError extends RuntimeException {

    private final List<ErrorMessage> errorMessages = Lists.newArrayList();

    public ValidationError(final ErrorMessage message) {
        this.errorMessages.add(message);
    }

    public ValidationError(final Iterable<ErrorMessage> messages) {
        Iterables.addAll(this.errorMessages, messages);
    }

    public ValidationError(final Iterable<ErrorMessage> messages, Throwable cause) {
        super(cause);
        Iterables.addAll(this.errorMessages, messages);
    }

    @Override
    public String getMessage() {
        return Joiner.on("', '").join(errorMessages);
    }

    public Iterable<String> getDisplayMessages() {
        return Iterables.transform(this.errorMessages, new Function<ErrorMessage, String>() {
            @Override
            public String apply(ErrorMessage errorMessage) {
                return errorMessage.getDisplayMessage();
            }
        });
    }

    public Iterable<String> getDetailedMessages() {
        return Iterables.transform(this.errorMessages, new Function<ErrorMessage, String>() {
            @Override
            public String apply(ErrorMessage errorMessage) {
                return errorMessage.getDetails();
            }
        });
    }

}
