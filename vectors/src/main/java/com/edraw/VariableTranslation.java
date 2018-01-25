package com.edraw;

import com.google.common.base.Optional;

import java.util.Locale;

public interface VariableTranslation {

    public String getLabel(final Locale locale);

    public Optional<String> getUnit(final Locale locale);

}
