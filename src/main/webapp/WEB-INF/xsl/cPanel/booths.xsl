<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		method="html"
		encoding="UTF-8"
		omit-xml-declaration="yes"
		indent="no"
		media-type="text/html"
	/>

	<xsl:import href="/cPanel/import.xsl"/>

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&#60;!DOCTYPE HTML&#62;</xsl:text>
		<HTML dir="ltr" lang="zh-TW">
			<xsl:apply-templates/>
		</HTML>
	</xsl:template>

	<xsl:template match="document">
		<HEAD>
			<META charset="UTF-8"/>
			<META name="viewport" content="width=device-width, initial-scale=1.0"/>
			<LINK href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/dark-hive/jquery-ui.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/cPanel.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"/>
			<SCRIPT src="/SCRIPT/cPanel.js"/>
			<TITLE>控制臺 &#187; 店家</TITLE>
		</HEAD>
		<xsl:comment>
			<xsl:value-of select="system-property('xsl:version')"/>
		</xsl:comment>
		<BODY>
			<TABLE id="cPanel">
				<TBODY>
					<TR>
						<TD>
							<xsl:apply-templates select="aside"/>
						</TD>
						<TD>
							<FORM id="pagination" action="{list/@action}">
								<TABLE class="list">
									<THEAD>
										<TR>
											<TH>帳號(電子郵件)</TH>
											<TH>店家抬頭</TH>
											<TH>手機號碼</TH>
											<TH>市內電話</TH>
											<TH>聯絡代表</TH>
											<TH>實體地址</TH>
											<TH>停權管制</TH>
										</TR>
									</THEAD>
									<TFOOT>
										<TR>
											<TD colspan="7">
												<DIV class="fR">
													<xsl:apply-templates select="pagination"/>
												</DIV>
											</TD>
										</TR>
									</TFOOT>
									<TBODY>
										<xsl:for-each select="list/row">
											<TR>
												<xsl:if test="position()mod'2'='0'">
													<xsl:attribute name="class">even</xsl:attribute>
												</xsl:if>
												<TD class="monospace textAlignRite">
													<xsl:value-of select="login"/>
												</TD>
												<TD class="textAlignLeft">
													<xsl:value-of select="name"/>
												</TD>
												<TD class="monospace">
													<xsl:value-of select="cellular"/>
												</TD>
												<TD class="monospace">
													<xsl:value-of select="phone"/>
												</TD>
												<TD>
													<xsl:value-of select="representative"/>
												</TD>
												<TD>
													<xsl:value-of select="representative"/>
												</TD>
												<TD class="textAlignCenter">
													<A href="/cPanel/booth/{id}/available.json">
														<xsl:attribute name="class">
															<xsl:choose>
																<xsl:when test="available='true'">fa fa-check-square fa-2x post</xsl:when>
																<xsl:otherwise>fa fa-pause-circle fa-2x post</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
														<xsl:attribute name="title">
															<xsl:choose>
																<xsl:when test="available='false'">啟用</xsl:when>
																<xsl:otherwise>停用</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</A>
												</TD>
											</TR>
										</xsl:for-each>
									</TBODY>
								</TABLE>
							</FORM>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			<HEADER class="cF">
				<DIV class="fL">
					<A href="/cPanel/">控制臺首頁</A>
					<SPAN>&#187;</SPAN>
					<A href="/cPanel/booth/">店家</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>