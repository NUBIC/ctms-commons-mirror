<%@include file="tags.jsp"%>
<%@taglib prefix="tags" uri="gov.nih.nci.cabig.ccts.tags.CheckRoleTag"%>

<script type="text/javascript">
$(document).ready(function(){
	$(".fade").fadeTo("300", 0.7); // This sets the opacity of the thumbs to fade down to 60% when the page loads

	$(".fade").hover(function(){
		$(this).fadeTo("300", 1.0); // This should set the opacity to 100% on hover
	},function(){
   		$(this).fadeTo("300", 0.7); // This should set the opacity back to 60% on mouseout
	});
});
</script>

<script src="<c:url value="/js/wz_tooltip.js" />"></script>
<c:if test="${true || exists}">

    <div id="main-nav_wrapper">
        <div id="main-nav_top"></div>
        <div id="main-nav_stretch">
            <ul class="content">
                <li class="fade" style="opacity: 0.7;"><a href="${urls['caaers.url']}" target="_blank"><img src="images/caaers.png"></a></li>
                <li class="fade" style="opacity: 0.7;"><a href="${urls['c3pr.url']}" target="_blank"><img src="images/c3pr.png"></a></li>
                <li class="fade" style="opacity: 0.7;"><a href="${urls['psc.url']}" target="_blank"><img src="images/psc.png"></a></li>
                <li class="fade" style="opacity: 0.7;"><a href="${urls['labviewer.url']}" target="_blank"><img src="images/labviewer.png"></a></li>
                <div class="clear"></div>
            </ul>
        </div>
        <div id="main-nav_footer"></div>
    </div>

    <div id="quicklinks_wrapper">
        <div id="quicklinks_top">Quick Links</div>
        <div id="quicklinks">
            <tags:UserTag roleName="study_creator">
                <li class="fade" style="opacity: 0.7;"><a href="${urls['c3pr.url']}/pages/study/createStudy"><div class="img"><img src="images/create_subject.png"></div>Create Study</a></li>
                <tags:UserTag roleName="study_qa_manager">
                    <li class="fade" style="opacity: 0.7;"><a href="${urls['c3pr.url']}/pages/study/searchStudy"><div class="img"><img src="images/create_subject.png"></div>Manage Study</a></li>
                    <li class="fade" style="opacity: 0.7;"><a href="${urls['caaers.url']}/pages/study/search"><div class="img"><img src="images/create_subject.png"></div>Manage Study</a></li>
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="registrar">
                <li class="fade" style="opacity: 0.7;"><a href="${urls['c3pr.url']}/pages/personAndOrganization/participant/createParticipant"><div class="img"><img src="images/create_subject.png"></div>Register Subject</a></li>
                <tags:UserTag roleName="registration_qa_manager">
                    <li class="fade" style="opacity: 0.7;"><a href="${urls['c3pr.url']}/pages/registration/searchRegistration"><div class="img"><img src="images/create_subject.png"></div>Manage Registrations</a></li>
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="ae_reporter">
                <li class="fade" style="opacity: 0.7;"><a href="${urls['caaers.url']}/pages/ae/captureRoutine"><div class="img"><img src="images/create_subject.png"></div>Report Adverse Events</a></li>
            </tags:UserTag>

            <tags:UserTag roleName="study_subject_calendar_manager">
                <tags:UserTag roleName="study_subject_calendar_manager">
                    <li class="fade" style="opacity: 0.7;"><a href="${urls['psc.url']}/pages/cal/studyList"><div class="img"><img src="images/create_subject.png"></div>Manage Calendars</a></li>
                </tags:UserTag>
                <li class="fade" style="opacity: 0.7;"><a href="${urls['psc.url']}/pages/report/scheduledActivitiesReport"><div class="img"><img src="images/create_subject.png"></div>Schedule Activities</a></li>
            </tags:UserTag>

            <tags:UserTag roleName="ae_expedited_report_reviewer">
                <tags:UserTag roleName="ae_study_data_reviewer">
                    <li class="fade" style="opacity: 0.7;"><a href="${urls['caaers.url']}/pages/ae/routingAndReview"><div class="img"><img src="images/create_subject.png"></div>Review Adverse Events</a></li>
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="supplemental_study_information_manager">
                <li class="fade" style="opacity: 0.7;"><a href="${urls['caaers.url']}/pages/study/search"><div class="img"><img src="images/create_subject.png"></div>Update Study Adverse Event Requirements</a></li>
            </tags:UserTag>

            <tags:UserTag roleName="user_administrator">
                <tags:UserTag roleName="person_and_organization_information_manager">
                    <li class="fade" style="opacity: 0.7;"><a href="${urls['caaers.url']}/pages/admin/createUser"><div class="img"><img src="images/create_subject.png"></div>Create Personnel</a></li>
                </tags:UserTag>
            </tags:UserTag>

            <tags:UserTag roleName="lab_data_user">
                <li class="fade" style="opacity: 0.7;"><a href="${urls['labviewer.url']}/"><div class="img"><img src="images/create_subject.png"></div>Manage Labs</a></li>
            </tags:UserTag>

            <tags:UserTag roleName="ae_rule_and_report_manager">
                <li class="fade" style="opacity: 0.7;"><a href="${urls['caaers.url']}/pages/rule/list"><div class="img"><img src="images/create_subject.png"></div>Manage Safety Reporting Requirements</a></li>
            </tags:UserTag>
        </div>
        <div id="quicklinks_footer"></div>
    </div>

        <ul>
<%--
--%>
    </ul>

    <div class="clear"></div>
</div>
</c:if>
