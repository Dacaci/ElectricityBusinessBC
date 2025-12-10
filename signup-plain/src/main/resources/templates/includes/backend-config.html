<%-- Configuration du backend depuis les variables d'environnement --%>
<%
    String backendUrl = (String) request.getAttribute("BACKEND_URL");
    if (backendUrl == null || backendUrl.isEmpty()) {
        // Fallback si le filtre n'a pas été exécuté
        backendUrl = System.getenv("BACKEND_URL");
        if (backendUrl == null || backendUrl.isEmpty()) {
            // Dernier fallback : détection automatique
            String serverName = request.getServerName();
            if (serverName == null || serverName.equals("localhost") || serverName.equals("127.0.0.1")) {
                backendUrl = "http://localhost:8080";
            } else {
                // En production, utiliser l'URL par défaut de Render
                backendUrl = "https://electricity-business-backend-jvc9.onrender.com";
            }
        }
    }
    // S'assurer que l'URL ne se termine pas par /
    if (backendUrl != null && backendUrl.endsWith("/")) {
        backendUrl = backendUrl.substring(0, backendUrl.length() - 1);
    }
    request.setAttribute("BACKEND_URL", backendUrl);
%>
<script>
    // Injecter BACKEND_URL dans le contexte JavaScript global
    window.BACKEND_URL = '<%= backendUrl != null ? backendUrl : "http://localhost:8080" %>';
    console.log('🔧 Backend URL configuré:', window.BACKEND_URL);
</script>
