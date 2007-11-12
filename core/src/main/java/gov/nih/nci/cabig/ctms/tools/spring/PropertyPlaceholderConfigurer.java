/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package gov.nih.nci.cabig.ctms.tools.spring;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.core.Constants;

//////
//  This is a straight copy of the Spring 2.0.2 version of this class, with one feature added:
//  You can configure it to use null values if a placeholder isn't found.  (The spring version
//  only supports throwing an exception or inserting the placeholder itself.)

/**
 * A property resource configurer that resolves placeholders in bean property values of
 * context definitions. It <i>pulls</i> values from a properties file into bean definitions.
 *
 * <p>The default placeholder syntax follows the Ant / Log4J / JSP EL style:
 *
 * <pre class="code">${...}</pre>
 *
 * Example XML context definition:
 *
 * <pre class="code">&lt;bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource"&gt;
 *   &lt;property name="driverClassName"&gt;&lt;value&gt;${driver}&lt;/value&gt;&lt;/property&gt;
 *   &lt;property name="url"&gt;&lt;value&gt;jdbc:${dbname}&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * Example properties file:
 *
 * <pre class="code">driver=com.mysql.jdbc.Driver
 * dbname=mysql:mydb</pre>
 *
 * PropertyPlaceholderConfigurer checks simple property values, lists, maps,
 * props, and bean names in bean references. Furthermore, placeholder values can
 * also cross-reference other placeholders, like:
 *
 * <pre class="code">rootPath=myrootdir
 * subPath=${rootPath}/subdir</pre>
 *
 * In contrast to PropertyOverrideConfigurer, this configurer allows to fill in
 * explicit placeholders in context definitions. Therefore, the original definition
 * cannot specify any default values for such bean properties, and the placeholder
 * properties file is supposed to contain an entry for each defined placeholder.
 *
 * <p>If a configurer cannot resolve a placeholder, a BeanDefinitionStoreException
 * will be thrown. If you want to check against multiple properties files, specify
 * multiple resources via the "locations" setting. You can also define multiple
 * PropertyPlaceholderConfigurers, each with its <i>own</i> placeholder syntax.
 *
 * <p>Default property values can be defined via "properties", to make overriding
 * definitions in properties files optional. A configurer will also check against
 * system properties (e.g. "user.dir") if it cannot resolve a placeholder with any
 * of the specified properties. This can be customized via "systemPropertiesMode".
 *
 * <p>Note that the context definition <i>is</i> aware of being incomplete;
 * this is immediately obvious to users when looking at the XML definition file.
 * Hence, placeholders have to be resolved; any desired defaults have to be
 * defined as placeholder values as well (for example in a default properties file).
 *
 * <p>Property values can be converted after reading them in, through overriding
 * the <code>convertPropertyValue</code> method. For example, encrypted values
 * can be detected and decrypted accordingly before processing them.
 *
 * @author Juergen Hoeller
 * @since 02.10.2003
 * @see #setLocations
 * @see #setProperties
 * @see #setPlaceholderPrefix
 * @see #setPlaceholderSuffix
 * @see #setSystemPropertiesModeName
 * @see System#getProperty(String)
 * @see #convertPropertyValue
 * @see org.springframework.beans.factory.config.PropertyOverrideConfigurer
 */
