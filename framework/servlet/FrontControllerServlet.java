package framework.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

import framework.annotations.ApiRest;
import framework.core.ModelAndView;
import framework.http.HttpMethod;
import framework.http.Route;
import framework.utils.*;

@MultipartConfig 
public class FrontControllerServlet extends HttpServlet {

    private Map<Route, MethodDescriptor> routeMapper;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        // Rempli par InitializationListener au déploiement
        this.routeMapper = (Map<Route, MethodDescriptor>) getServletContext().getAttribute("route_mapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        processRequest(req, res);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpMethod httpMethod = HttpMethod.from(req.getMethod());
        if (httpMethod == null) {
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non supportée: " + req.getMethod());
            return;
        }

        MethodDescriptor md = routeMapper.get(new Route(ServletUtils.getRequestPath(req), httpMethod));
        if (md == null) {
            ServletUtils.writeHtml(res, ServletUtils.routesToHtml(routeMapper));
            return;
        }

        // try {
        //     Method method = md.getMethod();
        //     if (method.isAnnotationPresent(ApiRest.class)) {
        //         ServletUtils.writeJson(res, MethodExecutor.executeApiRestMethod(md));
        //     } else if (method.getParameterCount() == 0) {   
        //         Object result = MethodExecutor.executeMethod(md);
        //         if (!(result instanceof ModelAndView mv)) {
        //             throw new IllegalStateException(
        //                 method.getName() + " doit retourner un ModelAndView");
        //         }
        //         ServletUtils.render(mv, req, res);
        //     } else {
        //         Object[] args = ServletUtils.getParamValues(method, req);
        //         Object result = MethodExecutor.executeMethod(md, args);
        //         if (!(result instanceof ModelAndView mv)) {
        //             throw new IllegalStateException(
        //                 method.getName() + " doit retourner un ModelAndView");
        //         }
        //         ServletUtils.render(mv, req, res);
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     ServletUtils.writeHtml(res, String.valueOf(e.getMessage()));
        // }

        try {
            Method method = md.getMethod();

            // obtention de tous les arguments necessaires a la methode a invoquer
            Object[] args = ServletUtils.getParamValues(method, req);

            Object result = MethodExecutor.executeMethod(md, args);
            if (method.isAnnotationPresent(ApiRest.class)) {
                String json = (result instanceof String r) ? r : new JsonConverter().toJson(result);
                ServletUtils.writeJson(res, json);
            } else if (result instanceof ModelAndView mv) {
                ServletUtils.render(mv, req, res);
            } else {
                throw new IllegalStateException(
                    method.getName() + " doit retourner un ModelAndView"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (!res.isCommitted()) {
                ServletUtils.writeHtml(res, String.valueOf(e.getMessage()));
            }
        }
    }
}