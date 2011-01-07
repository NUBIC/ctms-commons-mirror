<%@include file="/WEB-INF/views/tags.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>Suite Dashoard || <decorator:title/></title>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <!--[if IE]>
    <style type="text/css" media="screen">
        div,
        li {
            zoom: 1;
        }
    </style>
    <![endif]-->

    <link rel="stylesheet" href='<c:url value="/css/index.css" />' type="text/css">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.1/jquery.min.js" type="text/javascript"></script>
    <script src='<c:url value="/js/index.js" />'></script>

    <decorator:head/>

</head>
<body>
<script src='<c:url value="/js/wz_tooltip.js" />'></script>

<script>
function showUserRolesToolTip(text, title) {
    Tip(text,
            WIDTH, 300,
            TITLE, title,
            SHADOW, false,
            FADEIN, 300,
            FADEOUT, 300,
            STICKY, 1,
            CLOSEBTN, false,
            CLICKCLOSE, false,
            OPACITY, 95,
            FONTCOLOR, "#fff",
            BORDERCOLOR, "#fff",
            BGCOLOR, "#444",
            PADDING, 15,
            FONTSIZE, "12px"
    );
}
</script>

<div id="USER_ROLES" style="display:none;">
    <ol>
        <c:if test="${fn:length(roles) > 0}">
            <b style="font-size:14px">Roles:</b><br><br>
            <div style='color:yellow;'>
                <c:forEach items="${roles}" var="a">
                    <li>${a}</li>
                </c:forEach>
            </div>
        </c:if>
    </ol>
</div>

<div id="header">
    <div id="header_container">
        <img src="images/suite_logo.png" width="290" height="71" alt="caBIG Clinical Trials Suite"/>
        <div class="header-right">
            <div class="welcome_user"><a onmouseover="showUserRolesToolTip(jQuery('#USER_ROLES').html(), '')" onmouseout="tt_Hide();">${not empty user ? user.firstName : ''} ${not empty user ? user.lastName : ''}</a></div>
            <div class="button-wrap ">
                <div class="grey button"><a href='#' onclick="logout();">Log Out</a></div>
            </div>
        </div>
    </div>
</div>

<div id="container">
    <decorator:body/>
    <div class="clear"></div>
    <div class="footer"><div id="build-name">${buildInfo}</div></div>
</div>

<script>
    var logoutLinks = [
    '${urls['caaers.url']}/j_acegi_logout',
    '${urls['c3pr.url']}/j_acegi_logout',
    '${urls['psc.url']}/j_acegi_logout',
    '${urls['labviewer.url']}/logout'];

    function logout() {
        for (i = 0; i < logoutLinks.length; i++) {
            // alert(logoutLinks[i]);
            jQuery.ajax({type: 'POST',
                         url: logoutLinks[i],
                         data: {},
                         timeout: 3000,
                         success: function(data){},
                         complete: function(data){}
            });
        }
        window.location = '<c:url value="/j_acegi_logout" />';
    }
</script>

</body>
</html>
