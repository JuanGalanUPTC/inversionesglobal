package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import co.edu.uptc.security.PasswordEncoder;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.User;
import co.edu.uptc.model.enums.UserRole;
import co.edu.uptc.persistence.JsonRepository;

public class UserService {
    private final JsonRepository<User> userRepo;

    /**
     * Constructor por defecto usando la ruta de persistencia universal.
     */
    public UserService() {
        Type type = new TypeToken<List<User>>() {
        }.getType();
        this.userRepo = new JsonRepository<>("src/main/resources/data/user.json", type);
    }

    /**
     * Constructor con repositorio inyectado (para pruebas).
     */
    public UserService(JsonRepository<User> userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Valida si las credenciales coinciden para iniciar sesión usando BCrypt.
     */
    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userRepo.findBy(user -> user.getEmail().equalsIgnoreCase(email.trim()));

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (co.edu.uptc.security.PasswordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    /**
     * Registra un nuevo usuario en el sistema recibiendo la ruta de su imagen de
     * perfil inicial.
     */
    public void registerUser(String email, String password, String respuestaSeguridad, UserRole role,
            String imagePath) {
        if (email == null || email.isBlank() || password == null || password.isBlank() || respuestaSeguridad == null
                || respuestaSeguridad.isBlank() || role == null || imagePath == null || imagePath.isBlank()) {
            throw new IllegalArgumentException("CREDENTIALS_ROLE_AND_IMAGE_CANNOT_BE_EMPTY");
        }

        String cleanedEmail = email.trim();
        String cleanedRespuesta = respuestaSeguridad.trim();
        String passwordCrypt = PasswordEncoder.encode(password);

        if (verifyEmailExists(cleanedEmail)) {
            throw new IllegalArgumentException("EMAIL_ALREADY_EXISTS");
        }

        try {
            // 🛠️ Instancia construida dinámicamente con el 'imagePath' inyectado del
            // controlador
            User newUser = new User(
                    UUID.randomUUID().toString(),
                    cleanedEmail,
                    passwordCrypt,
                    cleanedRespuesta,
                    role,
                    imagePath.trim());

            userRepo.save(newUser);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error trying to save the user credentials.", e);
        }
    }

    /**
     * Sobrecarga de registro que acepta un objeto User directo.
     */
    public boolean registerUser(User nuevoUsuario) {
        if (nuevoUsuario == null || verifyEmailExists(nuevoUsuario.getEmail())) {
            return false;
        }
        try {
            userRepo.save(nuevoUsuario);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el objeto usuario directo.", e);
        }
    }

    public boolean verifyEmailExists(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        try {
            String cleanedCorreo = correo.trim();
            return userRepo.findBy(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(cleanedCorreo))
                    .isPresent();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la persistencia de usuarios.", e);
        }
    }

    public boolean verifySecurityAnswer(User userParam, String answer) {
        if (answer == null || answer.isBlank() || userParam == null || userParam.getEmail() == null) {
            return false;
        }
        try {
            String cleanedAnswer = answer.trim();

            return userRepo
                    .findBy(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(userParam.getEmail()))
                    .map(userEncontrado -> {
                        String ciudadReg = userEncontrado.getCiudadNacimiento();
                        return ciudadReg != null && ciudadReg.trim().equalsIgnoreCase(cleanedAnswer);
                    })
                    .orElse(false);

        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la persistencia de usuarios.", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        try {
            return userRepo.findBy(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email.trim()));
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el usuario en la persistencia.", e);
        }
    }

    public List<User> obtenerTodosLosUsuarios() {
        return userRepo.findAll();
    }

    public void guardarListaEnJson(List<User> users) {
        userRepo.saveAll(users);
    }

    /**
     * Actualiza la información de un usuario existente (como su imagePath al usar
     * la paletica).
     */
    public boolean updateUserInPersistence(User usuarioActualizado) {
        try {
            List<User> usuarios = obtenerTodosLosUsuarios();

            usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(usuarioActualizado.getEmail()));
            usuarios.add(usuarioActualizado);
            guardarListaEnJson(usuarios);

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al intentar actualizar el usuario en el JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}