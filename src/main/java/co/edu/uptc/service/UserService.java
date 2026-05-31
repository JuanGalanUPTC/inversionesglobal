package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.User;
import co.edu.uptc.persistence.JsonRepository;

public class UserService {
    private final JsonRepository<User> userRepo;

    /**
     * Constructor por defecto usando la ruta de persistencia universal.
     */
    public UserService() {
        Type type = new TypeToken<List<User>>() {
        }.getType();
        // Usamos '/' universales para evitar problemas de compatibilidad entre sistemas
        // operativos
        this.userRepo = new JsonRepository<>("src/main/resources/data/user.json", type);
    }

    /**
     * Constructor con repositorio inyectado (para pruebas).
     */
    public UserService(JsonRepository<User> userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Valida si las credenciales coinciden para iniciar sesión.
     * 
     * @return El usuario autenticado si es exitoso, o un Optional vacío si falla.
     */
    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Optional.empty();
        }

        return userRepo.findBy(user -> user.getEmail().equalsIgnoreCase(email.trim()) &&
                user.getPassword().equals(password));
    }

    /**
     * Registra un nuevo usuario en el sistema con su pregunta de seguridad.
     */
    public void registerUser(String email, String password, String respuestaSeguridad) {
        if (email == null || email.isBlank() || password == null || password.isBlank() || respuestaSeguridad == null
                || respuestaSeguridad.isBlank()) {
            throw new IllegalArgumentException("CREDENTIALS_CANNOT_BE_EMPTY");
        }

        String cleanedEmail = email.trim();
        String cleanedRespuesta = respuestaSeguridad.trim();

        // Validar si el correo ya está registrado en el JSON
        verifyEmailExists(cleanedEmail);

        try {
            // 🛠️ Instancia de User con la estructura limpia: Correo, Contraseña y
            // Respuesta
            User newUser = new User(UUID.randomUUID().toString(), cleanedEmail, password, cleanedRespuesta);
            userRepo.save(newUser);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to save the user credentials.", e);
        }
    }

    public boolean verifyEmailExists(String correo) {
    if (correo == null || correo.isBlank()) {
        return false;
    }
    try {
        String cleanedCorreo = correo.trim();
        // 🛡️ Protegido contra nulos (user.getEmail() != null) para evitar el NullPointerException
        return userRepo.findBy(user -> 
            user.getEmail() != null && user.getEmail().equalsIgnoreCase(cleanedCorreo)
        ).isPresent();
        
    } catch (Exception e) {
        // Excepción genérica real por si falla la lectura del archivo físico
        throw new RuntimeException("Error al consultar la persistencia de usuarios.", e);
    }
}
}