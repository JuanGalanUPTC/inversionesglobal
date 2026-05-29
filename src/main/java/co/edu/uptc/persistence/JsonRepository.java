package co.edu.uptc.persistence;

import com.google.gson.*;
import co.edu.uptc.repository.Repository;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class JsonRepository<T> implements Repository<T> {

    private final String filePath;
    private final Type type;
    private final Gson gson;

    public JsonRepository(String filePath, Type type) {
        this.filePath = filePath;
        this.type = type;
        this.gson = createGson();
        ensureFileExists(); // Asegura la salud de la persistencia desde el inicio
    }

    private Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) 
                (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) 
                (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) 
                (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) 
                (json, typeOfT, context) -> LocalTime.parse(json.getAsString()))
            .setPrettyPrinting()
            .create();
    }

    /**
     * Revisa si el archivo y sus directorios existen; si no, los crea con una estructura [] limpia.
     */
    private void ensureFileExists() {
        File file = new File(filePath);
        try {
            // Crear carpetas contenedoras si no existen (ej: persistence/)
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // Si el archivo no existe o está vacío, inicializarlo con un arreglo JSON vacío
            if (!file.exists() || file.length() == 0) {
                try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                    writer.write("[]");
                }
            }
        } catch (IOException e) {
            System.err.println("Error crítico al inicializar el archivo de datos: " + filePath);
            e.printStackTrace();
        }
    }

    @Override
    public List<T> findAll() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        // Corregido: Lectura forzada en UTF-8 para soporte de tildes y eñes
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            List<T> data = gson.fromJson(reader, type);
            return data != null ? data : new ArrayList<>();
        } catch (IOException | JsonSyntaxException e) {
            // Si el archivo está corrupto, es mejor devolver una lista vacía manejable
            return new ArrayList<>();
        }
    }

    public void saveAll(List<T> data) {
        // Corregido: Escritura forzada en UTF-8
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(T element) {
        List<T> data = findAll();
        data.add(element);
        saveAll(data);
    }

    @Override
    public void replaceAll(List<T> data) {
        saveAll(data);
    }

    @Override
    public Optional<T> findBy(Predicate<T> condition) {
        return findAll().stream().filter(condition).findFirst();
    }

    @Override
    public void deleteBy(Predicate<T> condition) {
        List<T> data = findAll();
        boolean removed = data.removeIf(condition);
        if (removed) {
            saveAll(data);
        }
    }
}