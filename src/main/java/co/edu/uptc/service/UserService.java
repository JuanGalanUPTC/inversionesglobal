package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import co.edu.uptc.security.PasswordEncoder;

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
     * Valida si las credenciales coinciden para iniciar sesión usando BCrypt.
     * * @return El usuario autenticado si es exitoso, o un Optional vacío si falla.
     */
    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Optional.empty();
        }

        // 1. Buscamos primero al usuario únicamente por su correo electrónico
        Optional<User> userOpt = userRepo.findBy(user -> user.getEmail().equalsIgnoreCase(email.trim()));

        // 2. Si el usuario existe, validamos su contraseña con el PasswordEncoder
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 🔐 Comparamos de forma segura el texto plano contra el hash del JSON
            if (co.edu.uptc.security.PasswordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user); // ¡Éxito! Las contraseñas coinciden
            }
        }

        // Si no se encuentra el correo o la contraseña no coincide, retornamos vacío
        return Optional.empty();
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
        String passwordCrypt = PasswordEncoder.encode(password);
        // Validar si el correo ya está registrado en el JSON
        verifyEmailExists(cleanedEmail);

        try {
            // 🛠️ Instancia de User con la estructura limpia: Correo, Contraseña y
            // Respuesta
            User newUser = new User(UUID.randomUUID().toString(), cleanedEmail, passwordCrypt, cleanedRespuesta);
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
            // 🛡️ Protegido contra nulos (user.getEmail() != null) para evitar el
            // NullPointerException
            return userRepo.findBy(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(cleanedCorreo))
                    .isPresent();

        } catch (Exception e) {
            // Excepción genérica real por si falla la lectura del archivo físico
            throw new RuntimeException("Error al consultar la persistencia de usuarios.", e);
        }
    }

    public boolean verifySecurityAnswer(User userParam, String answer) {
        if (answer == null || answer.isBlank() || userParam == null || userParam.getEmail() == null) {
            return false;
        }
        try {
            String cleanedAnswer = answer.trim();

            // Buscamos en el repositorio al usuario que tenga el MISMO email que userParam
            return userRepo
                    .findBy(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(userParam.getEmail()))
                    .map(userEncontrado -> {
                        // Si encontramos al usuario, verificamos si su ciudad de nacimiento coincide
                        // (ignorando mayúsculas/minúsculas)
                        String ciudadReg = userEncontrado.getCiudadNacimiento();
                        return ciudadReg != null && ciudadReg.trim().equalsIgnoreCase(cleanedAnswer);
                    })
                    .orElse(false); // Si no se encuentra el usuario, retorna false

        } catch (Exception e) {
            // Excepción genérica real por si falla la lectura del archivo físico
            throw new RuntimeException("Error al consultar la persistencia de usuarios.", e);
        }
    }

    /**
     * Busca un usuario en la persistencia utilizando su correo electrónico
     * 
     * @param email Correo electrónico a buscar
     * @return Un Optional que contiene al Usuario si existe, o vacío si no
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        try {
            // Buscamos en el repositorio al usuario cuyo email coincida (ignorando
            // mayúsculas/minúsculas)
            return userRepo.findBy(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email.trim()));

        } catch (Exception e) {
            // Control de excepción por si falla la lectura física del JSON
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
     * Actualiza la información de un usuario existente en la persistencia (archivo
     * JSON)
     * * @param usuarioActualizado Objeto usuario con los datos modificados
     * 
     * @return true si el proceso de guardado fue exitoso, false de lo contrario
     */
    public boolean updateUserInPersistence(User usuarioActualizado) {
        try {
            List<User> usuarios = obtenerTodosLosUsuarios();

            // 🔄 Quita el registro viejo que tenga el mismo email de golpe
            usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(usuarioActualizado.getEmail()));

            // ➕ Agrega el nuevo objeto con la contraseña cambiada
            usuarios.add(usuarioActualizado);

            // 💾 Reescribe el archivo json completo
            guardarListaEnJson(usuarios);

            // 🚀 Si llegó hasta aquí sin caer en el catch, todo fue un éxito
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al intentar actualizar el usuario en el JSON: " + e.getMessage());
            e.printStackTrace();
            return false; // Retorna false para que el controlador muestre la alerta de error
        }
    }
}