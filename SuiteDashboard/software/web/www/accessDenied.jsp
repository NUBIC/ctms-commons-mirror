<%@include file="/WEB-INF/views/tags.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>Suite Dashoard</title>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <!--[if IE]>
    <style type="text/css" media="screen">
        div,
        li {
            zoom: 1;
        }
    </style>
    <![endif]-->

    <link rel="stylesheet" href="<c:url value="/css/index.css" />" type="text/css">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.1/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value="/js/index.js" />"></script>
</head>
<body>

<div id="header">
    <div id="header_container">
        <img src="images/suite_logo.png" width="290" height="71" alt="caBIG Clinical Trials Suite"/>
        <div class="header-right">
            <div class="welcome_user">${not empty user ? user.firstName : ''} ${not empty user ? user.lastName : ''}</div>
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