<%@ tag isELIgnored="false" body-content="scriptless"
 dynamic-attributes="true"%><%@
 attribute name="type" required="true" type="java.lang.String"
%><%/*
<div class="${type}1 nopadding"><div class="${type}2 nopadding">
<jsp:doBody />
</div></div>
 */%>
<!--  ${type} --><style>.${type} { border-collapse: collapse; }</style>
<table class="${type}"><tr><td class="${type}1 nopadding"><table class="${type}"><tr><td class="${type}2 nopadding">
<jsp:doBody />
</td></tr></table></td></tr></table><!-- ${type}  -->