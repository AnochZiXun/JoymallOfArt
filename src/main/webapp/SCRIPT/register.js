/**
 * 加入店家
 *
 * @author:	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
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