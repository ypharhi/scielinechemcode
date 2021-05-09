<table width="100%" style="margin: 0px;" cellpadding="0" cellspacing="0" border="0">
    <tr><td><br/></td></tr>
    <tr >
        <td width="22%" style="text-align:left">                            
            <img src="../skylineFormWebapp/images/skyline_logo.png" border="0" >
        </td>
        <td id="pageTitle" width="54%" class="cssPageHeader" style="padding-bottom:10px">
              
        </td>        
        <td width="15%" style="text-align: right; line-height: 25px;" nowrap>
            <DIV class="cssUser" style="text-align:right;">
				<IMG align="absMiddle" src="../skylineFormWebapp/images/IconUserS.gif" vspace="0" border="0"><%=request.getAttribute("userName")%>
				&nbsp;&nbsp;
            </DIV>
        </td>
        <td width="7%" class="cssUser" style="text-align: right; line-height: 25px;" nowrap>  <!--  colspan=3 -->
                        <a id="headerShowBackIcon" href="" style="display:none;cursor:pointer">
<img src="../skylineFormWebapp/images/back_image.png" title="Back" style="margin-bottom:4px"/></a> &nbsp;&nbsp;
			<a href='../<%= session.getAttribute("homePage") %>'><img src="../skylineFormWebapp/images/home_image.png" title="Main Menu"/></a> &nbsp;&nbsp;
			<a href="../?exit=true"><img src="../skylineFormWebapp/images/logout_image32g.png" title="Logout" style="height:28px; width:28px;"/></a>			
		</td>	
    </tr>
    <TR>
		<td><br/></td>
		<TD id="pageSubTitle" colspan="1" class="cssCurrentPath" style=""> 
                   
		<TD>	
		<td><br/></td>	
    </TR>
    <tr><td><br/></td></tr>
    <TR>            
		<TD colspan="2" class="cssStaticData" style="text-align:left;" vAlign="Top">
			
		<TD>
    </TR>
</table>