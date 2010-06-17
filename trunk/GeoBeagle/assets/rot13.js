function encryptAll() {
	var elts = document.getElementsByClassName('hint');
	for (var i = 0; i < elts.length; i++) {
		dht(elts[i].id);
	}
}

function dht(id) {
	hint_link = document.getElementById(id);
	hint_text = document.getElementById(id + '_text');
	try {
		hint_text.innerHTML = convertROTStringWithBrackets(hint_text.innerHTML);
		hint_link.innerHTML = (hint_link.innerHTML == 'Decrypt') ? 'Encrypt' : 'Decrypt';
	} catch (e) {
		alert(e);
		return false;
	}
	return false;
}
function convertROT13Char(b) {
	return (b >= "A" && b <= "Z" || b >= "a" && b <= "z" ? rot13array[b] : b)
}
function createROT13array() {
	var a = 0, c = [], d = "abcdefghijklmnopqrstuvwxyz", b = d.length;
	for (a = 0; a < b; a++) {
		c[d.charAt(a)] = d.charAt((a + 13) % 26)
	}
	for (a = 0; a < b; a++) {
		c[d.charAt(a).toUpperCase()] = d.charAt((a + 13) % 26).toUpperCase()
	}
	return c
}
var rot13array;
function convertROTStringWithBrackets(e) {
	var h = "", f = "", g = true, a = 0, b = e.length;
	if (!rot13array) {
		rot13array = createROT13array()
	}
	for (a = 0; a < b; a++) {
		h = e.charAt(a);
		if (a < (b - 4)) {
			if (e.toLowerCase().substr(a, 4) == "<br/>") {
				f += "<br>";
				a += 3;
				continue
			}
		}
		if (a < (b - 3)) {
			if (e.toLowerCase().substr(a, 4) == "<br>") {
				f += "<br>";
				a += 3;
				continue
			}
		}
		if (h == "[") {
			g = false
		} else {
			if (h == "]") {
				g = true
			} else {
				if (h == " ") {
				} else {
					if (h == "&") {
						var d = /\&[^;]*\;/;
						var c = d.exec(e.substr(a, e.length - a))[0];
						if (c) {
							f += c;
							a += c.length - 1;
							h = ""
						}
					} else {
						if (g) {
							h = convertROT13Char(h)
						}
					}
				}
			}
		}
		f += h
	}
	return f
}
