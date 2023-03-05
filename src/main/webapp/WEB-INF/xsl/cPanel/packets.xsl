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
			<TITLE>
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</TITLE>
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
											<TH>會員</TH>
											<TH>交易編號</TH>
											<TH>交易時間</TH>
											<TH>交易金額</TH>
											<TH>操作</TH>
										</TR>
									</THEAD>
									<TFOOT>
										<TR>
											<TD colspan="5">
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
												<TD title="{regular/fullname}">
													<xsl:value-of select="regular"/>
												</TD>
												<TD class="monospace">
													<xsl:value-of select="merchantTradeNo"/>
												</TD>
												<TD class="monospace">
													<xsl:value-of select="merchantTradeDate"/>
												</TD>
												<TD class="monospace textAlignRite">
													<SPAN>&#36;</SPAN>
													<xsl:value-of select="format-number(totalAmount,'###,###')"/>
												</TD>
												<TD class="textAlignCenter">
													<xsl:choose>
														<xsl:when test="status='3'">
															<A class="fa fa-star fa-2x" title="已結單。" href="javascript:void()">&#160;</A>
														</xsl:when>
														<xsl:when test="status='2'">
															<A class="fa fa-star-half-o fa-2x post" title="出貨中；點擊後此訂單狀態即改變為已結單。" href="/cPanel/delivering/{@id}.json">&#160;</A>
														</xsl:when>
														<xsl:when test="status='1'">
															<A class="fa fa-star-o fa-2x post" title="備貨中；點擊後此訂單狀態即改變為出貨中。" href="/cPanel/preparing/{@id}.json">&#160;</A>
														</xsl:when>
														<xsl:otherwise>交易未完成</xsl:otherwise>
													</xsl:choose>
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
					<A>
						<xsl:value-of select="@breadcrumb"/>
					</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>