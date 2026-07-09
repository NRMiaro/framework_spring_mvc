package framework.listener;

import java.text.Annotation;
import java.util.ArrayList;
import java.util.List;

import framework.annotations.Controller;
import framework.exceptions.ScanException;
import framework.utils.PackageScanner;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class InitializationListener implements ServletContextListener {

    private final String CONTROLLERS_PACKAGE_NAME_PARAM = "controller_package_name";
    private final List<Class<?>> controllerClasses = new ArrayList<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("==================");
            System.out.println("Démarrage de l'application web :)");

            initialize(sce);

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
        System.out.println("Package des controllers initie dans web.xml: " + controllersPackageName);

        // On insere la liste des controllers dans controllerClasses
        PackageScanner.findAnnotatedClassesInPackage(
                controllersPackageName,
                Controller.class,
                controllerClasses);
        // for (Class<?> clazz : controllerClasses)
        //     System.out.println(clazz.getSimpleName());
    }

}
