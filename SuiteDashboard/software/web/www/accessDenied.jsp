<%@include file="/WEB-INF/views/tags.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<div id="header">
    <div id="header_container">
        <img src="images/suite_logo.png" width="290" height="71" alt="caBIG Clinical Trials Suite"/>
        <div class="header-right">
            <div class="welcome_user"><a onmouseover="showUserRolesToolTip(jQuery('#USER_ROLES').html(), '')" onmouseout="tt_Hide();">${not empty user ? user.firstName : ''} ${not empty user ? user.lastName : ''}</a></div>
            <div class="button-wrap ">
                <div class="grey button"><a href="<c:url value="/j_acegi_logout"/>">Log Out</a></div>
            </div>
        </div>
    </div>
</div>

<div id="container">
    <div id="wrapper">
        <div id="content" style="color:red; font-weight:normal; font-size:11px;">
            User does not exist.
        </div>
        <div class="clear"></div>
    </div>
    <div class="footer"><div id="build-name">${buildInfo}</div></div>
</div>

</body>
</html>