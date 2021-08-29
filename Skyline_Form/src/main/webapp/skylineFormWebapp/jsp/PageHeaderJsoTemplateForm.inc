<header class="top-bar" style="width: 100%;">
	<a id="homePageHeaderJspTempalte"
		href='../<%= session.getAttribute("homePage") %>'> <img
		src="../skylineFormWebapp/images/skyline_logo.png" border="0"
		title="Main Menu" style="margin-top: 3px;">
	</a> <span class="user-avatar" style="margin-left: calc(100vw - 300px)">
		<img src="../skylineFormWebapp/images/icon-user.png">
	</span> <span class="user-name">${userName}</span> <a href="#"
		onclick="Logout()"> <img
		src="../skylineFormWebapp/images/icon-logout.png" title="Logout" />
	</a>
</header>
