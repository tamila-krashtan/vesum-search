package com.github.tamilakrashtan.vesumsearch;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

	@GetMapping("/")
	public String greeting(Model model) {
		return "search";
	}

}
