package co.edu.uptc.model;

import co.edu.uptc.model.enums.UserRole;

public class User {
    private String id;
    private String email;
    private String password;
    private String ciudadNacimiento;
    private UserRole userRole;
    private String profileImagePath;

    public User(String id, String email, String password, String ciudadNacimiento, UserRole userRole, String profileImagePath) {
        this.email = email;
        this.password = password;
        this.ciudadNacimiento = ciudadNacimiento;
        this.userRole = userRole;
        this.profileImagePath=profileImagePath;
    }

    // Getters y Setters (Obligatorios para que Gson los procese)

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCiudadNacimiento() {
        return ciudadNacimiento;
    }

    public void setCiudadNacimiento(String ciudadNacimiento) {
        this.ciudadNacimiento = ciudadNacimiento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    

    
}