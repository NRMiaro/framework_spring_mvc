package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import framework.annotations.Controller;
import framework.annotations.UrlMapping;
import framework.utils.PackageScanner;
import framework.utils.Rules;
import framework.utils.ScanException;
import framework.utils.ServletUtils;

public class FrontControllerServlet extends HttpServlet {

    private String controllersPackage;
    // private List<String> controllerNames;
    private List<Class<?>> controllerClasses;
    private Map<String, Method> urlMapper;

    @Override
    public void init() throws ServletException {
        this.urlMapper = new HashMap<>();

        try {
            loadParams();
            scanControllers();
        } catch (Exception e) {
            throw new ServletException("Échec: ", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    // Pour charger tous les parametres de la FrontControllerServlet
    private void loadParams() {
        // package des @Controller
        final String PACKAGE_NAME_PARAM = "controller_package_name";
        this.controllersPackage = this.getInitParameter(PACKAGE_NAME_PARAM);
    }

    private void scanControllers()
            throws ScanException, IOException, ClassNotFoundException {
        if (this.controllersPackage == null || this.controllersPackage.trim().isEmpty()) {
            throw new ScanException("Le paramètre d'initialisation 'controller_package_name' est manquant.");
        }

        // ancienne version (sprint 01): on stocke les noms des controllers
        // this.controllerNames = PackageScanner.getAnnotatedClassesNamesInPackage(
        // controllersPackage,
        // Controller.class);

        // nouvell version: stocker les class
        this.controllerClasses = PackageScanner.getAnnotatedClassesInPackage(
                controllersPackage,
                Controller.class);

        for (Class<?> controllerClass : controllerClasses) {
            scanUrlsIn(controllerClass);
        }

    }

    public void scanUrlsIn(Class<?> controllerClass) {

        // verifier que c'est un controller
        if (controllerClass.getAnnotation(Controller.class) == null)
            throw new ScanException("Erreur lors du scan: " + controllerClass.getName() + " n'est pas un Controller");

        for (Method method : controllerClass.getDeclaredMethods()) {
            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
            if (urlMapping != null) {
                String URL = urlMapping.url();
                if (urlMapper.containsKey(URL)) {
                    // version simple sans check de method POST/GET/...
                    // donc pour l'instant une meme route est attribuable a une seule fonction
                    throw new ScanException("L'URL: " + URL + " a ete utilisee pour plusieurs methodes");
                } else if (!Rules.isValidUrl(URL))
                    throw new ScanException("URL invalide pour " + controllerClass.getName() + "." + method.getName());

                urlMapper.put(URL, method);
            }
        }

    }

    // Methode generique: ecrit une page HTML complete a partir d'un fragment de contenu.
    // Autonome (aucune dependance a un fichier externe type JSP), donc utilisable
    // telle quelle une fois le framework empaquete en .jar.
    private void writeHtmlResponse(HttpServletResponse res, String bodyContent) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = res.getWriter()) {
            out.println("<!DOCTYPE html><html><body>");
            out.println(bodyContent);
            out.println("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du framework.");
        }
    }

    private void displayValidUrls(HttpServletResponse res) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<h3>Routes valides: </h3><br><ul>");
        urlMapper.forEach((url, method) ->
                content.append("<li>")
                        .append(url)
                        .append(" -> ")
                        .append(method.getDeclaringClass().getName())
                        .append(".")
                        .append(method.getName())
                        .append("</li>")
        );
        content.append("</ul>");

        writeHtmlResponse(res, content.toString());
    }

    private void displayInvokedMethodInfos(HttpServletResponse res, Method method) throws IOException {
        String content = "<p>Methode appelee: "
                + method.getDeclaringClass().getName() + "." + method.getName()
                + "</p>";

        writeHtmlResponse(res, content);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = ServletUtils.getRequestPath(req);
        Method method = urlMapper.get(path);
        if (method == null) {
            displayValidUrls(res);
        } else {
            displayInvokedMethodInfos(res, method);
        }
    }

}