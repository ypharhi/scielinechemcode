<header class="top-bar" style="width: 100%;">
	<a id="homePageHeaderJspTempalte"
		href='../<%= session.getAttribute("homePage") %>'> <img
		src='../skylineFormWebapp/images/skyline_logo.png?<spring:message code="Env" text="" />' border="0"
		title="Main Menu" style="margin-top: 1px;">
	</a> <span class="user-avatar" style="margin-left: calc(100vw - 320px)">
		<img src='../skylineFormWebapp/images/icon-user.png?<spring:message code="Env" text="" />'>
	</span> <span class="user-name">${userName}</span> <a href="#"
		onclick="Logout()"> <img
		src='../skylineFormWebapp/images/icon-logout.png?<spring:message code="Env" text="" />' title="Logout" />
	</a>
</header>
