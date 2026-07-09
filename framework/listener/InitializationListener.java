package framework.listener;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class InitializationListener implements ServletContextListener {
    
    @Override 
    public void contextInitialized(ServletContextEvent sce){

        System.out.println("==================");
        System.out.println("Demarrage de l'application web :)");
        ServletContext context = sce.getServletContext();
        

        System.out.println("==================\n");

    }

    @Override 
    public void contextDestroyed(ServletContextEvent sce){

        System.out.println("==================");
        System.out.println("Arret de l'application web !");
        System.out.println("==================\n");

    }

}
