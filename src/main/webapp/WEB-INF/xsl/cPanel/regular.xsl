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
			<TITLE>控制臺 &#187; 會員</TITLE>
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
											<TH>主鍵</TH>
											<TH>姓氏</TH>
											<TH>名字</TH>
											<TH>電子郵件</TH>
											<TH>生日</TH>
											<TH>性別</TH>
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
												<TD class="textAlignCenter">
													<xsl:value-of select="id"/>
												</TD>
												<TD class="textAlignRite">
													<xsl:value-of select="lastname"/>
												</TD>
												<TD class="textAlignLeft">
													<xsl:value-of select="firstname"/>
												</TD>
												<TD class="monospace">
													<xsl:value-of select="email"/>
												</TD>
												<TD class="monospace textAlignCenter">
													<xsl:value-of select="birth"/>
												</TD>
												<TD class="textAlignCenter">
													<B>
														<xsl:attribute name="class">
															<xsl:choose>
																<xsl:when test="gender='true'">fa fa-male fa-2x</xsl:when>
																<xsl:otherwise>fa fa-female fa-2x</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</B>
												</TD>
												<TD class="textAlignCenter">
													<A href="{id}/available.json">
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
					<A href="/cPanel/regular/">會員</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>