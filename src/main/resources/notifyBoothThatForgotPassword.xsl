<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		method="html"
		encoding="UTF-8"
		omit-xml-declaration="yes"
		indent="no"
		media-type="text/html"
	/>

	<xsl:template match="document">
		<p>
			<xsl:value-of select="name"/>
			<span>，您好：</span>
		</p>
		<p>請至 <a href="http://www.e95.com.tw/reset.asp?code={code}" target="_blank">http://www.e95.com.tw/reset.asp?code=<xsl:value-of select="code"/></a> 重設密碼，謝謝。</p>
	</xsl:template>

</xsl:stylesheet>