<header class="top-bar" style="width: 100%;">

	<div class="top-bar-left">

		<a id="homePageHeaderJspTempalte"
			href='../<%= session.getAttribute("homePage") %>'> <img
			src='../skylineFormWebapp/images/skyline_logo.png?<spring:message code="Env" text="" />' border="0"
			title="Main Menu" style="margin-top: 1px;">
		</a>

	</div>

	<div class="top-bar-right">

		<ul class="menu">

			<li class="menu-text"><span class="user-avatar"> <img
					src='../skylineFormWebapp/images/icon-user.png?<spring:message code="Env" text="" />'>
			</span> <span class="user-name">${userName}</span></li>

			<li style="padding-left: 10px;"><a href="#" onclick="Logout()">
					<img src='../skylineFormWebapp/images/icon-logout.png?<spring:message code="Env" text="" />'
					title="Logout" />
			</a></li>

		</ul>

	</div>
</header>