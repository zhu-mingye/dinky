package org.dinky.data.enums;

import lombok.Getter;

@Getter
public enum OpenaiManufacturer {
    OPENAI("OpenAI");

    private final String manufacturer;

    OpenaiManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
