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
			<LINK href="http://fonts.googleapis.com/earlyaccess/cwtexhei.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/owl.carousel.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/owl.theme.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/common.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/welcome.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="/SCRIPT/owl.carousel.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<SCRIPT src="/SCRIPT/welcome.js"/>
			<TITLE>柴燒原之鄉</TITLE>
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
							<DIV id="banner">
								<DIV class="owl-carousel">
									<xsl:for-each select="banners/*">
										<DIV>
											<xsl:choose>
												<xsl:when test="string-length(.)&gt;'0'">
													<A href="{.}">
														<xsl:if test="@external='true'">
															<xsl:attribute name="target">_blank</xsl:attribute>
														</xsl:if>
														<IMG alt="" src="/banner/{@id}.png"/>
													</A>
												</xsl:when>
												<xsl:otherwise>
													<IMG alt="" height="325" src="/banner/{@id}.png" width="980"/>
												</xsl:otherwise>
											</xsl:choose>
										</DIV>
									</xsl:for-each>
								</DIV>
							</DIV>
							<DIV class="gs" id="topSales">
								<!--4x1x3 = (5+235+5)x4-->
								<DIV>
									<P class="gs" style="margin-left:0;background-color:#E4951C">熱銷陶藝系列</P>
								</DIV>
								<DIV class="owl-carousel">
									<xsl:for-each select="topSales/*">
										<DIV>
											<A class="figure" href="/product/{@id}/">
												<FIGURE>
													<IMG alt="">
														<xsl:attribute name="src">
															<xsl:choose>
																<xsl:when test="merchandiseImageId">/twoThirtyFive/<xsl:value-of select="merchandiseImageId"/>.png</xsl:when>
																<xsl:otherwise>/IMG/productWithoutImage/235.png</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</IMG>
													<FIGCAPTION>
														<P>
															<xsl:value-of select="booth"/>
														</P>
														<P>
															<xsl:value-of select="name"/>
														</P>
														<P class="price">
															<xsl:value-of select="format-number(price,'###,###')"/>
														</P>
													</FIGCAPTION>
												</FIGURE>
											</A>
										</DIV>
									</xsl:for-each>
								</DIV>
							</DIV>
							<DIV class="gs" id="recommendations">
								<!--8x1 = (5+186+5)-->
								<DIV>
									<P class="gs" style="background-color:#CD2A1F">陶瓷推薦商品</P>
								</DIV>
								<UL class="cF">
									<xsl:for-each select="recommendations/*">
										<LI>
											<A class="figure" href="/product/{@id}/">
												<FIGURE>
													<IMG alt="">
														<xsl:attribute name="src">
															<xsl:choose>
																<xsl:when test="merchandiseImageId">/oneEightySix/<xsl:value-of select="merchandiseImageId"/>.png</xsl:when>
																<xsl:otherwise>/IMG/productWithoutImage/186.png</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</IMG>
													<FIGCAPTION>
														<P>
															<xsl:value-of select="booth"/>
														</P>
														<P>
															<xsl:value-of select="name"/>
														</P>
														<P class="price">
															<xsl:value-of select="format-number(price,'###,###')"/>
														</P>
													</FIGCAPTION>
												</FIGURE>
											</A>
										</LI>
									</xsl:for-each>
								</UL>
							</DIV>
							<xsl:call-template name="footer"/>
						</NAV>
					</DIV>
				</DIV>
			</DIV>
		</BODY>
	</xsl:template>

</xsl:stylesheet>