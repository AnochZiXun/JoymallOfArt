/**
 * 會員個人資料管理
 *
 * @author:	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
	/**
	 * 日期選擇器
	 */
	if (typeof jQuery.ui !== 'undefined' && /[1-9]\.[1-9]{1,2}.[1-9]{1,2}/.test($.ui.version)) {
		$('INPUT[type="text"].dP').datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'yy-mm-dd'
		}).datepicker($.datepicker.regional['zh-TW']);
	}
});