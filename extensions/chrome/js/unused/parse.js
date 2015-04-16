function parse(page) {
	
	var out = {};
	
	var text = page.getElementsByTagName("body")[0].innerText;
		
	var regex = /[^\s"\d(){},'\-=_:;#%!<>&\|\*\?\[\]\.\/\+\\]{2,}/g;

	var words_map = {};
	var match;	
	
	while (match = regex.exec(text)) {
		
		var word = match[0].toLowerCase();
		
		if (words_map[word] == null) words_map[word] = 1;
		else words_map[word]++;
	}
	
	out['scroll'] = document.body.scrollTop;
	out['words'] = words_map;
	return out;
}

chrome.extension.sendMessage({
	
    action: "getSource",
    source: parse(document)
});