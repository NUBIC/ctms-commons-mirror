package gov.nih.nci.cabig.ctms.web.tabs;

import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;
import gov.nih.nci.cabig.ctms.domain.MutableDomainObject;
import org.springframework.validation.Errors;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * A base class for flows which have both edit and create modes.  In these situations, much of the
 * behavior will be shared in a common base class, which may descend from this class for
 * convenience.
 * <p>
 * This class exposes to the view a flag (<code>willSave</code>) indicating whether the
 * primary domain object will be automatically saved when the page is submitted.  This flag
 * may be used to parameterize the view such that controls are properly labeled or
 * excluded/included depending on the mode.
 * <p>
 * Note that
 * <p>
 * By default, this flag is calculated by examining the object returned by
 * {@link #getPrimaryDomainObject}.  If it has already been saved (i.e., it's ID is not null),
 * the flow is in edit mode.  Otherwise, it is in create mode.
 *
 * @author Rhett Sutphin
 * @see #shouldSave
 * @see #save
 */
public abstract class AutomaticSaveFlowFormController<C, D extends MutableDomainObject, A extends MutableDomainObjectDao<D>> 
    extends AbstractTabbedFlowFormController<C>
{
    /**
     * Return the domain object which should be used for create/edit determinations and
     * for automatic saves.
     */
    protected abstract D getPrimaryDomainObject(C command);

    protected abstract A getDao();

    /**
     * The logic behind automatic save determinations.  Subclasses may override it to provide
     * alternate/more conditions.
     * <p>
     * The default behavior is that there should be an automatic save if the value returned by
     * {@link #getPrimaryDomainObject} has already been saved.
     */
    protected boolean shouldSave(HttpServletRequest request, C command, Tab<C> tab) {
        return getPrimaryDomainObject(command).getId() != null;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected Map referenceData(
        HttpServletRequest request, Object oCommand, Errors errors, int page
    ) throws Exception {
        Map<String, Object> refdata = super.referenceData(request, oCommand, errors, page);
        C command = (C) oCommand;
        refdata.put("willSave", shouldSave(request, command, getTab(command, page)));
        return refdata;
    }

    /**
     * Perform any saves associated with the given command.  Default implementation uses
     * the configured dao to save the primary domain object.
     * <p>
     * This method is used to implement page-to-page automatic saves.  It is not called
     * from {@link #processFinish} by default, but subclasses may find it appropriate
     * to do so.
     * <p>
     * If the save results in a changed command (e.g., if there's a <code>merge</code> and
     * the command is a domain object) this method should return it.  The session
     * command will be replaced with the returned value, so long as it is not null.
     *
     * @param command
     * @param errors An errors instance which may be used to communicate user-recoverable
     *  error messages
     */
    protected C save(C command, Errors errors) {
        getDao().save(getPrimaryDomainObject(command));
        return command;
    }

    /**
     * Overridden to implement replacing the provided obj with the one returned by the last call to
     * {@link #save}.  Subclasses need to call this method (via <code>super</code>) if they
     * override it.
     */
    @Override
    protected Object currentFormObject(HttpServletRequest request, Object oCommand) throws Exception {
        super.currentFormObject(request, oCommand); // for side-effects
        Object replacedCommand
            = request.getSession().getAttribute(getReplacedCommandSessionAttributeName(request));
        if (replacedCommand != null) {
            request.getSession().removeAttribute(getReplacedCommandSessionAttributeName(request));
            return replacedCommand;
        } else {
            return oCommand;
        }
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected void postProcessPage(
        HttpServletRequest request, Object oCommand, Errors errors, int page
    ) throws Exception {
        C command = (C) oCommand;
        super.postProcessPage(request, oCommand, errors, page);
        if (!errors.hasErrors() && shouldSave(request, command, getTab(command, page))) {
            C newCommand = save(command, errors);
            if (newCommand != null) {
                request.getSession().setAttribute(getReplacedCommandSessionAttributeName(request), newCommand);
            }
        }
    }

    protected String getReplacedCommandSessionAttributeName(HttpServletRequest request) {
        return getFormSessionAttributeName(request) + ".to-replace";
    }
}
