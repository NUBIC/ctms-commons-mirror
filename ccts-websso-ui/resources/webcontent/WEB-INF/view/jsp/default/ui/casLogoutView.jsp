<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
    <style type="text/css" media="screen">@import 'css/common.css' /**/;</style>
    <style type="text/css" media="screen">@import 'css/styles.css' /**/;</style>
    <style type="text/css" media="screen">@import 'css/fields.css' /**/;</style>


</head>
<body>
<chrome:body title="CCTS Single Sign On">
    <chrome:box title="Logout Confirmed" autopad="true">

        <h2>
            <spring:message code="screen.logout.header"/>
        </h2>

        <p>
            <spring:message code="screen.logout.success"/>
        </p>

        <p>
            <spring:message code="screen.logout.security"/>
        </p>

        <%--
               Implementation of support for the "url" parameter to logout as recommended in CAS spec section 2.3.1.
               A service sending a user to CAS for logout can specify this parameter to suggest that we offer
               the user a particular link out from the logout UI once logout is completed.  We do that here.
              --%>
        <c:if test="${not empty param['url']}">
            <p>
                <spring:message code="screen.logout.redirect" arguments="${param['url']}"/>
            </p>
        </c:if>

    </chrome:box>
</chrome:body>


<div id="footer">
</div>

</body>
</html>

