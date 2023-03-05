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
			<LINK href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/sunny/jquery-ui.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/common.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/me.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/i18n/jquery-ui-i18n.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<SCRIPT src="/SCRIPT/me.js"/>
			<TITLE>柴燒原之鄉 &#187; 會員個人資料管理</TITLE>
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
									<A id="aside1">會員管理</A>
									<A id="aside2" href="/me.asp">基本資料</A>
									<A id="aside3" href="/history.asp">訂單查詢</A>
									<A href="/shadow.asp">變更密碼</A>
								</ASIDE>
								<DIV>
									<H1 id="asideName">會員個人資料管理</H1>
									<FORM action="{form/@action}" method="POST">
										<TABLE class="form">
											<xsl:if test="form/@error">
												<CAPTION>
													<xsl:value-of select="form/@error"/>
												</CAPTION>
											</xsl:if>
											<TBODY>
												<TR>
													<TH class="must">
														<LABEL for="lastname">姓氏</LABEL>
													</TH>
													<TD>
														<INPUT id="lastname" maxlength="16" name="lastname" type="text" value="{form/lastname}"/>
													</TD>
												</TR>
												<TR>
													<TH class="must">
														<LABEL for="firstname">名字</LABEL>
													</TH>
													<TD>
														<INPUT id="firstname" maxlength="16" name="firstname" type="text" value="{form/firstname}"/>
													</TD>
												</TR>
												<TR>
													<TH class="must">
														<LABEL for="email">帳號</LABEL>
													</TH>
													<TD>
														<INPUT class="monospace" id="email" style="width:100%" maxlength="256" name="email" placeholder="電子郵件" type="text" value="{form/email}"/>
													</TD>
												</TR>
												<TR>
													<TH class="must">
														<LABEL for="birth">生日</LABEL>
													</TH>
													<TD>
														<INPUT class="dP monospace" id="birth" maxlength="10" name="birth" type="text" value="{form/birth}"/>
													</TD>
												</TR>
												<TR>
													<TH class="must">性別</TH>
													<TD>
														<LABEL>
															<INPUT name="gender" type="radio" value="false">
																<xsl:if test="form/gender='false'">
																	<xsl:attribute name="checked"/>
																</xsl:if>
															</INPUT>
															<SPAN>小姐</SPAN>
														</LABEL>
														<SPAN>、</SPAN>
														<LABEL>
															<INPUT name="gender" type="radio" value="true">
																<xsl:if test="form/gender='true'">
																	<xsl:attribute name="checked"/>
																</xsl:if>
															</INPUT>
															<SPAN>先生</SPAN>
														</LABEL>
													</TD>
												</TR>
												<TR>
													<TH>
														<LABEL for="phone">聯絡電話</LABEL>
													</TH>
													<TD>
														<INPUT id="phone" maxlength="16" name="phone" type="text" value="{form/phone}"/>
													</TD>
												</TR>
												<TR>
													<TH>
														<LABEL for="address">預設地址</LABEL>
													</TH>
													<TD>
														<INPUT id="address" style="width:100%" maxlength="32" name="address" type="text" value="{form/address}"/>
													</TD>
												</TR>
												<TR>
													<TD colspan="2">
														<INPUT class="fL" style="background-color:#EF7F00" type="reset" value="重新填寫"/>
														<INPUT class="fR" style="background-color:#CE2118" type="submit" value="確認送出"/>
													</TD>
												</TR>
											</TBODY>
										</TABLE>
									</FORM>
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