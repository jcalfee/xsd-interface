<%@ page isELIgnored="false"
    import="com.google.appengine.api.users.UserServiceFactory"%><html>
<head>
<!--[if lt IE 9]>
<script src="http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE9.js"></script>
<![endif]-->
<style>
* {
 font-family: "Trebuchet MS", Trebuchet, Arial, Verdana, sans-serif;
 font-size: 14px;
}

body {
 background-color: white;
 margin: 5px;
}

.xsi-navigation {
 margin-bottom: 15px;
}

.xsi-validation-debug {
 display: ${param['debug']==null?'none':'block'};
}
/*.table-bottom {
    bgcolor: #666666;
    padding: 1px;
    display: table-row;
}*/

<%
// http://www.w3schools.com/HTML/html_colornames.asp
String grey = "#808080";
String darkgrey = "#A9A9A9";
String themeTitleBackground = "#072360";
String themeTitleForeground = "White";
String themeRequiredColor = "#ac3159";
String themeHighlight = "yellow";
String themeSelectedBackground = "lightCyan";
String themeResponseBackground = "lightYellow";
String themeResponseFail = "lightCoral";
String themeDocumentationColor = "navy";
%> 

table thead a:link {
 color: <%=themeTitleForeground%>;
}
table thead a:visited {
 color: lightgrey;
}
table thead {
 background-color: <%=themeTitleBackground%>;
 color: <%=themeTitleForeground%>;
 white-space: nowrap;
 padding-right: 5px;
 padding-left: 5px;
}
<%/*.top-shadow {
 background: none repeat scroll 0 0 #F9F9F9;
 border-bottom: 0.23em solid #EEEEEE;
 height: 0.692em;
 left: 0;
 overflow: hidden;
 position: absolute;
 right: 0;
 top: 0;
}*/%>
.nopadding {
 padding: 0px;
}

.border1 {
 border-color: black lightgrey lightgrey black;
 border-style: solid;
 border-width: 1px;
}
.border2 {
 border-color: <%=darkgrey%> <%=grey%> <%=grey%> <%=darkgrey%>;
 border-style: solid;
 border-width: 1px;
}
.title {
 width: 100%;
}
.title1 {
 border-bottom: <%=grey%> solid 1px;
}
.title2 {
 border-bottom: <%=themeHighlight%> solid 2px;
 text-align: center;
 background: none repeat scroll 0 0 <%=themeTitleBackground%>;
 color: <%=themeTitleForeground%>;
}

.label {
 text-align: right;
 vertical-align: top;
}

.required-color {
 color: <%=themeRequiredColor%>;
}

.xsi-selected {
 background: <%=themeSelectedBackground%>;
}

.xsi-response-success {
 background: <%=themeResponseBackground%>;
}

.xsi-response-fail {
 background: <%=themeResponseFail%>;
}

.xsi-documentation {
 display: block;
 color: <%=themeDocumentationColor%>;
}

/*
.xsi-value {
    top: 2px;
    position: relative;
    border: 1px inset;
    padding: 0px 3px 0px 3px; 
}
*/
#button-bar {
 display: inline;
}

#user {
 float: right;
}
</style>
</head>
<body>
<a href="/">Home</a>
<div id="user">${xsi.user.name} | <a
    href="<%=UserServiceFactory.getUserService().createLogoutURL("/")%>">Sign
Out</a></div>
<jsp:include page="elements.jsp" />
</body>
</html>