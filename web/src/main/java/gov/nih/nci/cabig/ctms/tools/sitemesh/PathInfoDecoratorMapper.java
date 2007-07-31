package gov.nih.nci.cabig.ctms.tools.sitemesh;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Properties;
/*
 * Title:        ConfigDecoratorMapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.ConfigLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * Better implementation of ConfigDecoratorMapper. Reads decorators and
 * mappings from the <code>config</code> property (default '/WEB-INF/decorators.xml'), but
 * uses the path info to determine the matching decorator. Unfortunately, the
 * <code>configLoader</code> member variable in ConfigDecoratorMapper is private, so we have to
 * replicate code instead of just override.
 *
 * @author <a href="mailto:mmhohman@northwestern.edu">Moses Hohman</a>
 * @see com.opensymphony.module.sitemesh.mapper.ConfigDecoratorMapper
 * @see com.opensymphony.module.sitemesh.mapper.DefaultDecorator
 * @see com.opensymphony.module.sitemesh.mapper.ConfigLoader
 */
public class PathInfoDecoratorMapper extends AbstractDecoratorMapper {
    protected final Log logger = LogFactory.getLog(getClass());

    private ConfigLoader configLoader = null;

    /* Creates new ConfigLoader using '/WEB-INF/decorators.xml' file. */
    @Override
    public void init(
        Config cfg, Properties properties, DecoratorMapper parentMapper
    ) throws InstantiationException {
        super.init(cfg, properties, parentMapper);
        try {
            String fileName = properties.getProperty("config", "/WEB-INF/decorators.xml");
            configLoader = new ConfigLoader(fileName, cfg);
        } catch (Exception e) {
            throw new InstantiationException(e.toString());
        }
    }

    /* Retrieve {@link com.opensymphony.module.sitemesh.Decorator} based on 'pattern' tag. */
    @Override
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String thisPath = ((request.getServletPath() == null) ? "" : request.getServletPath()) +
            ((request.getPathInfo() == null) ? "" : request.getPathInfo());

        if (logger.isDebugEnabled()) logger.debug("Decorating path (servletPath + pathInfo): " + thisPath);

        String name = null;
        try {
            name = configLoader.getMappedName(thisPath);
        } catch (ServletException e) {
            logger.error("ServletException thrown when getting mapped name", e);
        }

        if (logger.isDebugEnabled()) logger.debug("Mapped to name: " + name);

        Decorator result = getNamedDecorator(request, name);

        if (logger.isDebugEnabled()) logger.debug("Named decorator: " + result);
        return result == null ? super.getDecorator(request, page) : result;
    }

    /* Retrieve Decorator named in 'name' attribute. Checks the role if specified. */
    @Override
    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        Decorator result = null;
        try {
            result = configLoader.getDecoratorByName(name);
        } catch (ServletException e) {
            e.printStackTrace();
        }

        if (result == null || (result.getRole() != null && !request.isUserInRole(result.getRole()))) {
            // if the result is null or the user is not in the role
            return super.getNamedDecorator(request, name);
        } else {
            return result;
        }
    }
}
