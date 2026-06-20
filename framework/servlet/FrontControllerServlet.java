package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import framework.annotations.Controller;
import framework.utils.PackageScanner;
import framework.utils.ScanException;

public class FrontControllerServlet extends HttpServlet {

    private String controllersPackage;
    // private List<String> controllerNames;
    private List<Class<?>> controllerClasses;

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
            out.println("<h3>Controllers: </h3> " + controllersPackage + "<br><ul>");

            for (Class<?> controllerClass: controllerClasses) {
                out.println("<li>" + controllerClass.getName() + "</li>");
            }
            out.println("</ul></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du framework.");
        }
    }
    
}