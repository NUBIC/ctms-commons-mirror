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
                    <li>${urls['caaers.url']}/pages/study/search
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="registrar">
                <li>${urls['c3pr.url']}/Register Subject
                <tags:UserTag roleName="registration_qa_manager">
                    <li>${urls['c3pr.url']}/Manage Registrations
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="ae_reporter">
                <li>Report Adverse Events - ${urls['caaers.url']}/pages/ae/captureRoutine
                <li>${urls['caaers.url']}/pages/ae/captureRoutine
                <li>${urls['caaers.url']}/pages/ae/captureRoutine
            </tags:UserTag>

            <tags:UserTag roleName="study_subject_calendar_manager">
                <tags:UserTag roleName="study_subject_calendar_manager">
                    <li>${urls['psc.url']}/study_calendar_template_builder
                </tags:UserTag>
                <li>${urls['psc.url']}/Schedule Activities
            </tags:UserTag>

            <tags:UserTag roleName="ae_expedited_report_reviewer">
                <tags:UserTag roleName="ae_study_data_reviewer">
                    <li>Review Adverse Events - ${urls['caaers.url']}/pages/ae/routingAndReview
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="supplemental_study_information_manager">
                <li>Update Study Adverse Event Requirements - ${urls['caaers.url']}/caaers/pages/study/search
            </tags:UserTag>

            <tags:UserTag roleName="user_administrator">
                <tags:UserTag roleName="person_and_organization_information_manager">
                    <li>Create Personnel - ${urls['caaers.url']}/pages/admin/createUser
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="lab_data_user">
                <li>Manage Labs - ${urls['labviewer.url']}/caaers/pages/study/search
            </tags:UserTag>

            <tags:UserTag roleName="ae_rule_and_report_manager">
                <li>Manage Safety Reporting Requirements  - ${urls['caaers.url']}/caaers/pages/rule/list
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
