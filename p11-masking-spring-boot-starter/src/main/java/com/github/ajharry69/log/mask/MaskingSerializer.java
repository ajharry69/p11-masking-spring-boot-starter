package com.github.ajharry69.log.mask;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.jdk.StringSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

public class MaskingSerializer extends StdSerializer<Object> {

    private final MaskingService maskingService;
    private final P11MaskingProperties properties;

    public MaskingSerializer(MaskingService maskingService, P11MaskingProperties properties) {
        super(Object.class);
        this.maskingService = maskingService;
        this.properties = properties;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        gen.writeString(maskingService.mask(value.toString()));
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
        if (property == null) return new StringSerializer();

        if (properties.getFields() != null && properties.getFields().contains(property.getName())) {
            return this;
        }

        return new StringSerializer();
    }
}