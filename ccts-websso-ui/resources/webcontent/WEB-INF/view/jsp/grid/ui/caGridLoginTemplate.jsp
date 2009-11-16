<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="includes/top.jsp"></jsp:include>
<tr>
	<td height="100%"><!-- target of anchor to skip menus -->
	<a name="content" />

	<table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
		<tr>
			<td width="65%"><!-- welcome begins -->
			<table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
				<tr>
					<td class="welcomeTitle" height="20"><spring:message code="screen.ccts.welcome.heading" /></td>
				</tr>
				<tr>
					<td class="welcomeContent" valign="top">
					<div style="float: left; padding-right: 20px;" width="30%"><img
							src="images/ccts_logo.gif" alt="Application Logo"
							hspace="10" border="0"></div>
					<div><font size="2"><spring:message code="screen.ccts.welcome.message" />
					</font></div></td>
				</tr>
			</table>
			<!-- welcome ends --></td>
			<td valign="top" width="35%"><!-- sidebar begins -->
			<table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">

				<!-- login begins -->
				<jsp:include page="casLoginView.jsp" />
				<!-- login ends -->

			</table>
			<!-- sidebar ends --></td>
		</tr>
	  </table>
	</td>
</tr>
</table>
</td>
</tr>
<!--_____ main content ends _____-->
<jsp:include page="includes/bottom.jsp"></jsp:include>