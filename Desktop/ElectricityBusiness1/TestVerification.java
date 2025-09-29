import org.mindrot.jbcrypt.BCrypt;

public class TestVerification {
    public static void main(String[] args) {
        // Générer un code de test
        String testCode = "123456";
        String codeHash = BCrypt.hashpw(testCode, BCrypt.gensalt(10));
        
        System.out.println("Code de test: " + testCode);
        System.out.println("Hash du code: " + codeHash);
        
        // Vérifier que le code fonctionne
        boolean isValid = BCrypt.checkpw(testCode, codeHash);
        System.out.println("Vérification: " + isValid);
    }
}
