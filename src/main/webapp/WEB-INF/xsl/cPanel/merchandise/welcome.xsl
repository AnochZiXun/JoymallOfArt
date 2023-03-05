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
			<TITLE>控制臺 &#187; 商品分類</TITLE>
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
											<TH>商品名稱</TH>
											<TH>單價</TH>
											<TH>上架狀態</TH>
											<!--<TH>庫儲狀態</TH>-->
											<TH>推薦與否</TH>
											<TH>操作</TH>
										</TR>
									</THEAD>
									<TFOOT>
										<TR>
											<!--<TD colspan="6">-->
											<TD colspan="5">
												<DIV class="fL">
													<A class="button" href="add.asp">新增</A>
												</DIV>
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
												<TD>
													<xsl:value-of select="name"/>
												</TD>
												<TD class="taRight monospace">NT&#36;<xsl:value-of select="format-number(price,'###,###')"/></TD>
												<TD class="textAlignCenter">
													<A href="/cPanel/merchandise/{id}/carrying.json">
														<xsl:attribute name="class">
															<xsl:choose>
																<xsl:when test="carrying='true'">fa fa-check-square fa-2x post</xsl:when>
																<xsl:otherwise>fa fa-pause-circle fa-2x post</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</A>
												</TD>
												<!--
												<TD class="textAlignCenter">
													<A href="/cPanel/merchandise/{id}/inStock.json">
														<xsl:attribute name="class">
															<xsl:choose>
																<xsl:when test="inStock='true'">fa fa-check-square fa-2x post</xsl:when>
																<xsl:otherwise>fa fa-pause-circle fa-2x post</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</A>
												</TD>
												-->
												<TD class="textAlignCenter">
													<A href="/cPanel/merchandise/{id}/recommended.json">
														<xsl:attribute name="class">
															<xsl:choose>
																<xsl:when test="recommended='true'">fa fa-check-square fa-2x post</xsl:when>
																<xsl:otherwise>fa fa-pause-circle fa-2x post</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</A>
												</TD>
												<TD class="textAlignCenter">
													<A class="fa fa-edit fa-2x" title="編輯" href="/cPanel/merchandise/{id}.asp">&#160;</A>
													<B class="blank">&#160;</B>
													<A class="fa fa-object-group fa-2x" title="nested browsing context (&#60;IFRAME&#47;&#62;)" href="/cPanel/merchandise/{id}/internalFrame/">&#160;</A>
													<B class="blank">&#160;</B>
													<A class="fa fa-list-alt fa-2x" title="商品圖片" href="/cPanel/merchandise/{id}/merchandiseImage/">&#160;</A>
													<B class="blank">&#160;</B>
													<A class="fa fa-cogs fa-2x" title="商品規格" href="/cPanel/merchandise/{id}/merchandiseSpecification/">&#160;</A>
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
					<A href="{@requestURI}">商品</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>