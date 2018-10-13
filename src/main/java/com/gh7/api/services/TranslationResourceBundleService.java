package com.gh7.api.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class TranslationResourceBundleService {
  @Cacheable("translationResourceBundle")
  public ResourceBundle getTranslationResourceBundle(Locale locale) {
    return ResourceBundle.getBundle("translations/translations", locale);
  }
}
