package framework.servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import framework.annotations.*;
import framework.core.ModelAndView;
import framework.core.ViewNameHandler;
import framework.exceptions.RouteException;
import framework.exceptions.ScanException;
import framework.http.HttpMethod;
import framework.http.Route;
import framework.utils.*;

public class FrontControllerServlet extends HttpServlet {

    private String controllersPackage;
    // private List<String> controllerNames;
    private List<Class<?>> controllerClasses;
    // Version de urlMapper durant sprint_02
    // private Map<String, Method> urlMapper;
    private Map<Route, MethodDescriptor> routeMapper;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        /**
         * On prend les donnees obtenues lors du deployage de l'app
         * dans le servletContext
         */
        this.controllerClasses = (List<Class<?>>) getServletContext().getAttribute("controllers_list");
        this.routeMapper = (Map<Route, MethodDescriptor>) getServletContext().getAttribute("route_mapper"); 
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
        this.controllersPackage = this.getServletContext().getInitParameter(PACKAGE_NAME_PARAM);
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

    public void scanUrlsIn(Class<?> controllerClass)
            throws ScanException {

        // verifier que c'est un controller
        if (controllerClass.getAnnotation(Controller.class) == null)
            throw new ScanException("Erreur lors du scan: " + controllerClass.getName() + " n'est pas un Controller");

        for (Method method : controllerClass.getDeclaredMethods()) {
            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
            if (urlMapping != null) {
                String URL = urlMapping.url();
                HttpMethod httpMethod = urlMapping.httpMethod();
                Route newRoute = new Route(URL, httpMethod);
                if (routeMapper.containsKey(newRoute)) {
                    throw new RouteException(newRoute, method);
                }
                // } else if (!Rules.isValidUrl(URL))
                // throw new ScanException("URL invalide pour " + controllerClass.getName() +
                // "." + method.getName());

                routeMapper.put(newRoute, new MethodDescriptor(controllerClass, method));
            }
        }

    }

    // Methode generique: ecrit une page HTML complete a partir d'un fragment de
    // contenu.
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

    private void writeJsonResponse(HttpServletResponse res, String jsonContent) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = res.getWriter()) {
            out.println(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du framework.");
        }
    }

    private void displayValidUrls(HttpServletResponse res) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<h3>Routes connues: </h3><br><ul>");
        routeMapper.forEach((route, methodDescriptor) -> content.append("<li>")
                .append(route.getHttpMethod().toString() + " " + route.getUrl())
                .append(" -> ")
                .append(methodDescriptor.getOwnerClass().getName())
                .append(".")
                .append(methodDescriptor.getMethod().getName())
                .append("</li>"));
        content.append("</ul>");
        writeHtmlResponse(res, content.toString());
    }

    private void displayInvokedMethodInfos(HttpServletResponse res, MethodDescriptor methodDescriptor)
            throws IOException {
        Method method = methodDescriptor.getMethod();
        Class<?> ownerClass = methodDescriptor.getOwnerClass();

        try {
            Object instance = ownerClass.getDeclaredConstructor().newInstance();
            Object result = method.invoke(instance);

            String content = "<p>Methode appelee: "
                    + ownerClass.getName() + "." + method.getName()
                    + "</p><p>Resultat: " + result + "</p>";
            writeHtmlResponse(res, content);

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Erreur lors de l'invocation de la methode", e);
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String path = ServletUtils.getRequestPath(req);
        HttpMethod httpMethod = HttpMethod.from(req.getMethod());
        
        if (httpMethod == null) {
            throw new RuntimeException("Methode " + req.getMethod() + " non supportee");
        }

        MethodDescriptor methodDescriptor = routeMapper.get(new Route(path, httpMethod));
        Method method = null;
        if (methodDescriptor == null) {
            displayValidUrls(res);
        } else { method = methodDescriptor.getMethod(); }

        if (method.isAnnotationPresent(ApiRest.class)){
            // writeHtmlResponse(res, "Vous avez appele une REST API");
            try {
                String jsonResult = MethodExecutor.executeApiRestMethod(methodDescriptor);
                writeJsonResponse(res, jsonResult);            
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        else {
            try {
                Object returnValue = MethodExecutor.executeMethod(methodDescriptor);
                if (returnValue instanceof ModelAndView mv){
                    transferAttributesToRequest(mv, req);
                    String pageName = mv.getPageName();
                    pageName = ViewNameHandler.prefix + pageName + ViewNameHandler.suffix;
                    res.getWriter().print(pageName);
                    
                    if (pageName != null && !pageName.trim().isEmpty()){
                        RequestDispatcher disp = req.getRequestDispatcher(pageName);
                        disp.forward(req, res);
                    }

                } else {
                    throw new RuntimeException();
                }
            } catch (Exception e) {}
        }
    }

    private void transferAttributesToRequest(ModelAndView mv, HttpServletRequest request){
        if (mv == null || request == null) {
            return;
        }

        Map<String, Object> attributes = mv.getAttributes();
        if (attributes != null) {
            attributes.forEach((key, value) -> {
                if (key != null) {
                    request.setAttribute(key, value);
                }
            });
        }
    }

}