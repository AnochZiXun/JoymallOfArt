<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		method="html"
		encoding="UTF-8"
		omit-xml-declaration="yes"
		indent="no"
		media-type="text/html"
	/>

	<!--下拉式選單群組-->
	<xsl:template match="optgroup">
		<OPTGROUP label="{@label}">
			<xsl:apply-templates/>
		</OPTGROUP>
	</xsl:template>

	<!--下拉式選單選項-->
	<xsl:template match="option">
		<OPTION value="{@value}">
			<xsl:if test="@selected">
				<xsl:attribute name="selected"/>
			</xsl:if>
			<xsl:value-of select="." disable-output-escaping="yes"/>
		</OPTION>
	</xsl:template>

	<!--工具列-->
	<xsl:template name="toolbar">
		<xsl:param name="contextPath"/>
		<DIV id="header" class="cF">
			<DIV class="fL">
				<A href="{$contextPath}/">首頁</A>
				<xsl:if test="@title and string-length(@title)&gt;'0'">
					<SPAN>&#187;</SPAN>
					<A>
						<xsl:value-of select="@title" disable-output-escaping="yes"/>
					</A>
				</xsl:if>
			</DIV>
			<DIV class="fR">
				<A>
					<xsl:value-of select="@fullname"/>
				</A>
				<SPAN>&#187;</SPAN>
				<A class="ajaxDelete" href="{$contextPath}/">登出</A>
			</DIV>
		</DIV>
	</xsl:template>

	<!--工具列的右上角-->
	<xsl:template name="topRite">
		<DIV class="fR">
			<DIV id="me">
				<A>
					<xsl:value-of select="@me"/>
				</A>
				<UL>
					<LI>
						<A href="/cPanel/shadow.asp">變更密碼</A>
					</LI>
					<LI>
						<A href="/cPanel/myself.asp">
							<xsl:attribute name="href">
								<xsl:choose>
									<xsl:when test="@internal='false'">/cPanel/booth.asp</xsl:when>
									<xsl:otherwise>/cPanel/staff.asp</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<SPAN>基本資料</SPAN>
						</A>
					</LI>
					<LI>
						<A href="/cPanel/logOut.asp">登出</A>
					</LI>
				</UL>
			</DIV>
		</DIV>
	</xsl:template>

	<!--手風琴(側邊選單)-->
	<xsl:template match="aside">
		<xsl:param name="contextPath"/>
		<ASIDE id="accordion">
			<xsl:apply-templates select="expanded"/>
			<xsl:apply-templates select="collapsed"/>
		</ASIDE>
	</xsl:template>

	<!--側邊選單裡的連結-->
	<xsl:template match="collapsed|expanded">
		<H3>
			<xsl:value-of select="@name"/>
		</H3>
		<DIV>
			<OL>
				<xsl:for-each select="anchor">
					<LI>
						<A href="/cPanel/{@href}">
							<xsl:value-of select="."/>
						</A>
					</LI>
				</xsl:for-each>
			</OL>
		</DIV>
	</xsl:template>

	<!--分頁選擇器-->
	<xsl:template match="pagination">
		<xsl:if test="@previous">
			<xsl:if test="@first">
				<A class="button fa fa-fast-backward paginate" title="第一頁" tabindex="{@first}">&#160;</A>
			</xsl:if>
			<A class="button fa fa-backward paginate" title="上一頁" tabindex="{@previous}">&#160;</A>
		</xsl:if>
		<SPAN style="margin:0 3px">
			<LABEL>
				<SPAN>每頁</SPAN>
				<INPUT class="numeric" maxlength="2" name="s" type="text" value="{@size}"/>
				<SPAN>筆</SPAN>
			</LABEL>
			<SPAN>：</SPAN>
			<LABEL>
				<SPAN>第</SPAN>
				<SELECT name="p">
					<xsl:apply-templates select="*"/>
				</SELECT>
				<SPAN>&#47;</SPAN>
				<SPAN>
					<xsl:value-of select="@totalPages"/>
				</SPAN>
				<SPAN>頁</SPAN>
			</LABEL>
			<SPAN>&#40;共</SPAN>
			<SPAN>
				<xsl:value-of select="@totalElements"/>
			</SPAN>
			<SPAN>筆&#41;</SPAN>
		</SPAN>
		<xsl:if test="@next">
			<A class="button fa fa-forward paginate" title="下一頁" tabindex="{@next}">&#160;</A>
			<xsl:if test="@last">
				<A class="button fa fa-fast-forward paginate" title="最後頁" tabindex="{@last}">&#160;</A>
			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>