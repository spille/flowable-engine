/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.app.servlet;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import org.activiti.app.conf.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
public class WebConfigurer implements ServletContextListener {

  private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

  public AnnotationConfigWebApplicationContext context;

  public void setContext(AnnotationConfigWebApplicationContext context) {
    this.context = context;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    log.debug("Configuring Spring root application context");

    ServletContext servletContext = sce.getServletContext();

    AnnotationConfigWebApplicationContext rootContext = null;

    if (context == null) {
      rootContext = new AnnotationConfigWebApplicationContext();
      rootContext.register(ApplicationConfiguration.class);

      if (rootContext.getServletContext() == null) {
        rootContext.setServletContext(servletContext);
      }

      rootContext.refresh();
      context = rootContext;

    } else {
      rootContext = context;
      if (rootContext.getServletContext() == null) {
        rootContext.setServletContext(servletContext);
      }
    }

    servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, rootContext);

    EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

    initSpring(servletContext, rootContext);
    initSpringSecurity(servletContext, disps);

    log.debug("Web application fully configured");
  }

  /**
   * Initializes Spring and Spring MVC.
   */
  private void initSpring(ServletContext servletContext, AnnotationConfigWebApplicationContext rootContext) {
    log.debug("Configuring Spring Web application context");
    AnnotationConfigWebApplicationContext appDispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
    appDispatcherServletConfiguration.setParent(rootContext);
    appDispatcherServletConfiguration.register(AppDispatcherServletConfiguration.class);

    log.debug("Registering Spring MVC Servlet");
    ServletRegistration.Dynamic appDispatcherServlet = servletContext.addServlet("appDispatcher", new DispatcherServlet(appDispatcherServletConfiguration));
    appDispatcherServlet.addMapping("/app/*");
    appDispatcherServlet.setLoadOnStartup(1);
    appDispatcherServlet.setAsyncSupported(true);

    initSpringProcessRest(servletContext, rootContext);
    initSpringDMNRest(servletContext, rootContext);
    initSpringFormRest(servletContext, rootContext);
  }
  
  /**
   * Initializes Process Spring and Spring MVC .
   */
  private ServletRegistration.Dynamic initSpringProcessRest(ServletContext servletContext, AnnotationConfigWebApplicationContext rootContext) {
    return initSpringRestComponent(servletContext, rootContext, "process-api", ProcessDispatcherServletConfiguration.class);
  }

  /**
   * Initializes DMN Spring and Spring MVC.
   */
  protected ServletRegistration.Dynamic initSpringDMNRest(ServletContext servletContext, AnnotationConfigWebApplicationContext rootContext) {
    return initSpringRestComponent(servletContext, rootContext, "dmn-api", DmnDispatcherServletConfiguration.class);
  }

  /**
   * Initializes Form Spring and Spring MVC.
   */
  protected ServletRegistration.Dynamic initSpringFormRest(ServletContext servletContext, AnnotationConfigWebApplicationContext rootContext) {
    return initSpringRestComponent(servletContext, rootContext, "form-api", FormDispatcherServletConfiguration.class);
  }
  
  protected ServletRegistration.Dynamic initSpringRestComponent(ServletContext servletContext, AnnotationConfigWebApplicationContext rootContext,
      String restContextRoot, Class<? extends WebMvcConfigurationSupport> webConfigClass) {
    
    log.debug("Configuring Spring Web application context - " + restContextRoot + " REST");
    AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
    dispatcherServletConfiguration.setParent(rootContext);
    dispatcherServletConfiguration.register(webConfigClass);

    log.debug("Registering Spring MVC Servlet - " + restContextRoot + " REST");
    ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet(restContextRoot + "-dispatcher", new DispatcherServlet(dispatcherServletConfiguration));
    dispatcherServlet.addMapping("/" + restContextRoot + "/*");
    dispatcherServlet.setLoadOnStartup(1);
    dispatcherServlet.setAsyncSupported(true);

    return dispatcherServlet;
  }

  /**
   * Initializes Spring Security.
   */
  private void initSpringSecurity(ServletContext servletContext, EnumSet<DispatcherType> disps) {
    log.debug("Registering Spring Security Filter");
    FilterRegistration.Dynamic springSecurityFilter = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy());

    springSecurityFilter.addMappingForUrlPatterns(disps, false, "/*");
    springSecurityFilter.setAsyncSupported(true);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    log.info("Destroying Web application");
    WebApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
    AnnotationConfigWebApplicationContext gwac = (AnnotationConfigWebApplicationContext) ac;
    gwac.close();
    log.debug("Web application destroyed");
  }
}
