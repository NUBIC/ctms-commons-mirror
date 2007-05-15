package gov.nih.nci.cabig.ctms.web.tabs;

import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;
import gov.nih.nci.cabig.ctms.domain.MutableDomainObject;
import org.springframework.validation.Errors;

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
        refdata.put("willSave", shouldSave(request, (C) oCommand, getFlow().getTab(page)));
        return refdata;
    }

    /**
     * Perform any saves associated with the given command.  Default implementation uses
     * the configured dao to save the primary domain object.
     * <p>
     * This method is used to implement page-to-page automatic saves.  It is not called
     * from {@link #processFinish} by default, but subclasses may find it appropriate
     * to do so.
     *
     * @param command
     * @param errors An errors instance which may be used to communicate user-recoverable
     *         errors to the view for rendering
     */
    protected void save(C command, Errors errors) {
        getDao().save(getPrimaryDomainObject(command));
    }

    @Override
    protected void postProcessPage(
        HttpServletRequest request, Object oCommand, Errors errors, int page
    ) throws Exception {
        C command = (C) oCommand;
        super.postProcessPage(request, oCommand, errors, page);
        if (!errors.hasErrors() && shouldSave(request, command, getFlow().getTab(page))) {
            save(command, errors);
        }
    }
}
