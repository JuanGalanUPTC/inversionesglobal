package co.edu.uptc.model;

public class User {
    private String id;
    private String email;
    private String password;
    private String ciudadNacimiento;

    public User(String id,String email, String password, String ciudadNacimiento) {
        this.email = email;
        this.password = password;
        this.ciudadNacimiento = ciudadNacimiento;
    }

    // Getters y Setters (Obligatorios para que Gson los procese)

    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCiudadNacimiento() { return ciudadNacimiento; }
    public void setCiudadNacimiento(String ciudadNacimiento) { this.ciudadNacimiento = ciudadNacimiento; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
}