package com.example;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class ClickjackingProtectionFilter implements Filter {
    
    public void init(FilterConfig config) throws ServletException {
        // Inizializzazione del filtro
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Aggiungi gli header anti-clickjacking
        httpResponse.addHeader("X-Frame-Options", "DENY");
        httpResponse.addHeader("Content-Security-Policy", "frame-ancestors 'none'");

        // Prosegui con la catena di filtri
        chain.doFilter(request, response);
    }

    public void destroy() {
        // Pulizia del filtro
    }
}
