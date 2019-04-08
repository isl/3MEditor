/*
 * Copyright 2014-2019  Institute of Computer Science,
 * Foundation for Research and Technology - Hellas
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 *
 * Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
 * Tel:+30-2810-391632
 * Fax: +30-2810-391638
 * E-mail: isl@ics.forth.gr
 * http://www.ics.forth.gr/isl
 *
 * Authors : Georgios Samaritakis, Konstantina Konsolaki.
 *
 * This file is part of the 3MEditor webapp of Mapping Memory Manager project.
 */
package gr.forth.ics.isl.x3mlEditor.filter;

import isl.dms.DMSException;
import isl.dms.file.DMSUser;
import gr.forth.ics.isl.x3mlEditor.BasicServlet;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author samarita
 */
public class LoginFilter extends BasicServlet implements Filter {

    private FilterConfig filterConfig = null;

    /**
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     *
     */
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     *
     * @param cookies
     * @param session
     * @return
     */
    public String getCookieValue(Cookie[] cookies, HttpSession session) {
        try {
            String[] users = DMSUser.getUsers(BasicServlet.conf);
            List<String> usersL = Arrays.asList(users);
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (usersL.contains(URLDecoder.decode(cookie.getName()))) {                       
                        session.setAttribute("username", URLDecoder.decode(cookie.getName()));
                        return (cookie.getName() + "-" + cookie.getValue());
                    }
                }
            }
        } catch (DMSException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest hrequest = (HttpServletRequest) request;
            HttpSession session = hrequest.getSession(true);

            if (editorType.equals("standalone")) {
                session.setAttribute("username", "user");
                chain.doFilter(request, response);
            } else {

                HttpServletResponse hresponse = (HttpServletResponse) response;

                String res = getCookieValue(hrequest.getCookies(), session);
                if (res == null) {
                     session.invalidate();
                    hresponse.sendRedirect(systemURL);                   
                } else {
                    String cookieName = res.split("-")[0];
                    String cookieValue = res.split("-")[1];
                    Cookie cookie = new Cookie(cookieName, cookieValue);
                    String editorWebapp = editorName;
                    cookie.setPath("/" + editorWebapp);
                    cookie.setMaxAge(10800);
                    hresponse.addCookie(cookie);
                    chain.doFilter(request, response);
                }

            }
        }
    }
}
