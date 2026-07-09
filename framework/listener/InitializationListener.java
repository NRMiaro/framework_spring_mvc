package framework.listener;

import java.text.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.annotations.Controller;
import framework.exceptions.ScanException;
import framework.http.Route;
import framework.utils.MethodDescriptor;
import framework.utils.PackageScanner;
import framework.utils.ServletUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class InitializationListener implements ServletContextListener {

    private final String CONTROLLERS_PACKAGE_NAME_PARAM = "controller_package_name";
    private final List<Class<?>> controllerClasses = new ArrayList<>();
    private Map<Route, MethodDescriptor> routeMapper = new HashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("==================");
            System.out.println("Démarrage de l'application web :)");

            initialize(sce);
            ServletContext context = sce.getServletContext();
            
            // ajout des donnees necessaires au FrontControllerServlet dans le context
            context.setAttribute("controllers_list", controllerClasses);
            context.setAttribute("route_mapper", routeMapper);

            System.out.println("Framework initialisé avec succès.");
            System.out.println("==================");

        } catch (Exception e) {
            sce.getServletContext().log(
                    "Échec de l'initialisation du framework",
                    e);
            throw new IllegalStateException(
                    "Le framework n'a pas pu être initialisé.",
                    e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        System.out.println("==================");
        System.out.println("Arret de l'application web !");
        System.out.println("==================\n");

    }

    private void initialize(ServletContextEvent sce) throws Exception {
        ServletContext context = sce.getServletContext();
        // obtenir le package de controllers depuis le fichier web.xml
        String controllersPackageName = context.getInitParameter(CONTROLLERS_PACKAGE_NAME_PARAM);

        // On insere la liste des controllers dans controllerClasses
        PackageScanner.findAnnotatedClassesInPackage(
                controllersPackageName,
                Controller.class,
                controllerClasses);

        for (Class<?> clazz : controllerClasses){
            ServletUtils.scanUrlsIn(
                clazz, 
                routeMapper
            );
        }
    }

}
