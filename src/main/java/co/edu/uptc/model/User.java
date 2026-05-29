package co.edu.uptc.model;

/**
 * Representa las credenciales y el acceso de una cuenta al sistema.
 */
public class User {
    private String investorId; // Vincula este login con su billetera financiera
    private String username; // Puede ser el mismo email o un nickname único
    private String password; // En un entorno real iría encriptada (Hash)
    private String role; // Opcional: "ADMIN" (para crear activos) o "INVESTOR" (para comprar)

    public User() {
    }

   
    public User(String investorId, String username, String password, String role) {
        this.investorId = investorId;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}