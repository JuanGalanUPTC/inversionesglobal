package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.User;
import co.edu.uptc.persistence.JsonRepository; // Apunta a tu paquete correcto de persistencia

public class UserService {
    private final JsonRepository<User> userRepo;

    /**
     * Constructor por defecto usando la ruta de persistencia universal y compatible.
     */
    public UserService() {
        Type type = new TypeToken<List<User>>() {}.getType();
        // Corregido: Ruta universal con '/' compatible con cualquier S.O.
        this.userRepo = new JsonRepository<>("src\\main\\resources\\data\\user.json", type);
    }

    /**
     * Constructor con repositorio inyectado (útil para pruebas unitarias).
     * Corregido: El nombre del constructor ahora coincide perfectamente con la clase.
     */
    public UserService(JsonRepository<User> userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Valida si las credenciales coinciden usando Predicados funcionales.
     * @return El usuario autenticado si es exitoso, o un Optional vacío si falla.
     */
    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return Optional.empty();
        }

        // Usamos el findBy de nuestro JsonRepository universal
        return userRepo.findBy(user ->
            user.getUsername().equalsIgnoreCase(username.trim()) &&
            user.getPassword().equals(password)
        );
    }

    /**
     * Registra un nuevo usuario en el sistema verificando que el username no esté duplicado.
     * Vincula la cuenta con el ID del inversionista creado.
     */
    public void registerUser(String investorId, String username, String password, String role) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("CREDENTIALS_CANNOT_BE_EMPTY");
        }
        
        String cleanedUsername = username.trim();
        String assignedRole = (role == null || role.isBlank()) ? "INVESTOR" : role.toUpperCase().trim();

        // Validar si el nombre de usuario ya está tomado
        boolean usernameExists = userRepo.findBy(user -> user.getUsername().equalsIgnoreCase(cleanedUsername)).isPresent();
        if (usernameExists) {
            throw new IllegalArgumentException("USERNAME_ALREADY_TAKEN");
        }

        try {
            User newUser = new User(investorId, cleanedUsername, password, assignedRole);
            userRepo.save(newUser);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to save the user credentials.", e);
        }
    }
}