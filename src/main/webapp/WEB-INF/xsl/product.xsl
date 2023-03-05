<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		method="html"
		encoding="UTF-8"
		omit-xml-declaration="yes"
		indent="no"
		media-type="text/html"
	/>

	<xsl:import href="/import.xsl"/>

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
			<LINK href="http://fonts.googleapis.com/earlyaccess/cwtexyen.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/owl.carousel.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/owl.theme.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/common.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/lightslider.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/product.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="/SCRIPT/lightslider.min.js"/>
			<SCRIPT src="/SCRIPT/owl.carousel.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<SCRIPT src="/SCRIPT/product.js"/>
			<TITLE>柴燒原之鄉 &#187; <xsl:value-of select="booth/merchandise/name"/></TITLE>
		</HEAD>
		<xsl:comment>
			<xsl:value-of select="system-property('xsl:version')"/>
		</xsl:comment>
		<BODY>
			<DIV>
				<DIV>
					<HEADER>
						<DIV>
							<xsl:call-template name="header"/>
						</DIV>
					</HEADER>
				</DIV>
				<DIV>
					<DIV>
						<NAV>
							<xsl:call-template name="navigator"/>
							<DIV class="cF">
								<ASIDE>
									<A id="aside1">
										<xsl:value-of select="booth/name"/>
									</A>
									<A id="aside2" href="/store/{booth/@id}/">店家簡介</A>
									<A id="aside3">產品分類</A>
									<xsl:for-each select="booth/shelves/*">
										<A href="/category/{@id}/">
											<xsl:value-of select="."/>
										</A>
									</xsl:for-each>
								</ASIDE>
								<DIV>
									<DIV class="cF" id="merchandiseTop">
										<DIV class="fL" id="lightSliderWrapper">
											<UL id="lightSlider">
												<xsl:choose>
													<xsl:when test="booth/merchandise/images">
														<xsl:for-each select="booth/merchandise/images/*">
															<LI data-thumb="/seventySeven/{@id}.png">
																<IMG alt="產品圖片{position()}" src="/threeEightyNine/{@id}.png" width="389" height="389"/>
															</LI>
														</xsl:for-each>
													</xsl:when>
													<xsl:otherwise>
														<LI data-thumb="/IMG/productWithoutImage/77.png">
															<IMG alt="嘸產品圖片" src="/IMG/productWithoutImage/389.png" width="389" height="389"/>
														</LI>
													</xsl:otherwise>
												</xsl:choose>
											</UL>
										</DIV>
										<DIV class="fR">
											<H1 id="merchandiseName">
												<xsl:value-of select="booth/merchandise/name"/>
											</H1>
											<H2 id="merchandisePrice">
												<xsl:value-of select="format-number(booth/merchandise/price,'###,###')"/>
											</H2>
											<FORM class="ajax" action="add.json" method="POST">
												<TABLE>
													<xsl:if test="booth/merchandise/specifications">
														<TR>
															<TH class="must">
																<LABEL for="specification">規格</LABEL>
															</TH>
															<TD>
																<SELECT id="specification" name="specification" required="">
																	<xsl:if test="booth/merchandise/carrying='false'">
																		<xsl:attribute name="disabled"/>
																	</xsl:if>
																	<OPTION value="">(無)</OPTION>
																	<xsl:apply-templates select="booth/merchandise/specifications/*"/>
																</SELECT>
															</TD>
														</TR>
													</xsl:if>
													<TR>
														<TH class="must">
															<LABEL for="quantity">數量</LABEL>
														</TH>
														<TD>
															<INPUT id="quantity" maxlength="2" name="quantity" required="" type="text">
																<xsl:if test="booth/merchandise/carrying='false'">
																	<xsl:attribute name="disabled"/>
																	<xsl:attribute name="value">已下架</xsl:attribute>
																</xsl:if>
															</INPUT>
														</TD>
													</TR>
													<TR>
														<TD colspan="2">
															<INPUT type="submit" value="加入購物車">
																<xsl:if test="booth/merchandise/carrying='false'">
																	<xsl:attribute name="style">text-decoration:line-through;cursor:not-allowed</xsl:attribute>
																	<xsl:attribute name="disabled"/>
																</xsl:if>
															</INPUT>
														</TD>
													</TR>
												</TABLE>
											</FORM>
										</DIV>
									</DIV>
									<P>&#160;</P>
									<DIV class="cF" id="merchandiseHtml">
										<DIV>
											<H3 class="tab">產品購買說明</H3>
										</DIV>
										<xsl:value-of select="booth/merchandise/html" disable-output-escaping="yes"/>
									</DIV>
									<P>&#160;</P>
									<DIV class="cF" id="otherMerchandises">
										<DIV>
											<H3 class="tab">店家其它產品</H3>
										</DIV>
										<DIV class="owl-carousel">
											<xsl:for-each select="booth/otherMerchandises/*">
												<DIV>
													<A class="figure" href="/product/{@id}/">
														<FIGURE>
															<IMG alt="產品{position()}" width="195" height="195">
																<xsl:attribute name="src">
																	<xsl:choose>
																		<xsl:when test="@merchandiseImageId">/oneNinetyFive/<xsl:value-of select="@merchandiseImageId"/>.png</xsl:when>
																		<xsl:otherwise>/IMG/productWithoutImage/195.png</xsl:otherwise>
																	</xsl:choose>
																</xsl:attribute>
															</IMG>
															<FIGCAPTION>
																<P>
																	<xsl:value-of select="."/>
																</P>
																<P class="price">
																	<xsl:value-of select="format-number(@price,'###,###')"/>
																</P>
															</FIGCAPTION>
														</FIGURE>
													</A>
												</DIV>
											</xsl:for-each>
										</DIV>
									</DIV>
								</DIV>
							</DIV>
							<xsl:call-template name="footer"/>
						</NAV>
					</DIV>
				</DIV>
			</DIV>
		</BODY>
	</xsl:template>

</xsl:stylesheet>