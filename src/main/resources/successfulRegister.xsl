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
			<xsl:value-of select="name"/>
			<span>，您好：</span>
		</p>
		<p>歡迎您加入 e95 易購物網站店家；以下是您的註冊資訊，請查核確認，謝謝。</p>
		<ol>
			<li>註冊帳號：<xsl:value-of select="login"/></li>
			<li>註冊密碼：<xsl:value-of select="shadow"/></li>
			<li>登入網址：http://www.e95.com.tw/cPanel/</li>
		</ol>
		<p>歡迎您的加入！</p>
	</xsl:template>

</xsl:stylesheet>