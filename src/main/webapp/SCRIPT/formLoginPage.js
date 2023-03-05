/**
 * 控制臺登入頁面
 *
 * @author:	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
	$('INPUT#j_username').keyup(function () {
		$(this).val($(this).val().toLowerCase());
	});
});