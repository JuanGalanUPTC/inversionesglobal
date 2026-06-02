package co.edu.uptc.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nManager {
    private static I18nManager instance;
    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    private I18nManager() {
        // Idioma por defecto (Español)
        setLocale(new Locale("es"));
    }

    public static I18nManager getInstance() {
        if (instance == null) {
            instance = new I18nManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        // Nueva ruta centralizada para los archivos de propiedades
        this.resourceBundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", locale);
    }

    public ResourceBundle getBundle() {
        return resourceBundle;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
}