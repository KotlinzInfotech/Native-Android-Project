package com.cybertechinfosoft.photoslideshowwithmusic.util;

public class Language {
    private String languageCode;
    private String languageName;
    private String languageNameInDefaultLocale;

    public Language(String str, String str2, String str3) {
        setLanguageCode(str);
        setLanguageName(str2);
        setLanguageNameInDefaultLocale(str3);
    }

    public String getLanguageName() {
        return this.languageName;
    }

    public void setLanguageName(String str) {
        this.languageName = str;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(String str) {
        this.languageCode = str;
    }

    public String getLanguageNameInDefaultLocale() {
        return this.languageNameInDefaultLocale;
    }

    public void setLanguageNameInDefaultLocale(String str) {
        this.languageNameInDefaultLocale = str;
    }
}
