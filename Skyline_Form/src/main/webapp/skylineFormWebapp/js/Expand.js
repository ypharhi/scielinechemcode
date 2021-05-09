var arrIMG = new Array();

arrIMG[0] = new Image();
arrIMG[0].src = "images/Minus.gif";

arrIMG[1] = new Image();
arrIMG[1].src = "images/Plus.gif";

function Expand(oImg, oContent)
{
    if(oImg.src == arrIMG[0].src)
    {
	    oImg.src = arrIMG[1].src;
	    oContent.style.display = "none";
    }
    else
    {
	    oImg.src = arrIMG[0].src;
	    oContent.style.display = "block";
    }
}
