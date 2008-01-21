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

    <style type="text/css">
        .box {
            width: 30em;
            margin: 0 auto;
        }

        .submit {
            float: right;
            margin-top: 1em;
        }
    </style>
</head>
<body>
<chrome:body title="CCTS Single Sign On">

    <chrome:box title="Please log in" autopad="true">

        <form:form method="post" id="fm1" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">
            <form:errors path="*" cssClass="error" id="status" element="div"/>


            <div class="row">
                <div class="label">
                    Username
                </div>
                <div class="value">
                    <c:if test="${not empty sessionScope.openIdLocalId}">
                        <strong>${sessionScope.openIdLocalId}</strong>
                        <input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}"/>
                    </c:if>

                    <c:if test="${empty sessionScope.openIdLocalId}">
                        <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey"/>
                        <form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1"
                                    accesskey="${userNameAccessKey}" path="username" autocomplete="false"
                                    htmlEscape="true"/>
                    </c:if>
                </div>
            </div>
            <div class="row">
                <div class="label">
                    Password
                </div>
                <div class="value">
                        <%--
                        NOTE: Certain browsers will offer the option of caching passwords for a user.  There is a non-standard attribute,
                        "autocomplete" that when set to "off" will tell certain browsers not to prompt to cache credentials.  For more
                        information, see the following web page:
                        http://www.geocities.com/technofundo/tech/web/ie_autocomplete.html
                        --%>
                    <spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey"/>
                    <form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2"
                                   path="password" accesskey="${passwordAccessKey}" htmlEscape="true"/>
                </div>
            </div>

            <div class="row">
                <div class="submit">
                    <input type="hidden" name="lt" value="${flowExecutionKey}"/>
                    <input type="hidden" name="_eventId" value="submit"/>

                    <input accesskey="l" value="Log in" tabindex="4"
                           type="submit"/>
                    <input accesskey="c" value="Clear" tabindex="5"
                           type="reset"/>
                </div>
            </div>

        </form:form>

    </chrome:box>

</chrome:body>


<div id="footer">

</div>


</body>
</html>
