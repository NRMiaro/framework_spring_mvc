package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import framework.annotations.Controller;
import framework.annotations.UrlMapping;
import framework.utils.PackageScanner;
import framework.utils.Rules;
import framework.utils.ScanException;

public class FrontControllerServlet extends HttpServlet {

    private String controllersPackage;
    // private List<String> controllerNames;
    private List<Class<?>> controllerClasses;
    private Map<String, Method> urlMapper;
    

    @Override
    public void init() throws ServletException {
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
    private void loadParams(){
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
        //     controllersPackage,
        //     Controller.class);

        // nouvell version: stocker les class
        this.controllerClasses = PackageScanner.getAnnotatedClassesInPackage(
            controllersPackage, 
            Controller.class);

        for (Class<?> controllerClass: controllerClasses){
            scanUrlsIn(controllerClass);
        }

    }

    public void scanUrlsIn(Class<?> controllerClass){

        // verifier que c'est un controller
        if (controllerClass.getAnnotation(Controller.class) == null)
            throw new ScanException("Erreur lors du scan: " + controllerClass.getName() + " n'est pas un Controller");

        for (Method method : controllerClass.getDeclaredMethods()){
            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
            if (urlMapping != null){
                String URL = urlMapping.url();
                if (urlMapper.containsKey(URL)){
                    // version simple sans check de method POST/GET/... 
                    // donc pour l'instant une meme route est attribuable a une seule fonction     
                    throw new ScanException("L'URL: " + URL + " a ete utilisee pour plusieurs methodes");
                } else if (!Rules.isValidUrl(URL))
                    throw new ScanException("URL invalide pour " + controllerClass.getName() + "." + method.getName());

                urlMapper.put(URL, method);
            }
        }

    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StringBuilder mainURL = new StringBuilder(req.getRequestURL());
        String queryParameters = req.getQueryString();
        if (queryParameters != null) {
            mainURL.append('?').append(queryParameters);
        }

        res.setContentType("text/html;charset=UTF-8");


        try (PrintWriter out = res.getWriter()) {
            out.println("<!DOCTYPE html><html><body>");
            // Controllers
            out.println("<h3>Controllers: </h3> " + controllersPackage + "<br><ul>");

            for (Class<?> controllerClass: controllerClasses) {
                out.println("<li>" + controllerClass.getName() + "</li>");
            }
            out.println("</ul>");

            // URLs
            out.println("<h3>Routes: </h3><br><ul>");
            urlMapper.forEach((url, method) -> {
                out.println("<li>" + url + " -> " + method.getDeclaringClass().getName() + "." + method.getName());
            });
            out.println("</ul>");
            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du framework.");
        }
    }
    
}