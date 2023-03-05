<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		method="html"
		encoding="UTF-8"
		omit-xml-declaration="yes"
		indent="no"
		media-type="text/html"
	/>

	<xsl:template match="form">
		<p>
			<b>
				<xsl:value-of select="lastname"/>
				<xsl:value-of select="firstname"/>
			</b>
			<xsl:choose>
				<xsl:when test="gender='false'">小姐</xsl:when>
				<xsl:otherwise>先生</xsl:otherwise>
			</xsl:choose>
			<span>，您好：</span>
		</p>
		<p>您在 e95 易購物的密碼已重設；以下是您的登入資訊，請查核確認，謝謝。</p>
		<ol>
			<li>註冊帳號：<xsl:value-of select="email"/></li>
			<li>註冊密碼：<xsl:value-of select="shadow"/></li>
			<li>登入網址：http://www.e95.com.tw/logIn.asp</li>
		</ol>
		<p>謝謝您的光臨！</p>
	</xsl:template>

</xsl:stylesheet>