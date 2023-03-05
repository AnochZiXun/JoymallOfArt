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
				<xsl:value-of select="name"/>
			</b>
			<span>店家，您好：</span>
		</p>
		<p>貴公司在 e95 易購物的密碼已重設；以下是新的登入資訊，請查核確認，謝謝。</p>
		<ol>
			<li>店家帳號：<xsl:value-of select="login"/></li>
			<li>新的密碼：<xsl:value-of select="shadow"/></li>
			<li>登入網址：http://www.e95.com.tw/cPanel/</li>
		</ol>
		<p>謝謝您的光臨！</p>
	</xsl:template>

</xsl:stylesheet>