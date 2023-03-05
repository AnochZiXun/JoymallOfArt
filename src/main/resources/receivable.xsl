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
		<P>
			<xsl:value-of select="regular"/>
			<SPAN>，您好：</SPAN>
		</P>
		<P>
			<SPAN>感謝您在 e95 易購物網站店家，</SPAN>
			<B style="color:#79AE3D">
				<xsl:value-of select="booth"/>
			</B>
			<SPAN>的購物；以下是您的訂單資訊，謝謝。</SPAN>
		</P>
		<TABLE style="border-collapse:collapse" border="1" cellspacing="6" cellpadding="6">
			<CAPTION style="text-align:left">
				<P>
					<SPAN>收件人：</SPAN>
					<xsl:value-of select="recipient"/>
				</P>
				<P>
					<SPAN>聯絡電話：</SPAN>
					<xsl:value-of select="phone"/>
				</P>
				<P>
					<SPAN>運送地址：</SPAN>
					<xsl:value-of select="address"/>
				</P>
			</CAPTION>
			<THEAD>
				<TR>
					<TH style="text-align:center">產品名稱</TH>
					<TH style="text-align:center">規格</TH>
					<TH style="text-align:center">單價</TH>
					<TH style="text-align:center">數量</TH>
					<TH style="text-align:right">小計</TH>
				</TR>
			</THEAD>
			<TFOOT>
				<TR>
					<TD style="text-align:right" colspan="4">合計</TD>
					<TD style="text-align:right">
						<xsl:value-of select="format-number(total,'###,###')"/>
						<SPAN>元</SPAN>
					</TD>
				</TR>
			</TFOOT>
			<TBODY>
				<xsl:for-each select="packet/*">
					<TR>
						<TD style="text-align:center">
							<xsl:value-of select="."/>
						</TD>
						<TD style="text-align:center">
							<xsl:value-of select="@specification"/>
						</TD>
						<TD style="text-align:center">
							<xsl:value-of select="format-number(@price,'###,###')"/>
							<SPAN>元</SPAN>
						</TD>
						<TD style="text-align:center">
							<xsl:value-of select="@quantity"/>
						</TD>
						<TD style="font-family:monospace;text-align:right">
							<xsl:value-of select="format-number(@subTotal,'###,###')"/>
							<SPAN>元</SPAN>
						</TD>
					</TR>
				</xsl:for-each>
			</TBODY>
		</TABLE>
	</xsl:template>

</xsl:stylesheet>