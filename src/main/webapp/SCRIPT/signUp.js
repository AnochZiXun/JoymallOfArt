/**
 * 加入會員
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
	/**
	 * 同意
	 */
	$('A#agreement').click(function (e) {
		e.preventDefault();
		$("#jDialog").dialog({
			draggable: false,
			maxHeight: document.documentElement.clientHeight / 3 * 2,
			modal: true,
			resizable: false,
			width: document.documentElement.clientWidth / 2
		});
		return false;
	});
});