package co.ke.xently.log.mask.utils.validators;

import co.ke.xently.log.mask.LogProperties.P11.Masking.PartialMaskingExemption;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PartialMaskingExemptionValidator implements ConstraintValidator<ValidPartialMaskingExemption, PartialMaskingExemption> {

    @Override
    public boolean isValid(PartialMaskingExemption exemption, ConstraintValidatorContext context) {
        if (exemption == null) {
            return true;
        }

        return exemption.getFromStart() > 0 || exemption.getFromEnd() > 0;
    }
}