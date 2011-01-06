<%@include file="tags.jsp"%>
<%@taglib prefix="tags" uri="gov.nih.nci.cabig.ccts.tags.CheckRoleTag"%>

<script src="<c:url value="/js/wz_tooltip.js" />"></script>
<c:if test="${exists}">
<div id="content">
    <ul>
        <li class="top_left"><a href="${urls['caaers.url']}" target="_blank"><img src="images/caaers.png"/></a></li>
        <li class="top_right"><a href="${urls['c3pr.url']}" target="_blank"><img src="images/c3pr.png"/></a></li>
        <li class="bottom_left"><a href="${urls['psc.url']}" target="_blank"><img src="images/psc.png"/></a></li>
        <li class="bottom_right"><a href="${urls['labviewer.url']}" target="_blank"><img src="images/labviewer.png"/></a></li>
    </ul>

    <ul>
            <tags:UserTag roleName="study_creator">
                <li>${urls['c3pr.url']}/Create Study
                <tags:UserTag roleName="study_qa_manager">
                    <li>${urls['c3pr.url']}/Manage Study
                    ${urls['caaers.url']}/Manage-Study
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="registrar">
                <li>${urls['c3pr.url']}/Register Subject
                <tags:UserTag roleName="registration_qa_manager">
                    <li>${urls['c3pr.url']}/Manage Registrations
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="ae_reporter">
                <li>${urls['caaers.url']}/Create-Expedited-Report
                <li>${urls['caaers.url']}/Enter Adverse Event Information
                <li>${urls['caaers.url']}/Enter Adverse Event
            </tags:UserTag>
    </ul>

    <div class="clear"></div>
</div>
</c:if>
<c:if test="${not exists}">
    <div id="content" style="color:red; font-weight:normal; font-size:11px;">
        User does not exist.
    </div>
</c:if>