public class PropertyPlaceholderConfigurer extends PropertyResourceConfigurer
    implements BeanNameAware, BeanFactoryAware {

    public static final String UNRESOLVED_PLACEHOLDER_NULL_HOLDER = PropertyPlaceholderConfigurer.class + ".NULL_VALUE";

    /** Default placeholder prefix: "${" */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/** Default placeholder suffix: "}" */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";


	/** Never check system properties. */
	public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

	/**
	 * Check system properties if not resolvable in the specified properties.
	 * This is the default.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

	/**
	 * Check system properties first, before trying the specified properties.
	 * This allows system properties to override any other property source.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;


	private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);

	private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

	private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	private boolean searchSystemEnvironment = true;

	private boolean ignoreUnresolvablePlaceholders = false;

	private String beanName;

	private BeanFactory beanFactory;

    private boolean useNullForUnresolvablePlaceholders = false;

    /**
	 * Set the prefix that a placeholder string starts with.
	 * The default is "${".
	 * @see #DEFAULT_PLACEHOLDER_PREFIX
	 */
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * Set the suffix that a placeholder string ends with.
	 * The default is "}".
	 * @see #DEFAULT_PLACEHOLDER_SUFFIX
	 */
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Set the system property mode by the name of the corresponding constant,
	 * e.g. "SYSTEM_PROPERTIES_MODE_OVERRIDE".
	 * @param constantName name of the constant
	 * @throws java.lang.IllegalArgumentException if an invalid constant was specified
	 * @see #setSystemPropertiesMode
	 */
	public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
		this.systemPropertiesMode = constants.asNumber(constantName).intValue();
	}

	/**
	 * Set how to check system properties: as fallback, as override, or never.
	 * For example, will resolve ${user.dir} to the "user.dir" system property.
	 * <p>The default is "fallback": If not being able to resolve a placeholder
	 * with the specified properties, a system property will be tried.
	 * "override" will check for a system property first, before trying the
	 * specified properties. "never" will not check system properties at all.
	 * @see #SYSTEM_PROPERTIES_MODE_NEVER
	 * @see #SYSTEM_PROPERTIES_MODE_FALLBACK
	 * @see #SYSTEM_PROPERTIES_MODE_OVERRIDE
	 * @see #setSystemPropertiesModeName
	 */
	public void setSystemPropertiesMode(int systemPropertiesMode) {
		this.systemPropertiesMode = systemPropertiesMode;
	}

	/**
	 * Set whether to search for a matching system environment variable
	 * if no matching system property has been found. Only applied when
	 * "systemPropertyMode" is active (i.e. "fallback" or "override"), right
	 * after checking JVM system properties.
	 * <p>Default is "true". Switch this setting off to never resolve placeholders
	 * against system environment variables. Note that it is generally recommended
	 * to pass external values in as JVM system properties: This can easily be
	 * achieved in a startup script, even for existing environment variables.
	 * @see #setSystemPropertiesMode
	 * @see java.lang.System#getProperty(String)
	 * @see java.lang.System#getenv(String)
	 */
	public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
		this.searchSystemEnvironment = searchSystemEnvironment;
	}

	/**
	 * Set whether to ignore unresolvable placeholders. Default is "false":
	 * An exception will be thrown if a placeholder cannot be resolved.
	 */
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

    /**
     * If true, use null for the value when a placeholder can't be resolved.  Implies
     * <code>ignoreUnresolvablePlaceholders=true</code>.  Default is false.
     */
    public void setUseNullForUnresolvablePlaceholders(boolean useNullForUnresolvablePlaceholders) {
        this.useNullForUnresolvablePlaceholders = useNullForUnresolvablePlaceholders;
        if (useNullForUnresolvablePlaceholders) setIgnoreUnresolvablePlaceholders(true);
    }

    /**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {

		BeanDefinitionVisitor visitor = new PlaceholderResolvingBeanDefinitionVisitor(props);
		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		for (int i = 0; i < beanNames.length; i++) {
			// Check that we're not parsing our own bean definition,
			// to avoid failing on unresolvable placeholders in properties file locations.
			if (!(beanNames[i].equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanNames[i]);
				try {
					visitor.visitBeanDefinition(bd);
				}
				catch (BeanDefinitionStoreException ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanNames[i], ex.getMessage());
				}
			}
		}
	}

	/**
	 * Parse the given String value recursively, to be able to resolve
	 * nested placeholders (when resolved property values in turn contain
	 * placeholders again).
	 * @param strVal the String value to parse
	 * @param props the Properties to resolve placeholders against
	 * @param visitedPlaceholders the placeholders that have already been visited
	 * during the current resolution attempt (used to detect circular references
	 * between placeholders). Only non-null if we're parsing a nested placeholder.
	 * @throws BeanDefinitionStoreException if invalid values are encountered
	 * @see #resolvePlaceholder(String, java.util.Properties, int)
	 */
	protected String parseStringValue(String strVal, Properties props, Set visitedPlaceholders)
	    throws BeanDefinitionStoreException {

		StringBuffer buf = new StringBuffer(strVal);

		// The following code does not use JDK 1.4's StringBuffer.indexOf(String)
		// method to retain JDK 1.3 compatibility. The slight loss in performance
		// is not really relevant, as this code will typically just run on startup.

		int startIndex = strVal.indexOf(this.placeholderPrefix);
		while (startIndex != -1) {
			int endIndex = buf.toString().indexOf(
			    this.placeholderSuffix, startIndex + this.placeholderPrefix.length());
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
				if (!visitedPlaceholders.add(placeholder)) {
					throw new BeanDefinitionStoreException(
							"Circular placeholder reference '" + placeholder + "' in property definitions");
				}
				String propVal = resolvePlaceholder(placeholder, props, this.systemPropertiesMode);
				if (propVal != null) {
					// Recursive invocation, parsing placeholders contained in the
					// previously resolved placeholder value.
					propVal = parseStringValue(propVal, props, visitedPlaceholders);
					buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
					if (logger.isDebugEnabled()) {
						logger.debug("Resolved placeholder '" + placeholder + "' to value [" + propVal + "]");
					}
					startIndex = buf.toString().indexOf(this.placeholderPrefix, startIndex + propVal.length());
				}
				else if (this.ignoreUnresolvablePlaceholders) {
                    if (this.useNullForUnresolvablePlaceholders) {
                        buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), UNRESOLVED_PLACEHOLDER_NULL_HOLDER);
                    }
                    // else, proceed with unprocessed value.
					startIndex = buf.toString().indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                }
				else {
					throw new BeanDefinitionStoreException("Could not resolve placeholder '" + placeholder + "'");
				}
				visitedPlaceholders.remove(placeholder);
			}
			else {
				startIndex = -1;
			}
		}

        String finalValue = buf.toString();
        if (UNRESOLVED_PLACEHOLDER_NULL_HOLDER.equals(finalValue)) {
            // if the whole buffer was a single, unresolved placeholder, return null
            finalValue = null;
        } else {
            // if it was nested, replace the null representation with ""
            finalValue = finalValue.replace(UNRESOLVED_PLACEHOLDER_NULL_HOLDER, "");
        }
        return finalValue;
	}

	/**
	 * Resolve the given placeholder using the given properties, performing
	 * a system properties check according to the given mode.
	 * <p>Default implementation delegates to <code>resolvePlaceholder
	 * (placeholder, props)</code> before/after the system properties check.
	 * <p>Subclasses can override this for custom resolution strategies,
	 * including customized points for the system properties check.
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @param systemPropertiesMode the system properties mode,
	 * according to the constants in this class
	 * @return the resolved value, of null if none
	 * @see #setSystemPropertiesMode
	 * @see System#getProperty
	 * @see #resolvePlaceholder(String, java.util.Properties)
	 */
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		String propVal = null;
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE) {
			propVal = resolveSystemProperty(placeholder);
		}
		if (propVal == null) {
			propVal = resolvePlaceholder(placeholder, props);
		}
		if (propVal == null && systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK) {
			propVal = resolveSystemProperty(placeholder);
		}
		return propVal;
	}

	/**
	 * Resolve the given placeholder using the given properties.
	 * Default implementation simply checks for a corresponding property key.
	 * <p>Subclasses can override this for customized placeholder-to-key mappings
	 * or custom resolution strategies, possibly just using the given properties
	 * as fallback.
	 * <p>Note that system properties will still be checked before respectively
	 * after this method is invoked, according to the system properties mode.
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @return the resolved value, of <code>null</code> if none
	 * @see #setSystemPropertiesMode
	 */
	protected String resolvePlaceholder(String placeholder, Properties props) {
		return props.getProperty(placeholder);
	}

	/**
	 * Resolve the given key as JVM system property, and optionally also as
	 * system environment variable if no matching system property has been found.
	 * @param key the placeholder to resolve as system property key
	 * @return the system property value, or <code>null</code> if not found
	 * @see #setSearchSystemEnvironment
	 * @see java.lang.System#getProperty(String)
	 * @see java.lang.System#getenv(String)
	 */
	protected String resolveSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if (value == null && this.searchSystemEnvironment) {
				value = System.getenv(key);
			}
			return value;
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not access system property '" + key + "': " + ex);
			}
			return null;
		}
	}


	/**
	 * BeanDefinitionVisitor that resolves placeholders in String values,
	 * delegating to the <code>parseStringValue</code> method of the
	 * containing class.
	 */
	private class PlaceholderResolvingBeanDefinitionVisitor extends BeanDefinitionVisitor {

		private final Properties props;

		public PlaceholderResolvingBeanDefinitionVisitor(Properties props) {
			this.props = props;
		}

		protected String resolveStringValue(String strVal) throws BeansException {
			return parseStringValue(strVal, this.props, new HashSet());
		}
	}

}
