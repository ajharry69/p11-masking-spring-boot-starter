package co.ke.xently.log.mask;

public enum MaskingStyle {
    /**
     * Represents the default masking style. This is the primary or fallback style used when no specific
     * masking style is provided or explicitly required.
     * The behaviour associated with the DEFAULT style depends on its usage context.
     */
    DEFAULT,
    /**
     * Represents the full masking style. This style is used to mask all characters in a string.
     */
    FULL,
    /**
     * Represents the partial masking style. This style is used to mask all characters in a string except
     * the first ({@code LogProperties.P11.Masking.partialExemption}) characters.
     */
    PARTIAL
}
